package goblinbob.mobends.standard.client.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ArmorDebugRenderer
{
    private static final List<CapturedVertex> capturedVertices = new ArrayList<>();
    private static long lastCaptureTime = 0;
    private static String armorSlotInfo = "No armor captured";

    public static void storeCapturedVertices(List<CapturedVertex> vertices, BipedEntityData<?> data)
    {
        capturedVertices.clear();
        if (vertices != null)
        {
            capturedVertices.addAll(vertices);
        }
        lastCaptureTime = System.currentTimeMillis();
        armorSlotInfo = data == null ? "No entity data" : "Captured";
    }

    public static int getCapturedVertexCount()
    {
        return capturedVertices.size();
    }

    public static String getArmorSlotInfo()
    {
        return armorSlotInfo;
    }

    public static String getCaptureStatus()
    {
        if (capturedVertices.isEmpty())
        {
            return "No vertices captured";
        }
        long age = System.currentTimeMillis() - lastCaptureTime;
        return "Captured " + capturedVertices.size() + " vertices (" + age + "ms ago)";
    }

    public static void renderDebug(
            PoseStack poseStack,
            BipedEntityData<?> data,
            boolean showOriginalVertices,
            boolean showTransformedVertices,
            boolean showBoneRegions,
            boolean showBoneAxes,
            boolean showTestGeometry,
            boolean boneEnabled)
    {
    }

    public static void clearCapturedData()
    {
        capturedVertices.clear();
        armorSlotInfo = "No armor captured";
        lastCaptureTime = 0;
    }

    public static class CapturedVertex
    {
        public final float x;
        public final float y;
        public final float z;
        public final int color;

        public CapturedVertex(float x, float y, float z, int color)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.color = color;
        }
    }
}
