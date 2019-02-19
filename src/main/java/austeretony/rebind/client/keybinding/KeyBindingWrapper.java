package austeretony.rebind.client.keybinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import austeretony.rebind.client.reference.ClientReference;
import austeretony.rebind.common.main.ReBindMain;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class KeyBindingWrapper {

    public static final Map<String, KeyBindingWrapper> PROPERTIES_BY_IDS = new LinkedHashMap<String, KeyBindingWrapper>();

    public static final Map<KeyBinding, KeyBindingWrapper> PROPERTIES_BY_KEYBINDINGS = new HashMap<KeyBinding, KeyBindingWrapper>();

    public static final Set<KeyBinding> SORTED_KEYBINDINGS = new LinkedHashSet<KeyBinding>();  

    public static final Set<KeyBindingWrapper> UNKNOWN = new LinkedHashSet<KeyBindingWrapper>();  

    public static final Multimap<EnumKeyModifier, KeyBindingWrapper> KEY_MODIFIERS = HashMultimap.<EnumKeyModifier, KeyBindingWrapper>create();

    private KeyBinding keyBinding;

    private final String keyBindingId, holderBindingId, configName, configCategory;

    private String modId, modName;

    private final int configKeyCode;

    private final boolean isEnabled, isKnown;

    private final EnumKeyModifier defaultKeyModifier;

    private EnumKeyModifier keyModifier;

    private EnumKeyConflictContext keyConflictContext;

    public KeyBindingWrapper(String configKey, String holderId, String name, String category, int keyCode, EnumKeyModifier keyModifier, boolean isEnabled, boolean isKnown) {
        this.keyBindingId = configKey;
        this.holderBindingId = holderId;
        this.configName = name;
        this.configCategory = category;         
        this.configKeyCode = keyCode;
        this.defaultKeyModifier = keyModifier;
        this.keyModifier = keyModifier;    
        //Cause NoClassDefFoundError for Minecraft
        //if (keyModifier.match(keyCode))         
            //this.keyModifier = EnumKeyModifier.NONE;       
        this.isEnabled = isEnabled;
        this.isKnown = isKnown;
        this.keyConflictContext = EnumKeyConflictContext.UNIVERSAL;
        if (!isKnown)
            UNKNOWN.add(this);
        PROPERTIES_BY_IDS.put(configKey, this);
        KEY_MODIFIERS.put(this.keyModifier, this);
    }

    public static KeyBindingWrapper get(String configKey) {
        return PROPERTIES_BY_IDS.get(configKey);
    }

    public static KeyBindingWrapper get(KeyBinding keyBinding) {
        return PROPERTIES_BY_KEYBINDINGS.get(keyBinding);
    }

    public String getKeyBindingId() {
        return this.keyBindingId;
    }

    public String getName() {
        return this.configName;
    }

    public String getCategory() {
        return this.configCategory;
    }

    public int getKeyCode() {
        return this.configKeyCode;
    }

    public EnumKeyModifier getDefaultKeyModifier() {
        return this.defaultKeyModifier;
    }

    public EnumKeyModifier getKeyModifier() {
        return this.keyModifier;
    }

    public void setKeyModifier(EnumKeyModifier keyModifier) {
        this.keyModifier = keyModifier;
    }

    public EnumKeyConflictContext getKeyConflictContext() {
        return this.keyConflictContext;
    }

    public void setKeyConflictContext(EnumKeyConflictContext keyConflictContext) {
        this.keyConflictContext = keyConflictContext;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public boolean isKnown() {
        return this.isKnown;
    }

    public boolean isKeyBindingMerged() {
        return !this.holderBindingId.isEmpty();
    }

    public String getModId() {
        return this.modId;
    }

    public void setModId(String modId) {
        this.modId = modId;
    }

    public String getModName() {
        return this.modName;
    }

    public void setModName(String modName) {
        this.modName = modName;
    }

    public KeyBinding getKeyBinding() {
        return this.keyBinding;
    }

    public boolean isFullyLoaded() {
        return this.keyBinding != null;
    }

    public String getHolderId() {
        return this.holderBindingId;
    }

    public KeyBindingWrapper getHolderProperty() {
        return PROPERTIES_BY_IDS.get(this.holderBindingId);
    }

    public KeyBinding getHolderKeyBinding() {
        return PROPERTIES_BY_IDS.get(this.holderBindingId).getKeyBinding();
    }

    public void bindKeyBinding(KeyBinding keyBinding) {
        this.keyBinding = keyBinding;
        PROPERTIES_BY_KEYBINDINGS.put(this.keyBinding, this);
    }

    public void setKeyModifierAndCode(EnumKeyModifier keyModifier, int keyCode) {
        this.getKeyBinding().setKeyCode(keyCode);
        if (keyModifier.match(keyCode))         
            keyModifier = EnumKeyModifier.NONE;
        KEY_MODIFIERS.remove(this.getKeyModifier(), this);
        KeyBinding.hash.removeObject(keyCode);
        this.setKeyModifier(keyModifier);
        KEY_MODIFIERS.put(keyModifier, this);
        KeyBinding.hash.addKey(keyCode, this.getKeyBinding());
    }

    public void setToDefault() {
        setKeyModifierAndCode(this.getDefaultKeyModifier(), this.getKeyBinding().getKeyCodeDefault());
    }

    public boolean isSetToDefaultValue() {
        return this.getKeyBinding().getKeyCode() == this.getKeyBinding().getKeyCodeDefault() && this.getKeyModifier() == this.getDefaultKeyModifier();
    }

    public boolean isKeyPressed() {
        if (!this.isKeyBindingMerged()) {
            return this.getKeyBinding().pressed && this.getKeyConflictContext().isActive() && this.getKeyModifier().isActive(this.getKeyConflictContext());
        } else {
            KeyBindingWrapper holder = this.getHolderProperty();
            return holder.getKeyBinding().pressed && holder.getKeyConflictContext().isActive() && holder.getKeyModifier().isActive(holder.getKeyConflictContext());
        }
    }

    public static KeyBinding lookup(int keyCode) {
        EnumKeyModifier activeModifier = EnumKeyModifier.getActiveModifier();
        if (!activeModifier.match(keyCode)) {
            KeyBinding key = getBinding(keyCode, activeModifier);
            if (key != null)            
                return key;
        }
        return getBinding(keyCode, EnumKeyModifier.NONE);
    }

    private static KeyBinding getBinding(int keyCode, EnumKeyModifier keyModifier) {
        Collection<KeyBindingWrapper> properties = KEY_MODIFIERS.get(keyModifier);
        if (properties != null)
            for (KeyBindingWrapper property : properties)
                if (property.isActiveAndMatches(keyCode))                       
                    return property.getKeyBinding();
        return null;
    }

    public boolean isActiveAndMatches(int keyCode) {
        if (!this.isKeyBindingMerged())
            return keyCode != 0 && keyCode == this.getKeyBinding().getKeyCode() && this.getKeyConflictContext().isActive() && this.getKeyModifier().isActive(this.getKeyConflictContext());
        return false;
    }

    public static List<KeyBinding> lookupAll(int keyCode) {
        List<KeyBinding> matchingBindings = new ArrayList<KeyBinding>();
        for (KeyBindingWrapper property : KEY_MODIFIERS.values())
            if (property.getKeyBinding().getKeyCode() == keyCode)               
                matchingBindings.add(property.getKeyBinding());
        return matchingBindings;
    }

    public boolean conflicts(KeyBindingWrapper other) {
        if (this.getKeyConflictContext().conflicts(other.getKeyConflictContext()) || other.getKeyConflictContext().conflicts(this.getKeyConflictContext())) {
            EnumKeyModifier 
            keyModifier = this.getKeyModifier(),
            otherKeyModifier = other.getKeyModifier();
            if (keyModifier.match(other.getKeyBinding().getKeyCode()) || otherKeyModifier.match(this.getKeyBinding().getKeyCode()))             
                return true;
            else if (this.getKeyBinding().getKeyCode() == other.getKeyBinding().getKeyCode())                   
                return keyModifier == otherKeyModifier || (this.getKeyConflictContext().conflicts(EnumKeyConflictContext.IN_GAME) && (keyModifier == EnumKeyModifier.NONE || otherKeyModifier == EnumKeyModifier.NONE));
        }

        return false;
    }

    public boolean hasKeyCodeModifierConflict(KeyBindingWrapper other) {
        if (this.getKeyConflictContext().conflicts(other.getKeyConflictContext()) || other.getKeyConflictContext().conflicts(this.getKeyConflictContext()))
            if (this.getKeyModifier().match(other.getKeyBinding().getKeyCode()) || other.getKeyModifier().match(this.getKeyBinding().getKeyCode()))             
                return true;
        return false;
    }

    public String getDisplayName() {
        return this.getKeyModifier().getLocalizedName(this.getKeyBinding().getKeyCode());
    }

    public static void setKeysConflictContext() {
        GameSettings gameSetings = ClientReference.getGameSettings();
        EnumKeyConflictContext inGame = EnumKeyConflictContext.IN_GAME;
        PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindForward).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindLeft).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindBack).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindRight).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindJump).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindSneak).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindSprint).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindAttack).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindChat).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindDrop).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindPlayerList).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindCommand).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindTogglePerspective).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindSmoothCamera).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(ReBindMain.keyBindingHideHUD).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(ReBindMain.keyBindingDebugScreen).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(ReBindMain.keyBindingDisableShader).setKeyConflictContext(inGame);
    }
}
