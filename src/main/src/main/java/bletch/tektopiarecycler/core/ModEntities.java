package bletch.tektopiarecycler.core;

import bletch.common.core.CommonEntities;
import bletch.tektopiarecycler.entities.EntityRecycler;
import bletch.tektopiarecycler.entities.render.RenderRecycler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public class ModEntities extends CommonEntities {

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
