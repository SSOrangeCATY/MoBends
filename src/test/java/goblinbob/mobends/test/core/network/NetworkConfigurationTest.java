package goblinbob.mobends.test.core.network;

import goblinbob.mobends.core.network.NetworkConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NetworkConfigurationTest
{
    @Test
    public void moddedServerDefaultsAreSetFromClientSuppliedSingleplayerState()
    {
        NetworkConfiguration config = new NetworkConfiguration();

        config.onModdedServerJoin(true);
        assertTrue(config.isModelScalingAllowed());
        assertTrue(config.areBendsPacksAllowed());
        assertFalse(config.isMovementLimited());

        config.onModdedServerJoin(false);
        assertFalse(config.isModelScalingAllowed());
        assertTrue(config.areBendsPacksAllowed());
        assertTrue(config.isMovementLimited());
    }

    @Test
    public void commonNetworkConfigurationDoesNotReferenceMinecraftClient() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/network/NetworkConfiguration.java"));

        assertFalse(source.contains("net.minecraft.client.Minecraft"));
        assertFalse(source.contains("Minecraft.getInstance()"));
    }
}
