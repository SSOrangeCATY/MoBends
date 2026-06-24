package goblinbob.mobends.core.client.event;

import goblinbob.mobends.core.bender.EntityBender;
import goblinbob.mobends.core.bender.EntityBenderRegistry;
import goblinbob.mobends.core.client.MoBendsRenderContext;
import goblinbob.mobends.core.compat.PlayerAnimationLibCompat;
import goblinbob.mobends.core.mutators.Mutator;
import goblinbob.mobends.standard.mutators.BipedMutator;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

public class EntityRenderHandler
{
    public static void applyLivingEntityMutation(LivingEntityRenderer<?, ?, ?> renderer, LivingEntity entity, float partialTicks)
    {
        MoBendsRenderContext.clear();

        if (PlayerAnimationLibCompat.hasActiveAnimation(entity))
        {
            MoBendsRenderContext.clearEntity(entity);
            return;
        }

        EntityBender<LivingEntity> bender = EntityBenderRegistry.instance.getForEntity(entity);
        if (bender == null || !bender.isAnimated())
        {
            MoBendsRenderContext.clearEntity(entity);
            return;
        }

        if (bender.applyMutation(renderer, entity, partialTicks))
        {
            Model<?> model = renderer.getModel();
            Mutator<?, ?, ?> mutator = bender.getMutator(renderer);
            if (model instanceof HumanoidModel<?> humanoidModel && mutator instanceof BipedMutator<?, ?, ?> bipedMutator)
                bipedMutator.syncPosesToVanillaModel(humanoidModel);

            MoBendsRenderContext.setCurrentMutation(model, entity, mutator);
        }
    }

    @SubscribeEvent
    public void afterLivingRender(RenderLivingEvent.Post<?, ?, ?> event)
    {
        MoBendsRenderContext.clear();
    }
}
