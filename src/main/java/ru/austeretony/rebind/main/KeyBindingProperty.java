package ru.austeretony.rebind.main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class KeyBindingProperty {
	
	private final String configKey, name, category, modifier;
	
	private final int keyCode;
	
	private final boolean enabled;
			
	public KeyBindingProperty(String configKey, String name, String category, int keyCode, String modifier, boolean enabled) {
					
		this.configKey = configKey;
		this.name = name;
		this.category = category;		
		this.keyCode = keyCode;
		this.modifier = modifier;
		this.enabled = enabled;
	}
	
	public String getConfigKey() {
		
		return this.configKey;
	}
	
	public String getName() {
		
		return this.name;
	}
	
	public String getCategory() {
		
		return this.category;
	}
	
	public int getKeyCode() {
		
		return this.keyCode;
	}
	
	public String getKeyModifier() {
		
		return this.modifier;
	}
	
	public boolean isEnabled() {
		
		return this.enabled;
	}
	
	//TODO Key Modifiers getters and setters
	
	public static EnumKeyModifier getKeyModifier(KeyBinding key) {
		
		return ConfigLoader.KEY_MODIFIERS.get(key);
	}
	
	public static void setKeyModifier(KeyBinding key, EnumKeyModifier keyModifier) {
		
		ConfigLoader.KEY_MODIFIERS.put(key, keyModifier);
	}
	
	public static EnumKeyModifier getDefaultKeyModifier(KeyBinding key) {
		
		return ConfigLoader.DEFAULT_KEY_MODIFIERS.get(key);
	}
	
	public static EnumKeyConflictContext getKeyConflictContext(KeyBinding key) {
		
		return ConfigLoader.CONFLICT_CONTEXT.get(key);
	}
	
	public static void setKeyConflictContext(KeyBinding key, EnumKeyConflictContext keyConflictContext) {
		
		ConfigLoader.CONFLICT_CONTEXT.put(key, keyConflictContext);
	}
	
    public static void setKeyModifierAndCode(KeyBinding key, EnumKeyModifier keyModifier, int keyCode) {
    	
        key.setKeyCode(keyCode);
        
        if (keyModifier.match(keyCode)) {
        	
            keyModifier = EnumKeyModifier.NONE;
        }
        
        ConfigLoader.MODIFIERS.remove(getKeyModifier(key), key);
        ConfigLoader.keyBindingsHash.removeObject(keyCode);
        setKeyModifier(key, keyModifier);
        ConfigLoader.MODIFIERS.put(keyModifier, key);
        ConfigLoader.keyBindingsHash.addKey(keyCode, key);
    }
    
    public static void setToDefault(KeyBinding key) {
    	
        setKeyModifierAndCode(key, getDefaultKeyModifier(key), key.getKeyCodeDefault());
    }

    public static boolean isSetToDefaultValue(KeyBinding key) {
    	
        return key.getKeyCode() == key.getKeyCodeDefault() && getKeyModifier(key) == getDefaultKeyModifier(key);
    }
    
	public static boolean isKeyDown(KeyBinding key) {
		
		EnumKeyConflictContext conflictContext = getKeyConflictContext(key);
		
        return key.pressed && conflictContext.isActive() && getKeyModifier(key).isActive(conflictContext);
	}
	
	public static KeyBinding lookupActive(int keyCode) {
		
        EnumKeyModifier activeModifier = EnumKeyModifier.getActiveModifier();
        
        if (!activeModifier.match(keyCode)) {
        	
            KeyBinding key = getBinding(keyCode, activeModifier);
            
            if (key != null) {
            	
                return key;
            }
        }
        
        return getBinding(keyCode, EnumKeyModifier.NONE);
	}
	
    private static KeyBinding getBinding(int keyCode, EnumKeyModifier keyModifier) {
    	
        Collection<KeyBinding> keys = ConfigLoader.MODIFIERS.get(keyModifier);
        
        if (keys != null) {
        	
            for (KeyBinding key : keys) {
            	
                if (isActiveAndMatch(key, keyCode)) {
                	
                    return key;
                }
            }
        }
        
        return null;
    }
    
    public static List<KeyBinding> lookupAll(int keyCode) {
    	
        List<KeyBinding> matchingBindings = new ArrayList<KeyBinding>();
        
        for (KeyBinding key : ConfigLoader.MODIFIERS.values()) {
        	            
            if (key.getKeyCode() == keyCode) {
            	
                matchingBindings.add(key);
            }
        }
        
        return matchingBindings;
    }
    
    public static boolean isActiveAndMatch(KeyBinding key, int keyCode) {
    	
    	EnumKeyConflictContext conflictContext = getKeyConflictContext(key);
    	
        return keyCode != 0 && keyCode == key.getKeyCode() && conflictContext.isActive() && getKeyModifier(key).isActive(conflictContext);
    }
	
    public static boolean conflicts(KeyBinding currentKey, KeyBinding other) {
    	
        if (getKeyConflictContext(currentKey).conflicts(getKeyConflictContext(other)) || getKeyConflictContext(other).conflicts(getKeyConflictContext(currentKey))) {
        	
            EnumKeyModifier 
            keyModifier = getKeyModifier(currentKey),
            otherKeyModifier = getKeyModifier(other);
            
            if (keyModifier.match(other.getKeyCode()) || otherKeyModifier.match(currentKey.getKeyCode())) {
            	
                return true;
            }
            
            else if (currentKey.getKeyCode() == other.getKeyCode()) {
            	
                return keyModifier == otherKeyModifier || (getKeyConflictContext(currentKey).conflicts(EnumKeyConflictContext.IN_GAME) && (keyModifier == EnumKeyModifier.NONE || otherKeyModifier == EnumKeyModifier.NONE));
            }
        }
        
        return false;
    }
    
    public static boolean hasKeyCodeModifierConflict(KeyBinding currentKey, KeyBinding other) {
    	
        if (getKeyConflictContext(currentKey).conflicts(getKeyConflictContext(other)) || getKeyConflictContext(other).conflicts(getKeyConflictContext(currentKey))) {
        	
            if (getKeyModifier(currentKey).match(other.getKeyCode()) || getKeyModifier(other).match(currentKey.getKeyCode())) {
            	
                return true;
            }
        }
        
        return false;
    }
    
    public static String getDisplayName(KeyBinding key) {
    	
        return getKeyModifier(key).getLocalizedName(key.getKeyCode());
    }
    
    public static void setKeysConflictContext() {
    	
    	GameSettings gameSetings = Minecraft.getMinecraft().gameSettings;
    	
    	EnumKeyConflictContext inGame = EnumKeyConflictContext.IN_GAME;
        
        setKeyConflictContext(gameSetings.keyBindForward, inGame);
        setKeyConflictContext(gameSetings.keyBindLeft, inGame);
        setKeyConflictContext(gameSetings.keyBindBack, inGame);
        setKeyConflictContext(gameSetings.keyBindRight, inGame);
        setKeyConflictContext(gameSetings.keyBindJump, inGame);
        setKeyConflictContext(gameSetings.keyBindSneak, inGame);
        setKeyConflictContext(gameSetings.keyBindSprint, inGame);
        setKeyConflictContext(gameSetings.keyBindAttack, inGame);
        setKeyConflictContext(gameSetings.keyBindChat, inGame);
        setKeyConflictContext(gameSetings.keyBindPlayerList, inGame);
        setKeyConflictContext(gameSetings.keyBindCommand, inGame);
        setKeyConflictContext(gameSetings.keyBindTogglePerspective, inGame);
        setKeyConflictContext(gameSetings.keyBindSmoothCamera, inGame);
        
        setKeyConflictContext(ReBindMain.Registry.KEY_QUIT, inGame);
        setKeyConflictContext(ReBindMain.Registry.KEY_HIDE_HUD, inGame);
        setKeyConflictContext(ReBindMain.Registry.KEY_DEBUG_SCREEN, inGame);
        setKeyConflictContext(ReBindMain.Registry.KEY_DISABLE_SHADER, inGame);
    }
}