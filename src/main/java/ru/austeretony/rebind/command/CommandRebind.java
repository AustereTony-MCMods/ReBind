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
	USAGE = "/rebind <keys, file>";

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
    	
		if (args.length != 1 || !(args[0].equals("keys") || args[0].equals("file"))) {
			
			throw new WrongUsageException(this.getUsage(sender));
		}
    	
		EntityPlayer player = Minecraft.getMinecraft().player;	
		
		this.sortKeyBindings();
								
		if (args[0].equals("keys")) {
													
			ITextComponent main, modidLog, modid, nameLog, name, codeLog, code, catLog, cat;
			
			if (ConfigLoader.UNKNOWN_MODIDS.isEmpty()) {
				
				main = new TextComponentString("[ReBind] " + I18n.format("command.rebind.none"));
				
				main.getStyle().setColor(TextFormatting.RED);
				
				player.sendMessage(main);
				
				return;
			}
			
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
					
					modidLog.appendSibling(modid).appendSibling(nameLog).appendSibling(name).appendSibling(codeLog).appendSibling(code).appendSibling(catLog).appendSibling(cat);
		
					player.sendMessage(modidLog);
				}
				
				player.sendMessage(new TextComponentString(" "));
			}
		}
		
		if (args[0].equals("file")) {
						
			ITextComponent keyInfo;
			
			if (ConfigLoader.UNKNOWN_MODIDS.isEmpty()) {
				
				keyInfo = new TextComponentString("[ReBind] " + I18n.format("command.rebind.none"));
				
				keyInfo.getStyle().setColor(TextFormatting.RED);
				
				player.sendMessage(keyInfo);
				
				return;
			}
												
			String 
			gameDirPath = Minecraft.getMinecraft().mcDataDir.getAbsolutePath(),
			filePath = gameDirPath + "/config/rebind/keys.json";

			Path path = Paths.get(filePath);
			
			if (Files.exists(path)) {
				
				keyInfo = new TextComponentString("[ReBind] " + I18n.format("command.rebind.exist"));
				
				keyInfo.getStyle().setColor(TextFormatting.RED);
				
				player.sendMessage(keyInfo);
				
				return;
			}
			
			else {
				
	            try {
	            	
					Files.createDirectories(path.getParent());
										
					this.createFile(filePath);
					
					keyInfo = new TextComponentString("[ReBind] " + I18n.format("command.rebind.generated"));
					
					keyInfo.getStyle().setColor(TextFormatting.GREEN);
										
					player.sendMessage(keyInfo);
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
		
		String line, keyModifier;
		
		fileLines.add("{/keybindings/: [".replace('/', '"'));
		
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