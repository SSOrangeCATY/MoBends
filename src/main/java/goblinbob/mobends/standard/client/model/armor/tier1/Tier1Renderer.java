package goblinbob.mobends.standard.client.model.armor.tier1;

import goblinbob.mobends.standard.client.model.armor.ArmorRenderContext;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Tier1Renderer
{
    public <T extends LivingEntity> boolean render(ArmorRenderContext<T> context, HumanoidModel<?> armorModel)
    {
        return false;
    }

    public <T extends LivingEntity> boolean renderWithTexture(
            ArmorRenderContext<T> context,
            HumanoidModel<?> armorModel,
            Identifier texture,
            boolean hasFoil)
    {
        return false;
    }
}
