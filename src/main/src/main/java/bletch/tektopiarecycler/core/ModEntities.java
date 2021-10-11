package bletch.tektopiarecycler.core;

import bletch.tektopiarecycler.entities.EntityRecycler;
import bletch.tektopiarecycler.entities.render.RenderRecycler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public class ModEntities {
	
	public static final String ANIMATION_VILLAGER_EAT = "villager_eat";
	public static final String ANIMATION_VILLAGER_READ = "villager_read";
	public static final String ANIMATION_VILLAGER_RUN = "villager_run";
	public static final String ANIMATION_VILLAGER_SIT = "villager_sit";
	public static final String ANIMATION_VILLAGER_SITCHEER = "villager_sit_cheer";
	public static final String ANIMATION_VILLAGER_SLEEP = "villager_sleep";
	public static final String ANIMATION_VILLAGER_WALK = "villager_walk";
	public static final String ANIMATION_VILLAGER_WALKSAD = "villager_walk_sad";
	
	public static void register(IForgeRegistry<EntityEntry> registry) {
		int id = 1;
		
		registry.register(EntityEntryBuilder.create()
			    .entity(EntityRecycler.class)
			    .id(new ResourceLocation(ModDetails.MOD_ID, EntityRecycler.RESOURCE_PATH), id++)
			    .name(EntityRecycler.ENTITY_NAME)
			    .egg(2697513, 7494986)
			    .tracker(128, 1, true)
			    .build()
	    );
	}
    
	@SideOnly(Side.CLIENT)
	public static void registerModels() {
		RenderingRegistry.registerEntityRenderingHandler(EntityRecycler.class, RenderRecycler.FACTORY);
	}

}
