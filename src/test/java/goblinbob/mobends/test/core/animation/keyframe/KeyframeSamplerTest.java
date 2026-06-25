package goblinbob.mobends.test.core.animation.keyframe;

import goblinbob.mobends.core.animation.keyframe.Bone;
import goblinbob.mobends.core.animation.keyframe.Keyframe;
import goblinbob.mobends.core.animation.keyframe.KeyframeSampler;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KeyframeSamplerTest
{
    @Test
    public void singleFrameBoneSamplesOnlyFrameForBothEndpoints()
    {
        Keyframe onlyFrame = new Keyframe();
        Bone bone = bone(onlyFrame);

        KeyframeSampler.Sample sample = KeyframeSampler.sample(bone, 7.25F);

        assertSame(onlyFrame, sample.current());
        assertSame(onlyFrame, sample.next());
        assertEquals(0.0F, sample.progress());
    }

    @Test
    public void multiFrameBoneClampsFrameIndexToAvailableRange()
    {
        Keyframe first = new Keyframe();
        Keyframe last = new Keyframe();
        Bone bone = bone(first, last);

        KeyframeSampler.Sample sample = KeyframeSampler.sample(bone, 9.75F);

        assertSame(last, sample.current());
        assertSame(last, sample.next());
        assertEquals(0.0F, sample.progress());
    }

    @Test
    public void animationExecutionUsesPerBoneSampling() throws IOException
    {
        String bitSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/animation/bit/KeyframeAnimationBit.java"));
        String layerSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/animation/layer/KeyframeAnimationLayer.java"));
        String kumoSource = Files.readString(Path.of(
                "src/main/java/goblinbob/mobends/core/kumo/state/keyframe/KeyframeLayerState.java"));

        assertTrue(bitSource.contains("KeyframeSampler.sample("));
        assertTrue(layerSource.contains("KeyframeSampler.sample("));
        assertTrue(kumoSource.contains("KeyframeSampler.sample("));
    }

    private static Bone bone(Keyframe... keyframes)
    {
        Bone bone = new Bone();
        bone.keyframes = List.of(keyframes);
        return bone;
    }
}
