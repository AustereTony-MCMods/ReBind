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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cpw.mods.fml.relauncher.FMLInjectionData;
import net.minecraft.client.settings.KeyBinding;

public class ConfigLoader {
	
	private boolean useExternalConfig, enableRewriting;
	
    public static final Map<String, KeyBindingProperty> PROPERTIES = new HashMap<String, KeyBindingProperty>();
    	
    public static final List<KeyBindingProperty> SORTED_PROPERTIES = new ArrayList<KeyBindingProperty>();
    
    public static final List<String> HIDDEN_KEYS = new ArrayList<String>();
    
    public static final Map<String, KeyBinding> KEYBINDINGS = new HashMap<String, KeyBinding>();
    
    public static final List<KeyBinding> SORTED_KEYS = new ArrayList<KeyBinding>();  
    
	public void loadConfiguration() {
		
        try {       
        	
        	InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("assets/rebind/rebind.json");
        	        	
            JsonObject internalConfig = (JsonObject) new JsonParser().parse(new InputStreamReader(inputStream, "UTF-8"));  
            
            inputStream.close();   
                                    
            this.useExternalConfig = internalConfig.get("use_external_config").getAsBoolean();   
            
            if (!this.useExternalConfig) {
            	
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
        	        	        	
			List<String> configLinesList = IOUtils.readLines(new InputStreamReader(inputStream, "UTF-8"));
			
			inputStream.close();
			
			configLinesList.remove(1);
			configLinesList.remove(1);
			
            PrintStream fileStream = new PrintStream(new File(configPath));
            
            for (String line : configLinesList) {
            	
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
		
        this.enableRewriting = configFile.get("rewrite_controls").getAsBoolean();
                
        Map<String, JsonObject> properties = new LinkedHashMap<String, JsonObject>();
        
        Set<Map.Entry<String, JsonElement>> entrySet;
        
        for (JsonElement jsonElement : configFile.get("controls").getAsJsonArray()) {
        	
        	entrySet = jsonElement.getAsJsonObject().entrySet();
        	
        	for (Map.Entry<String, JsonElement> entry : entrySet) {
        		
            	properties.put(entry.getKey(), entry.getValue().getAsJsonObject());
        	}
        }
        
        Map<String, KeyBindingProperty> enabledKeys = new HashMap<String, KeyBindingProperty>();
        
        int i = 0;
        
        JsonObject property;
        
        KeyBindingProperty keyBindingProperty;
                
        for (EnumKeys enumKey : EnumKeys.values()) {
        	
        	i = 0;
        	
        	for (String key : enumKey.getConfigKeys()) {
        		
        		if (properties.containsKey(key)) {
        			
        			property = properties.get(key);
        			
        			keyBindingProperty = new KeyBindingProperty(
        					enumKey.getDomain(), 
        					enumKey.getDefaultNames()[i], 
        					property.get("name").getAsString(), 
        					property.get("category").getAsString(), 
        					property.get("key_code").getAsInt(), 
        					property.get("enabled").getAsBoolean());     
        			
                    PROPERTIES.put(keyBindingProperty.getDefaultName(), keyBindingProperty);
                    
                    if (keyBindingProperty.isEnabled())
                    enabledKeys.put(key, keyBindingProperty);
                    else
                    HIDDEN_KEYS.add(keyBindingProperty.getDefaultName());
        		}
        		
        		i++;
        	}
        }
        
        for (String configKey : properties.keySet()) {
        	
        	if (enabledKeys.containsKey(configKey)) {
        		
        		SORTED_PROPERTIES.add(enabledKeys.get(configKey));
        	}
        }
	}
	
	public boolean shouldRewriteControlsSettings() {
		
		return this.enableRewriting;
	}
}
