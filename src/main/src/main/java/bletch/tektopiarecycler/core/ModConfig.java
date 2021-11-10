package bletch.tektopiarecycler.core;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;

@Config(modid = ModDetails.MOD_ID, category = "")
@ParametersAreNonnullByDefault
public class ModConfig {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent event) {

        if (event.getModID().equals(ModDetails.MOD_ID)) {
            ConfigManager.sync(ModDetails.MOD_ID, Type.INSTANCE);
        }

    }

    @Config.LangKey("config.debug")
    public static final Debug debug = new Debug();

    @Config.LangKey("config.recycler")
    public static final Recycler recycler = new Recycler();

    public static class Debug {

        @Config.Comment("If true, debug information will be output to the console.")
        @Config.LangKey("config.debug.enableDebug")
        public boolean enableDebug = false;

    }

    public static class Recycler {

        @Config.Comment("The percentage of emeralds returned per recycle, based on the token cost. Default: 50")
        @Config.LangKey("config.recycler.recyclecostpercentage")
        @Config.RangeInt(min = 1, max = 100)
        public int recyclecostpercentage = 50;

        @Config.Comment("The number of recycles that can be made for each item per day. Default: 5")
        @Config.LangKey("config.recycler.recyclesperday")
        @Config.RangeInt(min = 1, max = 99999)
        public int recyclesperday = 5;

        @Config.Comment("If enabled, when trying to spawn a recycler it will check the size of the village. The more villagers the less often the recycler will spawn. Default: True")
        @Config.LangKey("config.recycler.checksvillagesize")
        public Boolean checksVillageSize = true;
    }

}
