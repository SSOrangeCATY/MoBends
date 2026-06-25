package goblinbob.mobends.mixin.armor;

import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Placeholder for the removed pre-26.2 armor trim hook.
 *
 * Armor trim rendering is folded into EquipmentLayerRenderer in 26.2. Mo' Bends
 * delegates armor submission through LayerCustomBipedArmor, so this mixin is
 * intentionally not listed in mobends.mixins.json.
 */
@Mixin(HumanoidArmorLayer.class)
public abstract class ArmorTrimLayerMixin {
}
