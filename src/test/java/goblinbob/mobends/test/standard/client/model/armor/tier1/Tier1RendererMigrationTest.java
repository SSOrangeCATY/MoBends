package goblinbob.mobends.test.standard.client.model.armor.tier1;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class Tier1RendererMigrationTest
{
    @Test
    public void tier1RendererDoesNotKeepUnusedMigrationPlaceholders() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/client/model/armor/tier1/Tier1Renderer.java"));

        assertFalse(source.contains("placeholder"));
        assertFalse(source.contains("PartStateStorage"));
    }
}
