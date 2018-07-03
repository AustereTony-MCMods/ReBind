package ru.austeretony.rebind.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cpw.mods.fml.relauncher.FMLInjectionData;
import net.minecraft.client.settings.KeyBinding;

public class ConfigLoader {

	private static boolean checkForUpdates, showChangelog, useExternalConfig, rewriteControls, enableDebugMode, enableAutoJump, allowDoubleTapForwardSprint, allowPlayerSprint, allowMountSprint;
	
    public static final Map<String, KeyBindingProperty> PROPERTIES = new HashMap<String, KeyBindingProperty>();
    
    public static final List<KeyBindingProperty> SORTED_PROPERTIES = new ArrayList<KeyBindingProperty>();
    
    public static final Map<String, KeyBinding> KEYBINDINGS_BY_KEYS = new HashMap<String, KeyBinding>();
    
    public static final Map<KeyBinding, String> KEYS_BY_KEYBINDINGS = new HashMap<KeyBinding, String>();
    
    public static final Multimap<String, KeyBinding> KEYBINDINGS_BY_MODIDS = LinkedHashMultimap.<String, KeyBinding>create();
        	    
    public static final Map<KeyBinding, String> MODIDS_BY_KEYBINDINGS = new HashMap<KeyBinding, String>();
    
    public static final Set<KeyBinding> SORTED_KEYBINDINGS = new LinkedHashSet<KeyBinding>();  
    
    public static final Set<String> HIDDEN_KEYBINDINGS = new HashSet<String>();
    
    public static final Set<String> UNKNOWN_MODIDS = new TreeSet<String>();
    
    public static final Map<String, String> MODNAMES_BY_MODIDS = new HashMap<String, String>();
	                   
	public void loadConfiguration() {
		
        try {       
        	
        	InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("assets/rebind/rebind.json");
        	        	
            JsonObject internalConfig = (JsonObject) new JsonParser().parse(new InputStreamReader(inputStream, "UTF-8"));  
            
            inputStream.close();   
                                    
            useExternalConfig = internalConfig.get("main").getAsJsonObject().get("external_config").getAsBoolean();   
            
            if (!useExternalConfig) {
            	
            	this.loadData(internalConfig);
            }
            
            else {
            	
            	this.loadExternalConfig(internalConfig);
            }
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
        	
        	catch (FileNotFoundException exception) {
				
        		exception.printStackTrace();		
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
			
			configData.remove(2);
			configData.remove(2);
			
            PrintStream fileStream = new PrintStream(new File(configPath));
            
            for (String line : configData) {
            	
            	fileStream.println(line);
            }
            
            fileStream.close();
                    	        				            			
        	this.loadData(internalConfig);
		} 
        
        catch (UnsupportedEncodingException exception) {
        	
        	exception.printStackTrace();
		} 
        
        catch (IOException exception) {
        	
        	exception.printStackTrace();
		}
    }
	
	private void loadData(JsonObject configFile) {
		
        JsonObject rawMainData = configFile.get("main").getAsJsonObject();
        
        rewriteControls = rawMainData.get("rewrite_controls").getAsBoolean();
        
        enableDebugMode = rawMainData.get("debug_mode").getAsBoolean();
        
        checkForUpdates = rawMainData.get("updates").getAsJsonObject().get("update_checker").getAsBoolean();
				
        showChangelog = rawMainData.get("updates").getAsJsonObject().get("show_changelog").getAsBoolean();
        
        JsonObject rawIngameData = configFile.get("game").getAsJsonObject();
        
        enableAutoJump = rawIngameData.get("auto_jump").getAsBoolean();
        
        JsonObject rawControlsData = configFile.get("controls").getAsJsonObject();
                
        allowPlayerSprint = rawControlsData.get("player_sprint").getAsBoolean();
        
        allowDoubleTapForwardSprint = rawControlsData.get("double_tap_forward_sprint").getAsBoolean();
        
        allowMountSprint = rawControlsData.get("mount_sprint").getAsBoolean();
                
        Map<String, JsonObject> rawProperties = new LinkedHashMap<String, JsonObject>();
        
        Set<Map.Entry<String, JsonElement>> entrySet;
        
        for (JsonElement jsonElement : configFile.get("keybindings").getAsJsonArray()) {
        	
        	entrySet = jsonElement.getAsJsonObject().entrySet();
        	
        	for (Map.Entry<String, JsonElement> entry : entrySet) {
        		
            	rawProperties.put(entry.getKey(), entry.getValue().getAsJsonObject());
        	}
        }
                                
        int i = 0;         
                
        JsonObject rawProperty;
        
        KeyBindingProperty property;            

    	for (String configKey : rawProperties.keySet()) {
    			
			rawProperty = rawProperties.get(configKey);
			
			property = new KeyBindingProperty(
					configKey, 
					rawProperty.get("name").getAsString(), 
					rawProperty.get("category").getAsString(), 
					rawProperty.get("key").getAsInt(), 
					rawProperty.get("enabled").getAsBoolean());     
			
            PROPERTIES.put(configKey, property);
            
            if (property.isEnabled()) 
        	SORTED_PROPERTIES.add(property);
            else
            HIDDEN_KEYBINDINGS.add(configKey);
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
}

