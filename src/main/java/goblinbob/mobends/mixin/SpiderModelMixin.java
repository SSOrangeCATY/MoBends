package goblinbob.mobends.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import goblinbob.mobends.core.client.MoBendsRenderContext;
import goblinbob.mobends.standard.mutators.SpiderMutator;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.monster.spider.SpiderModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to intercept model rendering and redirect to MoBends custom rendering
 * when a spider mutation is active. In 26.2 SpiderModel extends EntityModel
 * directly and renderToBuffer is defined on Model.
 */
@Mixin(Model.class)
public abstract class SpiderModelMixin {

    @Inject(method = "renderToBuffer", at = @At("HEAD"), cancellable = true)
    private void mobends$interceptSpiderRender(PoseStack poseStack, VertexConsumer vertexConsumer,
                                               int packedLight, int packedOverlay, int color,
                                               CallbackInfo ci) {
        // Only intercept if this is a SpiderModel and we have an active spider mutator
        if (!((Object) this instanceof SpiderModel)) {
            return;
        }

        SpiderMutator mutator = MoBendsRenderContext.getSpiderMutator((Model) (Object) this);

        if (mutator != null && mutator.shouldRenderCustom()) {
            mutator.renderMutated(poseStack, vertexConsumer, packedLight, packedOverlay, color);
            ci.cancel();
        }
    }
}
