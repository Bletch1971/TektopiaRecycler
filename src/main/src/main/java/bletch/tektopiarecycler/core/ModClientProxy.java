package bletch.tektopiarecycler.core;

import bletch.tektopiarecycler.entities.EntityRecycler;
import bletch.tektopiarecycler.utils.LoggerUtils;
import com.leviathanstudio.craftstudio.client.registry.CSRegistryHelper;
import com.leviathanstudio.craftstudio.client.util.EnumRenderType;
import com.leviathanstudio.craftstudio.client.util.EnumResourceType;
import net.minecraft.client.Minecraft;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;

@ParametersAreNonnullByDefault
public class ModClientProxy extends ModCommonProxy {

    protected CSRegistryHelper registry = new CSRegistryHelper(ModDetails.MOD_ID);

    @Override
    public boolean isRemote() {
        return true;
    }

    @Override
    public File getMinecraftDirectory() {
        return Minecraft.getMinecraft().mcDataDir;
    }

    @Override
    public void registerCraftStudioAnimations() {
        super.registerCraftStudioAnimations();

        LoggerUtils.info("Starting registerCraftStudioAnimations...");

        this.registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, ModEntities.ANIMATION_VILLAGER_EAT);
        this.registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, ModEntities.ANIMATION_VILLAGER_READ);
        this.registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, ModEntities.ANIMATION_VILLAGER_RUN);
        this.registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, ModEntities.ANIMATION_VILLAGER_SIT);
        this.registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, ModEntities.ANIMATION_VILLAGER_SITCHEER);
        this.registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, ModEntities.ANIMATION_VILLAGER_SLEEP);
        this.registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, ModEntities.ANIMATION_VILLAGER_WALK);
        this.registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, ModEntities.ANIMATION_VILLAGER_WALKSAD);

        LoggerUtils.info("Finished registerCraftStudioAnimations...");
    }

    @Override
    public void registerCraftStudioModels() {
        super.registerCraftStudioModels();

        LoggerUtils.info("Starting registerCraftStudioModels...");

        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, EntityRecycler.ANIMATION_MODEL_NAME);

        LoggerUtils.info("Finished registerCraftStudioModels...");
    }

}
