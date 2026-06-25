package goblinbob.mobends.test.core.compat;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayerAnimationLibCompatTest
{
    @Test
    public void usesPlayerAnimationLibrary26ApiNames() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/compat/PlayerAnimationLibCompat.java"));

        assertTrue(source.contains("\"player_animation_library\""));
        assertTrue(source.contains("com.zigythebird.playeranim.api.PlayerAnimationAccess"));
        assertTrue(source.contains("getPlayerAnimManager"));
        assertTrue(source.contains("com.zigythebird.playeranimcore.animation.layered.AnimationStack"));

        assertFalse(source.contains("\"playeranimator\""));
        assertFalse(source.contains("dev.kosmx"));
        assertFalse(source.contains("getPlayerAnimLayer"));
    }
}
