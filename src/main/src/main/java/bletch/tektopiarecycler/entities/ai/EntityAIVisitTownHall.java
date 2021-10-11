package bletch.tektopiarecycler.entities.ai;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIPatrolPoint;
import net.tangotek.tektopia.structures.VillageStructureTownHall;
import net.tangotek.tektopia.structures.VillageStructureType;

public class EntityAIVisitTownHall extends EntityAIPatrolPoint {
	protected final EntityVillagerTek entity;
	protected VillageStructureTownHall townHall;

	public EntityAIVisitTownHall(EntityVillagerTek entity, Predicate<EntityVillagerTek> shouldPred, int distanceFromPoint, int waitTime) {
		super(entity, shouldPred, distanceFromPoint, waitTime);
		this.entity = entity;
	}

	protected BlockPos getPatrolPoint() {
		List<?> townHalls = this.villager.getVillage().getStructures(VillageStructureType.TOWNHALL);
		if (!townHalls.isEmpty()) {
			Collections.shuffle(townHalls);
			this.townHall = (VillageStructureTownHall)townHalls.get(0);
		}
		
		return this.townHall != null ? this.townHall.getDoor() : null;
	}

	@Override
	public boolean shouldExecute() {
		return this.villager.isAITick() && this.navigator.hasVillage() && this.shouldPred.test(this.villager) ? super.func_75250_a() : false;
	}
}
