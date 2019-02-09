package austeretony.rebind.common.config;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class KeyBindingsDeserializer implements JsonDeserializer<KeyBindingsObject> {

    @Override
    public KeyBindingsObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject input = json.getAsJsonObject();
        KeyBindingsObject properties = new KeyBindingsObject();
        for (Map.Entry<String, JsonElement> entry : input.entrySet())
            properties.getMap().put(entry.getKey(), context.deserialize(entry.getValue(), KeyBindingObject.class));
        return properties;
    }
}
