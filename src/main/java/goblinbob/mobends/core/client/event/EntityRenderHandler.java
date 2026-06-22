package goblinbob.mobends.core.client.event;

import goblinbob.mobends.core.client.MoBendsRenderContext;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class EntityRenderHandler
{
    @SubscribeEvent
    public void beforeLivingRender(RenderLivingEvent.Pre<?, ?, ?> event)
    {
        MoBendsRenderContext.clear();
    }

    @SubscribeEvent
    public void afterLivingRender(RenderLivingEvent.Post<?, ?, ?> event)
    {
        MoBendsRenderContext.clear();
    }
}
