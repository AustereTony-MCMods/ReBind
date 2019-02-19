package austeretony.rebind.common.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import austeretony.rebind.client.reference.ClientReference;
import austeretony.rebind.common.commands.CommandReBind;
import austeretony.rebind.common.config.EnumConfigSettings;
import austeretony.rebind.common.reference.CommonReference;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;

@Mod(
        modid = ReBindMain.MODID, 
        name = ReBindMain.NAME,
        version = ReBindMain.VERSION,
        certificateFingerprint = "@FINGERPRINT@")
public class ReBindMain {

    public static final String 
    MODID = "rebind", 
    NAME = "ReBind", 
    VERSION = "2.7.5", 
    VERSION_CUSTOM = VERSION + ":release:0",
    GAME_VERSION = "1.7.10",
    VERSIONS_CUSTOM_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/ReBind/info/mod_versions_custom.json",
    PROJECT_LOCATION = "minecraft.curseforge.com",
    PROJECT_URL = "https://minecraft.curseforge.com/projects/rebind";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    @SideOnly(Side.CLIENT)
    public static KeyBinding keyBindingQuit, keyBindingHideHUD, keyBindingDebugScreen, keyBindingDisableShader;

    @SideOnly(Side.CLIENT)
    @EventHandler
    public void init(FMLInitializationEvent event) {
        ClientReference.registerKeyBinding(keyBindingQuit = new KeyBinding("key.quit", 0, ""));
        ClientReference.registerKeyBinding(keyBindingHideHUD = new KeyBinding("key.hideHUD", 0, ""));
        ClientReference.registerKeyBinding(keyBindingDebugScreen = new KeyBinding("key.debugScreen", 0, ""));
        ClientReference.registerKeyBinding(keyBindingDisableShader = new KeyBinding("key.disableShader", 0, ""));
        if (EnumConfigSettings.DEBUG_MODE.isEnabled())
            ClientReference.registerCommand(new CommandReBind());
        if (EnumConfigSettings.CHECK_UPDATES.isEnabled()) {
            UpdateChecker updateChecker = new UpdateChecker();
            CommonReference.registerEvent(updateChecker);
            new Thread(updateChecker, "ReBind Update Check").start();
        }
        if (EnumConfigSettings.AUTO_JUMP.isEnabled())
            CommonReference.registerEvent(new ReBindEvents());
    }
}
