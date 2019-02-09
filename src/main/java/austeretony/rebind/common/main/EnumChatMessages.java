package austeretony.rebind.common.main;

import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import austeretony.rebind.client.keybinding.KeyBindingProperty;
import austeretony.rebind.client.reference.ClientReference;
import austeretony.rebind.common.command.EnumCommandReBindArgs;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

public enum EnumChatMessages {

    UPDATE_MESSAGE,
    COMMAND_REBIND_HELP,
    COMMAND_REBIND_ERR_NO_UNKNOWN_KEY_BINDINGS,
    COMMAND_REBIND_LIST,
    COMMAND_REBIND_ERR_EXTERNAL_CONFIG_DISABLED,
    COMMAND_REBIND_UPDATE;

    public static final ITextComponent PREFIX;

    static {
        PREFIX = new TextComponentString("[ReBind] ");
        PREFIX.getStyle().setColor(TextFormatting.AQUA);                   
    }

    private static ITextComponent prefix() {
        return PREFIX.createCopy();
    }

    public void showMessage(String... args) {
        ITextComponent msg1, msg2, msg3;
        switch (this) {
        case UPDATE_MESSAGE:
            msg1 = new TextComponentTranslation("rebind.update.newVersion");
            msg2 = new TextComponentString(" [" + ReBindMain.VERSION + "/" + args[0] + "]");        
            ClientReference.showChatMessageClient(prefix().appendSibling(msg1).appendSibling(msg2));
            msg1 = new TextComponentTranslation("rebind.update.projectPage");
            msg2 = new TextComponentString(": ");
            msg3 = new TextComponentString(ReBindMain.PROJECT_LOCATION);   
            msg1.getStyle().setColor(TextFormatting.AQUA);      
            msg3.getStyle().setColor(TextFormatting.WHITE);                             
            msg3.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, ReBindMain.PROJECT_URL));             
            ClientReference.showChatMessageClient(msg1.appendSibling(msg2).appendSibling(msg3));
            break;
        case COMMAND_REBIND_HELP:
            ClientReference.showChatMessageClient(prefix().appendSibling(new TextComponentTranslation("rebind.command.help.title")));
            for (EnumCommandReBindArgs arg : EnumCommandReBindArgs.values()) {
                if (arg != EnumCommandReBindArgs.HELP) {
                    msg1 = new TextComponentString("/rebind " + arg);
                    msg2 = new TextComponentString(" - ");
                    msg1.getStyle().setColor(TextFormatting.GREEN);  
                    msg2.getStyle().setColor(TextFormatting.WHITE); 
                    ClientReference.showChatMessageClient(msg1.appendSibling(msg2.appendSibling(new TextComponentTranslation("rebind.command.help." + arg))));
                }
            }
            break;
        case COMMAND_REBIND_ERR_NO_UNKNOWN_KEY_BINDINGS:
            msg1 = new TextComponentTranslation("rebind.command.err.noUnknownKeys");
            msg1.getStyle().setColor(TextFormatting.RED);
            ClientReference.showChatMessageClient(prefix().appendSibling(msg1));
            break;
        case COMMAND_REBIND_LIST:
            ITextComponent modNameLog, modName, nameLog, name, codeLog, code;
            Multimap<String, KeyBindingProperty> propsByModnames = LinkedHashMultimap.<String, KeyBindingProperty>create();
            Set<String> sortedModNames = new TreeSet<String>();
            for (KeyBindingProperty property : KeyBindingProperty.UNKNOWN) {
                propsByModnames.put(property.getModName(), property);
                sortedModNames.add(property.getModName());
            }
            ClientReference.showChatMessageClient(prefix().appendSibling(new TextComponentTranslation("rebind.command.list")));
            for (String modNameStr : sortedModNames) {
                for (KeyBindingProperty property : propsByModnames.get(modNameStr)) {
                    modNameLog = new TextComponentString("M: ");
                    modNameLog.getStyle().setColor(TextFormatting.AQUA);
                    modName = new TextComponentString(property.getModName());
                    modName.getStyle().setColor(TextFormatting.WHITE);
                    nameLog = new TextComponentString(", N: ");
                    nameLog.getStyle().setColor(TextFormatting.AQUA);
                    name = new TextComponentString(I18n.format(property.getKeyBinding().getKeyDescription()));
                    name.getStyle().setColor(TextFormatting.WHITE);
                    codeLog = new TextComponentString(", K: ");
                    code = new TextComponentString(GameSettings.getKeyDisplayString(property.getKeyBinding().getKeyCode()));
                    code.getStyle().setColor(TextFormatting.WHITE);
                    ClientReference.showChatMessageClient(modNameLog.appendSibling(modName).appendSibling(nameLog).appendSibling(name).appendSibling(codeLog).appendSibling(code));
                }
                ClientReference.showChatMessageClient(new TextComponentString(""));
            }
            break;
        case COMMAND_REBIND_ERR_EXTERNAL_CONFIG_DISABLED:
            msg1 = new TextComponentTranslation("rebind.command.err.externalConfigDisabled");
            msg1.getStyle().setColor(TextFormatting.RED);
            ClientReference.showChatMessageClient(prefix().appendSibling(msg1));
            break;
        case COMMAND_REBIND_UPDATE:
            msg1 = new TextComponentTranslation("rebind.command.update");
            msg1.getStyle().setColor(TextFormatting.GREEN);
            ClientReference.showChatMessageClient(prefix().appendSibling(msg1));
            break;
        }
    }
}
