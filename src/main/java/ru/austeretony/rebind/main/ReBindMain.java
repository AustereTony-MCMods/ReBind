package ru.austeretony.rebind.main;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import ru.austeretony.rebind.command.CommandRebind;
import ru.austeretony.rebind.event.ReBindEvents;

@Mod(modid = ReBindMain.MODID, name = ReBindMain.NAME, version = ReBindMain.VERSION)
public class ReBindMain {
	
    public static final String 
	MODID = "rebind",
    NAME = "ReBind",
    VERSION = "2.5.6",
    COREMOD_VERSION = "1.5.0",
    VERSIONS_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/ReBind/info/versions.json";
    
    public static final ConfigLoader CONFIG_LOADER = new ConfigLoader();
        
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	    		    	    	
    	Registry.register();    	
    }
    
    public static class Registry {
    	
        public static final KeyBinding 
        KEY_QUIT = new KeyBinding("key.quit", 0, ""),
        KEY_HIDE_HUD = new KeyBinding("key.hideHUD", 0, ""),
        KEY_DEBUG_SCREEN = new KeyBinding("key.debugScreen", 0, ""),
        KEY_DISABLE_SHADER = new KeyBinding("key.disableShader", 0, "");
    	
        public static void register() {
        	
        	ClientRegistry.registerKeyBinding(KEY_QUIT);
        	ClientRegistry.registerKeyBinding(KEY_HIDE_HUD);
        	ClientRegistry.registerKeyBinding(KEY_DEBUG_SCREEN);
        	ClientRegistry.registerKeyBinding(KEY_DISABLE_SHADER);
    		
        	if (CONFIG_LOADER.isDebugModeEnabled())
        	ClientCommandHandler.instance.registerCommand(new CommandRebind());
        	
        	if (CONFIG_LOADER.isUpdateCheckerEnabled() || CONFIG_LOADER.isAutoJumpEnabled())
        	MinecraftForge.EVENT_BUS.register(new ReBindEvents());
        }
    }
}