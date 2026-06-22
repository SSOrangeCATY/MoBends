package goblinbob.mobends.standard.client.renderer.entity;

import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.math.vector.Vec3f;
import goblinbob.mobends.core.math.vector.VectorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;

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

    public void render(double x, double y, double z, float partialTicks)
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

        renderNodes(partialTicks);
    }

    public void resetNodes()
    {
        for (int i = 0; i < MAX_LENGTH; i++)
            this.nodes[i] = new TrailNode(trackedArrow);
    }

    public void renderNodes(float partialTicks)
    {
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
