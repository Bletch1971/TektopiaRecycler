package bletch.tektopiarecycler.commands;

import java.util.List;
import bletch.tektopiarecycler.core.ModCommands;
import bletch.tektopiarecycler.entities.EntityRecycler;
import bletch.tektopiarecycler.utils.LoggerUtils;
import bletch.tektopiarecycler.utils.TektopiaUtils;
import bletch.tektopiarecycler.utils.TextUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillageManager;

public class CommandSpawn extends RecyclerCommandBase {

	private static final String COMMAND_NAME = "spawn";
	
	public CommandSpawn() {
		super(COMMAND_NAME);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length != 1) {
			throw new WrongUsageException(ModCommands.COMMAND_PREFIX + COMMAND_NAME + ".usage", new Object[0]);
		} 
		
		int argValue = 0;
		try {
			argValue = Integer.parseInt(args[0]);
			
			if (!EntityRecycler.isRecyclerTypeValid(argValue)) {
				throw new WrongUsageException(ModCommands.COMMAND_PREFIX + COMMAND_NAME + ".usage", new Object[0]);
			}
		}
		catch (Exception ex) {
			throw new WrongUsageException(ModCommands.COMMAND_PREFIX + COMMAND_NAME + ".usage", new Object[0]);
		}
        final int recyclerType = argValue;
		
		EntityPlayer entityPlayer = super.getCommandSenderAsPlayer(sender);
		World world = entityPlayer != null ? entityPlayer.getEntityWorld() : null;
		
		if (world == null || world.isRaining() || Village.isNightTime(world)) {
			notifyCommandListener(sender, this, ModCommands.COMMAND_PREFIX + COMMAND_NAME + ".badconditions", new Object[0]);
			LoggerUtils.info(TextUtils.translate(ModCommands.COMMAND_PREFIX + COMMAND_NAME + ".badconditions", new Object[0]), true);
			return;
		}
		
		VillageManager villageManager = world != null ? VillageManager.get(world) : null;
		Village village = villageManager != null && entityPlayer != null ? villageManager.getVillageAt(entityPlayer.getPosition()) : null;
		if (village == null) {
			notifyCommandListener(sender, this, ModCommands.COMMAND_PREFIX + COMMAND_NAME + ".novillage", new Object[0]);
			LoggerUtils.info(TextUtils.translate(ModCommands.COMMAND_PREFIX + COMMAND_NAME + ".novillage", new Object[0]), true);
			return;
		}

		BlockPos spawnPosition = village.getEdgeNode();
		if (spawnPosition == null) {
			notifyCommandListener(sender, this, ModCommands.COMMAND_PREFIX + COMMAND_NAME + ".noposition", new Object[0]);
			LoggerUtils.info(TextUtils.translate(ModCommands.COMMAND_PREFIX + COMMAND_NAME + ".noposition", new Object[0]), true);
			return;
		}

        List<EntityRecycler> entityList = world.getEntitiesWithinAABB(EntityRecycler.class, village.getAABB().grow(Village.VILLAGE_SIZE));
		long recyclerTypeCount = entityList.stream().filter((r) -> r.getRecyclerType() == recyclerType).count();
        if (recyclerTypeCount > 0) {
			notifyCommandListener(sender, this, ModCommands.COMMAND_PREFIX + COMMAND_NAME + ".exists", new Object[0]);
			LoggerUtils.info(TextUtils.translate(ModCommands.COMMAND_PREFIX + COMMAND_NAME + ".exists", new Object[0]), true);
			return;
        }
        
		// attempt to spawn the recycler
		Boolean entitySpawned = TektopiaUtils.trySpawnEntity(world, spawnPosition, (World w) -> new EntityRecycler(w, recyclerType));
		
		if (!entitySpawned) {
			notifyCommandListener(sender, this, ModCommands.COMMAND_PREFIX + COMMAND_NAME + ".failed", new Object[0]);
			LoggerUtils.info(TextUtils.translate(ModCommands.COMMAND_PREFIX + COMMAND_NAME + ".failed", new Object[0]), true);
			return;
		}
		
		notifyCommandListener(sender, this, ModCommands.COMMAND_PREFIX + COMMAND_NAME + ".success", new Object[] { TektopiaUtils.formatBlockPos(spawnPosition) });
		LoggerUtils.info(TextUtils.translate(ModCommands.COMMAND_PREFIX + COMMAND_NAME + ".success", new Object[] { TektopiaUtils.formatBlockPos(spawnPosition) }), true);
	}
    
}
