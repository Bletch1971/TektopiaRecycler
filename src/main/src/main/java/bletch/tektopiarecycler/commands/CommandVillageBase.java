package bletch.tektopiarecycler.commands;

import java.util.Collections;
import java.util.List;

import bletch.tektopiarecycler.core.ModCommands;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.PermissionAPI;

public abstract class CommandVillageBase extends CommandBase {
	
	protected final String name;

	public CommandVillageBase(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
    public List<String> getAliases() {
        return Collections.singletonList(this.name);
    }

	@Override
	public String getUsage(ICommandSender sender) {
		return ModCommands.COMMAND_PREFIX + this.name + ".usage";
	}
	
	@Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		try {
			return PermissionAPI.hasPermission(super.getCommandSenderAsPlayer(sender), ModCommands.COMMAND_PREFIX_WITH_MODID + this.getName());
		} catch (PlayerNotFoundException ex) {
			ex.printStackTrace();
			return false;
		}
    }    
	
	@Override
	public int getRequiredPermissionLevel() {
		return 4;
    }
}
