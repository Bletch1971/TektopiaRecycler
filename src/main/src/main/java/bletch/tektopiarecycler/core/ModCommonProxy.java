package bletch.tektopiarecycler.core;

import java.io.File;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@ParametersAreNonnullByDefault
public class ModCommonProxy {
	
	public boolean isRemote() {
		return false;
	}

	public File getMinecraftDirectory() {
		return FMLCommonHandler.instance().getMinecraftServerInstance().getFile("");
	}

	public void preInitialize(FMLPreInitializationEvent e) {
		MinecraftForge.EVENT_BUS.register(new ModConfig());
	}

	public void initialize(FMLInitializationEvent e) {
	}

	public void postInitialize(FMLPostInitializationEvent e) {
	}
	   
	public void registerCraftStudioAnimations() {
	}
	   
	public void registerCraftStudioModels() {
	}
	
}
