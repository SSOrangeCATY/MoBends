package goblinbob.mobends.core.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import goblinbob.mobends.core.mutators.Mutator;
import goblinbob.mobends.standard.mutators.BipedMutator;
import goblinbob.mobends.standard.mutators.SpiderMutator;
import goblinbob.mobends.standard.mutators.WolfMutator;
import net.minecraft.client.model.Model;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MoBendsRenderContext
{
    private static final ThreadLocal<Model<?>> currentModel = new ThreadLocal<>();
    private static final ThreadLocal<Mutator<?, ?, ?>> currentMutator = new ThreadLocal<>();
    private static final ThreadLocal<BipedMutator<?, ?, ?>> currentBipedMutator = new ThreadLocal<>();
    private static final ThreadLocal<SpiderMutator> currentSpiderMutator = new ThreadLocal<>();
    private static final ThreadLocal<WolfMutator> currentWolfMutator = new ThreadLocal<>();

    public static void setCurrentMutation(Model<?> model, Mutator<?, ?, ?> mutator)
    {
        currentModel.set(model);
        currentMutator.set(mutator);

        if (mutator instanceof BipedMutator<?, ?, ?> bipedMutator)
            currentBipedMutator.set(bipedMutator);
        else if (mutator instanceof SpiderMutator spiderMutator)
            currentSpiderMutator.set(spiderMutator);
        else if (mutator instanceof WolfMutator wolfMutator)
            currentWolfMutator.set(wolfMutator);
    }

    public static boolean renderCurrentModel(Model<?> model, PoseStack poseStack, VertexConsumer vertexConsumer,
                                             int packedLight, int packedOverlay, int color)
    {
        Mutator<?, ?, ?> mutator = currentMutator.get();
        if (currentModel.get() != model || mutator == null || !mutator.shouldRenderCustom())
            return false;

        mutator.renderMutated(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        return true;
    }

    public static BipedMutator<?, ?, ?> getCurrentBipedMutator()
    {
        return currentBipedMutator.get();
    }

    public static SpiderMutator getCurrentSpiderMutator()
    {
        return currentSpiderMutator.get();
    }

    public static WolfMutator getCurrentWolfMutator()
    {
        return currentWolfMutator.get();
    }

    public static void clear()
    {
        currentModel.remove();
        currentMutator.remove();
        currentBipedMutator.remove();
        currentSpiderMutator.remove();
        currentWolfMutator.remove();
    }
}
