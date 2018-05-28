package ru.austeretony.rebind.main;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeyRegistry {

	public static final Map<Integer, KeyBinding> INTERNAL_CONTROLS = new HashMap<Integer, KeyBinding>();
	
	public static final int 
	QUIT = 0,
	HIDE_HUD = 1,
	DEBUG_MENU = 2,
	SWITCH_SHADER = 3;
	
	public static void registerInternalVanillaKeys() {
		
		if (ReBindMain.CONFIG_LOADER.enableQuit)
		registerKeyBinding("key.quit", QUIT, ReBindMain.CONFIG_LOADER.keyCodeQuit, ReBindMain.getCat(ReBindMain.CONFIG_LOADER.categoryQuit));
		
		if (ReBindMain.CONFIG_LOADER.enableHideGUI)
		registerKeyBinding("key.hideHUD", HIDE_HUD, ReBindMain.CONFIG_LOADER.keyCodeHideGUI, ReBindMain.getCat(ReBindMain.CONFIG_LOADER.categoryHideGUI));
		
		if (ReBindMain.CONFIG_LOADER.enableDebugMenu)
		registerKeyBinding("key.debugMenu", DEBUG_MENU, ReBindMain.CONFIG_LOADER.keyCodeDebugMenu, ReBindMain.getCat(ReBindMain.CONFIG_LOADER.categoryDebugMenu));
		
		if (ReBindMain.CONFIG_LOADER.enableSwitchShader)
		registerKeyBinding("key.switchShader", SWITCH_SHADER, ReBindMain.CONFIG_LOADER.keyCodeSwitchShader, ReBindMain.getCat(ReBindMain.CONFIG_LOADER.categorySwitchShader));
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
