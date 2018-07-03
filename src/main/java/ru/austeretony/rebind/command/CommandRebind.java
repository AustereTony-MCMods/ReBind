package ru.austeretony.rebind.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
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
    	
		if (args.length != 1 || !(args[0].equals("keys") || args[0].equals("file") || args[0].equals("update"))) {
			
			throw new WrongUsageException(this.getUsage(sender));
		}
    	
		EntityPlayer player = Minecraft.getMinecraft().player;	
		
		this.sortKeyBindings();
		
		if (ConfigLoader.UNKNOWN_MODIDS.isEmpty()) {
			
			ITextComponent message = new TextComponentString("[ReBind] " + I18n.format("command.rebind.none"));
			
			message.getStyle().setColor(TextFormatting.RED);
			
			player.sendMessage(message);
			
			return;
		}
								
		if (args[0].equals("keys")) {
													
			ITextComponent main, modidLog, modid, nameLog, name, codeLog, code, catLog, cat;
			
			player.sendMessage(new TextComponentString("[ReBind] " + I18n.format("command.rebind.unsupportedKeys") + ":"));
						
			for (String modId : ConfigLoader.UNKNOWN_MODIDS) {
								
				for (KeyBinding key : ConfigLoader.KEYBINDINGS_BY_MODIDS.get(modId)) {
					
					modidLog = new TextComponentString("M: ");	
					modidLog.getStyle().setColor(TextFormatting.AQUA);
					
					modid = new TextComponentString(ConfigLoader.MODNAMES_BY_MODIDS.get(modId));			
					modid.getStyle().setColor(TextFormatting.WHITE);
					
					nameLog = new TextComponentString(", N: ");	
					nameLog.getStyle().setColor(TextFormatting.AQUA);
					
					name = new TextComponentString(I18n.format(key.getKeyDescription()));			
					name.getStyle().setColor(TextFormatting.WHITE);
					
					codeLog = new TextComponentString(", K: ");
					
					code = new TextComponentString(key.getDisplayName());
					code.getStyle().setColor(TextFormatting.WHITE);
					
					catLog = new TextComponentString(", C: ");
		
					cat = new TextComponentString(I18n.format(key.getKeyCategory()));
					cat.getStyle().setColor(TextFormatting.WHITE);
		
					player.sendMessage(modidLog.appendSibling(modid).appendSibling(nameLog).appendSibling(name).appendSibling(codeLog).appendSibling(code).appendSibling(catLog).appendSibling(cat));
				}
				
				player.sendMessage(new TextComponentString(" "));
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
							
					keybindingsData.addAll(this.getModsKeybindingsData());
					
					keybindingsData.add("]}");	
					
					try {
						
				        PrintStream fileStream = new PrintStream(new File(filePath));
				        
				        for (String line : keybindingsData) {
				        	
				        	fileStream.println(line);
				        }
				        
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

				List<String> modsKeybindingsData = this.getModsKeybindingsData();
				
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
			        
			        for (String line : configData) {
			        	
			        	fileStream.println(line);
			        }
			        
			        fileStream.close();
			        
					message = new TextComponentString("[ReBind] " + I18n.format("command.rebind.updated"));
					
					message.getStyle().setColor(TextFormatting.GREEN);
					
					player.sendMessage(message);
				}
		        
		        catch (IOException exception) {
		        	
		        	exception.printStackTrace();
				}
			}
			
	        catch (UnsupportedEncodingException exception) {
	        	
	        	exception.printStackTrace();
			} 
	        
	        catch (IOException exception) {
	        	
	        	exception.printStackTrace();
			}
		}
    }
    
    private void sortKeyBindings() {
    	
		if (ConfigLoader.SORTED_KEYBINDINGS.isEmpty()) {

	        KeyBinding[] bindings = (KeyBinding[]) ArrayUtils.clone(Minecraft.getMinecraft().gameSettings.keyBindings);

	        Arrays.sort(bindings);

	        ReBindHooks.sortKeyBindings(bindings);
		}
    }
    
    private List<String> getModsKeybindingsData() {
    	
		List<String> data = new ArrayList<String>();
				
		int 
		modidIndex = 0,
		keyIndex = 0;
		
		String line, keyModifier;
				
		for (String modId : ConfigLoader.UNKNOWN_MODIDS) {
				
			modidIndex++;
			
			for (KeyBinding key : ConfigLoader.KEYBINDINGS_BY_MODIDS.get(modId)) {
				
				keyIndex++;
				
				keyModifier = key.getKeyModifierDefault() == KeyModifier.NONE ? "" : key.getKeyModifierDefault().toString();
				
				line = "{/" + ConfigLoader.KEYS_BY_KEYBINDINGS.get(key) + "/: { /name/: //, /category/: /" + ConfigLoader.MODNAMES_BY_MODIDS.get(modId) + "/, /key/: " + key.getKeyCodeDefault() + ", /mod/: /" + keyModifier + "/, /enabled/: true}}";
			
				if (keyIndex < ConfigLoader.KEYBINDINGS_BY_MODIDS.get(modId).size()) {
					
					line += ",";
				}
				
				else if (modidIndex < ConfigLoader.UNKNOWN_MODIDS.size()) {
					
					line += ",";
				}
				
				data.add(line.replace('/', '"'));
			}
			
			keyIndex = 0;
									
			if (modidIndex < ConfigLoader.UNKNOWN_MODIDS.size()) {
				
				data.add("");
			}
		}
				
		return data;
    }
}