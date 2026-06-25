package goblinbob.mobends.standard.client;

import com.mojang.logging.LogUtils;
import goblinbob.mobends.core.Core;
import goblinbob.mobends.core.CoreClient;
import goblinbob.mobends.core.addon.AddonHelper;
import goblinbob.mobends.core.addon.Addons;
import goblinbob.mobends.core.animation.keyframe.AnimationLoader;
import goblinbob.mobends.core.bender.EntityBenderRegistry;
import goblinbob.mobends.core.client.event.KeyboardHandler;
import goblinbob.mobends.core.client.event.MoBendsRenderState;
import goblinbob.mobends.core.compat.PlayerAnimationLibCompat;
import goblinbob.mobends.core.configuration.CoreClientConfig;
import goblinbob.mobends.core.data.EntityDatabase;
import goblinbob.mobends.core.pack.PackDataProvider;
import goblinbob.mobends.core.util.GsonResources;
import goblinbob.mobends.standard.DefaultAddon;
import goblinbob.mobends.standard.main.ModConfig;
import goblinbob.mobends.standard.main.ModStatics;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;

public final class ClientBootstrap
{
    private static final Logger LOGGER = LogUtils.getLogger();

    private ClientBootstrap()
    {
    }

    public static void register(IEventBus modEventBus, ModContainer container)
    {
        CoreClientConfig.register(container);
        ModConfig.register(container);
        modEventBus.addListener(ClientBootstrap::clientSetup);
        modEventBus.addListener(KeyboardHandler::registerKeyMappings);
        modEventBus.addListener(MoBendsRenderState::registerRenderStateModifiers);
    }

    public static void refreshSystems()
    {
        AnimationLoader.clearCache();
        GsonResources.clearCache();
        PackDataProvider.INSTANCE.clearCache();
        EntityDatabase.instance.refresh();
        EntityBenderRegistry.instance.refreshMutators();
        Addons.onRefresh();

        if (Core.getInstance() != null)
        {
            Core.getInstance().refreshModules();
        }
    }

    private static void clientSetup(final FMLClientSetupEvent event)
    {
        CoreClient.create();
        Core.getInstance().onClientSetup();

        AddonHelper.registerAddon(ModStatics.MODID, new DefaultAddon());
        Core.getInstance().applyConfigurationToEntityBenders();

        PlayerAnimationLibCompat.init();

        LOGGER.info("Mo' Bends client setup complete");
    }
}
