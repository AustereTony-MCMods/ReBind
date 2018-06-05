package ru.austeretony.rebind.main;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = ReBindMain.MODID, name = ReBindMain.NAME, version = ReBindMain.VERSION)
public class ReBindMain {
	
    public static final String 
	MODID = "rebind",
    NAME = "ReBind",
    VERSION = "2.4.1",
    COREMOD_VERSION = "1.3.0";
    
    public static final ConfigLoader CONFIG_LOADER = new ConfigLoader();
    
    public KeyBinding keyBindQuit, keyBindHideHUD, keyBindDebugScreen, keyBindSwitchShader;
    
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
		ClientRegistry.registerKeyBinding(this.keyBindSwitchShader = new KeyBinding("key.switchShader", 0, ""));
	}
}
