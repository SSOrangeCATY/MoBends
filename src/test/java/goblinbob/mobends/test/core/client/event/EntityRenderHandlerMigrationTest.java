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
    public void livingSubmitMixinTargetsConcreteRenderStateSubmitMethod() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/mixin/LivingEntityRendererSubmitMixin.java"));

        assertTrue(source.contains(
                "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;" +
                        "Lcom/mojang/blaze3d/vertex/PoseStack;" +
                        "Lnet/minecraft/client/renderer/SubmitNodeCollector;" +
                        "Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V"));
    }

    @Test
    public void mixinCompatibilityMatchesJavaToolchain() throws IOException
    {
        String buildSource = Files.readString(Path.of("build.gradle"));
        String mixinConfig = Files.readString(Path.of("src/main/resources/mobends.mixins.json"));

        assertTrue(buildSource.contains("JavaLanguageVersion.of(25)"));
        assertTrue(mixinConfig.contains("\"compatibilityLevel\": \"JAVA_25\""));
    }

    @Test
    public void cleanupIsOwnedBySubmitReturnHookInsteadOfRenderPostEvent() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/client/event/EntityRenderHandler.java"));

        assertFalse(source.contains("RenderLivingEvent.Post"));
        assertFalse(source.contains("afterLivingRender"));
    }

    @Test
    public void renderContextIsBoundToSubmittedModelForDelayedRendering() throws IOException
    {
        String contextSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/client/MoBendsRenderContext.java"));
        String handlerSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/client/event/EntityRenderHandler.java"));
        String mixinSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/mixin/AgeableListModelMixin.java"));

        assertTrue(contextSource.contains("Map<Model,"));
        assertTrue(contextSource.contains("Map<LivingEntity,"));
        assertTrue(contextSource.contains("new IdentityHashMap<>()"));
        assertFalse(contextSource.contains("Map<Model, BipedMutator<?, ?, ?, ?>> bipedMutators =\n            Collections.synchronizedMap(new WeakHashMap<>());"));
        assertTrue(handlerSource.contains("MoBendsRenderContext.setBipedMutator(model, living, bipedMutator);"));
        assertTrue(mixinSource.contains("MoBendsRenderContext.getBipedMutator((Model) (Object) this)"));
    }

    @Test
    public void submitReturnCleanupDoesNotClearDelayedModelRenderContext() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/client/event/EntityRenderHandler.java"));

        assertFalse(source.contains("MoBendsRenderContext.clear();"));
    }

    @Test
    public void wolfRenderContextKeepsWolfSpecificBindingForLayers() throws IOException
    {
        String wolfMixinSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/mixin/ColorableAgeableListModelMixin.java"));
        String handlerSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/client/event/EntityRenderHandler.java"));

        assertTrue(wolfMixinSource.contains("instanceof WolfModel"));
        assertTrue(wolfMixinSource.contains("MoBendsRenderContext.getWolfMutator((Model) (Object) this)"));
        assertTrue(handlerSource.contains("MoBendsRenderContext.setWolfMutator(model, living, wolfMutator);"));
    }

    @Test
    public void retainedModelFeatureRendererBindsContextFromSubmittedRenderState() throws IOException
    {
        Path mixinPath = Path.of("src/main/java/goblinbob/mobends/mixin/ModelFeatureRendererMixin.java");
        assertTrue(Files.isRegularFile(mixinPath));

        String mixinConfig = Files.readString(Path.of("src/main/resources/mobends.mixins.json"));
        String mixinSource = Files.readString(mixinPath);

        assertTrue(mixinConfig.contains("\"ModelFeatureRendererMixin\""));
        assertTrue(mixinSource.contains("ModelFeatureRenderer"));
        assertTrue(mixinSource.contains("MoBendsRenderContext.beginModelRender(model, submit.state());"));
        assertTrue(mixinSource.contains("MoBendsRenderContext.endModelRender(model);"));
    }

    @Test
    public void livingRendererExtractionAttachesEntityAsRenderStateFallback() throws IOException
    {
        Path mixinPath = Path.of("src/main/java/goblinbob/mobends/mixin/LivingEntityRendererMixin.java");
        assertTrue(Files.isRegularFile(mixinPath));

        String mixinConfig = Files.readString(Path.of("src/main/resources/mobends.mixins.json"));
        String mixinSource = Files.readString(mixinPath);

        assertTrue(mixinConfig.contains("\"LivingEntityRendererMixin\""));
        assertTrue(mixinSource.contains("LivingEntityRenderer"));
        assertTrue(mixinSource.contains(
                "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;" +
                        "Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V"));
        assertTrue(mixinSource.contains("renderState.setRenderData(MoBendsRenderState.LIVING_ENTITY, entity);"));
    }

    @Test
    public void livingRendererExtractionStillAppliesMutationFallback() throws IOException
    {
        String mixinSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/mixin/LivingEntityRendererMixin.java"));

        assertTrue(mixinSource.contains("EntityRenderHandler.applyLivingEntityMutation"));
        assertTrue(mixinSource.contains("(LivingEntityRenderer<?, ?, ?>) (Object) this"));
    }

    @Test
    public void genericModelMixinRendersCurrentMutation() throws IOException
    {
        Path mixinPath = Path.of("src/main/java/goblinbob/mobends/mixin/ModelMixin.java");
        assertTrue(Files.isRegularFile(mixinPath));

        String mixinConfig = Files.readString(Path.of("src/main/resources/mobends.mixins.json"));
        String mixinSource = Files.readString(mixinPath);
        String contextSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/client/MoBendsRenderContext.java"));

        assertTrue(mixinConfig.contains("\"ModelMixin\""));
        assertTrue(mixinSource.contains("MoBendsRenderContext.renderCurrentModel"));
        assertTrue(contextSource.contains("public static boolean renderCurrentModel"));
    }

    @Test
    public void renderHandlerBindsGenericMutationForAllMutatorTypes() throws IOException
    {
        String handlerSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/client/event/EntityRenderHandler.java"));
        String contextSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/client/MoBendsRenderContext.java"));

        assertTrue(handlerSource.contains("MoBendsRenderContext.setMutation(model, living, mutator);"));
        assertTrue(contextSource.contains("Map<Model, Mutator<?, ?, ?, ?>>"));
        assertTrue(contextSource.contains("Map<LivingEntity, Mutator<?, ?, ?, ?>>"));
    }

    @Test
    public void renderContextRebindsCompatibleSubmittedLayerModels() throws IOException
    {
        String contextSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/client/MoBendsRenderContext.java"));

        assertTrue(contextSource.contains("Map<LivingEntity, Model"));
        assertTrue(contextSource.contains("submittedModels.put(living, model);"));
        assertTrue(contextSource.contains("submittedModels.remove(living);"));
        assertTrue(contextSource.contains("isSubmittedOrCompatibleLayerModel"));
        assertTrue(contextSource.contains("isCompatibleMutatedModel"));
        assertTrue(contextSource.contains("model instanceof EntityModel<?> entityModel"));
        assertTrue(contextSource.contains("!mutator.shouldModelBeSkipped(entityModel)"));
    }

    @Test
    public void retainedModelContextSurvivesEachSubmittedModelRender() throws IOException
    {
        String bipedMixin = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/mixin/AgeableListModelMixin.java"));
        String spiderMixin = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/mixin/SpiderModelMixin.java"));
        String wolfMixin = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/mixin/ColorableAgeableListModelMixin.java"));
        String contextSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/client/MoBendsRenderContext.java"));
        String handlerSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/client/event/EntityRenderHandler.java"));

        assertFalse(bipedMixin.contains("MoBendsRenderContext.clear((Model) (Object) this);"));
        assertFalse(spiderMixin.contains("MoBendsRenderContext.clear((Model) (Object) this);"));
        assertFalse(wolfMixin.contains("MoBendsRenderContext.clear((Model) (Object) this);"));
        assertTrue(contextSource.contains("public static void clearModel(Model model)"));
        assertTrue(handlerSource.contains("MoBendsRenderContext.clearModel(model);"));
        assertFalse(contextSource.contains("public static void clear(Model model)"));
    }
}
