package ru.austeretony.rebind.main;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.Configuration;

@SideOnly(Side.CLIENT)
public class ConfigurationRegistry {
	
   public static Configuration config;
   
   public static boolean rebindQuit, rebindHideGUI, rebindDebugMenu, rebingDisableShader;
   
   public static int defaultQuitKey, defaultHideGUIKey, defaultDebugMenuKey, defaultDisableShaderKey;

   public static void register(FMLPreInitializationEvent event) {
	   
       config = new Configuration(event.getSuggestedConfigurationFile());
       
       config.load();
       
       rebindQuit = config.getBoolean("rebindQuit", "Rebinding", true, I18n.format("config.rebindQuit"), "");
       rebindHideGUI = config.getBoolean("rebindHideGUI", "Rebinding", true, I18n.format("config.rebindHideGUI"), "");
       rebindDebugMenu = config.getBoolean("rebindDebugMenu", "Rebinding", true, I18n.format("config.rebindDebugMenu"), "");
       rebingDisableShader = config.getBoolean("rebindDisableShader", "Rebinding", true, I18n.format("config.rebindDisableShader"), "");

       defaultQuitKey = config.getInt("defaultQuitKey", "Default Key Codes", 1, 0, 223, I18n.format("config.defaultQuitKey"), "");
       defaultHideGUIKey = config.getInt("defaultHideGUIKey", "Default Key Codes", 59, 0, 223, I18n.format("config.defaultHideGUIKey"), "");
       defaultDebugMenuKey = config.getInt("defaultDebugMenuKey", "Default Key Codes", 61, 0, 223, I18n.format("config.defaultDebugMenuKey"), "");
       defaultDisableShaderKey = config.getInt("defaultDisableShaderKey", "Default Key Codes", 62, 0, 223, I18n.format("config.defaultDisableShaderKey"), "");

       config.save();
   }
}
