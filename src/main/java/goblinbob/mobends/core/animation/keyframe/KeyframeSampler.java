package goblinbob.mobends.core.animation.keyframe;

public final class KeyframeSampler
{
    private KeyframeSampler()
    {
    }

    public static Sample sample(Bone bone, float keyframeIndex)
    {
        if (bone == null || bone.keyframes == null || bone.keyframes.isEmpty())
        {
            return null;
        }

        int lastFrame = bone.keyframes.size() - 1;
        if (lastFrame == 0)
        {
            Keyframe frame = bone.keyframes.get(0);
            return new Sample(frame, frame, 0.0F);
        }

        float clampedIndex = Math.max(0.0F, Math.min(keyframeIndex, lastFrame));
        int currentIndex = (int) clampedIndex;
        int nextIndex = Math.min(currentIndex + 1, lastFrame);
        float progress = currentIndex == nextIndex ? 0.0F : clampedIndex - currentIndex;

        return new Sample(
                bone.keyframes.get(currentIndex),
                bone.keyframes.get(nextIndex),
                progress);
    }

    public static int maxFrameCount(KeyframeAnimation animation)
    {
        if (animation == null || animation.bones == null)
        {
            return 0;
        }

        int maxFrameCount = 0;
        for (Bone bone : animation.bones.values())
        {
            if (bone != null && bone.keyframes != null)
            {
                maxFrameCount = Math.max(maxFrameCount, bone.keyframes.size());
            }
        }
        return maxFrameCount;
    }

    public record Sample(Keyframe current, Keyframe next, float progress)
    {
    }
}
