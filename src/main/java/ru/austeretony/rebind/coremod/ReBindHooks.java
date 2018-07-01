package ru.austeretony.rebind.coremod;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.Loader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import ru.austeretony.rebind.main.ConfigLoader;
import ru.austeretony.rebind.main.KeyBindingProperty;
import ru.austeretony.rebind.main.ReBindMain;

public class ReBindHooks {
	
	private static boolean knownKeyBinding;
			
	private static String currentModid, currentModName, bindingConfigKey;
	
	private static KeyBindingProperty currentProperty;
		
	public static void removeHiddenKeyBindings() {
		
		if (Minecraft.getMinecraft().gameSettings != null) {
			
			List<KeyBinding> bindingsList = new ArrayList<KeyBinding>(Arrays.asList(Minecraft.getMinecraft().gameSettings.keyBindings));
			
	    	Set<String> occurrences = new HashSet<String>();
			
			Iterator<KeyBinding> iterator = bindingsList.iterator();
			
			KeyBinding curBinding;
			
			String bindingKey;
			
			while (iterator.hasNext()) {
				
				curBinding = iterator.next();
				
				bindingKey = ConfigLoader.KEYS_BY_KEYBINDINGS.get(curBinding);
								
				if (ConfigLoader.HIDDEN_KEYBINDINGS.contains(bindingKey)) {				
						
					ConfigLoader.KEYBINDINGS_BY_KEYS.get(bindingKey).setKeyCode(ConfigLoader.PROPERTIES.get(bindingKey).getKeyCode());
					
					iterator.remove();				
				}
				
				else {
					
					occurrences.add(curBinding.getKeyCategory());
				}
			}
	
			KeyBinding.getKeybinds().retainAll(occurrences);
						
			Minecraft.getMinecraft().gameSettings.keyBindings = bindingsList.toArray(new KeyBinding[bindingsList.size()]);
		}
	}
	
	public static void rewriteControlsSettings() {
		
		if (ConfigLoader.isControllsSettingsRewritingAllowed()) {

	    	try {
	    		
	    		String optionsPath = Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + "/options.txt";
	    		
				InputStream inputStream = new FileInputStream(new File(optionsPath));
				
				List<String> optionsLines = IOUtils.readLines(new InputStreamReader(inputStream, "UTF-8"));
				
				inputStream.close();
				
				Set<String> options = new HashSet<String>();
							
				Splitter splitter = Splitter.on(':');
				
				Iterator<String> splitIterator;
				
				for (String option : optionsLines) {
					
					splitIterator = splitter.split(option).iterator();
					
					options.add(splitIterator.next());
					
					splitIterator.next();
				}
								
				if (!options.contains("key_key.quit") &&
						!options.contains("key_key.hideHUD") &&
						!options.contains("key_key.debugScreen") &&
						!options.contains("key_key.disableShader")) {
									
					Iterator<String> iterator = optionsLines.iterator();
					
					String curLine;
					
					while (iterator.hasNext()) {
						
						curLine = iterator.next();
						
						if (curLine.length() > 4 && curLine.substring(0, 4).equals("key_")) {
							
							iterator.remove();
						}
					}
					
		            PrintStream fileStream = new PrintStream(new File(optionsPath));
					
		            for (String line : optionsLines) {
		            	
		            	fileStream.println(line);
		            }
		            
		            fileStream.close();
				}
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
		
		if (ConfigLoader.SORTED_KEYBINDINGS.isEmpty()) {
									
			Multimap<String, KeyBinding> bindingsByCategory = HashMultimap.<String, KeyBinding>create();
			
			List<KeyBinding> bindingsList = new ArrayList<KeyBinding>(Arrays.asList(bindingsArray));
			
			for (KeyBinding key : bindingsList) {
				
				if (isVanillaCategory(key.getKeyCategory()))
				bindingsByCategory.put("mc." + key.getKeyCategory().substring(15), key);
			}
			
			int propIndex;
			
			KeyBinding curKnownBinding, curUnknownBinding;
			
			List<KeyBindingProperty> sortedProps = ConfigLoader.SORTED_PROPERTIES;
			
			Iterator<KeyBinding> iterator;
			
			for (KeyBindingProperty property : sortedProps) {
									
				if (ConfigLoader.KEYBINDINGS_BY_KEYS.containsKey(property.getConfigKey())) {				 

					propIndex = sortedProps.indexOf(property);
															
					curKnownBinding = ConfigLoader.KEYBINDINGS_BY_KEYS.get(property.getConfigKey());
								
					ConfigLoader.SORTED_KEYBINDINGS.add(curKnownBinding);	
					
					bindingsList.remove(curKnownBinding);
					
					bindingsByCategory.remove(property.getCategory(), curKnownBinding);
					
					if ((propIndex + 1 < sortedProps.size() && !sortedProps.get(propIndex + 1).getCategory().equals(property.getCategory())) || propIndex + 1 == sortedProps.size()) {
						
						while (bindingsByCategory.containsKey(property.getCategory())) {
							
							iterator = bindingsByCategory.get(property.getCategory()).iterator();
							
							while (iterator.hasNext()) {
								
								curUnknownBinding = iterator.next();
																
								ConfigLoader.SORTED_KEYBINDINGS.add(curUnknownBinding);
								
								bindingsList.remove(curUnknownBinding);
											
								ConfigLoader.UNKNOWN_MODIDS.add(ConfigLoader.MODIDS_BY_KEYBINDINGS.get(curUnknownBinding));
								
								iterator.remove();
							}
						}
					}
				}
			}
			
			for (KeyBinding key : bindingsList) {
										
				if (!key.getKeyDescription().equals("key.pickItem"))//TODO Optifine bug. Vanilla key duplicate occurrence. Need normal fix.
				ConfigLoader.UNKNOWN_MODIDS.add(ConfigLoader.MODIDS_BY_KEYBINDINGS.get(key));
			}						
			
			ConfigLoader.SORTED_KEYBINDINGS.addAll(bindingsList);
		}
		
		return ConfigLoader.SORTED_KEYBINDINGS.toArray(new KeyBinding[ConfigLoader.SORTED_KEYBINDINGS.size()]);		
	}
	
	private static boolean isVanillaCategory(String category) {
				
		return category.equals("key.categories.gameplay") || 
				category.equals("key.categories.movement") || 
				category.equals("key.categories.inventory") ||
				category.equals("key.categories.misc") ||
				category.equals("key.categories.multiplayer") ||
				category.equals("key.categories.stream");
	}

	public static int getQuitKeyCode() {
		
		return ReBindMain.Registry.KEY_QUIT.getKeyCode();
	}

	public static int getHideHUDKeyCode() {
		
		return ReBindMain.Registry.KEY_HIDE_HUD.getKeyCode();
	}
	
	public static int getDebugMenuKeyCode() {
		
		return ReBindMain.Registry.KEY_DEBUG_SCREEN.getKeyCode();
	}
	
	public static int getDisableShaderKeyCode() {
		
		return ReBindMain.Registry.KEY_DISABLE_SHADER.getKeyCode();
	}
	
	public static String getKeyBindingName(String keyName) {
						
		if (Loader.instance().activeModContainer() != null) {
									
			currentModid = Loader.instance().activeModContainer().getModId();
			
			currentModName = Loader.instance().activeModContainer().getName();
		}
		
		else {
			
			if (ConfigLoader.KEYBINDINGS_BY_KEYS.size() < 32) {
				
				currentModid = "mc";
				
				currentModName = "Minecraft";
			}
			
			else {
								
				currentModid = "optifine";
				
				currentModName = "Optifine";
			}
		}
		
		currentModid = currentModid.toLowerCase();
		
		bindingConfigKey = currentModid + "_" + keyName.toLowerCase();
		
		bindingConfigKey = bindingConfigKey.replace(' ', '_').replace('.', '_').replaceAll("_key", "").replaceAll("_" + currentModid, "");
				
		knownKeyBinding = ConfigLoader.PROPERTIES.containsKey(bindingConfigKey);
		
		//TODO Debug		
		ReBindClassTransformer.LOGGER.info("Keybinding id: " + bindingConfigKey + ", known: " + knownKeyBinding);
		
		if (knownKeyBinding) {
			
			currentProperty = ConfigLoader.PROPERTIES.get(bindingConfigKey);
			
			if (!currentProperty.getName().isEmpty()) {
				
				keyName = currentProperty.getName();
			}
		}
		
		return keyName;
	}
	
	public static int getKeyBindingKeyCode(int keyCode) {
				
		if (knownKeyBinding)
		keyCode = currentProperty.getKeyCode();
		
		return keyCode;
	}

	public static String getKeyBindingCategory(String category) {
				
		if (knownKeyBinding) {
			
			String cat = currentProperty.getCategory();
			
			if (cat.equals("mc.gameplay") ||
					cat.equals("mc.movement") ||
					cat.equals("mc.inventory") ||
					cat.equals("mc.misc") ||
					cat.equals("mc.multiplayer") ||
					cat.equals("mc.stream")) {
				
				category = "key.categories." + cat.substring(3);
			}
			
			else {
				
				category = currentProperty.getCategory();
			}
		}
		
		return category;
	}
	
	public static void storeKeybinding(KeyBinding keyBinding) {
		
		ConfigLoader.MODNAMES_BY_MODIDS.put(currentModid, currentModName);
		
		ConfigLoader.KEYS_BY_KEYBINDINGS.put(keyBinding, bindingConfigKey);

		ConfigLoader.KEYBINDINGS_BY_KEYS.put(bindingConfigKey, keyBinding);
		
		ConfigLoader.MODIDS_BY_KEYBINDINGS.put(keyBinding, currentModid);
		
		ConfigLoader.KEYBINDINGS_BY_MODIDS.put(currentModid, keyBinding);
	}
}
