package goblinbob.mobends.test.core.client.gui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GuiBendsMenuRenderingTest
{
    @Test
    public void customMenuScreensDoNotRequestAnExtraBlurredBackground() throws IOException
    {
        String menuSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/client/gui/GuiBendsMenu.java"));
        String settingsSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/client/gui/settingswindow/GuiSettingsWindow.java"));
        String packsSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/client/gui/packswindow/GuiPacksWindow.java"));

        assertDoesNotRequestBackground(menuSource);
        assertDoesNotRequestBackground(settingsSource);
        assertDoesNotRequestBackground(packsSource);
    }

    @Test
    public void mainMenuKeepsTheFullTextureTitleBlit() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/client/gui/GuiBendsMenu.java"));

        assertTrue(source.contains("guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MENU_TITLE_TEXTURE"));
        assertTrue(source.contains("titleWidth, titleHeight, titleWidth, titleHeight"));
        assertFalse(source.contains("Draw.texturedModalRect((width - titleWidth) / 2"));
    }

    @Test
    private static void assertDoesNotRequestBackground(String source)
    {
        assertFalse(source.contains("extractBackground(guiGraphics, mouseX, mouseY, partialTicks);"));
        assertFalse(source.contains("renderBlurredBackground"));
        assertFalse(source.contains("blurBeforeThisStratum"));
    }
}
