package goblinbob.mobends.core.mutators;


import goblinbob.mobends.core.data.IEntityDataFactory;
import goblinbob.mobends.core.data.LivingEntityData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface IMutatorFactory<E extends LivingEntity>
{

    Mutator<? extends LivingEntityData<E>, ? extends E, ? extends LivingEntityRenderState, ? extends EntityModel<?>> createMutator(
            IEntityDataFactory<E> dataFactory);

}
