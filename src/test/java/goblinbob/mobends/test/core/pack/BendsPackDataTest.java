package goblinbob.mobends.test.core.pack;

import goblinbob.mobends.core.pack.BendsPackData;
import goblinbob.mobends.core.pack.PackCombiner;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BendsPackDataTest
{
    @Test
    public void normalizesMissingAnimationAndTargetMaps()
    {
        BendsPackData data = new BendsPackData();
        data.targets = null;
        data.keyframeAnimations = null;

        assertDoesNotThrow(data::normalize);

        assertNotNull(data.targets);
        assertNotNull(data.keyframeAnimations);
        assertTrue(data.targets.isEmpty());
        assertTrue(data.keyframeAnimations.isEmpty());
    }

    @Test
    public void combinerSkipsNullPackDataAndNormalizesMissingMaps()
    {
        BendsPackData data = new BendsPackData();
        data.targets = null;
        data.keyframeAnimations = null;

        BendsPackData combined = assertDoesNotThrow(() -> PackCombiner.combineData(List.of(data)));

        assertNotNull(combined.targets);
        assertNotNull(combined.keyframeAnimations);
        assertTrue(combined.targets.isEmpty());
        assertTrue(combined.keyframeAnimations.isEmpty());
    }
}
