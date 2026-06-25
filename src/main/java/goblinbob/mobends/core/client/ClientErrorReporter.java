package goblinbob.mobends.core.client;

import goblinbob.mobends.core.util.ErrorReporter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class ClientErrorReporter
{
    private ClientErrorReporter()
    {
    }

    public static void showErrorToPlayer(Component textComponent)
    {
        if (Minecraft.getInstance().player == null)
        {
            return;
        }

        MutableComponent base = Component.literal("").withStyle(ChatFormatting.WHITE);
        base.append(ErrorReporter.createErrorHeader());
        base.append(textComponent);

        Minecraft.getInstance().player.sendSystemMessage(base);
    }
}
