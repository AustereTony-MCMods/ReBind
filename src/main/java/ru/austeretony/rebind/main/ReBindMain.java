package ru.austeretony.rebind.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import ru.austeretony.rebind.command.CommandReBind;

@Mod(modid = ReBindMain.MODID, name = ReBindMain.NAME, version = ReBindMain.VERSION)
public class ReBindMain {
	
    public static final String 
	MODID = "rebind",
    NAME = "ReBind",
    VERSION = "2.7.0",
    GAME_VERSION = "1.7.10",
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
        	
        if (ConfigLoader.isUpdateCheckerEnabled() || ConfigLoader.isAutoJumpEnabled())
        	MinecraftForge.EVENT_BUS.register(new UpdateChecker());   	
    }
}