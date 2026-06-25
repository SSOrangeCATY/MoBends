package goblinbob.mobends.standard.client.renderer.entity.mutated;

import com.mojang.blaze3d.vertex.PoseStack;
import goblinbob.mobends.core.data.EntityData;
import net.minecraft.client.player.AbstractClientPlayer;

/**
 * Renderer for player entities with Mo' Bends animations.
 * Updated for Minecraft 26.2 to use PoseStack-based rendering.
 */
public class PlayerRenderer extends BipedRenderer<AbstractClientPlayer>
{

    @Override
    protected void transformLocally(AbstractClientPlayer entity, EntityData<?> data, float partialTicks, PoseStack poseStack)
    {
        if (entity.isCrouching())
        {
            if (entity.getAbilities().flying)
            {
                poseStack.translate(0F, 4F * scale, 0F);
            }
            else
            {
                poseStack.translate(0F, 5F * scale, 0F);
            }
        }
    }

}
