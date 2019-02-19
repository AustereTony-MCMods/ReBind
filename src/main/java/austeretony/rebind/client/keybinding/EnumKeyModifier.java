package austeretony.rebind.client.keybinding;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import austeretony.rebind.client.reference.ClientReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;

public enum EnumKeyModifier {

    CONTROL,
    SHIFT,
    ALT,
    NONE;

    public static final EnumKeyModifier[] MODIFIER_VALUES = {SHIFT, CONTROL, ALT};

    public static EnumKeyModifier getActiveModifier() {
        for (EnumKeyModifier keyModifier : MODIFIER_VALUES)       	
            if (keyModifier.isActive(null))        	
                return keyModifier;       
        return NONE;
    }

    public static boolean isKeyCodeModifier(int keyCode) {   	
        for (EnumKeyModifier keyModifier : MODIFIER_VALUES)     	
            if (keyModifier.match(keyCode))      	
                return true;
        return false;
    }

    public static EnumKeyModifier valueFromString(String stringValue) {  	
        try {    	
            return valueOf(stringValue);
        } catch (NullPointerException | IllegalArgumentException exception) {
            return NONE;
        }
    }

    public static boolean isAltKeyDown() {	
        return Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
    }

    public boolean match(int keyCode) {
        switch (this) {
        case CONTROL:
            if (ClientReference.getMinecraft().isRunningOnMac)           
                return keyCode == Keyboard.KEY_LMETA || keyCode == Keyboard.KEY_RMETA;
            else
                return keyCode == Keyboard.KEY_LCONTROL || keyCode == Keyboard.KEY_RCONTROL;
        case SHIFT:
            return keyCode == Keyboard.KEY_LSHIFT || keyCode == Keyboard.KEY_RSHIFT;
        case ALT:
            return keyCode == Keyboard.KEY_LMENU || keyCode == Keyboard.KEY_RMENU;
        case NONE:
            return false;
        }
        return false;
    }

    public boolean isActive(@Nullable EnumKeyConflictContext conflictContext) {
        switch (this) {
        case CONTROL:
            return GuiScreen.isCtrlKeyDown();
        case SHIFT:
            return GuiScreen.isShiftKeyDown();
        case ALT:
            return isAltKeyDown();
        case NONE:
            if (conflictContext != null && !conflictContext.conflicts(EnumKeyConflictContext.IN_GAME))               
                for (EnumKeyModifier keyModifier : MODIFIER_VALUES) 
                    if (keyModifier.isActive(conflictContext))                          
                        return false;
            return true;
        }
        return false;
    }

    public String getLocalizedName(int keyCode) {
        switch (this) {
        case CONTROL:
            String localizationFormatKey = ClientReference.getMinecraft().isRunningOnMac ? "CMD" : "CTRL";
            return localizationFormatKey + " + " + GameSettings.getKeyDisplayString(keyCode);
        case SHIFT:            
            return  "SHIFT + " + GameSettings.getKeyDisplayString(keyCode);
        case ALT:            
            return  "ALT + " + GameSettings.getKeyDisplayString(keyCode);
        case NONE:
            return GameSettings.getKeyDisplayString(keyCode);
        }
        return null;
    }
}