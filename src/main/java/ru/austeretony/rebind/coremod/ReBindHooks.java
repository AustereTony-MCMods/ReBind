package ru.austeretony.rebind.coremod;

import ru.austeretony.rebind.main.ConfigurationRegistry;
import ru.austeretony.rebind.main.KeyRegistry;

public class ReBindHooks {
	
	public static int getQuitKeyCode() {
		
		return ConfigurationRegistry.rebindQuit ? KeyRegistry.getKeyBinding(KeyRegistry.QUIT).getKeyCode() : ConfigurationRegistry.defaultQuitKey;
	}

	public static int getHideHUDKeyCode() {
		
		return ConfigurationRegistry.rebindHideGUI ? KeyRegistry.getKeyBinding(KeyRegistry.HIDE_HUD).getKeyCode() : ConfigurationRegistry.defaultHideGUIKey;
	}
	
	public static int getDebugMenuKeyCode() {
		
		return ConfigurationRegistry.rebindDebugMenu ? KeyRegistry.getKeyBinding(KeyRegistry.DEBUG_MENU).getKeyCode() : ConfigurationRegistry.defaultDebugMenuKey;
	}
	
	public static int getDisableShaderKeyCode() {
		
		return ConfigurationRegistry.rebingDisableShader ? KeyRegistry.getKeyBinding(KeyRegistry.DISABLE_SHADER).getKeyCode() : ConfigurationRegistry.defaultDisableShaderKey;
	}
}
