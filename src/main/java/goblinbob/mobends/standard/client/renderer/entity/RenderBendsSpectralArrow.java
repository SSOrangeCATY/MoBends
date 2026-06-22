package goblinbob.mobends.standard.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.projectile.arrow.SpectralArrow;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderBendsSpectralArrow extends RenderBendsArrow<SpectralArrow, ArrowRenderState>
{
    public static final Identifier RES_SPECTRAL_ARROW = Identifier.parse(
            "textures/entity/projectiles/spectral_arrow.png");

    public RenderBendsSpectralArrow(EntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    protected Identifier getTextureLocation(ArrowRenderState state)
    {
        return RES_SPECTRAL_ARROW;
    }

    @Override
    public ArrowRenderState createRenderState()
    {
        return new ArrowRenderState();
    }
}
