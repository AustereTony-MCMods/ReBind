package ru.austeretony.rebind.coremod;

import ru.austeretony.rebind.main.KeyRegistry;
import ru.austeretony.rebind.main.ReBindMain;

public class ReBindHooks {
	
	public static int getQuitKeyCode() {
		
		return ReBindMain.CONFIG_LOADER.enableQuit ? KeyRegistry.getKeyBinding(KeyRegistry.QUIT).getKeyCode() : ReBindMain.CONFIG_LOADER.keyCodeQuit;
	}

	public static int getHideHUDKeyCode() {
		
		return ReBindMain.CONFIG_LOADER.enableHideGUI ? KeyRegistry.getKeyBinding(KeyRegistry.HIDE_HUD).getKeyCode() : ReBindMain.CONFIG_LOADER.keyCodeHideGUI;
	}
	
	public static int getDebugMenuKeyCode() {
		
		return ReBindMain.CONFIG_LOADER.enableDebugMenu ? KeyRegistry.getKeyBinding(KeyRegistry.DEBUG_MENU).getKeyCode() : ReBindMain.CONFIG_LOADER.keyCodeDebugMenu;
	}
	
	public static int getSwitchShaderKeyCode() {
		
		return ReBindMain.CONFIG_LOADER.enableSwitchShader ? KeyRegistry.getKeyBinding(KeyRegistry.SWITCH_SHADER).getKeyCode() : ReBindMain.CONFIG_LOADER.keyCodeSwitchShader;
	}
}
