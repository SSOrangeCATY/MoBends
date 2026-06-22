package goblinbob.mobends.standard.animation.bit.skeleton;

import goblinbob.mobends.standard.data.SkeletonData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;

public class WalkAnimationBit<E extends AbstractSkeleton> extends goblinbob.mobends.standard.animation.bit.biped.WalkAnimationBit<SkeletonData<E>>
{
	@Override
	public void perform(SkeletonData<E> data)
	{
		super.perform(data);

		if (data.isStrafing())
		{
			final float PI = (float) Math.PI;
			float limbSwing = data.limbSwing.get() * 0.6662F;

			float legSwingAmount = 0.7F * data.limbSwingAmount.get() / PI * 180F;
			data.rightLeg.rotation.setSmoothness(1.0F).orientZ(-5F + Mth.cos(limbSwing) * legSwingAmount);
			data.leftLeg.rotation.setSmoothness(1.0F).orientZ(-5F + Mth.cos(limbSwing + PI) * legSwingAmount);
		}
	}
}
