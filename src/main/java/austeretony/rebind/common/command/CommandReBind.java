package austeretony.rebind.common.command;

import austeretony.rebind.client.keybinding.KeyBindingProperty;
import austeretony.rebind.common.config.ConfigLoader;
import austeretony.rebind.common.core.ReBindHooks;
import austeretony.rebind.common.main.EnumChatMessages;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

public class CommandReBind extends CommandBase {

    public static final String 
    NAME = "rebind", 
    USAGE = "/rebind <arg>, type </rebind help> for available arguments.";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return USAGE;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EnumCommandReBindArgs arg;
        if (args.length != 1 || (arg = EnumCommandReBindArgs.get(args[0])) == null)        
            throw new WrongUsageException(this.getUsage(sender));   
        switch(arg) {
        case HELP:
            EnumChatMessages.COMMAND_REBIND_HELP.showMessage();
            break;
        case LIST:
            if (!this.validAction(false)) break;
            EnumChatMessages.COMMAND_REBIND_LIST.showMessage();
            break;
        case UPDATE:
            if (!this.validAction(true)) break;
            ConfigLoader.updateSettingsFile();
            EnumChatMessages.COMMAND_REBIND_UPDATE.showMessage();
            break;
        }
    }

    private boolean validAction(boolean checkExternalConfig) {
        ReBindHooks.sortKeyBindings(); 
        if (KeyBindingProperty.UNKNOWN.isEmpty()) {
            EnumChatMessages.COMMAND_REBIND_ERR_NO_UNKNOWN_KEY_BINDINGS.showMessage();
            return false;
        } else if (checkExternalConfig && !ConfigLoader.isExternalConfigEnabled()) {
            EnumChatMessages.COMMAND_REBIND_ERR_EXTERNAL_CONFIG_DISABLED.showMessage();
            return false;
        }
        return true;
    }
}
