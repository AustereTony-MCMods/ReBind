package austeretony.rebind.common.config;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class KeyBindingDeserializer implements JsonDeserializer<KeyBindingObject> {

    @Override
    public KeyBindingObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject input = json.getAsJsonObject();
        return new KeyBindingObject(
                input.get("holder").getAsString(), 
                input.get("name").getAsString(),
                input.get("category").getAsString(), 
                input.get("key").getAsInt(), 
                input.get("mod").getAsString(),
                input.get("enabled").getAsBoolean());
    }
}
