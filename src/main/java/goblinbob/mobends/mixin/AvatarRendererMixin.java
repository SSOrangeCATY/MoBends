package goblinbob.mobends.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import goblinbob.mobends.core.util.BenderHelper;
import goblinbob.mobends.standard.mutators.PlayerMutator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin
{
    @Inject(
            method = "renderHand",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModelPart(Lnet/minecraft/client/model/geom/ModelPart;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IILnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void mobends$applyFirstPersonHandPose(
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int lightCoords,
            Identifier skinTexture,
            ModelPart arm,
            boolean hasSleeve,
            CallbackInfo callbackInfo,
            PlayerModel model
    ) {
        AbstractClientPlayer player = Minecraft.getInstance().player;
        if (player == null || !BenderHelper.isEntityAnimated(player))
            return;

        PlayerMutator mutator = (PlayerMutator) BenderHelper.getMutatorForRenderer(
                AbstractClientPlayer.class,
                (AvatarRenderer<AbstractClientPlayer>) (Object) this
        );
        if (mutator != null)
        {
            mutator.poseForFirstPersonView();
            mutator.applyFirstPersonPoseToVanillaArm(model, arm);
        }
    }
}
