package goblinbob.mobends.test.core.client.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EntityRenderHandlerMigrationTest
{
    @Test
    public void renderStateCarriesLivingEntityForRetainedModelRender() throws IOException
    {
        String renderStateSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/client/event/MoBendsRenderState.java"));
        String rendererMixinSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/mixin/LivingEntityRendererMixin.java"));

        assertTrue(renderStateSource.contains("ContextKey<LivingEntity>"));
        assertTrue(rendererMixinSource.contains("renderState.setRenderData(MoBendsRenderState.LIVING_ENTITY, entity);"));
    }

    @Test
    public void retainedModelFeatureRendererRebindsModelContextFromRenderState() throws IOException
    {
        String contextSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/client/MoBendsRenderContext.java"));
        String mixinSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/mixin/ModelFeatureRendererMixin.java"));
        String mixinConfig = Files.readString(Path.of("src/main/resources/mobends.mixins.json"));

        assertTrue(contextSource.contains("new IdentityHashMap<>()"));
        assertTrue(contextSource.contains("new WeakHashMap<>()"));
        assertTrue(contextSource.contains("beginModelRender(Model<?> model, Object state)"));
        assertTrue(contextSource.contains("livingState.getRenderData(MoBendsRenderState.LIVING_ENTITY)"));
        assertTrue(mixinSource.contains("MoBendsRenderContext.beginModelRender(model, submit.state());"));
        assertTrue(mixinSource.contains("MoBendsRenderContext.endModelRender(model);"));
        assertTrue(mixinConfig.contains("\"ModelFeatureRendererMixin\""));
    }

    @Test
    public void renderContextNoLongerUsesThreadLocalCurrentEntityBinding() throws IOException
    {
        String contextSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/client/MoBendsRenderContext.java"));

        assertFalse(contextSource.contains("ThreadLocal"));
        assertFalse(contextSource.contains("currentModel"));
        assertFalse(contextSource.contains("currentMutator"));
    }

    @Test
    public void entityRenderHandlerBindsMutatorToEntityAndModel() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/client/event/EntityRenderHandler.java"));

        assertTrue(source.contains("MoBendsRenderContext.setCurrentMutation(model, entity, mutator);"));
        assertTrue(source.contains("MoBendsRenderContext.clearEntity(entity);"));
    }
}
