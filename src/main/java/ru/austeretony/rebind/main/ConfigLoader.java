package ru.austeretony.rebind.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ConfigLoader {
	
	public boolean enableControlsRewriting;
	
	public KeyBindingProperty
	propertyAttack,
	propertyUseItem, 
	propertyPickBlock, 
	
	propertyForward,
	propertyLeft,
	propertyBack,
	propertyRight,
	propertyJump,
	propertySneak,
	propertySprint,
	propertyInventory,
	propertySwapHands,
	propertyDrop,
	propertyChat,
	propertyPlayerList,
	propertyCommand,
	propertyScreenshot,
	propertyTogglePerspective,
	propertySmoothCamera,
	propertyFullscreen,
	propertySpectatorOutlines,
	propertyHotbar1,
	propertyHotbar2,
	propertyHotbar3,
	propertyHotbar4,
	propertyHotbar5, 
	propertyHotbar6,
	propertyHotbar7,
	propertyHotbar8,
	propertyHotbar9,
	propertyQuit, 
	propertyHideGUI, 
	propertyDebugMenu, 
	propertySwitchShader;
	
    public final List<KeyBindingProperty> orderedProperties = new ArrayList<KeyBindingProperty>();
	
	@SideOnly(Side.CLIENT)
	public void loadConfiguration() {
		
        try {       	        	      
        	
        	InputStream inputStream = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(ReBindMain.MODID, "rebind.json")).getInputStream();

            JsonObject configFile = (JsonObject) new JsonParser().parse(new InputStreamReader(inputStream, "UTF-8"));
            
            this.enableControlsRewriting = configFile.get("rewrite_controls").getAsJsonObject().get("enabled").getAsBoolean();
            
            List<JsonObject> props = new ArrayList<JsonObject>();
            
            for (JsonElement jsonElement : configFile.get("controls").getAsJsonArray()) {
            	
        		props.add(jsonElement.getAsJsonObject());
            }
            
            KeyBindingProperty currentProperty = null;
            
            for (JsonObject jsonObj : props) {
            	
            	//Mouse
        		
        		if (jsonObj.has("attack"))
        		currentProperty = this.propertyAttack = new KeyBindingProperty(jsonObj.get("attack").getAsJsonObject().get("name").getAsString(), jsonObj.get("attack").getAsJsonObject().get("category").getAsString(), jsonObj.get("attack").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("attack").getAsJsonObject().get("enabled").getAsBoolean());     
        		
        		if (jsonObj.has("use_item"))
        		currentProperty = this.propertyUseItem = new KeyBindingProperty(jsonObj.get("use_item").getAsJsonObject().get("name").getAsString(), jsonObj.get("use_item").getAsJsonObject().get("category").getAsString(), jsonObj.get("use_item").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("use_item").getAsJsonObject().get("enabled").getAsBoolean());     
        		
        		if (jsonObj.has("pick_block"))
        		currentProperty = this.propertyPickBlock = new KeyBindingProperty(jsonObj.get("pick_block").getAsJsonObject().get("name").getAsString(), jsonObj.get("pick_block").getAsJsonObject().get("category").getAsString(), jsonObj.get("pick_block").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("pick_block").getAsJsonObject().get("enabled").getAsBoolean());     
            
        		//Keyboard
        		
        		if (jsonObj.has("forward"))
        		currentProperty = this.propertyForward = new KeyBindingProperty(jsonObj.get("forward").getAsJsonObject().get("name").getAsString(), jsonObj.get("forward").getAsJsonObject().get("category").getAsString(), jsonObj.get("forward").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("forward").getAsJsonObject().get("enabled").getAsBoolean()); 
            
        		if (jsonObj.has("left"))
        		currentProperty = this.propertyLeft = new KeyBindingProperty(jsonObj.get("left").getAsJsonObject().get("name").getAsString(), jsonObj.get("left").getAsJsonObject().get("category").getAsString(), jsonObj.get("left").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("left").getAsJsonObject().get("enabled").getAsBoolean()); 
        		
        		if (jsonObj.has("back"))
        		currentProperty = this.propertyBack = new KeyBindingProperty(jsonObj.get("back").getAsJsonObject().get("name").getAsString(), jsonObj.get("back").getAsJsonObject().get("category").getAsString(), jsonObj.get("back").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("back").getAsJsonObject().get("enabled").getAsBoolean()); 
        		
        		if (jsonObj.has("right"))
        		currentProperty = this.propertyRight = new KeyBindingProperty(jsonObj.get("right").getAsJsonObject().get("name").getAsString(), jsonObj.get("right").getAsJsonObject().get("category").getAsString(), jsonObj.get("right").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("right").getAsJsonObject().get("enabled").getAsBoolean()); 
        		
        		if (jsonObj.has("jump"))
        		currentProperty = this.propertyJump = new KeyBindingProperty(jsonObj.get("jump").getAsJsonObject().get("name").getAsString(), jsonObj.get("jump").getAsJsonObject().get("category").getAsString(), jsonObj.get("jump").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("jump").getAsJsonObject().get("enabled").getAsBoolean()); 
        		
        		if (jsonObj.has("sneak"))
        		currentProperty = this.propertySneak = new KeyBindingProperty(jsonObj.get("sneak").getAsJsonObject().get("name").getAsString(), jsonObj.get("sneak").getAsJsonObject().get("category").getAsString(), jsonObj.get("sneak").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("sneak").getAsJsonObject().get("enabled").getAsBoolean()); 
        		
        		if (jsonObj.has("sprint"))
        		currentProperty = this.propertySprint = new KeyBindingProperty(jsonObj.get("sprint").getAsJsonObject().get("name").getAsString(), jsonObj.get("sprint").getAsJsonObject().get("category").getAsString(), jsonObj.get("sprint").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("sprint").getAsJsonObject().get("enabled").getAsBoolean()); 
        		
        		if (jsonObj.has("inventory"))
        		currentProperty = this.propertyInventory = new KeyBindingProperty(jsonObj.get("inventory").getAsJsonObject().get("name").getAsString(), jsonObj.get("inventory").getAsJsonObject().get("category").getAsString(), jsonObj.get("inventory").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("inventory").getAsJsonObject().get("enabled").getAsBoolean()); 
        		
        		if (jsonObj.has("swap_hands"))
        		currentProperty = this.propertySwapHands = new KeyBindingProperty(jsonObj.get("swap_hands").getAsJsonObject().get("name").getAsString(), jsonObj.get("swap_hands").getAsJsonObject().get("category").getAsString(), jsonObj.get("swap_hands").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("swap_hands").getAsJsonObject().get("enabled").getAsBoolean()); 
        		
        		if (jsonObj.has("drop"))
        		currentProperty = this.propertyDrop = new KeyBindingProperty(jsonObj.get("drop").getAsJsonObject().get("name").getAsString(), jsonObj.get("drop").getAsJsonObject().get("category").getAsString(), jsonObj.get("drop").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("drop").getAsJsonObject().get("enabled").getAsBoolean());           
        		
        		if (jsonObj.has("chat"))
        		currentProperty = this.propertyChat = new KeyBindingProperty(jsonObj.get("chat").getAsJsonObject().get("name").getAsString(), jsonObj.get("chat").getAsJsonObject().get("category").getAsString(), jsonObj.get("chat").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("chat").getAsJsonObject().get("enabled").getAsBoolean()); 
        		
        		if (jsonObj.has("playerlist"))
        		currentProperty = this.propertyPlayerList = new KeyBindingProperty(jsonObj.get("playerlist").getAsJsonObject().get("name").getAsString(), jsonObj.get("playerlist").getAsJsonObject().get("category").getAsString(), jsonObj.get("playerlist").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("playerlist").getAsJsonObject().get("enabled").getAsBoolean()); 
        		
        		if (jsonObj.has("command"))
        		currentProperty = this.propertyCommand = new KeyBindingProperty(jsonObj.get("command").getAsJsonObject().get("name").getAsString(), jsonObj.get("command").getAsJsonObject().get("category").getAsString(), jsonObj.get("command").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("command").getAsJsonObject().get("enabled").getAsBoolean()); 
        		
        		if (jsonObj.has("screenshot"))
        		currentProperty = this.propertyScreenshot = new KeyBindingProperty(jsonObj.get("screenshot").getAsJsonObject().get("name").getAsString(), jsonObj.get("screenshot").getAsJsonObject().get("category").getAsString(), jsonObj.get("screenshot").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("screenshot").getAsJsonObject().get("enabled").getAsBoolean()); 
        		
        		if (jsonObj.has("toggle_perspective"))
        		currentProperty = this.propertyTogglePerspective = new KeyBindingProperty(jsonObj.get("toggle_perspective").getAsJsonObject().get("name").getAsString(), jsonObj.get("toggle_perspective").getAsJsonObject().get("category").getAsString(), jsonObj.get("toggle_perspective").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("toggle_perspective").getAsJsonObject().get("enabled").getAsBoolean()); 
        		
        		if (jsonObj.has("smooth_camera"))
        		currentProperty = this.propertySmoothCamera = new KeyBindingProperty(jsonObj.get("smooth_camera").getAsJsonObject().get("name").getAsString(), jsonObj.get("smooth_camera").getAsJsonObject().get("category").getAsString(), jsonObj.get("smooth_camera").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("smooth_camera").getAsJsonObject().get("enabled").getAsBoolean()); 
        		
        		if (jsonObj.has("fullscreen"))
        		currentProperty = this.propertyFullscreen = new KeyBindingProperty(jsonObj.get("fullscreen").getAsJsonObject().get("name").getAsString(), jsonObj.get("fullscreen").getAsJsonObject().get("category").getAsString(), jsonObj.get("fullscreen").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("fullscreen").getAsJsonObject().get("enabled").getAsBoolean()); 
        		
        		if (jsonObj.has("spectator_outlines"))
        		currentProperty = this.propertySpectatorOutlines = new KeyBindingProperty(jsonObj.get("spectator_outlines").getAsJsonObject().get("name").getAsString(), jsonObj.get("spectator_outlines").getAsJsonObject().get("category").getAsString(), jsonObj.get("spectator_outlines").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("spectator_outlines").getAsJsonObject().get("enabled").getAsBoolean()); 
        		
        		if (jsonObj.has("hotbar_1"))
        		currentProperty = this.propertyHotbar1 = new KeyBindingProperty(jsonObj.get("hotbar_1").getAsJsonObject().get("name").getAsString(), jsonObj.get("hotbar_1").getAsJsonObject().get("category").getAsString(), jsonObj.get("hotbar_1").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("hotbar_1").getAsJsonObject().get("enabled").getAsBoolean()); 
        		
        		if (jsonObj.has("hotbar_2"))
        		currentProperty = this.propertyHotbar2 = new KeyBindingProperty(jsonObj.get("hotbar_2").getAsJsonObject().get("name").getAsString(), jsonObj.get("hotbar_2").getAsJsonObject().get("category").getAsString(), jsonObj.get("hotbar_2").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("hotbar_2").getAsJsonObject().get("enabled").getAsBoolean()); 
        		
        		if (jsonObj.has("hotbar_3"))
        		currentProperty = this.propertyHotbar3 = new KeyBindingProperty(jsonObj.get("hotbar_3").getAsJsonObject().get("name").getAsString(), jsonObj.get("hotbar_3").getAsJsonObject().get("category").getAsString(), jsonObj.get("hotbar_3").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("hotbar_3").getAsJsonObject().get("enabled").getAsBoolean()); 
        		
        		if (jsonObj.has("hotbar_4"))
        		currentProperty = this.propertyHotbar4 = new KeyBindingProperty(jsonObj.get("hotbar_4").getAsJsonObject().get("name").getAsString(), jsonObj.get("hotbar_4").getAsJsonObject().get("category").getAsString(), jsonObj.get("hotbar_4").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("hotbar_4").getAsJsonObject().get("enabled").getAsBoolean()); 		
            
        		if (jsonObj.has("hotbar_5"))
        		currentProperty = this.propertyHotbar5 = new KeyBindingProperty(jsonObj.get("hotbar_5").getAsJsonObject().get("name").getAsString(), jsonObj.get("hotbar_5").getAsJsonObject().get("category").getAsString(), jsonObj.get("hotbar_5").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("hotbar_5").getAsJsonObject().get("enabled").getAsBoolean()); 
            		
            	if (jsonObj.has("hotbar_6"))
            	currentProperty = this.propertyHotbar6 = new KeyBindingProperty(jsonObj.get("hotbar_6").getAsJsonObject().get("name").getAsString(), jsonObj.get("hotbar_6").getAsJsonObject().get("category").getAsString(), jsonObj.get("hotbar_6").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("hotbar_6").getAsJsonObject().get("enabled").getAsBoolean()); 
            		
            	if (jsonObj.has("hotbar_7"))
            	currentProperty = this.propertyHotbar7 = new KeyBindingProperty(jsonObj.get("hotbar_7").getAsJsonObject().get("name").getAsString(), jsonObj.get("hotbar_7").getAsJsonObject().get("category").getAsString(), jsonObj.get("hotbar_7").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("hotbar_7").getAsJsonObject().get("enabled").getAsBoolean()); 
            		
            	if (jsonObj.has("hotbar_8"))
            	currentProperty = this.propertyHotbar8 = new KeyBindingProperty(jsonObj.get("hotbar_8").getAsJsonObject().get("name").getAsString(), jsonObj.get("hotbar_8").getAsJsonObject().get("category").getAsString(), jsonObj.get("hotbar_8").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("hotbar_8").getAsJsonObject().get("enabled").getAsBoolean()); 	
            		
            	if (jsonObj.has("hotbar_9"))
            	currentProperty = this.propertyHotbar9 = new KeyBindingProperty(jsonObj.get("hotbar_9").getAsJsonObject().get("name").getAsString(), jsonObj.get("hotbar_9").getAsJsonObject().get("category").getAsString(), jsonObj.get("hotbar_9").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("hotbar_9").getAsJsonObject().get("enabled").getAsBoolean()); 
                		
                if (jsonObj.has("quit"))
                currentProperty = this.propertyQuit = new KeyBindingProperty(jsonObj.get("quit").getAsJsonObject().get("name").getAsString(), jsonObj.get("quit").getAsJsonObject().get("category").getAsString(), jsonObj.get("quit").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("quit").getAsJsonObject().get("enabled").getAsBoolean()); 
                		
                if (jsonObj.has("hide_hud"))
                currentProperty = this.propertyHideGUI = new KeyBindingProperty(jsonObj.get("hide_hud").getAsJsonObject().get("name").getAsString(), jsonObj.get("hide_hud").getAsJsonObject().get("category").getAsString(), jsonObj.get("hide_hud").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("hide_hud").getAsJsonObject().get("enabled").getAsBoolean()); 
                		
                if (jsonObj.has("debug_screen"))
                currentProperty = this.propertyDebugMenu = new KeyBindingProperty(jsonObj.get("debug_screen").getAsJsonObject().get("name").getAsString(), jsonObj.get("debug_screen").getAsJsonObject().get("category").getAsString(), jsonObj.get("debug_screen").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("debug_screen").getAsJsonObject().get("enabled").getAsBoolean()); 	
            
                if (jsonObj.has("switch_shader"))
                currentProperty = this.propertySwitchShader = new KeyBindingProperty(jsonObj.get("switch_shader").getAsJsonObject().get("name").getAsString(), jsonObj.get("switch_shader").getAsJsonObject().get("category").getAsString(), jsonObj.get("switch_shader").getAsJsonObject().get("key_code").getAsInt(), props.indexOf(jsonObj), jsonObj.get("switch_shader").getAsJsonObject().get("enabled").getAsBoolean()); 
            
                if (currentProperty.isEnabled())
                this.orderedProperties.add(currentProperty);
            }
                		            
            inputStream.close();        
        }
        
        catch (IOException exception) {
        	
        	exception.printStackTrace();
        }
    }
}
