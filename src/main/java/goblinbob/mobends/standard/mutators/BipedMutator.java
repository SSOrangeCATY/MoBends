package goblinbob.mobends.standard.mutators;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import goblinbob.mobends.core.client.model.BendsCube;
import goblinbob.mobends.core.client.model.BendsModelPart;
import goblinbob.mobends.core.client.model.IModelPart;
import goblinbob.mobends.core.data.IEntityDataFactory;
import goblinbob.mobends.core.math.Quaternion;
import goblinbob.mobends.core.mutators.Mutator;
import goblinbob.mobends.standard.client.renderer.entity.layers.LayerCustomBipedArmor;
import goblinbob.mobends.standard.client.renderer.entity.layers.LayerCustomHeldItem;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;

public abstract class BipedMutator<D extends BipedEntityData<E>,
                                   E extends LivingEntity,
                                   M extends EntityModel>
                                  extends Mutator<D, E, M>
{
    // Custom bendable parts
    protected BendsModelPart body;
    protected BendsModelPart head;
    protected BendsModelPart headwear;
    protected BendsModelPart leftArm;
    protected BendsModelPart rightArm;
    protected BendsModelPart leftForeArm;
    protected BendsModelPart rightForeArm;
    protected BendsModelPart leftLeg;
    protected BendsModelPart rightLeg;
    protected BendsModelPart leftForeLeg;
    protected BendsModelPart rightForeLeg;
    protected BendsModelPart babyBody;
    protected BendsModelPart babyHead;
    protected BendsModelPart babyHeadwear;
    protected BendsModelPart babyLeftArm;
    protected BendsModelPart babyRightArm;
    protected BendsModelPart babyLeftLeg;
    protected BendsModelPart babyRightLeg;

    // Store vanilla model parts for demutation
    protected ModelPart vanillaBody;
    protected ModelPart vanillaHead;
    protected ModelPart vanillaHat;
    protected ModelPart vanillaLeftArm;
    protected ModelPart vanillaRightArm;
    protected ModelPart vanillaLeftLeg;
    protected ModelPart vanillaRightLeg;

    protected LayerCustomBipedArmor layerArmor;
    protected Object layerArmorVanilla;
    protected LayerCustomHeldItem layerHeldItem;
    protected Object layerHeldItemVanilla;
    protected Object layerCustomHead;
    protected Object layerCustomHeadVanilla;
    protected D currentData;

    public BipedMutator(IEntityDataFactory<E> dataFactory)
    {
        super(dataFactory);
    }

    /**
     * Used to store the model parameter as the
     * vanilla model, so then the mutation can be
     * reversed.
     */
    @Override
    public void storeVanillaModel(M model)
    {
        if (model instanceof HumanoidModel<?> humanoidModel)
        {
            this.vanillaBody = humanoidModel.body;
            this.vanillaHead = humanoidModel.head;
            this.vanillaHat = humanoidModel.hat;
            this.vanillaLeftArm = humanoidModel.leftArm;
            this.vanillaRightArm = humanoidModel.rightArm;
            this.vanillaLeftLeg = humanoidModel.leftLeg;
            this.vanillaRightLeg = humanoidModel.rightLeg;
        }
    }

    /**
     * Sets the model parameter back to it's vanilla
     * state. Used to demutate the model.
     */
    @Override
    public void applyVanillaModel(M model)
    {
        // In 1.20.1, model parts are final and cannot be directly replaced
        // The demutation will need to handle this differently
        // For now, we just track that we need to restore vanilla rendering
    }

    /**
     * Swaps out the vanilla layers for their custom counterparts,
     * and if it's a vanilla model, it stores the vanilla layers
     * for future mutation reversal.
     */
    @Override
    public void swapLayer(LivingEntityRenderer<?, ?, ?> renderer, int index, boolean isModelVanilla)
    {
    }

    /**
     * Swaps the custom layers back with the vanilla layers.
     * Used to demutate the model.
     */
    @Override
    public void deswapLayer(LivingEntityRenderer<?, ?, ?> renderer, int index)
    {
    }

    /**
     * Creates all the custom parts you need! It creates custom
     * BendsModelPart instances for bendable limbs.
     */
    @Override
    public boolean createParts(M original, float scaleFactor)
    {
        // Create custom bendable parts
        // Body - the root of the upper body hierarchy
        body = new BendsModelPart(16, 16)
                .setTextureSize(64, 64)
                .setPosition(0.0F, 12.0F, 0.0F);
        body.addCube(-4.0F, -12.0F, -2.0F, 8, 12, 4, scaleFactor);

        // Head - child of body
        head = new BendsModelPart(0, 0)
                .setTextureSize(64, 64)
                .setPosition(0.0F, -12.0F, 0.0F);
        head.addCube(-4.0F, -8.0F, -4.0F, 8, 8, 8, scaleFactor);
        body.addChild(head);

        // Headwear - child of head
        headwear = new BendsModelPart(32, 0)
                .setTextureSize(64, 64);
        headwear.addCube(-4.0F, -8.0F, -4.0F, 8, 8, 8, scaleFactor + 0.5F);
        head.addChild(headwear);

        // Arms
        int armWidth = 4;
        float armY = -10F;

        // Left arm - child of body
        leftArm = new BendsModelPart(40, 16)
                .setTextureSize(64, 64)
                .setPosition(5.0F, armY, 0.0F)
                .setMirror(true);
        leftArm.addCube(-1.0F, -2.0F, -2.0F, armWidth, 6, 4, scaleFactor);
        body.addChild(leftArm);

        // Right arm - child of body
        rightArm = new BendsModelPart(40, 16)
                .setTextureSize(64, 64)
                .setPosition(-5.0F, armY, 0.0F);
        rightArm.addCube(-armWidth + 1, -2.0F, -2.0F, armWidth, 6, 4, scaleFactor);
        body.addChild(rightArm);

        // Left forearm - child of left arm
        leftForeArm = new BendsModelPart(40, 22)
                .setTextureSize(64, 64)
                .setPosition(0.0F, 4.0F, 2.0F)
                .setMirror(true);
        leftForeArm.addCube(-1.0F, 0.0F, -4.0F, armWidth, 6, 4, scaleFactor);
        leftArm.addChild(leftForeArm);

        // Right forearm - child of right arm
        rightForeArm = new BendsModelPart(40, 22)
                .setTextureSize(64, 64)
                .setPosition(0.0F, 4.0F, 2.0F);
        rightForeArm.addCube(-armWidth + 1, 0.0F, -4.0F, armWidth, 6, 4, scaleFactor);
        rightArm.addChild(rightForeArm);

        // Legs - independent roots (not children of body)
        rightLeg = new BendsModelPart(0, 16)
                .setTextureSize(64, 64)
                .setPosition(0.0F, 12F, 0F);
        rightLeg.addCube(-3.9F, 0.0F, -2.0F, 4, 6, 4, scaleFactor);

        leftLeg = new BendsModelPart(0, 16)
                .setTextureSize(64, 64)
                .setPosition(0.0F, 12.0F, 0.0F)
                .setMirror(true);
        leftLeg.addCube(-0.1F, 0.0F, -2.0F, 4, 6, 4, scaleFactor);

        // Left foreleg - child of left leg
        leftForeLeg = new BendsModelPart(0, 22)
                .setTextureSize(64, 64)
                .setPosition(0, 6.0F, -2.0F)
                .setMirror(true);
        leftForeLeg.addCube(-0.1F, 0.0F, 0.0F, 4, 6, 4, scaleFactor);
        leftLeg.addChild(leftForeLeg);

        // Right foreleg - child of right leg
        rightForeLeg = new BendsModelPart(0, 22)
                .setTextureSize(64, 64)
                .setPosition(0, 6.0F, -2.0F);
        rightForeLeg.addCube(-3.9F, 0.0F, 0.0F, 4, 6, 4, scaleFactor);
        rightLeg.addChild(rightForeLeg);

        createBabyParts(scaleFactor);

        return true;
    }

    protected void createBabyParts(float scaleFactor)
    {
        babyBody = new BendsModelPart(16, 16)
                .setTextureSize(64, 64)
                .setPosition(0.0F, 17.5F, 0.0F);
        babyBody.addCube(-2.0F, -2.5F, -1.0F, 4, 5, 2, scaleFactor);

        babyHead = new BendsModelPart(3, 3)
                .setTextureSize(64, 64)
                .setPosition(0.0F, -2.25F, 0.0F);
        babyHead.addCube(-3.0F, -6.25F, -3.0F, 6, 6, 6, scaleFactor);
        babyBody.addChild(babyHead);

        babyHeadwear = new BendsModelPart(35, 3)
                .setTextureSize(64, 64);
        babyHeadwear.addCube(-3.0F, -6.15F, -3.0F, 6, 6, 6, scaleFactor + 0.25F);
        babyHead.addChild(babyHeadwear);

        babyRightArm = new BendsModelPart(36, 16)
                .setTextureSize(64, 64)
                .setPosition(-3.0F, -2.0F, 0.0F);
        babyRightArm.addCube(-1.0F, -0.5F, -1.0F, 2, 5, 2, scaleFactor);
        babyBody.addChild(babyRightArm);

        babyLeftArm = new BendsModelPart(28, 16)
                .setTextureSize(64, 64)
                .setPosition(3.0F, -2.0F, 0.0F)
                .setMirror(true);
        babyLeftArm.addCube(-1.0F, -0.5F, -1.0F, 2, 5, 2, scaleFactor);
        babyBody.addChild(babyLeftArm);

        babyRightLeg = new BendsModelPart(8, 16)
                .setTextureSize(64, 64)
                .setPosition(-1.0F, 20.0F, 0.0F);
        babyRightLeg.addCube(-1.0F, 0.0F, -1.0F, 2, 4, 2, scaleFactor);

        babyLeftLeg = new BendsModelPart(0, 16)
                .setTextureSize(64, 64)
                .setPosition(1.0F, 20.0F, 0.0F)
                .setMirror(true);
        babyLeftLeg.addCube(-1.0F, 0.0F, -1.0F, 2, 4, 2, scaleFactor);
    }

    @Override
    public void syncUpWithData(D data)
    {
        this.currentData = data;
        head.syncUp(data.head);
        body.syncUp(data.body);
        leftArm.syncUp(data.leftArm);
        rightArm.syncUp(data.rightArm);
        leftLeg.syncUp(data.leftLeg);
        rightLeg.syncUp(data.rightLeg);
        leftForeArm.syncUp(data.leftForeArm);
        rightForeArm.syncUp(data.rightForeArm);
        leftForeLeg.syncUp(data.leftForeLeg);
        rightForeLeg.syncUp(data.rightForeLeg);
        syncBabyParts(data);
    }

    protected void syncBabyParts(D data)
    {
        syncBabyPart(babyBody, data.body);
        syncBabyPart(babyHead, data.head);
        syncBabyPart(babyLeftArm, data.leftArm);
        syncBabyPart(babyRightArm, data.rightArm);
        syncBabyPart(babyLeftLeg, data.leftLeg);
        syncBabyPart(babyRightLeg, data.rightLeg);
    }

    protected void syncBabyPart(BendsModelPart target, IModelPart source)
    {
        if (target == null || source == null)
            return;

        target.offset.set(source.getOffset());
        target.rotation.set(source.getRotation());
        target.scale.set(source.getScale());
        target.offsetScale = source.getOffsetScale();
        target.globalOffset.set(source.getGlobalOffset());
        target.setVisible(source.isShowing());
    }

    /**
     * True, if this renderer wasn't mutated before.
     */
    @Override
    public boolean isModelVanilla(M model)
    {
        // Check if we've already created custom parts
        return this.body == null;
    }

    @Override
    public boolean shouldModelBeSkipped(EntityModel<?> model)
    {
        return !(model instanceof HumanoidModel);
    }

    @Override
    public boolean shouldRenderCustom()
    {
        return this.body != null;
    }

    @Override
    public void renderMutated(PoseStack poseStack, VertexConsumer vertexConsumer,
                              int packedLight, int packedOverlay, int color)
    {
        poseStack.pushPose();
        if (isRenderingBaby())
            renderBabyBipedParts(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        else
            renderBipedParts(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        poseStack.popPose();
    }

    protected void renderBipedParts(PoseStack poseStack, VertexConsumer vertexConsumer,
                                    int packedLight, int packedOverlay, int color)
    {
        // Render body and attached parts
        if (body != null)
        {
            body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        }

        // Render legs (not attached to body)
        if (leftLeg != null)
        {
            leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        }
        if (rightLeg != null)
        {
            rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        }
    }

    protected void renderBabyBipedParts(PoseStack poseStack, VertexConsumer vertexConsumer,
                                        int packedLight, int packedOverlay, int color)
    {
        if (babyBody != null)
        {
            babyBody.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        }

        if (babyLeftLeg != null)
        {
            babyLeftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        }
        if (babyRightLeg != null)
        {
            babyRightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        }
    }

    protected boolean isRenderingBaby()
    {
        return babyBody != null && currentData != null && currentData.getEntity() != null && currentData.getEntity().isBaby();
    }

    // Getters for layers to access parts
    public BendsModelPart getBody() { return body; }
    public BendsModelPart getHead() { return head; }
    public BendsModelPart getLeftArm() { return leftArm; }
    public BendsModelPart getRightArm() { return rightArm; }
    public BendsModelPart getLeftForeArm() { return leftForeArm; }
    public BendsModelPart getRightForeArm() { return rightForeArm; }
    public BendsModelPart getLeftLeg() { return leftLeg; }
    public BendsModelPart getRightLeg() { return rightLeg; }
    public BendsModelPart getLeftForeLeg() { return leftForeLeg; }
    public BendsModelPart getRightForeLeg() { return rightForeLeg; }

    /**
     * Sync animated poses from BendsModelParts to vanilla HumanoidModel ModelParts.
     * This allows vanilla layers (armor, held items) to use our animated poses.
     */
    public void syncPosesToVanillaModel(HumanoidModel<?> model)
    {
        if (model == null) return;

        syncPartToModelPart(head, model.head);
        syncPartToModelPart(body, model.body);
        syncPartToModelPart(leftArm, model.leftArm);
        syncPartToModelPart(rightArm, model.rightArm);
        syncPartToModelPart(leftLeg, model.leftLeg);
        syncPartToModelPart(rightLeg, model.rightLeg);

        // Sync hat visibility with head
        if (model.hat != null && head != null)
        {
            model.hat.visible = head.isShowing();
        }
    }

    /**
     * Sync a single BendsModelPart's transform to a vanilla ModelPart.
     * Only syncs rotation and visibility - NOT position, because position defines
     * the pivot point and changing it would distort the armor geometry/textures.
     */
    private void syncPartToModelPart(BendsModelPart bendsPart, ModelPart modelPart)
    {
        if (bendsPart == null || modelPart == null) return;

        // DON'T sync position - the armor model's pivot points must stay at vanilla values
        // to avoid texture stretching. The armor follows the body through rotation only.

        // Convert quaternion rotation to Euler angles (XYZ order)
        Quaternion q = bendsPart.rotation.getSmooth();
        float[] euler = quaternionToEulerXYZ(q);
        modelPart.xRot = euler[0];
        modelPart.yRot = euler[1];
        modelPart.zRot = euler[2];

        // Sync visibility
        modelPart.visible = bendsPart.isShowing();
    }

    /**
     * Convert quaternion to Euler angles in XYZ rotation order (radians).
     * Returns [xRot, yRot, zRot].
     */
    private static float[] quaternionToEulerXYZ(Quaternion q)
    {
        float[] euler = new float[3];

        // X rotation (pitch)
        float sinX = 2.0f * (q.w * q.x + q.y * q.z);
        float cosX = 1.0f - 2.0f * (q.x * q.x + q.y * q.y);
        euler[0] = (float) Math.atan2(sinX, cosX);

        // Y rotation (yaw)
        float sinY = 2.0f * (q.w * q.y - q.z * q.x);
        if (Math.abs(sinY) >= 1.0f)
        {
            euler[1] = (float) Math.copySign(Math.PI / 2, sinY);
        }
        else
        {
            euler[1] = (float) Math.asin(sinY);
        }

        // Z rotation (roll)
        float sinZ = 2.0f * (q.w * q.z + q.x * q.y);
        float cosZ = 1.0f - 2.0f * (q.y * q.y + q.z * q.z);
        euler[2] = (float) Math.atan2(sinZ, cosZ);

        return euler;
    }

}
