package goblinbob.mobends.core.client.gui.elements;

import goblinbob.mobends.core.util.GuiHelper;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public class GuiToggleButton
{

	protected static final Identifier BUTTON_TEXTURES = Identifier.parse("textures/gui/widgets.png");

    private static final int FLIPPER_WIDTH = 30;
    private static final int HEIGHT = 20;

    protected int x;
    protected int y;
    protected boolean hovered;
    protected boolean enabled;
    protected boolean toggleState;
    protected final String title;
    protected final int labelWidth;

    public GuiToggleButton(String title, int minLabelWidth)
    {
        this.x = 0;
        this.y = 0;
        this.enabled = true;
        this.hovered = false;
        this.toggleState = false;

        this.title = title;

        int titleWidth = Minecraft.getInstance().font.width(title) + 20;

        this.labelWidth = titleWidth > minLabelWidth ? titleWidth : minLabelWidth;
    }

    public void initGui(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public void update(int mouseX, int mouseY)
    {
        this.hovered = mouseX >= x && mouseX <= x + this.labelWidth + FLIPPER_WIDTH &&
                mouseY >= y && mouseY <= y + HEIGHT;
    }

    public void draw(GuiGraphicsExtractor GuiGraphicsExtractor)
    {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        int k = this.hovered ? 1 : 0;

        // Draw the label background
        GuiHelper.blit(GuiGraphicsExtractor, BUTTON_TEXTURES, this.x, this.y, 0, 66 + k * 20, (this.labelWidth + FLIPPER_WIDTH) / 2, HEIGHT);
        GuiHelper.blit(GuiGraphicsExtractor, BUTTON_TEXTURES, this.x + this.labelWidth / 2, this.y, 200 - (this.labelWidth + FLIPPER_WIDTH) / 2, 66 + k * 20, (this.labelWidth + FLIPPER_WIDTH) / 2, HEIGHT);

        // Draw the flipper (toggle part)
        if (this.toggleState)
        {
        }
        else
        {
        }
        GuiHelper.blit(GuiGraphicsExtractor, BUTTON_TEXTURES, this.x + this.labelWidth, this.y, 0, 66 + k * 20, FLIPPER_WIDTH / 2, HEIGHT);
        GuiHelper.blit(GuiGraphicsExtractor, BUTTON_TEXTURES, this.x + this.labelWidth + FLIPPER_WIDTH / 2, this.y, 200 - FLIPPER_WIDTH / 2, 66 + k * 20, FLIPPER_WIDTH / 2, HEIGHT);

        int l = 14737632;

        if (!this.enabled)
        {
            l = 10526880;
        }
        else if (this.hovered)
        {
            l = 16777120;
        }

        String stateText = this.toggleState ? "ON" : "OFF";
        int textWidth = font.width(stateText);
        GuiHelper.drawString(GuiGraphicsExtractor, font, stateText, this.x + this.labelWidth - textWidth/2 + FLIPPER_WIDTH/2, this.y + (HEIGHT - 8) / 2, l, false);

        GuiHelper.drawString(GuiGraphicsExtractor, font, this.title, this.x + 10, this.y + (HEIGHT - 8) / 2, l, false);
    }

    public void setToggleState(boolean state)
    {
    	this.toggleState = state;
    }

    public boolean mouseClicked(int mouseX, int mouseY, int button)
    {
    	if (hovered && button == 0)
    	{
	    	this.toggleState = !this.toggleState;
	        return true;
    	}

		return false;
    }

    public boolean getToggleState()
    {
    	return this.toggleState;
    }

}
