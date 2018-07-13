package ru.austeretony.rebind.main;

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

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class KeyBindingProperty {
	
	public static final Map<String, KeyBindingProperty> PROPERTIES_BY_KEYS = new LinkedHashMap<String, KeyBindingProperty>();
	
	private static final Map<KeyBinding, KeyBindingProperty> PROPERTIES_BY_KEYBINDINGS = new HashMap<KeyBinding, KeyBindingProperty>();
	
    public static final Set<KeyBinding> SORTED_KEYBINDINGS = new LinkedHashSet<KeyBinding>();  
    
    public static final Set<KeyBindingProperty> UNKNOWN = new LinkedHashSet<KeyBindingProperty>();  
    
    private static final Multimap<EnumKeyModifier, KeyBindingProperty> KEY_MODIFIERS = HashMultimap.<EnumKeyModifier, KeyBindingProperty>create();
	    
	private KeyBinding keyBinding;
	
	private final String configKey, configName, configCategory;
	
	private String modId, modName;
	
	private final int configKeyCode;
	
	private final boolean isEnabled, isKnown;
	
	private final EnumKeyModifier defaultKeyModifier;
	
	private EnumKeyModifier keyModifier;
	
	private EnumKeyConflictContext keyConflictContext;
			
	public KeyBindingProperty(String configKey, String name, String category, int keyCode, EnumKeyModifier keyModifier, boolean enabled, boolean known) {
					
		this.configKey = configKey;
		this.configName = name;
		this.configCategory = category;		
		this.configKeyCode = keyCode;
		this.defaultKeyModifier = keyModifier;
		this.keyModifier = keyModifier;		
		
        if (keyModifier.match(keyCode))  	
        	this.keyModifier = EnumKeyModifier.NONE;       
        
		this.isEnabled = enabled;
		this.isKnown = known;
		
		this.keyConflictContext = EnumKeyConflictContext.UNIVERSAL;
		
		PROPERTIES_BY_KEYS.put(configKey, this);
		KEY_MODIFIERS.put(this.keyModifier, this);
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
    
	public boolean isKeyDown() {
				
		EnumKeyConflictContext conflictContext = this.getKeyConflictContext();
		
        return this.getKeyBinding().pressed && conflictContext.isActive() && this.getKeyModifier().isActive(conflictContext);
	}
	
	public static KeyBinding lookupActive(int keyCode) {
		
        EnumKeyModifier activeModifier = EnumKeyModifier.getActiveModifier();
        
        if (!activeModifier.match(keyCode)) {
        	
            KeyBinding key = getBinding(keyCode, activeModifier);
            
            if (key != null)          	
                return key;
        }
        
        return getBinding(keyCode, EnumKeyModifier.NONE);
	}
	
    private static KeyBinding getBinding(int keyCode, EnumKeyModifier keyModifier) {
    	
        Collection<KeyBindingProperty> propertties = KEY_MODIFIERS.get(keyModifier);
        
        if (propertties != null) {
        	
            for (KeyBindingProperty property : propertties) {
            	
                if (property.isActiveAndMatch(keyCode))               	
                    return property.getKeyBinding();
            }
        }
        
        return null;
    }
    
    public static List<KeyBinding> lookupAll(int keyCode) {
    	
        List<KeyBinding> matchingBindings = new ArrayList<KeyBinding>();
        
        for (KeyBindingProperty property : KEY_MODIFIERS.values()) {
        	            
            if (property.getKeyBinding().getKeyCode() == keyCode)           	
                matchingBindings.add(property.getKeyBinding());
        }
        
        return matchingBindings;
    }
    
    public boolean isActiveAndMatch(int keyCode) {
    	    	
    	EnumKeyConflictContext conflictContext = this.getKeyConflictContext();
    	
        return keyCode != 0 && keyCode == this.getKeyBinding().getKeyCode() && conflictContext.isActive() && this.getKeyModifier().isActive(conflictContext);
    }
	
    public boolean conflicts(KeyBindingProperty other) {
    	
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
    
    public boolean hasKeyCodeModifierConflict(KeyBindingProperty other) {
    	
        if (this.getKeyConflictContext().conflicts(other.getKeyConflictContext()) || other.getKeyConflictContext().conflicts(this.getKeyConflictContext())) {
        	
            if (this.getKeyModifier().match(other.getKeyBinding().getKeyCode()) || other.getKeyModifier().match(this.getKeyBinding().getKeyCode()))         	
                return true;
        }
        
        return false;
    }
    
    public String getDisplayName() {
    	
        return this.getKeyModifier().getLocalizedName(this.getKeyBinding().getKeyCode());
    }
    
    public static void setKeysConflictContext() {
    	
    	GameSettings gameSetings = Minecraft.getMinecraft().gameSettings;
    	
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
    	PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindPlayerList).setKeyConflictContext(inGame);
    	PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindCommand).setKeyConflictContext(inGame);
    	PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindTogglePerspective).setKeyConflictContext(inGame);
    	PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindSmoothCamera).setKeyConflictContext(inGame);
    	
    	PROPERTIES_BY_KEYBINDINGS.get(ReBindMain.Registry.KEY_HIDE_HUD).setKeyConflictContext(inGame);
    	PROPERTIES_BY_KEYBINDINGS.get(ReBindMain.Registry.KEY_DEBUG_SCREEN).setKeyConflictContext(inGame);
    	PROPERTIES_BY_KEYBINDINGS.get(ReBindMain.Registry.KEY_DISABLE_SHADER).setKeyConflictContext(inGame);
    }
}