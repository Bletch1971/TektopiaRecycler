package bletch.tektopiarecycler.schedulers;

import bletch.common.schedulers.IScheduler;
import bletch.common.utils.TektopiaUtils;
import bletch.common.utils.TextUtils;
import bletch.tektopiarecycler.core.ModConfig;
import bletch.tektopiarecycler.entities.EntityRecycler;
import bletch.tektopiarecycler.utils.LoggerUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;

import java.util.List;

public class RecyclerScheduler implements IScheduler {

    protected Boolean checkedVillages = false;
    protected Boolean resetNight = false;

    @Override
    public void resetDay() {
    }

    @Override
    public void resetNight() {
        if (this.resetNight)
            return;

        LoggerUtils.info("RecyclerScheduler - resetNight called", true);

        // if it is nighttime, then clear the village checks
        this.checkedVillages = false;
        this.resetNight = true;
    }

    @Override
    public void update(World world) {
        // do not process any further if we have already performed the check, it is raining, or it is night
        if (this.checkedVillages || world == null || world.isRaining() || !EntityRecycler.isWorkTime(world, 0))
            return;

        LoggerUtils.info("RecyclerScheduler - update called", true);

        this.resetNight = false;
        this.checkedVillages = true;

        // get a list of the villages from the VillageManager
        List<Village> villages = TektopiaUtils.getVillages(world);
        if (villages == null || villages.isEmpty())
            return;

        // cycle through each village
        villages.forEach((v) -> {

            List<EntityRecycler> entityList = null;
            String villageName = v.getName();
            int villageLevel = TektopiaUtils.getVillageLevel(v);

            for (int recyclerType : EntityRecycler.getRecyclerTypes()) {

                // get the village level (1-5) and test to spawn - bigger villages will reduce the number of spawns of the Recycler.
                int villageCheck = ModConfig.recycler.checksVillageSize ? world.rand.nextInt(villageLevel) : 0;

                if (villageLevel > 0 && villageCheck == 0) {

                    LoggerUtils.info(TextUtils.translate("message.recycler.villagechecksuccess", villageName, villageLevel, villageCheck), true);

                    // get a list of the Recyclers in the village
                    if (entityList == null)
                        entityList = world.getEntitiesWithinAABB(EntityRecycler.class, v.getAABB().grow(Village.VILLAGE_SIZE));

                    long recyclerTypeCount = entityList.stream().filter((r) -> r.getRecyclerType() == recyclerType).count();

                    if (recyclerTypeCount == 0) {

                        BlockPos spawnPosition = TektopiaUtils.getVillageSpawnPoint(world, v);

                        // attempt spawn
                        if (TektopiaUtils.trySpawnEntity(world, spawnPosition, (World w) -> new EntityRecycler(w, recyclerType))) {
                            v.sendChatMessage(new TextComponentTranslation("message.recycler.spawned"));
                            LoggerUtils.info(TextUtils.translate("message.recycler.spawned.village", villageName, TektopiaUtils.formatBlockPos(spawnPosition)), true);
                        } else {
                            LoggerUtils.info(TextUtils.translate("message.recycler.noposition.village", villageName), true);
                        }

                    } else {
                        LoggerUtils.info(TextUtils.translate("message.recycler.exists", villageName), true);
                    }

                } else {
                    LoggerUtils.info(TextUtils.translate("message.recycler.villagecheckfailed", villageName, villageLevel, villageCheck), true);
                }
            }
        });
    }
}
