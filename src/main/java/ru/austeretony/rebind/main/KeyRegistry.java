package ru.austeretony.rebind.main;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeyRegistry {

	public static final Map<Integer, KeyBinding> CONTROLS = new HashMap<Integer, KeyBinding>();
	
	public static final int 
	QUIT = 0,
	HIDE_HUD = 1,
	DEBUG_MENU = 2,
	SWITCH_SHADER = 3;
	
	public static void registerKeys() {
		
		if (ConfigurationRegistry.rebindQuit)
		registerKeyBinding("key.exit", QUIT, ConfigurationRegistry.defaultQuitKey, "key.categories.misc");
		
		if (ConfigurationRegistry.rebindHideGUI)
		registerKeyBinding("key.hideHUD", HIDE_HUD, ConfigurationRegistry.defaultHideGUIKey, "key.categories.misc");
		
		if (ConfigurationRegistry.rebindDebugMenu)
		registerKeyBinding("key.debugMenu", DEBUG_MENU, ConfigurationRegistry.defaultDebugMenuKey, "key.categories.misc");
		
		if (ConfigurationRegistry.rebingSwitchShader)
		registerKeyBinding("key.switchShader", SWITCH_SHADER, ConfigurationRegistry.defaultSwitchShaderKey, "key.categories.misc");
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
