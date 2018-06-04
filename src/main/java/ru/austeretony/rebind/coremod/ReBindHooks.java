package ru.austeretony.rebind.coremod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.Loader;
import net.minecraft.client.settings.KeyBinding;
import ru.austeretony.rebind.main.EnumKeys;
import ru.austeretony.rebind.main.KeyBindingProperty;
import ru.austeretony.rebind.main.ReBindMain;

public class ReBindHooks {
	
	public static boolean wasLoadedBefore, optionsChecked;
	
	public static KeyBinding[] removeHiddenKeyBindings(KeyBinding[] keyBindings) {
		
		List<KeyBinding> bindingsList = new ArrayList<KeyBinding>(Arrays.asList(keyBindings));
		
    	Set<String> occurrences = new HashSet<String>();
		
		Iterator<KeyBinding> iterator = bindingsList.iterator();
		
		KeyBinding curBinding;
		
		while (iterator.hasNext()) {
			
			curBinding = iterator.next();
			
			if (ReBindMain.CONFIG_LOADER.HIDDEN_KEYS.contains(curBinding.getKeyDescription())) {
				
				iterator.remove();
			}
			
			else {
				
				occurrences.add(curBinding.getKeyCategory());
			}
		}

		KeyBinding.getKeybinds().retainAll(occurrences);
			
		return bindingsList.toArray(new KeyBinding[bindingsList.size()]);
	}
	
	public static boolean loadControlsFromOptionsFile(File options) {
		
		checkOptionsFile(options);
		
		if (wasLoadedBefore) {
			
			return true;
		}
		
		else if (ReBindMain.CONFIG_LOADER.shouldRewriteControlsSettings()) {
			
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
		
		if (ReBindMain.CONFIG_LOADER.SORTED_KEYS.isEmpty()) {
						
			Map<String, Integer> nativeOrder = new HashMap<String, Integer>();		
			
			Multimap<String, Integer> bindsByCategory = HashMultimap.<String, Integer>create();
			
			List<KeyBinding> bindingsList = new ArrayList<KeyBinding>(Arrays.asList(bindingsArray));
					
			for (int bindIndex = 0; bindIndex < bindingsArray.length; bindIndex++) {
											
				nativeOrder.put(bindingsArray[bindIndex].getKeyDescription(), bindIndex);
				
				if (isVanillaCategory(bindingsArray[bindIndex].getKeyCategory()))
				bindsByCategory.put("mc." + bindingsArray[bindIndex].getKeyCategory().substring(15), bindIndex);
			}
			
			int propIndex, knownBindNumber, unknownBindIndex;
			
			KeyBinding curKnownBinding, curUnknownBinding;
			
			List<KeyBindingProperty> sortedProps = ReBindMain.CONFIG_LOADER.SORTED_PROPERTIES;
			
			Iterator<Integer> iterator;
			
			for (KeyBindingProperty property : sortedProps) {
				
				if (isActualDomain(property.getDomain())) {				 
					
					propIndex = sortedProps.indexOf(property);
										
					knownBindNumber = nativeOrder.containsKey(property.getDefaultName()) ? nativeOrder.get(property.getDefaultName()) : nativeOrder.get("key." + property.getName());
					
					curKnownBinding = bindingsArray[knownBindNumber];
								
					ReBindMain.CONFIG_LOADER.SORTED_KEYS.add(curKnownBinding);	
					
					bindingsList.remove(curKnownBinding);
					
					bindsByCategory.remove(property.getCategory(), knownBindNumber);
					
					if ((propIndex + 1 < sortedProps.size() && !sortedProps.get(propIndex + 1).getCategory().equals(property.getCategory())) || propIndex + 1 == sortedProps.size()) {
						
						while (bindsByCategory.containsKey(property.getCategory())) {
							
							iterator = bindsByCategory.get(property.getCategory()).iterator();
							
							while (iterator.hasNext()) {
								
								unknownBindIndex = iterator.next();
								
								curUnknownBinding = bindingsArray[unknownBindIndex];
								
								ReBindMain.CONFIG_LOADER.SORTED_KEYS.add(curUnknownBinding);
								
								bindingsList.remove(curUnknownBinding);
								
								iterator.remove();
							}
						}
					}
				}
			}
			
			ReBindMain.CONFIG_LOADER.SORTED_KEYS.addAll(bindingsList);
		}
		
		return ReBindMain.CONFIG_LOADER.SORTED_KEYS.toArray(new KeyBinding[ReBindMain.CONFIG_LOADER.SORTED_KEYS.size()]);		
	}
	
	private static boolean isVanillaCategory(String string) {
				
		return string.equals("key.categories.gameplay") || 
				string.equals("key.categories.movement") || 
				string.equals("key.categories.inventory") ||
				string.equals("key.categories.misc") ||
				string.equals("key.categories.stream") ||
				string.equals("key.categories.multiplayer");
	}
	
	private static boolean isActualDomain(String domain) {
		
		if (domain.equals(EnumKeys.MINECRAFT.getDomain())) {
			
			return true;
		}
	
		for (EnumKeys modKey : EnumKeys.values()) {
					
			if (domain.equals(modKey.getDomain()) && Loader.isModLoaded(domain)) {
				
				return true;
			}
		}

		return false;
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
	
	public static int getKeyBindingKeyCode(String bindingName, int keyCode) {
				
		if (ReBindMain.CONFIG_LOADER.PROPERTIES.containsKey(bindingName))
		keyCode = ReBindMain.CONFIG_LOADER.PROPERTIES.get(bindingName).getKeyCode();
		
		return keyCode;
	}

	public static String getKeyBindingCategory(String bindingName, String category) {
				
		if (ReBindMain.CONFIG_LOADER.PROPERTIES.containsKey(bindingName)) {
			
			String cat = ReBindMain.CONFIG_LOADER.PROPERTIES.get(bindingName).getCategory();
			
			if (cat.equals("mc.gameplay") ||
					cat.equals("mc.movement") ||
					cat.equals("mc.inventory") ||
					cat.equals("mc.misc") ||
					cat.equals("mc.stream") ||
					cat.equals("mc.multiplayer")) {
				
				category = "key.categories." + cat.substring(3);
			}
			
			else {
				
				category = ReBindMain.CONFIG_LOADER.PROPERTIES.get(bindingName).getCategory();
			}
		}
		
		return category;
	}
	
	public static String getKeyBindingName(String bindingName) {	    
		
		if (ReBindMain.CONFIG_LOADER.PROPERTIES.containsKey(bindingName) && ReBindMain.CONFIG_LOADER.PROPERTIES.get(bindingName).getName().length() > 0)
		bindingName = "key." + ReBindMain.CONFIG_LOADER.PROPERTIES.get(bindingName).getName();
		
		return bindingName;
	}
}
