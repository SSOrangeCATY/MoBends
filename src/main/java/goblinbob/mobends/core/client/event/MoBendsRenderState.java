package goblinbob.mobends.core.client.event;

import goblinbob.mobends.standard.main.ModStatics;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.LivingEntity;

public final class MoBendsRenderState
{
    public static final ContextKey<LivingEntity> LIVING_ENTITY =
            new ContextKey<>(Identifier.fromNamespaceAndPath(ModStatics.MODID, "living_entity"));

    private MoBendsRenderState()
    {
    }
}
