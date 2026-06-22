package goblinbob.mobends.core.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import goblinbob.mobends.core.util.IColorRead;
import net.minecraft.resources.Identifier;

public class Mesh
{
    private final VertexFormat vertexFormat;
    private Object drawMode;

    public Mesh(VertexFormat vertexFormat, int maxVertices)
    {
        this.vertexFormat = vertexFormat;
    }

    public Mesh(int maxVertices)
    {
        this(DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, maxVertices);
    }

    public void beginDrawing(Object mode)
    {
        this.drawMode = mode;
    }

    public void beginDrawing()
    {
        this.beginDrawing(null);
    }

    public void finishDrawing()
    {
    }

    public Mesh pos(double x, double y, double z)
    {
        return this;
    }

    public Mesh normal(float x, float y, float z)
    {
        return this;
    }

    public Mesh tex(double u, double v)
    {
        return this;
    }

    public Mesh color(IColorRead color)
    {
        return this;
    }

    public void endVertex()
    {
    }

    public void display()
    {
    }

    public void render(PoseStack poseStack, Object bufferSource, Identifier texture, int packedLight)
    {
    }

    public VertexFormat getVertexFormat()
    {
        return this.vertexFormat;
    }

    public Object getDrawMode()
    {
        return this.drawMode;
    }
}
