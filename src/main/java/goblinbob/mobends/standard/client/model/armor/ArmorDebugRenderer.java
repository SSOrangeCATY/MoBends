package goblinbob.mobends.standard.client.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import goblinbob.mobends.core.client.model.IModelPart;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Debug renderer for armor visualization.
 * Shows original vertices, transformed vertices, bone regions, and bone axes.
 */
public class ArmorDebugRenderer
{
    private static final float SCALE = 1.0f / 16.0f;

    // Cached capture data for debugging
    private static List<CapturedVertex> lastCapturedVertices = null;
    private static BipedEntityData<?> lastEntityData = null;

    // Cached armor models for capturing
    private static HumanoidModel<HumanoidRenderState> innerArmorModel = null;
    private static HumanoidModel<HumanoidRenderState> outerArmorModel = null;

    /**
     * Store captured vertices for debug rendering.
     */
    public static void storeCapturedVertices(List<CapturedVertex> vertices, BipedEntityData<?> data)
    {
        lastCapturedVertices = vertices;
        lastEntityData = data;
    }

    /**
     * Get the number of captured vertices for debug display.
     */
    public static int getCapturedVertexCount()
    {
        return lastCapturedVertices != null ? lastCapturedVertices.size() : 0;
    }

    /**
     * Get armor slot info for debug display.
     */
    public static String getArmorSlotInfo()
    {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return "No player";

        StringBuilder sb = new StringBuilder();
        sb.append("H:").append(hasArmor(player, EquipmentSlot.HEAD) ? "Y" : "N");
        sb.append(" C:").append(hasArmor(player, EquipmentSlot.CHEST) ? "Y" : "N");
        sb.append(" L:").append(hasArmor(player, EquipmentSlot.LEGS) ? "Y" : "N");
        sb.append(" F:").append(hasArmor(player, EquipmentSlot.FEET) ? "Y" : "N");
        return sb.toString();
    }

    /**
     * Get detailed capture status for debugging.
     */
    public static String getCaptureStatus()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Models: ").append(innerArmorModel != null && outerArmorModel != null ? "OK" : "NOT INIT");
        if (lastCapturedVertices != null)
        {
            sb.append(" | Last: ").append(lastCapturedVertices.size()).append("v");
        }
        else
        {
            sb.append(" | Last: null");
        }
        return sb.toString();
    }

    private static boolean hasArmor(Player player, EquipmentSlot slot)
    {
        ItemStack stack = player.getItemBySlot(slot);
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        return equippable != null && equippable.assetId().isPresent() && equippable.slot() == slot;
    }

    /**
     * Initialize armor models for capturing.
     */
    @SuppressWarnings("unchecked")
    private static void initArmorModels()
    {
        if (innerArmorModel == null || outerArmorModel == null)
        {
            Minecraft mc = Minecraft.getInstance();
            ArmorModelSet<HumanoidModel<HumanoidRenderState>> models = ArmorModelSet.bake(
                    ModelLayers.PLAYER_ARMOR,
                    mc.getEntityModels(),
                    HumanoidModel::new);
            innerArmorModel = models.legs();
            outerArmorModel = models.chest();
        }
    }

    /**
     * Render debug visualization.
     */
    public static void renderDebug(
            PoseStack poseStack,
            BipedEntityData<?> data,
            boolean showOriginal,
            boolean showTransformed,
            boolean showBoneRegions,
            boolean showBoneAxes,
            boolean showTestGeometry,
            Map<BoneRegion, Boolean> boneEnabled)
    {
        renderDebug(poseStack, data, showOriginal, showTransformed, showBoneRegions, showBoneAxes,
                showTestGeometry, boneEnabled, ArmorDebugRenderer::noopLine);
    }

    /**
     * Extract debug line geometry. Used by retained GUI rendering in 26.2.
     */
    public static void extractDebugLines(
            PoseStack poseStack,
            BipedEntityData<?> data,
            boolean showOriginal,
            boolean showTransformed,
            boolean showBoneRegions,
            boolean showBoneAxes,
            boolean showTestGeometry,
            Map<BoneRegion, Boolean> boneEnabled,
            LineSink lineSink)
    {
        renderDebug(poseStack, data, showOriginal, showTransformed, showBoneRegions, showBoneAxes,
                showTestGeometry, boneEnabled, lineSink);
    }

    private static void renderDebug(
            PoseStack poseStack,
            BipedEntityData<?> data,
            boolean showOriginal,
            boolean showTransformed,
            boolean showBoneRegions,
            boolean showBoneAxes,
            boolean showTestGeometry,
            Map<BoneRegion, Boolean> boneEnabled,
            LineSink lineSink)
    {
        // Capture fresh vertices if we have armor
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player != null)
        {
            capturePlayerArmor(player, data);
        }

        // Always render origin marker for reference
        renderOriginMarker(poseStack, lineSink);

        if (showBoneRegions)
        {
            renderBoneRegions(poseStack, boneEnabled, lineSink);
        }

        if (showBoneAxes)
        {
            renderBoneAxes(poseStack, data, boneEnabled, lineSink);
        }

        // Show test geometry (simple cubes at known positions) to verify rendering
        if (showTestGeometry)
        {
            renderTestGeometry(poseStack, lineSink);
        }

        if (lastCapturedVertices != null && !lastCapturedVertices.isEmpty())
        {
            if (showOriginal)
            {
                renderOriginalVertices(poseStack, lastCapturedVertices, boneEnabled, lineSink);
            }

            if (showTransformed)
            {
                renderTransformedVertices(poseStack, lastCapturedVertices, data, boneEnabled, lineSink);
            }
        }
    }

    /**
     * Render test geometry - simple cubes at vanilla armor model positions.
     * This verifies the rendering pipeline without depending on armor capture.
     */
    private static void renderTestGeometry(PoseStack poseStack, LineSink lineSink)
    {
        Matrix4f matrix = poseStack.last().pose();

        // Draw wireframe cubes matching vanilla armor model positions (in model units, scaled by SCALE)
        // These are the ACTUAL vanilla HumanoidModel cube positions

        // Body: cube at (-4, 0, -2) size (8, 12, 4)
        drawWireBox(lineSink, matrix, -4, 0, -2, 4, 12, 2, 1.0f, 1.0f, 0.0f, 1.0f);

        // Head: cube at (-4, -8, -4) size (8, 8, 8)
        drawWireBox(lineSink, matrix, -4, -8, -4, 4, 0, 4, 0.0f, 1.0f, 1.0f, 1.0f);

        // Left arm: pivot (5, 2, 0), cube (-1, -2, -2) size (4, 12, 4) ->world (4, 0, -2) to (8, 12, 2)
        drawWireBox(lineSink, matrix, 4, 0, -2, 8, 12, 2, 1.0f, 0.5f, 0.0f, 1.0f);

        // Right arm: pivot (-5, 2, 0), cube (-3, -2, -2) size (4, 12, 4) ->world (-8, 0, -2) to (-4, 12, 2)
        drawWireBox(lineSink, matrix, -8, 0, -2, -4, 12, 2, 1.0f, 0.5f, 0.0f, 1.0f);

        // Left leg: pivot (1.9, 12, 0), cube (-2, 0, -2) size (4, 12, 4) ->world (-0.1, 12, -2) to (3.9, 24, 2)
        drawWireBox(lineSink, matrix, -0.1f, 12, -2, 3.9f, 24, 2, 0.5f, 0.0f, 1.0f, 1.0f);

        // Right leg: pivot (-1.9, 12, 0), cube (-2, 0, -2) size (4, 12, 4) ->world (-3.9, 12, -2) to (0.1, 24, 2)
        drawWireBox(lineSink, matrix, -3.9f, 12, -2, 0.1f, 24, 2, 0.5f, 0.0f, 1.0f, 1.0f);
    }

    /**
     * Draw a wireframe box (coordinates in model units, will be scaled).
     */
    private static void drawWireBox(LineSink lineSink, Matrix4f matrix,
                                     float minX, float minY, float minZ,
                                     float maxX, float maxY, float maxZ,
                                     float r, float g, float b, float a)
    {
        // Scale to render units
        minX *= SCALE;
        minY *= SCALE;
        minZ *= SCALE;
        maxX *= SCALE;
        maxY *= SCALE;
        maxZ *= SCALE;

        // Bottom face
        addLine(lineSink, matrix, minX, minY, minZ, maxX, minY, minZ, r, g, b, a);
        addLine(lineSink, matrix, maxX, minY, minZ, maxX, minY, maxZ, r, g, b, a);
        addLine(lineSink, matrix, maxX, minY, maxZ, minX, minY, maxZ, r, g, b, a);
        addLine(lineSink, matrix, minX, minY, maxZ, minX, minY, minZ, r, g, b, a);

        // Top face
        addLine(lineSink, matrix, minX, maxY, minZ, maxX, maxY, minZ, r, g, b, a);
        addLine(lineSink, matrix, maxX, maxY, minZ, maxX, maxY, maxZ, r, g, b, a);
        addLine(lineSink, matrix, maxX, maxY, maxZ, minX, maxY, maxZ, r, g, b, a);
        addLine(lineSink, matrix, minX, maxY, maxZ, minX, maxY, minZ, r, g, b, a);

        // Vertical edges
        addLine(lineSink, matrix, minX, minY, minZ, minX, maxY, minZ, r, g, b, a);
        addLine(lineSink, matrix, maxX, minY, minZ, maxX, maxY, minZ, r, g, b, a);
        addLine(lineSink, matrix, maxX, minY, maxZ, maxX, maxY, maxZ, r, g, b, a);
        addLine(lineSink, matrix, minX, minY, maxZ, minX, maxY, maxZ, r, g, b, a);
    }

    /**
     * Render origin marker (XYZ axes at origin) to show coordinate system.
     */
    private static void renderOriginMarker(PoseStack poseStack, LineSink lineSink)
    {
        Matrix4f matrix = poseStack.last().pose();

        float len = 0.5f; // Length in render units

        // X axis (red) - points right
        addLine(lineSink, matrix, 0, 0, 0, len, 0, 0, 1, 0, 0, 1);
        // Y axis (green) - points down in model space
        addLine(lineSink, matrix, 0, 0, 0, 0, len, 0, 0, 1, 0, 1);
        // Z axis (blue) - points forward
        addLine(lineSink, matrix, 0, 0, 0, 0, 0, len, 0, 0, 1, 1);
    }

    /**
     * Capture actual armor vertices from player's equipped armor.
     */
    private static void capturePlayerArmor(Player player, BipedEntityData<?> data)
    {
        initArmorModels();

        CapturingVertexConsumer captureConsumer = new CapturingVertexConsumer();
        captureConsumer.clear();

        // Check if player has any armor
        boolean hasAnyArmor = hasArmor(player, EquipmentSlot.HEAD) ||
                              hasArmor(player, EquipmentSlot.CHEST) ||
                              hasArmor(player, EquipmentSlot.LEGS) ||
                              hasArmor(player, EquipmentSlot.FEET);

        if (hasAnyArmor)
        {
            // Capture actual armor slots
            captureArmorSlot(player, EquipmentSlot.HEAD, captureConsumer);
            captureArmorSlot(player, EquipmentSlot.CHEST, captureConsumer);
            captureArmorSlot(player, EquipmentSlot.LEGS, captureConsumer);
            captureArmorSlot(player, EquipmentSlot.FEET, captureConsumer);
        }
        else
        {
            // No armor equipped - capture base armor model geometry for debugging
            captureBaseArmorModel(captureConsumer);
        }

        List<CapturedVertex> vertices = captureConsumer.getVertices();
        if (!vertices.isEmpty())
        {
            lastCapturedVertices = new ArrayList<>(vertices);
            lastEntityData = data;
        }
    }

    /**
     * Capture the base armor model geometry without requiring actual armor.
     * Used for debugging the capture pipeline.
     */
    private static void captureBaseArmorModel(CapturingVertexConsumer captureConsumer)
    {
        if (outerArmorModel == null) return;

        // Make all parts visible
        setAllVisible(outerArmorModel, true);

        // Reset to rest pose
        resetToRestPose(outerArmorModel);

        // Render to capture
        PoseStack capturePoseStack = new PoseStack();
        outerArmorModel.renderToBuffer(capturePoseStack, captureConsumer, 15728880,
                                       OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
    }

    /**
     * Capture vertices for a single armor slot.
     */
    private static void captureArmorSlot(Player player, EquipmentSlot slot, CapturingVertexConsumer captureConsumer)
    {
        ItemStack itemStack = player.getItemBySlot(slot);
        if (itemStack.isEmpty()) return;
        Equippable equippable = itemStack.get(DataComponents.EQUIPPABLE);
        if (equippable == null || equippable.assetId().isEmpty() || equippable.slot() != slot) return;

        // Select inner or outer model based on slot
        boolean usesInnerModel = (slot == EquipmentSlot.LEGS);
        HumanoidModel<HumanoidRenderState> armorModel = usesInnerModel ? innerArmorModel : outerArmorModel;
        if (armorModel == null) return;

        // Set up model visibility for this slot
        setAllVisible(armorModel, false);
        switch (slot)
        {
            case HEAD:
                armorModel.head.visible = true;
                armorModel.hat.visible = true;
                break;
            case CHEST:
                armorModel.body.visible = true;
                armorModel.rightArm.visible = true;
                armorModel.leftArm.visible = true;
                break;
            case LEGS:
                armorModel.body.visible = true;
                armorModel.rightLeg.visible = true;
                armorModel.leftLeg.visible = true;
                break;
            case FEET:
                armorModel.rightLeg.visible = true;
                armorModel.leftLeg.visible = true;
                break;
            default:
                break;
        }

        // Reset model to rest pose (no rotations)
        resetToRestPose(armorModel);

        // Render to capture consumer with identity transform
        PoseStack capturePoseStack = new PoseStack();
        armorModel.renderToBuffer(capturePoseStack, captureConsumer, 15728880, OverlayTexture.NO_OVERLAY,
                                  0xFFFFFFFF);
    }

    /**
     * Reset armor model to rest pose (no rotations).
     */
    private static void resetToRestPose(HumanoidModel<?> model)
    {
        resetPart(model.head);
        resetPart(model.hat);
        resetPart(model.body);
        resetPart(model.leftArm);
        resetPart(model.rightArm);
        resetPart(model.leftLeg);
        resetPart(model.rightLeg);
    }

    private static void setAllVisible(HumanoidModel<?> model, boolean visible)
    {
        model.head.visible = visible;
        model.hat.visible = visible;
        model.body.visible = visible;
        model.leftArm.visible = visible;
        model.rightArm.visible = visible;
        model.leftLeg.visible = visible;
        model.rightLeg.visible = visible;
    }

    private static void resetPart(ModelPart part)
    {
        part.xRot = 0;
        part.yRot = 0;
        part.zRot = 0;
        // Keep the default positions (x, y, z) as they define the part's origin
    }

    /**
     * Render original captured vertices as green points/lines.
     */
    private static void renderOriginalVertices(PoseStack poseStack, List<CapturedVertex> vertices,
                                                Map<BoneRegion, Boolean> boneEnabled,
                                                LineSink lineSink)
    {
        ArmorBoneAssignment assignment = new ArmorBoneAssignment();

        Matrix4f matrix = poseStack.last().pose();

        for (int i = 0; i < vertices.size(); i += 4)
        {
            if (i + 3 >= vertices.size()) break;

            CapturedVertex v0 = vertices.get(i);
            CapturedVertex v1 = vertices.get(i + 1);
            CapturedVertex v2 = vertices.get(i + 2);
            CapturedVertex v3 = vertices.get(i + 3);

            // Check if bone is enabled
            BoneRegion region = assignment.assignVertex(v0.x, v0.y, v0.z);
            if (!boneEnabled.getOrDefault(region, true)) continue;

            // Green color for original
            float r = 0.0f, g = 1.0f, b = 0.0f, a = 1.0f;

            // Draw quad edges
            addLine(lineSink, matrix, v0.x, v0.y, v0.z, v1.x, v1.y, v1.z, r, g, b, a);
            addLine(lineSink, matrix, v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, r, g, b, a);
            addLine(lineSink, matrix, v2.x, v2.y, v2.z, v3.x, v3.y, v3.z, r, g, b, a);
            addLine(lineSink, matrix, v3.x, v3.y, v3.z, v0.x, v0.y, v0.z, r, g, b, a);
        }
    }

    /**
     * Render transformed vertices as red points/lines.
     */
    private static void renderTransformedVertices(PoseStack poseStack, List<CapturedVertex> vertices,
                                                   BipedEntityData<?> data,
                                                   Map<BoneRegion, Boolean> boneEnabled,
                                                   LineSink lineSink)
    {
        ArmorBoneAssignment assignment = new ArmorBoneAssignment();

        // Compute transforms and rest positions
        java.util.Map<BoneRegion, float[]> transforms = computeTransforms(poseStack, data);
        java.util.Map<BoneRegion, float[]> restPositions = computeRestPositions(data);

        Matrix4f matrix = poseStack.last().pose();

        for (int i = 0; i < vertices.size(); i += 4)
        {
            if (i + 3 >= vertices.size()) break;

            CapturedVertex v0 = vertices.get(i);
            CapturedVertex v1 = vertices.get(i + 1);
            CapturedVertex v2 = vertices.get(i + 2);
            CapturedVertex v3 = vertices.get(i + 3);

            BoneRegion region = assignment.assignVertex(v0.x, v0.y, v0.z);
            if (!boneEnabled.getOrDefault(region, true)) continue;

            float[] transform = transforms.get(region);
            float[] restPos = restPositions.get(region);
            if (transform == null || restPos == null) continue;

            // Transform vertices
            float[] t0 = transformVertex(v0, transform, restPos);
            float[] t1 = transformVertex(v1, transform, restPos);
            float[] t2 = transformVertex(v2, transform, restPos);
            float[] t3 = transformVertex(v3, transform, restPos);

            // Red color for transformed
            float r = 1.0f, g = 0.0f, b = 0.0f, a = 1.0f;

            // Draw quad edges
            addLine(lineSink, matrix, t0[0], t0[1], t0[2], t1[0], t1[1], t1[2], r, g, b, a);
            addLine(lineSink, matrix, t1[0], t1[1], t1[2], t2[0], t2[1], t2[2], r, g, b, a);
            addLine(lineSink, matrix, t2[0], t2[1], t2[2], t3[0], t3[1], t3[2], r, g, b, a);
            addLine(lineSink, matrix, t3[0], t3[1], t3[2], t0[0], t0[1], t0[2], r, g, b, a);
        }
    }

    private static float[] transformVertex(CapturedVertex v, float[] transform, float[] restPos)
    {
        // Bone-local coordinates
        float localX = v.x - restPos[0];
        float localY = v.y - restPos[1];
        float localZ = v.z - restPos[2];

        // Transform by bone matrix (4x3 row-major stored as flat array)
        float tx = transform[0] * localX + transform[3] * localY + transform[6] * localZ + transform[9];
        float ty = transform[1] * localX + transform[4] * localY + transform[7] * localZ + transform[10];
        float tz = transform[2] * localX + transform[5] * localY + transform[8] * localZ + transform[11];

        return new float[]{tx, ty, tz};
    }

    /**
     * Render bone region bounding boxes matching vanilla HumanoidModel coordinates.
     * Coordinates are in model units (will be scaled by SCALE in drawBox).
     *
     * Vanilla HumanoidModel positions:
     * - body: pivot (0,0,0), cube (-4,0,-2) to (4,12,2)
     * - head: pivot (0,0,0), cube (-4,-8,-4) to (4,0,4)
     * - leftArm: pivot (5,2,0), cube (-1,-2,-2) to (3,10,2) ->world (4,0,-2) to (8,12,2)
     * - rightArm: pivot (-5,2,0), cube (-3,-2,-2) to (1,10,2) ->world (-8,0,-2) to (-4,12,2)
     * - leftLeg: pivot (1.9,12,0), cube (-2,0,-2) to (2,12,2) ->world (-0.1,12,-2) to (3.9,24,2)
     * - rightLeg: pivot (-1.9,12,0), cube (-2,0,-2) to (2,12,2) ->world (-3.9,12,-2) to (0.1,24,2)
     */
    private static void renderBoneRegions(PoseStack poseStack, Map<BoneRegion, Boolean> boneEnabled, LineSink lineSink)
    {
        Matrix4f matrix = poseStack.last().pose();

        float a = 0.8f;

        // Head region: X[-4,4], Y[-8,0], Z[-4,4]
        if (boneEnabled.getOrDefault(BoneRegion.HEAD, true))
            drawBox(lineSink, matrix, -4, -8, -4, 4, 0, 4, 0.5f, 0.5f, 1.0f, a);

        // Body region: X[-4,4], Y[0,12], Z[-2,2]
        if (boneEnabled.getOrDefault(BoneRegion.BODY, true))
            drawBox(lineSink, matrix, -4, 0, -2, 4, 12, 2, 1.0f, 0.5f, 0.0f, a);

        // Left arm upper: X[4,8], Y[0,6], Z[-2,2]
        if (boneEnabled.getOrDefault(BoneRegion.LEFT_ARM_UPPER, true))
            drawBox(lineSink, matrix, 4, 0, -2, 8, 6, 2, 0.0f, 1.0f, 0.5f, a);
        // Left arm lower: X[4,8], Y[6,12], Z[-2,2]
        if (boneEnabled.getOrDefault(BoneRegion.LEFT_ARM_LOWER, true))
            drawBox(lineSink, matrix, 4, 6, -2, 8, 12, 2, 0.0f, 0.7f, 0.3f, a);

        // Right arm upper: X[-8,-4], Y[0,6], Z[-2,2]
        if (boneEnabled.getOrDefault(BoneRegion.RIGHT_ARM_UPPER, true))
            drawBox(lineSink, matrix, -8, 0, -2, -4, 6, 2, 1.0f, 1.0f, 0.0f, a);
        // Right arm lower: X[-8,-4], Y[6,12], Z[-2,2]
        if (boneEnabled.getOrDefault(BoneRegion.RIGHT_ARM_LOWER, true))
            drawBox(lineSink, matrix, -8, 6, -2, -4, 12, 2, 0.7f, 0.7f, 0.0f, a);

        // Left leg upper: X[-0.1,3.9], Y[12,18], Z[-2,2]
        if (boneEnabled.getOrDefault(BoneRegion.LEFT_LEG_UPPER, true))
            drawBox(lineSink, matrix, -0.1f, 12, -2, 3.9f, 18, 2, 1.0f, 0.0f, 1.0f, a);
        // Left leg lower: X[-0.1,3.9], Y[18,24], Z[-2,2]
        if (boneEnabled.getOrDefault(BoneRegion.LEFT_LEG_LOWER, true))
            drawBox(lineSink, matrix, -0.1f, 18, -2, 3.9f, 24, 2, 0.7f, 0.0f, 0.7f, a);

        // Right leg upper: X[-3.9,0.1], Y[12,18], Z[-2,2]
        if (boneEnabled.getOrDefault(BoneRegion.RIGHT_LEG_UPPER, true))
            drawBox(lineSink, matrix, -3.9f, 12, -2, 0.1f, 18, 2, 0.0f, 1.0f, 1.0f, a);
        // Right leg lower: X[-3.9,0.1], Y[18,24], Z[-2,2]
        if (boneEnabled.getOrDefault(BoneRegion.RIGHT_LEG_LOWER, true))
            drawBox(lineSink, matrix, -3.9f, 18, -2, 0.1f, 24, 2, 0.0f, 0.7f, 0.7f, a);
    }

    private static void drawBox(LineSink lineSink, Matrix4f matrix,
                                 float minX, float minY, float minZ,
                                 float maxX, float maxY, float maxZ,
                                 float r, float g, float b, float a)
    {
        // Scale to render units
        minX *= SCALE;
        minY *= SCALE;
        minZ *= SCALE;
        maxX *= SCALE;
        maxY *= SCALE;
        maxZ *= SCALE;

        // Bottom face
        addLine(lineSink, matrix, minX, minY, minZ, maxX, minY, minZ, r, g, b, a);
        addLine(lineSink, matrix, maxX, minY, minZ, maxX, minY, maxZ, r, g, b, a);
        addLine(lineSink, matrix, maxX, minY, maxZ, minX, minY, maxZ, r, g, b, a);
        addLine(lineSink, matrix, minX, minY, maxZ, minX, minY, minZ, r, g, b, a);

        // Top face
        addLine(lineSink, matrix, minX, maxY, minZ, maxX, maxY, minZ, r, g, b, a);
        addLine(lineSink, matrix, maxX, maxY, minZ, maxX, maxY, maxZ, r, g, b, a);
        addLine(lineSink, matrix, maxX, maxY, maxZ, minX, maxY, maxZ, r, g, b, a);
        addLine(lineSink, matrix, minX, maxY, maxZ, minX, maxY, minZ, r, g, b, a);

        // Vertical edges
        addLine(lineSink, matrix, minX, minY, minZ, minX, maxY, minZ, r, g, b, a);
        addLine(lineSink, matrix, maxX, minY, minZ, maxX, maxY, minZ, r, g, b, a);
        addLine(lineSink, matrix, maxX, minY, maxZ, maxX, maxY, maxZ, r, g, b, a);
        addLine(lineSink, matrix, minX, minY, maxZ, minX, maxY, maxZ, r, g, b, a);
    }

    /**
     * Render bone axes to show bone orientations.
     */
    private static void renderBoneAxes(PoseStack poseStack, BipedEntityData<?> data,
                                        Map<BoneRegion, Boolean> boneEnabled,
                                        LineSink lineSink)
    {
        float axisLength = 0.15f;

        for (BoneRegion region : BoneRegion.values())
        {
            if (!boneEnabled.getOrDefault(region, true)) continue;

            IModelPart part = getBoneModelPart(region, data);
            if (part == null && region != BoneRegion.ROOT) continue;

            poseStack.pushPose();

            if (part != null)
            {
                part.applyCharacterTransform(poseStack, SCALE);
            }

            Matrix4f matrix = poseStack.last().pose();

            // X axis (red)
            addLine(lineSink, matrix, 0, 0, 0, axisLength, 0, 0, 1, 0, 0, 1);
            // Y axis (green)
            addLine(lineSink, matrix, 0, 0, 0, 0, axisLength, 0, 0, 1, 0, 1);
            // Z axis (blue)
            addLine(lineSink, matrix, 0, 0, 0, 0, 0, axisLength, 0, 0, 1, 1);

            poseStack.popPose();
        }
    }

    private static void addLine(LineSink lineSink, Matrix4f matrix,
                                 float x1, float y1, float z1,
                                 float x2, float y2, float z2,
                                 float r, float g, float b, float a)
    {
        // Pack RGBA into single int for 26.2
        int color = ((int)(a * 255.0F) << 24) | ((int)(r * 255.0F) << 16) | ((int)(g * 255.0F) << 8) | (int)(b * 255.0F);
        lineSink.add(matrix, x1, y1, z1, x2, y2, z2, color);
    }

    private static void noopLine(Matrix4f matrix, float x1, float y1, float z1,
                                 float x2, float y2, float z2, int color)
    {
    }

    @FunctionalInterface
    public interface LineSink
    {
        void add(Matrix4f matrix, float x1, float y1, float z1, float x2, float y2, float z2, int color);
    }

    private static IModelPart getBoneModelPart(BoneRegion region, BipedEntityData<?> data)
    {
        switch (region)
        {
            case HEAD: return data.head;
            case BODY: return data.body;
            case LEFT_ARM_UPPER: return data.leftArm;
            case LEFT_ARM_LOWER: return data.leftForeArm;
            case RIGHT_ARM_UPPER: return data.rightArm;
            case RIGHT_ARM_LOWER: return data.rightForeArm;
            case LEFT_LEG_UPPER: return data.leftLeg;
            case LEFT_LEG_LOWER: return data.leftForeLeg;
            case RIGHT_LEG_UPPER: return data.rightLeg;
            case RIGHT_LEG_LOWER: return data.rightForeLeg;
            case ROOT:
            default: return null;
        }
    }

    private static java.util.Map<BoneRegion, float[]> computeTransforms(PoseStack poseStack, BipedEntityData<?> data)
    {
        java.util.Map<BoneRegion, float[]> transforms = new java.util.EnumMap<>(BoneRegion.class);

        for (BoneRegion region : BoneRegion.values())
        {
            IModelPart part = getBoneModelPart(region, data);
            if (part == null && region != BoneRegion.ROOT) continue;

            poseStack.pushPose();

            if (part != null)
            {
                part.applyCharacterTransform(poseStack, SCALE);
            }

            Matrix4f matrix = poseStack.last().pose();

            // Store as flat array [m00,m01,m02, m10,m11,m12, m20,m21,m22, m30,m31,m32]
            float[] t = new float[12];
            t[0] = matrix.m00(); t[1] = matrix.m01(); t[2] = matrix.m02();
            t[3] = matrix.m10(); t[4] = matrix.m11(); t[5] = matrix.m12();
            t[6] = matrix.m20(); t[7] = matrix.m21(); t[8] = matrix.m22();
            t[9] = matrix.m30(); t[10] = matrix.m31(); t[11] = matrix.m32();

            transforms.put(region, t);

            poseStack.popPose();
        }

        return transforms;
    }

    /**
     * Compute rest positions using VANILLA HumanoidModel positions.
     * This must match RigidArmorRenderer.computeRestPosePositions exactly.
     */
    private static java.util.Map<BoneRegion, float[]> computeRestPositions(BipedEntityData<?> data)
    {
        java.util.Map<BoneRegion, float[]> positions = new java.util.EnumMap<>(BoneRegion.class);

        // Use vanilla HumanoidModel positions (in render units = model units * SCALE)
        positions.put(BoneRegion.HEAD, new float[]{0, 0, 0});
        positions.put(BoneRegion.BODY, new float[]{0, 0, 0});

        // Left arm: pivot at (5, 2, 0) in model units
        positions.put(BoneRegion.LEFT_ARM_UPPER, new float[]{5 * SCALE, 2 * SCALE, 0});
        positions.put(BoneRegion.LEFT_ARM_LOWER, new float[]{5 * SCALE, 8 * SCALE, 0});

        // Right arm: pivot at (-5, 2, 0) in model units
        positions.put(BoneRegion.RIGHT_ARM_UPPER, new float[]{-5 * SCALE, 2 * SCALE, 0});
        positions.put(BoneRegion.RIGHT_ARM_LOWER, new float[]{-5 * SCALE, 8 * SCALE, 0});

        // Left leg: pivot at (1.9, 12, 0) in model units
        positions.put(BoneRegion.LEFT_LEG_UPPER, new float[]{1.9f * SCALE, 12 * SCALE, 0});
        positions.put(BoneRegion.LEFT_LEG_LOWER, new float[]{1.9f * SCALE, 18 * SCALE, 0});

        // Right leg: pivot at (-1.9, 12, 0) in model units
        positions.put(BoneRegion.RIGHT_LEG_UPPER, new float[]{-1.9f * SCALE, 12 * SCALE, 0});
        positions.put(BoneRegion.RIGHT_LEG_LOWER, new float[]{-1.9f * SCALE, 18 * SCALE, 0});

        // ROOT at origin
        positions.put(BoneRegion.ROOT, new float[]{0, 0, 0});

        return positions;
    }
}
