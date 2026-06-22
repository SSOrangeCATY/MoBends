package goblinbob.mobends.standard.animation.controller;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.animation.controller.IAnimationController;
import goblinbob.mobends.core.animation.layer.HardAnimationLayer;
import goblinbob.mobends.standard.animation.bit.biped.item.BipedActionController;
import goblinbob.mobends.standard.animation.bit.biped.JumpAnimationBit;
import goblinbob.mobends.standard.animation.bit.skeleton.StandAnimationBit;
import goblinbob.mobends.standard.animation.bit.skeleton.WalkAnimationBit;
import goblinbob.mobends.standard.data.BipedEntityData;
import goblinbob.mobends.standard.data.SkeletonData;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This is an animation controller for a skeleton instance.
 * It's a part of the EntityData structure.
 * 
 * @author Iwo Plaza
 *
 */
public class SkeletonController<E extends AbstractSkeleton> implements IAnimationController<SkeletonData<E>>
{
	protected HardAnimationLayer<BipedEntityData<E>> layerBase;
	protected AnimationBit<? extends BipedEntityData<E>> bitStand, bitWalk, bitJump;

	protected final BipedActionController actionController = new BipedActionController();

	public SkeletonController()
	{
		this.layerBase = new HardAnimationLayer<>();

		this.bitStand = new StandAnimationBit();
		this.bitWalk = new WalkAnimationBit();
		this.bitJump = new JumpAnimationBit<>();
	}

	public void performActionAnimations(SkeletonData<E> data, E skeleton)
	{
		final HumanoidArm primaryHand = skeleton.getMainArm();
		final ItemStack heldItemMainhand = skeleton.getMainHandItem();
		final ItemStack heldItemOffhand = skeleton.getOffhandItem();
		final Item activeItem = skeleton.getUseItem().getItem();

		actionController.perform(data, primaryHand, heldItemMainhand, heldItemOffhand, activeItem);
	}

	@Override
	public Collection<String> perform(SkeletonData<E> skeletonData)
	{
		E skeleton = skeletonData.getEntity();
		
		if (!skeletonData.isOnGround() || skeletonData.getTicksAfterTouchdown() < 1)
		{
			this.layerBase.playOrContinueBit(bitJump, skeletonData);
		}
		else
		{
			if (skeletonData.isStillHorizontally())
			{
				this.layerBase.playOrContinueBit(bitStand, skeletonData);
			}
			else
			{
				this.layerBase.playOrContinueBit(bitWalk, skeletonData);
			}
		}


		List<String> actions = new ArrayList<>();
		this.layerBase.perform(skeletonData, actions);
		this.performActionAnimations(skeletonData, skeleton);
		return actions;
	}
}
