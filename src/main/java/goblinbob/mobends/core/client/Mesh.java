package goblinbob.mobends.core.client;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import goblinbob.mobends.core.util.IColorRead;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class Mesh
{

	private VertexFormat vertexFormat;
	private PrimitiveTopology drawMode;
	private final List<Vertex> vertices = new ArrayList<>();

	public Mesh(VertexFormat vertexFormat, int maxVertices)
	{
		this.vertexFormat = vertexFormat;
	}

	/**
	 * Legacy constructor for backward compatibility.
	 * Uses POSITION_TEX_COLOR_NORMAL format.
	 */
	public Mesh(int maxVertices)
	{
		this(DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, maxVertices);
	}

	public void beginDrawing(PrimitiveTopology mode)
	{
		this.drawMode = mode;
		this.vertices.clear();
	}

	/**
	 * Legacy beginDrawing for backward compatibility.
	 * Uses QUADS mode by default.
	 */
	public void beginDrawing()
	{
		this.beginDrawing(PrimitiveTopology.TRIANGLES);
	}

	public void finishDrawing()
	{
		// Geometry is submitted later through SubmitNodeCollector.
	}

	// Temporary vertex data for building
	private float pendingX, pendingY, pendingZ;
	private float pendingU, pendingV;
	private float pendingR = 1.0f, pendingG = 1.0f, pendingB = 1.0f, pendingA = 1.0f;
	private float pendingNX, pendingNY, pendingNZ;

	public Mesh pos(double x, double y, double z)
	{
		this.pendingX = (float) x;
		this.pendingY = (float) y;
		this.pendingZ = (float) z;
		return this;
	}

	public Mesh normal(float x, float y, float z)
	{
		this.pendingNX = x;
		this.pendingNY = y;
		this.pendingNZ = z;
		return this;
	}

	public Mesh tex(double u, double v)
	{
		this.pendingU = (float) u;
		this.pendingV = (float) v;
		return this;
	}

	public Mesh color(IColorRead color)
	{
		this.pendingR = color.getR();
		this.pendingG = color.getG();
		this.pendingB = color.getB();
		this.pendingA = color.getA();
		return this;
	}

	public void endVertex()
	{
		this.vertices.add(new Vertex(
				pendingX, pendingY, pendingZ,
				pendingU, pendingV,
				pendingR, pendingG, pendingB, pendingA,
				pendingNX, pendingNY, pendingNZ
		));

		// Reset to defaults
		pendingR = pendingG = pendingB = 1.0f;
		pendingA = 1.0f;
		pendingNX = pendingNY = pendingNZ = 0.0f;
	}

	public void display()
	{
		// In 26.2, drawing is done automatically via finishDrawing()
		// This method is kept for compatibility but mesh should use finishDrawing() instead
	}

	/**
	 * Submit the mesh through Minecraft 26.2's retained render pipeline.
	 */
	public void render(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, Identifier texture, int packedLight)
	{
		if (this.vertices.isEmpty())
		{
			return;
		}

		submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(texture), (pose, buffer) ->
		{
			for (Vertex vertex : this.vertices)
			{
				vertex.write(buffer, pose, packedLight);
			}
		});
	}

	private record Vertex(float x, float y, float z,
	                      float u, float v,
	                      float r, float g, float b, float a,
	                      float normalX, float normalY, float normalZ)
	{
		void write(VertexConsumer buffer, PoseStack.Pose pose, int packedLight)
		{
			int color = ((int)(a * 255.0F) << 24)
					| ((int)(r * 255.0F) << 16)
					| ((int)(g * 255.0F) << 8)
					| (int)(b * 255.0F);

			buffer.addVertex(pose, x, y, z)
					.setColor(color)
					.setUv(u, v)
					.setLight(packedLight)
					.setNormal(pose, normalX, normalY, normalZ);
		}
	}

}
