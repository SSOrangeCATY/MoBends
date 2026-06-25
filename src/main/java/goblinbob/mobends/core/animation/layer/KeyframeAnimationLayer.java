package goblinbob.mobends.core.animation.layer;

import goblinbob.mobends.core.animation.keyframe.Bone;
import goblinbob.mobends.core.animation.keyframe.Keyframe;
import goblinbob.mobends.core.animation.keyframe.KeyframeAnimation;
import goblinbob.mobends.core.animation.keyframe.KeyframeSampler;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.client.model.IModelPart;
import goblinbob.mobends.core.data.EntityData;

import java.util.Map;

public class KeyframeAnimationLayer<T extends EntityData<?>> extends AnimationLayer<T>
{
	public KeyframeAnimation performedAnimation;
	public float keyframeIndex = 0;
	
	public void playBit(KeyframeAnimation animation, T entityData)
	{
		this.performedAnimation = animation;
	}
	
	public void playOrContinueBit(KeyframeAnimation animation, T entityData)
	{
		if (!this.isPlaying(animation))
			this.playBit(animation, entityData);
	}
	
	public boolean isPlaying(KeyframeAnimation animation)
	{
		return animation == this.performedAnimation;
	}
	
	public boolean isPlaying()
	{
		return this.performedAnimation != null;
	}

	public void clearAnimation()
	{
		this.performedAnimation = null;
	}

	public KeyframeAnimation getPerformedBit()
	{
		return this.performedAnimation;
	}
	
	@Override
	public String[] getActions(T entityData)
	{
		return null;
	}
	
	@Override
	public void perform(T entityData)
	{
		if (this.performedAnimation != null)
		{
			int animationFrameCount = KeyframeSampler.maxFrameCount(this.performedAnimation);
			if (animationFrameCount <= 0)
				return;

			if (this.performedAnimation.bones.containsKey("root"))
			{
				Bone rootBone = this.performedAnimation.bones.get("root");
				KeyframeSampler.Sample sample = KeyframeSampler.sample(rootBone, keyframeIndex);
				if (sample == null)
					return;
				Keyframe keyframe = sample.current();
				Keyframe nextFrame = sample.next();
				float progress = sample.progress();

				float x = keyframe.position[0] + (nextFrame.position[0] - keyframe.position[0]) * progress;
				float y = keyframe.position[1] + (nextFrame.position[1] - keyframe.position[1]) * progress;
				float z = keyframe.position[2] + (nextFrame.position[2] - keyframe.position[2]) * progress;

				entityData.globalOffset.set(z, x, y);
			}

			for (Map.Entry<String, Bone> entry : this.performedAnimation .bones.entrySet())
			{
				Bone bone = entry.getValue();
				KeyframeSampler.Sample sample = KeyframeSampler.sample(bone, keyframeIndex);
				if (sample == null)
					continue;

				Object part = entityData.getPartForName(entry.getKey());

				Keyframe keyframe = sample.current();
				Keyframe nextFrame = sample.next();
				float progress = sample.progress();

				if (keyframe != null && nextFrame != null)
				{
					if (part instanceof IModelPart)
					{
						IModelPart box = (IModelPart) part;
						float x0 = keyframe.rotation[0];
						float y0 = keyframe.rotation[1];
						float z0 = keyframe.rotation[2];
						float w0 = keyframe.rotation[3];
						float x1 = nextFrame.rotation[0];
						float y1 = nextFrame.rotation[1];
						float z1 = nextFrame.rotation[2];
						float w1 = nextFrame.rotation[3];

						box.getRotation().set(x0 + (x1-x0) * progress,
								y0 + (y1-y0) * progress,
								z0 + (z1-z0) * progress,
								w0 + (w1-w0) * progress);
					}
				}
			}

			keyframeIndex += DataUpdateHandler.ticksPerFrame * 0.8F;
			float loopFrame = animationFrameCount - 1;
			if (loopFrame <= 0.0F)
			{
				keyframeIndex = 0.0F;
			}
			else
			{
				while (keyframeIndex >= loopFrame)
					keyframeIndex -= loopFrame;
			}
		}
	}
}
