package ru.austeretony.rebind.main;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.client.settings.KeyBinding;

@Mod(modid = ReBindMain.MODID, name = ReBindMain.NAME, version = ReBindMain.VERSION)
public class ReBindMain {
	
    public static final String 
	MODID = "rebind",
    NAME = "ReBind",
    VERSION = "2.4.0",
    COREMOD_VERSION = "1.3.0";
    
    public static final ConfigLoader CONFIG_LOADER = new ConfigLoader();
    
    public KeyBinding keyBindQuit, keyBindHideHUD, keyBindDebugScreen, keyBindDisableShader;
    
    @Instance(MODID)
    public static ReBindMain instance;
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	
    	this.createKeyBindingsForInternalKeys();   		
    }
    
    private void createKeyBindingsForInternalKeys() {

		ClientRegistry.registerKeyBinding(this.keyBindQuit = new KeyBinding("key.quit", 0, ""));
		ClientRegistry.registerKeyBinding(this.keyBindHideHUD = new KeyBinding("key.hideHUD", 0, ""));
		ClientRegistry.registerKeyBinding(this.keyBindDebugScreen = new KeyBinding("key.debugScreen", 0, ""));
		ClientRegistry.registerKeyBinding(this.keyBindDisableShader = new KeyBinding("key.disableShader", 0, ""));
	}
}