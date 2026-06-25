package goblinbob.mobends.core.client.gui.popup;

import goblinbob.mobends.core.client.gui.elements.GuiCompactTextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.resources.language.I18n;

public class GuiPopUpCreatePack extends GuiPopUp
{

    protected GuiCompactTextField titleTextField;

    public GuiPopUpCreatePack()
    {
        super(I18n.get("mobends.gui.createpack"), new ButtonProps[] {
            new ButtonProps("Cancel", () -> {}),
            new ButtonProps("Create", () -> {})
        });
        this.titleTextField = new GuiCompactTextField(Minecraft.getInstance().font, 190, 16);
    }

    public void initGui(int x, int y)
    {
        super.initGui(x, y);
        titleTextField.setPosition(this.x + 5, this.y + 39);
    }

    public void display(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks)
    {
        super.display(guiGraphics, mouseX, mouseY, partialTicks);
        titleTextField.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
    }

    public void update(int mouseX, int mouseY)
    {
        super.update(mouseX, mouseY);
        // Note: tick() method removed in 26.2, cursor animation handled internally
    }

    public void mouseClicked(int mouseX, int mouseY, int button)
    {
        titleTextField.mouseClicked(new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(button, 0)), false);
        super.mouseClicked(mouseX, mouseY, button);
    }

    public void keyTyped(char typedChar, int keyCode)
    {
        // In 26.2, key handling is done differently via keyPressed/charTyped methods
        // This method is kept for compatibility but may need to be replaced with:
        // titleTextField.keyPressed(keyCode, scanCode, modifiers) or titleTextField.charTyped(typedChar, modifiers)
    }

}
