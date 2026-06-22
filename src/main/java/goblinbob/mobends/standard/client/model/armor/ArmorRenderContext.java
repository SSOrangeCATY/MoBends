package goblinbob.mobends.standard.client.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import goblinbob.mobends.standard.client.model.armor.tier.RenderTier;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class ArmorRenderContext<E extends LivingEntity>
{
    private static final int DEFAULT_LEATHER_COLOR = 0xFFA06540;

    private final E entity;
    private final BipedEntityData<?> entityData;
    private final EquipmentSlot slot;
    private final ItemStack armorStack;
    private final PoseStack poseStack;
    private final Object bufferSource;
    private final int packedLight;
    private final int packedOverlay;
    private final float partialTicks;
    @Nullable
    private final HumanoidModel<?> armorModel;
    @Nullable
    private RenderTier determinedTier;
    private final boolean isBaby;
    private final boolean isSlimArms;

    private ArmorRenderContext(Builder<E> builder)
    {
        this.entity = builder.entity;
        this.entityData = builder.entityData;
        this.slot = builder.slot;
        this.armorStack = builder.armorStack;
        this.poseStack = builder.poseStack;
        this.bufferSource = builder.bufferSource;
        this.packedLight = builder.packedLight;
        this.packedOverlay = builder.packedOverlay;
        this.partialTicks = builder.partialTicks;
        this.armorModel = builder.armorModel;
        this.determinedTier = builder.determinedTier;
        this.isBaby = builder.entity != null && builder.entity.isBaby();
        this.isSlimArms = detectSlimArms(builder.entity);
    }

    private static <E extends LivingEntity> boolean detectSlimArms(E entity)
    {
        if (entity instanceof net.minecraft.client.player.AbstractClientPlayer player)
        {
            return player.getSkin().model() == PlayerModelType.SLIM;
        }
        return false;
    }

    public E getEntity()
    {
        return entity;
    }

    public BipedEntityData<?> getEntityData()
    {
        return entityData;
    }

    public EquipmentSlot getSlot()
    {
        return slot;
    }

    public ItemStack getArmorStack()
    {
        return armorStack;
    }

    public PoseStack getPoseStack()
    {
        return poseStack;
    }

    public Object getBufferSource()
    {
        return bufferSource;
    }

    public int getPackedLight()
    {
        return packedLight;
    }

    public int getPackedOverlay()
    {
        return packedOverlay;
    }

    public float getPartialTicks()
    {
        return partialTicks;
    }

    @Nullable
    public HumanoidModel<?> getArmorModel()
    {
        return armorModel;
    }

    @Nullable
    public RenderTier getDeterminedTier()
    {
        return determinedTier;
    }

    public boolean isBaby()
    {
        return isBaby;
    }

    public boolean isSlimArms()
    {
        return isSlimArms;
    }

    public float getEntityScale()
    {
        return isBaby ? 0.5f : 1.0f;
    }

    public boolean isLimbSlot()
    {
        return slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
    }

    public boolean isArmSlot()
    {
        return slot == EquipmentSlot.CHEST;
    }

    public int getArmorColor()
    {
        if (armorStack == null || armorStack.isEmpty())
        {
            return 0xFFFFFFFF;
        }

        DyedItemColor dyedColor = armorStack.get(DataComponents.DYED_COLOR);
        if (dyedColor != null)
        {
            return 0xFF000000 | dyedColor.rgb();
        }

        return 0xFFFFFFFF;
    }

    public boolean hasDyedColor()
    {
        return armorStack != null && armorStack.has(DataComponents.DYED_COLOR);
    }

    public static <E extends LivingEntity> Builder<E> builder()
    {
        return new Builder<>();
    }

    public static class Builder<E extends LivingEntity>
    {
        private E entity;
        private BipedEntityData<?> entityData;
        private EquipmentSlot slot;
        private ItemStack armorStack;
        private PoseStack poseStack;
        private Object bufferSource;
        private int packedLight;
        private int packedOverlay;
        private float partialTicks;
        private HumanoidModel<?> armorModel;
        private RenderTier determinedTier;

        public Builder<E> entity(E entity)
        {
            this.entity = entity;
            return this;
        }

        public Builder<E> entityData(BipedEntityData<?> entityData)
        {
            this.entityData = entityData;
            return this;
        }

        public Builder<E> slot(EquipmentSlot slot)
        {
            this.slot = slot;
            return this;
        }

        public Builder<E> armorStack(ItemStack armorStack)
        {
            this.armorStack = armorStack;
            return this;
        }

        public Builder<E> poseStack(PoseStack poseStack)
        {
            this.poseStack = poseStack;
            return this;
        }

        public Builder<E> bufferSource(Object bufferSource)
        {
            this.bufferSource = bufferSource;
            return this;
        }

        public Builder<E> packedLight(int packedLight)
        {
            this.packedLight = packedLight;
            return this;
        }

        public Builder<E> packedOverlay(int packedOverlay)
        {
            this.packedOverlay = packedOverlay;
            return this;
        }

        public Builder<E> partialTicks(float partialTicks)
        {
            this.partialTicks = partialTicks;
            return this;
        }

        public Builder<E> armorModel(HumanoidModel<?> armorModel)
        {
            this.armorModel = armorModel;
            return this;
        }

        public Builder<E> determinedTier(RenderTier determinedTier)
        {
            this.determinedTier = determinedTier;
            return this;
        }

        public ArmorRenderContext<E> build()
        {
            return new ArmorRenderContext<>(this);
        }
    }
}
