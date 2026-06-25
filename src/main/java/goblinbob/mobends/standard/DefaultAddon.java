package goblinbob.mobends.standard;

import goblinbob.mobends.core.addon.AddonAnimationRegistry;
import goblinbob.mobends.core.addon.IAddon;
import goblinbob.mobends.standard.client.model.armor.ArmorModelFactory;
import goblinbob.mobends.standard.client.renderer.entity.ArrowTrailManager;
import goblinbob.mobends.standard.client.renderer.entity.mutated.*;
import goblinbob.mobends.standard.data.*;
import goblinbob.mobends.standard.kumo.WolfStateCondition;
import goblinbob.mobends.standard.main.ModConfig;
import goblinbob.mobends.standard.mutators.*;
import goblinbob.mobends.standard.previewer.BipedPreviewer;
import goblinbob.mobends.standard.previewer.PlayerPreviewer;
import goblinbob.mobends.standard.previewer.SpiderPreviewer;
import goblinbob.mobends.standard.previewer.ZombiePreviewer;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
import net.minecraft.world.entity.animal.squid.Squid;
import net.minecraft.world.entity.animal.wolf.Wolf;

public class DefaultAddon implements IAddon
{
	@Override
	public void registerContent(AddonAnimationRegistry registry)
	{
		registry.registerEntity(new PlayerBender());

		registry.registerNewEntity("minecraft:zombie", "entity.minecraft.zombie", Zombie.class, ZombieData::new, ZombieMutator::new, new ZombieRenderer<>(),
				new ZombiePreviewer(),
				"head", "body", "leftArm", "rightArm", "leftForeArm", "rightForeArm",
				"leftLeg", "rightLeg", "leftForeLeg", "rightForeLeg");

		registry.registerNewEntity("minecraft:skeleton", "entity.minecraft.skeleton", Skeleton.class, SkeletonData::new, SkeletonMutator::new, new BipedRenderer<>(),
				"head", "body", "leftArm", "rightArm", "leftForeArm", "rightForeArm", "leftLeg",
						"rightLeg", "leftForeLeg", "rightForeLeg");

//		registry.registerNewEntity(ZombieVillager.class, ZombieVillagerData::new, ZombieVillagerMutator::new, new ZombieRenderer<>(),
//				new BipedPreviewer<>(),
//				"head", "body", "leftArm", "rightArm", "leftForeArm", "rightForeArm",
//				"leftLeg", "rightLeg", "leftForeLeg", "rightForeLeg");
//
		registry.registerNewEntity("minecraft:zombified_piglin", "entity.minecraft.zombified_piglin", ZombifiedPiglin.class, PigZombieData::new, PigZombieMutator::new, new ZombieRenderer<>(),
				new BipedPreviewer<>(),
				"head", "body", "leftArm", "rightArm", "leftForeArm", "rightForeArm",
				"leftLeg", "rightLeg", "leftForeLeg", "rightForeLeg");

		registry.registerNewEntity("minecraft:spider", "entity.minecraft.spider", Spider.class, SpiderData::new, SpiderMutator::new, new SpiderRenderer<>(),
				new SpiderPreviewer(),
				"head", "body", "neck", "leg1", "leg2", "leg3", "leg4", "leg5", "leg6", "leg7", "leg8",
				"foreLeg1", "foreLeg2", "foreLeg3", "foreLeg4", "foreLeg5", "foreLeg6", "foreLeg7", "foreLeg8");

		registry.registerNewEntity("minecraft:squid", "entity.minecraft.squid", Squid.class, SquidData::new, SquidMutator::new, new SquidRenderer<>(),
				"body", "tentacle1", "tentacle2", "tentacle3", "tentacle4", "tentacle5", "tentacle6", "tentacle7", "tentacle8");

		registry.registerNewEntity("minecraft:wolf", "entity.minecraft.wolf", Wolf.class, WolfData::new, WolfMutator::new, new WolfRenderer<>(),
				"wolfHeadMain", "wolfBody", "wolfLeg1", "wolfLeg2", "wolfLeg3", "wolfLeg4", "wolfTail", "wolfMane");



//		registry.registerEntity(new AnimatedEntity(EntityHusk.class,
//						new RenderBendsHusk(Minecraft.getMinecraft().getRenderManager()),
//						new String[] { "head", "body", "leftArm", "rightArm", "leftForeArm", "rightForeArm", "leftLeg",
//								"rightLeg", "leftForeLeg", "rightForeLeg" }));

//		registry.registerEntity(new AnimatedEntity(Skeleton.class,
//						new RenderBendsSkeleton(Minecraft.getMinecraft().getRenderManager()),
//						new String[] { "head", "body", "leftArm", "rightArm", "leftForeArm", "rightForeArm", "leftLeg",
//								"rightLeg", "leftForeLeg", "rightForeLeg" }));

//		registry.registerEntity(new AnimatedEntity(WitherSkeleton.class,
//						new RenderBendsWitherSkeleton(Minecraft.getMinecraft().getRenderManager()),
//						new String[] { "head", "body", "leftArm", "rightArm", "leftForeArm", "rightForeArm", "leftLeg",
//								"rightLeg", "leftForeLeg", "rightForeLeg" }));

//		registry.registerEntity(new AnimatedEntity(Stray.class,
//						new RenderBendsStray(Minecraft.getMinecraft().getRenderManager()),
//						new String[] { "head", "body", "leftArm", "rightArm", "leftForeArm", "rightForeArm", "leftLeg",
//								"rightLeg", "leftForeLeg", "rightForeLeg" }));

		registry.registerTriggerCondition("wolf_state", WolfStateCondition::new, WolfStateCondition.Template.class);
	}

	@Override
	public void onRenderTick(float partialTicks)
	{
		if (ModConfig.showArrowTrails)
			ArrowTrailManager.onRenderTick();
		PlayerPreviewer.updatePreviewData(partialTicks);
	}

	@Override
	public void onClientTick()
	{
		PlayerPreviewer.updatePreviewDataClient();
	}

	@Override
	public void onRefresh()
	{
		ArmorModelFactory.refresh();
	}

	@Override
	public String getDisplayName()
	{
		return "Default";
	}
}
