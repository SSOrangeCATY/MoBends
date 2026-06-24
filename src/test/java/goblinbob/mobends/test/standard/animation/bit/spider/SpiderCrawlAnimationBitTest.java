package goblinbob.mobends.test.standard.animation.bit.spider;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpiderCrawlAnimationBitTest
{
    @Test
    public void crawlAnimationDoesNotStoreWholeBodyWallRotation() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/animation/bit/spider/SpiderCrawlAnimationBit.java"));

        assertFalse(source.contains("data.renderRotation.orientX(-90F);"));
        assertFalse(source.contains("renderRotationY"));
        assertTrue(source.contains("data.renderRotation.orientZero();"));
    }

    @Test
    public void spiderMutatorAppliesWallRotationWhenRendering() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/mutators/SpiderMutator.java"));

        assertTrue(source.contains("if (isClimbing && spider != null)"));
        assertTrue(source.contains("poseStack.mulPose(Axis.YP.rotationDegrees(bodyYaw - 180.0F));"));
        assertTrue(source.contains("poseStack.mulPose(Axis.YP.rotationDegrees(climbingRotation));"));
        assertTrue(source.contains("poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));"));
    }
}
