package goblinbob.mobends.mixin;

import goblinbob.mobends.core.client.MoBendsRenderContext;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelFeatureRenderer.class)
public abstract class ModelFeatureRendererMixin
{
    @Inject(method = "prepareModel", at = @At("HEAD"))
    private <S> void mobends$beginModelRender(ModelFeatureRenderer.Submit<S> submit, CallbackInfo callbackInfo)
    {
        Model<? super S> model = submit.model();
        MoBendsRenderContext.beginModelRender(model, submit.state());
    }

    @Inject(method = "prepareModel", at = @At("RETURN"))
    private <S> void mobends$endModelRender(ModelFeatureRenderer.Submit<S> submit, CallbackInfo callbackInfo)
    {
        Model<? super S> model = submit.model();
        MoBendsRenderContext.endModelRender(model);
    }
}
