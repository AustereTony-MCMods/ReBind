package ru.austeretony.rebind.main;

import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;

@SideOnly(Side.CLIENT)
public class KeyRegistry {

	public static final Map<Integer, KeyBinding> CONTROLS = new HashMap<Integer, KeyBinding>();
	
	public static final int 
	QUIT = 0,
	HIDE_HUD = 1,
	DEBUG_MENU = 2,
	DISABLE_SHADER = 3;
	
	public static void registerKeys() {
		
		if (ConfigurationRegistry.rebindQuit)
		registerKeyBinding("key.exit", QUIT, ConfigurationRegistry.defaultQuitKey, "key.categories.misc");
		
		if (ConfigurationRegistry.rebindHideGUI)
		registerKeyBinding("key.hideHUD", HIDE_HUD, ConfigurationRegistry.defaultHideGUIKey, "key.categories.misc");
		
		if (ConfigurationRegistry.rebindDebugMenu)
		registerKeyBinding("key.debugMenu", DEBUG_MENU, ConfigurationRegistry.defaultDebugMenuKey, "key.categories.misc");
		
		if (ConfigurationRegistry.rebingDisableShader)
		registerKeyBinding("key.disableShader", DISABLE_SHADER, ConfigurationRegistry.defaultDisableShaderKey, "key.categories.misc");
	}

	private static void registerKeyBinding(String name, int index, int keyCode, String category) {
		
		KeyBinding newKeybinding = new KeyBinding(name, keyCode, category);
		
		CONTROLS.put(index, newKeybinding);
		
		ClientRegistry.registerKeyBinding(newKeybinding);		
	}
	
	public static KeyBinding getKeyBinding(int index) {
		
		return CONTROLS.get(index);	
	}
}
