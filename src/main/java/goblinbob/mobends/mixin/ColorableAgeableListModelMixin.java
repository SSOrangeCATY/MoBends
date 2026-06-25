package goblinbob.mobends.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import goblinbob.mobends.core.client.MoBendsRenderContext;
import goblinbob.mobends.standard.mutators.WolfMutator;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.wolf.WolfModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to intercept model rendering and redirect to MoBends custom rendering
 * when a wolf mutation is active.
 */
@Mixin(Model.class)
public abstract class ColorableAgeableListModelMixin {

    @Inject(method = "renderToBuffer", at = @At("HEAD"), cancellable = true)
    private void mobends$interceptWolfRender(PoseStack poseStack, VertexConsumer vertexConsumer,
                                             int packedLight, int packedOverlay, int color,
                                             CallbackInfo ci) {
        // Check if this is a WolfModel
        if ((Object) this instanceof WolfModel) {
            WolfMutator mutator = MoBendsRenderContext.getWolfMutator((Model) (Object) this);

            if (mutator != null && mutator.shouldRenderCustom()) {
                mutator.renderMutated(poseStack, vertexConsumer, packedLight, packedOverlay, color);
                ci.cancel();
            }
        }
    }
}
