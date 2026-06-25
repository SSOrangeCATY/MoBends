package goblinbob.mobends.mixin.armor;

import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Placeholder for the removed pre-26.2 armor hook.
 *
 * 26.2 armor rendering is driven by HumanoidRenderState, Equippable data
 * components, and SubmitNodeCollector. Mo' Bends now handles armor through
 * LayerCustomBipedArmor, so this mixin is intentionally not listed in
 * mobends.mixins.json.
 */
@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin {
}
