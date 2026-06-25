package goblinbob.mobends.core.client.event;

import com.mojang.blaze3d.platform.InputConstants;
import goblinbob.mobends.core.client.gui.GuiBendsMenu;
import goblinbob.mobends.standard.client.gui.ArmorDebugScreen;
import goblinbob.mobends.standard.main.MoBends;
import goblinbob.mobends.standard.main.ModStatics;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class KeyboardHandler
{

    private static final KeyMapping.Category MAIN_CATEGORY = new KeyMapping.Category(
            Identifier.fromNamespaceAndPath(ModStatics.MODID, "main"));
    private static final KeyMapping KEY_MENU = new KeyMapping(
            "key.mobends.menu",
            GLFW.GLFW_KEY_G,
            MAIN_CATEGORY);
    private static final KeyMapping KEY_REFRESH = new KeyMapping(
            "key.mobends.refresh",
            GLFW.GLFW_KEY_F10,
            MAIN_CATEGORY);
    private static final KeyMapping KEY_ARMOR_DEBUG = new KeyMapping(
            "key.mobends.armor_debug",
            GLFW.GLFW_KEY_F9,
            MAIN_CATEGORY);

    public static void registerKeyMappings(RegisterKeyMappingsEvent event)
    {
        event.registerCategory(MAIN_CATEGORY);
        event.register(KEY_MENU);
        event.register(KEY_REFRESH);
        event.register(KEY_ARMOR_DEBUG);
    }

    @SubscribeEvent
    public void onKeyPressed(InputEvent.Key event)
    {
        if (KEY_MENU.consumeClick())
        {
            Minecraft.getInstance().gui.setScreen(new GuiBendsMenu());
        }
        else if (KEY_REFRESH.consumeClick())
        {
            MoBends.refreshSystems();
        }
        else if (KEY_ARMOR_DEBUG.consumeClick())
        {
            Minecraft.getInstance().gui.setScreen(new ArmorDebugScreen());
        }
    }

}
