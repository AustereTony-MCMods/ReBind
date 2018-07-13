package ru.austeretony.rebind.coremod;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
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

import cpw.mods.fml.common.Loader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import ru.austeretony.rebind.main.ConfigLoader;
import ru.austeretony.rebind.main.EnumKeyModifier;
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
						!options.contains("key_key.disableShader")) {
									
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
		
		return KeyBindingProperty.get(ReBindMain.Registry.KEY_QUIT).isActiveAndMatch(key);
	}

	public static boolean isHideHUDKeyPressed(int key) {
		
		return KeyBindingProperty.get(ReBindMain.Registry.KEY_HIDE_HUD).isActiveAndMatch(key);
	}
	
	public static int getDebugScreenKeyCode() {
				
		return ReBindMain.Registry.KEY_DEBUG_SCREEN.isPressed() ? ReBindMain.Registry.KEY_DEBUG_SCREEN.getKeyCode() : 0;
	}
	
	public static int getDisableShaderKeyCode() {
		
		return ReBindMain.Registry.KEY_DISABLE_SHADER.isPressed() ? ReBindMain.Registry.KEY_DISABLE_SHADER.getKeyCode() : 0;
	}
	
	public static String getKeyBindingName(String keyName) {
						
		if (Loader.instance().activeModContainer() != null) {
									
			currentModid = Loader.instance().activeModContainer().getModId();			
			currentModName = Loader.instance().activeModContainer().getName();
		}
		
		else {
			
			if (keyCount < 32) {
				
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
					cat.equals("mc.multiplayer") ||
					cat.equals("mc.stream"))				
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
					EnumKeyModifier.NONE,
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
						
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		
		if ((!player.isRiding() && ConfigLoader.isPlayerSprintAllowed()) || (player.isRiding() && ConfigLoader.isMountSprintAllowed()))
			return true;
		
		return false;
	}
	
	public static boolean isHotbarScrollingAllowed() {
		
		return ConfigLoader.isHotbarScrollingAllowed();
	}
	
	public static int verifyClickAction(int actionId) {
		
		switch (actionId) {
		
			case 0: 
				return 0;
			case 1: 
				return ConfigLoader.isGuiQuickMoveContainerAllowed() ? 1 : 0;
			case 2: 
				return ConfigLoader.isGuiHotbarSlotSwapAllowed() ? 2 : 0;
			case 3: 
				return ConfigLoader.isGuiSlotStackCloneAllowed() ? 3 : 0;
			case 4: 
				return ConfigLoader.isGuiThrowAllowed() ? 4 : 0;
			case 5: 
				return ConfigLoader.isGuiQuickCraftAllowed() ? 5 : 0;
			case 6: 
				return ConfigLoader.isGuiPickUpAllAllowed() ? 6 : 0;
				
			default:
				return actionId;
		}
	}
	
	public static void setKeyModifierAndCode(KeyBinding key, EnumKeyModifier keyModifier, int keyCode) {
				
		KeyBindingProperty.get(key).setKeyModifierAndCode(keyModifier, keyCode);
	}
	
	public static KeyBinding resetKeyBinding(KeyBinding key, int keyCode) {
		
		if (!EnumKeyModifier.isKeyCodeModifier(keyCode))
			return null;
		
		return key;
	}
	
	public static void drawCuiControlsKeyEntry(GuiButton changeKeyButton, GuiButton resetButton, KeyBinding key, boolean flag, int par2, int par3, int par7, int par8) {
		
		KeyBindingProperty 
		property = KeyBindingProperty.get(key),
		otherProperty;

        resetButton.xPosition = par2 + 210;
        resetButton.yPosition = par3;
        resetButton.enabled = !property.isSetToDefaultValue();
        resetButton.drawButton(Minecraft.getMinecraft(), par7, par8);
                   
        changeKeyButton.width = 95;
        changeKeyButton.xPosition = par2 + 105;
        changeKeyButton.yPosition = par3;
        changeKeyButton.displayString = property.getDisplayName();
        
        boolean 
        flag1 = false,
		keyCodeModifierConflict = true;
		
        if (key.getKeyCode() != 0) {
        	
            for (KeyBinding keyBinding : Minecraft.getMinecraft().gameSettings.keyBindings) {
            	
            	otherProperty = KeyBindingProperty.get(keyBinding);
            	
                if (keyBinding != key && property.conflicts(otherProperty)) {
                	
                    flag1 = true;
                    
                    keyCodeModifierConflict &= property.hasKeyCodeModifierConflict(otherProperty);
                }
            }
        }

        if (flag)     	
        	changeKeyButton.displayString = EnumChatFormatting.WHITE + "> " + EnumChatFormatting.YELLOW + changeKeyButton.displayString + EnumChatFormatting.WHITE + " <";
        else if (flag1)        	
        	changeKeyButton.displayString = (keyCodeModifierConflict ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + changeKeyButton.displayString;
        
        changeKeyButton.drawButton(Minecraft.getMinecraft(), par7, par8);
	}

	public static void setResetButtonState(GuiButton resetAllButton) {
				
		boolean state = false;
		
        for (KeyBinding key : Minecraft.getMinecraft().gameSettings.keyBindings) {
        	        	
            if (!KeyBindingProperty.get(key).isSetToDefaultValue()) {
            	
            	state = true;
            	
                break;
            }
        }
        
    	resetAllButton.enabled = state;
	}
	
	public static void setToDefault(KeyBinding key) {
				
		KeyBindingProperty.get(key).setToDefault();
	}
	
	public static void resetAllKeys() {
		
		KeyBindingProperty property;
		
        for (KeyBinding key : Minecraft.getMinecraft().gameSettings.keyBindings)       	        	
        	KeyBindingProperty.get(key).setToDefault();

        KeyBinding.resetKeyBindingArrayAndHash();
	}
	
	public static boolean loadControlsFromOptionsFile(String[] data) {
				
		if (Minecraft.getMinecraft().gameSettings != null) {
			
			KeyBindingProperty property;

	        for (KeyBinding key : Minecraft.getMinecraft().gameSettings.keyBindings) {
			
		        if (data[0].equals("key_" + key.getKeyDescription())) {
		        	
		        	property = KeyBindingProperty.get(key);
		        	
			        if (data[1].indexOf('&') != - 1) {
			        	
			            String[] keySettings = data[1].split("&");
			            
			            property.setKeyModifierAndCode(EnumKeyModifier.valueFromString(keySettings[1]), Integer.parseInt(keySettings[0]));
			        } 
			        
			        else {
			        	
			        	property.setKeyModifierAndCode(EnumKeyModifier.NONE, Integer.parseInt(data[1]));
			        }
		        }
	        }
		}
        
        return false;
	}
	
	public static boolean saveControlsToOptionsFile(PrintWriter writer) {
		
		if (Minecraft.getMinecraft().gameSettings != null) {
			
			KeyBindingProperty property;
			
	        for (KeyBinding key : Minecraft.getMinecraft().gameSettings.keyBindings) {
	        	
	        	property = KeyBindingProperty.get(key);
		
		        String keyString = "key_" + key.getKeyDescription() + ":" + key.getKeyCode();
		        	        
		        writer.println(property.getKeyModifier() != EnumKeyModifier.NONE ? keyString + "&" + property.getKeyModifier().toString() : keyString);
			}
		}
        
        return false;
	}
	
	public static KeyBinding lookupActive(int keyCode) {
		
		return KeyBindingProperty.lookupActive(keyCode);
	}
	
	public static void setKeybindingsState(int keyCode, boolean state) {
		
        if (keyCode != 0) {
        	
            for (KeyBinding key : KeyBindingProperty.lookupAll(keyCode)) {

            	if (key != null)	        	
            		key.pressed = state;  
            }
        }
	}
	
	public static boolean isKeyPressed(KeyBinding key) {
				
		return KeyBindingProperty.get(key).isKeyDown();
	}
}
