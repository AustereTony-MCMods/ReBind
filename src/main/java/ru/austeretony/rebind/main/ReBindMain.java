package ru.austeretony.rebind.main;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import ru.austeretony.rebind.command.CommandRebind;
import ru.austeretony.rebind.event.ReBindEvents;

@Mod(modid = ReBindMain.MODID, name = ReBindMain.NAME, version = ReBindMain.VERSION)
public class ReBindMain {
	
    public static final String 
	MODID = "rebind",
    NAME = "ReBind",
    VERSION = "2.6.0",
    GAME_VERSION = "1.12.2",
    VERSIONS_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/ReBind/info/versions.json",
    PROJECT_URL = "https://minecraft.curseforge.com/projects/rebind";
    
    public static final ConfigLoader CONFIG_LOADER = new ConfigLoader();
        
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	    		    
    	if (event.getSide() == Side.CLIENT)
    		Registry.register();    	
    }
    
    public static class Registry {
    	
        public static final KeyBinding 
        KEY_QUIT = new KeyBinding("key.quit", 0, ""),
        KEY_HIDE_HUD = new KeyBinding("key.hideHUD", 0, ""),
        KEY_DEBUG_SCREEN = new KeyBinding("key.debugScreen", 0, ""),
        KEY_SWITCH_SHADER = new KeyBinding("key.switchShader", 0, ""),
        KEY_NARRATOR = new KeyBinding("key.narrator", 0, "");
    	
        public static void register() {
        	
        	ClientRegistry.registerKeyBinding(KEY_QUIT);
        	ClientRegistry.registerKeyBinding(KEY_HIDE_HUD);
        	ClientRegistry.registerKeyBinding(KEY_DEBUG_SCREEN);
        	ClientRegistry.registerKeyBinding(KEY_SWITCH_SHADER);
        	ClientRegistry.registerKeyBinding(KEY_NARRATOR);
        	
        	KEY_HIDE_HUD.setKeyConflictContext(KeyConflictContext.IN_GAME);
        	KEY_DEBUG_SCREEN.setKeyConflictContext(KeyConflictContext.IN_GAME);
        	KEY_SWITCH_SHADER.setKeyConflictContext(KeyConflictContext.IN_GAME);    
        	KEY_NARRATOR.setKeyConflictContext(KeyConflictContext.IN_GAME);    
        	
        	if (CONFIG_LOADER.isDebugModeEnabled())
        		ClientCommandHandler.instance.registerCommand(new CommandRebind());
        	
        	if (CONFIG_LOADER.isUpdateCheckerEnabled() || CONFIG_LOADER.isAutoJumpEnabled())
        		MinecraftForge.EVENT_BUS.register(new ReBindEvents());
        }
    }
}
