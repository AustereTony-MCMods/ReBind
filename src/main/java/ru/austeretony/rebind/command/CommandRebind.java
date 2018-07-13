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
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.settings.KeyModifier;
import ru.austeretony.rebind.coremod.ReBindHooks;
import ru.austeretony.rebind.main.ConfigLoader;
import ru.austeretony.rebind.main.KeyBindingProperty;

public class CommandRebind extends CommandBase {
	
	public static final String 
	NAME = "rebind",
	USAGE = "/rebind <keys, file, update>";

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
    	
		if (args.length != 1 || !(args[0].equals("keys") || args[0].equals("file") || args[0].equals("update")))		
			throw new WrongUsageException(this.getUsage(sender));
    	
		EntityPlayer player = Minecraft.getMinecraft().player;	
		
		ReBindHooks.sortKeyBindings();
		
		if (KeyBindingProperty.UNKNOWN.isEmpty()) {
			
			ITextComponent message = new TextComponentString("[ReBind] " + I18n.format("command.rebind.none"));
			
			message.getStyle().setColor(TextFormatting.RED);
			
			player.sendMessage(message);
			
			return;
		}
								
		if (args[0].equals("keys")) {
										
			ITextComponent main, modNameLog, modName, nameLog, name, codeLog, code, catLog, cat;
									
			Multimap<String, KeyBindingProperty> propsByModnames = LinkedHashMultimap.<String, KeyBindingProperty>create();
			
			Set<String> sortedModNames = new TreeSet<String>();
			
			for (KeyBindingProperty property : KeyBindingProperty.UNKNOWN) {

				propsByModnames.put(property.getModName(), property);
				
				sortedModNames.add(property.getModName());
			}
			
			player.sendMessage(new TextComponentString("[ReBind] " + I18n.format("command.rebind.unsupportedKeys") + ":"));

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
					
					catLog = new TextComponentString(", C: ");
		
					cat = new TextComponentString(I18n.format(property.getKeyBinding().getKeyCategory()));
					cat.getStyle().setColor(TextFormatting.WHITE);
							
					player.sendMessage(modNameLog.appendSibling(modName).appendSibling(nameLog).appendSibling(name).appendSibling(codeLog).appendSibling(code).appendSibling(catLog).appendSibling(cat));
				}
				
				player.sendMessage(new TextComponentString(""));
			}
		}
		
		if (args[0].equals("file")) {
						
			ITextComponent message;
												
			String 
			gameDirPath = Minecraft.getMinecraft().mcDataDir.getAbsolutePath(),
			filePath = gameDirPath + "/config/rebind/keys.json";

			Path path = Paths.get(filePath);
			
			if (Files.exists(path)) {
				
				message = new TextComponentString("[ReBind] " + I18n.format("command.rebind.exist"));
				
				message.getStyle().setColor(TextFormatting.RED);
				
				player.sendMessage(message);
				
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
				        
						message = new TextComponentString("[ReBind] " + I18n.format("command.rebind.generated"));
						
						message.getStyle().setColor(TextFormatting.GREEN);
											
						player.sendMessage(message);
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
			
			ITextComponent message;
			
			if (!ConfigLoader.isExternalConfigEnabled()) {
				
				message = new TextComponentString("[ReBind] " + I18n.format("command.rebind.noExternal"));
				
				message.getStyle().setColor(TextFormatting.RED);
				
				player.sendMessage(message);
				
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
					
					message = new TextComponentString("[ReBind] " + I18n.format("command.rebind.alreadyUpdated"));
					
					message.getStyle().setColor(TextFormatting.RED);
					
					player.sendMessage(message);
					
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
			        
					message = new TextComponentString("[ReBind] " + I18n.format("command.rebind.updated"));
					
					message.getStyle().setColor(TextFormatting.GREEN);
					
					player.sendMessage(message);
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
				
				keyModifier = property.getKeyBinding().getKeyModifierDefault() == KeyModifier.NONE ? "" : property.getKeyBinding().getKeyModifierDefault().toString();
				
				line = "{/" + property.getConfigKey() + "/: { /name/: //, /category/: /" + property.getModName() + "/, /key/: " + property.getKeyBinding().getKeyCodeDefault() + ", /mod/: /" + keyModifier + "/, /enabled/: true}}";
			
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


