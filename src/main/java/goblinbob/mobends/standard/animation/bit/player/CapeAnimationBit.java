package goblinbob.mobends.standard.animation.bit.player;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.standard.data.PlayerData;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;

public class CapeAnimationBit extends AnimationBit<PlayerData>
{

    @Override
    public String[] getActions(PlayerData entityData)
    {
        return null;
    }

    @Override
    public void perform(PlayerData data)
    {
        final AbstractClientPlayer player = data.getEntity();

        data.cape.rotation.orientX(0.0F);

        final double partialTicks = DataUpdateHandler.partialTicks;
        double f = player.yBodyRotO + (player.yBodyRot - player.yBodyRotO) * partialTicks;
        double d3 = Math.sin(f * 0.017453292);
        double d4 = -Math.cos(f * 0.017453292);
        double motionX = player.getX() - player.xo;
        double motionY = player.getY() - player.yo;
        double motionZ = player.getZ() - player.zo;
        double f1 = Mth.clamp(motionY * 10.0, -6.0F, 32.0F);
        float f2 = (float)(motionX * d3 + motionZ * d4) * 100.0F;
        float f3 = (float)(motionX * d4 - motionZ * d3) * 100.0F;

        if (f2 < 0.0F)
        {
            f2 = 0.0F;
        }

        if (player.isCrouching())
        {
            f1 += 25.0F;
        }

        if (data.isFlying() && player.isSprinting())
        {
            data.cape.rotation.setSmoothness(0.5F).orientX(0.0F);

            data.setCapeWaveSpeed(4.0F);
        }
        else
        {
            data.cape.rotation.setSmoothness(0.5F).orientX((float) (6.0F + f2 / 2.0F + f1));
            data.cape.rotation.rotateZ(f3 / 2.0F);
            data.cape.rotation.rotateY(-f3 / 2.0F);

            data.setCapeWaveSpeed(1.0F);
        }
    }

}
