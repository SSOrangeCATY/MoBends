package goblinbob.mobends.test.standard.mutators;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WolfMutatorMigrationTest
{
    @Test
    public void wolfControllerRestoresAdultWolfBasePoseAfterAnimatorUpdate() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/animation/controller/WolfController.java"));

        assertTrue(source.contains("data.body.rotation.localRotateX(90.0F).finish();"));
        assertTrue(source.contains("data.mane.rotation.localRotateX(90.0F).finish();"));
    }

    @Test
    public void wolfControllerKeepsShakeAndTailRotationsInPortedApi() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/animation/controller/WolfController.java"));

        assertTrue(source.contains("getBodyRollAngle(wolf, DataUpdateHandler.partialTicks, -0.08F)"));
        assertTrue(source.contains("getBodyRollAngle(wolf, DataUpdateHandler.partialTicks, -0.2F)"));
        assertTrue(source.contains("wolf.getTailAngle() * GUtil.RAD_TO_DEG - 90.0F"));
    }
}
