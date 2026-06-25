package goblinbob.mobends.standard.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.math.vector.Vec3f;
import goblinbob.mobends.core.math.vector.VectorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.phys.Vec3;

public class ArrowTrail
{
    public static final int MAX_LENGTH = 10;
    public static final float SPAWN_INTERVAL = 1;

    private final Minecraft mc;
    private AbstractArrow trackedArrow;
    private TrailNode[] nodes;
    private float spawnCooldown = 0;

    public ArrowTrail(AbstractArrow arrow)
    {
        this.mc = Minecraft.getInstance();
        this.trackedArrow = arrow;
        this.spawnCooldown = SPAWN_INTERVAL;
        this.nodes = new TrailNode[MAX_LENGTH];

        resetNodes();
    }

    public void onRenderTick()
    {
        spawnCooldown += DataUpdateHandler.ticksPerFrame;
    }

    public void render(SubmitNodeCollector submitNodeCollector, Vec3 viewPos)
    {
        advanceNodes();
        renderNodes(submitNodeCollector, viewPos);
    }

    public void render(double x, double y, double z, float partialTicks)
    {
        advanceNodes();
    }

    public void resetNodes()
    {
        for (int i = 0; i < MAX_LENGTH; i++)
            this.nodes[i] = new TrailNode(trackedArrow);
    }

    private void advanceNodes()
    {
        if (this.spawnCooldown > 40)
        {
            this.spawnCooldown = 0;
            resetNodes();
        }

        while (this.spawnCooldown >= SPAWN_INTERVAL)
        {
            for (int i = MAX_LENGTH - 1; i > 0; i--)
            {
                nodes[i].moveTo(nodes[i - 1]);
            }
            nodes[0].moveTo(trackedArrow);
            this.spawnCooldown -= SPAWN_INTERVAL;
        }
    }

    public void renderNodes(SubmitNodeCollector submitNodeCollector, Vec3 viewPos)
    {
        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        poseStack.translate(-viewPos.x, -viewPos.y, -viewPos.z);
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.debugQuads(), (pose, buffer) ->
                submitTrailVertices(pose, buffer));
        poseStack.popPose();
    }

    private void submitTrailVertices(PoseStack.Pose pose, VertexConsumer vertexBuffer)
    {
        final int color = 0x80FFFFFF;

        for (int i = 1; i < MAX_LENGTH; i++)
        {
            TrailNode node0 = nodes[i - 1];
            TrailNode node1 = nodes[i];

            float scale0 = ((float) (MAX_LENGTH - i)) / MAX_LENGTH * .1F;
            float scale1 = ((float) MAX_LENGTH - i - 1.0f) / MAX_LENGTH * .1F;
            if (i == 1)
            {
                scale1 = 0;
            }
            final Vec3f up0 = node0.up;
            final Vec3f right0 = node0.right;
            final Vec3f up1 = node1.up;
            final Vec3f right1 = node1.right;

            addVertex(vertexBuffer, pose, node0, -right0.x, -right0.y, -right0.z, scale0, color);
            addVertex(vertexBuffer, pose, node0, right0.x, right0.y, right0.z, scale0, color);
            addVertex(vertexBuffer, pose, node1, right1.x, right1.y, right1.z, scale1, color);
            addVertex(vertexBuffer, pose, node1, -right1.x, -right1.y, -right1.z, scale1, color);

            addVertex(vertexBuffer, pose, node0, -up0.x, -up0.y, -up0.z, scale0, color);
            addVertex(vertexBuffer, pose, node0, up0.x, up0.y, up0.z, scale0, color);
            addVertex(vertexBuffer, pose, node1, up1.x, up1.y, up1.z, scale1, color);
            addVertex(vertexBuffer, pose, node1, -up1.x, -up1.y, -up1.z, scale1, color);
        }
    }

    private static void addVertex(VertexConsumer vertexBuffer, PoseStack.Pose pose, TrailNode node,
            float offsetX, float offsetY, float offsetZ, float scale, int color)
    {
        vertexBuffer.addVertex(pose,
                (float) node.x + offsetX * scale,
                (float) node.y + offsetY * scale,
                (float) node.z + offsetZ * scale)
                .setColor(color);
    }

    public boolean shouldBeRemoved()
    {
        return mc.level == null || trackedArrow.isRemoved();
    }

    static class TrailNode
    {
        public double x;
        public double y;
        public double z;

        public final Vec3f up;
        public final Vec3f right;

        TrailNode(AbstractArrow arrow)
        {
            this.up = new Vec3f();
            this.right = new Vec3f();

            this.moveTo(arrow);
        }

        public void moveTo(TrailNode trailNode)
        {
            this.x = trailNode.x;
            this.y = trailNode.y;
            this.z = trailNode.z;
            this.up.set(trailNode.up);
            this.right.set(trailNode.right);
        }

        public void moveTo(AbstractArrow arrow)
        {
            this.x = arrow.getX();
            this.y = arrow.getY();
            this.z = arrow.getZ();

            final Vec3 forward = arrow.getForward();

            // Calculate up vector from pitch and yaw
            float pitch = arrow.getXRot();
            float yaw = arrow.getYRot();
            float upPitch = pitch + 90F;

            float f = Mth.cos(-yaw * ((float)Math.PI / 180F) - (float)Math.PI);
            float f1 = Mth.sin(-yaw * ((float)Math.PI / 180F) - (float)Math.PI);
            float f2 = -Mth.cos(-upPitch * ((float)Math.PI / 180F));
            float f3 = Mth.sin(-upPitch * ((float)Math.PI / 180F));
            Vec3 up = new Vec3(f1 * f2, f3, f * f2);

            this.up.set((float) -up.x, (float) -up.y, (float) up.z);

            VectorUtils.cross(
                    (float) -forward.x, (float) -forward.y, (float) forward.z,
                    this.up.x, this.up.y, this.up.z, this.right);
        }
    }
}
