package goblinbob.mobends.standard.mutators;

import goblinbob.mobends.core.data.IEntityDataFactory;
import goblinbob.mobends.standard.data.ZombieData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.monster.zombie.ZombieModel;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.world.entity.monster.zombie.Zombie;

public class ZombieMutator extends ZombieMutatorBase<ZombieData, Zombie, ZombieRenderState, ZombieModel<ZombieRenderState>>
{

    public ZombieMutator(IEntityDataFactory<Zombie> dataFactory)
    {
        super(dataFactory);
    }

    @Override
    public void storeVanillaModel(ZombieModel<ZombieRenderState> model)
    {
        super.storeVanillaModel(model);
    }

    @Override
    public boolean shouldModelBeSkipped(EntityModel<?> model)
    {
        return !(model instanceof ZombieModel);
    }
}
