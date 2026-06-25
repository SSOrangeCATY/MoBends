package goblinbob.mobends.test.core.configuration;

import goblinbob.mobends.core.network.NetworkConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CoreServerConfigTest
{
    @Test
    public void networkConfigurationAcceptsExplicitServerPolicyValues()
    {
        NetworkConfiguration config = new NetworkConfiguration();

        config.updateServerValues(true, false, false);

        assertTrue(config.isModelScalingAllowed());
        assertFalse(config.areBendsPacksAllowed());
        assertFalse(config.isMovementLimited());
    }

    @Test
    public void serverConfigDefinesAndSyncsAllSharedNetworkPolicies() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/configuration/CoreServerConfig.java"));

        assertTrue(source.contains("MODEL_SCALING_ALLOWED"));
        assertTrue(source.contains("BENDS_PACKS_ALLOWED"));
        assertTrue(source.contains("MOVEMENT_LIMITED"));
        assertTrue(source.contains("updateServerValues("));
        assertTrue(source.contains("event.getConfig().getSpec() == SPEC"));
    }

    @Test
    public void configResponseRefreshesSharedConfigBeforeSerializing() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/network/msg/ConfigResponsePayload.java"));

        assertTrue(source.contains("CoreServerConfig.syncNetworkConfiguration();"));
    }
}
