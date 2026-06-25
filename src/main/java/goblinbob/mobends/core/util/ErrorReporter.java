package goblinbob.mobends.core.util;

import com.mojang.logging.LogUtils;
import goblinbob.mobends.core.pack.InvalidPackFormatException;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

public class ErrorReporter
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String CLIENT_REPORTER_CLASS = "goblinbob.mobends.core.client.ClientErrorReporter";

    public static MutableComponent createErrorHeader()
    {
        return Component.literal("[Mo' Bends] ").withStyle(ChatFormatting.YELLOW);
    }

    public static void showErrorToPlayer(Component textComponent)
    {
        LOGGER.warn("[Mo' Bends] {}", textComponent.getString());

        if (FMLEnvironment.getDist() != Dist.CLIENT)
        {
            return;
        }

        try
        {
            Class<?> reporter = Class.forName(CLIENT_REPORTER_CLASS);
            reporter.getMethod("showErrorToPlayer", Component.class).invoke(null, textComponent);
        }
        catch (ReflectiveOperationException e)
        {
            LOGGER.warn("Failed to display Mo' Bends error to the local player.", e);
        }
    }

    public static void showErrorToPlayer(String error)
    {
        showErrorToPlayer(Component.literal(error));
    }

    public static void showErrorToPlayer(InvalidPackFormatException ex)
    {
        MutableComponent textComponent = Component.literal("A pack has been disabled due to it's wrong format: ");

        MutableComponent packName = Component.literal(ex.getPackName()).withStyle(ChatFormatting.BOLD);
        textComponent.append(packName);

        textComponent.append(Component.literal(". Check the logs for more details..."));

        showErrorToPlayer(textComponent);
    }

}
