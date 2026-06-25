package goblinbob.mobends.mixin;

import goblinbob.mobends.core.client.MoBendsRenderContext;
import goblinbob.mobends.standard.mutators.BipedMutator;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin
{
    @Inject(method = "setupAnim", at = @At("TAIL"))
    private void mobends$afterSetupAnim(CallbackInfo callbackInfo)
    {
        BipedMutator<?, ?, ?, ?> bipedMutator =
                MoBendsRenderContext.getBipedMutator((Model) (Object) this);
        if (bipedMutator != null)
        {
            bipedMutator.syncPosesToVanillaModel((HumanoidModel<?>) (Object) this);
        }
    }
}
