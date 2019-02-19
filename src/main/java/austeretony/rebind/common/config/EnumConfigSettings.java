package austeretony.rebind.common.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public enum EnumConfigSettings {

    EXTERNAL_CONFIG(0, "main", "external_config", true),
    REWRITE_CONTROLS(0, "main", "rewrite_controls"), 
    DEBUG_MODE(0, "main", "debug_mode"),
    CHECK_UPDATES(0, "main", "custom_update_checker"),
    CUSTOM_LOCALIZATION(0, "main", "enable_custom_localization"),
    FIX_MM_KEYBINDING(0, "main", "fix_minemenu_keybinding"),
    AUTO_JUMP(0, "game", "auto_jump"),
    PLAYER_SPRINT(0, "controls", "player_sprint"),
    DOUBLE_TAP_FORWARD_SPRINT(0, "controls", "double_tap_forward_sprint"),
    MOUNT_SPRINT(0, "controls", "mount_sprint"),
    HOTBAR_SCROLLING(0, "controls", "hotbar_scrolling");

    public final int type;//0 - boolean

    public final String configSection, configKey;

    public final boolean exclude;

    private boolean enabled;

    EnumConfigSettings(int type, String configSection, String configKey, boolean... exclude) {
        this.type = type;
        this.configSection = configSection;
        this.configKey = configKey;
        this.exclude = exclude.length > 0 ? exclude[0] : false;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    private JsonElement getValue(JsonObject jsonObject) {
        return jsonObject.get(this.configSection).getAsJsonObject().get(this.configKey);
    }

    public void initByType(JsonObject jsonObject) {
        switch (this.type) {
        case 0:
            this.enabled = this.getValue(jsonObject).getAsBoolean();
        }
    }

    public static void initAll(JsonObject config) {
        for (EnumConfigSettings enumSetting : values())
            if (!enumSetting.exclude)
                enumSetting.initByType(config);
    }
}
