package bletch.tektopiarecycler.commands;

import bletch.tektopiarecycler.entities.EntityRecycler;
import bletch.tektopiarecycler.utils.LoggerUtils;
import bletch.tektopiarecycler.utils.TextUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillageManager;

import java.util.List;
import java.util.stream.Collectors;

public class CommandRecyclerKill extends CommandRecyclerBase {

    private static final String COMMAND_NAME = "kill";

    public CommandRecyclerKill() {
        super(COMMAND_NAME);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 1) {
            throw new WrongUsageException(RecyclerCommands.COMMAND_PREFIX + COMMAND_NAME + ".usage");
        }

        int argValue = -1;
        if (args.length > 0) {
            try {
                argValue = Integer.parseInt(args[0]);

                if (!EntityRecycler.isRecyclerTypeValid(argValue)) {
                    throw new WrongUsageException(RecyclerCommands.COMMAND_PREFIX + COMMAND_NAME + ".usage");
                }
            } catch (Exception ex) {
                throw new WrongUsageException(RecyclerCommands.COMMAND_PREFIX + COMMAND_NAME + ".usage");
            }
        }
        final int recyclerType = argValue;

        EntityPlayer entityPlayer = getCommandSenderAsPlayer(sender);
        World world = entityPlayer != null ? entityPlayer.getEntityWorld() : null;

        VillageManager villageManager = world != null ? VillageManager.get(world) : null;
        Village village = villageManager != null && entityPlayer != null ? villageManager.getVillageAt(entityPlayer.getPosition()) : null;
        if (village == null) {
            notifyCommandListener(sender, this, RecyclerCommands.COMMAND_PREFIX + COMMAND_NAME + ".novillage");
            LoggerUtils.info(TextUtils.translate(RecyclerCommands.COMMAND_PREFIX + COMMAND_NAME + ".novillage"), true);
            return;
        }

        List<EntityRecycler> entityList = world.getEntitiesWithinAABB(EntityRecycler.class, village.getAABB().grow(Village.VILLAGE_SIZE));
        if (recyclerType != -1) {
            entityList = entityList.stream()
                    .filter((r) -> r.getRecyclerType() == recyclerType)
                    .collect(Collectors.toList());
        }
        if (entityList.size() == 0) {
            notifyCommandListener(sender, this, RecyclerCommands.COMMAND_PREFIX + COMMAND_NAME + ".noexists");
            LoggerUtils.info(TextUtils.translate(RecyclerCommands.COMMAND_PREFIX + COMMAND_NAME + ".noexists"), true);
            return;
        }

        for (EntityRecycler entity : entityList) {
            if (entity.isDead)
                continue;

            entity.setDead();

            String name = (entity.isMale() ? TextFormatting.BLUE : TextFormatting.LIGHT_PURPLE) + entity.getName();

            notifyCommandListener(sender, this, RecyclerCommands.COMMAND_PREFIX + COMMAND_NAME + ".success", name);
            LoggerUtils.info(TextUtils.translate(RecyclerCommands.COMMAND_PREFIX + COMMAND_NAME + ".success", name), true);
        }
    }

}
