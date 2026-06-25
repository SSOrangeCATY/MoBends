package goblinbob.mobends.test.core.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ErrorReporterSideSafetyTest
{
    @Test
    public void commonErrorReporterDoesNotReferenceMinecraftClient() throws IOException
    {
        String source = Files.readString(Path.of("src/main/java/goblinbob/mobends/core/util/ErrorReporter.java"));

        assertFalse(source.contains("net.minecraft.client.Minecraft"));
        assertFalse(source.contains("Minecraft.getInstance()"));
    }

    @Test
    public void clientErrorDisplayLivesBehindClientOnlyHelper() throws IOException
    {
        Path clientHelper = Path.of("src/main/java/goblinbob/mobends/core/client/ClientErrorReporter.java");

        assertTrue(Files.isRegularFile(clientHelper));
        assertTrue(Files.readString(clientHelper).contains("Minecraft.getInstance()"));
    }
}
