package goblinbob.mobends.core.client.event;

import com.google.common.reflect.TypeToken;
import goblinbob.mobends.standard.main.ModStatics;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.renderstate.AvatarRenderStateModifier;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;

/**
 * Stores Mo' Bends render-time context on NeoForge 26.2 render states.
 */
public final class MoBendsRenderState
{
    public static final ContextKey<LivingEntity> LIVING_ENTITY =
            new ContextKey<>(Identifier.fromNamespaceAndPath(ModStatics.MODID, "living_entity"));

    private MoBendsRenderState()
    {
    }

    public static void registerRenderStateModifiers(RegisterRenderStateModifiersEvent event)
    {
        event.registerEntityModifier(new TypeToken<LivingEntityRenderer<LivingEntity, LivingEntityRenderState, ?>>() {},
                (LivingEntity entity, LivingEntityRenderState state) ->
                        state.setRenderData(LIVING_ENTITY, entity));
        event.registerAvatarEntityModifier(new AvatarRenderStateModifier()
        {
            @Override
            public <T extends Avatar & ClientAvatarEntity> void accept(T avatar, AvatarRenderState renderState)
            {
                renderState.setRenderData(LIVING_ENTITY, avatar);
            }
        });
    }
}
