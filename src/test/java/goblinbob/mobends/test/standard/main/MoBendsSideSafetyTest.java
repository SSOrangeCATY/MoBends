package goblinbob.mobends.test.standard.main;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MoBendsSideSafetyTest
{
    @Test
    public void modEntrypointDelegatesClientSetupWithoutHardClientReferences() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/main/MoBends.java"));

        assertFalse(source.contains("goblinbob.mobends.core.client.event.KeyboardHandler"));
        assertFalse(source.contains("goblinbob.mobends.core.client.event.MoBendsRenderState"));
        assertFalse(source.contains("goblinbob.mobends.core.configuration.CoreClientConfig"));
        assertFalse(source.contains("FMLClientSetupEvent"));
        assertTrue(source.contains("Class.forName(CLIENT_BOOTSTRAP_CLASS)"));
    }

    @Test
    public void modEntrypointBytecodeDoesNotContainKnownClientOnlyClassReferences() throws IOException
    {
        String classBytes = Files.readString(Path.of(
                "build/classes/java/main/goblinbob/mobends/standard/main/MoBends.class"),
                StandardCharsets.ISO_8859_1);

        assertFalse(classBytes.contains("goblinbob/mobends/core/client/event/KeyboardHandler"));
        assertFalse(classBytes.contains("goblinbob/mobends/core/client/event/MoBendsRenderState"));
        assertFalse(classBytes.contains("goblinbob/mobends/core/configuration/CoreClientConfig"));
        assertFalse(classBytes.contains("net/neoforged/fml/event/lifecycle/FMLClientSetupEvent"));
    }

    @Test
    public void dedicatedServerCommonSetupCreatesServerCore() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/main/MoBends.java"));

        assertTrue(source.contains("FMLEnvironment.getDist() == Dist.DEDICATED_SERVER"));
        assertTrue(source.contains("Core.createAsServer();"));
        assertTrue(source.contains("Core.getInstance().onCommonSetup();"));
    }
}
