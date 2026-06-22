package goblinbob.mobends.core.client.gui.settingswindow;

import com.mojang.blaze3d.systems.RenderSystem;
import goblinbob.mobends.core.Core;
import goblinbob.mobends.core.bender.EntityBender;
import goblinbob.mobends.core.bender.EntityBenderRegistry;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.client.gui.GuiBendsMenu;
import goblinbob.mobends.core.client.gui.elements.GuiCompactTextField;
import goblinbob.mobends.core.util.Draw;
import goblinbob.mobends.core.util.GuiHelper;
import goblinbob.mobends.standard.main.ModStatics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class GuiSettingsWindow extends Screen
{

    public static final Identifier BACKGROUND_TEXTURE = Identifier.fromNamespaceAndPath(ModStatics.MODID,
            "textures/gui/pack_window.png");
    public static final int EDITOR_WIDTH = 280;
    public static final int EDITOR_HEIGHT = 177;

    private int x, y;

    private GuiCompactTextField filterQueryInput;
    private final GuiBenderList bendsSettingsListUI = new GuiBenderList(0, 0, EDITOR_WIDTH - 10, EDITOR_HEIGHT - 10 - 20);

    private final EntityBenderRegistry.Filter filter = new EntityBenderRegistry.Filter();

    public GuiSettingsWindow()
    {
        super(Component.translatable("mobends.gui.settings"));

        fetchBenders();
    }

    @Override
    protected void init()
    {
        super.init();

        this.x = (this.width - EDITOR_WIDTH) / 2;
        this.y = (this.height - EDITOR_HEIGHT) / 2;

        clearWidgets();
        addRenderableWidget(Button.builder(Component.translatable("mobends.gui.back"), button -> goBack())
                .bounds(10, height - 30, 60, 20)
                .build());
        filterQueryInput = new GuiCompactTextField(this.font, x + 6, y + 6, 150, 16);
        filterQueryInput.setFocused(true);
        filterQueryInput.setPlaceholderText(I18n.get("mobends.gui.search"));
        addRenderableWidget(filterQueryInput);
        bendsSettingsListUI.initGui(this.x + 9, this.y + 9 + 20);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTicks)
    {
        this.extractBackground(GuiGraphicsExtractor, mouseX, mouseY, partialTicks);
        // Container
        Draw.borderBox(x + 4, y + 4, EDITOR_WIDTH, EDITOR_HEIGHT, 4, 36, 126);
        // Title background
        Draw.texturedModalRect(x, y - 13, 101, 0, 4, 16);
        Draw.texturedModalRect(x + 4, y - 13, EDITOR_WIDTH - 16, 16, 105, 0, 1, 16);
        Draw.texturedModalRect(x + EDITOR_WIDTH - 17, y - 13, 106, 0, 19, 16);

        bendsSettingsListUI.draw(GuiGraphicsExtractor, DataUpdateHandler.partialTicks);

        GuiHelper.drawString(GuiGraphicsExtractor, font, I18n.get("mobends.gui.settings"), this.x + 6, this.y - 9, 0xffffff, true);

        super.extractRenderState(GuiGraphicsExtractor, mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick()
    {
        super.tick();

        if (this.minecraft == null) return;

        final double mouseX = this.minecraft.mouseHandler.xpos() * (double)this.width / (double)this.minecraft.getWindow().getScreenWidth();
        final double mouseY = this.minecraft.mouseHandler.ypos() * (double)this.height / (double)this.minecraft.getWindow().getScreenHeight();

        bendsSettingsListUI.update((int)mouseX, (int)mouseY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubled)
    {
        if (super.mouseClicked(event, doubled)) return true;

        bendsSettingsListUI.handleMouseClicked((int)event.x(), (int)event.y(), event.button());
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event)
    {
        super.mouseReleased(event);

        bendsSettingsListUI.handleMouseReleased((int)event.x(), (int)event.y(), event.button());
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY)
    {
        if (bendsSettingsListUI.handleMouseScroll(mouseX, mouseY, scrollY))
        {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(KeyEvent event)
    {
        if (filterQueryInput.isFocused() && filterQueryInput.keyPressed(event))
        {
            checkFilterChanged();
            return true;
        }

        if (event.key() == 256)
        {
            Core.saveConfiguration();
            GuiHelper.closeGui();
            return true;
        }

        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event)
    {
        if (filterQueryInput.isFocused() && filterQueryInput.charTyped(event))
        {
            checkFilterChanged();
            return true;
        }
        return super.charTyped(event);
    }

    private void checkFilterChanged()
    {
        if (!filterQueryInput.getValue().equals(filter.query))
        {
            filter.query = filterQueryInput.getValue();
            fetchBenders();
        }
    }

    private void goBack()
    {
        Core.saveConfiguration();
        this.minecraft.setScreenAndShow(new GuiBendsMenu());
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    public void fetchBenders()
    {
        bendsSettingsListUI.clearElements();
        for (final EntityBender<?> bender : EntityBenderRegistry.instance.getRegistered(filter))
        {
            bendsSettingsListUI.addElement(new GuiBenderSettings(bender));
        }
    }

}
