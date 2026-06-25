package goblinbob.mobends.mixin.armor;

import net.minecraft.client.renderer.entity.layers.WingsLayer;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Placeholder for the removed pre-26.2 elytra hook.
 *
 * ElytraLayer was replaced by WingsLayer and Mo' Bends swaps in
 * LayerCustomElytra through the renderer layer list. This mixin is kept
 * compile-safe for 26.2 but is intentionally not listed in mobends.mixins.json.
 */
@Mixin(WingsLayer.class)
public abstract class ElytraLayerMixin {
}
