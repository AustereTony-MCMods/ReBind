<<<<<<< HEAD
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
	
	public static int getSwitchShaderKeyCode() {
		
		return ConfigurationRegistry.rebingSwitchShader ? KeyRegistry.getKeyBinding(KeyRegistry.SWITCH_SHADER).getKeyCode() : ConfigurationRegistry.defaultSwitchShaderKey;
	}
}
=======
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
	
	public static int getSwitchShaderKeyCode() {
		
		return ConfigurationRegistry.rebingSwitchShader ? KeyRegistry.getKeyBinding(KeyRegistry.SWITCH_SHADER).getKeyCode() : ConfigurationRegistry.defaultSwitchShaderKey;
	}
}
>>>>>>> f6fa39ce2f08d82c35365a74a37763b417079fd3
