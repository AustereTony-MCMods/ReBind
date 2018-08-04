package ru.austeretony.rebind.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraftforge.fml.relauncher.FMLInjectionData;
import ru.austeretony.rebind.main.EnumKeyModifier;
import ru.austeretony.rebind.main.KeyBindingProperty;
import ru.austeretony.rebind.util.ReBindUtil;

public class ConfigLoader {

	public static final String 
	EXT_REBIND_FILE_PATH = ((File) (FMLInjectionData.data()[6])).getAbsolutePath() + "/config/rebind/rebind.json",
	EXT_KEYBINDINGS_FILE_PATH = ((File) (FMLInjectionData.data()[6])).getAbsolutePath() + "/config/rebind/keybindings.json";
	
	public static KeyBindingsObject properties;
	
	private static boolean 
	checkForUpdates, 
	showChangelog, 
	useExternalConfig, 
	rewriteControls, 
	enableDebugMode, 
	enableAutoJump, 
	allowDoubleTapForwardSprint, 
	allowPlayerSprint, 
	allowMountSprint,
	allowHotbarScrolling;

	public static void loadConfiguration() {
		
        JsonObject intMainConf, intKeybindingsConf;  
		
		try {
			
	        intMainConf = (JsonObject) ReBindUtil.getInternalJsonData("assets/rebind/rebind.json");
	        intKeybindingsConf = (JsonObject) ReBindUtil.getInternalJsonData("assets/rebind/keybindings.json");  
		}
		
		catch (IOException exception) {
			
			exception.printStackTrace();
			
			return;
		}
		
        useExternalConfig = intMainConf.get("main").getAsJsonObject().get("external_config").getAsBoolean();   
        
        if (!useExternalConfig)           	
        	loadData(intMainConf, intKeybindingsConf);
        else           	
        	loadExternalConfig(intMainConf, intKeybindingsConf);
    }
    
	private static void loadExternalConfig(JsonObject intMainConf, JsonObject intKeybindingsConf) {

		Path 
		mainPath = Paths.get(EXT_REBIND_FILE_PATH),
		keybindingsPath = Paths.get(EXT_KEYBINDINGS_FILE_PATH);
		
		if (Files.exists(mainPath) && Files.exists(keybindingsPath)) {
			
	        JsonObject extMainConf, extKeybindingsConf;  
			
			try {
				
		        extMainConf = (JsonObject) ReBindUtil.getExternalJsonData(EXT_REBIND_FILE_PATH);
		        extKeybindingsConf = (JsonObject) ReBindUtil.getExternalJsonData(EXT_KEYBINDINGS_FILE_PATH);  
			}
			
			catch (IOException exception) {
				
				exception.printStackTrace();
				
				return;
			}
			
			loadData(extMainConf, extKeybindingsConf);	
		}
		
		else {
			
            try {
            	
				Files.createDirectories(mainPath.getParent());		
				Files.createDirectories(keybindingsPath.getParent());															
			} 
            
            catch (IOException exception) {
            	
            	exception.printStackTrace();
			}	
            
            createExternalCopyAndLoad(intMainConf, intKeybindingsConf);
		}
	}
	
	private static void createExternalCopyAndLoad(JsonObject intMainConf, JsonObject intKeybindingsConf) {
    	
        try {
        	
        	ReBindUtil.createExternalJsonFile(EXT_REBIND_FILE_PATH, intMainConf);      	
        	ReBindUtil.createAbsoluteFileCopy(EXT_KEYBINDINGS_FILE_PATH, ReBindUtil.class.getClassLoader().getResourceAsStream("assets/rebind/keybindings.json"));       	
		} 
        
        catch (IOException exception) {
        	
        	exception.printStackTrace();
		}
        
    	loadData(intMainConf, intKeybindingsConf);
    }
	
	private static void loadData(JsonObject mainConfigFile, JsonObject keybindingsConfigFile) {
		
        JsonObject mainSettings = mainConfigFile.get("main").getAsJsonObject();
        
        rewriteControls = mainSettings.get("rewrite_controls").getAsBoolean();        
        enableDebugMode = mainSettings.get("debug_mode").getAsBoolean();      
        checkForUpdates = mainSettings.get("updates").getAsJsonObject().get("update_checker").getAsBoolean();				
        showChangelog = mainSettings.get("updates").getAsJsonObject().get("show_changelog").getAsBoolean();
        
        JsonObject ingameSettings = mainConfigFile.get("game").getAsJsonObject();
        
        enableAutoJump = ingameSettings.get("auto_jump").getAsBoolean();
        
        JsonObject controlsSettings = mainConfigFile.get("controls").getAsJsonObject();
                
        allowPlayerSprint = controlsSettings.get("player_sprint").getAsBoolean();       
        allowDoubleTapForwardSprint = controlsSettings.get("double_tap_forward_sprint").getAsBoolean();      
        allowMountSprint = controlsSettings.get("mount_sprint").getAsBoolean();        
        allowHotbarScrolling = controlsSettings.get("hotbar_scrolling").getAsBoolean();
          
        Gson gson = new GsonBuilder()
        		.registerTypeAdapter(KeyBindingsObject.class, new KeyBindingsDeserializer())
        		.registerTypeAdapter(KeyBindingObject.class, new KeyBindingDeserializer())
        		.create();
        
        properties = gson.fromJson(keybindingsConfigFile, KeyBindingsObject.class);
        
    	for (Map.Entry<String, KeyBindingObject> entry : properties.getMap().entrySet()) {   
    		
			new KeyBindingProperty(
					entry.getKey(),
					entry.getValue().holder, 
					entry.getValue().name, 
					entry.getValue().category, 
					entry.getValue().keyCode,
					EnumKeyModifier.valueFromString(entry.getValue().keyModifier),
					entry.getValue().isEnabled, 
					true);     
    	}
	}
	
	public static boolean isUpdateCheckerEnabled() {
		
		return checkForUpdates;
	}
	
	public static boolean shouldShowChangelog() {
		
		return showChangelog;
	}
	
	public static boolean isExternalConfigEnabled() {
		
		return useExternalConfig;
	}
	
	public static boolean isControllsSettingsRewritingEnabled() {
		
		return rewriteControls;
	}
	
	public static boolean isDebugModeEnabled() {
		
		return enableDebugMode;
	}
	
	public static boolean isAutoJumpEnabled() {
		
		return enableAutoJump;
	}
	
	public static boolean isPlayerSprintAllowed() {
		
		return allowPlayerSprint;
	}
	
	public static boolean isDoubleTapForwardSprintAllowed() {
		
		return allowDoubleTapForwardSprint;
	}
	
	public static boolean isMountSprintAllowed() {
		
		return allowMountSprint;
	}
	
	public static boolean isHotbarScrollingAllowed() {
		
		return allowHotbarScrolling;
	}
}
