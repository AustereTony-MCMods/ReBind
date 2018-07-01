package ru.austeretony.rebind.command;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
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

public class CommandRebind extends CommandBase {
	
	public static final String 
	NAME = "rebind",
	USAGE = "/rebind <keys, file>";

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
    	
		if (args.length != 1 || !(args[0].equals("keys") || args[0].equals("file"))) {
			
			throw new WrongUsageException(this.getCommandUsage(sender));
		}
    	
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;	
		
		this.sortKeyBindings();
								
		if (args[0].equals("keys")) {
										
			IChatComponent main, modidLog, modid, nameLog, name, codeLog, code, catLog, cat;
			
			if (ConfigLoader.UNKNOWN_MODIDS.isEmpty()) {
				
				main = new ChatComponentText("[ReBind] " + I18n.format("command.rebind.none"));
				
				main.getChatStyle().setColor(EnumChatFormatting.RED);
				
				player.addChatMessage(main);
				
				return;
			}
			
			player.addChatMessage(new ChatComponentText("[ReBind] " + I18n.format("command.rebind.unsupportedKeys") + ":"));
						
			for (String modId : ConfigLoader.UNKNOWN_MODIDS) {
								
				for (KeyBinding key : ConfigLoader.KEYBINDINGS_BY_MODIDS.get(modId)) {
					
					modidLog = new ChatComponentText("M: ");	
					modidLog.getChatStyle().setColor(EnumChatFormatting.AQUA);
					
					modid = new ChatComponentText(ConfigLoader.MODNAMES_BY_MODIDS.get(modId));			
					modid.getChatStyle().setColor(EnumChatFormatting.WHITE);
					
					nameLog = new ChatComponentText(", N: ");	
					nameLog.getChatStyle().setColor(EnumChatFormatting.AQUA);
					
					name = new ChatComponentText(I18n.format(key.getKeyDescription()));			
					name.getChatStyle().setColor(EnumChatFormatting.WHITE);
					
					codeLog = new ChatComponentText(", K: ");
					
					code = new ChatComponentText(GameSettings.getKeyDisplayString(key.getKeyCode()));
					code.getChatStyle().setColor(EnumChatFormatting.WHITE);
					
					catLog = new ChatComponentText(", C: ");
		
					cat = new ChatComponentText(I18n.format(key.getKeyCategory()));
					cat.getChatStyle().setColor(EnumChatFormatting.WHITE);
					
					modidLog.appendSibling(modid).appendSibling(nameLog).appendSibling(name).appendSibling(codeLog).appendSibling(code).appendSibling(catLog).appendSibling(cat);
		
					player.addChatMessage(modidLog);
				}
				
				player.addChatMessage(new ChatComponentText(" "));
			}
		}
		
		if (args[0].equals("file")) {
						
			IChatComponent keyInfo;
			
			if (ConfigLoader.UNKNOWN_MODIDS.isEmpty()) {
				
				keyInfo = new ChatComponentText("[ReBind] " + I18n.format("command.rebind.none"));
				
				keyInfo.getChatStyle().setColor(EnumChatFormatting.RED);
				
				player.addChatMessage(keyInfo);
				
				return;
			}
												
			String 
			gameDirPath = Minecraft.getMinecraft().mcDataDir.getAbsolutePath(),
			filePath = gameDirPath + "/config/rebind/keys.json";

			Path path = Paths.get(filePath);
			
			if (Files.exists(path)) {
				
				keyInfo = new ChatComponentText("[ReBind] " + I18n.format("command.rebind.exist"));
				
				keyInfo.getChatStyle().setColor(EnumChatFormatting.RED);
				
				player.addChatMessage(keyInfo);
				
				return;
			}
			
			else {
				
	            try {
	            	
					Files.createDirectories(path.getParent());
										
					this.createFile(filePath);
					
					keyInfo = new ChatComponentText("[ReBind] " + I18n.format("command.rebind.generated"));
					
					keyInfo.getChatStyle().setColor(EnumChatFormatting.GREEN);
										
					player.addChatMessage(keyInfo);
				} 
	            
	            catch (IOException exception) {
	            	
	            	exception.printStackTrace();
				}	
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
    
    private void createFile(String filePath) {
    	
		List<String> fileLines = new ArrayList<String>();
				
		int 
		modidIndex = 0,
		keyIndex = 0;
		
		String line;
		
		fileLines.add("{/keybindings/: [".replace('/', '"'));
		
		for (String modId : ConfigLoader.UNKNOWN_MODIDS) {
				
			modidIndex++;
			
			for (KeyBinding key : ConfigLoader.KEYBINDINGS_BY_MODIDS.get(modId)) {
				
				keyIndex++;
				
				line = "{/" + ConfigLoader.KEYS_BY_KEYBINDINGS.get(key) + "/: { /name/: //, /category/: /" + ConfigLoader.MODNAMES_BY_MODIDS.get(modId) + "/, /key/: " + key.getKeyCodeDefault() + ", /enabled/: true}}";
			
				if (keyIndex < ConfigLoader.KEYBINDINGS_BY_MODIDS.get(modId).size()) {
					
					line += ",";
				}
				
				else if (modidIndex < ConfigLoader.UNKNOWN_MODIDS.size()) {
					
					line += ",";
				}
				
				fileLines.add(line.replace('/', '"'));
			}
			
			keyIndex = 0;
									
			if (modidIndex < ConfigLoader.UNKNOWN_MODIDS.size()) {
				
				fileLines.add("");
			}
		}
		
		fileLines.add("]}");

		try {
			
	        PrintStream fileStream = new PrintStream(new File(filePath));
	        
	        for (String l : fileLines) {
	        	
	        	fileStream.println(l);
	        }
	        
	        fileStream.close();
		}
        
        catch (IOException exception) {
        	
        	exception.printStackTrace();
		}
    }
}


