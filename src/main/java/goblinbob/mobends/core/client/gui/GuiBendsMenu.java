package goblinbob.mobends.core.client.gui;

import goblinbob.mobends.core.WebAPI;
import goblinbob.mobends.core.client.gui.elements.GuiSectionButton;
import goblinbob.mobends.core.client.gui.packswindow.GuiPacksWindow;
import goblinbob.mobends.core.client.gui.popup.GuiEditorNotFound;
import goblinbob.mobends.core.client.gui.popup.GuiPopUp;
import goblinbob.mobends.core.client.gui.settingswindow.GuiSettingsWindow;
import goblinbob.mobends.core.network.NetworkConfiguration;
import goblinbob.mobends.core.util.Draw;
import goblinbob.mobends.core.util.GuiHelper;
import goblinbob.mobends.standard.main.ModStatics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class GuiBendsMenu extends Screen
{

	private static final Identifier MENU_TITLE_TEXTURE = Identifier.fromNamespaceAndPath(ModStatics.MODID,
			"textures/gui/title.png");
	public static final Identifier ICONS_TEXTURE = Identifier.fromNamespaceAndPath(ModStatics.MODID,
			"textures/gui/icons.png");

	private GuiSectionButton settingsButton;
	private GuiSectionButton packsButton;
	private GuiSectionButton customizeButton;
	private GuiPopUp popUp;

	public GuiBendsMenu()
	{
		super(Component.translatable("mobends.gui.title"));

		this.settingsButton = new GuiSectionButton(Component.translatable("mobends.gui.section.settings").getString(), 0xFFDA3A00)
				.setLeftIcon(0, 43, 19, 19).setRightIcon(19, 43, 19, 19);
		this.packsButton = new GuiSectionButton(Component.translatable("mobends.gui.section.packs").getString(), 0xFF4577DE)
				.setLeftIcon(38, 43, 23, 20).setRightIcon(38, 43, 23, 20);
		this.customizeButton = new GuiSectionButton(Component.translatable("mobends.gui.section.customize").getString(), 0xFF26DAA3)
				.setLeftIcon(80, 43, 19, 14).setRightIcon(80, 43, 19, 14);

		this.popUp = null;
	}

	@Override
	protected void init()
	{
		super.init();
		this.clearWidgets();

		if (this.popUp != null)
			this.popUp.initGui(this.width / 2, this.height / 2);

		int startY = height / 2 - 32;
		int distance = 49;

		if (NetworkConfiguration.instance.areBendsPacksAllowed())
		{
			this.settingsButton.initGui((this.width - 318) / 2, startY);
			this.packsButton.initGui((this.width - 318) / 2, startY + distance);
			this.customizeButton.initGui((this.width - 318) / 2, startY + distance * 2);
		}
		else
		{
			this.settingsButton.initGui((this.width - 318) / 2, startY);
			this.customizeButton.initGui((this.width - 318) / 2, startY + distance);
		}
	}

	@Override
	public boolean keyPressed(KeyEvent event)
	{
		if (popUp != null)
		{
			return true;
		}

		if (event.isEscape())
		{
			this.onClose();
			return true;
		}

		return super.keyPressed(event);
	}

	@Override
	public void onClose()
	{
		super.onClose();
	}

	@Override
	public void tick()
	{
		super.tick();

		if (this.minecraft == null) return;

		double mouseX = this.minecraft.mouseHandler.xpos() * (double)this.width / (double)this.minecraft.getWindow().getScreenWidth();
		double mouseY = this.minecraft.mouseHandler.ypos() * (double)this.height / (double)this.minecraft.getWindow().getScreenHeight();

		if (this.popUp != null)
		{
			this.popUp.update((int)mouseX, (int)mouseY);
			return;
		}

		this.settingsButton.update((int)mouseX, (int)mouseY);
		this.packsButton.update((int)mouseX, (int)mouseY);
		this.customizeButton.update((int)mouseX, (int)mouseY);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick)
	{
		double mouseX = event.x();
		double mouseY = event.y();
		int button = event.button();
		int x = (int) mouseX;
		int y = (int) mouseY;

		if (popUp != null)
		{
			popUp.mouseClicked(x, y, button);
			return true;
		}

		if (settingsButton.mouseClicked(x, y, button))
		{
			minecraft.gui.setScreen(new GuiSettingsWindow());
			return true;
		}
		else if (packsButton.mouseClicked(x, y, button))
		{
			minecraft.gui.setScreen(new GuiPacksWindow());
			return true;
		}
		else if (customizeButton.mouseClicked(x, y, button))
		{
			IAnimationEditor editor = AnimationEditorRegistry.INSTANCE.getPrimaryEditor();

			if (editor == null)
			{
				openPopUp(new GuiEditorNotFound(this::closePopUp, () -> {
					GuiEditorNotFound editorNotFoundPopup = (GuiEditorNotFound) popUp;
					String downloadUrl = WebAPI.INSTANCE.getOfficialAnimationEditorUrl();

					if (downloadUrl == null || !GuiHelper.openUrlInBrowser(downloadUrl))
					{
						editorNotFoundPopup.setErrorOccurred(true);
					}
					else
					{
						closePopUp();
					}
				}));
			}
			else
			{
				// Opens the animation editor.
				editor.openEditorGui();
			}
			return true;
		}

		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event)
	{
		double mouseX = event.x();
		double mouseY = event.y();
		int button = event.button();
		int x = (int) mouseX;
		int y = (int) mouseY;

		this.settingsButton.mouseReleased(x, y, button);
		this.packsButton.mouseReleased(x, y, button);
		this.customizeButton.mouseReleased(x, y, button);

		return super.mouseReleased(event);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks)
	{
        Draw.setGuiGraphics(guiGraphics);
        Draw.resetColor();

		int titleWidth = 167 * 2;
		int titleHeight = 37 * 2;

		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MENU_TITLE_TEXTURE,
				(width - titleWidth) / 2, (height - titleHeight) / 2 - 70,
				0, 0, titleWidth, titleHeight, titleWidth, titleHeight);

		this.settingsButton.display(guiGraphics);
		if (NetworkConfiguration.instance.areBendsPacksAllowed())
		{
			this.packsButton.display(guiGraphics);
		}
		this.customizeButton.display(guiGraphics);

		super.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);

		if (this.popUp != null)
		{
			guiGraphics.fill(0, 0, this.width, this.height, 0x66000000);
			this.popUp.display(guiGraphics, mouseX, mouseY, partialTicks);
		}
	}

	@Override
	public boolean isPauseScreen()
	{
		return false;
	}

	private void closePopUp()
	{
		this.popUp = null;
		this.init();
	}

	private void openPopUp(GuiPopUp popUp)
	{
		this.popUp = popUp;
		this.init();
	}

}
