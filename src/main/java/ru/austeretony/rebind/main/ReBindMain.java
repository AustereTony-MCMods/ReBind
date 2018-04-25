package ru.austeretony.rebind.main;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = ReBindMain.MODID, name = ReBindMain.NAME, version = ReBindMain.VERSION)
public class ReBindMain {
	
    public static final String 
	MODID = "rebind",
    NAME = "ReBind",
    VERSION = "1.0";
    
    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
		
        logger = event.getModLog();	  
        
        if (event.getSide() == Side.CLIENT)	{
        	
        	ConfigurationRegistry.register(event);
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    	
    	if (event.getSide() == Side.CLIENT)	{
    		
    		KeyRegistry.registerKeys();
    	}
    }
	
	public static Logger logger() {
		
		return logger;
	}
}
