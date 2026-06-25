package goblinbob.mobends.standard.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import goblinbob.mobends.standard.main.ModConfig;
import goblinbob.mobends.standard.main.ModStatics;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;

import java.util.HashMap;
import java.util.Map;
public abstract class RenderBendsArrow<T extends AbstractArrow, S extends ArrowRenderState> extends ArrowRenderer<T, S>
{
    private static final ContextKey<AbstractArrow> TRACKED_ARROW =
            new ContextKey<>(Identifier.fromNamespaceAndPath(ModStatics.MODID, "tracked_arrow"));
    private static final Map<AbstractArrow, ArrowTrail> TRAILS = new HashMap<>();

    public RenderBendsArrow(EntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    public void extractRenderState(T entity, S state, float partialTicks)
    {
        super.extractRenderState(entity, state, partialTicks);
        state.setRenderData(TRACKED_ARROW, entity);
    }

    @Override
    public void submit(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera)
    {
        if (ModConfig.showArrowTrails)
        {
            AbstractArrow arrow = state.getRenderData(TRACKED_ARROW);
            if (arrow != null)
            {
                getOrCreateTrail(arrow).render(submitNodeCollector, camera.pos);
            }
        }

        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    private static ArrowTrail getOrCreateTrail(AbstractArrow arrow)
    {
        cleanupTrails();
        return TRAILS.computeIfAbsent(arrow, ArrowTrail::new);
    }

    private static void cleanupTrails()
    {
        TRAILS.entrySet().removeIf(entry -> entry.getValue().shouldBeRemoved());
    }
}
