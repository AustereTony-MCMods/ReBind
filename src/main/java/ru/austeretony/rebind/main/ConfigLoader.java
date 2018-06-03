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
import java.util.List;
import java.util.Map;

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
    
	public void loadExternalConfig(JsonObject internalConfig) {

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
	
    public void createExternalCopyAndLoad(String configPath, JsonObject internalConfig) {
    	
        try {
        	
        	InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("assets/rebind/rebind.json");
        	        	        	
			List<String> configLinesList = IOUtils.readLines(new InputStreamReader(inputStream, "UTF-8"));
			
			inputStream.close();
			
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
                
        List<JsonObject> properties = new ArrayList<JsonObject>();
        
        for (JsonElement jsonElement : configFile.get("controls").getAsJsonArray()) {
        	
        	properties.add(jsonElement.getAsJsonObject());
        }
        
        KeyBindingProperty currentProperty = null;
        
        for (JsonObject jsonObj : properties) {
        	
        	//***Minecraft***//           	
    		
    		if (jsonObj.has("mc_attack"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.attack", jsonObj.get("mc_attack").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_attack").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_attack").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_attack").getAsJsonObject().get("enabled").getAsBoolean());     
    		
    		if (jsonObj.has("mc_use_item"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.use", jsonObj.get("mc_use_item").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_use_item").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_use_item").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_use_item").getAsJsonObject().get("enabled").getAsBoolean());     
    		
    		if (jsonObj.has("mc_pick_block"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.pickItem", jsonObj.get("mc_pick_block").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_pick_block").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_pick_block").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_pick_block").getAsJsonObject().get("enabled").getAsBoolean());               
    		
    		
    		if (jsonObj.has("mc_forward"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.forward", jsonObj.get("mc_forward").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_forward").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_forward").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_forward").getAsJsonObject().get("enabled").getAsBoolean()); 
        
    		if (jsonObj.has("mc_left"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.left", jsonObj.get("mc_left").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_left").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_left").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_left").getAsJsonObject().get("enabled").getAsBoolean()); 
    		
    		if (jsonObj.has("mc_back"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.back", jsonObj.get("mc_back").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_back").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_back").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_back").getAsJsonObject().get("enabled").getAsBoolean()); 
    		
    		if (jsonObj.has("mc_right"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.right", jsonObj.get("mc_right").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_right").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_right").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_right").getAsJsonObject().get("enabled").getAsBoolean()); 
    		
    		if (jsonObj.has("mc_jump"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.jump", jsonObj.get("mc_jump").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_jump").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_jump").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_jump").getAsJsonObject().get("enabled").getAsBoolean()); 
    		
    		if (jsonObj.has("mc_sneak"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.sneak", jsonObj.get("mc_sneak").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_sneak").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_sneak").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_sneak").getAsJsonObject().get("enabled").getAsBoolean()); 
    		
    		if (jsonObj.has("mc_sprint"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.sprint", jsonObj.get("mc_sprint").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_sprint").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_sprint").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_sprint").getAsJsonObject().get("enabled").getAsBoolean()); 
    		
    		if (jsonObj.has("mc_inventory"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.inventory", jsonObj.get("mc_inventory").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_inventory").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_inventory").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_inventory").getAsJsonObject().get("enabled").getAsBoolean()); 
    		
    		if (jsonObj.has("mc_drop"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.drop", jsonObj.get("mc_drop").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_drop").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_drop").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_drop").getAsJsonObject().get("enabled").getAsBoolean());           
    		
    		if (jsonObj.has("mc_chat"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.chat", jsonObj.get("mc_chat").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_chat").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_chat").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_chat").getAsJsonObject().get("enabled").getAsBoolean()); 
    		
    		if (jsonObj.has("mc_playerlist"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.playerlist", jsonObj.get("mc_playerlist").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_playerlist").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_playerlist").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_playerlist").getAsJsonObject().get("enabled").getAsBoolean()); 
    		
    		if (jsonObj.has("mc_command"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.command", jsonObj.get("mc_command").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_command").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_command").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_command").getAsJsonObject().get("enabled").getAsBoolean()); 
    		
    		if (jsonObj.has("mc_screenshot"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.screenshot", jsonObj.get("mc_screenshot").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_screenshot").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_screenshot").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_screenshot").getAsJsonObject().get("enabled").getAsBoolean()); 
    		
    		if (jsonObj.has("mc_toggle_perspective"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.togglePerspective", jsonObj.get("mc_toggle_perspective").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_toggle_perspective").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_toggle_perspective").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_toggle_perspective").getAsJsonObject().get("enabled").getAsBoolean()); 
    		
    		if (jsonObj.has("mc_smooth_camera"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.smoothCamera", jsonObj.get("mc_smooth_camera").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_smooth_camera").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_smooth_camera").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_smooth_camera").getAsJsonObject().get("enabled").getAsBoolean()); 
    		
    		if (jsonObj.has("mc_fullscreen"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.fullscreen", jsonObj.get("mc_fullscreen").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_fullscreen").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_fullscreen").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_fullscreen").getAsJsonObject().get("enabled").getAsBoolean()); 

    		if (jsonObj.has("mc_stream_start_stop"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.streamStartStop", jsonObj.get("mc_stream_start_stop").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_stream_start_stop").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_stream_start_stop").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_stream_start_stop").getAsJsonObject().get("enabled").getAsBoolean()); 
    		
    		if (jsonObj.has("mc_stream_pause_unpause"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.streamPauseUnpause", jsonObj.get("mc_stream_pause_unpause").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_stream_pause_unpause").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_stream_pause_unpause").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_stream_pause_unpause").getAsJsonObject().get("enabled").getAsBoolean()); 
    		
    		if (jsonObj.has("mc_stream_commercial"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.streamCommercial", jsonObj.get("mc_stream_commercial").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_stream_commercial").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_stream_commercial").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_stream_commercial").getAsJsonObject().get("enabled").getAsBoolean()); 
    		
    		if (jsonObj.has("mc_stream_toggle_mic"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.streamToggleMic", jsonObj.get("mc_stream_toggle_mic").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_stream_toggle_mic").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_stream_toggle_mic").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_stream_toggle_mic").getAsJsonObject().get("enabled").getAsBoolean()); 	
    		
    		if (jsonObj.has("mc_hotbar_1"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.hotbar.1", jsonObj.get("mc_hotbar_1").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_hotbar_1").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_hotbar_1").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_hotbar_1").getAsJsonObject().get("enabled").getAsBoolean()); 
    		
    		if (jsonObj.has("mc_hotbar_2"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.hotbar.2", jsonObj.get("mc_hotbar_2").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_hotbar_2").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_hotbar_2").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_hotbar_2").getAsJsonObject().get("enabled").getAsBoolean()); 
    		
    		if (jsonObj.has("mc_hotbar_3"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.hotbar.3", jsonObj.get("mc_hotbar_3").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_hotbar_3").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_hotbar_3").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_hotbar_3").getAsJsonObject().get("enabled").getAsBoolean()); 
    		
    		if (jsonObj.has("mc_hotbar_4"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.hotbar.4", jsonObj.get("mc_hotbar_4").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_hotbar_4").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_hotbar_4").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_hotbar_4").getAsJsonObject().get("enabled").getAsBoolean()); 		
        
    		if (jsonObj.has("mc_hotbar_5"))
    		currentProperty = new KeyBindingProperty("minecraft", "key.hotbar.5", jsonObj.get("mc_hotbar_5").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_hotbar_5").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_hotbar_5").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_hotbar_5").getAsJsonObject().get("enabled").getAsBoolean()); 
        		
        	if (jsonObj.has("mc_hotbar_6"))
        	currentProperty = new KeyBindingProperty("minecraft", "key.hotbar.6", jsonObj.get("mc_hotbar_6").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_hotbar_6").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_hotbar_6").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_hotbar_6").getAsJsonObject().get("enabled").getAsBoolean()); 
        		
        	if (jsonObj.has("mc_hotbar_7"))
        	currentProperty = new KeyBindingProperty("minecraft", "key.hotbar.7", jsonObj.get("mc_hotbar_7").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_hotbar_7").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_hotbar_7").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_hotbar_7").getAsJsonObject().get("enabled").getAsBoolean()); 
        		
        	if (jsonObj.has("mc_hotbar_8"))
        	currentProperty = new KeyBindingProperty("minecraft", "key.hotbar.8", jsonObj.get("mc_hotbar_8").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_hotbar_8").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_hotbar_8").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_hotbar_8").getAsJsonObject().get("enabled").getAsBoolean()); 	
        		
        	if (jsonObj.has("mc_hotbar_9"))
        	currentProperty = new KeyBindingProperty("minecraft", "key.hotbar.9", jsonObj.get("mc_hotbar_9").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_hotbar_9").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_hotbar_9").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_hotbar_9").getAsJsonObject().get("enabled").getAsBoolean()); 
            		
            if (jsonObj.has("mc_quit"))
            currentProperty = new KeyBindingProperty("minecraft", "key.quit", jsonObj.get("mc_quit").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_quit").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_quit").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_quit").getAsJsonObject().get("enabled").getAsBoolean()); 
            		
            if (jsonObj.has("mc_hide_hud"))
            currentProperty = new KeyBindingProperty("minecraft", "key.hideHUD", jsonObj.get("mc_hide_hud").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_hide_hud").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_hide_hud").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_hide_hud").getAsJsonObject().get("enabled").getAsBoolean()); 
            		
            if (jsonObj.has("mc_debug_screen"))
            currentProperty = new KeyBindingProperty("minecraft", "key.debugScreen", jsonObj.get("mc_debug_screen").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_debug_screen").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_debug_screen").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_debug_screen").getAsJsonObject().get("enabled").getAsBoolean()); 	
        
            if (jsonObj.has("mc_disable_shader"))
            currentProperty = new KeyBindingProperty("minecraft", "key.disableShader", jsonObj.get("mc_disable_shader").getAsJsonObject().get("name").getAsString(), jsonObj.get("mc_disable_shader").getAsJsonObject().get("category").getAsString(), jsonObj.get("mc_disable_shader").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("mc_disable_shader").getAsJsonObject().get("enabled").getAsBoolean()); 
        
            //***Baubles***//
            
            if (jsonObj.has("ba_open_inventory"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.BAUBLES.getDomain(), EnumModsKeys.BAUBLES.getKeysNames()[0], jsonObj.get("ba_open_inventory").getAsJsonObject().get("name").getAsString(), jsonObj.get("ba_open_inventory").getAsJsonObject().get("category").getAsString(), jsonObj.get("ba_open_inventory").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("ba_open_inventory").getAsJsonObject().get("enabled").getAsBoolean());           
               
            //***Better Builder's Wands***//
            
            if (jsonObj.has("bw_fluid_mode"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.BB_WANDS.getDomain(), EnumModsKeys.BB_WANDS.getKeysNames()[0], jsonObj.get("bw_fluid_mode").getAsJsonObject().get("name").getAsString(), jsonObj.get("bw_fluid_mode").getAsJsonObject().get("category").getAsString(), jsonObj.get("bw_fluid_mode").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("bw_fluid_mode").getAsJsonObject().get("enabled").getAsBoolean());           
            
            if (jsonObj.has("bw_mode"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.BB_WANDS.getDomain(), EnumModsKeys.BB_WANDS.getKeysNames()[1], jsonObj.get("bw_mode").getAsJsonObject().get("name").getAsString(), jsonObj.get("bw_mode").getAsJsonObject().get("category").getAsString(), jsonObj.get("bw_mode").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("bw_mode").getAsJsonObject().get("enabled").getAsBoolean());                          

            //***CoFHCore***//
            
            if (jsonObj.has("cc_multimode"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.COFH_CORE.getDomain(), EnumModsKeys.COFH_CORE.getKeysNames()[0], jsonObj.get("cc_multimode").getAsJsonObject().get("name").getAsString(), jsonObj.get("cc_multimode").getAsJsonObject().get("category").getAsString(), jsonObj.get("cc_multimode").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("cc_multimode").getAsJsonObject().get("enabled").getAsBoolean());           
            
            if (jsonObj.has("cc_empower"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.COFH_CORE.getDomain(), EnumModsKeys.COFH_CORE.getKeysNames()[1], jsonObj.get("cc_empower").getAsJsonObject().get("name").getAsString(), jsonObj.get("cc_empower").getAsJsonObject().get("category").getAsString(), jsonObj.get("cc_empower").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("cc_empower").getAsJsonObject().get("enabled").getAsBoolean());                          
            
            //***GraviSuit***//
            
            if (jsonObj.has("gs_display_hud"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.GRAVI_SUITE.getDomain(), EnumModsKeys.GRAVI_SUITE.getKeysNames()[0], jsonObj.get("gs_display_hud").getAsJsonObject().get("name").getAsString(), jsonObj.get("gs_display_hud").getAsJsonObject().get("category").getAsString(), jsonObj.get("gs_display_hud").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("gs_display_hud").getAsJsonObject().get("enabled").getAsBoolean());           
            
            if (jsonObj.has("gs_toggle_fly"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.GRAVI_SUITE.getDomain(), EnumModsKeys.GRAVI_SUITE.getKeysNames()[1], jsonObj.get("gs_toggle_fly").getAsJsonObject().get("name").getAsString(), jsonObj.get("gs_toggle_fly").getAsJsonObject().get("category").getAsString(), jsonObj.get("gs_toggle_fly").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("gs_toggle_fly").getAsJsonObject().get("enabled").getAsBoolean());                                       
            
            //***Industrial Craft 2***//
            
            if (jsonObj.has("ic_alt"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.IC2.getDomain(), EnumModsKeys.IC2.getKeysNames()[0], jsonObj.get("ic_alt").getAsJsonObject().get("name").getAsString(), jsonObj.get("ic_alt").getAsJsonObject().get("category").getAsString(), jsonObj.get("ic_alt").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("ic_alt").getAsJsonObject().get("enabled").getAsBoolean());           
            
            if (jsonObj.has("ic_boost"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.IC2.getDomain(), EnumModsKeys.IC2.getKeysNames()[1], jsonObj.get("ic_boost").getAsJsonObject().get("name").getAsString(), jsonObj.get("ic_boost").getAsJsonObject().get("category").getAsString(), jsonObj.get("ic_boost").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("ic_boost").getAsJsonObject().get("enabled").getAsBoolean());           
            
            if (jsonObj.has("ic_hub_expand"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.IC2.getDomain(), EnumModsKeys.IC2.getKeysNames()[2], jsonObj.get("ic_hub_expand").getAsJsonObject().get("name").getAsString(), jsonObj.get("ic_hub_expand").getAsJsonObject().get("category").getAsString(), jsonObj.get("ic_hub_expand").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("ic_hub_expand").getAsJsonObject().get("enabled").getAsBoolean());           
            
            if (jsonObj.has("ic_mode_switch"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.IC2.getDomain(), EnumModsKeys.IC2.getKeysNames()[3], jsonObj.get("ic_mode_switch").getAsJsonObject().get("name").getAsString(), jsonObj.get("ic_mode_switch").getAsJsonObject().get("category").getAsString(), jsonObj.get("ic_mode_switch").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("ic_mode_switch").getAsJsonObject().get("enabled").getAsBoolean());                              
            
            if (jsonObj.has("ic_side_inventory"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.IC2.getDomain(), EnumModsKeys.IC2.getKeysNames()[4], jsonObj.get("ic_side_inventory").getAsJsonObject().get("name").getAsString(), jsonObj.get("ic_side_inventory").getAsJsonObject().get("category").getAsString(), jsonObj.get("ic_side_inventory").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("ic_side_inventory").getAsJsonObject().get("enabled").getAsBoolean());                             
            
            //***Inventory Tweaks***//
            
            if (jsonObj.has("it_sort_inventory"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.INV_TWEAKS.getDomain(), EnumModsKeys.INV_TWEAKS.getKeysNames()[0], jsonObj.get("it_sort_inventory").getAsJsonObject().get("name").getAsString(), jsonObj.get("it_sort_inventory").getAsJsonObject().get("category").getAsString(), jsonObj.get("it_sort_inventory").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("it_sort_inventory").getAsJsonObject().get("enabled").getAsBoolean());                        
            
            //***Iron Backpacks***//
            
            if (jsonObj.has("ib_equip_backpack"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.IRON_BACKPACKS.getDomain(), EnumModsKeys.IRON_BACKPACKS.getKeysNames()[0], jsonObj.get("ib_equip_backpack").getAsJsonObject().get("name").getAsString(), jsonObj.get("ib_equip_backpack").getAsJsonObject().get("category").getAsString(), jsonObj.get("ib_equip_backpack").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("ib_equip_backpack").getAsJsonObject().get("enabled").getAsBoolean());           
            
            if (jsonObj.has("ib_open_backpack"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.IRON_BACKPACKS.getDomain(), EnumModsKeys.IRON_BACKPACKS.getKeysNames()[1], jsonObj.get("ib_open_backpack").getAsJsonObject().get("name").getAsString(), jsonObj.get("ib_open_backpack").getAsJsonObject().get("category").getAsString(), jsonObj.get("ib_open_backpack").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("ib_open_backpack").getAsJsonObject().get("enabled").getAsBoolean());                                   
            
            //***RPG Inventory***//
            
            if (jsonObj.has("ri_open_inventory"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.RPG_INVENTORY.getDomain(), EnumModsKeys.RPG_INVENTORY.getKeysNames()[0], jsonObj.get("ri_open_inventory").getAsJsonObject().get("name").getAsString(), jsonObj.get("ri_open_inventory").getAsJsonObject().get("category").getAsString(), jsonObj.get("ri_open_inventory").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("ri_open_inventory").getAsJsonObject().get("enabled").getAsBoolean());           
               
            if (jsonObj.has("ri_spec_ability"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.RPG_INVENTORY.getDomain(), EnumModsKeys.RPG_INVENTORY.getKeysNames()[1], jsonObj.get("ri_spec_ability").getAsJsonObject().get("name").getAsString(), jsonObj.get("ri_spec_ability").getAsJsonObject().get("category").getAsString(), jsonObj.get("ri_spec_ability").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("ri_spec_ability").getAsJsonObject().get("enabled").getAsBoolean());                  
            
            //***Thaumcraft***//
            
            if (jsonObj.has("th_activate_harness"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.THAUMCRAFT.getDomain(), EnumModsKeys.THAUMCRAFT.getKeysNames()[0], jsonObj.get("th_activate_harness").getAsJsonObject().get("name").getAsString(), jsonObj.get("th_activate_harness").getAsJsonObject().get("category").getAsString(), jsonObj.get("th_activate_harness").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("th_activate_harness").getAsJsonObject().get("enabled").getAsBoolean());                      
            
            if (jsonObj.has("th_change_focus"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.THAUMCRAFT.getDomain(), EnumModsKeys.THAUMCRAFT.getKeysNames()[1], jsonObj.get("th_change_focus").getAsJsonObject().get("name").getAsString(), jsonObj.get("th_change_focus").getAsJsonObject().get("category").getAsString(), jsonObj.get("th_change_focus").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("th_change_focus").getAsJsonObject().get("enabled").getAsBoolean());           
              
            if (jsonObj.has("th_misc_toggle"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.THAUMCRAFT.getDomain(), EnumModsKeys.THAUMCRAFT.getKeysNames()[2], jsonObj.get("th_misc_toggle").getAsJsonObject().get("name").getAsString(), jsonObj.get("th_misc_toggle").getAsJsonObject().get("category").getAsString(), jsonObj.get("th_misc_toggle").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("th_misc_toggle").getAsJsonObject().get("enabled").getAsBoolean());                             
            
            //***Xaero's Minimap***//
            
            if (jsonObj.has("xm_enlarge_map"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.XAERO_MINIMAP.getDomain(), EnumModsKeys.XAERO_MINIMAP.getKeysNames()[0], jsonObj.get("xm_enlarge_map").getAsJsonObject().get("name").getAsString(), jsonObj.get("xm_enlarge_map").getAsJsonObject().get("category").getAsString(), jsonObj.get("xm_enlarge_map").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("xm_enlarge_map").getAsJsonObject().get("enabled").getAsBoolean());                                 
            
            if (jsonObj.has("xm_minimap_settings"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.XAERO_MINIMAP.getDomain(), EnumModsKeys.XAERO_MINIMAP.getKeysNames()[1], jsonObj.get("xm_minimap_settings").getAsJsonObject().get("name").getAsString(), jsonObj.get("xm_minimap_settings").getAsJsonObject().get("category").getAsString(), jsonObj.get("xm_minimap_settings").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("xm_minimap_settings").getAsJsonObject().get("enabled").getAsBoolean());                                 
            
            if (jsonObj.has("xm_zoom_in"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.XAERO_MINIMAP.getDomain(), EnumModsKeys.XAERO_MINIMAP.getKeysNames()[2], jsonObj.get("xm_zoom_in").getAsJsonObject().get("name").getAsString(), jsonObj.get("xm_zoom_in").getAsJsonObject().get("category").getAsString(), jsonObj.get("xm_zoom_in").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("xm_zoom_in").getAsJsonObject().get("enabled").getAsBoolean());                                 
            
            if (jsonObj.has("xm_zoom_out"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.XAERO_MINIMAP.getDomain(), EnumModsKeys.XAERO_MINIMAP.getKeysNames()[3], jsonObj.get("xm_zoom_out").getAsJsonObject().get("name").getAsString(), jsonObj.get("xm_zoom_out").getAsJsonObject().get("category").getAsString(), jsonObj.get("xm_zoom_out").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("xm_zoom_out").getAsJsonObject().get("enabled").getAsBoolean());                                 
            
            if (jsonObj.has("xm_new_waypoint"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.XAERO_MINIMAP.getDomain(), EnumModsKeys.XAERO_MINIMAP.getKeysNames()[4], jsonObj.get("xm_new_waypoint").getAsJsonObject().get("name").getAsString(), jsonObj.get("xm_new_waypoint").getAsJsonObject().get("category").getAsString(), jsonObj.get("xm_new_waypoint").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("xm_new_waypoint").getAsJsonObject().get("enabled").getAsBoolean());                                 
            
            if (jsonObj.has("xm_instant_waypoint"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.XAERO_MINIMAP.getDomain(), EnumModsKeys.XAERO_MINIMAP.getKeysNames()[5], jsonObj.get("xm_instant_waypoint").getAsJsonObject().get("name").getAsString(), jsonObj.get("xm_instant_waypoint").getAsJsonObject().get("category").getAsString(), jsonObj.get("xm_instant_waypoint").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("xm_instant_waypoint").getAsJsonObject().get("enabled").getAsBoolean());                                 
            
            if (jsonObj.has("xm_switch_waypoint_set"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.XAERO_MINIMAP.getDomain(), EnumModsKeys.XAERO_MINIMAP.getKeysNames()[6], jsonObj.get("xm_switch_waypoint_set").getAsJsonObject().get("name").getAsString(), jsonObj.get("xm_switch_waypoint_set").getAsJsonObject().get("category").getAsString(), jsonObj.get("xm_switch_waypoint_set").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("xm_switch_waypoint_set").getAsJsonObject().get("enabled").getAsBoolean());                                 
            
            if (jsonObj.has("xm_toggle_grid"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.XAERO_MINIMAP.getDomain(), EnumModsKeys.XAERO_MINIMAP.getKeysNames()[7], jsonObj.get("xm_toggle_grid").getAsJsonObject().get("name").getAsString(), jsonObj.get("xm_toggle_grid").getAsJsonObject().get("category").getAsString(), jsonObj.get("xm_toggle_grid").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("xm_toggle_grid").getAsJsonObject().get("enabled").getAsBoolean());                                 
            
            if (jsonObj.has("xm_toggle_map"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.XAERO_MINIMAP.getDomain(), EnumModsKeys.XAERO_MINIMAP.getKeysNames()[8], jsonObj.get("xm_toggle_map").getAsJsonObject().get("name").getAsString(), jsonObj.get("xm_toggle_map").getAsJsonObject().get("category").getAsString(), jsonObj.get("xm_toggle_map").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("xm_toggle_map").getAsJsonObject().get("enabled").getAsBoolean());                                 
            
            if (jsonObj.has("xm_toggle_slime"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.XAERO_MINIMAP.getDomain(), EnumModsKeys.XAERO_MINIMAP.getKeysNames()[9], jsonObj.get("xm_toggle_slime").getAsJsonObject().get("name").getAsString(), jsonObj.get("xm_toggle_slime").getAsJsonObject().get("category").getAsString(), jsonObj.get("xm_toggle_slime").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("xm_toggle_slime").getAsJsonObject().get("enabled").getAsBoolean());                                 
            
            if (jsonObj.has("xm_toggle_waypoints"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.XAERO_MINIMAP.getDomain(), EnumModsKeys.XAERO_MINIMAP.getKeysNames()[10], jsonObj.get("xm_toggle_waypoints").getAsJsonObject().get("name").getAsString(), jsonObj.get("xm_toggle_waypoints").getAsJsonObject().get("category").getAsString(), jsonObj.get("xm_toggle_waypoints").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("xm_toggle_waypoints").getAsJsonObject().get("enabled").getAsBoolean());                                 
            
            if (jsonObj.has("xm_waypoints_key"))
            currentProperty = new KeyBindingProperty(EnumModsKeys.XAERO_MINIMAP.getDomain(), EnumModsKeys.XAERO_MINIMAP.getKeysNames()[11], jsonObj.get("xm_waypoints_key").getAsJsonObject().get("name").getAsString(), jsonObj.get("xm_waypoints_key").getAsJsonObject().get("category").getAsString(), jsonObj.get("xm_waypoints_key").getAsJsonObject().get("key_code").getAsInt(), jsonObj.get("xm_waypoints_key").getAsJsonObject().get("enabled").getAsBoolean());                                 

            PROPERTIES.put(currentProperty.getDefaultName(), currentProperty);
                        
            if (currentProperty.isEnabled())
            SORTED_PROPERTIES.add(currentProperty);
            else
            HIDDEN_KEYS.add(currentProperty.getDefaultName());
        }                		            
	}
	
	public boolean shouldRewriteControlsSettings() {
		
		return this.enableRewriting;
	}
}
