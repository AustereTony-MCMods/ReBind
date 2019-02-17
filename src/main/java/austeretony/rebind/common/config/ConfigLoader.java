package austeretony.rebind.common.config;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import austeretony.rebind.client.keybinding.KeyBindingWrapper;
import austeretony.rebind.common.core.ReBindClassTransformer;
import austeretony.rebind.common.main.ReBindMain;
import austeretony.rebind.common.main.UpdateChecker;
import austeretony.rebind.common.reference.CommonReference;
import austeretony.rebind.common.util.ReBindUtils;

public class ConfigLoader {

    public static final String
    EXT_CONFIGURATION_FILE = CommonReference.getGameFolder() + "/config/rebind/rebind.json",
    EXT_DATA_FILE = CommonReference.getGameFolder() + "/config/rebind/keybindings.json";

    public static void load() {
        JsonObject internalConfig, internalSettings;
        try {
            internalConfig = (JsonObject) ReBindUtils.getInternalJsonData("assets/rebind/rebind.json");
            internalSettings = (JsonObject) ReBindUtils.getInternalJsonData("assets/rebind/keybindings.json");
        } catch (IOException exception) {
            ReBindMain.LOGGER.error("Internal configuration files damaged!");
            exception.printStackTrace();
            return;
        } 
        if (EnumConfigSettings.EXTERNAL_CONFIG.initBoolean(internalConfig))
            loadExternalConfig(internalConfig, internalSettings);
        else
            loadData(internalConfig, internalSettings);
    }

    private static void loadExternalConfig(JsonObject internalConfig, JsonObject internalSettings) {
        Path 
        configPath = Paths.get(EXT_CONFIGURATION_FILE), 
        settingsPath = Paths.get(EXT_DATA_FILE);
        if (Files.exists(configPath) 
                && Files.exists(settingsPath)) {
            JsonObject externalConfig, externalSettings;
            try {
                externalConfig = updateConfig(internalConfig);  
                externalSettings = (JsonObject) ReBindUtils.getExternalJsonData(EXT_DATA_FILE);
            } catch (IOException exception) {
                ReBindMain.LOGGER.error("External configuration file damaged!");
                exception.printStackTrace();
                return;
            }
            loadData(externalConfig, externalSettings);
        } else {
            try { 
                Files.createDirectories(configPath.getParent());
                Files.createDirectories(settingsPath.getParent());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            createExternalCopyAndLoad(internalConfig, internalSettings);
        }
    }

    private static JsonObject updateConfig(JsonObject internalConfig) throws IOException {
        JsonObject externalConfigOld, externalConfigNew, externalGroupNew;
        try {                   
            externalConfigOld = (JsonObject) ReBindUtils.getExternalJsonData(EXT_CONFIGURATION_FILE);       
        } catch (IOException exception) {  
            ReBindClassTransformer.CORE_LOGGER.info("External configuration file damaged!");
            exception.printStackTrace();
            return null;
        }
        JsonElement versionElement = externalConfigOld.get("version");
        if (versionElement == null || UpdateChecker.isOutdated(versionElement.getAsString(), ReBindMain.VERSION_CUSTOM)) {
            ReBindClassTransformer.CORE_LOGGER.info("Updating external config file...");
            externalConfigNew = new JsonObject();
            externalConfigNew.add("version", new JsonPrimitive(ReBindMain.VERSION_CUSTOM));
            Map<String, JsonElement> 
            internalData = new LinkedHashMap<String, JsonElement>(),
            externlDataOld = new HashMap<String, JsonElement>(),
            internalGroup, externlGroupOld;
            for (Map.Entry<String, JsonElement> entry : internalConfig.entrySet())
                internalData.put(entry.getKey(), entry.getValue());
            for (Map.Entry<String, JsonElement> entry : externalConfigOld.entrySet())
                externlDataOld.put(entry.getKey(), entry.getValue());      
            for (String key : internalData.keySet()) {
                internalGroup = new LinkedHashMap<String, JsonElement>();
                externlGroupOld = new HashMap<String, JsonElement>();
                externalGroupNew = new JsonObject();
                for (Map.Entry<String, JsonElement> entry : internalData.get(key).getAsJsonObject().entrySet())
                    internalGroup.put(entry.getKey(), entry.getValue());
                if (externlDataOld.containsKey(key)) {                    
                    for (Map.Entry<String, JsonElement> entry : externlDataOld.get(key).getAsJsonObject().entrySet())
                        externlGroupOld.put(entry.getKey(), entry.getValue());   
                    for (String k : internalGroup.keySet()) {
                        if (externlGroupOld.containsKey(k))
                            externalGroupNew.add(k, externlGroupOld.get(k));
                        else 
                            externalGroupNew.add(k, internalGroup.get(k));
                    }
                } else {
                    for (String k : internalGroup.keySet())
                        externalGroupNew.add(k, internalGroup.get(k));
                }
                externalConfigNew.add(key, externalGroupNew);
                ReBindUtils.createExternalJsonFile(EXT_CONFIGURATION_FILE, externalConfigNew);
            }
            return externalConfigNew;
        }
        ReBindClassTransformer.CORE_LOGGER.info("External config up-to-date!");
        return externalConfigOld;
    }

    private static void createExternalCopyAndLoad(JsonObject internalConfig, JsonObject internalSettings) {
        try {
            ReBindUtils.createExternalJsonFile(EXT_CONFIGURATION_FILE, internalConfig);
            ReBindUtils.createAbsoluteJsonCopy(EXT_DATA_FILE, ReBindUtils.class.getClassLoader().getResourceAsStream("assets/rebind/keybindings.json"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        loadData(internalConfig, internalSettings);
    }

    private static void loadData(JsonObject configFile, JsonObject settingsFile) {
        EnumConfigSettings.REWRITE_CONTROLS.initBoolean(configFile);
        EnumConfigSettings.DEBUG_MODE.initBoolean(configFile);
        EnumConfigSettings.CHECK_UPDATES.initBoolean(configFile);
        EnumConfigSettings.FIX_MM_KEYBINDING.initBoolean(configFile);
        EnumConfigSettings.AUTO_JUMP.initBoolean(configFile);
        EnumConfigSettings.PLAYER_SPRINT.initBoolean(configFile);
        EnumConfigSettings.DOUBLE_TAP_FORWARD_SPRINT.initBoolean(configFile);
        EnumConfigSettings.MOUNT_SPRINT.initBoolean(configFile);
        EnumConfigSettings.HOTBAR_SCROLLING.initBoolean(configFile);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(KeyBindingsObject.class, new KeyBindingsDeserializer())
                .registerTypeAdapter(KeyBindingObject.class, new KeyBindingDeserializer())
                .create();
        KeyBindingsObject properties = gson.fromJson(settingsFile, KeyBindingsObject.class);
        for (Map.Entry<String, KeyBindingObject> entry : properties.getMap().entrySet()) {
            new KeyBindingWrapper(
                    entry.getKey(), 
                    entry.getValue().holder, 
                    entry.getValue().name,
                    entry.getValue().category, 
                    entry.getValue().keyCode, 
                    entry.getValue().keyModifier,
                    entry.getValue().isEnabled, 
                    true);
        }
    }

    public static void updateSettingsFile() {
        Multimap<String, KeyBindingWrapper> propsByModnames = LinkedHashMultimap.<String, KeyBindingWrapper>create();
        Set<String> sortedModNames = new TreeSet<String>();
        for (KeyBindingWrapper property : KeyBindingWrapper.UNKNOWN) {
            propsByModnames.put(property.getModName(), property);
            sortedModNames.add(property.getModName());
        }
        KeyBindingsObject properties = new KeyBindingsObject();
        for (String modName : sortedModNames) {
            for (KeyBindingWrapper property : propsByModnames.get(modName)) {
                properties.getMap().put(property.getKeyBindingId(), new KeyBindingObject(
                        property.getHolderId(), 
                        property.getName(), 
                        property.getCategory(),
                        property.getKeyCode(), 
                        property.getKeyModifier(), 
                        property.isEnabled()));
            }
        }
        Map<String, String> lines = new LinkedHashMap<String, String>();
        String line, l, 
        prevCategory = "";
        for (Map.Entry<String, KeyBindingObject> entry : properties.getMap().entrySet()) {
            line = "*" 
                    + entry.getKey() 
                    + "*: { *holder*: *" + entry.getValue().holder 
                    + "*, *name*: *" + entry.getValue().name 
                    + "*, *category*: *" + entry.getValue().category 
                    + "*, *key*: " + entry.getValue().keyCode 
                    + ", *mod*: *" + entry.getValue().keyModifier 
                    + "*, *enabled*: " + entry.getValue().isEnabled 
                    + "}";
            lines.put(line.replace('*', '"'), entry.getValue().category);//Hope '*' symbol will never be used in key binding name...
        }
        int index = 0;
        try (PrintStream printStream = new PrintStream(new File(EXT_DATA_FILE))) {
            printStream.println("{");
            for (Map.Entry<String, String> entry : lines.entrySet()) {
                index++;
                if (index != 1 && !entry.getValue().equals(prevCategory))
                    printStream.println("");
                l = entry.getKey();
                if (index < lines.size())
                    l = l + ",";
                printStream.println(l);
                prevCategory = entry.getValue();
            }
            printStream.println("}");
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
