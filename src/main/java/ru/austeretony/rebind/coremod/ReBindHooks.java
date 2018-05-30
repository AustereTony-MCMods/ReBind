package ru.austeretony.rebind.coremod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.settings.KeyBinding;
import ru.austeretony.rebind.main.KeyBindingProperty;
import ru.austeretony.rebind.main.ReBindMain;

public class ReBindHooks {
	
	public static boolean wasLoadedBefore, optionsChecked;
	
	public static boolean loadControlsFromOptionsFile(File options) {
		
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
	
	public static KeyBinding[] sortKeyBindings(KeyBinding[] bindingsArray) {
		
		Map<String, Integer> nativeOrder = new HashMap<String, Integer>();		
		
		Multimap<String, Integer> bindsByCategory = HashMultimap.<String, Integer>create();
		
		List<KeyBinding> bindingsList = new ArrayList<KeyBinding>(Arrays.asList(bindingsArray));
		
		List<KeyBinding> orderedBindings = new ArrayList<KeyBinding>();
		
		for (int bindIndex = 0; bindIndex < bindingsArray.length; bindIndex++) {
			
			nativeOrder.put(bindingsArray[bindIndex].getKeyDescription().substring(4), bindIndex);
			
			if (isVanillaCategory(bindingsArray[bindIndex].getKeyCategory()))
			bindsByCategory.put(bindingsArray[bindIndex].getKeyCategory().substring(15), bindIndex);
		}
		
		int propIndex,knownBindNumber, unknownBindIndex;
		
		KeyBinding curKnownBinding, curUnknownBinding;
		
		List<KeyBindingProperty> props = ReBindMain.CONFIG_LOADER.orderedProperties;
		
		Iterator<Integer> iterator;
		
		for (KeyBindingProperty property : props) {
			
			propIndex = props.indexOf(property);
			
			knownBindNumber = nativeOrder.get(property.getName());
			
			curKnownBinding = bindingsArray[knownBindNumber];
						
			orderedBindings.add(curKnownBinding);	
			
			bindingsList.remove(curKnownBinding);
			
			bindsByCategory.remove(property.getCategory(), knownBindNumber);
			
			if ((propIndex + 1 < props.size() && !props.get(propIndex + 1).getCategory().equals(property.getCategory())) || propIndex + 1 == props.size()) {
				
				while (bindsByCategory.containsKey(property.getCategory())) {
					
					iterator = bindsByCategory.get(property.getCategory()).iterator();
					
					while (iterator.hasNext()) {
						
						unknownBindIndex = iterator.next();
						
						curUnknownBinding = bindingsArray[unknownBindIndex];
						
						orderedBindings.add(curUnknownBinding);
						
						bindingsList.remove(curUnknownBinding);
						
						iterator.remove();
					}
				}
			}
		}
		
		orderedBindings.addAll(bindingsList);
		
		return orderedBindings.toArray(new KeyBinding[orderedBindings.size()]);		
	}
	
	private static boolean isVanillaCategory(String string) {
				
		return string.equals("key.categories.gameplay") || 
				string.equals("key.categories.movement") || 
				string.equals("key.categories.inventory") ||
				string.equals("key.categories.misc") ||
				string.equals("key.categories.stream") ||
				string.equals("key.categories.multiplayer");
	}
	
	public static int getQuitKeyCode() {
		
		return ReBindMain.instance.keyBindQuit.getKeyCode();
	}

	public static int getHideHUDKeyCode() {
		
		return ReBindMain.instance.keyBindHideHUD.getKeyCode();
	}
	
	public static int getDebugMenuKeyCode() {
		
		return ReBindMain.instance.keyBindDebugScreen.getKeyCode();
	}
	
	public static int getDisableShaderKeyCode() {
		
		return ReBindMain.instance.keyBindDisableShader.getKeyCode();
	}
}
