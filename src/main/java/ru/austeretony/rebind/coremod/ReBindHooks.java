package ru.austeretony.rebind.coremod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import ru.austeretony.rebind.main.KeyRegistry;
import ru.austeretony.rebind.main.ReBindMain;

public class ReBindHooks {
	
	public static boolean wasLoadedBefore, optionsChecked;
	
	public static boolean loadOptionsControls(File options) {
		
		checkOptionsFile(options);
		
		if (wasLoadedBefore) {
			
			return true;
		}
		
		else if (ReBindMain.CONFIG_LOADER.enableControlsRewriting) {
			
			return false;
		}
										
		return true;
	}

	private static void checkOptionsFile(File options) {
				
		if (!optionsChecked) {
						
			String line = "";
			
	        try {
	        	
				BufferedReader bufferedReader = new BufferedReader(new FileReader(options));
				
	            while ((line = bufferedReader.readLine()) != null) {
	            		            		            	
	            	if (line.split(":")[0].equals("key_key.quit")) {
	            		
	            		wasLoadedBefore = true;
	            	}
	            }
	            
	            optionsChecked = true;
	            
				bufferedReader.close();
			} 
	        
	        catch (FileNotFoundException exception) {
				
	        	exception.printStackTrace();       	
			} 
	        
	        catch (IOException exception) {
				
	        	exception.printStackTrace();
			}
		}
	}
	
	public static int getQuitKeyCode() {
		
		return ReBindMain.CONFIG_LOADER.enableQuit ? KeyRegistry.getKeyBinding(KeyRegistry.QUIT).getKeyCode() : ReBindMain.CONFIG_LOADER.keyCodeQuit;
	}

	public static int getHideHUDKeyCode() {
		
		return ReBindMain.CONFIG_LOADER.enableHideGUI ? KeyRegistry.getKeyBinding(KeyRegistry.HIDE_HUD).getKeyCode() : ReBindMain.CONFIG_LOADER.keyCodeHideGUI;
	}
	
	public static int getDebugMenuKeyCode() {
		
		return ReBindMain.CONFIG_LOADER.enableDebugMenu ? KeyRegistry.getKeyBinding(KeyRegistry.DEBUG_MENU).getKeyCode() : ReBindMain.CONFIG_LOADER.keyCodeDebugMenu;
	}
	
	public static int getDisableShaderKeyCode() {
		
		return ReBindMain.CONFIG_LOADER.enableDisableShader ? KeyRegistry.getKeyBinding(KeyRegistry.DISABLE_SHADER).getKeyCode() : ReBindMain.CONFIG_LOADER.keyCodeDisableShader;
	}
}
