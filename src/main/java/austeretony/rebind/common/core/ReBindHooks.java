package austeretony.rebind.common.core;

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
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.base.Splitter;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import austeretony.rebind.client.keybinding.EnumKeyModifier;
import austeretony.rebind.client.keybinding.KeyBindingWrapper;
import austeretony.rebind.client.reference.ClientReference;
import austeretony.rebind.common.config.ConfigLoader;
import austeretony.rebind.common.config.EnumConfigSettings;
import austeretony.rebind.common.main.ReBindMain;
import austeretony.rebind.common.reference.CommonReference;
import cpw.mods.fml.common.Loader;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;

public class ReBindHooks {

    public static final Set<String> ID_OCCURENCES = new HashSet<String>();  

    private static boolean isKnown;

    private static String modid, modName, keyBindingId;

    private static KeyBindingWrapper keyBindingProperty;

    private static int keyCount;

    public static void loadCustomLocalization(List<String> languageList, Map<String, String> properties) {
        ConfigLoader.loadCustomLocalization(languageList, properties);
    }

    public static void removeHiddenKeyBindings() {           
        if (ClientReference.getGameSettings() != null) {                                          
            List<KeyBinding> bindingsList = new ArrayList<KeyBinding>(Arrays.asList(ClientReference.getKeyBindings()));
            for (KeyBinding keyBinding : KeyBindingWrapper.PROPERTIES_BY_KEYBINDINGS.keySet())
                if (!bindingsList.contains(keyBinding))
                    bindingsList.add(keyBinding);                                         
            Set<String> occurrences = new HashSet<String>();
            Iterator<KeyBinding> iterator = bindingsList.iterator();
            KeyBinding keyBinding;
            KeyBindingWrapper property;
            while (iterator.hasNext()) {
                keyBinding = iterator.next();
                property = KeyBindingWrapper.get(keyBinding);
                if (property.isKeyBindingMerged()) {    
                    iterator.remove();                              
                } else if (!property.isEnabled()) {                               
                    keyBinding.setKeyCode(property.getKeyCode());
                    iterator.remove();                              
                } else {
                    occurrences.add(keyBinding.getKeyCategory());
                }
            }
            KeyBinding.getKeybinds().retainAll(occurrences);
            ClientReference.getGameSettings().keyBindings = bindingsList.toArray(new KeyBinding[bindingsList.size()]);
        }
    }

    public static void removeControlsSettings() {
        if (EnumConfigSettings.REWRITE_CONTROLS.isEnabled()) {
            String optionsPath = CommonReference.getGameFolder() + "/options.txt";
            List<String> optionsLines;
            try (InputStream inputStream = new FileInputStream(new File(optionsPath))) {
                optionsLines = IOUtils.readLines(new InputStreamReader(inputStream, "UTF-8"));
            } catch (IOException exception) {
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
                    !options.contains("key_key.disableShader")) {
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
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }               
        }
    }

    public static KeyBinding[] sortKeyBindings() {
        if (KeyBindingWrapper.SORTED_KEYBINDINGS.isEmpty()) {
            removeHiddenKeyBindings();
            List<KeyBinding> bindingsList = new ArrayList<KeyBinding>(Arrays.asList(ClientReference.getKeyBindings()));
            KeyBinding keyBinding;
            Iterator<Map.Entry<String, KeyBindingWrapper>> propIterator = KeyBindingWrapper.PROPERTIES_BY_IDS.entrySet().iterator();                                      
            KeyBindingWrapper prop;                        
            while (propIterator.hasNext()) {
                prop = propIterator.next().getValue();                          
                if (prop.getKeyBinding() == null) {
                    propIterator.remove();
                    continue;
                }                                           
                if (prop.isKnown()) {        
                    if (prop.isEnabled() && prop.isFullyLoaded() && !prop.isKeyBindingMerged()) {
                        keyBinding = prop.getKeyBinding();                       
                        if (bindingsList.contains(keyBinding))
                            KeyBindingWrapper.SORTED_KEYBINDINGS.add(prop.getKeyBinding());
                    }
                } else {
                    continue;
                }
            }
            Iterator<Map.Entry<EnumKeyModifier, KeyBindingWrapper>> modIterator = KeyBindingWrapper.KEY_MODIFIERS.entries().iterator();                   
            while (modIterator.hasNext()) {
                prop = modIterator.next().getValue();
                if (prop.getKeyBinding() == null)
                    modIterator.remove();
            }
            Multimap<String, KeyBindingWrapper> propsByModnames = LinkedHashMultimap.<String, KeyBindingWrapper>create();
            Set<String> sortedModNames = new TreeSet<String>();
            for (KeyBindingWrapper property : KeyBindingWrapper.UNKNOWN) {
                propsByModnames.put(property.getModName(), property);
                sortedModNames.add(property.getModName());
            }
            for (String modName : sortedModNames) {
                for (KeyBindingWrapper property : propsByModnames.get(modName)) {
                    keyBinding = property.getKeyBinding();                       
                    if (bindingsList.contains(keyBinding))
                        KeyBindingWrapper.SORTED_KEYBINDINGS.add(property.getKeyBinding());
                }
            }
        }
        return KeyBindingWrapper.SORTED_KEYBINDINGS.toArray(new KeyBinding[KeyBindingWrapper.SORTED_KEYBINDINGS.size()]);                 
    }

    public static void onTick(int keyCode) {
        if (keyCode != 0) {
            KeyBinding keybinding = KeyBindingWrapper.lookup(keyCode);
            if (keybinding != null)
                ++keybinding.pressTime;
        }
    }

    public static void setKeyBindState(int keyCode, boolean state) {
        if (keyCode != 0)
            for (KeyBinding key : KeyBindingWrapper.lookupAll(keyCode))
                if (key != null)                        
                    key.pressed = state;  
    }

    public static boolean isKeyPressed(KeyBinding key) {
        return KeyBindingWrapper.get(key).isKeyPressed();
    }

    public static boolean isPressed(KeyBinding keyBinding) {
        KeyBindingWrapper property = KeyBindingWrapper.get(keyBinding);
        if (!property.isKeyBindingMerged()) {
            if (keyBinding.pressTime == 0) {
                return false;
            } else {
                --keyBinding.pressTime;
                return true;
            }
        } else {                  
            if (property.getHolderKeyBinding().pressTime == 0)                                                                      
                return false;
            else                            
                return true;                
        }
    }

    public static int getQuitKeyCode() {
        return ReBindMain.keyBindingQuit.getKeyCode();
    }

    public static boolean isQuitKeyPressed(int key) {
        return KeyBindingWrapper.get(ReBindMain.keyBindingQuit).isActiveAndMatches(key);
    }

    public static boolean isHideHUDKeyPressed(int key) {
        return KeyBindingWrapper.get(ReBindMain.keyBindingHideHUD).isActiveAndMatches(key);
    }

    public static int getDebugScreenKeyCode() {
        return KeyBindingWrapper.get(ReBindMain.keyBindingDebugScreen).isActiveAndMatches(ReBindMain.keyBindingDebugScreen.getKeyCode()) ? ReBindMain.keyBindingDebugScreen.getKeyCode() : 0;
    }

    public static int getDisableShaderKeyCode() {
        return ReBindMain.keyBindingDisableShader.isPressed() ? ReBindMain.keyBindingDisableShader.getKeyCode() : 0;
    }

    public static boolean isMineMenuKeyPressed(KeyBinding keyBinding) {
        return KeyBindingWrapper.get(keyBinding).isActiveAndMatches(Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey());
    }

    public static String getKeyBindingName(String keyName) {
        if (Loader.instance().activeModContainer() != null) {
            modid = Loader.instance().activeModContainer().getModId();                      
            modName = Loader.instance().activeModContainer().getName();
        } else {
            if (keyCount < 32) {
                modid = "mc";                           
                modName = "Minecraft";
            } else {
                modid = "optifine";                             
                modName = "Optifine";
            }
        }
        if (modid.equals("rebind")) {
            if (keyName.equals("key.macro_override") || keyName.equals("key.macros")) {
                modid = "macrosmod";                            
                modName = "Macros Mod";
            }                               
        }
        modid = modid.toLowerCase();
        keyBindingId = modid + "_" + keyName.toLowerCase();             
        keyBindingId = keyBindingId.replaceAll("[()?:!.,;{} |]+", "_").replace("_key", "").replace("_" + modid, "");
        while (ID_OCCURENCES.contains(keyBindingId)) {
            keyBindingId = keyBindingId + "_";
        }
        isKnown = KeyBindingWrapper.PROPERTIES_BY_IDS.containsKey(keyBindingId);
        //TODO Debug            
        ReBindClassTransformer.CORE_LOGGER.info("Keybinding id: " + keyBindingId + ", known: " + isKnown);
        if (isKnown) {
            keyBindingProperty = KeyBindingWrapper.get(keyBindingId);
            if (!keyBindingProperty.getName().isEmpty())                            
                keyName = keyBindingProperty.getName();         
        }
        return keyName;
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
                    cat.equals("mc.multiplayer") ||
                    cat.equals("mc.stream"))                                
                category = "key.categories." + cat.substring(3);
            else
                category = keyBindingProperty.getCategory();
        } else {
            category = modName;
        }
        return category;
    }

    public static void storeKeybinding(KeyBinding keyBinding) {
        keyCount++;
        if (!isKnown)           
            keyBindingProperty = new KeyBindingWrapper(
                    keyBindingId,
                    "",
                    "",
                    modName,
                    keyBinding.getKeyCodeDefault(),
                    EnumKeyModifier.NONE,
                    true,
                    false);
        keyBindingProperty.bindKeyBinding(keyBinding);
        keyBindingProperty.setModId(modid);
        keyBindingProperty.setModName(modName);
        ID_OCCURENCES.add(keyBindingId);
    }

    public static boolean isDoubleTapForwardSprintAllowed() {
        return EnumConfigSettings.PLAYER_SPRINT.isEnabled() && EnumConfigSettings.DOUBLE_TAP_FORWARD_SPRINT.isEnabled();
    }

    public static boolean isPlayerSprintAllowed() {
        if ((!ClientReference.getClientPlayer().isRiding() && EnumConfigSettings.PLAYER_SPRINT.isEnabled()) 
                || (ClientReference.getClientPlayer().isRiding() && EnumConfigSettings.MOUNT_SPRINT.isEnabled()))
            return true;
        return false;
    }

    public static boolean isHotbarScrollingAllowed() {
        return EnumConfigSettings.HOTBAR_SCROLLING.isEnabled();
    }

    public static void setKeyModifierAndCode(KeyBinding key, EnumKeyModifier keyModifier, int keyCode) {
        KeyBindingWrapper.get(key).setKeyModifierAndCode(keyModifier, keyCode);
    }

    public static KeyBinding resetKeyBinding(KeyBinding key, int keyCode) {
        if (!EnumKeyModifier.isKeyCodeModifier(keyCode))
            return null;
        return key;
    }

    public static void drawCuiControlsKeyEntry(GuiButton changeKeyButton, GuiButton resetButton, KeyBinding key, boolean flag, int par2, int par3, int par7, int par8) {
        KeyBindingWrapper 
        property = KeyBindingWrapper.get(key),
        otherProperty;
        resetButton.xPosition = par2 + 210;
        resetButton.yPosition = par3;
        resetButton.enabled = !property.isSetToDefaultValue();
        resetButton.drawButton(ClientReference.getMinecraft(), par7, par8);
        changeKeyButton.width = 95;
        changeKeyButton.xPosition = par2 + 105;
        changeKeyButton.yPosition = par3;
        changeKeyButton.displayString = property.getDisplayName();
        boolean 
        flag1 = false,
        keyCodeModifierConflict = true;
        if (key.getKeyCode() != 0) {
            for (KeyBinding keyBinding : ClientReference.getKeyBindings()) {
                otherProperty = KeyBindingWrapper.get(keyBinding);
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
        changeKeyButton.drawButton(ClientReference.getMinecraft(), par7, par8);
    }

    public static void setResetButtonState(GuiButton resetAllButton) {
        boolean state = false;
        for (KeyBinding key : ClientReference.getKeyBindings()) {
            if (!KeyBindingWrapper.get(key).isSetToDefaultValue()) {
                state = true;
                break;
            }
        }
        resetAllButton.enabled = state;
    }

    public static void setToDefault(KeyBinding key) {
        KeyBindingWrapper.get(key).setToDefault();
    }

    public static void resetAllKeys() {
        KeyBindingWrapper property;
        for (KeyBinding key : ClientReference.getKeyBindings())                        
            KeyBindingWrapper.get(key).setToDefault();
        KeyBinding.resetKeyBindingArrayAndHash();
    }

    public static boolean loadControlsFromOptionsFile(String[] data) {
        if (ClientReference.getGameSettings() != null) {
            KeyBindingWrapper property;
            for (KeyBinding key : ClientReference.getKeyBindings()) {
                if (data[0].equals("key_" + key.getKeyDescription())) {
                    property = KeyBindingWrapper.get(key);
                    if (data[1].indexOf('&') != - 1) {
                        String[] keySettings = data[1].split("&");
                        property.setKeyModifierAndCode(EnumKeyModifier.valueFromString(keySettings[1]), Integer.parseInt(keySettings[0]));
                    } else {
                        property.setKeyModifierAndCode(EnumKeyModifier.NONE, Integer.parseInt(data[1]));
                    }
                }
            }
        }
        return false;
    }

    public static boolean saveControlsToOptionsFile(PrintWriter writer) {
        if (ClientReference.getGameSettings() != null) {
            KeyBindingWrapper property;
            for (KeyBinding key : ClientReference.getKeyBindings()) {
                property = KeyBindingWrapper.get(key);
                String keyString = "key_" + key.getKeyDescription() + ":" + key.getKeyCode();
                writer.println(property.getKeyModifier() != EnumKeyModifier.NONE ? keyString + "&" + property.getKeyModifier().toString() : keyString);
            }
        }
        return false;
    }
}
