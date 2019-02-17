package austeretony.rebind.common.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public enum EnumConfigSettings {
    
    EXTERNAL_CONFIG("main", "external_config"),
    REWRITE_CONTROLS("main", "rewrite_controls"), 
    DEBUG_MODE("main", "debug_mode"),
    CHECK_UPDATES("main", "custom_update_checker"),
    FIX_MM_KEYBINDING("main", "fix_minemenu_keybinding"),
    AUTO_JUMP("game", "auto_jump"),
    PLAYER_SPRINT("controls", "player_sprint"),
    DOUBLE_TAP_FORWARD_SPRINT("controls", "double_tap_forward_sprint"),
    MOUNT_SPRINT("controls", "mount_sprint"),
    HOTBAR_SCROLLING("controls", "hotbar_scrolling");

    public final String configSection, configKey;

    private boolean isEnabled;

    EnumConfigSettings(String configSection, String configKey) {
        this.configSection = configSection;
        this.configKey = configKey;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    private JsonElement init(JsonObject jsonObject) {
        return jsonObject.get(this.configSection).getAsJsonObject().get(this.configKey);
    }

    public boolean initBoolean(JsonObject jsonObject) {
        return this.isEnabled = this.init(jsonObject).getAsBoolean();
    }
}
