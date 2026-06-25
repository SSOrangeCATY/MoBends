package goblinbob.mobends.standard.main;

import com.mojang.logging.LogUtils;
import goblinbob.mobends.core.Core;
import goblinbob.mobends.core.configuration.CoreServerConfig;
import goblinbob.mobends.core.network.NetworkHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Main entry point for the Mo' Bends mod.
 * Ported to Minecraft 26.2 with NeoForge.
 */
@Mod(ModStatics.MODID)
public class MoBends
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String CLIENT_BOOTSTRAP_CLASS = "goblinbob.mobends.standard.client.ClientBootstrap";
    public static final Logger LOG = LOGGER;
    public static MoBends instance;

    public MoBends(IEventBus modEventBus, ModContainer container)
    {
        instance = this;

        // Register configurations
        CoreServerConfig.register(container);
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            registerClient(modEventBus, container);
        }

        // Register lifecycle event listeners
        modEventBus.addListener(this::commonSetup);

        // Register network handler
        modEventBus.addListener(NetworkHandler::register);

        LOGGER.info("Mo' Bends {} initializing...", ModStatics.VERSION);
    }

    /**
     * Common setup - runs on both client and server.
     */
    private void commonSetup(final FMLCommonSetupEvent event)
    {
        if (FMLEnvironment.getDist() == Dist.DEDICATED_SERVER)
        {
            Core.createAsServer();
            Core.getInstance().onCommonSetup();
        }

        LOGGER.info("Mo' Bends common setup complete");
    }

    /**
     * Used to refresh all systems, clear caches. Usually performed when configuration changes.
     */
    public static void refreshSystems()
    {
        if (FMLEnvironment.getDist() == Dist.CLIENT)
        {
            invokeClient("refreshSystems", new Class<?>[0]);
        }
    }

    private static void registerClient(IEventBus modEventBus, ModContainer container)
    {
        invokeClient("register", new Class<?>[] { IEventBus.class, ModContainer.class }, modEventBus, container);
    }

    private static void invokeClient(String methodName, Class<?>[] parameterTypes, Object... args)
    {
        try
        {
            Class<?> clientBootstrap = Class.forName(CLIENT_BOOTSTRAP_CLASS);
            Method method = clientBootstrap.getMethod(methodName, parameterTypes);
            method.invoke(null, args);
        }
        catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException e)
        {
            throw new IllegalStateException("Unable to initialize Mo' Bends client bootstrap", e);
        }
        catch (InvocationTargetException e)
        {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException runtimeException)
            {
                throw runtimeException;
            }
            if (cause instanceof Error error)
            {
                throw error;
            }

            throw new IllegalStateException("Mo' Bends client bootstrap failed", cause);
        }
    }
}
