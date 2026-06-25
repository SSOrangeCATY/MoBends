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
    public void crawlAnimationLeavesWholeBodyWallRotationToMutatorRenderPath() throws IOException
    {
        String bitSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/animation/bit/spider/SpiderCrawlAnimationBit.java"));
        String mutatorSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/mutators/SpiderMutator.java"));

        assertFalse(bitSource.contains("renderRotation.orientX(-90F)"));
        assertFalse(bitSource.contains("renderRotation.setSmoothness(.6F).rotateY"));
        assertTrue(bitSource.contains("data.renderRotation.orientZero();"));

        assertTrue(mutatorSource.contains("currentData.getWallFacing() != null"));
        assertTrue(mutatorSource.contains("if (isClimbing && spider != null)"));
        assertTrue(mutatorSource.contains("poseStack.mulPose(Axis.YP.rotationDegrees(bodyYaw - 180.0F));"));
        assertTrue(mutatorSource.contains("poseStack.mulPose(Axis.YP.rotationDegrees(climbingRotation));"));
        assertTrue(mutatorSource.contains("poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));"));

        int climbingBranch = mutatorSource.indexOf("if (isClimbing && spider != null)");
        int normalRenderRotationBranch = mutatorSource.indexOf("else if (!renderRot.isIdentity())");
        assertTrue(climbingBranch >= 0);
        assertTrue(normalRenderRotationBranch >= 0);
        assertTrue(climbingBranch < normalRenderRotationBranch);
    }
}
