package goblinbob.mobends.test.standard.mutators;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DrownedMutatorMigrationTest
{
    @Test
    public void defaultAddonRegistersDrownedWithZombieAnimationBinding() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/DefaultAddon.java"));

        assertTrue(source.contains("import net.minecraft.world.entity.monster.zombie.Drowned;")
                || source.contains("import net.minecraft.world.entity.monster.Drowned;"));
        assertTrue(source.contains("registry.registerNewEntity(Drowned.class"));
        assertTrue(source.contains("DrownedData::new"));
        assertTrue(source.contains("DrownedMutator::new"));
        assertTrue(source.contains("new ZombieRenderer<>()"));
    }

    @Test
    public void drownedMutatorAcceptsDrownedModelForCustomRenderBinding() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/mutators/DrownedMutator.java"));

        assertTrue(source.contains("import net.minecraft.client.model.DrownedModel;")
                || source.contains("import net.minecraft.client.model.monster.zombie.DrownedModel;"));
        assertTrue(source.contains("extends ZombieMutatorBase<DrownedData, Drowned, DrownedModel>"));
        assertTrue(source.contains("return !(model instanceof DrownedModel);"));
    }

    @Test
    public void renderContextRebindsSameClassDrownedOuterLayerModel() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/client/MoBendsRenderContext.java"));

        assertTrue(source.contains("submittedModels.put(entity, model);"));
        assertTrue(source.contains("isCompatibleMutatedModel(submittedModel, mutator)"));
        assertTrue(source.contains("isCompatibleMutatedModel(model, mutator)"));
        assertTrue(source.contains("mutator.shouldModelBeSkipped(entityModel)"));
    }
}
