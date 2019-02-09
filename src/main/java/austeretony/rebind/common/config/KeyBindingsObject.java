package austeretony.rebind.common.config;

import java.util.LinkedHashMap;
import java.util.Map;

public class KeyBindingsObject {

    private static final Map<String, KeyBindingObject> PROPERTIES = new LinkedHashMap<String, KeyBindingObject>();

    public static Map<String, KeyBindingObject> getMap() {
        return PROPERTIES;
    }
}
