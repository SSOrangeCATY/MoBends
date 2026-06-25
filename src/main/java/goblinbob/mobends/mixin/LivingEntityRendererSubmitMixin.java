package goblinbob.mobends.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import goblinbob.mobends.core.client.event.EntityRenderHandler;
import goblinbob.mobends.core.client.event.MoBendsRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererSubmitMixin
{
    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At("RETURN"))
    private void mobends$cleanupAfterSubmit(LivingEntityRenderState state, PoseStack poseStack,
                                            SubmitNodeCollector submitNodeCollector,
                                            CameraRenderState cameraRenderState,
                                            CallbackInfo ci)
    {
        LivingEntity living = state.getRenderData(MoBendsRenderState.LIVING_ENTITY);
        EntityRenderHandler.cleanupAfterLivingSubmit(living, state.partialTick, poseStack);
    }
}
