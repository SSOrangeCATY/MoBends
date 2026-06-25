package goblinbob.mobends.standard.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import goblinbob.mobends.core.client.event.MoBendsRenderState;
import goblinbob.mobends.core.data.EntityData;
import goblinbob.mobends.core.data.EntityDatabase;
import goblinbob.mobends.core.math.SmoothOrientation;
import goblinbob.mobends.core.util.GlHelper;
import goblinbob.mobends.standard.data.BipedEntityData;
import goblinbob.mobends.standard.mutators.BipedMutator;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwingAnimationType;
public class LayerCustomHeldItem<E extends LivingEntity, S extends HumanoidRenderState, M extends HumanoidModel<S>>
        extends RenderLayer<S, M>
{

    private final BipedMutator<?, E, S, M> mutator;

    public LayerCustomHeldItem(LivingEntityRenderer<E, S, M> renderer, BipedMutator<?, E, S, M> mutator)
    {
        super(renderer);
        this.mutator = mutator;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight,
                       S state, float yRot, float xRot)
    {
        @SuppressWarnings("unchecked")
        E entity = (E) state.getRenderData(MoBendsRenderState.LIVING_ENTITY);
        if (entity == null)
        {
            return;
        }

        this.submitHeldItem(entity, state, state.rightHandItemState, state.rightHandItemStack,
                HumanoidArm.RIGHT, poseStack, submitNodeCollector, packedLight);
        this.submitHeldItem(entity, state, state.leftHandItemState, state.leftHandItemStack,
                HumanoidArm.LEFT, poseStack, submitNodeCollector, packedLight);
    }

    private void submitHeldItem(E entity, S state, ItemStackRenderState itemState, ItemStack itemStack,
                                HumanoidArm arm, PoseStack poseStack,
                                SubmitNodeCollector submitNodeCollector, int packedLight)
    {
        if (itemState.isEmpty())
            return;

        poseStack.pushPose();

        this.translateToHand(arm, entity, state, poseStack);

        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        boolean leftHanded = arm == HumanoidArm.LEFT;
        float offsetX = state.isBaby ? 0.0F : 1.0F;
        float offsetY = state.isBaby ? 1.0F : 2.0F;
        float offsetZ = state.isBaby ? -4.5F : -10.0F;
        poseStack.translate((leftHanded ? -1 : 1) * offsetX / 16.0F, offsetY / 16.0F, offsetZ / 16.0F);

        if (state.attackTime > 0.0F && state.attackArm == arm && state.swingAnimationType == SwingAnimationType.STAB)
        {
            net.minecraft.client.model.effects.SpearAnimations.thirdPersonAttackItem(state, poseStack);
        }

        float ticksUsingItem = state.ticksUsingItem(arm);
        if (ticksUsingItem != 0.0F)
        {
            (arm == HumanoidArm.RIGHT ? state.rightArmPose : state.leftArmPose)
                    .animateUseItem(state, poseStack, ticksUsingItem, arm, itemStack);
        }

        itemState.submit(poseStack, submitNodeCollector, packedLight, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();
    }

    /**
     * Translates to the hand position with Mo' Bends custom animation transforms applied.
     * This replicates the original Mo' Bends postRenderArm behavior:
     * 1. arm.applyCharacterTransform (body + arm transforms)
     * 2. arm.applyPostTransform -> forearm.propagateTransform (forearm local + postOffset)
     * 3. Item rotation adjustment with translate/rotate/untranslate
     */
    protected void translateToHand(HumanoidArm arm, E entity, S state, PoseStack poseStack)
    {
        // Apply the full Mo' Bends transform chain matching the original postRenderArm behavior
        if (mutator != null && mutator.shouldRenderCustom())
        {
            var body = mutator.getBody();
            var armPart = arm == HumanoidArm.RIGHT ? mutator.getRightArm() : mutator.getLeftArm();
            var foreArm = arm == HumanoidArm.RIGHT ? mutator.getRightForeArm() : mutator.getLeftForeArm();

            if (body != null && armPart != null && foreArm != null)
            {
                float scale = 1.0F / 16.0F;

                // === arm.applyCharacterTransform ===
                // Apply body transform (position + offset + rotation)
                poseStack.translate(body.position.x * scale, body.position.y * scale, body.position.z * scale);
                if (body.offset.x != 0 || body.offset.y != 0 || body.offset.z != 0)
                {
                    poseStack.translate(body.offset.x * scale, body.offset.y * scale, body.offset.z * scale);
                }
                GlHelper.rotate(poseStack, body.rotation.getSmooth());

                // Apply arm transform (position + offset + rotation)
                poseStack.translate(armPart.position.x * scale, armPart.position.y * scale, armPart.position.z * scale);
                if (armPart.offset.x != 0 || armPart.offset.y != 0 || armPart.offset.z != 0)
                {
                    poseStack.translate(armPart.offset.x * scale, armPart.offset.y * scale, armPart.offset.z * scale);
                }
                GlHelper.rotate(poseStack, armPart.rotation.getSmooth());

                // === arm.applyPostTransform -> forearm.propagateTransform ===
                // Apply forearm local transform (position + offset + rotation)
                poseStack.translate(foreArm.position.x * scale, foreArm.position.y * scale, foreArm.position.z * scale);
                if (foreArm.offset.x != 0 || foreArm.offset.y != 0 || foreArm.offset.z != 0)
                {
                    poseStack.translate(foreArm.offset.x * scale, foreArm.offset.y * scale, foreArm.offset.z * scale);
                }
                GlHelper.rotate(poseStack, foreArm.rotation.getSmooth());

                // Apply forearm postOffset (0, -4, -2) - this positions the held item correctly
                // In the original, this was set via ModelPartPostOffset.setPostOffset(0, -4F, -2F)
                poseStack.translate(0, -4.0F * scale, -2.0F * scale);

                // === Item rotation adjustment ===
                // Apply custom item rotation with translate/rotate/untranslate pattern
                EntityData<?> entityData = EntityDatabase.instance.get(entity);
                if (entityData instanceof BipedEntityData<?> bipedData)
                {
                    SmoothOrientation itemRotation = arm == HumanoidArm.RIGHT
                            ? bipedData.renderRightItemRotation
                            : bipedData.renderLeftItemRotation;

                    poseStack.translate(0, 8.0F * scale, 0);
                    GlHelper.rotate(poseStack, itemRotation.getSmooth());
                    poseStack.translate(0, -8.0F * scale, 0);
                }

                return;
            }
        }

        // Fallback to vanilla transform if Mo' Bends parts not available
        M model = this.getParentModel();
        model.translateToHand(state, arm, poseStack);

        // Apply Mo' Bends custom item rotation
        EntityData<?> entityData = EntityDatabase.instance.get(entity);
        if (entityData instanceof BipedEntityData<?> bipedData)
        {
            SmoothOrientation itemRotation = arm == HumanoidArm.RIGHT
                    ? bipedData.renderRightItemRotation
                    : bipedData.renderLeftItemRotation;

            poseStack.translate(0, 8F * 0.0625F, 0);
            GlHelper.rotate(poseStack, itemRotation.getSmooth());
            poseStack.translate(0, -8F * 0.0625F, 0);
        }
    }
}
