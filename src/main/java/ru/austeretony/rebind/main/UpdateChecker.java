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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.austeretony.rebind.config.ConfigLoader;

public class UpdateChecker implements Runnable {

	private static boolean notified;
	
	private static String availableVersion = ReBindMain.VERSION;
	
	private static List<String> changelog;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPlayerJoinedWorld(EntityJoinWorldEvent event) {
		
		if (event.getEntity() instanceof EntityPlayer) {
			
			if (!notified) {
				
				notified = true;
				
		        if (this.compareVersions(ReBindMain.VERSION, availableVersion)) {	
	            	
		        	ITextComponent 
		        	updateMessage1 = new TextComponentString("[ReBind] "),
		            updateMessage2 = new TextComponentTranslation("rebind.update.newVersion"),
		            updateMessage3 = new TextComponentString(" [" + ReBindMain.VERSION + "/" + availableVersion + "]"),
		        	pageMessage1 = new TextComponentTranslation("rebind.update.projectPage"),
		            pageMessage2 = new TextComponentString(": "),
		        	urlMessage = new TextComponentString("minecraft.curseforge.com");		        
		        	updateMessage1.getStyle().setColor(TextFormatting.AQUA);
		        	pageMessage1.getStyle().setColor(TextFormatting.AQUA);
		        	urlMessage.getStyle().setColor(TextFormatting.WHITE);		        	
		        	urlMessage.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, ReBindMain.PROJECT_URL));		        	
		        	event.getEntity().sendMessage(updateMessage1.appendSibling(updateMessage2).appendSibling(updateMessage3));
		        	event.getEntity().sendMessage(pageMessage1.appendSibling(pageMessage2).appendSibling(urlMessage));
		        	
		            if (ConfigLoader.shouldShowChangelog()) {

	            		ITextComponent changelogMessage = new TextComponentString("Changelog:");           		
	            		changelogMessage.getStyle().setColor(TextFormatting.AQUA);            		
	            		event.getEntity().sendMessage(changelogMessage);
		            		            		
	            		for (String line : changelog)            			            			
	            			event.getEntity().sendMessage(new TextComponentString(" + " + line));
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
