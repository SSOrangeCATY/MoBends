package goblinbob.mobends.test.standard.mutators;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BabyBipedMutatorMigrationTest
{
    @Test
    public void bipedMutatorKeepsCurrentEntityDataForBabyRendering() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/mutators/BipedMutator.java"));

        assertTrue(source.contains("protected D currentData;"));
        assertTrue(source.contains("this.currentData = data;"));
    }

    @Test
    public void bipedMutatorUsesBabyPartsAroundCustomModelRender() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/mutators/BipedMutator.java"));

        assertTrue(source.contains("currentData.getEntity().isBaby()"));
        assertTrue(source.contains("createBabyParts(scaleFactor);"));
        assertTrue(source.contains("poseStack.pushPose();"));
        assertTrue(source.contains("renderBabyBipedParts(poseStack, vertexConsumer, packedLight, packedOverlay, color);"));
        assertTrue(source.contains("renderBipedParts(poseStack, vertexConsumer, packedLight, packedOverlay, color);"));
        assertTrue(source.contains("poseStack.popPose();"));
    }

    @Test
    public void bipedMutatorBuildsBabyZombieSizedCustomParts() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/mutators/BipedMutator.java"));

        assertTrue(source.contains("babyBody.addCube(-2.0F, -2.5F, -1.0F, 4, 5, 2, scaleFactor);"));
        assertTrue(source.contains("babyHead.addCube(-3.0F, -6.25F, -3.0F, 6, 6, 6, scaleFactor);"));
        assertTrue(source.contains("babyRightArm.addCube(-1.0F, -0.5F, -1.0F, 2, 5, 2, scaleFactor);"));
        assertTrue(source.contains("babyRightLeg.addCube(-1.0F, 0.0F, -1.0F, 2, 4, 2, scaleFactor);"));
        assertTrue(source.contains("babyBody.addChild(babyHead);"));
        assertTrue(source.contains("babyBody.addChild(babyRightArm);"));
        assertTrue(source.contains("babyBody.addChild(babyLeftArm);"));
        assertTrue(source.contains("syncBabyPart(babyBody, data.body);"));
        assertTrue(source.contains("renderBabyBipedParts"));
    }

    @Test
    public void babyZombieAndDrownedModelsRemainBoundToSpecializedMutators() throws IOException
    {
        String zombieSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/mutators/ZombieMutator.java"));
        String drownedSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/mutators/DrownedMutator.java"));

        assertTrue(zombieSource.contains("return !(model instanceof ZombieModel);"));
        assertTrue(drownedSource.contains("return !(model instanceof DrownedModel);"));
    }

    @Test
    public void renderContextAllowsAgeableRendererToSwapAdultAndBabyModels() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/client/MoBendsRenderContext.java"));

        assertTrue(source.contains("isCompatibleMutatedModel(model, mutator)"));
        assertTrue(source.contains("isCompatibleMutatedModel(submittedModel, mutator)"));
    }
}
