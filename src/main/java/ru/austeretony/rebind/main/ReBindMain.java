package ru.austeretony.rebind.main;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

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
