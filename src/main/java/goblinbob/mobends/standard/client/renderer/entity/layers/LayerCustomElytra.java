package goblinbob.mobends.standard.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import goblinbob.mobends.core.client.event.MoBendsRenderState;
import goblinbob.mobends.core.data.EntityData;
import goblinbob.mobends.core.data.EntityDatabase;
import goblinbob.mobends.standard.data.PlayerData;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.equipment.ElytraModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

/**
 * Custom elytra layer for Mo' Bends animated elytra rendering.
 * Updated for Minecraft 26.2 to use PoseStack-based rendering.
 */
public class LayerCustomElytra extends RenderLayer<AvatarRenderState, PlayerModel>
{
    /** The basic Elytra texture. */
    private static final Identifier TEXTURE_ELYTRA = Identifier.parse("textures/entity/elytra.png");
    /** The model used by the Elytra. */
    private final ElytraModel elytraModel;

    public LayerCustomElytra(RenderLayerParent<AvatarRenderState, PlayerModel> renderer, EntityModelSet modelSet)
    {
        super(renderer);
        this.elytraModel = new ElytraModel(modelSet.bakeLayer(ModelLayers.ELYTRA));
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

        if (state.chestEquipment.is(Items.ELYTRA))
        {
            Identifier texture;
            if (state.skin.elytra() != null)
            {
                texture = state.skin.elytra().texturePath();
            }
            else if (state.skin.cape() != null && state.showCape)
            {
                texture = state.skin.cape().texturePath();
            }
            else
            {
                texture = TEXTURE_ELYTRA;
            }

            poseStack.pushPose();
            data.body.applyCharacterTransform(poseStack, 0.0625F);
            poseStack.translate(0.0F, -12.0F * scale, 0.0F);

            this.elytraModel.setupAnim(state);
            submitNodeCollector.submitModel(this.elytraModel, state, poseStack,
                    RenderTypes.armorCutoutNoCull(texture), packedLight, OverlayTexture.NO_OVERLAY,
                    state.outlineColor, null);
            if (state.chestEquipment.hasFoil())
            {
                submitNodeCollector.submitModel(this.elytraModel, state, poseStack,
                        RenderTypes.armorEntityGlint(), packedLight, OverlayTexture.NO_OVERLAY,
                        state.outlineColor, null);
            }

            poseStack.popPose();
        }
    }
}
