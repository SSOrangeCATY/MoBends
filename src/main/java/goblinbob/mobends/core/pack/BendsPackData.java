package goblinbob.mobends.core.pack;

import goblinbob.mobends.core.animation.keyframe.KeyframeAnimation;
import goblinbob.mobends.core.kumo.state.IKumoInstancingContext;
import goblinbob.mobends.core.kumo.state.template.AnimatorTemplate;

import java.util.HashMap;
import java.util.Map;

public class BendsPackData implements IKumoInstancingContext
{

    public Map<String, AnimatorTemplate> targets = new HashMap<>();
    public Map<String, KeyframeAnimation> keyframeAnimations = new HashMap<>();

    public void normalize()
    {
        if (targets == null)
        {
            targets = new HashMap<>();
        }

        if (keyframeAnimations == null)
        {
            keyframeAnimations = new HashMap<>();
        }
    }

    @Override
    public KeyframeAnimation getAnimation(String key)
    {
        normalize();
        return keyframeAnimations.get(key);
    }

}
