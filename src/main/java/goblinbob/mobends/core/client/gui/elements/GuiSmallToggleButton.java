package goblinbob.mobends.core.client.gui.elements;

import goblinbob.mobends.core.util.GuiHelper;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public class GuiSmallToggleButton
{

    protected static final Identifier BUTTON_TEXTURES = Identifier.parse("textures/gui/widgets.png");

    private static final int WIDTH = 30;
    private static final int HEIGHT = 20;

    protected int x;
    protected int y;
    protected boolean hovered;
    protected boolean enabled;
    protected boolean toggleState;

    public GuiSmallToggleButton()
    {
        this.x = 0;
        this.y = 0;
        this.enabled = true;
        this.hovered = false;
        this.toggleState = false;
    }

    public void initGui(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public void update(int mouseX, int mouseY)
    {
        this.hovered = mouseX >= x && mouseX <= x + WIDTH &&
                mouseY >= y && mouseY <= y + HEIGHT;
    }

    public void draw(GuiGraphicsExtractor GuiGraphicsExtractor)
    {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        int k = this.hovered ? 1 : 0;

        if (this.toggleState)
        {
        }
        else
        {
        }

        GuiHelper.blit(GuiGraphicsExtractor, BUTTON_TEXTURES, this.x, this.y, 0, 66 + k * 20, WIDTH / 2, HEIGHT);
        GuiHelper.blit(GuiGraphicsExtractor, BUTTON_TEXTURES, this.x + WIDTH / 2, this.y, 200 - WIDTH / 2, 66 + k * 20, WIDTH / 2, HEIGHT);

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
        GuiHelper.drawString(GuiGraphicsExtractor, font, stateText, this.x - textWidth/2 + WIDTH /2, this.y + (HEIGHT - 8) / 2, l, false);
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
