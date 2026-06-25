package goblinbob.mobends.test.standard.animation.bit.wolf;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WolfSittingAnimationBitTest
{
    @Test
    public void sittingDownAnimationPointsAtBundledBinaryAnimation() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/animation/bit/wolf/WolfSittingAnimationBit.java"));

        assertTrue(source.contains("\"bends/animations/wolf_sitting_down.bendsanim\""));
        assertFalse(source.contains("\"bends/animations/wolf_sitting_down.json\""));
        assertTrue(Files.isRegularFile(Path.of(
                "src/main/resources/assets/mobends/bends/animations/wolf_sitting_down.bendsanim")));
    }
}
