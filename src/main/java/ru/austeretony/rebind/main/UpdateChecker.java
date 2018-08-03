package ru.austeretony.rebind.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import ru.austeretony.rebind.config.ConfigLoader;

public class UpdateChecker implements Runnable {

	private static boolean notified;
	
	private static String availableVersion = ReBindMain.VERSION;
	
	private static List<String> changelog;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPlayerJoinedWorld(EntityJoinWorldEvent event) {
		
		if (event.entity instanceof EntityPlayer) {
			
			if (!notified) {
				
				notified = true;
				
		        if (this.compareVersions(ReBindMain.VERSION, availableVersion)) {	
	            	
		        	IChatComponent 
		        	updateMessage1 = new ChatComponentText("[ReBind] "),
		            updateMessage2 = new ChatComponentTranslation("rebind.update.newVersion"),
		            updateMessage3 = new ChatComponentText(" [" + ReBindMain.VERSION + "/" + availableVersion + "]"),
		        	pageMessage1 = new ChatComponentTranslation("rebind.update.projectPage"),
		            pageMessage2 = new ChatComponentText(": "),
		        	urlMessage = new ChatComponentText("minecraft.curseforge.com");		        
		        	updateMessage1.getChatStyle().setColor(EnumChatFormatting.AQUA);
		        	pageMessage1.getChatStyle().setColor(EnumChatFormatting.AQUA);
		        	urlMessage.getChatStyle().setColor(EnumChatFormatting.WHITE);		        	
		        	urlMessage.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, ReBindMain.PROJECT_URL));		        	
		        	((EntityPlayer) event.entity).addChatMessage(updateMessage1.appendSibling(updateMessage2).appendSibling(updateMessage3));
		        	((EntityPlayer) event.entity).addChatMessage(pageMessage1.appendSibling(pageMessage2).appendSibling(urlMessage));
		        	
		            if (ConfigLoader.shouldShowChangelog()) {

		            	IChatComponent changelogMessage = new ChatComponentText("Changelog:");           		
	            		changelogMessage.getChatStyle().setColor(EnumChatFormatting.AQUA);            		
	            		((EntityPlayer) event.entity).addChatMessage(changelogMessage);
		            		            		
	            		for (String line : changelog)            			            			
	            			((EntityPlayer) event.entity).addChatMessage(new ChatComponentText(" + " + line));
		            }
		        }
			}
			
			else {
				
				MinecraftForge.EVENT_BUS.unregister(this);
			}
		}
	}

	@Override
	public void run() {

		URL versionsURL;
		
		try {
			
			versionsURL = new URL(ReBindMain.VERSIONS_URL);
		}
		
		catch (MalformedURLException exception) {
			
			exception.printStackTrace();
			
			return;
		}
		
		JsonObject remoteData;
					
		try (InputStream inputStream = versionsURL.openStream()) {
			
			remoteData = (JsonObject) new JsonParser().parse(new InputStreamReader(inputStream, "UTF-8")); 
		}
		
		catch (UnknownHostException exception) {
			
			ReBindMain.LOGGER.error("Update check failed, no internet connection.");
			
			return;
		}
		
		catch (FileNotFoundException exception) {
			
			ReBindMain.LOGGER.error("Update check failed, remote file is absent.");
			
			return;
		}
		
		catch (IOException exception) {
						
			exception.printStackTrace();
			
			return;
		}
				        
        JsonObject data;  
        
        try {
        	
        	data = remoteData.get(ReBindMain.GAME_VERSION).getAsJsonObject();      
        }
        
        catch (NullPointerException exception) {
        	
        	ReBindMain.LOGGER.error("Update check failed, data is undefined for " + ReBindMain.GAME_VERSION + " version.");
        	
        	return;
        }
        
        availableVersion = data.get("available").getAsString();
        
        if (ConfigLoader.shouldShowChangelog()) {
        	
        	changelog = new ArrayList<String>();
        	
        	for (JsonElement element : data.get("changelog").getAsJsonArray())       		
        		changelog.add(element.getAsString());
        }
	}
	
	private boolean compareVersions(String currentVersion, String availableVersion) {
								
		String[] 
		cVer = currentVersion.split("[.]"),
		aVer = availableVersion.split("[.]");
				
		int diff;
		
		for (int i = 0; i < cVer.length; i++) {
					
			try {
				
				diff = Integer.parseInt(aVer[i]) - Integer.parseInt(cVer[i]);
												
				if (diff > 0)
					return true;
				
				if (diff < 0)
					return false;
			}
			
			catch (NumberFormatException exception) {
				
				exception.printStackTrace();
			}
		}
		
		return false;
	}
}
