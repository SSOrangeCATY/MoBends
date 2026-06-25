package goblinbob.mobends.core.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import goblinbob.mobends.core.client.event.MoBendsRenderState;
import goblinbob.mobends.core.data.LivingEntityData;
import goblinbob.mobends.core.mutators.Mutator;
import goblinbob.mobends.standard.mutators.BipedMutator;
import goblinbob.mobends.standard.mutators.SpiderMutator;
import goblinbob.mobends.standard.mutators.WolfMutator;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Context for binding mutation state to models submitted into Minecraft's
 * retained render pipeline.
 */
public class MoBendsRenderContext
{
    private static final Map<Model, Mutator<?, ?, ?, ?>> submittedMutators =
            Collections.synchronizedMap(new IdentityHashMap<>());
    private static final Map<Model, BipedMutator<?, ?, ?, ?>> submittedBipedMutators =
            Collections.synchronizedMap(new IdentityHashMap<>());
    private static final Map<Model, SpiderMutator> submittedSpiderMutators =
            Collections.synchronizedMap(new IdentityHashMap<>());
    private static final Map<Model, WolfMutator> submittedWolfMutators =
            Collections.synchronizedMap(new IdentityHashMap<>());
    private static final Map<Model, Mutator<?, ?, ?, ?>> renderingMutators =
            Collections.synchronizedMap(new IdentityHashMap<>());
    private static final Map<Model, BipedMutator<?, ?, ?, ?>> renderingBipedMutators =
            Collections.synchronizedMap(new IdentityHashMap<>());
    private static final Map<Model, SpiderMutator> renderingSpiderMutators =
            Collections.synchronizedMap(new IdentityHashMap<>());
    private static final Map<Model, WolfMutator> renderingWolfMutators =
            Collections.synchronizedMap(new IdentityHashMap<>());
    private static final Map<LivingEntity, Mutator<?, ?, ?, ?>> entityMutators =
            Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<LivingEntity, BipedMutator<?, ?, ?, ?>> entityBipedMutators =
            Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<LivingEntity, SpiderMutator> entitySpiderMutators =
            Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<LivingEntity, WolfMutator> entityWolfMutators =
            Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<LivingEntity, Model> submittedModels =
            Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * Sets the generic mutator for a submitted model and entity.
     */
    public static void setMutation(Model model, LivingEntity living, Mutator<?, ?, ?, ?> mutator)
    {
        if (model == null)
        {
            return;
        }

        if (mutator == null)
        {
            clearModel(model);
            if (living != null)
            {
                clearEntity(living);
            }
            return;
        }

        submittedMutators.put(model, mutator);
        bindSubmittedType(model, mutator);

        if (living != null)
        {
            entityMutators.put(living, mutator);
            submittedModels.put(living, model);
            bindEntityType(living, mutator);
        }
    }

    /**
     * Sets the biped mutator for a submitted model.
     */
    public static void setBipedMutator(Model model, BipedMutator<?, ?, ?, ?> mutator)
    {
        setMutation(model, null, mutator);
    }

    /**
     * Sets the biped mutator for a submitted model and entity.
     */
    public static void setBipedMutator(Model model, LivingEntity living, BipedMutator<?, ?, ?, ?> mutator)
    {
        setMutation(model, living, mutator);
    }

    /**
     * Gets the biped mutator bound to a submitted model.
     */
    public static BipedMutator<?, ?, ?, ?> getBipedMutator(Model model)
    {
        BipedMutator<?, ?, ?, ?> mutator = renderingBipedMutators.get(model);
        return mutator != null ? mutator : submittedBipedMutators.get(model);
    }

    /**
     * Sets the spider mutator for a submitted model.
     */
    public static void setSpiderMutator(Model model, SpiderMutator mutator)
    {
        setMutation(model, null, mutator);
    }

    /**
     * Sets the spider mutator for a submitted model and entity.
     */
    public static void setSpiderMutator(Model model, LivingEntity living, SpiderMutator mutator)
    {
        setMutation(model, living, mutator);
    }

    /**
     * Gets the spider mutator bound to a submitted model.
     */
    public static SpiderMutator getSpiderMutator(Model model)
    {
        SpiderMutator mutator = renderingSpiderMutators.get(model);
        return mutator != null ? mutator : submittedSpiderMutators.get(model);
    }

    /**
     * Sets the wolf mutator for a submitted model.
     */
    public static void setWolfMutator(Model model, WolfMutator mutator)
    {
        setMutation(model, null, mutator);
    }

    /**
     * Sets the wolf mutator for a submitted model and entity.
     */
    public static void setWolfMutator(Model model, LivingEntity living, WolfMutator mutator)
    {
        setMutation(model, living, mutator);
    }

    /**
     * Gets the wolf mutator bound to a submitted model.
     */
    public static WolfMutator getWolfMutator(Model model)
    {
        WolfMutator mutator = renderingWolfMutators.get(model);
        return mutator != null ? mutator : submittedWolfMutators.get(model);
    }

    /**
     * Renders the submitted model with the currently bound mutator.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static boolean renderCurrentModel(Model model, PoseStack poseStack, VertexConsumer vertexConsumer,
                                             int packedLight, int packedOverlay, int color)
    {
        Mutator mutator = getMutator(model);
        if (mutator == null || !mutator.shouldRenderCustom())
        {
            return false;
        }

        mutator.renderMutated(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        return true;
    }

    /**
     * Re-syncs the mutator immediately before Minecraft's retained model submit
     * node renders a model. The same vanilla model instance can be submitted for
     * multiple entities before any of them render, so custom parts must be
     * rebound from the submitted render state at render time.
     */
    public static void beginModelRender(Model model, Object state)
    {
        if (!(state instanceof LivingEntityRenderState livingState))
        {
            return;
        }

        LivingEntity living = livingState.getRenderData(MoBendsRenderState.LIVING_ENTITY);
        if (living == null)
        {
            clearRenderingModel(model);
            return;
        }

        if (!bindMutation(model, living))
        {
            clearRenderingModel(model);
        }
    }

    /**
     * Clears the temporary model binding after the retained submit node has
     * rendered.
     */
    public static void endModelRender(Model model)
    {
        clearRenderingModel(model);
    }

    /**
     * Clears any mutation context for a submitted model.
     */
    public static void clearModel(Model model)
    {
        submittedMutators.remove(model);
        submittedBipedMutators.remove(model);
        submittedSpiderMutators.remove(model);
        submittedWolfMutators.remove(model);
        synchronized (submittedModels)
        {
            submittedModels.values().removeIf(submittedModel -> submittedModel == model);
        }
        clearRenderingModel(model);
    }

    /**
     * Clears mutation context for a specific entity.
     */
    public static void clearEntity(LivingEntity living)
    {
        entityMutators.remove(living);
        entityBipedMutators.remove(living);
        entitySpiderMutators.remove(living);
        entityWolfMutators.remove(living);
        submittedModels.remove(living);
    }

    private static Mutator<?, ?, ?, ?> getMutator(Model model)
    {
        Mutator<?, ?, ?, ?> mutator = renderingMutators.get(model);
        return mutator != null ? mutator : submittedMutators.get(model);
    }

    private static void clearRenderingModel(Model model)
    {
        renderingMutators.remove(model);
        renderingBipedMutators.remove(model);
        renderingSpiderMutators.remove(model);
        renderingWolfMutators.remove(model);
    }

    private static boolean bindMutation(Model model, LivingEntity living)
    {
        Mutator<?, ?, ?, ?> mutator = entityMutators.get(living);
        if (mutator == null
                || !isSubmittedOrCompatibleLayerModel(model, living, mutator, submittedMutators)
                || !syncMutator(mutator, living))
        {
            return false;
        }

        renderingMutators.put(model, mutator);
        bindRenderingType(model, mutator);
        return true;
    }

    private static boolean isSubmittedOrCompatibleLayerModel(Model model, LivingEntity living,
                                                             Mutator<?, ?, ?, ?> mutator,
                                                             Map<Model, ?> submittedModelMutators)
    {
        if (submittedModelMutators.containsKey(model))
        {
            return true;
        }

        Model submittedModel = submittedModels.get(living);
        return submittedModel != null
                && isCompatibleMutatedModel(submittedModel, mutator)
                && isCompatibleMutatedModel(model, mutator);
    }

    private static boolean isCompatibleMutatedModel(Model model, Mutator<?, ?, ?, ?> mutator)
    {
        return model instanceof EntityModel<?> entityModel
                && !mutator.shouldModelBeSkipped(entityModel);
    }

    private static void bindSubmittedType(Model model, Mutator<?, ?, ?, ?> mutator)
    {
        submittedBipedMutators.remove(model);
        submittedSpiderMutators.remove(model);
        submittedWolfMutators.remove(model);

        if (mutator instanceof BipedMutator<?, ?, ?, ?> bipedMutator)
        {
            submittedBipedMutators.put(model, bipedMutator);
        }
        else if (mutator instanceof SpiderMutator spiderMutator)
        {
            submittedSpiderMutators.put(model, spiderMutator);
        }
        else if (mutator instanceof WolfMutator wolfMutator)
        {
            submittedWolfMutators.put(model, wolfMutator);
        }
    }

    private static void bindRenderingType(Model model, Mutator<?, ?, ?, ?> mutator)
    {
        renderingBipedMutators.remove(model);
        renderingSpiderMutators.remove(model);
        renderingWolfMutators.remove(model);

        if (mutator instanceof BipedMutator<?, ?, ?, ?> bipedMutator)
        {
            renderingBipedMutators.put(model, bipedMutator);
        }
        else if (mutator instanceof SpiderMutator spiderMutator)
        {
            renderingSpiderMutators.put(model, spiderMutator);
        }
        else if (mutator instanceof WolfMutator wolfMutator)
        {
            renderingWolfMutators.put(model, wolfMutator);
        }
    }

    private static void bindEntityType(LivingEntity living, Mutator<?, ?, ?, ?> mutator)
    {
        entityBipedMutators.remove(living);
        entitySpiderMutators.remove(living);
        entityWolfMutators.remove(living);

        if (mutator instanceof BipedMutator<?, ?, ?, ?> bipedMutator)
        {
            entityBipedMutators.put(living, bipedMutator);
        }
        else if (mutator instanceof SpiderMutator spiderMutator)
        {
            entitySpiderMutators.put(living, spiderMutator);
        }
        else if (mutator instanceof WolfMutator wolfMutator)
        {
            entityWolfMutators.put(living, wolfMutator);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static boolean syncMutator(Mutator mutator, LivingEntity living)
    {
        if (mutator == null)
        {
            return false;
        }

        LivingEntityData data = (LivingEntityData) mutator.getData(living);
        if (data == null)
        {
            return false;
        }

        mutator.syncUpWithData(data);
        return true;
    }
}
