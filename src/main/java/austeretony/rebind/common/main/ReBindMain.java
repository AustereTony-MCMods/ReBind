package austeretony.rebind.common.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import austeretony.rebind.client.reference.ClientReference;
import austeretony.rebind.common.command.CommandReBind;
import austeretony.rebind.common.config.ConfigLoader;
import austeretony.rebind.common.config.EnumConfigSettings;
import austeretony.rebind.common.reference.CommonReference;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(
        modid = ReBindMain.MODID, 
        name = ReBindMain.NAME,
        version = ReBindMain.VERSION,
        certificateFingerprint = "@FINGERPRINT@",
        updateJSON = ReBindMain.VERSIONS_FORGE_URL)
public class ReBindMain {

    public static final String 
    MODID = "rebind", 
    NAME = "ReBind", 
    VERSION = "2.7.3", 
    VERSION_CUSTOM = VERSION + ":release:0",
    GAME_VERSION = "1.12.2",
    VERSIONS_FORGE_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/ReBind/info/mod_versions_forge.json",
    VERSIONS_CUSTOM_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/ReBind/info/mod_versions_custom.json",
    PROJECT_LOCATION = "minecraft.curseforge.com",
    PROJECT_URL = "https://minecraft.curseforge.com/projects/rebind";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    @SideOnly(Side.CLIENT)
    public static KeyBinding keyBindingQuit, keyBindingHideHUD, keyBindingDebugScreen, keyBindingSwitchShader, keyBindingNarrator;

    @SideOnly(Side.CLIENT)
    @EventHandler
    public void init(FMLInitializationEvent event) {
        ClientReference.registerKeyBinding(keyBindingQuit = new KeyBinding("key.quit", 0, ""));
        ClientReference.registerKeyBinding(keyBindingHideHUD = new KeyBinding("key.hideHUD", KeyConflictContext.IN_GAME, 0, ""));
        ClientReference.registerKeyBinding(keyBindingDebugScreen = new KeyBinding("key.debugScreen", KeyConflictContext.IN_GAME, 0, ""));
        ClientReference.registerKeyBinding(keyBindingSwitchShader = new KeyBinding("key.switchShader", KeyConflictContext.IN_GAME, 0, ""));
        ClientReference.registerKeyBinding(keyBindingNarrator = new KeyBinding("key.narrator", KeyConflictContext.IN_GAME, 0, ""));
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
