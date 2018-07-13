package ru.austeretony.rebind.main;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;

public enum EnumKeyModifier {
	
    CONTROL {
    	
        @Override
        public boolean match(int keyCode) {
        	
            if (Minecraft.isRunningOnMac)          	
                return keyCode == Keyboard.KEY_LMETA || keyCode == Keyboard.KEY_RMETA;
            else
                return keyCode == Keyboard.KEY_LCONTROL || keyCode == Keyboard.KEY_RCONTROL;
        }
        
        @Override
        public boolean isActive(@Nullable EnumKeyConflictContext conflictContext) {
        	
            return GuiScreen.isCtrlKeyDown();
        }

        @Override
        public String getLocalizedName(int keyCode) {
        	
            String keyName = GameSettings.getKeyDisplayString(keyCode);
            
            String localizationFormatKey = Minecraft.isRunningOnMac ? "CMD" : "CTRL";
            
            return localizationFormatKey + " + " + keyName;
        }
    },
    
    SHIFT {
    	
        @Override
        public boolean match(int keyCode) {
        	
            return keyCode == Keyboard.KEY_LSHIFT || keyCode == Keyboard.KEY_RSHIFT;
        }
        
        @Override
        public boolean isActive(@Nullable EnumKeyConflictContext conflictContext) {
        	
            return GuiScreen.isShiftKeyDown();
        }

        @Override
        public String getLocalizedName(int keyCode) {
        	
            String keyName = GameSettings.getKeyDisplayString(keyCode);
            
            return  "SHIFT + " + keyName;
        }
    },
    
    ALT {
    	
        @Override
        public boolean match(int keyCode) {
        	
            return keyCode == Keyboard.KEY_LMENU || keyCode == Keyboard.KEY_RMENU;
        }
        
        @Override
        public boolean isActive(@Nullable EnumKeyConflictContext conflictContext) {
        	
            return isAltKeyDown();
        }

        @Override
        public String getLocalizedName(int keyCode) {
        	
            String keyName = GameSettings.getKeyDisplayString(keyCode);
            
            return  "ALT + " + keyName;
        }
    },
    
    NONE {
    	
        @Override
        public boolean match(int keyCode) {
        	
            return false;
        }
        
        @Override
        public boolean isActive(@Nullable EnumKeyConflictContext conflictContext) {
        	
            if (conflictContext != null && !conflictContext.conflicts(EnumKeyConflictContext.IN_GAME)) {
            	
                for (EnumKeyModifier keyModifier : MODIFIER_VALUES) {
                	
                    if (keyModifier.isActive(conflictContext))                    	
                        return false;
                }
            }
            
            return true;
        }

        @Override
        public String getLocalizedName(int keyCode) {
        	
            return GameSettings.getKeyDisplayString(keyCode);
        }
    };

    public static final EnumKeyModifier[] MODIFIER_VALUES = {SHIFT, CONTROL, ALT};

    public static EnumKeyModifier getActiveModifier() {
    	
        for (EnumKeyModifier keyModifier : MODIFIER_VALUES) {
        	
            if (keyModifier.isActive(null))        	
                return keyModifier;
        }
        
        return NONE;
    }

    public static boolean isKeyCodeModifier(int keyCode) {
    	
        for (EnumKeyModifier keyModifier : MODIFIER_VALUES) {
        	
            if (keyModifier.match(keyCode))      	
                return true;
        }
        
        return false;
    }

    public static EnumKeyModifier valueFromString(String stringValue) {
    	
        try {
        	
            return valueOf(stringValue);
        }
        
        catch (NullPointerException | IllegalArgumentException exception) {
        	
            return NONE;
        }
    }
    
    public static boolean isAltKeyDown() {
    	
        return Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
    }

    public abstract boolean match(int keyCode);
    
    public abstract boolean isActive(@Nullable EnumKeyConflictContext conflictContext);

    public abstract String getLocalizedName(int keyCode);
}