package ru.austeretony.rebind.main;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.settings.KeyBinding;

public class KeyBindingProperty {
	
	public static final Map<String, KeyBindingProperty> PROPERTIES_BY_KEYS = new LinkedHashMap<String, KeyBindingProperty>();
	
	private static final Map<KeyBinding, KeyBindingProperty> PROPERTIES_BY_KEYBINDINGS = new HashMap<KeyBinding, KeyBindingProperty>();
	
    public static final Set<KeyBinding> SORTED_KEYBINDINGS = new LinkedHashSet<KeyBinding>();  
    
    public static final Set<KeyBindingProperty> UNKNOWN = new LinkedHashSet<KeyBindingProperty>();  
    	    
	private KeyBinding keyBinding;
	
	private final String configKey, configName, configCategory, configKeyModifier;
	
	private String modId, modName;
	
	private final int configKeyCode;
		
	private final boolean isEnabled, isKnown;
		
	public KeyBindingProperty(String configKey, String name, String category, int keyCode, String keyModifier, boolean isEnabled, boolean isKnown) {
					
		this.configKey = configKey;
		this.configName = name;
		this.configCategory = category;		
		this.configKeyCode = keyCode;
		this.configKeyModifier = keyModifier;
		this.isEnabled = isEnabled;
		this.isKnown = isKnown;
		
		PROPERTIES_BY_KEYS.put(configKey, this);
	}
	
	public static KeyBindingProperty get(String configKey) {
		
		return PROPERTIES_BY_KEYS.get(configKey);
	}
	
	public static KeyBindingProperty get(KeyBinding keyBinding) {
		
		return PROPERTIES_BY_KEYBINDINGS.get(keyBinding);
	}
	
	public String getConfigKey() {
		
		return this.configKey;
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
	
	public void bindKeyBinding(KeyBinding keyBinding) {
		
		this.keyBinding = keyBinding;
		
		PROPERTIES_BY_KEYBINDINGS.put(this.keyBinding, this);
		
		if (!isKnown)
			UNKNOWN.add(this);
	}
}
