package bletch.tektopiarecycler.utils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillageManager;

public class TektopiaUtils {
    
	public static String formatBlockPos(BlockPos blockPos) {
    	if (blockPos == null) {
    		return "";
    	}
    	
    	return blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ();
    }

    public static List<Village> getVillages(World world) {
		if (world == null)
			return null;

		VillageManager villageManager = VillageManager.get(world);
		if (villageManager == null)
			return null;

		try {
			Field field = VillageManager.class.getDeclaredField("villages");
			if (field != null) {
				field.setAccessible(true);

				Object fieldValue = field.get(villageManager);
				if (fieldValue != null && fieldValue instanceof Set<?>) {
					return ((Set<?>)fieldValue).stream()
							.filter(v -> v instanceof Village)
							.map(v -> (Village)v)
							.filter(v -> v.isValid())
							.collect(Collectors.toList());
				}
			}
		}
		catch (Exception ex) {
			//do nothing if an error was encountered
		}

		return null;
	}

	public static int getVillageLevel(Village village) {
		if (village == null)
			return 0;

		int residentCount = village.getResidentCount();		
		return Math.max(Math.min(residentCount / 10, 5), 1);
	}

	public static Boolean trySpawnEntity(World world, BlockPos spawnPosition, Function<World, ?> createFunc) {
		if (world == null || spawnPosition == null || createFunc == null)
			return false;
		
		EntityLiving entity = (EntityLiving)createFunc.apply(world);
		if (entity == null)
			return false;
		
		entity.setLocationAndAngles((double)spawnPosition.getX() + 0.5D, (double)spawnPosition.getY(), (double)spawnPosition.getZ() + 0.5D, 0.0F, 0.0F);
		entity.onInitialSpawn(world.getDifficultyForLocation(spawnPosition), (IEntityLivingData)null);
		return world.spawnEntity(entity);
   }

}
