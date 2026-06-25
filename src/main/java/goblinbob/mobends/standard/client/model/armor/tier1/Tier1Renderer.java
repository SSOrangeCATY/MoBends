package goblinbob.mobends.standard.client.model.armor.tier1;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import goblinbob.mobends.core.client.model.ModelPartTransform;
import goblinbob.mobends.core.util.GlHelper;
import goblinbob.mobends.standard.client.model.armor.ArmorPoseHelper;
import goblinbob.mobends.standard.client.model.armor.ArmorRenderContext;
import goblinbob.mobends.standard.client.model.armor.CapturedVertex;
import goblinbob.mobends.standard.client.model.armor.CapturingVertexConsumer;
import goblinbob.mobends.standard.client.model.armor.JointDefinitions;
import goblinbob.mobends.standard.client.model.armor.JointPlane;
import goblinbob.mobends.standard.client.model.armor.QuadSlicer;
import goblinbob.mobends.standard.client.model.armor.SliceResult;
import goblinbob.mobends.standard.client.model.armor.cache.CacheManager;
import goblinbob.mobends.standard.client.model.armor.tier.RenderTier;
import goblinbob.mobends.standard.client.model.armor.tier2.Tier2Renderer;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Tier 1 renderer: Transform Injection.
 *
 * This is the most efficient rendering approach, used for ~85% of armor.
 * It works with vanilla and standard modded armor that uses HumanoidModel.
 *
 * Approach:
 * 1. Inject Mo'Bends transforms directly into the armor model's parts
 * 2. For limbs, use split rendering to handle elbow/knee bending
 * 3. Render using vanilla's rendering path with modified transforms
 * 4. Restore original transforms after rendering
 *
 * Falls back to Tier 2 if the model doesn't have expected structure.
 */
public class Tier1Renderer
{
    private final TransformInjector transformInjector;
    private final SplitLimbRenderer splitLimbRenderer;
    private final Tier2Renderer tier2Fallback;

    // Vertex capture and slicing for limb joint deformation
    private final CapturingVertexConsumer limbCapture = new CapturingVertexConsumer();
    private final QuadSlicer quadSlicer = new QuadSlicer();

    // Performance tracking
    private long renderCount = 0;
    private long fallbackCount = 0;

    // Current armor color for rendering (set per-render, used by vertex output)
    // Format: ARGB packed int (0xAARRGGBB)
    private int currentArmorColor = 0xFFFFFFFF;

    public Tier1Renderer()
    {
        this.transformInjector = new TransformInjector();
        this.splitLimbRenderer = new SplitLimbRenderer();
        this.tier2Fallback = new Tier2Renderer();
    }

    public Tier1Renderer(TransformInjector transformInjector, SplitLimbRenderer splitLimbRenderer, Tier2Renderer tier2Fallback)
    {
        this.transformInjector = transformInjector;
        this.splitLimbRenderer = splitLimbRenderer;
        this.tier2Fallback = tier2Fallback;
    }

    /**
     * Get the tier this renderer handles.
     */
    public RenderTier getTier()
    {
        return RenderTier.TIER_1_TRANSFORM_INJECTION;
    }

    /**
     * Simple render method for facade integration.
     * Returns true if rendering was handled, false to indicate fallback needed.
     */
    public <E extends LivingEntity> boolean render(ArmorRenderContext<E> context, HumanoidModel<?> model)
    {
        if (context == null || model == null || context.getEntityData() == null)
        {
            return false;
        }

        // We need a texture to render - check if it can be derived from context
        // For now, return false to indicate caller should use renderWithTexture
        return false;
    }

    /**
     * Render armor with a specific texture.
     * Returns true if rendering was handled, false to indicate fallback needed.
     */
    public <E extends LivingEntity> boolean renderWithTexture(
            ArmorRenderContext<E> context,
            HumanoidModel<?> model,
            Identifier texture,
            boolean hasFoil)
    {
        if (context == null || model == null || context.getEntityData() == null || texture == null)
        {
            return false;
        }

        try
        {
            renderWithFoil(context, model, texture, hasFoil);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Render armor with proper foil/glint support.
     */
    private <E extends LivingEntity> void renderWithFoil(
            ArmorRenderContext<E> context,
            HumanoidModel<?> model,
            Identifier texture,
            boolean hasFoil)
    {
        SubmitNodeCollector collector = context.getSubmitNodeCollector();
        if (collector == null)
        {
            return;
        }

        submitWithRenderType(context, model, RenderTypes.armorCutoutNoCull(texture), true);
        if (hasFoil)
        {
            submitWithRenderType(context, model, RenderTypes.armorEntityGlint(), false);
        }
    }

    /**
     * Render armor using the transform injection approach.
     *
     * @param context The armor render context
     * @param model The armor model to render
     * @param texture The armor texture
     * @param renderTypeProvider Function to get RenderType from texture
     * @param <E> Entity type
     */
    public <E extends LivingEntity> void render(
            ArmorRenderContext<E> context,
            Model model,
            Identifier texture,
            Function<Identifier, RenderType> renderTypeProvider)
    {
        // Verify we have a HumanoidModel
        if (!(model instanceof HumanoidModel<?> humanoidModel))
        {
            // Fall back to Tier 2
            fallbackCount++;
            tier2Fallback.render(context, model, texture, renderTypeProvider);
            return;
        }

        if (context.getEntityData() == null)
        {
            // No animation data - render vanilla
            renderVanilla(context, humanoidModel, texture, renderTypeProvider);
            return;
        }

        RenderType renderType = renderTypeProvider.apply(texture);
        submitWithRenderType(context, humanoidModel, renderType, true);
    }

    private <E extends LivingEntity> void submitWithRenderType(
            ArmorRenderContext<E> context,
            HumanoidModel<?> model,
            RenderType renderType,
            boolean recordStats)
    {
        SubmitNodeCollector collector = context.getSubmitNodeCollector();
        if (collector == null)
        {
            return;
        }

        collector.submitCustomGeometry(context.getPoseStack(), renderType, (pose, vertexConsumer) ->
        {
            PoseStack submittedPoseStack = new PoseStack();
            submittedPoseStack.last().set(pose);
            renderInternal(context, model, vertexConsumer, submittedPoseStack, recordStats);
        });
    }

    /**
     * Internal render method that takes a pre-configured VertexConsumer.
     * Used by renderWithFoil to properly support enchantment glint.
     */
    private <E extends LivingEntity> void renderInternal(
            ArmorRenderContext<E> context,
            HumanoidModel<?> humanoidModel,
            VertexConsumer vertexConsumer,
            PoseStack poseStack,
            boolean recordStats)
    {
        if (context.getEntityData() == null)
        {
            return;
        }

        if (recordStats)
        {
            renderCount++;
        }

        // Set armor color for leather armor support
        currentArmorColor = context.getArmorColor();

        BipedEntityData<?> entityData = context.getEntityData();
        EquipmentSlot slot = context.getSlot();

        // Configure visibility based on slot
        configureVisibility(humanoidModel, slot);

        // Get entity scale (1.0 for adults, 0.5 for babies)
        float entityScale = context.getEntityScale();

        // Get slim arm flag for arm position correction
        boolean isSlimArms = context.isSlimArms();

        // Render based on slot - each method applies proper hierarchical transforms
        switch (slot)
        {
            case HEAD:
                renderHead(poseStack, vertexConsumer, humanoidModel, entityData, context.getPackedLight(), context.getPackedOverlay(), entityScale);
                break;
            case CHEST:
                renderChest(poseStack, vertexConsumer, humanoidModel, entityData, context.getPackedLight(), context.getPackedOverlay(), entityScale, isSlimArms);
                break;
            case LEGS:
                renderLegs(poseStack, vertexConsumer, humanoidModel, entityData, context.getPackedLight(), context.getPackedOverlay(), entityScale);
                break;
            case FEET:
                renderFeet(poseStack, vertexConsumer, humanoidModel, entityData, context.getPackedLight(), context.getPackedOverlay(), entityScale);
                break;
        }

        // Record cache statistics
        if (recordStats)
        {
            CacheManager.getInstance().recordCacheAssistedRender();
        }
    }

    public <E extends LivingEntity> boolean renderToConsumer(
            ArmorRenderContext<E> context,
            HumanoidModel<?> humanoidModel,
            VertexConsumer vertexConsumer,
            PoseStack poseStack,
            boolean recordStats)
    {
        if (context == null || humanoidModel == null || vertexConsumer == null || poseStack == null || context.getEntityData() == null)
        {
            return false;
        }

        try
        {
            renderInternal(context, humanoidModel, vertexConsumer, poseStack, recordStats);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Render head armor piece.
     * Head is parented to body, so we apply body transform first, then head transform.
     *
     * Uses vertex capture approach (like arms) for consistent transform handling.
     */
    private void renderHead(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            HumanoidModel<?> model,
            BipedEntityData<?> entityData,
            int packedLight,
            int packedOverlay,
            float entityScale)
    {
        poseStack.pushPose();

        // Apply baby scale if needed (babies render at 0.5 scale)
        if (entityScale != 1.0f)
        {
            poseStack.scale(entityScale, entityScale, entityScale);
        }

        // Render helmet
        if (model.head != null && model.head.visible)
        {
            renderCapturedPart(poseStack, vertexConsumer, model.head, entityData, true, packedLight, packedOverlay, entityScale);
        }

        // Render hat overlay
        if (model.hat != null && model.hat.visible)
        {
            renderCapturedPart(poseStack, vertexConsumer, model.hat, entityData, true, packedLight, packedOverlay, entityScale);
        }

        poseStack.popPose();
    }

    /**
     * Render a part using vertex capture approach (consistent with arm/leg rendering).
     * This captures geometry at rest pose, then transforms vertices using Mo'Bends data.
     *
     * @param poseStack The pose stack
     * @param vertexConsumer The vertex consumer
     * @param part The model part to render
     * @param entityData The entity's animation data
     * @param isHead true for head parts (uses body + head transform), false otherwise
     * @param packedLight Light value
     * @param packedOverlay Overlay value
     * @param entityScale Scale factor (1.0 for adults, 0.5 for babies)
     */
    private void renderCapturedPart(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            ModelPart part,
            BipedEntityData<?> entityData,
            boolean isHead,
            int packedLight,
            int packedOverlay,
            float entityScale)
    {
        if (part == null || !part.visible)
        {
            return;
        }

        // 1. Capture geometry at rest pose using IDENTITY PoseStack
        limbCapture.clear();
        PoseStack captureStack = new PoseStack();  // Identity matrix
        resetPartToOrigin(part);
        part.render(captureStack, limbCapture, packedLight, packedOverlay);
        restorePartFromCapture(part);

        List<CapturedVertex> vertices = limbCapture.getVertices();
        if (vertices.isEmpty())
        {
            return;
        }

        // 2. Apply Mo'Bends transforms to PoseStack
        // Note: Global transforms (globalOffset, centerRotation, etc.) are already applied
        // by MutatedRenderer.beforeRender() to the PoseStack before armor renders.
        poseStack.pushPose();
        ArmorPoseHelper.applyPartTransform(poseStack, entityData.body, true);  // Body transform (head is parented to body)

        if (isHead)
        {
            ArmorPoseHelper.applyPartTransform(poseStack, entityData.head, true);  // Head transform
        }

        // 3. Transform and output vertices
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        // Output vertices directly (head has no slicing, just quads)
        for (CapturedVertex v : vertices)
        {
            // Transform position by matrix
            float tx = matrix.m00() * v.x + matrix.m10() * v.y + matrix.m20() * v.z + matrix.m30();
            float ty = matrix.m01() * v.x + matrix.m11() * v.y + matrix.m21() * v.z + matrix.m31();
            float tz = matrix.m02() * v.x + matrix.m12() * v.y + matrix.m22() * v.z + matrix.m32();

            // Transform normal by normal matrix
            float nx = normal.m00() * v.normalX + normal.m10() * v.normalY + normal.m20() * v.normalZ;
            float ny = normal.m01() * v.normalX + normal.m11() * v.normalY + normal.m21() * v.normalZ;
            float nz = normal.m02() * v.normalX + normal.m12() * v.normalY + normal.m22() * v.normalZ;

            // Output transformed vertex
            // Apply armor color tint (for leather armor dyeing)
            float tintR = ((currentArmorColor >> 16) & 0xFF) / 255.0F;
            float tintG = ((currentArmorColor >> 8) & 0xFF) / 255.0F;
            float tintB = (currentArmorColor & 0xFF) / 255.0F;
            float tintA = ((currentArmorColor >> 24) & 0xFF) / 255.0F;

            // Pack tinted RGBA into single int for 26.2
            int color = ((int)(v.alpha * tintA * 255.0F) << 24) |
                        ((int)(v.red * tintR * 255.0F) << 16) |
                        ((int)(v.green * tintG * 255.0F) << 8) |
                        (int)(v.blue * tintB * 255.0F);
            vertexConsumer.addVertex(tx, ty, tz)
                    .setColor(color)
                    .setUv(v.u, v.v)
                    .setOverlay(packedOverlay)
                    .setLight(packedLight)
                    .setNormal(nx, ny, nz);
        }

        poseStack.popPose();
    }

    /**
     * Render chest armor piece (body + arms).
     * Body uses vanilla position with Mo'Bends rotation.
     * Arms are split at elbow joint for proper bending.
     */
    private void renderChest(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            HumanoidModel<?> model,
            BipedEntityData<?> entityData,
            int packedLight,
            int packedOverlay,
            float entityScale,
            boolean isSlimArms)
    {
        poseStack.pushPose();

        // Apply baby scale if needed (babies render at 0.5 scale)
        if (entityScale != 1.0f)
        {
            poseStack.scale(entityScale, entityScale, entityScale);
        }

        // Render body using vertex capture with pivot rotation (matching 3.X approach)
        // Note: Global transforms are already applied by MutatedRenderer.beforeRender()
        if (model.body != null && model.body.visible)
        {
            renderBodyWithPivotRotation(poseStack, vertexConsumer, model.body, entityData, packedLight, packedOverlay);
        }

        // Render left arm (split at elbow joint)
        renderSplitArm(poseStack, vertexConsumer, model, entityData, true, packedLight, packedOverlay, entityScale, isSlimArms);

        // Render right arm (split at elbow joint)
        renderSplitArm(poseStack, vertexConsumer, model, entityData, false, packedLight, packedOverlay, entityScale, isSlimArms);

        poseStack.popPose();
    }

    /**
     * Render leg armor piece (body skirt + upper legs).
     * Body waist uses vanilla position with Mo'Bends rotation.
     * Legs are split at knee joint for proper bending.
     */
    private void renderLegs(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            HumanoidModel<?> model,
            BipedEntityData<?> entityData,
            int packedLight,
            int packedOverlay,
            float entityScale)
    {
        poseStack.pushPose();

        // Apply baby scale if needed (babies render at 0.5 scale)
        if (entityScale != 1.0f)
        {
            poseStack.scale(entityScale, entityScale, entityScale);
        }

        // Render body (waist/skirt part of leggings) using vertex capture with pivot rotation
        // Note: Global transforms are already applied by MutatedRenderer.beforeRender()
        if (model.body != null && model.body.visible)
        {
            renderBodyWithPivotRotation(poseStack, vertexConsumer, model.body, entityData, packedLight, packedOverlay);
        }

        // Render left leg (split at knee joint)
        renderSplitLeg(poseStack, vertexConsumer, model, entityData, true, packedLight, packedOverlay, entityScale);

        // Render right leg (split at knee joint)
        renderSplitLeg(poseStack, vertexConsumer, model, entityData, false, packedLight, packedOverlay, entityScale);

        poseStack.popPose();
    }

    /**
     * Render feet armor piece (boots/lower legs).
     * Legs are split at knee joint for proper bending.
     */
    private void renderFeet(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            HumanoidModel<?> model,
            BipedEntityData<?> entityData,
            int packedLight,
            int packedOverlay,
            float entityScale)
    {
        poseStack.pushPose();

        // Apply baby scale if needed (babies render at 0.5 scale)
        if (entityScale != 1.0f)
        {
            poseStack.scale(entityScale, entityScale, entityScale);
        }

        // Render left leg (split at knee joint)
        renderSplitLeg(poseStack, vertexConsumer, model, entityData, true, packedLight, packedOverlay, entityScale);

        // Render right leg (split at knee joint)
        renderSplitLeg(poseStack, vertexConsumer, model, entityData, false, packedLight, packedOverlay, entityScale);

        poseStack.popPose();
    }

    // ========== SPLIT LIMB RENDERING ==========

    // Enable limb slicing for proper elbow/knee bending
    private static final boolean ENABLE_LIMB_SLICING = true;

    // Slim arm Y offset: standard arm Y is -10F, slim arm Y is -9.5F
    // The armor model always uses standard positions (-10F), but Mo'Bends animation
    // for slim players uses -9.5F. We need to offset the armor UP by 0.5 model units.
    private static final float SLIM_ARM_Y_OFFSET = 0.5f * ArmorPoseHelper.SCALE;  // 0.5 model units in render units

    /**
     * Render an arm split at the elbow joint.
     * Upper arm geometry follows upperArm transform, forearm geometry follows foreArm transform.
     */
    private void renderSplitArm(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            HumanoidModel<?> model,
            BipedEntityData<?> entityData,
            boolean isLeft,
            int packedLight,
            int packedOverlay,
            float entityScale,
            boolean isSlimArms)
    {
        ModelPart armPart = isLeft ? model.leftArm : model.rightArm;
        if (armPart == null || !armPart.visible)
        {
            return;
        }

        ModelPartTransform upperArm = isLeft ? entityData.leftArm : entityData.rightArm;

        if (!ENABLE_LIMB_SLICING)
        {
            // Simple mode: render whole arm with upper arm transform only
            // This means armor won't bend at elbow, but will render correctly
            // Note: Global transforms are already applied by MutatedRenderer.beforeRender()
            poseStack.pushPose();
            ArmorPoseHelper.applyPartTransform(poseStack, entityData.body, true);
            ArmorPoseHelper.applyPartTransform(poseStack, upperArm, true);
            // Apply slim arm Y offset correction
            if (isSlimArms)
            {
                poseStack.translate(0, -SLIM_ARM_Y_OFFSET, 0);
            }
            ArmorPoseHelper.renderPartAtOrigin(armPart, poseStack, vertexConsumer, packedLight, packedOverlay);
            poseStack.popPose();
            return;
        }

        // Advanced mode: split arm at elbow joint
        ModelPartTransform foreArm = isLeft ? entityData.leftForeArm : entityData.rightForeArm;
        JointPlane elbowPlane = JointDefinitions.getElbow(isLeft);

        // 1. Capture arm geometry at rest pose using IDENTITY PoseStack
        // Critical: We must use a fresh PoseStack so captured vertices are in model-local space
        // The incoming poseStack already has entity transforms that would corrupt the capture
        limbCapture.clear();
        PoseStack captureStack = new PoseStack();  // Identity matrix - no transforms
        resetPartToOrigin(armPart);
        armPart.render(captureStack, limbCapture, packedLight, packedOverlay);
        restorePartFromCapture(armPart);

        List<CapturedVertex> vertices = limbCapture.getVertices();
        if (vertices.isEmpty())
        {
            return;
        }

        // 2. Group vertices into quads and slice at elbow
        List<CapturedVertex[]> quads = ArmorPoseHelper.groupIntoQuads(vertices);
        List<SliceResult> sliceResults = quadSlicer.sliceAll(quads, elbowPlane);

        // Slim arm Y offset for captured vertices (applied to output, not capture)
        float slimArmOffset = isSlimArms ? -SLIM_ARM_Y_OFFSET : 0;

        // 3. Render upper arm portion with upper arm transform
        // Upper portion vertices stay as-is (no offset needed except for slim arms)
        // Note: Global transforms are already applied by MutatedRenderer.beforeRender()
        poseStack.pushPose();
        ArmorPoseHelper.applyPartTransform(poseStack, entityData.body, true);
        ArmorPoseHelper.applyPartTransform(poseStack, upperArm, true);
        // Apply slim arm offset to Y
        ArmorPoseHelper.renderSlicedVertices(poseStack, vertexConsumer, sliceResults, true, 0, slimArmOffset, 0, packedLight, packedOverlay, currentArmorColor);
        poseStack.popPose();

        // 4. Render forearm portion with forearm transform (chained to upper arm)
        // Lower portion vertices need to be offset by forearm's position to be in forearm-local-space
        // foreArm.position is (0, 4, 2) in model units, so we offset by (-0, -0.25, -0.125) in render units
        // Note: Global transforms are already applied by MutatedRenderer.beforeRender()
        float foreArmOffsetX = -foreArm.position.x * ArmorPoseHelper.SCALE;
        float foreArmOffsetY = -foreArm.position.y * ArmorPoseHelper.SCALE + slimArmOffset;  // Include slim arm offset
        float foreArmOffsetZ = -foreArm.position.z * ArmorPoseHelper.SCALE;
        poseStack.pushPose();
        ArmorPoseHelper.applyPartTransform(poseStack, entityData.body, true);
        ArmorPoseHelper.applyPartTransform(poseStack, upperArm, true);
        ArmorPoseHelper.applyPartTransform(poseStack, foreArm, true);
        ArmorPoseHelper.renderSlicedVertices(poseStack, vertexConsumer, sliceResults, false, foreArmOffsetX, foreArmOffsetY, foreArmOffsetZ, packedLight, packedOverlay, currentArmorColor);
        poseStack.popPose();
    }

    /**
     * Render a leg split at the knee joint.
     * Upper leg geometry follows leg transform, lower leg geometry follows foreLeg transform.
     */
    private void renderSplitLeg(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            HumanoidModel<?> model,
            BipedEntityData<?> entityData,
            boolean isLeft,
            int packedLight,
            int packedOverlay,
            float entityScale)
    {
        ModelPart legPart = isLeft ? model.leftLeg : model.rightLeg;
        if (legPart == null || !legPart.visible)
        {
            return;
        }

        ModelPartTransform upperLeg = isLeft ? entityData.leftLeg : entityData.rightLeg;

        // Get vanilla leg X position for correct horizontal placement
        float vanillaLegX = legPart.x;

        if (!ENABLE_LIMB_SLICING)
        {
            // Simple mode: render whole leg with upper leg transform only
            // This means armor won't bend at knee, but will render correctly
            // Note: Global transforms are already applied by MutatedRenderer.beforeRender()
            poseStack.pushPose();
            ArmorPoseHelper.applyLegTransform(poseStack, upperLeg, vanillaLegX);
            ArmorPoseHelper.renderPartAtOrigin(legPart, poseStack, vertexConsumer, packedLight, packedOverlay);
            poseStack.popPose();
            return;
        }

        // Advanced mode: split leg at knee joint
        ModelPartTransform lowerLeg = isLeft ? entityData.leftForeLeg : entityData.rightForeLeg;
        JointPlane kneePlane = JointDefinitions.getKnee(isLeft);

        // 1. Capture leg geometry at rest pose using IDENTITY PoseStack
        // Critical: We must use a fresh PoseStack so captured vertices are in model-local space
        // The incoming poseStack already has entity transforms that would corrupt the capture
        limbCapture.clear();
        PoseStack captureStack = new PoseStack();  // Identity matrix - no transforms
        resetPartToOrigin(legPart);
        legPart.render(captureStack, limbCapture, packedLight, packedOverlay);
        restorePartFromCapture(legPart);

        List<CapturedVertex> vertices = limbCapture.getVertices();
        if (vertices.isEmpty())
        {
            return;
        }

        // 2. Group vertices into quads and slice at knee
        List<CapturedVertex[]> quads = ArmorPoseHelper.groupIntoQuads(vertices);
        List<SliceResult> sliceResults = quadSlicer.sliceAll(quads, kneePlane);

        // Use the vanilla armor model's leg X position (stored in capturedX from resetPartToOrigin)
        // This ensures leg armor renders at the correct horizontal position regardless of
        // what Mo'Bends entityData has. Legs are root parts and should use vanilla positioning.
        float vanillaLegXSliced = capturedX;

        // 3. Render upper leg portion with upper leg transform
        // Note: Legs are NOT parented to body in Mo'Bends
        // Note: Global transforms are already applied by MutatedRenderer.beforeRender()
        // Upper portion vertices stay as-is (no offset needed)
        poseStack.pushPose();
        ArmorPoseHelper.applyLegTransform(poseStack, upperLeg, vanillaLegXSliced);
        ArmorPoseHelper.renderSlicedVertices(poseStack, vertexConsumer, sliceResults, true, 0, 0, 0, packedLight, packedOverlay, currentArmorColor);
        poseStack.popPose();

        // 4. Render lower leg portion with lower leg transform (chained to upper leg)
        // Lower portion vertices need to be offset by lowerLeg's position to be in lower-leg-local-space
        // lowerLeg.position is (0, 6, -2) in model units, so we offset by (-0, -0.375, 0.125) in render units
        // Note: Global transforms are already applied by MutatedRenderer.beforeRender()
        float lowerLegOffsetX = -lowerLeg.position.x * ArmorPoseHelper.SCALE;
        float lowerLegOffsetY = -lowerLeg.position.y * ArmorPoseHelper.SCALE;
        float lowerLegOffsetZ = -lowerLeg.position.z * ArmorPoseHelper.SCALE;
        poseStack.pushPose();
        ArmorPoseHelper.applyLegTransform(poseStack, upperLeg, vanillaLegXSliced);
        ArmorPoseHelper.applyPartTransform(poseStack, lowerLeg, true);
        ArmorPoseHelper.renderSlicedVertices(poseStack, vertexConsumer, sliceResults, false, lowerLegOffsetX, lowerLegOffsetY, lowerLegOffsetZ, packedLight, packedOverlay, currentArmorColor);
        poseStack.popPose();
    }

    // ========== HELPER METHODS FOR SPLIT RENDERING ==========

    // Storage for original part values during capture
    private float capturedX, capturedY, capturedZ;
    private float capturedXRot, capturedYRot, capturedZRot;

    /**
     * Reset a ModelPart to origin for geometry capture.
     * Stores original values for later restoration.
     */
    private void resetPartToOrigin(ModelPart part)
    {
        capturedX = part.x;
        capturedY = part.y;
        capturedZ = part.z;
        capturedXRot = part.xRot;
        capturedYRot = part.yRot;
        capturedZRot = part.zRot;

        part.x = 0;
        part.y = 0;
        part.z = 0;
        part.xRot = 0;
        part.yRot = 0;
        part.zRot = 0;
    }

    /**
     * Restore a ModelPart to its original values after capture.
     */
    private void restorePartFromCapture(ModelPart part)
    {
        part.x = capturedX;
        part.y = capturedY;
        part.z = capturedZ;
        part.xRot = capturedXRot;
        part.yRot = capturedYRot;
        part.zRot = capturedZRot;
    }

    /**
     * Render body armor using vertex capture with pivot rotation.
     * This matches the 3.X approach: capture geometry at origin, apply pivot transform, output vertices.
     *
     * Unlike renderPartWithVanillaPosition, this approach ensures the vanilla part position
     * doesn't interfere with our pivot rotation transform.
     *
     * @param poseStack The pose stack (with entity transforms already applied)
     * @param vertexConsumer The vertex consumer
     * @param part The body model part
     * @param entityData The entity's animation data
     * @param packedLight Light value
     * @param packedOverlay Overlay value
     */
    private void renderBodyWithPivotRotation(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            ModelPart part,
            BipedEntityData<?> entityData,
            int packedLight,
            int packedOverlay)
    {
        if (part == null || !part.visible)
        {
            return;
        }

        ModelPartTransform body = entityData.body;
        if (body == null)
        {
            return;
        }

        // 1. Capture geometry at rest pose using IDENTITY PoseStack
        limbCapture.clear();
        PoseStack captureStack = new PoseStack();  // Identity matrix
        resetPartToOrigin(part);
        part.render(captureStack, limbCapture, packedLight, packedOverlay);
        restorePartFromCapture(part);

        List<CapturedVertex> vertices = limbCapture.getVertices();
        if (vertices.isEmpty())
        {
            return;
        }

        // 2. Apply pivot rotation transform to PoseStack
        // NOTE: Entity-level globalOffset is already on parent PoseStack from MutatedRenderer.beforeRender()
        poseStack.pushPose();

        float scale = 1.0f / 16.0f;

        // Apply per-part globalOffset (before other transforms, without offsetScale)
        if (body.globalOffset.x != 0 || body.globalOffset.y != 0 || body.globalOffset.z != 0)
        {
            poseStack.translate(
                body.globalOffset.x * scale,
                body.globalOffset.y * scale,
                body.globalOffset.z * scale
            );
        }

        float offsetScale = body.offsetScale;

        // Translate to body pivot point (use offsetScale to match player transform exactly)
        poseStack.translate(
            body.position.x * scale * offsetScale,
            body.position.y * scale * offsetScale,
            body.position.z * scale * offsetScale
        );

        // Apply animation offset with offsetScale (bobbing, etc.)
        if (body.offset.x != 0 || body.offset.y != 0 || body.offset.z != 0)
        {
            poseStack.translate(
                body.offset.x * scale * offsetScale,
                body.offset.y * scale * offsetScale,
                body.offset.z * scale * offsetScale
            );
        }

        // Apply rotation (now around the correct pivot)
        GlHelper.rotate(poseStack, body.rotation.getSmooth());

        // Translate back from pivot so armor stays at vanilla position (use offsetScale to match)
        poseStack.translate(
            -body.position.x * scale * offsetScale,
            -body.position.y * scale * offsetScale,
            -body.position.z * scale * offsetScale
        );

        // 3. Transform and output vertices
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        // Extract color tint from currentArmorColor (packed ARGB)
        float tintR = ((currentArmorColor >> 16) & 0xFF) / 255.0F;
        float tintG = ((currentArmorColor >> 8) & 0xFF) / 255.0F;
        float tintB = (currentArmorColor & 0xFF) / 255.0F;
        float tintA = ((currentArmorColor >> 24) & 0xFF) / 255.0F;

        for (CapturedVertex v : vertices)
        {
            // Transform position by matrix
            float tx = matrix.m00() * v.x + matrix.m10() * v.y + matrix.m20() * v.z + matrix.m30();
            float ty = matrix.m01() * v.x + matrix.m11() * v.y + matrix.m21() * v.z + matrix.m31();
            float tz = matrix.m02() * v.x + matrix.m12() * v.y + matrix.m22() * v.z + matrix.m32();

            // Transform normal by normal matrix
            float nx = normal.m00() * v.normalX + normal.m10() * v.normalY + normal.m20() * v.normalZ;
            float ny = normal.m01() * v.normalX + normal.m11() * v.normalY + normal.m21() * v.normalZ;
            float nz = normal.m02() * v.normalX + normal.m12() * v.normalY + normal.m22() * v.normalZ;

            // Pack tinted RGBA into single int for 26.2
            int color = ((int)(v.alpha * tintA * 255.0F) << 24) |
                        ((int)(v.red * tintR * 255.0F) << 16) |
                        ((int)(v.green * tintG * 255.0F) << 8) |
                        (int)(v.blue * tintB * 255.0F);
            vertexConsumer.addVertex(tx, ty, tz)
                    .setColor(color)
                    .setUv(v.u, v.v)
                    .setOverlay(packedOverlay)
                    .setLight(packedLight)
                    .setNormal(nx, ny, nz);
        }

        poseStack.popPose();
    }

    /**
     * Configure model part visibility based on equipment slot.
     */
    private void configureVisibility(HumanoidModel<?> model, EquipmentSlot slot)
    {
        model.head.visible = false;
        model.hat.visible = false;
        model.body.visible = false;
        model.rightArm.visible = false;
        model.leftArm.visible = false;
        model.rightLeg.visible = false;
        model.leftLeg.visible = false;

        switch (slot)
        {
            case HEAD:
                model.head.visible = true;
                model.hat.visible = true;
                break;
            case CHEST:
                model.body.visible = true;
                model.rightArm.visible = true;
                model.leftArm.visible = true;
                break;
            case LEGS:
                model.body.visible = true;
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
                break;
            case FEET:
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
                break;
        }
    }

    /**
     * Render without animation (vanilla passthrough).
     */
    private <E extends LivingEntity> void renderVanilla(
            ArmorRenderContext<E> context,
            HumanoidModel<?> model,
            Identifier texture,
            Function<Identifier, RenderType> renderTypeProvider)
    {
        RenderType renderType = renderTypeProvider.apply(texture);
        SubmitNodeCollector collector = context.getSubmitNodeCollector();
        if (collector == null)
        {
            return;
        }

        collector.submitCustomGeometry(context.getPoseStack(), renderType, (pose, vertexConsumer) ->
        {
            PoseStack submittedPoseStack = new PoseStack();
            submittedPoseStack.last().set(pose);
            submittedPoseStack.pushPose();
            configureVisibility(model, context.getSlot());
            model.renderToBuffer(
                    submittedPoseStack,
                    vertexConsumer,
                    context.getPackedLight(),
                    context.getPackedOverlay(),
                    0xFFFFFFFF
            );
            submittedPoseStack.popPose();
        });
    }

    /**
     * Get the transform injector.
     */
    public TransformInjector getTransformInjector()
    {
        return transformInjector;
    }

    /**
     * Get the split limb renderer.
     */
    public SplitLimbRenderer getSplitLimbRenderer()
    {
        return splitLimbRenderer;
    }

    /**
     * Get the Tier 2 fallback renderer.
     */
    public Tier2Renderer getTier2Fallback()
    {
        return tier2Fallback;
    }

    /**
     * Get the total number of render calls.
     */
    public long getRenderCount()
    {
        return renderCount;
    }

    /**
     * Get the number of renders that fell back to Tier 2.
     */
    public long getFallbackCount()
    {
        return fallbackCount;
    }

    /**
     * Get the fallback rate.
     */
    public float getFallbackRate()
    {
        return renderCount > 0 ? (float) fallbackCount / renderCount : 0;
    }

    /**
     * Reset statistics.
     */
    public void resetStats()
    {
        renderCount = 0;
        fallbackCount = 0;
    }

    /**
     * Get debug statistics.
     */
    public String getStats()
    {
        return String.format("Tier1Renderer: %d renders, %d fallbacks (%.1f%% fallback rate)",
                renderCount, fallbackCount, getFallbackRate() * 100);
    }

}
