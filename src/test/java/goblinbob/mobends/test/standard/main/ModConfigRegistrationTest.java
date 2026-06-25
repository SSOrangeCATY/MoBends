package goblinbob.mobends.test.standard.main;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ModConfigRegistrationTest
{
    @Test
    public void standardAnimationConfigIsRegisteredOnClient() throws IOException
    {
        String modConfigSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/main/ModConfig.java"));
        String clientBootstrapSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/client/ClientBootstrap.java"));

        assertTrue(modConfigSource.contains("public static void register(ModContainer container)"));
        assertTrue(modConfigSource.contains("container.registerConfig(net.neoforged.fml.config.ModConfig.Type.CLIENT, SPEC"));
        assertTrue(clientBootstrapSource.contains("ModConfig.register(container);"));
    }

    @Test
    public void configEventsSyncOnlyTheStandardAnimationSpec() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/main/ModConfig.java"));

        assertTrue(source.contains("event.getConfig().getSpec() == SPEC"));
    }
}
