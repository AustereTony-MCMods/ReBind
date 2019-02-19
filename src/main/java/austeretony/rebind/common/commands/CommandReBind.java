package austeretony.rebind.common.commands;

import austeretony.rebind.client.keybinding.KeyBindingWrapper;
import austeretony.rebind.common.config.ConfigLoader;
import austeretony.rebind.common.config.EnumConfigSettings;
import austeretony.rebind.common.core.ReBindHooks;
import austeretony.rebind.common.main.EnumChatMessages;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

public class CommandReBind extends CommandBase {

    public static final String 
    NAME = "rebind", 
    USAGE = "/rebind <arg>, type </rebind help> for available arguments.";

    @Override
    public String getCommandName() {
        return NAME;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return USAGE;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        EnumCommandReBindArgs arg;
        if (args.length != 1 || (arg = EnumCommandReBindArgs.get(args[0])) == null)        
            throw new WrongUsageException(this.getCommandUsage(sender));   
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
        if (KeyBindingWrapper.UNKNOWN.isEmpty()) {
            EnumChatMessages.COMMAND_REBIND_ERR_NO_UNKNOWN_KEY_BINDINGS.showMessage();
            return false;
        } else if (checkExternalConfig && !EnumConfigSettings.EXTERNAL_CONFIG.isEnabled()) {
            EnumChatMessages.COMMAND_REBIND_ERR_EXTERNAL_CONFIG_DISABLED.showMessage();
            return false;
        }
        return true;
    }
}
