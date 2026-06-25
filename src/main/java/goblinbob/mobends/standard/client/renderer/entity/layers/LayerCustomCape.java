package goblinbob.mobends.standard.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import goblinbob.mobends.core.client.event.MoBendsRenderState;
import goblinbob.mobends.core.data.EntityData;
import goblinbob.mobends.core.data.EntityDatabase;
import goblinbob.mobends.standard.client.renderer.entity.BendsCapeRenderer;
import goblinbob.mobends.standard.data.PlayerData;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

/**
 * Custom cape layer for Mo' Bends animated cape rendering.
 * Updated for Minecraft 26.2 to use PoseStack-based rendering.
 */
public class LayerCustomCape extends RenderLayer<AvatarRenderState, PlayerModel>
{

    private final BendsCapeRenderer capeRenderer;

    public LayerCustomCape(RenderLayerParent<AvatarRenderState, PlayerModel> renderer)
    {
        super(renderer);
        this.capeRenderer = new BendsCapeRenderer();
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight,
                       AvatarRenderState state, float yRot, float xRot)
    {
        AbstractClientPlayer player = (AbstractClientPlayer) state.getRenderData(MoBendsRenderState.LIVING_ENTITY);
        if (player == null)
        {
            return;
        }

        final EntityData<?> entityData = EntityDatabase.instance.get(player);
        if (!(entityData instanceof PlayerData))
            return;

        final PlayerData data = (PlayerData) entityData;
        final float scale = 0.0625F;

        if (state.skin.cape() != null && !state.isInvisible && state.showCape)
        {
            if (!state.chestEquipment.is(Items.ELYTRA))
            {
                poseStack.pushPose();

                if (state.isCrouching)
                {
                    if (player.getAbilities().flying)
                    {
                        poseStack.translate(0F, 4F * scale, 0F);
                    }
                    else
                    {
                        poseStack.translate(0F, 4F * scale, 0F);
                    }
                }

                data.body.applyLocalTransform(poseStack, 0.0625F);
                poseStack.translate(0.0F, -12.0F * scale, 2.2F * scale);
                data.cape.applyLocalTransform(poseStack, 0.0625F);
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

                capeRenderer.applyAnimation(data);
                Identifier capeTexture = state.skin.cape().texturePath();
                capeRenderer.submit(poseStack, submitNodeCollector, packedLight, capeTexture, state.outlineColor, 0.0625F);

                poseStack.popPose();
            }
        }
    }
}
