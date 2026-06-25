package goblinbob.mobends.standard.client.event;

import goblinbob.mobends.core.util.BenderHelper;
import goblinbob.mobends.standard.mutators.PlayerMutator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.client.event.RenderArmEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class RenderingEventHandler
{

    @SubscribeEvent
    public void beforeHandRender(RenderHandEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        Entity viewEntity = mc.getCameraEntity();

        if (!(viewEntity instanceof AbstractClientPlayer))
            return;

        poseFirstPersonPlayer((AbstractClientPlayer) viewEntity);
    }

    @SubscribeEvent
    public void beforeArmRender(RenderArmEvent event)
    {
        PlayerMutator mutator = getFirstPersonMutator(event.getPlayer());
        if (mutator == null)
        {
            return;
        }

        mutator.poseForFirstPersonView();
        mutator.submitFirstPersonArm(event.getPoseStack(), event.getSubmitNodeCollector(),
                event.getPackedLight(), event.getArm(), event.getPlayer());
        event.setCanceled(true);
    }

    private void poseFirstPersonPlayer(AbstractClientPlayer player)
    {
        PlayerMutator mutator = getFirstPersonMutator(player);
        if (mutator != null)
            mutator.poseForFirstPersonView();
    }

    private PlayerMutator getFirstPersonMutator(AbstractClientPlayer player)
    {
        if (player == null)
            return null;

        if (!BenderHelper.isEntityAnimated(player))
            return null;

        Minecraft mc = Minecraft.getInstance();
        EntityRenderer<? super AbstractClientPlayer, ?> renderer = mc.getEntityRenderDispatcher().getRenderer(player);
        if (!(renderer instanceof AvatarRenderer<?> renderPlayer))
            return null;

        return (PlayerMutator) BenderHelper.getMutatorForRenderer(AbstractClientPlayer.class, renderPlayer);
    }

}
