package goblinbob.mobends.standard.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import goblinbob.mobends.core.client.event.MoBendsRenderState;
import goblinbob.mobends.core.data.EntityData;
import goblinbob.mobends.core.data.EntityDatabase;
import goblinbob.mobends.mixin.armor.HumanoidArmorLayerAccessor;
import goblinbob.mobends.standard.client.model.armor.ArmorRenderingFacade;
import goblinbob.mobends.standard.data.BipedEntityData;
import goblinbob.mobends.standard.main.ModConfig;
import goblinbob.mobends.standard.mutators.BipedMutator;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public class LayerCustomBipedArmor<E extends LivingEntity, S extends HumanoidRenderState, M extends HumanoidModel<S>>
        extends RenderLayer<S, M>
{
    private final BipedMutator<?, E, S, M> mutator;
    private final ArmorRenderingFacade armorRenderingFacade = new ArmorRenderingFacade();
    private HumanoidArmorLayer<S, M, ?> vanillaArmorLayer;

    public LayerCustomBipedArmor(LivingEntityRenderer<E, S, M> renderer, BipedMutator<?, E, S, M> mutator)
    {
        super(renderer);
        this.mutator = mutator;
    }

    public void setVanillaArmorLayer(HumanoidArmorLayer<S, M, ?> vanillaLayer)
    {
        this.vanillaArmorLayer = vanillaLayer;
    }

    public void initArmor()
    {
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords,
                       S state, float yRot, float xRot)
    {
        if (this.mutator != null)
        {
            this.mutator.syncPosesToVanillaModel(this.getParentModel());
        }

        if (this.vanillaArmorLayer == null)
        {
            return;
        }

        E entity = (E) state.getRenderData(MoBendsRenderState.LIVING_ENTITY);
        EntityData<?> entityData = entity == null ? null : EntityDatabase.instance.get(entity);
        BipedEntityData<?> bipedData = entityData instanceof BipedEntityData<?> data ? data : null;

        renderArmorPiece(poseStack, submitNodeCollector, state.chestEquipment, EquipmentSlot.CHEST, lightCoords, state, entity, bipedData);
        renderArmorPiece(poseStack, submitNodeCollector, state.legsEquipment, EquipmentSlot.LEGS, lightCoords, state, entity, bipedData);
        renderArmorPiece(poseStack, submitNodeCollector, state.feetEquipment, EquipmentSlot.FEET, lightCoords, state, entity, bipedData);
        renderArmorPiece(poseStack, submitNodeCollector, state.headEquipment, EquipmentSlot.HEAD, lightCoords, state, entity, bipedData);
    }

    private void renderArmorPiece(PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
                                  ItemStack armorStack, EquipmentSlot slot, int lightCoords,
                                  S state, E entity, BipedEntityData<?> bipedData)
    {
        Equippable equippable = armorStack.get(DataComponents.EQUIPPABLE);
        if (equippable == null || !shouldRender(equippable, slot))
        {
            return;
        }

        if (bipedData == null || ModConfig.shouldKeepArmorAsVanilla(armorStack.getItem()))
        {
            renderVanillaArmorPiece(poseStack, submitNodeCollector, armorStack, slot, lightCoords, state);
            return;
        }

        HumanoidArmorLayerAccessor<S, M, HumanoidModel<S>> accessor =
                (HumanoidArmorLayerAccessor<S, M, HumanoidModel<S>>) this.vanillaArmorLayer;
        EquipmentLayerRenderer equipmentRenderer = accessor.mobends$getEquipmentRenderer();
        HumanoidModel<S> armorModel = getArmorModel(accessor, state, slot);
        EquipmentClientInfo.LayerType layerType = getLayerType(state, slot);
        ResourceKey<?> assetId = equippable.assetId().orElseThrow();

        BendingArmorSubmitNodeCollector<E, S> bendingCollector = new BendingArmorSubmitNodeCollector<>(
                submitNodeCollector, armorRenderingFacade, poseStack, entity, bipedData, slot, armorStack);
        equipmentRenderer.renderLayers(layerType, (ResourceKey) assetId, armorModel, state, armorStack,
                poseStack, bendingCollector, lightCoords, state.outlineColor);
    }

    private void renderVanillaArmorPiece(PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
                                         ItemStack armorStack, EquipmentSlot slot, int lightCoords, S state)
    {
        HumanoidArmorLayerAccessor<S, M, HumanoidModel<S>> accessor =
                (HumanoidArmorLayerAccessor<S, M, HumanoidModel<S>>) this.vanillaArmorLayer;
        EquipmentLayerRenderer equipmentRenderer = accessor.mobends$getEquipmentRenderer();
        Equippable equippable = armorStack.get(DataComponents.EQUIPPABLE);
        if (equippable == null)
        {
            return;
        }

        HumanoidModel<S> armorModel = getArmorModel(accessor, state, slot);
        EquipmentClientInfo.LayerType layerType = getLayerType(state, slot);
        ResourceKey<?> assetId = equippable.assetId().orElseThrow();
        equipmentRenderer.renderLayers(layerType, (ResourceKey) assetId, armorModel, state, armorStack,
                poseStack, submitNodeCollector, lightCoords, state.outlineColor);
    }

    private HumanoidModel<S> getArmorModel(HumanoidArmorLayerAccessor<S, M, HumanoidModel<S>> accessor, S state, EquipmentSlot slot)
    {
        ArmorModelSet<HumanoidModel<S>> armorModels = state.isBaby ? accessor.mobends$getBabyModelSet() : accessor.mobends$getModelSet();
        return armorModels.get(slot);
    }

    private EquipmentClientInfo.LayerType getLayerType(S state, EquipmentSlot slot)
    {
        if (state.isBaby && state.entityType != EntityTypes.ARMOR_STAND)
        {
            return EquipmentClientInfo.LayerType.HUMANOID_BABY;
        }
        return slot == EquipmentSlot.LEGS
                ? EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS
                : EquipmentClientInfo.LayerType.HUMANOID;
    }

    private boolean shouldRender(Equippable equippable, EquipmentSlot slot)
    {
        return equippable.assetId().isPresent() && equippable.slot() == slot;
    }
}
