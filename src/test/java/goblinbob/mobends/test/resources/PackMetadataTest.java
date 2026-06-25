package goblinbob.mobends.test.resources;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PackMetadataTest
{
    @Test
    public void packMetadataSupportsMinecraft262ResourceAndDataFormats() throws IOException
    {
        String metadata = Files.readString(Path.of("src/main/resources/pack.mcmeta"))
                .replaceAll("\\s+", "");

        assertTrue(metadata.contains("\"pack_format\":88"));
        assertTrue(metadata.contains("\"min_format\":88"));
        assertTrue(metadata.contains("\"max_format\":[107,1]"));
    }
}
