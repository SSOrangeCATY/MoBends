package goblinbob.mobends.standard.mutators;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import goblinbob.mobends.core.client.model.BendsModelPart;
import goblinbob.mobends.core.client.model.IModelPart;
import goblinbob.mobends.core.data.IEntityDataFactory;
import goblinbob.mobends.standard.data.PlayerData;
import goblinbob.mobends.standard.previewer.PlayerPreviewer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.player.PlayerModelType;

/**
 * Instantiated once per PlayerRenderer
 */
public class PlayerMutator extends BipedMutator<PlayerData, AbstractClientPlayer, PlayerModel>
{
    protected BendsModelPart bodywear;
    protected BendsModelPart leftArmwear;
    protected BendsModelPart rightArmwear;
    protected BendsModelPart leftForeArmwear;
    protected BendsModelPart rightForeArmwear;
    protected BendsModelPart leftLegwear;
    protected BendsModelPart rightLegwear;
    protected BendsModelPart leftForeLegwear;
    protected BendsModelPart rightForeLegwear;

    protected boolean smallArms;

    protected Object layerCape;
    protected Object layerCapeVanilla;
    protected Object layerElytra;
    protected Object layerElytraVanilla;
    protected Object layerPlayerAccessories;

    public PlayerMutator(IEntityDataFactory<AbstractClientPlayer> dataFactory)
    {
        super(dataFactory);
    }

    public boolean hasSmallArms()
    {
        return this.smallArms;
    }

    @Override
    public boolean mutate(LivingEntityRenderer<?, ?, ?> renderer)
    {
        return super.mutate(renderer);
    }

    @Override
    public void demutate(LivingEntityRenderer<?, ?, ?> renderer)
    {
        super.demutate(renderer);
    }

    @Override
    public void fetchFields(LivingEntityRenderer<?, ?, ?> renderer)
    {
        super.fetchFields(renderer);
    }

    /**
     * Update the smallArms field based on the player's model name.
     * This should be called when we have access to the player entity.
     */
    public void updateSmallArms(AbstractClientPlayer player)
    {
        if (player != null)
        {
            this.smallArms = player.getSkin().model() == PlayerModelType.SLIM;
        }
    }

    @Override
    public void storeVanillaModel(PlayerModel model)
    {
        super.storeVanillaModel(model);
    }

    @Override
    public void applyVanillaModel(PlayerModel model)
    {
        super.applyVanillaModel(model);
    }

    @Override
    public void swapLayer(LivingEntityRenderer<?, ?, ?> renderer, int index, boolean isModelVanilla)
    {
        super.swapLayer(renderer, index, isModelVanilla);
    }

    @Override
    public void deswapLayer(LivingEntityRenderer<?, ?, ?> renderer, int index)
    {
        super.deswapLayer(renderer, index);
    }

    @Override
    public boolean createParts(PlayerModel original, float scaleFactor)
    {
        // Arms
        int armWidth = this.smallArms ? 3 : 4;
        float armY = this.smallArms ? -9.5F : -10F;

        // Create custom bendable parts using BendsModelPart
        // Body - root of upper body hierarchy
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

        // Left Arm (texture at 32, 48 for player) - child of body
        leftArm = new BendsModelPart(32, 48)
                .setTextureSize(64, 64)
                .setPosition(5.0F, armY, 0.0F)
                .setMirror(true);
        leftArm.addCube(-1.0F, -2.0F, -2.0F, armWidth, 6, 4, scaleFactor);
        body.addChild(leftArm);

        // Right Arm (texture at 40, 16 for player) - child of body
        rightArm = new BendsModelPart(40, 16)
                .setTextureSize(64, 64)
                .setPosition(-5.0F, armY, 0.0F);
        rightArm.addCube(-armWidth + 1, -2.0F, -2.0F, armWidth, 6, 4, scaleFactor);
        body.addChild(rightArm);

        // Left Forearm - child of leftArm
        leftForeArm = new BendsModelPart(32, 48 + 6)
                .setTextureSize(64, 64)
                .setPosition(0.0F, 4.0F, 2.0F)
                .setMirror(true);
        leftForeArm.addCube(-1.0F, 0.0F, -4.0F, armWidth, 6, 4, scaleFactor);
        leftArm.addChild(leftForeArm);

        // Right Forearm - child of rightArm
        rightForeArm = new BendsModelPart(40, 16 + 6)
                .setTextureSize(64, 64)
                .setPosition(0.0F, 4.0F, 2.0F);
        rightForeArm.addCube(-armWidth + 1, 0.0F, -4.0F, armWidth, 6, 4, scaleFactor);
        rightArm.addChild(rightForeArm);

        // Legs (texture at 16, 48 for left leg, 0, 16 for right leg in player model)
        // Legs are independent roots (not children of body)
        leftLeg = new BendsModelPart(16, 48)
                .setTextureSize(64, 64)
                .setPosition(1.9F, 12.0F, 0.0F)
                .setMirror(true);
        leftLeg.addCube(-2.0F, 0.0F, -2.0F, 4, 6, 4, scaleFactor);

        rightLeg = new BendsModelPart(0, 16)
                .setTextureSize(64, 64)
                .setPosition(-1.9F, 12.0F, 0.0F);
        rightLeg.addCube(-2.0F, 0.0F, -2.0F, 4, 6, 4, scaleFactor);

        // Left Foreleg - child of leftLeg
        leftForeLeg = new BendsModelPart(16, 48 + 6)
                .setTextureSize(64, 64)
                .setPosition(0.0F, 6.0F, -2.0F)
                .setMirror(true);
        leftForeLeg.addCube(-2.0F, 0.0F, 0.0F, 4, 6, 4, scaleFactor);
        leftLeg.addChild(leftForeLeg);

        // Right Foreleg - child of rightLeg
        rightForeLeg = new BendsModelPart(0, 16 + 6)
                .setTextureSize(64, 64)
                .setPosition(0.0F, 6.0F, -2.0F);
        rightForeLeg.addCube(-2.0F, 0.0F, 0.0F, 4, 6, 4, scaleFactor);
        rightLeg.addChild(rightForeLeg);

        // Wear layers (second skin layer)
        float wearOffset = 0.25F;

        // Bodywear - child of body
        bodywear = new BendsModelPart(16, 32)
                .setTextureSize(64, 64);
        bodywear.addCube(-4.0F, -12.0F, -2.0F, 8, 12, 4, scaleFactor + wearOffset);
        body.addChild(bodywear);

        // Left arm wear - child of leftArm
        leftArmwear = new BendsModelPart(48, 48)
                .setTextureSize(64, 64)
                .setMirror(true);
        leftArmwear.addCube(-1.0F, -2.0F, -2.0F, armWidth, 6, 4, scaleFactor + wearOffset);
        leftArm.addChild(leftArmwear);

        // Right arm wear - child of rightArm
        rightArmwear = new BendsModelPart(40, 32)
                .setTextureSize(64, 64);
        rightArmwear.addCube(-armWidth + 1, -2.0F, -2.0F, armWidth, 6, 4, scaleFactor + wearOffset);
        rightArm.addChild(rightArmwear);

        // Left forearm wear - child of leftForeArm
        leftForeArmwear = new BendsModelPart(48, 48 + 6)
                .setTextureSize(64, 64)
                .setMirror(true);
        leftForeArmwear.addCube(-1.0F, 0.0F, -4.0F, armWidth, 6, 4, scaleFactor + wearOffset);
        leftForeArm.addChild(leftForeArmwear);

        // Right forearm wear - child of rightForeArm
        rightForeArmwear = new BendsModelPart(40, 32 + 6)
                .setTextureSize(64, 64);
        rightForeArmwear.addCube(-armWidth + 1, 0.0F, -4.0F, armWidth, 6, 4, scaleFactor + wearOffset);
        rightForeArm.addChild(rightForeArmwear);

        // Left leg wear - child of leftLeg
        leftLegwear = new BendsModelPart(0, 48)
                .setTextureSize(64, 64)
                .setMirror(true);
        leftLegwear.addCube(-2.0F, 0.0F, -2.0F, 4, 6, 4, scaleFactor + wearOffset);
        leftLeg.addChild(leftLegwear);

        // Right leg wear - child of rightLeg
        rightLegwear = new BendsModelPart(0, 32)
                .setTextureSize(64, 64);
        rightLegwear.addCube(-2.0F, 0.0F, -2.0F, 4, 6, 4, scaleFactor + wearOffset);
        rightLeg.addChild(rightLegwear);

        // Left foreleg wear - child of leftForeLeg
        leftForeLegwear = new BendsModelPart(0, 48 + 6)
                .setTextureSize(64, 64)
                .setMirror(true);
        leftForeLegwear.addCube(-2.0F, 0.0F, 0.0F, 4, 6, 4, scaleFactor + wearOffset);
        leftForeLeg.addChild(leftForeLegwear);

        // Right foreleg wear - child of rightForeLeg
        rightForeLegwear = new BendsModelPart(0, 32 + 6)
                .setTextureSize(64, 64);
        rightForeLegwear.addCube(-2.0F, 0.0F, 0.0F, 4, 6, 4, scaleFactor + wearOffset);
        rightForeLeg.addChild(rightForeLegwear);

        return true;
    }

    @Override
    public void syncUpWithData(PlayerData data)
    {
        super.syncUpWithData(data);
        // Sync wear parts with their base parts - they share the same transforms
    }

    @Override
    public void performAnimations(PlayerData data, String animatedEntityKey,
                                   LivingEntityRenderer<?, ?, ?> renderer,
                                   float partialTicks)
    {
        // Sync wear visibility with base parts
        if (leftForeArmwear != null && leftArmwear != null)
            leftForeArmwear.setVisible(leftArmwear.isShowing());
        if (rightForeArmwear != null && rightArmwear != null)
            rightForeArmwear.setVisible(rightArmwear.isShowing());
        if (leftForeLegwear != null && leftLegwear != null)
            leftForeLegwear.setVisible(leftLegwear.isShowing());
        if (rightForeLegwear != null && rightLegwear != null)
            rightForeLegwear.setVisible(rightLegwear.isShowing());

        super.performAnimations(data, animatedEntityKey, renderer, partialTicks);
    }

    @Override
    public void postRefresh()
    {
    }

    /**
     * Called before the first person hand is rendered, so the mutator can pose it
     * in any way.
     */
    public void poseForFirstPersonView()
    {
        if (this.body != null) this.body.getRotation().identity();
        if (this.rightArm != null) this.rightArm.getRotation().identity();
        if (this.rightForeArm != null) this.rightForeArm.getRotation().identity();
        if (this.leftArm != null) this.leftArm.getRotation().identity();
        if (this.leftForeArm != null) this.leftForeArm.getRotation().identity();
    }

    @Override
    public boolean isModelVanilla(PlayerModel model)
    {
        // Check if we've already created custom parts
        return this.body == null;
    }

    @Override
    public boolean shouldModelBeSkipped(EntityModel<?> model)
    {
        return !(model instanceof PlayerModel);
    }

    @Override
    public PlayerData getData(AbstractClientPlayer entity)
    {
        // Update slim arms based on the actual player's skin model
        // This ensures correct detection even if initial reflection failed
        if (entity != null && !PlayerPreviewer.isPreviewInProgress())
        {
            boolean playerIsSlim = entity.getSkin().model() == PlayerModelType.SLIM;
            if (playerIsSlim != this.smallArms)
            {
                goblinbob.mobends.standard.main.MoBends.LOG.debug(
                    "Slim arm mismatch detected for {}: mutator={}, player={}. Updating.",
                    entity.getName().getString(), this.smallArms, playerIsSlim);
                this.smallArms = playerIsSlim;
                // Note: Parts are already created with the old dimension.
                // They will be corrected on next mutation cycle.
            }
        }
        return PlayerPreviewer.isPreviewInProgress() ? PlayerPreviewer.getPreviewData() : super.getData(entity);
    }

    @Override
    public PlayerData getOrMakeData(AbstractClientPlayer entity)
    {
        return PlayerPreviewer.isPreviewInProgress() ? PlayerPreviewer.getPreviewData() : super.getOrMakeData(entity);
    }

    @Override
    public void renderMutated(PoseStack poseStack, VertexConsumer vertexConsumer,
                              int packedLight, int packedOverlay,
                              int packedColor)
    {
        // Render body and all attached parts (head, arms)
        if (body != null)
        {
            body.render(poseStack, vertexConsumer, packedLight, packedOverlay, packedColor);
        }

        // Render legs (not attached to body)
        if (leftLeg != null)
        {
            leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, packedColor);
        }
        if (rightLeg != null)
        {
            rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, packedColor);
        }
    }

    // Getters for wear parts
    public BendsModelPart getBodywear() { return bodywear; }
    public BendsModelPart getLeftArmwear() { return leftArmwear; }
    public BendsModelPart getRightArmwear() { return rightArmwear; }
    public BendsModelPart getLeftForeArmwear() { return leftForeArmwear; }
    public BendsModelPart getRightForeArmwear() { return rightForeArmwear; }
    public BendsModelPart getLeftLegwear() { return leftLegwear; }
    public BendsModelPart getRightLegwear() { return rightLegwear; }
    public BendsModelPart getLeftForeLegwear() { return leftForeLegwear; }
    public BendsModelPart getRightForeLegwear() { return rightForeLegwear; }
}
