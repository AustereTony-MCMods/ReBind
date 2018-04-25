package ru.austeretony.rebind.main;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ConfigurationRegistry {
	
   public static Configuration config;
   
   public static boolean rebindQuit, rebindHideGUI, rebindDebugMenu, rebingSwitchShader;
   
   public static int defaultQuitKey, defaultHideGUIKey, defaultDebugMenuKey, defaultSwitchShaderKey;

   public static void register(FMLPreInitializationEvent event) {
	   
       config = new Configuration(event.getSuggestedConfigurationFile());
       
       config.load();
       
       rebindQuit = config.getBoolean("rebindQuit", "Rebinding", true, I18n.format("config.rebindQuit"), "");
       rebindHideGUI = config.getBoolean("rebindHideGUI", "Rebinding", true, I18n.format("config.rebindHideGUI"), "");
       rebindDebugMenu = config.getBoolean("rebindDebugMenu", "Rebinding", true, I18n.format("config.rebindDebugMenu"), "");
       rebingSwitchShader = config.getBoolean("rebindSwitchShader", "Rebinding", true, I18n.format("config.rebindSwitchShader"), "");

       defaultQuitKey = config.getInt("defaultQuitKey", "Default Key Codes", 1, 0, 223, I18n.format("config.defaultQuitKey"), "");
       defaultHideGUIKey = config.getInt("defaultHideGUIKey", "Default Key Codes", 59, 0, 223, I18n.format("config.defaultHideGUIKey"), "");
       defaultDebugMenuKey = config.getInt("defaultDebugMenuKey", "Default Key Codes", 61, 0, 223, I18n.format("config.defaultDebugMenuKey"), "");
       defaultSwitchShaderKey = config.getInt("defaultSwitchShaderKey", "Default Key Codes", 62, 0, 223, I18n.format("config.defaultSwitchShaderKey"), "");

       config.save();
   }
}
