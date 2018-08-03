package ru.austeretony.rebind.command;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import ru.austeretony.rebind.config.ConfigLoader;
import ru.austeretony.rebind.config.KeyBindingObject;
import ru.austeretony.rebind.core.ReBindHooks;
import ru.austeretony.rebind.main.EnumKeyModifier;
import ru.austeretony.rebind.main.KeyBindingProperty;

public class CommandReBind extends CommandBase {
	
	public static final String 
	NAME = "rebind",
	USAGE = "/rebind <list, update>";

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
    	
		if (args.length != 1 || !(args[0].equals("list") || args[0].equals("update")))		
			throw new WrongUsageException(this.getCommandUsage(sender));
    	
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;	
		
		ReBindHooks.sortKeyBindings();
		
		IChatComponent message;
		
		if (KeyBindingProperty.UNKNOWN.isEmpty()) {
			
			message = new ChatComponentText("[ReBind] " + I18n.format("rebind.command.none"));			
			message.getChatStyle().setColor(EnumChatFormatting.RED);			
			player.addChatMessage(message);
			
			return;
		}
								
		if (args[0].equals("list")) {
										
			IChatComponent main, modNameLog, modName, nameLog, name, codeLog, code;
									
			Multimap<String, KeyBindingProperty> propsByModnames = LinkedHashMultimap.<String, KeyBindingProperty>create();
			
			Set<String> sortedModNames = new TreeSet<String>();
			
			for (KeyBindingProperty property : KeyBindingProperty.UNKNOWN) {

				propsByModnames.put(property.getModName(), property);
				
				sortedModNames.add(property.getModName());
			}
			
			player.addChatMessage(new ChatComponentText("[ReBind] " + I18n.format("rebind.command.unsupportedKeys") + ":"));

			for (String modNameStr : sortedModNames) {
								
				for (KeyBindingProperty property : propsByModnames.get(modNameStr)) {
					
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
					player.addChatMessage(modNameLog.appendSibling(modName).appendSibling(nameLog).appendSibling(name).appendSibling(codeLog).appendSibling(code));
				}
				
				player.addChatMessage(new ChatComponentText(""));
			}
		}
		
		if (args[0].equals("update")) {
						
			if (!ConfigLoader.isExternalConfigEnabled()) {
				
				message = new ChatComponentText("[ReBind] " + I18n.format("rebind.command.noExternal"));				
				message.getChatStyle().setColor(EnumChatFormatting.RED);				
				player.addChatMessage(message);
				
				return;
			}
			
			Multimap<String, KeyBindingProperty> propsByModnames = LinkedHashMultimap.<String, KeyBindingProperty>create();
			
			Set<String> sortedModNames = new TreeSet<String>();
			
			for (KeyBindingProperty property : KeyBindingProperty.UNKNOWN) {

				propsByModnames.put(property.getModName(), property);				
				sortedModNames.add(property.getModName());
			}
			
			for (String modName : sortedModNames) {
								
				for (KeyBindingProperty property : propsByModnames.get(modName)) {

					ConfigLoader.properties.getMap().put(property.getKeyBindingId(), new KeyBindingObject(
							property.getHolderId(),
							property.getName(),
							property.getCategory(),
							property.getKeyCode(),
							property.getKeyModifier() == EnumKeyModifier.NONE ? "" : property.getKeyModifier().toString(),
							property.isEnabled()));
				}
			}
			
			Map<String, String> lines = new LinkedHashMap<String, String>();
			
			String line, l, prevCategory = "";
			
			for (Map.Entry<String, KeyBindingObject> entry : ConfigLoader.properties.getMap().entrySet()) {
					
				line = "/" + entry.getKey() + "/: { /holder/: /" + entry.getValue().holder + "/, /name/: /" + entry.getValue().name + "/, /category/: /" + entry.getValue().category + "/, /key/: " + entry.getValue().keyCode + ", /mod/: /" + entry.getValue().keyModifier + "/, /enabled/: " + entry.getValue().isEnabled + "}";
				
				lines.put(line.replace('/', '"'), entry.getValue().category);
			}
			
			int index = 0;
			
			try (PrintStream printStream = new PrintStream(new File(ConfigLoader.EXT_KEYBINDINGS_FILE_PATH))) {
				
				printStream.println("{");
				
				for (Map.Entry<String, String> entry : lines.entrySet()) {
					
					index++;
					
					if (index != 1 && !entry.getValue().equals(prevCategory))
						printStream.println("");
										
					l = entry.getKey();
										
					if (index < lines.size())						
						l = l + ",";
					
					printStream.println(l);
					
					prevCategory = entry.getValue();
				}
				
				printStream.println("}");
			}
			
			catch (IOException exception) {
				
				exception.printStackTrace();
			}
            
			message = new ChatComponentText("[ReBind] " + I18n.format("rebind.command.updated"));				
			message.getChatStyle().setColor(EnumChatFormatting.GREEN);				
			player.addChatMessage(message);
		}
    }
}


