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

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.relauncher.FMLInjectionData;

public class ConfigLoader {
	
	private static boolean enableRewriting, enableDebugMode;
	
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
                                    
            boolean useExternalConfig = internalConfig.get("use_external").getAsBoolean();   
            
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
				
        enableRewriting = configFile.get("rewrite").getAsBoolean();
        
        enableDebugMode = configFile.get("debug_mode").getAsBoolean();
                
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
					rawProperty.get("mod").getAsString(), 
					rawProperty.get("enabled").getAsBoolean());     
			
            PROPERTIES.put(property.getConfigKey(), property);
            
            if (property.isEnabled()) 
        	SORTED_PROPERTIES.add(property);
            else
            HIDDEN_KEYBINDINGS.add(property.getConfigKey());
    	}
	}
	
	public static boolean isControllsSettingsRewritingAllowed() {
		
		return enableRewriting;
	}
	
	public static boolean isDebugModeEnabled() {
		
		return enableDebugMode;
	}
}
