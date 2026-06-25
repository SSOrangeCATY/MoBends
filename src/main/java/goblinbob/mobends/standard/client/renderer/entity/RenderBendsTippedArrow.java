package goblinbob.mobends.standard.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.TippableArrowRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.projectile.arrow.Arrow;
public class RenderBendsTippedArrow extends RenderBendsArrow<Arrow, TippableArrowRenderState>
{
    public static final Identifier RES_ARROW = Identifier.withDefaultNamespace("textures/entity/projectiles/arrow.png");
    public static final Identifier RES_TIPPED_ARROW = Identifier.parse(
            "textures/entity/projectiles/arrow_tipped.png");

    public RenderBendsTippedArrow(EntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    protected Identifier getTextureLocation(TippableArrowRenderState state)
    {
        return state.isTipped ? RES_TIPPED_ARROW : RES_ARROW;
    }

    @Override
    public TippableArrowRenderState createRenderState()
    {
        return new TippableArrowRenderState();
    }

    @Override
    public void extractRenderState(Arrow entity, TippableArrowRenderState state, float partialTicks)
    {
        super.extractRenderState(entity, state, partialTicks);
        state.isTipped = entity.getColor() > 0;
    }
}
