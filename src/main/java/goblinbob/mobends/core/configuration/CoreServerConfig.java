package goblinbob.mobends.core.configuration;

import com.mojang.logging.LogUtils;
import goblinbob.mobends.core.network.NetworkConfiguration;
import goblinbob.mobends.core.network.SharedProperty;
import goblinbob.mobends.standard.main.ModStatics;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.slf4j.Logger;

/**
 * Server-side configuration for Mo' Bends 26.2.
 */
@EventBusSubscriber(modid = ModStatics.MODID)
public class CoreServerConfig extends CoreConfig
{
    private static final Logger LOGGER = LogUtils.getLogger();

    private static ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static ModConfigSpec SPEC;
    public static ModConfigSpec.BooleanValue MODEL_SCALING_ALLOWED;
    public static ModConfigSpec.BooleanValue BENDS_PACKS_ALLOWED;
    public static ModConfigSpec.BooleanValue MOVEMENT_LIMITED;

    static
    {
        BUILDER.comment("Mo' Bends Server Configuration").push("server");

        MODEL_SCALING_ALLOWED = BUILDER
                .comment("Allow clients to scale player models beyond the vanilla size.")
                .translation(ModStatics.MODID + ".config.model_scaling_allowed")
                .define("modelScalingAllowed", false);

        BENDS_PACKS_ALLOWED = BUILDER
                .comment("Allow clients to use custom bends packs on this server.")
                .translation(ModStatics.MODID + ".config.bends_packs_allowed")
                .define("bendsPacksAllowed", true);

        MOVEMENT_LIMITED = BUILDER
                .comment("Limit excessive movement transforms from bends packs.")
                .translation(ModStatics.MODID + ".config.movement_limited")
                .define("movementLimited", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public CoreServerConfig()
    {
        load();
    }

    @Override
    public void save()
    {
        LOGGER.debug("Saving Mo' Bends server configuration");
    }

    @Override
    public void load()
    {
        LOGGER.debug("Loading Mo' Bends server configuration");

        // Initialize shared properties from network configuration
        final Iterable<SharedProperty<?>> props = NetworkConfiguration.instance.getSharedConfig().getProperties();
        for (SharedProperty<?> prop : props)
        {
            // Properties will use their default values initially
            LOGGER.debug("Initialized shared property: {}", prop.getKey());
        }
    }

    public static void syncNetworkConfiguration()
    {
        NetworkConfiguration.instance.updateServerValues(
                MODEL_SCALING_ALLOWED.get(),
                BENDS_PACKS_ALLOWED.get(),
                MOVEMENT_LIMITED.get());
    }

    @SubscribeEvent
    public static void onConfigLoading(final ModConfigEvent.Loading event)
    {
        if (event.getConfig().getSpec() == SPEC)
        {
            syncNetworkConfiguration();
        }
    }

    @SubscribeEvent
    public static void onConfigReloading(final ModConfigEvent.Reloading event)
    {
        if (event.getConfig().getSpec() == SPEC)
        {
            syncNetworkConfiguration();
        }
    }

    /**
     * Register this config with NeoForge.
     * Call during mod construction.
     */
    public static void register(ModContainer container)
    {
        container.registerConfig(ModConfig.Type.SERVER, SPEC, ModStatics.MODID + "-server.toml");
    }
}
