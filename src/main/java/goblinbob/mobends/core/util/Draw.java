package goblinbob.mobends.core.util;

public class Draw
{
    public static void rectangle(int left, int top, int width, int height, int color)
    {
    }

    public static void rectangle(float left, float top, float width, float height)
    {
    }

    public static void rectangle(float x, float y, float w, float h, int color)
    {
    }

    public static void rectangleHorizontalGradient(float x, float y, float w, float h, IColorRead color0, IColorRead color1)
    {
    }

    public static void rectangleVerticalGradient(float x, float y, float w, float h, IColorRead color0, IColorRead color1)
    {
    }

    public static void rectangleHorizontalGradient(float x, float y, float width, float height, int color0, int color1)
    {
    }

    public static void rectangleVerticalGradient(float x, float y, float w, float h, int color0, int color1)
    {
    }

    public static void circle(double x, double y, double radius, int vertices)
    {
    }

    public static void circle(double x, double y, double radius)
    {
        circle(x, y, radius, 100);
    }

    public static void cube(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, IColorRead color)
    {
    }

    public static void texturedModalRect(int x, int y, int textureX, int textureY, int width, int height)
    {
    }

    public static void texturedModalRect(int x, int y, int width, int height, int textureX, int textureY, int textureWidth, int textureHeight)
    {
    }

    public static void texturedRectangle(int x, int y, int width, int height, float textureX, float textureY, float textureWidth, float textureHeight)
    {
    }

    public static void thsPuzzle(int x, int y, int left, int middle, int right, int height, int textureX, int textureY)
    {
        Draw.texturedModalRect(x, y, textureX, textureY, left, height);
        Draw.texturedModalRect(x + left, y, middle, height, textureX + left, textureY, 1, height);
        Draw.texturedModalRect(x + left + middle, y, textureX + left + 1, textureY, right, height);
    }

    public static void borderBox(int x, int y, int width, int height, int border, int textureX, int textureY)
    {
        Draw.texturedModalRect(x - border, y - border, textureX, textureY, border, border);
        Draw.texturedModalRect(x, y - border, width, border, textureX + border, textureY, 1, border);
        Draw.texturedModalRect(x + width, y - border, textureX + border + 1, textureY, border, border);
        Draw.texturedModalRect(x + width, y, border, height, textureX + border + 1, textureY + border, border, 1);
        Draw.texturedModalRect(x + width, y + height, textureX + border + 1, textureY + border + 1, border, border);
        Draw.texturedModalRect(x, y + height, width, border, textureX + border, textureY + border + 1, 1, border);
        Draw.texturedModalRect(x - border, y + height, textureX, textureY + border + 1, border, border);
        Draw.texturedModalRect(x - border, y, border, height, textureX, textureY + border, border, 1);
        Draw.texturedModalRect(x, y, width, height, textureX + border, textureY + border, 1, 1);
    }

    public static void line(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, IColorRead color)
    {
    }
}
