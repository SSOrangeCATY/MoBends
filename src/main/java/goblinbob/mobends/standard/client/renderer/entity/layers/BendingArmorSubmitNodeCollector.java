package goblinbob.mobends.standard.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import goblinbob.mobends.standard.client.model.armor.ArmorRenderContext;
import goblinbob.mobends.standard.client.model.armor.ArmorRenderingFacade;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.gizmos.DrawableGizmoPrimitives;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class BendingArmorSubmitNodeCollector<E extends LivingEntity, S>
        implements SubmitNodeCollector, OrderedSubmitNodeCollector
{
    private final SubmitNodeCollector delegate;
    private final OrderedSubmitNodeCollector orderedDelegate;
    private final ArmorRenderingFacade armorRenderingFacade;
    private final PoseStack poseStack;
    private final E entity;
    private final BipedEntityData<?> entityData;
    private final EquipmentSlot slot;
    private final ItemStack armorStack;
    private final int order;

    public BendingArmorSubmitNodeCollector(SubmitNodeCollector delegate,
                                           ArmorRenderingFacade armorRenderingFacade,
                                           PoseStack poseStack,
                                           E entity,
                                           BipedEntityData<?> entityData,
                                           EquipmentSlot slot,
                                           ItemStack armorStack)
    {
        this(delegate, delegate, armorRenderingFacade, poseStack, entity, entityData, slot, armorStack, 0);
    }

    private BendingArmorSubmitNodeCollector(SubmitNodeCollector delegate,
                                            OrderedSubmitNodeCollector orderedDelegate,
                                            ArmorRenderingFacade armorRenderingFacade,
                                            PoseStack poseStack,
                                            E entity,
                                            BipedEntityData<?> entityData,
                                            EquipmentSlot slot,
                                            ItemStack armorStack,
                                            int order)
    {
        this.delegate = delegate;
        this.orderedDelegate = orderedDelegate;
        this.armorRenderingFacade = armorRenderingFacade;
        this.poseStack = poseStack;
        this.entity = entity;
        this.entityData = entityData;
        this.slot = slot;
        this.armorStack = armorStack;
        this.order = order;
    }

    @Override
    public OrderedSubmitNodeCollector order(int order)
    {
        return new BendingArmorSubmitNodeCollector<>(delegate, delegate.order(order),
                armorRenderingFacade, poseStack, entity, entityData, slot, armorStack, order);
    }

    @Override
    public <T> void submitModel(Model<? super T> model, T state, PoseStack poseStack, RenderType renderType,
                                int lightCoords, int overlayCoords, int tintedColor,
                                TextureAtlasSprite sprite, int outlineColor,
                                ModelFeatureRenderer.CrumblingOverlay crumblingOverlay)
    {
        if (sprite == null && crumblingOverlay == null && renderBendingModel(model, state, renderType,
                lightCoords, overlayCoords, tintedColor))
        {
            return;
        }

        orderedDelegate.submitModel(model, state, poseStack, renderType, lightCoords, overlayCoords,
                tintedColor, sprite, outlineColor, crumblingOverlay);
    }

    private <T> boolean renderBendingModel(Model<? super T> model, T state, RenderType renderType,
                                           int lightCoords, int overlayCoords, int tintedColor)
    {
        if (model == null || entityData == null)
        {
            return false;
        }

        if (model instanceof HumanoidModel<?> humanoidModel)
        {
            model.setupAnim(state);
            ArmorRenderContext<E> context = ArmorRenderContext.<E>builder()
                    .entity(entity)
                    .entityData(entityData)
                    .slot(slot)
                    .armorStack(armorStack)
                    .poseStack(poseStack)
                    .submitNodeCollector(this)
                    .packedLight(lightCoords)
                    .packedOverlay(overlayCoords)
                    .partialTicks(0.0F)
                    .armorModel(humanoidModel)
                    .forcedArmorColor(tintedColor)
                    .build();

            return armorRenderingFacade.renderModelToConsumer(context, model, renderType);
        }

        return false;
    }

    @Override
    public void submitShadow(PoseStack poseStack, float radius, List<EntityRenderState.ShadowPiece> shadowPieces)
    {
        orderedDelegate.submitShadow(poseStack, radius, shadowPieces);
    }

    @Override
    public void submitNameTag(PoseStack poseStack, Vec3 pos, int packedLight, Component component,
                              boolean shouldShowName, int backgroundColor, CameraRenderState cameraRenderState)
    {
        orderedDelegate.submitNameTag(poseStack, pos, packedLight, component, shouldShowName, backgroundColor, cameraRenderState);
    }

    @Override
    public void submitText(PoseStack poseStack, float x, float y, FormattedCharSequence text, boolean seeThrough,
                           Font.DisplayMode displayMode, int backgroundColor, int color, int packedLight, int order)
    {
        orderedDelegate.submitText(poseStack, x, y, text, seeThrough, displayMode, backgroundColor, color, packedLight, order);
    }

    @Override
    public void submitFlame(PoseStack poseStack, EntityRenderState state, org.joml.Quaternionf quaternion)
    {
        orderedDelegate.submitFlame(poseStack, state, quaternion);
    }

    @Override
    public void submitLeash(PoseStack poseStack, EntityRenderState.LeashState leashState)
    {
        orderedDelegate.submitLeash(poseStack, leashState);
    }

    @Override
    public void submitMovingBlock(PoseStack poseStack, MovingBlockRenderState movingBlockRenderState, int lightCoords)
    {
        orderedDelegate.submitMovingBlock(poseStack, movingBlockRenderState, lightCoords);
    }

    @Override
    public void submitBlockModel(PoseStack poseStack, RenderType renderType, List<BlockStateModelPart> parts,
                                 int[] tintValues, int lightCoords, int overlayCoords, int order)
    {
        orderedDelegate.submitBlockModel(poseStack, renderType, parts, tintValues, lightCoords, overlayCoords, order);
    }

    @Override
    public void submitBreakingBlockModel(PoseStack poseStack, List<BlockStateModelPart> parts, int progress)
    {
        orderedDelegate.submitBreakingBlockModel(poseStack, parts, progress);
    }

    @Override
    public void submitShapeOutline(PoseStack poseStack, VoxelShape shape, RenderType renderType, int color,
                                   float lineWidth, boolean polygonOffset)
    {
        orderedDelegate.submitShapeOutline(poseStack, shape, renderType, color, lineWidth, polygonOffset);
    }

    @Override
    public void submitItem(PoseStack poseStack, ItemDisplayContext displayContext, int lightCoords,
                           int overlayCoords, int seed, int[] tintValues, List<net.minecraft.client.resources.model.geometry.BakedQuad> quads,
                           ItemStackRenderState.FoilType foilType)
    {
        orderedDelegate.submitItem(poseStack, displayContext, lightCoords, overlayCoords, seed, tintValues, quads, foilType);
    }

    @Override
    public void submitCustomGeometry(PoseStack poseStack, RenderType renderType, CustomGeometryRenderer renderer)
    {
        orderedDelegate.submitCustomGeometry(poseStack, renderType, renderer);
    }

    @Override
    public void submitQuadParticleGroup(QuadParticleRenderState state)
    {
        orderedDelegate.submitQuadParticleGroup(state);
    }

    @Override
    public void submitGizmoPrimitives(DrawableGizmoPrimitives.Group group, CameraRenderState cameraRenderState, boolean visibleThroughBlocks)
    {
        orderedDelegate.submitGizmoPrimitives(group, cameraRenderState, visibleThroughBlocks);
    }

    @Override
    public void submitModelPart(ModelPart modelPart, PoseStack poseStack, RenderType renderType, int lightCoords,
                                int overlayCoords, TextureAtlasSprite sprite, int tintedColor,
                                ModelFeatureRenderer.CrumblingOverlay crumblingOverlay, int outlineColor)
    {
        orderedDelegate.submitModelPart(modelPart, poseStack, renderType, lightCoords, overlayCoords,
                sprite, tintedColor, crumblingOverlay, outlineColor);
    }
}
