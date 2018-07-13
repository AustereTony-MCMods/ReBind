package ru.austeretony.rebind.coremod;

import java.io.File;
import java.io.FileInputStream;
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
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.common.Loader;
import ru.austeretony.rebind.main.ConfigLoader;
import ru.austeretony.rebind.main.KeyBindingProperty;
import ru.austeretony.rebind.main.ReBindMain;

public class ReBindHooks {
	
	private static boolean isKnownKeyBinding;
			
	private static String currentModid, currentModName, bindingConfigKey;
	
	private static KeyBindingProperty currentProperty;
		
	private static int keyCount;
		
	public static void removeHiddenKeyBindings() {
		
		if (Minecraft.getMinecraft().gameSettings != null) {
			
			List<KeyBinding> bindingsList = new ArrayList<KeyBinding>(Arrays.asList(Minecraft.getMinecraft().gameSettings.keyBindings));
			
	    	Set<String> occurrences = new HashSet<String>();
			
			Iterator<KeyBinding> iterator = bindingsList.iterator();
			
			KeyBinding keyBinding;
			
			KeyBindingProperty property;
						
			while (iterator.hasNext()) {
				
				keyBinding = iterator.next();
				
				property = KeyBindingProperty.get(keyBinding);
												
				if (!property.isEnabled()) {				
						
					keyBinding.setKeyCode(property.getKeyCode());
					
					iterator.remove();				
				}
				
				else {
					
					occurrences.add(keyBinding.getKeyCategory());
				}
			}
	
			KeyBinding.getKeybinds().retainAll(occurrences);
						
			Minecraft.getMinecraft().gameSettings.keyBindings = bindingsList.toArray(new KeyBinding[bindingsList.size()]);
		}
	}
	
	public static void removeControlsSettings() {
		
		if (ConfigLoader.isControllsSettingsRewritingEnabled()) {

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
						!options.contains("key_key.switchShader")) {
									
					Iterator<String> iterator = optionsLines.iterator();
					
					String curLine;
					
					while (iterator.hasNext()) {
						
						curLine = iterator.next();
						
						if (curLine.length() > 4 && curLine.substring(0, 4).equals("key_"))							
							iterator.remove();
					}
					
		            PrintStream fileStream = new PrintStream(new File(optionsPath));
					
		            for (String line : optionsLines)	            	
		            	fileStream.println(line);
		            
		            fileStream.close();
				}
			} 
	    	
	    	catch (IOException exception) {
	    		
	    		exception.printStackTrace();
			}
		}
	}

	public static KeyBinding[] sortKeyBindings() {
		
		if (KeyBindingProperty.SORTED_KEYBINDINGS.isEmpty()) {
															
			for (KeyBindingProperty property : KeyBindingProperty.PROPERTIES_BY_KEYS.values()) {
						
				if (property.isKnown()) {
					 
					if (property.isEnabled()) 
						KeyBindingProperty.SORTED_KEYBINDINGS.add(property.getKeyBinding());
				}
				
				else {
					
					break;
				}
			}
			
			Multimap<String, KeyBindingProperty> propsByModnames = LinkedHashMultimap.<String, KeyBindingProperty>create();
			
			Set<String> sortedModNames = new TreeSet<String>();
			
			for (KeyBindingProperty property : KeyBindingProperty.UNKNOWN) {

				propsByModnames.put(property.getModName(), property);
				
				sortedModNames.add(property.getModName());
			}
			
			for (String modName : sortedModNames) {
				
				for (KeyBindingProperty property : propsByModnames.get(modName))					
					KeyBindingProperty.SORTED_KEYBINDINGS.add(property.getKeyBinding());
			}
		}
		
		return KeyBindingProperty.SORTED_KEYBINDINGS.toArray(new KeyBinding[KeyBindingProperty.SORTED_KEYBINDINGS.size()]);		
	}
	
	public static int getQuitKeyCode() {
		
		return ReBindMain.Registry.KEY_QUIT.getKeyCode();
	}

	public static boolean isQuitKeyPressed(int key) {
				
		return ReBindMain.Registry.KEY_QUIT.isActiveAndMatches(key);
	}

	public static boolean isHideHUDKeyPressed(int key) {
		
		return ReBindMain.Registry.KEY_HIDE_HUD.isActiveAndMatches(key);
	}
	
	public static int getDebugScreenKeyCode() {
				
		return ReBindMain.Registry.KEY_DEBUG_SCREEN.isPressed() ? ReBindMain.Registry.KEY_DEBUG_SCREEN.getKeyCode() : 0;
	}
	
	public static int getSwitchShaderKeyCode() {
		
		return ReBindMain.Registry.KEY_SWITCH_SHADER.isPressed() ? ReBindMain.Registry.KEY_SWITCH_SHADER.getKeyCode() : 0;
	}
	
	public static boolean isNarratorKeyPressed(int key) {
		
		return ReBindMain.Registry.KEY_NARRATOR.isActiveAndMatches(key);
	}
	
	public static String getKeyBindingName(String keyName) {
						
		if (Loader.instance().activeModContainer() != null) {
									
			currentModid = Loader.instance().activeModContainer().getModId();			
			currentModName = Loader.instance().activeModContainer().getName();
		}
		
		else {
			
			if (keyCount < 33) {
				
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
		bindingConfigKey = bindingConfigKey.replaceAll("[. ]", "_").replace("_key", "").replace("_" + currentModid, "");
				
		isKnownKeyBinding = KeyBindingProperty.PROPERTIES_BY_KEYS.containsKey(bindingConfigKey);
		
		//TODO Debug		
		ReBindClassTransformer.LOGGER.info("Keybinding id: " + bindingConfigKey + ", known: " + isKnownKeyBinding);
		
		if (isKnownKeyBinding) {
			
			currentProperty = KeyBindingProperty.get(bindingConfigKey);
			
			if (!currentProperty.getName().isEmpty())				
				keyName = currentProperty.getName();		
		}
		
		return keyName;
	}
	
	public static KeyModifier getKeyBindingKeyModifier(KeyModifier keyModifier) {
		
		if (isKnownKeyBinding)
			keyModifier = KeyModifier.valueFromString(currentProperty.getKeyModifier());
		
		return keyModifier;
	}
	
	public static int getKeyBindingKeyCode(int keyCode) {
						
		if (isKnownKeyBinding)			
			keyCode = currentProperty.getKeyCode();    
		
		return keyCode;
	}

	public static String getKeyBindingCategory(String category) {
				
		if (isKnownKeyBinding) {
			
			String cat = currentProperty.getCategory();
			
			if (cat.equals("mc.gameplay") ||
					cat.equals("mc.movement") ||
					cat.equals("mc.inventory") ||
					cat.equals("mc.misc") ||
					cat.equals("mc.creative") ||
					cat.equals("mc.multiplayer"))				
				category = "key.categories." + cat.substring(3);
			else
				category = currentProperty.getCategory();
		}
		
		else {
			
			category = currentModName;
		}
		
		return category;
	}
	
	public static void storeKeybinding(KeyBinding keyBinding) {
		
		keyCount++;
		
		if (!isKnownKeyBinding)		
			currentProperty = new KeyBindingProperty(
					bindingConfigKey,
					keyBinding.getKeyDescription(),
					currentModName,
					keyBinding.getKeyCodeDefault(),
					keyBinding.getKeyModifierDefault().toString(),
					true,
					false);
		
		currentProperty.bindKeyBinding(keyBinding);
		
		currentProperty.setModId(currentModid);
		currentProperty.setModName(currentModName);
	}
	
	public static boolean isDoubleTapForwardSprintAllowed() {
		
		return ConfigLoader.isDoubleTapForwardSprintAllowed() && ConfigLoader.isPlayerSprintAllowed();
	}
	
	public static boolean isPlayerSprintAllowed() {
						
		EntityPlayer player = Minecraft.getMinecraft().player;
		
		if ((!player.isRiding() && ConfigLoader.isPlayerSprintAllowed()) || (player.isRiding() && ConfigLoader.isMountSprintAllowed()))
			return true;
		
		return false;
	}
	
	public static int isHotbarScrollingAllowed(int direction) {
		
		return ConfigLoader.isHotbarScrollingAllowed() ? direction : 0;
	}
	
	public static ClickType verifyClickAction(ClickType clickType) {
		
		switch (clickType) {
		
			case PICKUP: 
				return ClickType.PICKUP;
			case QUICK_MOVE: 
				return ConfigLoader.isGuiQuickMoveContainerAllowed() ? ClickType.QUICK_MOVE : ClickType.PICKUP;
			case SWAP: 
				return ConfigLoader.isGuiHotbarSlotSwapAllowed() ? ClickType.SWAP : ClickType.PICKUP;
			case CLONE: 
				return ConfigLoader.isGuiSlotStackCloneAllowed() ? ClickType.CLONE : ClickType.PICKUP;
			case THROW: 
				return ConfigLoader.isGuiThrowAllowed() ? ClickType.THROW : ClickType.PICKUP;
			case QUICK_CRAFT: 
				return ConfigLoader.isGuiQuickCraftAllowed() ? ClickType.QUICK_CRAFT : ClickType.PICKUP;
			case PICKUP_ALL: 
				return ConfigLoader.isGuiPickUpAllAllowed() ? ClickType.PICKUP_ALL : ClickType.PICKUP;
				
			default:
				return ClickType.PICKUP;
		}
	}
}
