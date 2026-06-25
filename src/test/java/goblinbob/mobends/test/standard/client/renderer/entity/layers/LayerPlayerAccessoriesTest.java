package goblinbob.mobends.test.standard.client.renderer.entity.layers;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LayerPlayerAccessoriesTest
{
    @Test
    public void accessoryLayerSubmitsDiffuseAndInkedGeometry() throws IOException
    {
        String layerSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/client/renderer/entity/layers/LayerPlayerAccessories.java"));
        String rendererSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/client/renderer/entity/layers/AccessoryModelSubmitter.java"));

        assertTrue(layerSource.contains("AccessoryModelSubmitter.submit("));
        assertTrue(layerSource.contains("part.getDiffuseTexturePath()"));
        assertTrue(layerSource.contains("part.getInkedTexturePath()"));
        assertTrue(rendererSource.contains("submitNodeCollector.submitCustomGeometry("));
        assertTrue(rendererSource.contains("\"elements\""));
        assertFalse(layerSource.contains("submission needs to be rebuilt"));
    }
}
