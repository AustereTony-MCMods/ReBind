package ru.austeretony.rebind.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.austeretony.rebind.command.CommandReBind;
import ru.austeretony.rebind.config.ConfigLoader;
import ru.austeretony.rebind.event.ReBindEvents;

@Mod(modid = ReBindMain.MODID, name = ReBindMain.NAME, version = ReBindMain.VERSION)
public class ReBindMain {
	
    public static final String 
	MODID = "rebind",
    NAME = "ReBind",
    VERSION = "2.7.1",
    GAME_VERSION = "1.8.9",
    VERSIONS_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/ReBind/info/versions.json",
    PROJECT_URL = "https://minecraft.curseforge.com/projects/rebind";
    
	public static final Logger LOGGER = LogManager.getLogger("ReBind");
	
    @SideOnly(Side.CLIENT)
    public static KeyBinding keyBindingQuit, keyBindingHideHUD, keyBindingDebugScreen, keyBindingDisableShader;
            
    @SideOnly(Side.CLIENT)
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	
        ClientRegistry.registerKeyBinding(keyBindingQuit = new KeyBinding("key.quit", 0, ""));
        ClientRegistry.registerKeyBinding(keyBindingHideHUD = new KeyBinding("key.hideHUD", 0, ""));
        ClientRegistry.registerKeyBinding(keyBindingDebugScreen = new KeyBinding("key.debugScreen", 0, ""));
        ClientRegistry.registerKeyBinding(keyBindingDisableShader = new KeyBinding("key.disableShader", 0, "")); 
        	
        if (ConfigLoader.isDebugModeEnabled())
        	ClientCommandHandler.instance.registerCommand(new CommandReBind());
        	
    	if (ConfigLoader.isUpdateCheckerEnabled()) {
    		
    		UpdateChecker updateChecker = new UpdateChecker();
    		
    		MinecraftForge.EVENT_BUS.register(updateChecker);    		
    		new Thread(updateChecker, "ReBind Update Check").start();
    		
    		LOGGER.error("Update check started...");
    	}
        
        if (ConfigLoader.isAutoJumpEnabled())
        	MinecraftForge.EVENT_BUS.register(new ReBindEvents());  
    }
}