package goblinbob.mobends.test.standard.animation.bit.biped;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SwimmingAnimationBitTest
{
    @Test
    public void swimmingDoesNotRotateWholePlayerUpsideDown() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/animation/bit/biped/SwimmingAnimationBit.java"));

        assertFalse(source.contains("data.renderRotation.setSmoothness(.7F).orientX(t * 80F);"));
        assertTrue(source.contains("data.renderRotation.setSmoothness(.7F).orientZero();"));
    }
}
