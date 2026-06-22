package goblinbob.mobends.core.client.gui.packswindow;

import com.mojang.blaze3d.systems.RenderSystem;
import goblinbob.mobends.core.client.gui.GuiBendsMenu;
import goblinbob.mobends.core.pack.InvalidPackFormatException;
import goblinbob.mobends.core.pack.PackManager;
import goblinbob.mobends.core.util.Draw;
import goblinbob.mobends.core.util.ErrorReporter;
import goblinbob.mobends.core.util.GuiHelper;
import goblinbob.mobends.core.util.Timer;
import goblinbob.mobends.standard.main.ModStatics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class GuiPacksWindow extends Screen
{

    public static final Identifier BACKGROUND_TEXTURE = Identifier.fromNamespaceAndPath(ModStatics.MODID,
            "textures/gui/pack_window.png");
    public static final int EDITOR_WIDTH = 280;
    public static final int EDITOR_HEIGHT = 177;

    private int x;
    private int y;

    private final GuiTabNavigation tabNavigation;
    private final GuiPackTab localPacksTab;
    private final GuiPackTab publicPacksTab;

    private GuiLocalPacks localPacks;

    private Timer timer;

    public GuiPacksWindow()
    {
        super(Component.translatable("mobends.gui.packs"));

        this.localPacks = new GuiLocalPacks();
        this.tabNavigation = new GuiTabNavigation();
        this.localPacksTab = this.tabNavigation.addTab("mobends.gui.localpacks", 0);
        this.publicPacksTab = this.tabNavigation.addTab("mobends.gui.publicpacks", 1);
        this.tabNavigation.selectTab(0);

        this.timer = new Timer();

        // Initializing local packs from disk.
        try
        {
            PackManager.INSTANCE.initLocalPacks();
        }
        catch (InvalidPackFormatException e)
        {
            // Some of the packs were in an invalid format.
            e.printStackTrace();
            ErrorReporter.showErrorToPlayer(e);
        }
    }

    @Override
    public void onClose()
    {
        this.localPacks.dispose();
        super.onClose();
    }

    @Override
    protected void init()
    {
        super.init();

        this.x = (this.width - EDITOR_WIDTH) / 2;
        this.y = (this.height - EDITOR_HEIGHT) / 2;

        this.tabNavigation.initGui(this.x + 5, this.y);

        this.clearWidgets();
        this.addRenderableWidget(Button.builder(Component.translatable("mobends.gui.back"), button -> goBack())
                .bounds(10, height - 30, 60, 20)
                .build());
        this.localPacks.initGui(this.x, this.y);
    }

    @Override
    public void tick()
    {
        super.tick();

        if (this.minecraft == null) return;

        double mouseX = this.minecraft.mouseHandler.xpos() * (double)this.width / (double)this.minecraft.getWindow().getScreenWidth();
        double mouseY = this.minecraft.mouseHandler.ypos() * (double)this.height / (double)this.minecraft.getWindow().getScreenHeight();

        this.localPacks.update((int)mouseX, (int)mouseY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubled)
    {
        if (super.mouseClicked(event, doubled)) return true;

        this.tabNavigation.mouseClicked((int)event.x(), (int)event.y(), event.button());
        this.localPacks.mouseClicked((int)event.x(), (int)event.y(), event.button());
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event)
    {
        super.mouseReleased(event);

        this.localPacks.mouseReleased((int)event.x(), (int)event.y(), event.button());
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY)
    {
        if (this.localPacks.handleMouseScroll(mouseX, mouseY, scrollY))
        {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTicks)
    {
        this.extractBackground(GuiGraphicsExtractor, mouseX, mouseY, partialTicks);

        float delta = this.timer.tick();
        // Container
        Draw.borderBox(x + 4, y + 4, EDITOR_WIDTH, EDITOR_HEIGHT, 4, 36, 126);
        // Title background
        Draw.texturedModalRect(x, y - 13, 101, 0, 4, 16);
        Draw.texturedModalRect(x + 4, y - 13, EDITOR_WIDTH - 16, 16, 105, 0, 1, 16);
        Draw.texturedModalRect(x + EDITOR_WIDTH - 17, y - 13, 106, 0, 19, 16);

        this.tabNavigation.draw(GuiGraphicsExtractor, mouseX, mouseY);
        if (this.tabNavigation.getSelectedTab() == this.localPacksTab)
        {
            this.localPacks.draw(GuiGraphicsExtractor, partialTicks);
        }
        else if (this.tabNavigation.getSelectedTab() == this.publicPacksTab)
        {
            String text = "Coming soon...";
            GuiHelper.drawString(GuiGraphicsExtractor, font, text, x + EDITOR_WIDTH / 2 - font.width(text) / 2, y + EDITOR_HEIGHT / 2 - 10,
                    0xffffff, true);
        }

        super.extractRenderState(GuiGraphicsExtractor, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean keyPressed(KeyEvent event)
    {
        if (event.key() == 256)
        {
            GuiHelper.closeGui();
            return true;
        }
        return super.keyPressed(event);
    }

    private void goBack()
    {
        this.minecraft.setScreenAndShow(new GuiBendsMenu());
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

}
