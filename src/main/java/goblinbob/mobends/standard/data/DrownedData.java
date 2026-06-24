package goblinbob.mobends.standard.data;

import goblinbob.mobends.standard.animation.controller.DrownedController;
import net.minecraft.world.entity.monster.zombie.Drowned;

public class DrownedData extends ZombieDataBase<Drowned>
{
    private final DrownedController controller = new DrownedController();

    public DrownedData(Drowned entity)
    {
        super(entity);
    }

    @Override
    public DrownedController getController()
    {
        return this.controller;
    }

    @Override
    public void onTicksRestart()
    {
        // No behaviour
    }
}
