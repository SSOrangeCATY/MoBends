package goblinbob.mobends.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import goblinbob.mobends.core.client.MoBendsRenderContext;
import net.minecraft.client.model.Model;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Model.class)
public class ModelMixin
{
    @Inject(method = "renderToBuffer", at = @At("HEAD"), cancellable = true)
    private void mobends$renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer,
                                        int packedLight, int packedOverlay, int color, CallbackInfo callbackInfo)
    {
        if (MoBendsRenderContext.renderCurrentModel((Model<?>) (Object) this, poseStack, vertexConsumer, packedLight, packedOverlay, color))
        {
            callbackInfo.cancel();
        }
    }
}
