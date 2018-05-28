package ru.austeretony.rebind.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ConfigLoader {
	
	public String
	categoryAttack,
	categoryUseItem, 
	categoryPickBlock, 
	
	categoryForward,
	categoryLeft,
	categoryBack,
	categoryRight,
	categoryJump,
	categorySneak,
	categorySprint,
	categoryInventory,
	categorySwapHands,
	categoryDrop,
	categoryChat,
	categoryPlayerList,
	categoryCommand,
	categoryScreenshot,
	categoryTogglePerspective,
	categorySmoothCamera,
	categoryFullscreen,
	categorySpectatorOutlines,
	categoryHotbar1,
	categoryHotbar2,
	categoryHotbar3,
	categoryHotbar4,
	categoryHotbar5, 
	categoryHotbar6,
	categoryHotbar7,
	categoryHotbar8,
	categoryHotbar9,
	categoryQuit, 
	categoryHideGUI, 
	categoryDebugMenu, 
	categorySwitchShader;
	
	public int 
	keyCodeAttack, 
	keyCodeUseItem, 
	keyCodePickBlock, 
	
	keyCodeForward,
	keyCodeLeft,
	keyCodeBack,
	keyCodeRight,
	keyCodeJump,
	keyCodeSneak,
	keyCodeSprint,
	keyCodeInventory,
	keyCodeSwapHands,
	keyCodeDrop,
	keyCodeChat,
	keyCodePlayerList,
	keyCodeCommand,
	keyCodeScreenshot,
	keyCodeTogglePerspective,
	keyCodeSmoothCamera,
	keyCodeFullscreen,
	keyCodeSpectatorOutlines,
	keyCodeHotbar1,
	keyCodeHotbar2,
	keyCodeHotbar3,
	keyCodeHotbar4,
	keyCodeHotbar5, 
	keyCodeHotbar6,
	keyCodeHotbar7,
	keyCodeHotbar8,
	keyCodeHotbar9,
	keyCodeQuit, 
	keyCodeHideGUI, 
	keyCodeDebugMenu, 
	keyCodeSwitchShader;
	
	public boolean 
	enableControlsRewriting,
	
	enableAttack, 
	enableUseItem, 
	enablePickBlock, 
	
	enableForward,
	enableLeft,
	enableBack,
	enableRight,
	enableJump,
	enableSneak,
	enableSprint,
	enableInventory,
	enableSwapHands,
	enableDrop,
	enableChat,
	enablePlayerList,
	enableCommand,
	enableScreenshot,
	enableTogglePerspective,
	enableSmoothCamera,
	enableFullscreen,
	enableSpectatorOutlines,
	enableHotbar1,
	enableHotbar2,
	enableHotbar3,
	enableHotbar4,
	enableHotbar5, 
	enableHotbar6,
	enableHotbar7,
	enableHotbar8,
	enableHotbar9,
	enableQuit, 
	enableHideGUI, 
	enableDebugMenu, 
	enableSwitchShader;
	
	@SideOnly(Side.CLIENT)
	public void loadConfiguration() {
		
        try {
        	
        	InputStream inputStream = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(ReBindMain.MODID, "config/config.json")).getInputStream();

            JsonObject configFile = (JsonObject) new JsonParser().parse(new InputStreamReader(inputStream, "UTF-8"));
                    	
            enableControlsRewriting = configFile.get("rewrite_controls").getAsJsonObject().get("enabled").getAsBoolean();
            
            //Mouse
            
            categoryAttack = configFile.get("attack").getAsJsonObject().get("category").getAsString();
            keyCodeAttack = configFile.get("attack").getAsJsonObject().get("key_code").getAsInt();
            enableAttack = configFile.get("attack").getAsJsonObject().get("enabled").getAsBoolean();
            
            categoryUseItem = configFile.get("use_item").getAsJsonObject().get("category").getAsString();
            keyCodeUseItem = configFile.get("use_item").getAsJsonObject().get("key_code").getAsInt();
            enableUseItem = configFile.get("use_item").getAsJsonObject().get("enabled").getAsBoolean();
            
            categoryPickBlock = configFile.get("pick_block").getAsJsonObject().get("category").getAsString();
            keyCodePickBlock = configFile.get("pick_block").getAsJsonObject().get("key_code").getAsInt();
            enablePickBlock = configFile.get("pick_block").getAsJsonObject().get("enabled").getAsBoolean();
                                  
            //Keyboard
            
            categoryForward = configFile.get("forward").getAsJsonObject().get("category").getAsString();
            keyCodeForward = configFile.get("forward").getAsJsonObject().get("key_code").getAsInt();
            enableForward = configFile.get("forward").getAsJsonObject().get("enabled").getAsBoolean();
            
            categoryLeft = configFile.get("left").getAsJsonObject().get("category").getAsString();
            keyCodeLeft = configFile.get("left").getAsJsonObject().get("key_code").getAsInt();
            enableLeft = configFile.get("left").getAsJsonObject().get("enabled").getAsBoolean();
            
            categoryBack = configFile.get("back").getAsJsonObject().get("category").getAsString();
            keyCodeBack = configFile.get("back").getAsJsonObject().get("key_code").getAsInt();
            enableBack = configFile.get("back").getAsJsonObject().get("enabled").getAsBoolean();
            
            categoryRight = configFile.get("right").getAsJsonObject().get("category").getAsString();
            keyCodeRight = configFile.get("right").getAsJsonObject().get("key_code").getAsInt();
            enableRight = configFile.get("right").getAsJsonObject().get("enabled").getAsBoolean();
            
            categoryJump = configFile.get("jump").getAsJsonObject().get("category").getAsString();
            keyCodeJump = configFile.get("jump").getAsJsonObject().get("key_code").getAsInt();
            enableJump = configFile.get("jump").getAsJsonObject().get("enabled").getAsBoolean();
            
            categorySneak = configFile.get("sneak").getAsJsonObject().get("category").getAsString();
            keyCodeSneak = configFile.get("sneak").getAsJsonObject().get("key_code").getAsInt();
            enableSneak = configFile.get("sneak").getAsJsonObject().get("enabled").getAsBoolean();
            
            categorySprint = configFile.get("sprint").getAsJsonObject().get("category").getAsString();
            keyCodeSprint = configFile.get("sprint").getAsJsonObject().get("key_code").getAsInt();
            enableSprint = configFile.get("sprint").getAsJsonObject().get("enabled").getAsBoolean();
            
            categoryInventory = configFile.get("inventory").getAsJsonObject().get("category").getAsString();
            keyCodeInventory = configFile.get("inventory").getAsJsonObject().get("key_code").getAsInt();
            enableInventory = configFile.get("inventory").getAsJsonObject().get("enabled").getAsBoolean();
            
            categorySwapHands = configFile.get("swap_hands").getAsJsonObject().get("category").getAsString();
            keyCodeSwapHands = configFile.get("swap_hands").getAsJsonObject().get("key_code").getAsInt();
            enableSwapHands = configFile.get("swap_hands").getAsJsonObject().get("enabled").getAsBoolean();
            
            categoryDrop = configFile.get("drop").getAsJsonObject().get("category").getAsString();
            keyCodeDrop = configFile.get("drop").getAsJsonObject().get("key_code").getAsInt();
            enableDrop = configFile.get("drop").getAsJsonObject().get("enabled").getAsBoolean();
            
            categoryChat = configFile.get("chat").getAsJsonObject().get("category").getAsString();
            keyCodeChat = configFile.get("chat").getAsJsonObject().get("key_code").getAsInt();
            enableChat = configFile.get("chat").getAsJsonObject().get("enabled").getAsBoolean();
            
            categoryPlayerList = configFile.get("player_list").getAsJsonObject().get("category").getAsString();
            keyCodePlayerList = configFile.get("player_list").getAsJsonObject().get("key_code").getAsInt();
            enablePlayerList = configFile.get("player_list").getAsJsonObject().get("enabled").getAsBoolean();
            
            categoryCommand = configFile.get("command").getAsJsonObject().get("category").getAsString();
            keyCodeCommand = configFile.get("command").getAsJsonObject().get("key_code").getAsInt();
            enableCommand = configFile.get("command").getAsJsonObject().get("enabled").getAsBoolean();
            
            categoryScreenshot = configFile.get("screenshot").getAsJsonObject().get("category").getAsString();
            keyCodeScreenshot = configFile.get("screenshot").getAsJsonObject().get("key_code").getAsInt();
            enableScreenshot = configFile.get("screenshot").getAsJsonObject().get("enabled").getAsBoolean();
            
            categoryTogglePerspective = configFile.get("toggle_perspective").getAsJsonObject().get("category").getAsString();
            keyCodeTogglePerspective = configFile.get("toggle_perspective").getAsJsonObject().get("key_code").getAsInt();
            enableTogglePerspective = configFile.get("toggle_perspective").getAsJsonObject().get("enabled").getAsBoolean();
            
            categorySmoothCamera = configFile.get("smooth_camera").getAsJsonObject().get("category").getAsString();
            keyCodeSmoothCamera = configFile.get("smooth_camera").getAsJsonObject().get("key_code").getAsInt();
            enableSmoothCamera = configFile.get("smooth_camera").getAsJsonObject().get("enabled").getAsBoolean();
            
            categoryFullscreen = configFile.get("fullscreen").getAsJsonObject().get("category").getAsString();
            keyCodeFullscreen = configFile.get("fullscreen").getAsJsonObject().get("key_code").getAsInt();
            enableFullscreen = configFile.get("fullscreen").getAsJsonObject().get("enabled").getAsBoolean();
            
            categorySpectatorOutlines = configFile.get("spectator_outlines").getAsJsonObject().get("category").getAsString();
            keyCodeSpectatorOutlines = configFile.get("spectator_outlines").getAsJsonObject().get("key_code").getAsInt();
            enableSpectatorOutlines = configFile.get("spectator_outlines").getAsJsonObject().get("enabled").getAsBoolean();
            
            categoryHotbar1 = configFile.get("hotbar_1").getAsJsonObject().get("category").getAsString();
            keyCodeHotbar1 = configFile.get("hotbar_1").getAsJsonObject().get("key_code").getAsInt();
            enableHotbar1 = configFile.get("hotbar_1").getAsJsonObject().get("enabled").getAsBoolean();
            
            categoryHotbar2 = configFile.get("hotbar_2").getAsJsonObject().get("category").getAsString();
            keyCodeHotbar2 = configFile.get("hotbar_2").getAsJsonObject().get("key_code").getAsInt();
            enableHotbar2 = configFile.get("hotbar_2").getAsJsonObject().get("enabled").getAsBoolean();
            
            categoryHotbar3 = configFile.get("hotbar_3").getAsJsonObject().get("category").getAsString();
            keyCodeHotbar3 = configFile.get("hotbar_3").getAsJsonObject().get("key_code").getAsInt();
            enableHotbar3 = configFile.get("hotbar_3").getAsJsonObject().get("enabled").getAsBoolean();
            
            categoryHotbar4 = configFile.get("hotbar_4").getAsJsonObject().get("category").getAsString();
            keyCodeHotbar4 = configFile.get("hotbar_4").getAsJsonObject().get("key_code").getAsInt();
            enableHotbar4 = configFile.get("hotbar_4").getAsJsonObject().get("enabled").getAsBoolean();
            
            categoryHotbar5 = configFile.get("hotbar_5").getAsJsonObject().get("category").getAsString();
            keyCodeHotbar5 = configFile.get("hotbar_5").getAsJsonObject().get("key_code").getAsInt();
            enableHotbar5 = configFile.get("hotbar_5").getAsJsonObject().get("enabled").getAsBoolean();
            
            categoryHotbar6 = configFile.get("hotbar_6").getAsJsonObject().get("category").getAsString();
            keyCodeHotbar6 = configFile.get("hotbar_6").getAsJsonObject().get("key_code").getAsInt();
            enableHotbar6 = configFile.get("hotbar_6").getAsJsonObject().get("enabled").getAsBoolean();
            
            categoryHotbar7 = configFile.get("hotbar_7").getAsJsonObject().get("category").getAsString();
            keyCodeHotbar7 = configFile.get("hotbar_7").getAsJsonObject().get("key_code").getAsInt();
            enableHotbar7 = configFile.get("hotbar_7").getAsJsonObject().get("enabled").getAsBoolean();
            
            categoryHotbar8 = configFile.get("hotbar_8").getAsJsonObject().get("category").getAsString();
            keyCodeHotbar8 = configFile.get("hotbar_8").getAsJsonObject().get("key_code").getAsInt();
            enableHotbar8 = configFile.get("hotbar_8").getAsJsonObject().get("enabled").getAsBoolean();
            
            categoryHotbar9 = configFile.get("hotbar_9").getAsJsonObject().get("category").getAsString();
            keyCodeHotbar9 = configFile.get("hotbar_9").getAsJsonObject().get("key_code").getAsInt();
            enableHotbar9 = configFile.get("hotbar_9").getAsJsonObject().get("enabled").getAsBoolean();
            
            categoryQuit = configFile.get("quit").getAsJsonObject().get("category").getAsString();
            keyCodeQuit = configFile.get("quit").getAsJsonObject().get("key_code").getAsInt();
            enableQuit = configFile.get("quit").getAsJsonObject().get("enabled").getAsBoolean();
            
            categoryHideGUI = configFile.get("hide_gui").getAsJsonObject().get("category").getAsString();
            keyCodeHideGUI = configFile.get("hide_gui").getAsJsonObject().get("key_code").getAsInt();
            enableHideGUI = configFile.get("hide_gui").getAsJsonObject().get("enabled").getAsBoolean();
            
            categoryDebugMenu = configFile.get("debug_menu").getAsJsonObject().get("category").getAsString();
            keyCodeDebugMenu = configFile.get("debug_menu").getAsJsonObject().get("key_code").getAsInt();
            enableDebugMenu = configFile.get("debug_menu").getAsJsonObject().get("enabled").getAsBoolean();
            
            categorySwitchShader = configFile.get("switch_shader").getAsJsonObject().get("category").getAsString();
            keyCodeSwitchShader = configFile.get("switch_shader").getAsJsonObject().get("key_code").getAsInt();
            enableSwitchShader = configFile.get("switch_shader").getAsJsonObject().get("enabled").getAsBoolean();
                        
            inputStream.close();
        }
        
        catch (IOException exception) {
        	
        	exception.printStackTrace();
        }
    }
}
