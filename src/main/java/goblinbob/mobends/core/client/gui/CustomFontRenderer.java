package goblinbob.mobends.core.client.gui;

import goblinbob.mobends.core.util.Draw;

public class CustomFontRenderer
{

    protected CustomFont font;
    protected int characterSpacing = 1;

    public void setFont(CustomFont font)
    {
        this.font = font;
    }

    protected void drawSymbol(CustomFont.Symbol symbol, int x, int y)
    {
        if (symbol == null)
            symbol = new CustomFont.Symbol(10, 10, 5, 5, 0, 0);

        x += symbol.offsetX;
        y += symbol.offsetY;
        int width = symbol.width;
        int height = symbol.height;
        float textureX = (float) symbol.u / this.font.atlasWidth;
        float textureY = (float) symbol.v / this.font.atlasHeight;
        float textureWidth = (float) width / this.font.atlasWidth;
        float textureHeight = (float) height / this.font.atlasHeight;

        Draw.texturedRectangle(x, y - height, width, height, textureX, textureY, textureWidth, textureHeight);
    }

    public int getTextWidth(String textToDraw)
    {
        int width = 0;
        for (int i = 0; i < textToDraw.length(); ++i)
        {
            CustomFont.Symbol symbol = this.font.getSymbol(textToDraw.charAt(i));

            if (symbol == null)
                width += 2;
            else
                width += symbol.width;

            if (i != textToDraw.length() - 1)
                width += characterSpacing;
        }
        return width;
    }

    public void drawText(String textToDraw, int x, int y)
    {
        if (this.font == null)
            return;

        Draw.bindTexture(this.font.identifier);
        int nextCharX = x;
        for (int i = 0; i < textToDraw.length(); ++i)
        {
            CustomFont.Symbol symbol = this.font.getSymbol(textToDraw.charAt(i));

            if (symbol == null)
                symbol = new CustomFont.Symbol(10, 10, 5, 5, 0, 0);

            this.drawSymbol(symbol, nextCharX, y);
            nextCharX += symbol.width + characterSpacing;
        }
    }

    public void drawCenteredText(String textToDraw, int x, int y)
    {
        int width = this.getTextWidth(textToDraw);
        this.drawText(textToDraw, x - width / 2, y);
    }

}
