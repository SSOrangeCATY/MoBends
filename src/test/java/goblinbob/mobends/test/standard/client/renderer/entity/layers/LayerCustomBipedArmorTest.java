package goblinbob.mobends.test.standard.client.renderer.entity.layers;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LayerCustomBipedArmorTest
{
    @Test
    public void armorLayerInterceptsVanillaEquipmentSubmissionsForBending() throws IOException
    {
        String layerSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/client/renderer/entity/layers/LayerCustomBipedArmor.java"));
        String bridgeSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/client/renderer/entity/layers/BendingArmorSubmitNodeCollector.java"));
        String accessorSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/mixin/armor/HumanoidArmorLayerAccessor.java"));
        String mixinsSource = Files.readString(Path.of("src/main/resources/mobends.mixins.json"));

        assertTrue(layerSource.contains("EquipmentLayerRenderer"));
        assertTrue(layerSource.contains("BendingArmorSubmitNodeCollector"));
        assertTrue(layerSource.contains("ModConfig.shouldKeepArmorAsVanilla"));
        assertTrue(layerSource.contains("renderArmorPiece("));
        assertFalse(layerSource.contains("segmented custom armor deformation can be rebuilt"));

        assertTrue(bridgeSource.contains("submitCustomGeometry"));
        assertTrue(bridgeSource.contains("ArmorRenderingFacade"));
        assertTrue(bridgeSource.contains("renderModelToConsumer"));

        assertTrue(accessorSource.contains("@Accessor(\"modelSet\")"));
        assertTrue(accessorSource.contains("@Accessor(\"babyModelSet\")"));
        assertTrue(accessorSource.contains("@Accessor(\"equipmentRenderer\")"));
        assertTrue(mixinsSource.contains("armor.HumanoidArmorLayerAccessor"));
    }
}
