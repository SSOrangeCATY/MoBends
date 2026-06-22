package goblinbob.mobends.core.client.gui.elements;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public class GuiScrollPanelContainer extends GuiScrollPanel
{

    public GuiScrollPanelContainer(GuiElement parent, int x, int y, int width, int height, IGuiElement element)
    {
        super(parent, x, y, width, height);
        this.children.add(element);
    }

    @Override
    protected void drawContent(GuiGraphicsExtractor GuiGraphicsExtractor, float partialTicks)
    {

    }

    @Override
    protected void drawBackground(GuiGraphicsExtractor GuiGraphicsExtractor, float partialTicks)
    {

    }

}
