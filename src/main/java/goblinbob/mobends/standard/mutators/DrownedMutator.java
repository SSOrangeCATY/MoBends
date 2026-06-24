package goblinbob.mobends.standard.mutators;

import goblinbob.mobends.core.data.IEntityDataFactory;
import goblinbob.mobends.standard.data.DrownedData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.monster.zombie.DrownedModel;
import net.minecraft.world.entity.monster.zombie.Drowned;

public class DrownedMutator extends ZombieMutatorBase<DrownedData, Drowned, DrownedModel>
{
    public DrownedMutator(IEntityDataFactory<Drowned> dataFactory)
    {
        super(dataFactory);
    }

    @Override
    public void storeVanillaModel(DrownedModel model)
    {
        super.storeVanillaModel(model);
    }

    @Override
    public boolean shouldModelBeSkipped(EntityModel<?> model)
    {
        return !(model instanceof DrownedModel);
    }
}
