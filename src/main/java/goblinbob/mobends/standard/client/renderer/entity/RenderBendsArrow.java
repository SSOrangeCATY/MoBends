package goblinbob.mobends.standard.client.renderer.entity;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RenderBendsArrow<T extends AbstractArrow, S extends ArrowRenderState> extends ArrowRenderer<T, S>
{
    public RenderBendsArrow(EntityRendererProvider.Context context)
    {
        super(context);
    }
}
