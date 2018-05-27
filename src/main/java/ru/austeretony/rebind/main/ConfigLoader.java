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
        	
            //Mouse
            
            keyCodeAttack = configFile.get("attack").getAsJsonObject().get("key_code").getAsInt();
            enableAttack = configFile.get("attack").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeUseItem = configFile.get("use_item").getAsJsonObject().get("key_code").getAsInt();
            enableUseItem = configFile.get("use_item").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodePickBlock = configFile.get("pick_block").getAsJsonObject().get("key_code").getAsInt();
            enablePickBlock = configFile.get("pick_block").getAsJsonObject().get("enabled").getAsBoolean();
                                  
            //Keyboard
            
            keyCodeForward = configFile.get("forward").getAsJsonObject().get("key_code").getAsInt();
            enableForward = configFile.get("forward").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeLeft = configFile.get("left").getAsJsonObject().get("key_code").getAsInt();
            enableLeft = configFile.get("left").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeBack = configFile.get("back").getAsJsonObject().get("key_code").getAsInt();
            enableBack = configFile.get("back").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeRight = configFile.get("right").getAsJsonObject().get("key_code").getAsInt();
            enableRight = configFile.get("right").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeJump = configFile.get("jump").getAsJsonObject().get("key_code").getAsInt();
            enableJump = configFile.get("jump").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeSneak = configFile.get("sneak").getAsJsonObject().get("key_code").getAsInt();
            enableSneak = configFile.get("sneak").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeSprint = configFile.get("sprint").getAsJsonObject().get("key_code").getAsInt();
            enableSprint = configFile.get("sprint").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeInventory = configFile.get("inventory").getAsJsonObject().get("key_code").getAsInt();
            enableInventory = configFile.get("inventory").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeSwapHands = configFile.get("swap_hands").getAsJsonObject().get("key_code").getAsInt();
            enableSwapHands = configFile.get("swap_hands").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeDrop = configFile.get("drop").getAsJsonObject().get("key_code").getAsInt();
            enableDrop = configFile.get("drop").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeChat = configFile.get("chat").getAsJsonObject().get("key_code").getAsInt();
            enableChat = configFile.get("chat").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodePlayerList = configFile.get("player_list").getAsJsonObject().get("key_code").getAsInt();
            enablePlayerList = configFile.get("player_list").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeCommand = configFile.get("command").getAsJsonObject().get("key_code").getAsInt();
            enableCommand = configFile.get("command").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeScreenshot = configFile.get("screenshot").getAsJsonObject().get("key_code").getAsInt();
            enableScreenshot = configFile.get("screenshot").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeTogglePerspective = configFile.get("toggle_perspective").getAsJsonObject().get("key_code").getAsInt();
            enableTogglePerspective = configFile.get("toggle_perspective").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeSmoothCamera = configFile.get("smooth_camera").getAsJsonObject().get("key_code").getAsInt();
            enableSmoothCamera = configFile.get("smooth_camera").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeFullscreen = configFile.get("fullscreen").getAsJsonObject().get("key_code").getAsInt();
            enableFullscreen = configFile.get("fullscreen").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeSpectatorOutlines = configFile.get("spectator_outlines").getAsJsonObject().get("key_code").getAsInt();
            enableSpectatorOutlines = configFile.get("spectator_outlines").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeHotbar1 = configFile.get("hotbar_1").getAsJsonObject().get("key_code").getAsInt();
            enableHotbar1 = configFile.get("hotbar_1").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeHotbar2 = configFile.get("hotbar_2").getAsJsonObject().get("key_code").getAsInt();
            enableHotbar2 = configFile.get("hotbar_2").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeHotbar3 = configFile.get("hotbar_3").getAsJsonObject().get("key_code").getAsInt();
            enableHotbar3 = configFile.get("hotbar_3").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeHotbar4 = configFile.get("hotbar_4").getAsJsonObject().get("key_code").getAsInt();
            enableHotbar4 = configFile.get("hotbar_4").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeHotbar5 = configFile.get("hotbar_5").getAsJsonObject().get("key_code").getAsInt();
            enableHotbar5 = configFile.get("hotbar_5").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeHotbar6 = configFile.get("hotbar_6").getAsJsonObject().get("key_code").getAsInt();
            enableHotbar6 = configFile.get("hotbar_6").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeHotbar7 = configFile.get("hotbar_7").getAsJsonObject().get("key_code").getAsInt();
            enableHotbar7 = configFile.get("hotbar_7").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeHotbar8 = configFile.get("hotbar_8").getAsJsonObject().get("key_code").getAsInt();
            enableHotbar8 = configFile.get("hotbar_8").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeHotbar9 = configFile.get("hotbar_9").getAsJsonObject().get("key_code").getAsInt();
            enableHotbar9 = configFile.get("hotbar_9").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeQuit = configFile.get("quit").getAsJsonObject().get("key_code").getAsInt();
            enableQuit = configFile.get("quit").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeHideGUI = configFile.get("hide_gui").getAsJsonObject().get("key_code").getAsInt();
            enableHideGUI = configFile.get("hide_gui").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeDebugMenu = configFile.get("debug_menu").getAsJsonObject().get("key_code").getAsInt();
            enableDebugMenu = configFile.get("debug_menu").getAsJsonObject().get("enabled").getAsBoolean();
            
            keyCodeSwitchShader = configFile.get("switch_shader").getAsJsonObject().get("key_code").getAsInt();
            enableSwitchShader = configFile.get("switch_shader").getAsJsonObject().get("enabled").getAsBoolean();
            
            inputStream.close();
        }
        
        catch (IOException exception) {
        	
        	exception.printStackTrace();
        }
    }
}
