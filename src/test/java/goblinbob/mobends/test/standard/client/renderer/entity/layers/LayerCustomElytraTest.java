package goblinbob.mobends.test.standard.client.renderer.entity.layers;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LayerCustomElytraTest
{
    @Test
    public void enchantedElytraSubmitsArmorGlintLayer() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/client/renderer/entity/layers/LayerCustomElytra.java"));

        assertTrue(source.contains("state.chestEquipment.hasFoil()"));
        assertTrue(source.contains("RenderTypes.armorEntityGlint()"));
    }
}
