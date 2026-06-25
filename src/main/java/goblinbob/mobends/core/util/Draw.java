package goblinbob.mobends.core.util;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.BlitRenderState;
import net.minecraft.client.renderer.state.gui.ColoredRectangleRenderState;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.Nullable;

public class Draw
{
    private static @Nullable GuiGraphicsExtractor currentGraphics;
    private static @Nullable Identifier currentTexture;
    private static int currentColor = -1;

    public static void setGuiGraphics(GuiGraphicsExtractor guiGraphics)
    {
        currentGraphics = guiGraphics;
    }

    public static void setTexture(Identifier texture)
    {
        currentTexture = texture;
    }

    public static void bindTexture(Identifier texture)
    {
        setTexture(texture);
        resetColor();
    }

    public static void setColor(float red, float green, float blue, float alpha)
    {
        currentColor = ARGB.colorFromFloat(alpha, red, green, blue);
    }

    public static void resetColor()
    {
        currentColor = -1;
    }

    public static void text(Font font, String text, int x, int y, int color, boolean dropShadow)
    {
        if (currentGraphics != null)
            currentGraphics.text(font, text, x, y, color, dropShadow);
    }

    public static void centeredText(Font font, String text, int x, int y, int color)
    {
        if (currentGraphics != null)
            currentGraphics.centeredText(font, text, x, y, color);
    }

    public static void rectangle(int left, int top, int width, int height, int color)
    {
        rectangle((float) left, (float) top, (float) width, (float) height, color);
    }

    public static void rectangle(float left, float top, float width, float height)
    {
        rectangle(left, top, width, height, currentColor);
    }

    public static void rectangle(float x, float y, float w, float h, int color)
    {
        submit(new ColoredQuadRenderState(
                RenderPipelines.GUI,
                TextureSetup.noTexture(),
                pose(),
                x,
                y,
                x + w,
                y + h,
                color,
                color,
                scissor()
        ));
    }

    public static void rectangleHorizontalGradient(float x, float y, float w, float h, IColorRead color0, IColorRead color1)
    {
        rectangleHorizontalGradient(x, y, w, h, Color.asHex(color0), Color.asHex(color1));
    }

    public static void rectangleVerticalGradient(float x, float y, float w, float h, IColorRead color0, IColorRead color1)
    {
        rectangleVerticalGradient(x, y, w, h, Color.asHex(color0), Color.asHex(color1));
    }

    public static void rectangleHorizontalGradient(float x, float y, float width, float height, int color0, int color1)
    {
        submit(new HorizontalGradientRenderState(
                RenderPipelines.GUI,
                TextureSetup.noTexture(),
                pose(),
                x,
                y,
                x + width,
                y + height,
                color0,
                color1,
                scissor()
        ));
    }

    public static void rectangleVerticalGradient(float x, float y, float w, float h, int color0, int color1)
    {
        submit(new ColoredQuadRenderState(
                RenderPipelines.GUI,
                TextureSetup.noTexture(),
                pose(),
                x,
                y,
                x + w,
                y + h,
                color0,
                color1,
                scissor()
        ));
    }

    public static void circle(double x, double y, double radius, int vertices)
    {
        // Legacy helper was only used for immediate debug drawing. GUI extraction does not support line strips here.
    }

    public static void circle(double x, double y, double radius)
    {
        circle(x, y, radius, 100);
    }

	public static void cube(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, IColorRead color)
	{
        // 3D immediate debug drawing is outside the GUI extraction path.
	}

	public static void texturedModalRect(int x, int y, int textureX, int textureY, int width, int height)
    {
        texturedModalRect(x, y, width, height, textureX, textureY, width, height);
    }

	public static void texturedModalRect(int x, int y, int width, int height, int textureX, int textureY, int textureWidth, int textureHeight)
    {
        texture((float) x, (float) y, (float) width, (float) height,
                textureX / 256.0F, textureY / 256.0F,
                (textureX + textureWidth) / 256.0F, (textureY + textureHeight) / 256.0F);
    }

    public static void texturedRectangle(int x, int y, int width, int height, float textureX, float textureY, float textureWidth, float textureHeight)
    {
        texture((float) x, (float) y, (float) width, (float) height,
                textureX, textureY, textureX + textureWidth, textureY + textureHeight);
    }

    private static void texture(float x, float y, float width, float height, float u0, float v0, float u1, float v1)
    {
        if (currentTexture == null)
            return;

        final AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(currentTexture);
        submit(new BlitRenderState(
                RenderPipelines.GUI_TEXTURED,
                TextureSetup.singleTexture(texture.getTextureView(), texture.getSampler()),
                pose(),
                Math.round(x),
                Math.round(y),
                Math.round(x + width),
                Math.round(y + height),
                u0,
                u1,
                v0,
                v1,
                currentColor,
                scissor()
        ));
    }

    // Trimmed - HalfStretched
    public static void thsPuzzle(int x, int y, int left, int middle, int right, int height, int textureX, int textureY)
    {
        Draw.texturedModalRect(x, y, textureX, textureY, left, height);
        Draw.texturedModalRect(x + left, y, middle, height, textureX + left, textureY, 1, height);
        Draw.texturedModalRect(x + left + middle, y, textureX + left + 1, textureY, right, height);
    }

    public static void borderBox(int x, int y, int width, int height, int border, int textureX, int textureY)
    {
		/* Top-Left		*/ Draw.texturedModalRect(x-border, y-border, textureX, textureY, border, border);
		/* Top 			*/ Draw.texturedModalRect(x, y-border, width, border, textureX+border, textureY, 1, border);
		/* Top-Right 	*/ Draw.texturedModalRect(x+width, y-border, textureX+border+1, textureY, border, border);
		/* Right 		*/ Draw.texturedModalRect(x+width, y, border, height, textureX+border+1, textureY+border, border, 1);
		/* Bottom-Right */ Draw.texturedModalRect(x+width, y+height, textureX+border+1, textureY+border+1, border, border);
		/* Bottom		*/ Draw.texturedModalRect(x, y+height, width, border, textureX+border, textureY+border+1, 1, border);
		/* Bottom-Left	*/ Draw.texturedModalRect(x-border, y+height, textureX, textureY+border+1, border, border);
		/* Left 		*/ Draw.texturedModalRect(x-border, y, border, height, textureX, textureY+border, border, 1);
		/* Inside		*/ Draw.texturedModalRect(x, y, width, height, textureX+border, textureY+border, 1, 1);
	}

    public static void line(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, IColorRead color)
    {
        // 3D immediate debug drawing is outside the GUI extraction path.
    }

    private static Matrix3x2f pose()
    {
        return currentGraphics == null ? new Matrix3x2f() : new Matrix3x2f(currentGraphics.pose());
    }

    private static @Nullable ScreenRectangle scissor()
    {
        return currentGraphics == null ? null : currentGraphics.peekScissorStack();
    }

    private static void submit(GuiElementRenderState renderState)
    {
        if (currentGraphics != null)
            currentGraphics.submitGuiElementRenderState(renderState);
    }

    private record ColoredQuadRenderState(
            RenderPipeline pipeline,
            TextureSetup textureSetup,
            Matrix3x2fc pose,
            float x0,
            float y0,
            float x1,
            float y1,
            int col0,
            int col1,
            @Nullable ScreenRectangle scissorArea
    ) implements GuiElementRenderState
    {
        @Override
        public void buildVertices(VertexConsumer vertexConsumer)
        {
            vertexConsumer.addVertexWith2DPose(this.pose, this.x0, this.y0).setColor(this.col0);
            vertexConsumer.addVertexWith2DPose(this.pose, this.x0, this.y1).setColor(this.col1);
            vertexConsumer.addVertexWith2DPose(this.pose, this.x1, this.y1).setColor(this.col1);
            vertexConsumer.addVertexWith2DPose(this.pose, this.x1, this.y0).setColor(this.col0);
        }

        @Override
        public @Nullable ScreenRectangle bounds()
        {
            ScreenRectangle bounds = new ScreenRectangle(
                    Math.round(Math.min(this.x0, this.x1)),
                    Math.round(Math.min(this.y0, this.y1)),
                    Math.round(Math.abs(this.x1 - this.x0)),
                    Math.round(Math.abs(this.y1 - this.y0))
            ).transformMaxBounds(this.pose);
            return this.scissorArea == null ? bounds : this.scissorArea.intersection(bounds);
        }
    }

    private record HorizontalGradientRenderState(
            RenderPipeline pipeline,
            TextureSetup textureSetup,
            Matrix3x2fc pose,
            float x0,
            float y0,
            float x1,
            float y1,
            int col0,
            int col1,
            @Nullable ScreenRectangle scissorArea
    ) implements GuiElementRenderState
    {
        @Override
        public void buildVertices(VertexConsumer vertexConsumer)
        {
            vertexConsumer.addVertexWith2DPose(this.pose, this.x0, this.y0).setColor(this.col0);
            vertexConsumer.addVertexWith2DPose(this.pose, this.x0, this.y1).setColor(this.col0);
            vertexConsumer.addVertexWith2DPose(this.pose, this.x1, this.y1).setColor(this.col1);
            vertexConsumer.addVertexWith2DPose(this.pose, this.x1, this.y0).setColor(this.col1);
        }

        @Override
        public @Nullable ScreenRectangle bounds()
        {
            ScreenRectangle bounds = new ScreenRectangle(
                    Math.round(Math.min(this.x0, this.x1)),
                    Math.round(Math.min(this.y0, this.y1)),
                    Math.round(Math.abs(this.x1 - this.x0)),
                    Math.round(Math.abs(this.y1 - this.y0))
            ).transformMaxBounds(this.pose);
            return this.scissorArea == null ? bounds : this.scissorArea.intersection(bounds);
        }
    }
}
