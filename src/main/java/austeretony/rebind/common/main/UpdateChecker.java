package austeretony.rebind.common.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import austeretony.rebind.common.util.ReBindUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class UpdateChecker implements Runnable {

    private static String availableVersion = ReBindMain.VERSION;

    private static boolean notified;

    @SubscribeEvent
    public void onPlayerJoinedWorld(EntityJoinWorldEvent event) {
        if (event.getEntity().world.isRemote && event.getEntity() instanceof EntityPlayer) {
            if (!notified) {
                notified = true;
                if (ReBindUtils.isOutdated(ReBindMain.VERSION, availableVersion))
                    EnumChatMessages.UPDATE_MESSAGE.showMessage(availableVersion);
            } else {
                MinecraftForge.EVENT_BUS.unregister(this);
            }
        }
    } 

    @Override
    public void run() {
        ReBindMain.LOGGER.info("Update check started...");
        URL versionsURL;                
        try {                   
            versionsURL = new URL(ReBindMain.VERSIONS_URL);
        } catch (MalformedURLException exception) {                     
            exception.printStackTrace();                        
            return;
        }
        JsonObject remoteData;                                  
        try (InputStream inputStream = versionsURL.openStream()) {                      
            remoteData = (JsonObject) new JsonParser().parse(new InputStreamReader(inputStream, "UTF-8")); 
        } catch (UnknownHostException exception) {              
            ReBindMain.LOGGER.error("Update check failed, no internet connection.");               
            return;
        } catch (FileNotFoundException exception) {                     
            ReBindMain.LOGGER.error("Update check failed, remote file is absent.");                        
            return;
        } catch (IOException exception) {                                               
            exception.printStackTrace();                        
            return;
        }                                       
        JsonObject data;          
        try {           
            data = remoteData.get(ReBindMain.GAME_VERSION).getAsJsonObject();      
        } catch (NullPointerException exception) {              
            ReBindMain.LOGGER.error("Update check failed, data is undefined for " + ReBindMain.GAME_VERSION + " version.");           
            return;
        }        
        availableVersion = data.get("available").getAsString();
        ReBindMain.LOGGER.info("Update check ended. Current/available: " + ReBindMain.VERSION + "/" + availableVersion);
    }
}
