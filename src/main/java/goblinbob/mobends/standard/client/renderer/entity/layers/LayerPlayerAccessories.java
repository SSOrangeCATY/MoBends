package goblinbob.mobends.standard.client.renderer.entity.layers;

import com.google.gson.JsonElement;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import goblinbob.mobends.core.asset.AssetLocation;
import goblinbob.mobends.core.asset.AssetModels;
import goblinbob.mobends.core.client.event.MoBendsRenderState;
import goblinbob.mobends.core.client.model.IModelPart;
import goblinbob.mobends.core.data.EntityData;
import goblinbob.mobends.core.data.EntityDatabase;
import goblinbob.mobends.core.supporters.*;
import goblinbob.mobends.core.util.Color;
import goblinbob.mobends.standard.data.PlayerData;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.joml.Vector3f;

import java.util.Map;
import java.util.Set;

/**
 * Layer for rendering player accessories (supporter content).
 * Updated for Minecraft 26.2 to use PoseStack-based rendering.
 */
public class LayerPlayerAccessories extends RenderLayer<AvatarRenderState, PlayerModel>
{
    public LayerPlayerAccessories(RenderLayerParent<AvatarRenderState, PlayerModel> renderer)
    {
        super(renderer);
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

        Set<Map.Entry<String, AccessoryDetails>> accessories = SupporterContent.getAccessories();

        final EntityData<?> entityData = EntityDatabase.instance.get(player);
        if (!(entityData instanceof PlayerData))
            return;

        final PlayerData data = (PlayerData) entityData;
        final float scale = 0.0625F;

        Map<String, AccessorySettings> settingsMap = SupporterContent.getAccessorySettingsMapFor(player);

        for (Map.Entry<String, AccessoryDetails> entry : accessories)
        {
            AccessorySettings accessorySettings = settingsMap.getOrDefault(entry.getKey(), AccessorySettings.DEFAULT);
            renderAccessory(poseStack, submitNodeCollector, packedLight, player, data, entry.getValue(), accessorySettings, scale);
        }
    }

    private void renderAccessory(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight,
                                 AbstractClientPlayer player, PlayerData data, AccessoryDetails accessory,
                                 AccessorySettings settings, float scale)
    {
        if (!settings.isUnlocked() || settings.isHidden())
        {
            return;
        }

        for (AccessoryPart part : accessory.getParts())
        {
            renderPart(poseStack, submitNodeCollector, packedLight, player, data, part, settings, scale);
        }
    }

    private void renderPart(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight,
                            AbstractClientPlayer player, PlayerData data, AccessoryPart part,
                            AccessorySettings settings, float scale)
    {
        JsonElement modelJson = AssetModels.INSTANCE.getModel(part.getModelPath());

        if (modelJson == null)
        {
            return;
        }

        poseStack.pushPose();

        // Reverting the sneak transform
        if (player.isCrouching())
        {
            if (player.getAbilities().flying)
            {
                poseStack.translate(0F, 4F * scale, 0F);
            }
            else
            {
                poseStack.translate(0F, 3F * scale, 0F);
            }
        }

        applyBindPointTransform(poseStack, data, part.getBindPoint(), scale);

        // Apply item camera transforms - in 26.2 this is done differently
        // The model's transforms are applied through the rendering pipeline
        Vector3f translation = part.getTranslation();
        Vector3f rotation = part.getRotation();
        Vector3f scaleVec = part.getScale();

        if (translation != null)
        {
            poseStack.translate(translation.x, translation.y, translation.z);
        }
        if (rotation != null)
        {
            poseStack.mulPose(Axis.XP.rotationDegrees(rotation.x));
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation.y));
            poseStack.mulPose(Axis.ZP.rotationDegrees(rotation.z));
        }
        if (scaleVec != null)
        {
            poseStack.scale(scaleVec.x, scaleVec.y, scaleVec.z);
        }

        AccessoryModelSubmitter.submit(modelJson, poseStack, submitNodeCollector,
                part.getDiffuseTexturePath(), packedLight, 0xFFFFFFFF);

        AssetLocation inkedLocation = part.getInkedTexturePath();
        if (inkedLocation != null)
        {
            AccessoryModelSubmitter.submit(modelJson, poseStack, submitNodeCollector,
                    inkedLocation, packedLight, Color.asHex(settings.getColor()));
        }

        poseStack.popPose();
    }

    private void applyBindPointTransform(PoseStack poseStack, PlayerData data, BindPoint bindPoint, float scale)
    {
        IModelPart modelPart = bindPoint.getPartSelector().apply(data);

        if (modelPart != null)
        {
            modelPart.applyCharacterTransform(poseStack, scale);
        }
    }
}
