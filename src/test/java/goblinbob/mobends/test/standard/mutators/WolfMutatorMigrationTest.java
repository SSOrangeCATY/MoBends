package goblinbob.mobends.test.standard.mutators;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WolfMutatorMigrationTest
{
    @Test
    public void adultWolfPartsKeepVanillaBodyManeAndTailBaseRotations() throws IOException
    {
        String mutatorSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/mutators/WolfMutator.java"));
        String controllerSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/animation/controller/WolfController.java"));

        assertTrue(mutatorSource.contains("wolfBody.syncUp(data.body);"));
        assertTrue(mutatorSource.contains("wolfMane.syncUp(data.mane);"));
        assertTrue(mutatorSource.contains("wolfTail.syncUp(data.tail);"));

        assertTrue(controllerSource.contains("data.body.rotation.localRotateX(90.0F).finish();"));
        assertTrue(controllerSource.contains("data.mane.rotation.localRotateX(90.0F).finish();"));
        assertTrue(controllerSource.contains("data.tail.rotation.localRotateX(wolf.getTailAngle() * GUtil.RAD_TO_DEG - 90.0F).finish();"));
    }
}
