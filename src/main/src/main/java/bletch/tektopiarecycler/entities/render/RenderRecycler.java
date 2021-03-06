package bletch.tektopiarecycler.entities.render;

import bletch.common.entities.renders.RenderVillager;
import bletch.tektopiarecycler.core.ModDetails;
import bletch.tektopiarecycler.entities.EntityRecycler;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderRecycler<T extends EntityRecycler> extends RenderVillager<T> {
    public static final RenderRecycler.Factory<EntityRecycler> FACTORY;

    public RenderRecycler(RenderManager manager) {
        super(manager, ModDetails.MOD_ID, EntityRecycler.MODEL_NAME, false, 64, 64, EntityRecycler.MODEL_NAME);
    }

    public static class Factory<T extends EntityRecycler> implements IRenderFactory<T> {
        public Render<? super T> createRenderFor(RenderManager manager) {
            return new RenderRecycler<EntityRecycler>(manager);
        }
    }

    static {
        FACTORY = new RenderRecycler.Factory<>();
    }

}
