package goblinbob.mobends.test.standard.mutators;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayerFirstPersonHandRenderTest
{
    @Test
    public void firstPersonPoseRestoresArmVisibilityBeforeRenderingHand() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/mutators/PlayerMutator.java"));

        assertTrue(source.contains("setFirstPersonPartVisible(this.rightArm);"));
        assertTrue(source.contains("setFirstPersonPartVisible(this.rightForeArm);"));
        assertTrue(source.contains("setFirstPersonPartVisible(this.leftArm);"));
        assertTrue(source.contains("setFirstPersonPartVisible(this.leftForeArm);"));
        assertTrue(source.contains("setFirstPersonPartVisible(this.rightArmwear);"));
        assertTrue(source.contains("setFirstPersonPartVisible(this.rightForeArmwear);"));
        assertTrue(source.contains("setFirstPersonPartVisible(this.leftArmwear);"));
        assertTrue(source.contains("setFirstPersonPartVisible(this.leftForeArmwear);"));
    }

    @Test
    public void firstPersonHandEventSyncsPoseBackToVanillaPlayerModel() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/client/event/RenderingEventHandler.java"));

        assertTrue(source.contains("mutator.poseForFirstPersonView();"));
        assertTrue(source.contains("renderPlayer.getModel() instanceof PlayerModel model"));
        assertTrue(source.contains("mutator.syncFirstPersonPoseToVanillaModel(model);"));
    }

    @Test
    public void avatarRendererAppliesFirstPersonPoseAfterVanillaHandReset() throws IOException
    {
        String mixin = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/mixin/AvatarRendererMixin.java"));
        String config = Files.readString(Path.of("src/main/resources/mobends.mixins.json"));

        assertTrue(mixin.contains("@Mixin(AvatarRenderer.class)"));
        assertTrue(mixin.contains("target = \"Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModelPart"));
        assertTrue(mixin.contains("mutator.applyFirstPersonPoseToVanillaArm(model, arm);"));
        assertTrue(config.contains("\"AvatarRendererMixin\""));
    }
}
