package bletch.tektopiarecycler.schedulers;

import java.util.List;

import bletch.tektopiarecycler.core.ModConfig;
import bletch.tektopiarecycler.entities.EntityRecycler;
import bletch.tektopiarecycler.utils.TektopiaUtils;
import bletch.tektopiarecycler.utils.LoggerUtils;
import bletch.tektopiarecycler.utils.TextUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;

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

		// if it is night time, then clear the village checks
		this.checkedVillages = false;
		this.resetNight = true;
	}

	@Override
	public void update(World world) {
		// do not process any further if we have already performed the check, it is raining or it is night
		if (this.checkedVillages || world == null || world.isRaining() || Village.isNightTime(world))
			return;
		
		this.resetNight = false;
		this.checkedVillages = true;

		// get a list of the villages from the VillageManager 
		List<Village> villages = TektopiaUtils.getVillages(world);
		if (villages == null || villages.isEmpty())
			return;

		// cycle through each village
		villages.forEach((v) -> {

			List<EntityRecycler> entityList = null;
			
			for (int recyclerType : EntityRecycler.getRecyclerTypes()) {
				
				// get the village level (1-5) and test to spawn - bigger villages will reduce the number of spawns of the Recycler.
				int villageLevel = ModConfig.recycler.checksVillageSize ? TektopiaUtils.getVillageLevel(v) : 1;
				int villageCheck = world.rand.nextInt(villageLevel);
				
				if (villageLevel > 0 && villageCheck == 0) {
					
					LoggerUtils.info(TextUtils.translate("message.trader.villagechecksuccess", new Object[] { villageLevel, villageCheck }), true);
					
					// get a list of the Recyclers in the village
					if (entityList == null)
						entityList = world.getEntitiesWithinAABB(EntityRecycler.class, v.getAABB().grow(Village.VILLAGE_SIZE));
					
					long recyclerTypeCount = entityList.stream().filter((r) -> r.getRecyclerType() == recyclerType).count();
					if (recyclerTypeCount == 0) {
						
						BlockPos spawnPosition = v.getEdgeNode();

						// attempt spawn
						if (TektopiaUtils.trySpawnEntity(world, spawnPosition, (World w) -> new EntityRecycler(w, recyclerType))) {
							v.sendChatMessage(new TextComponentTranslation("message.recycler.spawned", new Object[] { TektopiaUtils.formatBlockPos(spawnPosition) }));
							LoggerUtils.info(TextUtils.translate("message.recycler.spawned", new Object[] { TektopiaUtils.formatBlockPos(spawnPosition) }), true);
						} else {
							v.sendChatMessage(new TextComponentTranslation("message.recycler.noposition", new Object[0]));
							LoggerUtils.info(TextUtils.translate("message.recycler.noposition", new Object[0]), true);
						}
						
					} else {
						LoggerUtils.info(TextUtils.translate("message.recycler.exists", new Object[0]), true);
					}
					
				} else {
					LoggerUtils.info(TextUtils.translate("message.recycler.villagecheckfailed", new Object[] { villageLevel, villageCheck }), true);
				}
			}
		});
	}
}
