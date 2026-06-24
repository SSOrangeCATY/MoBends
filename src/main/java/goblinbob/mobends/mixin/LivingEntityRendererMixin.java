package goblinbob.mobends.mixin;

import goblinbob.mobends.core.client.event.EntityRenderHandler;
import goblinbob.mobends.core.client.event.MoBendsRenderState;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin
{
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("TAIL"))
    private void mobends$extractRenderState(LivingEntity entity, LivingEntityRenderState renderState, float partialTicks, CallbackInfo callbackInfo)
    {
        renderState.setRenderData(MoBendsRenderState.LIVING_ENTITY, entity);
        EntityRenderHandler.applyLivingEntityMutation((LivingEntityRenderer<?, ?, ?>) (Object) this, entity, partialTicks);
    }
}
