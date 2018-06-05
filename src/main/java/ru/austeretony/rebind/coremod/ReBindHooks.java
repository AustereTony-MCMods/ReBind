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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.Loader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import ru.austeretony.rebind.main.EnumKeys;
import ru.austeretony.rebind.main.KeyBindingProperty;
import ru.austeretony.rebind.main.ReBindMain;

public class ReBindHooks {
	
	public static boolean wasLoadedBefore, optionsChecked;
	
	private static String keyDefaultName;
	
	public static void removeHiddenKeyBindings() {
		
		if (Minecraft.getMinecraft().gameSettings != null) {
						
			List<KeyBinding> bindingsList = new ArrayList<KeyBinding>(Arrays.asList(Minecraft.getMinecraft().gameSettings.keyBindings));
			
	    	Set<String> occurrences = new HashSet<String>();
			
			Iterator<KeyBinding> iterator = bindingsList.iterator();
			
			KeyBinding curBinding;
			
			String keyName;
			
			while (iterator.hasNext()) {
				
				curBinding = iterator.next();
				
				keyName = curBinding.getKeyDescription();
				
				if (ReBindMain.CONFIG_LOADER.HIDDEN_KEYS.contains(keyName)) {				
						
					ReBindMain.CONFIG_LOADER.KEYBINDINGS.get(keyName).setKeyCode(ReBindMain.CONFIG_LOADER.PROPERTIES.get(keyName).getKeyCode());
					
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
		
		if (Minecraft.getMinecraft().gameSettings != null && ReBindMain.CONFIG_LOADER.shouldRewriteControlsSettings()) {

	    	try {
	    		
	    		String optionsPath = Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + "/options.txt";
	    		
				InputStream inputStream = new FileInputStream(new File(optionsPath));
				
				List<String> optionsLines = IOUtils.readLines(new InputStreamReader(inputStream, "UTF-8"));
				
				inputStream.close();
				
				Map<String, String> options = new LinkedHashMap<String, String>();
							
				Splitter splitter = Splitter.on(':');
				
				Iterator<String> splitIterator;
				
				for (String option : optionsLines) {
					
					splitIterator = splitter.split(option).iterator();
					
					options.put(splitIterator.next(), splitIterator.next());
				}
								
				if (!options.containsKey("key_key.quit") &&
						!options.containsKey("key_key.hideHUD") &&
						!options.containsKey("key_key.debugScreen") &&
						!options.containsKey("key_key.switchShader")) {
									
					Iterator<String> iterator = optionsLines.iterator();
					
					String curLine;
					
					while (iterator.hasNext()) {
						
						curLine = iterator.next();
						
						if (curLine.length() > 5 && curLine.substring(0, 4).equals("key_")) {
							
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
		
		if (domain.equals(EnumKeys.OPTIFINE.getDomain()) && FMLClientHandler.instance().hasOptifine()) {
			
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
	
	public static String getKeyBindingName(String name) {	    
		
		keyDefaultName = name;
		
		if (ReBindMain.CONFIG_LOADER.PROPERTIES.containsKey(name) && ReBindMain.CONFIG_LOADER.PROPERTIES.get(name).getName().length() > 0)
		name = "key." + ReBindMain.CONFIG_LOADER.PROPERTIES.get(name).getName();
		
		return name;
	}
	
	public static int getKeyBindingKeyCode(int keyCode) {
				
		if (ReBindMain.CONFIG_LOADER.PROPERTIES.containsKey(keyDefaultName))
		keyCode = ReBindMain.CONFIG_LOADER.PROPERTIES.get(keyDefaultName).getKeyCode();
		
		return keyCode;
	}

	public static String getKeyBindingCategory(String category) {
				
		if (ReBindMain.CONFIG_LOADER.PROPERTIES.containsKey(keyDefaultName)) {
			
			String cat = ReBindMain.CONFIG_LOADER.PROPERTIES.get(keyDefaultName).getCategory();
			
			if (cat.equals("mc.gameplay") ||
					cat.equals("mc.movement") ||
					cat.equals("mc.inventory") ||
					cat.equals("mc.misc") ||
					cat.equals("mc.creative") ||
					cat.equals("mc.stream") ||
					cat.equals("mc.multiplayer")) {
				
				category = "key.categories." + cat.substring(3);
			}
			
			else {
				
				category = ReBindMain.CONFIG_LOADER.PROPERTIES.get(keyDefaultName).getCategory();
			}
		}
		
		return category;
	}
	
	public static void getKeybinding(KeyBinding keyBinding) {	    

		ReBindMain.CONFIG_LOADER.KEYBINDINGS.put(keyDefaultName, keyBinding);
	}
}
