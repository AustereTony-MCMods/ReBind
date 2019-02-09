package austeretony.rebind.common.config;

public class KeyBindingObject {

    public final String holder, name, category, keyModifier;

    public final int keyCode;

    public final boolean isEnabled;

    public KeyBindingObject(String holder, String name, String category, int keyCode, String keyModifier, boolean isEnabled) {
        this.holder = holder;
        this.name = name;
        this.category = category;
        this.keyCode = keyCode;
        this.keyModifier = keyModifier;
        this.isEnabled = isEnabled;
    }
}
