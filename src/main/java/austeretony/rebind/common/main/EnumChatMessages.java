package austeretony.rebind.common.main;

import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import austeretony.rebind.client.keybinding.KeyBindingWrapper;
import austeretony.rebind.client.reference.ClientReference;
import austeretony.rebind.common.commands.EnumCommandReBindArgs;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public enum EnumChatMessages {

    UPDATE_MESSAGE,
    COMMAND_REBIND_HELP,
    COMMAND_REBIND_LIST,
    COMMAND_REBIND_UPDATE,
    COMMAND_REBIND_ERR_NO_UNKNOWN_KEY_BINDINGS,
    COMMAND_REBIND_ERR_EXTERNAL_CONFIG_DISABLED;


    public static final IChatComponent PREFIX;

    static {
        PREFIX = new ChatComponentText("[" + ReBindMain.NAME + "] ");
        PREFIX.getChatStyle().setColor(EnumChatFormatting.AQUA);                   
    }

    private static IChatComponent prefix() {
        return PREFIX.createCopy();
    }

    private String formatVersion(String input) {
        try {  
            String[] splitted = input.split("[:]");
            return splitted[0] + " " + splitted[1] + " r-" + splitted[2];
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return input;
    }

    public void showMessage(String... args) {
        IChatComponent msg1, msg2, msg3;
        switch (this) {
        case UPDATE_MESSAGE:
            msg1 = new ChatComponentTranslation("rebind.update.newVersion");
            msg2 = new ChatComponentText(" " + this.formatVersion(ReBindMain.VERSION_CUSTOM) + " / " + this.formatVersion(args[0]));        
            ClientReference.showChatMessageClient(prefix().appendSibling(msg1).appendSibling(msg2));
            msg1 = new ChatComponentTranslation("rebind.update.projectPage");
            msg2 = new ChatComponentText(": ");
            msg3 = new ChatComponentText(ReBindMain.PROJECT_LOCATION);   
            msg1.getChatStyle().setColor(EnumChatFormatting.AQUA);      
            msg3.getChatStyle().setColor(EnumChatFormatting.WHITE);                             
            msg3.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, ReBindMain.PROJECT_URL));             
            ClientReference.showChatMessageClient(msg1.appendSibling(msg2).appendSibling(msg3));
            break;
        case COMMAND_REBIND_HELP:
            ClientReference.showChatMessageClient(prefix().appendSibling(new ChatComponentTranslation("rebind.command.help.title")));
            for (EnumCommandReBindArgs arg : EnumCommandReBindArgs.values()) {
                if (arg != EnumCommandReBindArgs.HELP) {
                    msg1 = new ChatComponentText("/rebind " + arg);
                    msg2 = new ChatComponentText(" - ");
                    msg1.getChatStyle().setColor(EnumChatFormatting.GREEN);  
                    msg2.getChatStyle().setColor(EnumChatFormatting.WHITE); 
                    ClientReference.showChatMessageClient(msg1.appendSibling(msg2.appendSibling(new ChatComponentTranslation("rebind.command.help." + arg))));
                }
            }
            break;
        case COMMAND_REBIND_LIST:
            IChatComponent modNameLog, modName, nameLog, name, codeLog, code;
            Multimap<String, KeyBindingWrapper> propsByModnames = LinkedHashMultimap.<String, KeyBindingWrapper>create();
            Set<String> sortedModNames = new TreeSet<String>();
            for (KeyBindingWrapper property : KeyBindingWrapper.UNKNOWN) {
                propsByModnames.put(property.getModName(), property);
                sortedModNames.add(property.getModName());
            }
            ClientReference.showChatMessageClient(prefix().appendSibling(new ChatComponentTranslation("rebind.command.list")));
            for (String modNameStr : sortedModNames) {
                for (KeyBindingWrapper property : propsByModnames.get(modNameStr)) {
                    modNameLog = new ChatComponentText("M: ");
                    modNameLog.getChatStyle().setColor(EnumChatFormatting.AQUA);
                    modName = new ChatComponentText(property.getModName());
                    modName.getChatStyle().setColor(EnumChatFormatting.WHITE);
                    nameLog = new ChatComponentText(", N: ");
                    nameLog.getChatStyle().setColor(EnumChatFormatting.AQUA);
                    name = new ChatComponentText(I18n.format(property.getKeyBinding().getKeyDescription()));
                    name.getChatStyle().setColor(EnumChatFormatting.WHITE);
                    codeLog = new ChatComponentText(", K: ");
                    code = new ChatComponentText(GameSettings.getKeyDisplayString(property.getKeyBinding().getKeyCode()));
                    code.getChatStyle().setColor(EnumChatFormatting.WHITE);
                    ClientReference.showChatMessageClient(modNameLog.appendSibling(modName).appendSibling(nameLog).appendSibling(name).appendSibling(codeLog).appendSibling(code));
                }
                ClientReference.showChatMessageClient(new ChatComponentText(""));
            }
            break;
        case COMMAND_REBIND_UPDATE:
            msg1 = new ChatComponentTranslation("rebind.command.update");
            msg1.getChatStyle().setColor(EnumChatFormatting.GREEN);
            ClientReference.showChatMessageClient(prefix().appendSibling(msg1));
            break;
        case COMMAND_REBIND_ERR_NO_UNKNOWN_KEY_BINDINGS:
            msg1 = new ChatComponentTranslation("rebind.command.err.noUnknownKeys");
            msg1.getChatStyle().setColor(EnumChatFormatting.RED);
            ClientReference.showChatMessageClient(prefix().appendSibling(msg1));
            break;
        case COMMAND_REBIND_ERR_EXTERNAL_CONFIG_DISABLED:
            msg1 = new ChatComponentTranslation("rebind.command.err.externalConfigDisabled");
            msg1.getChatStyle().setColor(EnumChatFormatting.RED);
            ClientReference.showChatMessageClient(prefix().appendSibling(msg1));
            break;
        }
    }
}
