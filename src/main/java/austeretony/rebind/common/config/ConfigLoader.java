package austeretony.rebind.common.config;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import austeretony.rebind.client.keybinding.KeyBindingProperty;
import austeretony.rebind.common.main.ReBindMain;
import austeretony.rebind.common.reference.CommonReference;
import austeretony.rebind.common.util.ReBindUtils;

public class ConfigLoader {

    public static final String
    EXT_CONFIGURATION_FILE = CommonReference.getGameFolder() + "/config/rebind/rebind.json",
    EXT_DATA_FILE = CommonReference.getGameFolder() + "/config/rebind/keybindings.json";

    private static boolean checkForUpdates, useExternalConfig, rewriteControls, enableDebugMode,
    enableAutoJump, allowDoubleTapForwardSprint, allowPlayerSprint, allowMountSprint, allowHotbarScrolling;

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
        useExternalConfig = internalConfig.get("main").getAsJsonObject().get("external_config").getAsBoolean();
        if (!useExternalConfig)
            loadData(internalConfig, internalSettings);
        else
            loadExternalConfig(internalConfig, internalSettings);
    }

    private static void loadExternalConfig(JsonObject internalConfig, JsonObject internalSettings) {
        Path 
        configPath = Paths.get(EXT_CONFIGURATION_FILE), 
        settingsPath = Paths.get(EXT_DATA_FILE);
        if (Files.exists(configPath) 
                && Files.exists(settingsPath)) {
            JsonObject externalConfig, externalSettings;
            try {
                externalConfig = (JsonObject) ReBindUtils.getExternalJsonData(EXT_CONFIGURATION_FILE);
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
        JsonObject mainSettings = configFile.get("main").getAsJsonObject();
        rewriteControls = mainSettings.get("rewrite_controls").getAsBoolean();
        enableDebugMode = mainSettings.get("debug_mode").getAsBoolean();
        checkForUpdates = mainSettings.get("updates").getAsJsonObject().get("update_checker").getAsBoolean();
        JsonObject ingameSettings = configFile.get("game").getAsJsonObject();
        enableAutoJump = ingameSettings.get("auto_jump").getAsBoolean();
        JsonObject controlsSettings = configFile.get("controls").getAsJsonObject();
        allowPlayerSprint = controlsSettings.get("player_sprint").getAsBoolean();
        allowDoubleTapForwardSprint = controlsSettings.get("double_tap_forward_sprint").getAsBoolean();
        allowMountSprint = controlsSettings.get("mount_sprint").getAsBoolean();
        allowHotbarScrolling = controlsSettings.get("hotbar_scrolling").getAsBoolean();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(KeyBindingsObject.class, new KeyBindingsDeserializer())
                .registerTypeAdapter(KeyBindingObject.class, new KeyBindingDeserializer())
                .create();
        KeyBindingsObject properties = gson.fromJson(settingsFile, KeyBindingsObject.class);
        for (Map.Entry<String, KeyBindingObject> entry : properties.getMap().entrySet()) {
            new KeyBindingProperty(
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
        Multimap<String, KeyBindingProperty> propsByModnames = LinkedHashMultimap.<String, KeyBindingProperty>create();
        Set<String> sortedModNames = new TreeSet<String>();
        for (KeyBindingProperty property : KeyBindingProperty.UNKNOWN) {
            propsByModnames.put(property.getModName(), property);
            sortedModNames.add(property.getModName());
        }
        KeyBindingsObject properties = new KeyBindingsObject();
        for (String modName : sortedModNames) {
            for (KeyBindingProperty property : propsByModnames.get(modName)) {
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

    public static boolean isUpdateCheckerEnabled() {
        return checkForUpdates;
    }

    public static boolean isExternalConfigEnabled() {
        return useExternalConfig;
    }

    public static boolean isControllsSettingsRewritingEnabled() {
        return rewriteControls;
    }

    public static boolean isDebugModeEnabled() {
        return enableDebugMode;
    }

    public static boolean isAutoJumpEnabled() {
        return enableAutoJump;
    }

    public static boolean isPlayerSprintAllowed() {
        return allowPlayerSprint;
    }

    public static boolean isDoubleTapForwardSprintAllowed() {
        return allowDoubleTapForwardSprint;
    }

    public static boolean isMountSprintAllowed() {
        return allowMountSprint;
    }

    public static boolean isHotbarScrollingAllowed() {
        return allowHotbarScrolling;
    }
}
