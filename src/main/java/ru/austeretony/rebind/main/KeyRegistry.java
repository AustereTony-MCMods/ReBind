package ru.austeretony.rebind.main;

import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;

@SideOnly(Side.CLIENT)
public class KeyRegistry {

	public static final Map<Integer, KeyBinding> INTERNAL_CONTROLS = new HashMap<Integer, KeyBinding>();
	
	public static final int 
	QUIT = 0,
	HIDE_HUD = 1,
	DEBUG_MENU = 2,
	DISABLE_SHADER = 3;
	
	public static void registerInternalKeys() {
		
		if (ReBindMain.CONFIG_LOADER.enableQuit)
		registerKeyBinding("key.exit", QUIT, ReBindMain.CONFIG_LOADER.keyCodeQuit, ReBindMain.CATEGORY_MISC);
		
		if (ReBindMain.CONFIG_LOADER.enableHideGUI)
		registerKeyBinding("key.hideHUD", HIDE_HUD, ReBindMain.CONFIG_LOADER.keyCodeHideGUI, ReBindMain.CATEGORY_MISC);
		
		if (ReBindMain.CONFIG_LOADER.enableDebugMenu)
		registerKeyBinding("key.debugMenu", DEBUG_MENU, ReBindMain.CONFIG_LOADER.keyCodeDebugMenu, ReBindMain.CATEGORY_MISC);
		
		if (ReBindMain.CONFIG_LOADER.enableDisableShader)
		registerKeyBinding("key.disableShader", DISABLE_SHADER, ReBindMain.CONFIG_LOADER.keyCodeDisableShader, ReBindMain.CATEGORY_MISC);
	}

	private static void registerKeyBinding(String name, int index, int keyCode, String category) {
		
		KeyBinding newKeybinding = new KeyBinding(name, keyCode, category);
		
		INTERNAL_CONTROLS.put(index, newKeybinding);
		
		ClientRegistry.registerKeyBinding(newKeybinding);		
	}
	
	public static KeyBinding getKeyBinding(int index) {
		
		return INTERNAL_CONTROLS.get(index);	
	}
}
