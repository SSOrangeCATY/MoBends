package goblinbob.mobends.test.core;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CoreSideSafetyTest
{
    @Test
    public void commonCoreDoesNotConstructClientCoreDirectly() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/Core.java"));

        assertFalse(source.contains("new CoreClient()"));
        assertFalse(source.contains("createAsClient"));
    }

    @Test
    public void commonCoreBytecodeDoesNotReferenceClientCore() throws IOException
    {
        String classBytes = Files.readString(Path.of(
                "build/classes/java/main/goblinbob/mobends/core/Core.class"),
                StandardCharsets.ISO_8859_1);

        assertFalse(classBytes.contains("goblinbob/mobends/core/CoreClient"));
    }

    @Test
    public void clientCoreOwnsClientInstanceCreation() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/CoreClient.java"));

        assertTrue(source.contains("public static void create()"));
        assertTrue(source.contains("Core.setInstance(new CoreClient())"));
    }
}
