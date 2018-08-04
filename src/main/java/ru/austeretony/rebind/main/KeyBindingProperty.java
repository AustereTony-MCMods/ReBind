package ru.austeretony.rebind.main;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.settings.KeyBinding;

public class KeyBindingProperty {
	
	public static final Map<String, KeyBindingProperty> PROPERTIES_BY_IDS = new LinkedHashMap<String, KeyBindingProperty>();
	
	public static final Map<KeyBinding, KeyBindingProperty> PROPERTIES_BY_KEYBINDINGS = new HashMap<KeyBinding, KeyBindingProperty>();
	
    public static final Set<KeyBinding> SORTED_KEYBINDINGS = new LinkedHashSet<KeyBinding>();  
    
    public static final Set<KeyBindingProperty> UNKNOWN = new LinkedHashSet<KeyBindingProperty>();  
        	    
	private KeyBinding keyBinding;
	
	private final String keyBindingId, holderBindingId, configName, configCategory, configKeyModifier;
	
	private String modId, modName;
	
	private final int configKeyCode;
		
	private final boolean isEnabled, isKnown;
		
	public KeyBindingProperty(String keyId, String holderId, String name, String category, int keyCode, String keyModifier, boolean isEnabled, boolean isKnown) {
					
		this.keyBindingId = keyId;
		this.holderBindingId = holderId;
		this.configName = name;
		this.configCategory = category;		
		this.configKeyCode = keyCode;
		this.configKeyModifier = keyModifier;
		this.isEnabled = isEnabled;
		this.isKnown = isKnown;
		
		if (!isKnown)
			UNKNOWN.add(this);
		
		PROPERTIES_BY_IDS.put(keyId, this);
	}
	
	public static KeyBindingProperty get(String keyId) {
		
		return PROPERTIES_BY_IDS.get(keyId);
	}
	
	public static KeyBindingProperty get(KeyBinding keyBinding) {
		
		return PROPERTIES_BY_KEYBINDINGS.get(keyBinding);
	}
	
	public String getKeyBindingId() {
		
		return this.keyBindingId;
	}
	
	public String getHolderId() {
		
		return this.holderBindingId;
	}
	
	public KeyBinding getHolderKeyBinding() {
		
		return PROPERTIES_BY_IDS.get(this.holderBindingId).getKeyBinding();
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
	
	public String getKeyModifier() {
	
		return this.configKeyModifier;
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
	
	public void bindKeyBinding(KeyBinding keyBinding) {
		
		this.keyBinding = keyBinding;
		
		PROPERTIES_BY_KEYBINDINGS.put(this.keyBinding, this);		
	}
}
