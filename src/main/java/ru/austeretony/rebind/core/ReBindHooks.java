package ru.austeretony.rebind.core;

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
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.common.Loader;
import ru.austeretony.rebind.config.ConfigLoader;
import ru.austeretony.rebind.main.KeyBindingProperty;
import ru.austeretony.rebind.main.ReBindMain;

public class ReBindHooks {
		
    public static final Set<String> ID_OCCURENCES = new HashSet<String>();  
	
	private static boolean isKnown;
			
	private static String modId, modName, keyBindingId;
	
	private static KeyBindingProperty keyBindingProperty;
		
	private static int keyCount;
		
	public static void removeHiddenKeyBindings() {
		
		if (Minecraft.getMinecraft().gameSettings != null) {
			
			List<KeyBinding> bindingsList = new ArrayList<KeyBinding>(Arrays.asList(Minecraft.getMinecraft().gameSettings.keyBindings));
			
			for (KeyBinding keyBinding : KeyBindingProperty.PROPERTIES_BY_KEYBINDINGS.keySet())
				if (!bindingsList.contains(keyBinding))
					bindingsList.add(keyBinding);
			
	    	Set<String> occurrences = new HashSet<String>();
			
			Iterator<KeyBinding> iterator = bindingsList.iterator();
			
			KeyBinding keyBinding;
			
			KeyBindingProperty property, holderProperty;
						
			while (iterator.hasNext()) {
				
				keyBinding = iterator.next();
				
				property = KeyBindingProperty.get(keyBinding);
				
				if (property.isKeyBindingMerged()) {	
										
					iterator.remove();				
				}
												
				else if (!property.isEnabled()) {				
						
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

    		String optionsPath = Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + "/options.txt";
			
			List<String> optionsLines;
			
			try (InputStream inputStream = new FileInputStream(new File(optionsPath))) {
				
				optionsLines = IOUtils.readLines(new InputStreamReader(inputStream, "UTF-8"));
			}
			
	    	catch (IOException exception) {
	    		
	    		exception.printStackTrace();
	    		
	    		return;
			}
			
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
				
	            try (PrintStream printStream = new PrintStream(new File(optionsPath))) {
				
	            	for (String line : optionsLines)	            	
	            		printStream.println(line);
	            }
	            
		    	catch (IOException exception) {
		    		
		    		exception.printStackTrace();
				}
			}		
		}
	}

	public static KeyBinding[] sortKeyBindings() {
		
		if (KeyBindingProperty.SORTED_KEYBINDINGS.isEmpty()) {
															
			for (KeyBindingProperty property : KeyBindingProperty.PROPERTIES_BY_IDS.values()) {
						
				if (property.isKnown()) {
					 
					if (property.isEnabled() && property.isFullyLoaded() && !property.isKeyBindingMerged()) 
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
	
	public static boolean isKeyDown(KeyBinding keyBinding) {
		
		KeyBindingProperty property = KeyBindingProperty.get(keyBinding);
		
		if (!property.isKeyBindingMerged()) {
			
			return keyBinding.pressed && keyBinding.getKeyConflictContext().isActive() && keyBinding.getKeyModifier().isActive();
		}
		
		else {
			
			KeyBinding holder = property.getHolderKeyBinding();
			
			return holder.pressed && holder.getKeyConflictContext().isActive() && holder.getKeyModifier().isActive();
		}
	}
	
	public static boolean isPressed(KeyBinding keyBinding) {
		
		KeyBindingProperty property = KeyBindingProperty.get(keyBinding);
		
		if (!property.isKeyBindingMerged()) {
					
	        if (keyBinding.pressTime == 0) {
	        		        		        	
	            return false;
	        }
	        
	        else {
	        		        	
	            --keyBinding.pressTime;
	            
	            return true;
	        }
		}
			
		else {			
									
	        if (property.getHolderKeyBinding().pressTime == 0)	        		        		        	
	            return false;
	        else   		        	
	            return true;	        
		}
	}
	
	public static boolean isActiveAndMatches(KeyBinding keyBinding, int keyCode) {
				
		if (!KeyBindingProperty.get(keyBinding).isKeyBindingMerged()) {
			
			return keyCode != 0 && keyCode == keyBinding.getKeyCode() && keyBinding.getKeyConflictContext().isActive() && keyBinding.getKeyModifier().isActive();
		}
		
		return false;
	}
	
	public static int getQuitKeyCode() {
		
		return ReBindMain.keyBindingQuit.getKeyCode();
	}

	public static boolean isQuitKeyPressed(int key) {
				
		return ReBindMain.keyBindingQuit.isActiveAndMatches(key);
	}

	public static boolean isHideHUDKeyPressed(int key) {
		
		return ReBindMain.keyBindingHideHUD.isActiveAndMatches(key);
	}
	
	public static int getDebugScreenKeyCode() {
				
		return ReBindMain.keyBindingDebugScreen.isActiveAndMatches(ReBindMain.keyBindingDebugScreen.getKeyCode()) ? ReBindMain.keyBindingDebugScreen.getKeyCode() : 0;
	}
	
	public static int getSwitchShaderKeyCode() {
		
		return ReBindMain.keyBindingSwitchShader.isPressed() ? ReBindMain.keyBindingSwitchShader.getKeyCode() : 0;
	}
	
	public static String getKeyBindingName(String keyName) {
						
		if (Loader.instance().activeModContainer() != null) {
									
			modId = Loader.instance().activeModContainer().getModId();			
			modName = Loader.instance().activeModContainer().getName();
		}
		
		else {
			
			if (keyCount < 30) {
				
				modId = "mc";				
				modName = "Minecraft";
			}
			
			else {
								
				modId = "optifine";				
				modName = "Optifine";
			}
		}
		
		modId = modId.toLowerCase();
		
		keyBindingId = modId + "_" + keyName.toLowerCase();		
		keyBindingId = keyBindingId.replaceAll("[()?:!.,;{} |]+", "_").replace("_key", "").replace("_" + modId, "");
					
		while (ID_OCCURENCES.contains(keyBindingId)) {
			
			keyBindingId = keyBindingId + "_";
		}
		
		isKnown = KeyBindingProperty.PROPERTIES_BY_IDS.containsKey(keyBindingId);
		
		//TODO Debug		
		ReBindClassTransformer.CORE_LOGGER.info("Keybinding id: " + keyBindingId + ", known: " + isKnown);
		
		if (isKnown) {
			
			keyBindingProperty = KeyBindingProperty.get(keyBindingId);
			
			if (!keyBindingProperty.getName().isEmpty())				
				keyName = keyBindingProperty.getName();		
		}
		
		return keyName;
	}
	
	public static KeyModifier getKeyBindingKeyModifier(KeyModifier keyModifier) {
		
		if (isKnown)
			keyModifier = KeyModifier.valueFromString(keyBindingProperty.getKeyModifier());
		
		return keyModifier;
	}
	
	public static int getKeyBindingKeyCode(int keyCode) {
						
		if (isKnown)			
			keyCode = keyBindingProperty.getKeyCode();    
		
		return keyCode;
	}

	public static String getKeyBindingCategory(String category) {
				
		if (isKnown) {
			
			String cat = keyBindingProperty.getCategory();
			
			if (cat.equals("mc.gameplay") ||
					cat.equals("mc.movement") ||
					cat.equals("mc.inventory") ||
					cat.equals("mc.misc") ||
					cat.equals("mc.multiplayer"))				
				category = "key.categories." + cat.substring(3);
			else
				category = keyBindingProperty.getCategory();
		}
		
		else {
			
			category = modName;
		}
		
		return category;
	}
	
	public static void storeKeybinding(KeyBinding keyBinding) {
		
		keyCount++;
		
		if (!isKnown)		
			keyBindingProperty = new KeyBindingProperty(
					keyBindingId,
					"",
					"",
					modName,
					keyBinding.getKeyCodeDefault(),
					keyBinding.getKeyModifierDefault() == KeyModifier.NONE ? "" : keyBinding.getKeyModifierDefault().toString(),
					true, 
					false);
		
		keyBindingProperty.bindKeyBinding(keyBinding);
		
		keyBindingProperty.setModId(modId);
		keyBindingProperty.setModName(modName);
		
		ID_OCCURENCES.add(keyBindingId);
	}
	
	public static boolean isDoubleTapForwardSprintAllowed() {
		
		return ConfigLoader.isDoubleTapForwardSprintAllowed() && ConfigLoader.isPlayerSprintAllowed();
	}
	
	public static boolean isPlayerSprintAllowed() {
						
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		
		if ((!player.isRiding() && ConfigLoader.isPlayerSprintAllowed()) || (player.isRiding() && ConfigLoader.isMountSprintAllowed()))
			return true;
		
		return false;
	}
	
	public static int isHotbarScrollingAllowed(int direction) {
		
		return ConfigLoader.isHotbarScrollingAllowed() ? direction : 0;
	}
}
