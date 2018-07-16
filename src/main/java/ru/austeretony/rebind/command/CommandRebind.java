package ru.austeretony.rebind.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;

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
import ru.austeretony.rebind.coremod.ReBindHooks;
import ru.austeretony.rebind.main.ConfigLoader;
import ru.austeretony.rebind.main.EnumKeyModifier;
import ru.austeretony.rebind.main.KeyBindingProperty;

public class CommandReBind extends CommandBase {
	
	public static final String 
	NAME = "rebind",
	USAGE = "/rebind <list, save, update>";

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
    	
		if (args.length != 1 || !(args[0].equals("list") || args[0].equals("save") || args[0].equals("update")))		
			throw new WrongUsageException(this.getCommandUsage(sender));
    	
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;	
		
		ReBindHooks.sortKeyBindings();
		
		if (KeyBindingProperty.UNKNOWN.isEmpty()) {
			
			IChatComponent message = new ChatComponentText("[ReBind] " + I18n.format("rebind.command.none"));
			
			message.getChatStyle().setColor(EnumChatFormatting.RED);
			
			player.addChatMessage(message);
			
			return;
		}
								
		if (args[0].equals("list")) {
										
			IChatComponent main, modNameLog, modName, nameLog, name, codeLog, code, catLog, cat;
									
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
					
					catLog = new ChatComponentText(", C: ");
		
					cat = new ChatComponentText(I18n.format(property.getKeyBinding().getKeyCategory()));
					cat.getChatStyle().setColor(EnumChatFormatting.WHITE);
							
					player.addChatMessage(modNameLog.appendSibling(modName).appendSibling(nameLog).appendSibling(name).appendSibling(codeLog).appendSibling(code).appendSibling(catLog).appendSibling(cat));
				}
				
				player.addChatMessage(new ChatComponentText(""));
			}
		}
		
		if (args[0].equals("save")) {
						
			IChatComponent message;
												
			String 
			gameDirPath = Minecraft.getMinecraft().mcDataDir.getAbsolutePath(),
			filePath = gameDirPath + "/config/rebind/keys.json";

			Path path = Paths.get(filePath);
			
			if (Files.exists(path)) {
				
				message = new ChatComponentText("[ReBind] " + I18n.format("rebind.command.exist"));
				
				message.getChatStyle().setColor(EnumChatFormatting.RED);
				
				player.addChatMessage(message);
				
				return;
			}
			
			else {
				
	            try {
	            	
					Files.createDirectories(path.getParent());
										
					List<String> keybindingsData = new ArrayList<String>();
					
					keybindingsData.add("{/keybindings/: [".replace('/', '"'));
							
					keybindingsData.addAll(this.getUnknownKeybindingsData());
					
					keybindingsData.add("]}");	
					
					try {
						
				        PrintStream fileStream = new PrintStream(new File(filePath));
				        
				        for (String line : keybindingsData)				        	
				        	fileStream.println(line);
				        
				        fileStream.close();
				        
						message = new ChatComponentText("[ReBind] " + I18n.format("rebind.command.generated"));
						
						message.getChatStyle().setColor(EnumChatFormatting.GREEN);
											
						player.addChatMessage(message);
					}
			        
			        catch (IOException exception) {
			        	
			        	exception.printStackTrace();
					}
				} 
	            
	            catch (IOException exception) {
	            	
	            	exception.printStackTrace();
				}	
			}
		}
		
		if (args[0].equals("update")) {
			
			IChatComponent message;
			
			if (!ConfigLoader.isExternalConfigEnabled()) {
				
				message = new ChatComponentText("[ReBind] " + I18n.format("rebind.command.noExternal"));
				
				message.getChatStyle().setColor(EnumChatFormatting.RED);
				
				player.addChatMessage(message);
				
				return;
			}
			
			try {
			
				String configPath = Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + "/config/rebind/rebind.json";
		
				InputStream inputStream = new FileInputStream(new File(configPath));

				List<String> configData = IOUtils.readLines(new InputStreamReader(inputStream, "UTF-8"));

				List<String> modsKeybindingsData = this.getUnknownKeybindingsData();
				
				String 
				lastConfigLine = configData.get(configData.size() - 2),
				lastModsLine = modsKeybindingsData.get(modsKeybindingsData.size() - 1);
				
				if (lastConfigLine.equals(lastModsLine)) {
					
					message = new ChatComponentText("[ReBind] " + I18n.format("rebind.command.alreadyUpdated"));
					
					message.getChatStyle().setColor(EnumChatFormatting.RED);
					
					player.addChatMessage(message);
					
					return;
				}
				
				configData.remove("]}");
								
				configData.remove(lastConfigLine);
				
				configData.add(lastConfigLine + ",");
								
				configData.add("");
				
				configData.addAll(modsKeybindingsData);
				
				configData.add("]}");
				
				try {
					
			        PrintStream fileStream = new PrintStream(new File(configPath));
			        
			        for (String line : configData)			        	
			        	fileStream.println(line);
			        
			        fileStream.close();
			        
					message = new ChatComponentText("[ReBind] " + I18n.format("rebind.command.updated"));
					
					message.getChatStyle().setColor(EnumChatFormatting.GREEN);
					
					player.addChatMessage(message);
				}
		        
		        catch (IOException exception) {
		        	
		        	exception.printStackTrace();
				}
			}
	        
	        catch (IOException exception) {
	        	
	        	exception.printStackTrace();
			}
		}
    }
    
    private List<String> getUnknownKeybindingsData() {
    	
		List<String> data = new ArrayList<String>();
								
		Multimap<String, KeyBindingProperty> propsByModnames = LinkedHashMultimap.<String, KeyBindingProperty>create();
		
		Set<String> sortedModNames = new TreeSet<String>();
		
		for (KeyBindingProperty property : KeyBindingProperty.UNKNOWN) {

			propsByModnames.put(property.getModName(), property);
			
			sortedModNames.add(property.getModName());
		}
		
		int 
		modNameIndex = 0,
		keyIndex = 0;
		
		String line, keyModifier;
				
		for (String modName : sortedModNames) {
				
			modNameIndex++;
			
			for (KeyBindingProperty property : propsByModnames.get(modName)) {
				
				keyIndex++;
				
				keyModifier = property.getDefaultKeyModifier() == EnumKeyModifier.NONE ? "" : property.getDefaultKeyModifier().toString();
				
				line = "{/" + property.getKeyBindingId() + "/: { /holder/: //, /name/: //, /category/: /" + property.getModName() + "/, /key/: " + property.getKeyBinding().getKeyCodeDefault() + ", /mod/: /" + keyModifier + "/, /enabled/: true}}";
			
				if (keyIndex < propsByModnames.get(modName).size())				
					line += ",";				
				else if (modNameIndex < sortedModNames.size())					
					line += ",";
				
				data.add(line.replace('/', '"'));
			}
			
			keyIndex = 0;
									
			if (modNameIndex < sortedModNames.size())				
				data.add("");
		}
				
		return data;
    }
}


