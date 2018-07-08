package ru.austeretony.rebind.coremod;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import org.apache.commons.io.IOUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.Loader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IntHashMap;
import ru.austeretony.rebind.main.ConfigLoader;
import ru.austeretony.rebind.main.EnumKeyConflictContext;
import ru.austeretony.rebind.main.EnumKeyModifier;
import ru.austeretony.rebind.main.KeyBindingProperty;
import ru.austeretony.rebind.main.ReBindMain;

public class ReBindHooks {
	
	private static Minecraft mc = Minecraft.getMinecraft();
	
	private static boolean isKnownKeyBinding;
			
	private static String currentModid, currentModName, bindingConfigKey;
	
	private static KeyBindingProperty currentProperty;
	
	private static EnumKeyModifier currentKeyModifier;
		
	public static void removeHiddenKeyBindings() {
		
		if (mc.gameSettings != null) {
			
			List<KeyBinding> bindingsList = new ArrayList<KeyBinding>(Arrays.asList(mc.gameSettings.keyBindings));
			
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
						
			mc.gameSettings.keyBindings = bindingsList.toArray(new KeyBinding[bindingsList.size()]);
		}
	}
	
	public static void rewriteControlsSettings() {
		
		if (ConfigLoader.isControllsSettingsRewritingEnabled()) {

	    	try {
	    		
	    		String optionsPath = mc.mcDataDir.getAbsolutePath() + "/options.txt";
	    		
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
		
		return ReBindMain.Registry.KEY_HIDE_HUD.isPressed() ? ReBindMain.Registry.KEY_HIDE_HUD.getKeyCode() : 0;
	}
	
	public static int getDebugMenuKeyCode() {
		
		return ReBindMain.Registry.KEY_DEBUG_SCREEN.isPressed() ? ReBindMain.Registry.KEY_DEBUG_SCREEN.getKeyCode() : 0;
	}
	
	public static int getDisableShaderKeyCode() {
		
		return ReBindMain.Registry.KEY_DISABLE_SHADER.isPressed() ? ReBindMain.Registry.KEY_DISABLE_SHADER.getKeyCode() : 0;
	}
	
	public static void getKeyBindingsHash(IntHashMap hash) {
		
		if (ConfigLoader.keyBindingsHash == null) {
						
			ConfigLoader.keyBindingsHash = hash;
		}
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
		
		bindingConfigKey = bindingConfigKey.replaceAll("[. ]", "_").replace("_key", "").replace("_" + currentModid, "");
				
		isKnownKeyBinding = ConfigLoader.PROPERTIES.containsKey(bindingConfigKey);
		
		//TODO Debug		
		ReBindClassTransformer.LOGGER.info("Keybinding id: " + bindingConfigKey + ", known: " + isKnownKeyBinding);
		
		if (isKnownKeyBinding) {
			
			currentProperty = ConfigLoader.PROPERTIES.get(bindingConfigKey);
			
			if (!currentProperty.getName().isEmpty()) {
				
				keyName = currentProperty.getName();
			}
		}
		
		return keyName;
	}
	
	public static int getKeyBindingKeyCode(int keyCode) {
		
    	currentKeyModifier = EnumKeyModifier.NONE;
				
		if (isKnownKeyBinding) {
			
			keyCode = currentProperty.getKeyCode();
			
			currentKeyModifier = EnumKeyModifier.valueFromString(currentProperty.getKeyModifier());
			
	        if (currentKeyModifier.match(keyCode)) {
	        	
	        	currentKeyModifier = EnumKeyModifier.NONE;
	        }	     
		}
		
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
				
		KeyBindingProperty.setKeyModifier(keyBinding, currentKeyModifier);
		
		ConfigLoader.DEFAULT_KEY_MODIFIERS.put(keyBinding, currentKeyModifier);
		
		KeyBindingProperty.setKeyConflictContext(keyBinding, EnumKeyConflictContext.UNIVERSAL);
		
		ConfigLoader.MODIFIERS.put(currentKeyModifier, keyBinding);
	}
	
	public static boolean isDoubleTapForwardSprintAllowed() {
		
		return ConfigLoader.isDoubleTapForwardSprintAllowed() && ConfigLoader.isPlayerSprintAllowed();
	}
	
	public static boolean isPlayerSprintAllowed() {
						
		EntityPlayer player = mc.thePlayer;
		
		if ((!player.isRiding() && ConfigLoader.isPlayerSprintAllowed()) || (player.isRiding() && ConfigLoader.isMountSprintAllowed()))
		return true;
		
		return false;
	}
	
	public static void setKeyModifierAndCode(KeyBinding key, EnumKeyModifier keyModifier, int keyCode) {
		
		KeyBindingProperty.setKeyModifierAndCode(key, keyModifier, keyCode);
	}
	
	public static KeyBinding resetKeyBinding(KeyBinding key, int keyCode) {
		
		if (!EnumKeyModifier.isKeyCodeModifier(keyCode))
		return null;
		
		return key;
	}
	
	public static void setKeyBindingButtonDispalyString(GuiButton keyButton, GuiButton resetButton, KeyBinding key, boolean flag) {
				
		resetButton.enabled = !KeyBindingProperty.isSetToDefaultValue(key);
		
		keyButton.displayString = KeyBindingProperty.getDisplayName(key);
		
        boolean 
        flag1 = false,
		keyCodeModifierConflict = true;
		
        if (key.getKeyCode() != 0) {
        	
            for (KeyBinding keyBinding : mc.gameSettings.keyBindings) {
            	
                if (keyBinding != key && KeyBindingProperty.conflicts(keyBinding, key)) {
                	
                    flag1 = true;
                    
                    keyCodeModifierConflict &= KeyBindingProperty.hasKeyCodeModifierConflict(keyBinding, key);
                }
            }
        }

        if (flag) {
        	
        	keyButton.displayString = EnumChatFormatting.WHITE + "> " + EnumChatFormatting.YELLOW + keyButton.displayString + EnumChatFormatting.WHITE + " <";
        }
        
        else if (flag1) {
        	
        	keyButton.displayString = (keyCodeModifierConflict ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + keyButton.displayString;
        }
	}
	
	public static void drawCuiControlsKeyEntry(GuiButton changeKeyButton, GuiButton resetButton, KeyBinding key, boolean flag, int par2, int par3, int par7, int par8) {
		
        resetButton.xPosition = par2 + 210;
        resetButton.yPosition = par3;
        resetButton.enabled = !KeyBindingProperty.isSetToDefaultValue(key);
        resetButton.drawButton(mc, par7, par8);
                   
        changeKeyButton.width = 95;
        changeKeyButton.xPosition = par2 + 105;
        changeKeyButton.yPosition = par3;
        changeKeyButton.displayString = KeyBindingProperty.getDisplayName(key);
        
        boolean 
        flag1 = false,
		keyCodeModifierConflict = true;
		
        if (key.getKeyCode() != 0) {
        	
            for (KeyBinding keyBinding : mc.gameSettings.keyBindings) {
            	
                if (keyBinding != key && KeyBindingProperty.conflicts(keyBinding, key)) {
                	
                    flag1 = true;
                    
                    keyCodeModifierConflict &= KeyBindingProperty.hasKeyCodeModifierConflict(keyBinding, key);
                }
            }
        }

        if (flag) {
        	
        	changeKeyButton.displayString = EnumChatFormatting.WHITE + "> " + EnumChatFormatting.YELLOW + changeKeyButton.displayString + EnumChatFormatting.WHITE + " <";
        }
        
        else if (flag1) {
        	
        	changeKeyButton.displayString = (keyCodeModifierConflict ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + changeKeyButton.displayString;
        }
        
        changeKeyButton.drawButton(mc, par7, par8);
	}

	public static void setResetButtonState(GuiButton resetAllButton) {
		
		boolean state = false;
		
        for (KeyBinding key : mc.gameSettings.keyBindings) {
        	
            if (!KeyBindingProperty.isSetToDefaultValue(key)) {
            	
            	state = true;
            	
                break;
            }
        }
        
    	resetAllButton.enabled = state;
	}
	
	public static void setToDefault(KeyBinding key) {
		
		KeyBindingProperty.setToDefault(key);
	}
	
	public static void resetAllKeys() {
		
        for (KeyBinding key : mc.gameSettings.keyBindings) {
        	
        	KeyBindingProperty.setToDefault(key);
        }

        KeyBinding.resetKeyBindingArrayAndHash();
	}
	
	public static boolean loadControlsFromOptionsFile(String[] data) {
				
		if (mc.gameSettings != null) {

	        for (KeyBinding key : mc.gameSettings.keyBindings) {
			
		        if (data[0].equals("key_" + key.getKeyDescription())) {
		        	
			        if (data[1].indexOf('&') != - 1) {
			        	
			            String[] keySettings = data[1].split("&");
			            
			            KeyBindingProperty.setKeyModifierAndCode(key, EnumKeyModifier.valueFromString(keySettings[1]), Integer.parseInt(keySettings[0]));
			        } 
			        
			        else {
			        	
			        	KeyBindingProperty.setKeyModifierAndCode(key, EnumKeyModifier.NONE, Integer.parseInt(data[1]));
			        }
		        }
	        }
		}
        
        return false;
	}
	
	public static boolean saveControlsToOptionsFile(PrintWriter writer) {
		
		if (mc.gameSettings != null) {

			EnumKeyModifier keyModifier;
			
	        for (KeyBinding key : mc.gameSettings.keyBindings) {
	        	
		        keyModifier = KeyBindingProperty.getKeyModifier(key);
		
		        String keyString = "key_" + key.getKeyDescription() + ":" + key.getKeyCode();
		        	        
		        writer.println(keyModifier != EnumKeyModifier.NONE ? keyString + "&" + keyModifier : keyString);
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

            	if (key != null) {
	        	
            		key.pressed = state;
            	}  
            }
        }
	}
	
	public static boolean isKeyPressed(KeyBinding key) {
		
		return KeyBindingProperty.isKeyDown(key);
	}
}
