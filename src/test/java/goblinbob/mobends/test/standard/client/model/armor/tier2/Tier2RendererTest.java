package goblinbob.mobends.test.standard.client.model.armor.tier2;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Tier2RendererTest
{
    @Test
    public void modelRootUsesMinecraft26ModelRootAccessor() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/client/model/armor/tier2/Tier2Renderer.java"));

        assertTrue(source.contains("return model.root();"));
        assertFalse(source.contains("getDeclaredField(\"root\")"));
    }
}
