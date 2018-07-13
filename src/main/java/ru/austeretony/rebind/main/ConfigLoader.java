package ru.austeretony.rebind.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cpw.mods.fml.relauncher.FMLInjectionData;

public class ConfigLoader {

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
	allowHotbarScrolling,
	allowGuiQuickMoveContainer,
	allowGuiHotbarSwap,
	allowGuiSlotClone,
	allowGuiThrow,
	allowGuiPickUpAll,
	allowGuiQuickCraft;

	public void loadConfiguration() {
		
        try {       
        	
        	InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("assets/rebind/rebind.json");
        	        	
            JsonObject internalConfig = (JsonObject) new JsonParser().parse(new InputStreamReader(inputStream, "UTF-8"));  
            
            inputStream.close();   
                                    
            useExternalConfig = internalConfig.get("main").getAsJsonObject().get("external_config").getAsBoolean();   
            
            if (!useExternalConfig)           	
            	this.loadData(internalConfig);
            else           	
            	this.loadExternalConfig(internalConfig);
        }
        
        catch (IOException exception) {
        	
        	exception.printStackTrace();
        }
    }
    
	private void loadExternalConfig(JsonObject internalConfig) {

		String 
		gameDirPath = ((File) (FMLInjectionData.data()[6])).getAbsolutePath(),
		configPath = gameDirPath + "/config/rebind/rebind.json";

		Path path = Paths.get(configPath);
		
		if (Files.exists(path)) {

        	try {
        		
				InputStream inputStream = new FileInputStream(new File(configPath));
				
	            JsonObject externalConfig = (JsonObject) new JsonParser().parse(new InputStreamReader(inputStream, "UTF-8"));  
				
				inputStream.close();
				
            	this.loadData(externalConfig);
			}
        	
        	catch (IOException exception) {
        		
        		exception.printStackTrace();
			} 	
		}
		
		else {
			
            try {
            	
				Files.createDirectories(path.getParent());
				
				this.createExternalCopyAndLoad(configPath, internalConfig);												
			} 
            
            catch (IOException exception) {
            	
            	exception.printStackTrace();
			}			
		}
	}
	
	private void createExternalCopyAndLoad(String configPath, JsonObject internalConfig) {
    	
        try {
        	
        	InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("assets/rebind/rebind.json");
        	        	        	
			List<String> configData = IOUtils.readLines(new InputStreamReader(inputStream, "UTF-8"));
			
			inputStream.close();
						
            PrintStream fileStream = new PrintStream(new File(configPath));
            
            for (String line : configData)           	
            	fileStream.println(line);
            
            fileStream.close();
                    	        				            			
        	this.loadData(internalConfig);
		} 
        
        catch (IOException exception) {
        	
        	exception.printStackTrace();
		}
    }
	
	private void loadData(JsonObject configFile) {
		
        JsonObject mainSettings = configFile.get("main").getAsJsonObject();
        
        rewriteControls = mainSettings.get("rewrite_controls").getAsBoolean();        
        enableDebugMode = mainSettings.get("debug_mode").getAsBoolean();      
        checkForUpdates = mainSettings.get("updates").getAsJsonObject().get("update_checker").getAsBoolean();				
        showChangelog = mainSettings.get("updates").getAsJsonObject().get("show_changelog").getAsBoolean();
        
        JsonObject ingameSettings = configFile.get("game").getAsJsonObject();
        
        enableAutoJump = ingameSettings.get("auto_jump").getAsBoolean();
        
        JsonObject controlsSettings = configFile.get("controls").getAsJsonObject();
                
        allowPlayerSprint = controlsSettings.get("player_sprint").getAsBoolean();       
        allowDoubleTapForwardSprint = controlsSettings.get("double_tap_forward_sprint").getAsBoolean();      
        allowMountSprint = controlsSettings.get("mount_sprint").getAsBoolean();        
        allowHotbarScrolling = controlsSettings.get("hotbar_scrolling").getAsBoolean();
        
        JsonObject guiSettings = configFile.get("gui").getAsJsonObject();
        
        allowGuiQuickMoveContainer = guiSettings.get("quick_move_container").getAsBoolean();       
        allowGuiHotbarSwap = guiSettings.get("hotbar_slot_swap").getAsBoolean();
        allowGuiSlotClone = guiSettings.get("slot_stack_clone").getAsBoolean();    
        allowGuiThrow = guiSettings.get("throw").getAsBoolean();
        allowGuiPickUpAll = guiSettings.get("pick_up_all").getAsBoolean();
        allowGuiQuickCraft = guiSettings.get("quick_craft").getAsBoolean();
                
        Map<String, JsonObject> rawProperties = new LinkedHashMap<String, JsonObject>();
        
        Set<Map.Entry<String, JsonElement>> entrySet;
        
        for (JsonElement jsonElement : configFile.get("keybindings").getAsJsonArray()) {
        	
        	entrySet = jsonElement.getAsJsonObject().entrySet();
        	
        	for (Map.Entry<String, JsonElement> entry : entrySet)      		
            	rawProperties.put(entry.getKey(), entry.getValue().getAsJsonObject());
        }
                                                
        JsonObject rawProperty;
        
        KeyBindingProperty property;            

    	for (String configKey : rawProperties.keySet()) {
    			
			rawProperty = rawProperties.get(configKey);
			
			property = new KeyBindingProperty(
					configKey, 
					rawProperty.get("name").getAsString(), 
					rawProperty.get("category").getAsString(), 
					rawProperty.get("key").getAsInt(), 
					EnumKeyModifier.valueFromString(rawProperty.get("mod").getAsString()), 
					rawProperty.get("enabled").getAsBoolean(),
					true);     
    	}
	}
	
	public static boolean isUpdateCheckerEnabled() {
		
		return checkForUpdates;
	}
	
	public static boolean shouldShowChangeolog() {
		
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
	
	public static boolean isGuiQuickMoveContainerAllowed() {
		
		return allowGuiQuickMoveContainer;
	}
	
	public static boolean isGuiHotbarSlotSwapAllowed() {
		
		return allowGuiHotbarSwap;
	}
	
	public static boolean isGuiSlotStackCloneAllowed() {
		
		return allowGuiSlotClone;
	}
	
	public static boolean isGuiThrowAllowed() {
		
		return allowGuiThrow;
	}
	
	public static boolean isGuiPickUpAllAllowed() {
		
		return allowGuiPickUpAll;
	}
	
	public static boolean isGuiQuickCraftAllowed() {
		
		return allowGuiQuickCraft;
	}
}

