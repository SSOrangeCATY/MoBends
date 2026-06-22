package goblinbob.mobends.standard.client.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import goblinbob.mobends.standard.client.model.armor.tier.RenderTier;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArmorRenderingFacade
{
    private long tier1Count = 0;
    private long tier2Count = 0;
    private long fallbackCount = 0;
    private boolean debugMode = false;
    private RenderTier forcedTier = null;

    public <T extends LivingEntity> boolean render(ArmorRenderContext<T> context, Model armorModel)
    {
        fallbackCount++;
        return false;
    }

    public <T extends LivingEntity> boolean renderArmor(
            PoseStack poseStack,
            Object bufferSource,
            int packedLight,
            T entity,
            EquipmentSlot slot,
            ItemStack armorStack,
            Object armorItem,
            HumanoidModel<?> armorModel,
            BipedEntityData<?> entityData,
            Identifier texture)
    {
        fallbackCount++;
        return false;
    }

    public void setDebugMode(boolean debugMode)
    {
        this.debugMode = debugMode;
    }

    public boolean isDebugMode()
    {
        return debugMode;
    }

    public void forceTier(RenderTier tier)
    {
        this.forcedTier = tier;
    }

    public void clearForcedTier()
    {
        this.forcedTier = null;
    }

    public RenderTier getForcedTier()
    {
        return forcedTier;
    }

    public long getTier1Count()
    {
        return tier1Count;
    }

    public long getTier2Count()
    {
        return tier2Count;
    }

    public long getFallbackCount()
    {
        return fallbackCount;
    }

    public void resetStats()
    {
        tier1Count = 0;
        tier2Count = 0;
        fallbackCount = 0;
    }

    public String getStatsSummary()
    {
        long total = tier1Count + tier2Count + fallbackCount;
        if (total == 0)
        {
            return "No armor renders recorded";
        }

        return String.format("Armor Rendering Stats - Tier1: %d (%.1f%%), Tier2: %d (%.1f%%), Fallback: %d (%.1f%%)",
                tier1Count, tier1Count * 100.0 / total,
                tier2Count, tier2Count * 100.0 / total,
                fallbackCount, fallbackCount * 100.0 / total);
    }
}
