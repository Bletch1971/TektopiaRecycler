package bletch.tektopiarecycler.entities.ai;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIPatrolPoint;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;

public class EntityAIRecyclerPatrolPoint extends EntityAIPatrolPoint {
	protected final EntityVillagerTek entity;
	protected VillageStructure structure;

	public EntityAIRecyclerPatrolPoint(EntityVillagerTek entity, Predicate<EntityVillagerTek> shouldPred, int distanceFromPoint, int waitTime) {
		super(entity, shouldPred, distanceFromPoint, waitTime);
		this.entity = entity;
	}

	protected BlockPos getPatrolPoint() {
		List<VillageStructure> structures = null;
		
		structures = this.villager.getVillage().getStructures(VillageStructureType.BLACKSMITH);
		if (structures.isEmpty())
			structures = this.villager.getVillage().getStructures(VillageStructureType.MERCHANT_STALL);
		
		if (structures.isEmpty())
			return null;
		
		Collections.shuffle(structures);
		this.structure = (VillageStructure)structures.get(0);
		return this.structure != null ? this.structure.getDoor() : null;
	}

	@Override
	public boolean shouldExecute() {
		return this.villager.isAITick() && this.navigator.hasVillage() && this.shouldPred.test(this.villager) ? super.func_75250_a() : false;
	}
}
