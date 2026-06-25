package goblinbob.mobends.core.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import goblinbob.mobends.core.bender.EntityBender;
import goblinbob.mobends.core.bender.EntityBenderRegistry;
import goblinbob.mobends.core.client.MoBendsRenderContext;
import goblinbob.mobends.core.compat.PlayerAnimationLibCompat;
import goblinbob.mobends.core.data.LivingEntityData;
import goblinbob.mobends.core.mutators.Mutator;
import goblinbob.mobends.standard.mutators.BipedMutator;
import goblinbob.mobends.standard.mutators.SpiderMutator;
import goblinbob.mobends.standard.mutators.WolfMutator;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class EntityRenderHandler
{
    // Track which entities we pushed pose for (to avoid imbalanced stack)
    private static final java.util.Set<Integer> entitiesWithPushedPose = new java.util.HashSet<>();

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void applyLivingEntityMutation(LivingEntityRenderer<?, ?, ?> renderer, LivingEntity living, float partialTicks)
    {
        final EntityBender<LivingEntity> entityBender = EntityBenderRegistry.instance.getForEntity(living);
        final EntityModel<?> model = renderer.getModel();

        if (entityBender == null)
        {
            MoBendsRenderContext.clearModel(model);
            MoBendsRenderContext.clearEntity(living);
            return;
        }

        if (PlayerAnimationLibCompat.hasActiveAnimation(living))
        {
            MoBendsRenderContext.clearModel(model);
            MoBendsRenderContext.clearEntity(living);
            entityBender.deapplyMutation((LivingEntityRenderer) renderer, living);
            return;
        }

        if (entityBender.isAnimated())
        {
            if (entityBender.applyMutation((LivingEntityRenderer) renderer, living, partialTicks))
            {
                final Object rawMutator = entityBender.getMutator((LivingEntityRenderer) renderer);
                final Mutator<?, LivingEntity, ?, ?> mutator =
                    (Mutator<?, LivingEntity, ?, ?>) rawMutator;

                MoBendsRenderContext.setMutation(model, living, mutator);

                if (rawMutator instanceof BipedMutator<?, ?, ?, ?> bipedMutator
                        && model instanceof HumanoidModel<?> humanoidModel)
                {
                    bipedMutator.syncPosesToVanillaModel(humanoidModel);
                }
            }
        }
        else
        {
            MoBendsRenderContext.clearModel(model);
            MoBendsRenderContext.clearEntity(living);
            entityBender.deapplyMutation((LivingEntityRenderer) renderer, living);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void beforeLivingRender(RenderLivingEvent.Pre<? extends LivingEntity, ? extends LivingEntityRenderState, ? extends EntityModel<?>> event)
    {
        final LivingEntity living = event.getRenderState().getRenderData(MoBendsRenderState.LIVING_ENTITY);
        if (living == null)
        {
            return;
        }

        final EntityBender<LivingEntity> entityBender = EntityBenderRegistry.instance.getForEntity(living);
        final LivingEntityRenderer renderer = (LivingEntityRenderer) event.getRenderer();
        final EntityModel<?> model = renderer.getModel();

        if (entityBender == null)
        {
            MoBendsRenderContext.clearModel(model);
            MoBendsRenderContext.clearEntity(living);
            return;
        }

        // Check if PlayerAnimationLib has an active animation for this entity
        // If so, let PlayerAnimationLib handle the animation and skip Mo'Bends
        if (PlayerAnimationLibCompat.hasActiveAnimation(living))
        {
            // De-apply any existing mutation so vanilla/PlayerAnimationLib can render normally
            MoBendsRenderContext.clearModel(model);
            MoBendsRenderContext.clearEntity(living);
            entityBender.deapplyMutation(renderer, living);
            return;
        }

        final float pt = event.getPartialTick();
        final PoseStack poseStack = event.getPoseStack();

        poseStack.pushPose();
        entitiesWithPushedPose.add(living.getId());

        if (entityBender.isAnimated())
        {
            if (entityBender.applyMutation(renderer, living, pt))
            {
                final Object rawMutator = entityBender.getMutator(renderer);
                final Mutator<?, LivingEntity, ?, ?> mutator =
                    (Mutator<?, LivingEntity, ?, ?>) rawMutator;
                final LivingEntityData<LivingEntity> data =
                    (LivingEntityData<LivingEntity>) mutator.getData(living);

                MoBendsRenderContext.setMutation(model, living, mutator);

                // Set the mutator in the render context so the mixin can intercept rendering
                if (rawMutator instanceof BipedMutator<?, ?, ?, ?> bipedMutator)
                {
                    MoBendsRenderContext.setBipedMutator(model, living, bipedMutator);

                    // Sync animated poses to vanilla model so layers (armor, held items) can use them
                    if (model instanceof HumanoidModel<?> humanoidModel)
                    {
                        bipedMutator.syncPosesToVanillaModel(humanoidModel);
                    }
                }
                else if (rawMutator instanceof SpiderMutator spiderMutator)
                {
                    MoBendsRenderContext.setSpiderMutator(model, living, spiderMutator);
                }
                else if (rawMutator instanceof WolfMutator wolfMutator)
                {
                    MoBendsRenderContext.setWolfMutator(model, living, wolfMutator);
                }

                entityBender.beforeRender(data, living, pt, poseStack);
            }
        }
        else
        {
            MoBendsRenderContext.clearModel(model);
            MoBendsRenderContext.clearEntity(living);
            entityBender.deapplyMutation(renderer, living);
        }
    }

    public static void cleanupAfterLivingSubmit(LivingEntity living, float partialTick, PoseStack poseStack)
    {
        if (living == null)
        {
            return;
        }

        final EntityBender<LivingEntity> entityBender = EntityBenderRegistry.instance.getForEntity(living);

        if (entityBender == null)
            return;

        // Only pop if we pushed for this entity
        if (entitiesWithPushedPose.remove(living.getId()))
        {
            entityBender.afterRender(living, partialTick, poseStack);
            poseStack.popPose();
        }
    }
}
