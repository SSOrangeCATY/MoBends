package goblinbob.mobends.standard.animation.controller;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.animation.controller.IAnimationController;
import goblinbob.mobends.core.animation.layer.HardAnimationLayer;
import goblinbob.mobends.standard.animation.bit.biped.JumpAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.StandAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.WalkAnimationBit;
import goblinbob.mobends.standard.animation.bit.zombie_base.ZombieLeanAnimationBit;
import goblinbob.mobends.standard.animation.bit.zombie_base.ZombieStumblingAnimationBit;
import goblinbob.mobends.standard.data.DrownedData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DrownedController implements IAnimationController<DrownedData>
{
    protected HardAnimationLayer<DrownedData> layerBase;
    protected HardAnimationLayer<DrownedData> layerSet;
    protected AnimationBit<DrownedData> bitStand, bitWalk, bitJump;
    protected AnimationBit<DrownedData>[] bitAnimationSet;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public DrownedController()
    {
        this.layerBase = new HardAnimationLayer<>();
        this.layerSet = new HardAnimationLayer<>();
        this.bitStand = new StandAnimationBit<>();
        this.bitWalk = new WalkAnimationBit<>();
        this.bitJump = new JumpAnimationBit<>();
        this.bitAnimationSet = new AnimationBit[] {
            new ZombieLeanAnimationBit(),
            new ZombieStumblingAnimationBit()
        };
    }

    @Override
    public Collection<String> perform(DrownedData drownedData)
    {
        if (!drownedData.isOnGround() || drownedData.getTicksAfterTouchdown() < 1)
        {
            this.layerBase.playOrContinueBit(bitJump, drownedData);
        }
        else
        {
            if (drownedData.isStillHorizontally())
            {
                this.layerBase.playOrContinueBit(bitStand, drownedData);
            }
            else
            {
                this.layerBase.playOrContinueBit(bitWalk, drownedData);
            }
        }

        this.layerSet.playOrContinueBit(bitAnimationSet[drownedData.getAnimationSet()], drownedData);

        final List<String> actions = new ArrayList<>();
        this.layerBase.perform(drownedData, actions);
        this.layerSet.perform(drownedData, actions);
        return actions;
    }
}
