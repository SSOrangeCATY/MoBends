package goblinbob.mobends.test.standard.client.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RenderingEventHandlerTest
{
    @Test
    public void firstPersonPoseIsSubscribedForHandAndArmEvents() throws IOException
    {
        String source = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/client/event/RenderingEventHandler.java"));

        assertHasSubscribedHandler(source, "RenderHandEvent");
        assertHasSubscribedHandler(source, "RenderArmEvent");
    }

    @Test
    public void firstPersonArmEventSubmitsMobendsGeometryAndCancelsVanillaArm() throws IOException
    {
        String handlerSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/client/event/RenderingEventHandler.java"));
        String mutatorSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/standard/mutators/PlayerMutator.java"));

        assertTrue(handlerSource.contains("mutator.submitFirstPersonArm("));
        assertTrue(handlerSource.contains("event.getSubmitNodeCollector()"));
        assertTrue(handlerSource.contains("event.setCanceled(true);"));
        assertTrue(mutatorSource.contains("public void submitFirstPersonArm("));
        assertTrue(mutatorSource.contains("submitNodeCollector.submitCustomGeometry("));
    }

    private static void assertHasSubscribedHandler(String source, String eventType)
    {
        Pattern handlerPattern = Pattern.compile("@SubscribeEvent\\s+public\\s+void\\s+\\w+\\s*\\(\\s*"
                + eventType + "\\s+event\\s*\\)");

        assertTrue(handlerPattern.matcher(source).find(),
                () -> "Expected subscribed first-person handler for " + eventType);
    }
}
