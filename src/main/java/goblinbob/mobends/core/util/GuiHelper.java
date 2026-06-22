package goblinbob.mobends.core.util;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import org.slf4j.Logger;

import java.net.URI;

public class GuiHelper
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void closeGui()
    {
        Minecraft.getInstance().setScreenAndShow(null);
    }

    public static void playButtonSound(SoundManager soundManager)
    {
        soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public static boolean openUrlInBrowser(String url)
    {
        try
        {
            Class<?> oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop").invoke((Object)null);
            oclass.getMethod("browse", URI.class).invoke(object, new URI(url));
            return true;
        }
        catch (Throwable throwable)
        {
            LOGGER.warn(String.format("Couldn't open link %s", url));
            return false;
        }
    }

    public static int drawString(GuiGraphicsExtractor guiGraphicsExtractor, Font font, String text, int x, int y, int color, boolean shadow)
    {
        guiGraphicsExtractor.textRenderer().accept(x, y, Component.literal(text));
        return x + font.width(text);
    }

    public static void drawCenteredString(GuiGraphicsExtractor guiGraphicsExtractor, Font font, String text, int x, int y, int color)
    {
        guiGraphicsExtractor.textRenderer().accept(x - font.width(text) / 2, y, Component.literal(text));
    }

    public static void blit(GuiGraphicsExtractor guiGraphicsExtractor, Identifier texture, int x, int y, int u, int v, int width, int height)
    {
        guiGraphicsExtractor.blit(texture, x, y, width, height, u, v, u + width, v + height);
    }

    public static void blit(GuiGraphicsExtractor guiGraphicsExtractor, Identifier texture, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight)
    {
        guiGraphicsExtractor.blit(texture, x, y, width, height, u, v, u + width, v + height);
    }

}
