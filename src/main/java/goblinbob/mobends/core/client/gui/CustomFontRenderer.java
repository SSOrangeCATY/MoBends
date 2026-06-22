package goblinbob.mobends.core.client.gui;

public class CustomFontRenderer
{
    protected CustomFont font;
    protected int characterSpacing = 1;

    public void setFont(CustomFont font)
    {
        this.font = font;
    }

    public int getTextWidth(String textToDraw)
    {
        if (this.font == null || textToDraw == null)
            return 0;

        int width = 0;
        for (int i = 0; i < textToDraw.length(); ++i)
        {
            CustomFont.Symbol symbol = this.font.getSymbol(textToDraw.charAt(i));
            width += symbol == null ? 2 : symbol.width;

            if (i != textToDraw.length() - 1)
                width += characterSpacing;
        }
        return width;
    }

    public void drawText(String textToDraw, int x, int y)
    {
    }

    public void drawCenteredText(String textToDraw, int x, int y)
    {
        int width = this.getTextWidth(textToDraw);
        this.drawText(textToDraw, x - width / 2, y);
    }
}
