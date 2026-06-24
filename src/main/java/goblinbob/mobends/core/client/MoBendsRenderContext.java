package goblinbob.mobends.core.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import goblinbob.mobends.core.client.event.MoBendsRenderState;
import goblinbob.mobends.core.data.LivingEntityData;
import goblinbob.mobends.core.mutators.Mutator;
import goblinbob.mobends.standard.mutators.BipedMutator;
import goblinbob.mobends.standard.mutators.SpiderMutator;
import goblinbob.mobends.standard.mutators.WolfMutator;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.WeakHashMap;

@OnlyIn(Dist.CLIENT)
public class MoBendsRenderContext
{
    private static final Map<Model<?>, Mutator<?, ?, ?>> submittedMutators =
            Collections.synchronizedMap(new IdentityHashMap<>());
    private static final Map<Model<?>, Mutator<?, ?, ?>> renderingMutators =
            Collections.synchronizedMap(new IdentityHashMap<>());
    private static final Map<LivingEntity, Mutator<?, ?, ?>> entityMutators =
            Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<LivingEntity, Model<?>> submittedModels =
            Collections.synchronizedMap(new WeakHashMap<>());

    public static void setCurrentMutation(Model<?> model, Mutator<?, ?, ?> mutator)
    {
        if (mutator == null)
            submittedMutators.remove(model);
        else
            submittedMutators.put(model, mutator);
    }

    public static void setCurrentMutation(Model<?> model, LivingEntity entity, Mutator<?, ?, ?> mutator)
    {
        setCurrentMutation(model, mutator);

        if (entity != null && mutator != null)
        {
            entityMutators.put(entity, mutator);
            submittedModels.put(entity, model);
        }
    }

    public static boolean renderCurrentModel(Model<?> model, PoseStack poseStack, VertexConsumer vertexConsumer,
                                             int packedLight, int packedOverlay, int color)
    {
        Mutator<?, ?, ?> mutator = renderingMutators.get(model);
        if (mutator == null || !mutator.shouldRenderCustom())
            return false;

        mutator.renderMutated(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        return true;
    }

    public static BipedMutator<?, ?, ?> getCurrentBipedMutator()
    {
        Mutator<?, ?, ?> mutator = firstRenderingMutator();
        return mutator instanceof BipedMutator<?, ?, ?> bipedMutator ? bipedMutator : null;
    }

    public static SpiderMutator getCurrentSpiderMutator()
    {
        Mutator<?, ?, ?> mutator = firstRenderingMutator();
        return mutator instanceof SpiderMutator spiderMutator ? spiderMutator : null;
    }

    public static WolfMutator getCurrentWolfMutator()
    {
        Mutator<?, ?, ?> mutator = firstRenderingMutator();
        return mutator instanceof WolfMutator wolfMutator ? wolfMutator : null;
    }

    public static void beginModelRender(Model<?> model, Object state)
    {
        if (!(state instanceof LivingEntityRenderState livingState))
        {
            renderingMutators.remove(model);
            return;
        }

        LivingEntity entity = livingState.getRenderData(MoBendsRenderState.LIVING_ENTITY);
        Mutator<?, ?, ?> mutator = entity == null ? null : entityMutators.get(entity);
        if (mutator == null || !isSubmittedOrCompatibleLayerModel(model, entity, mutator) || !syncMutator(mutator, entity))
        {
            renderingMutators.remove(model);
            return;
        }

        renderingMutators.put(model, mutator);
    }

    public static void endModelRender(Model<?> model)
    {
        renderingMutators.remove(model);
    }

    public static void clearEntity(LivingEntity entity)
    {
        entityMutators.remove(entity);
        submittedModels.remove(entity);
    }

    public static void clear()
    {
        renderingMutators.clear();
    }

    private static Mutator<?, ?, ?> firstRenderingMutator()
    {
        synchronized (renderingMutators)
        {
            return renderingMutators.values().stream().findFirst().orElse(null);
        }
    }

    private static boolean isSubmittedOrCompatibleLayerModel(Model<?> model, LivingEntity entity, Mutator<?, ?, ?> mutator)
    {
        if (submittedMutators.containsKey(model))
            return true;

        Model<?> submittedModel = submittedModels.get(entity);
        if (submittedModel == null || !isCompatibleMutatedModel(submittedModel, mutator))
            return false;

        return isCompatibleMutatedModel(model, mutator);
    }

    private static boolean isCompatibleMutatedModel(Model<?> model, Mutator<?, ?, ?> mutator)
    {
        return model instanceof EntityModel<?> entityModel && !mutator.shouldModelBeSkipped(entityModel);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static boolean syncMutator(Mutator mutator, LivingEntity entity)
    {
        LivingEntityData data = (LivingEntityData) mutator.getData(entity);
        if (data == null)
            return false;

        mutator.syncUpWithData(data);
        return true;
    }
}
