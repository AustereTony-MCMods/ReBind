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

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class UpdateChecker {

	@SubscribeEvent
	public void onPlayerJoinedWorld(EntityJoinWorldEvent event) {
		
		if (event.getWorld().isRemote && event.getEntity() instanceof EntityPlayer) {
					
			if (ConfigLoader.isUpdateCheckerEnabled())				
				this.checkForUpdates();
		}
	}
	
	@SubscribeEvent
	public void onPlayerUpdate(LivingUpdateEvent event) {
		
		if (event.getEntityLiving().worldObj.isRemote && event.getEntityLiving() instanceof EntityPlayer) {
						
			if (ConfigLoader.isAutoJumpEnabled()) {
				
				if (event.getEntityLiving().stepHeight != 1.0F)					
					event.getEntityLiving().stepHeight = 1.0F;
			}
		}
	}
	
	private void checkForUpdates() {
							
		try {
			
			URL versionsURL = new URL(ReBindMain.VERSIONS_URL);
			
			InputStream inputStream;
			
			try {
				
				inputStream = versionsURL.openStream();
			}
			
			catch (UnknownHostException exception) {
														
				ReBindMain.LOGGER.error("Update check failed, no internet connection.");
				
				return;
			}
			
            JsonObject remoteData = (JsonObject) new JsonParser().parse(new InputStreamReader(inputStream, "UTF-8"));  			
			
            inputStream.close();
            
            JsonObject data;  
            
            try {
            	
            	data = remoteData.get(ReBindMain.GAME_VERSION).getAsJsonObject();      
            }
            
            catch (NullPointerException exception) {
            	
            	ReBindMain.LOGGER.error("Update check failed, remote data is undefined for " + ReBindMain.GAME_VERSION + " version.");
            	
            	return;
            }
                        
            String availableVersion = data.get("available").getAsString();
            
            if (this.compareVersions(ReBindMain.VERSION, availableVersion)) {	
            	            	
            	List<String> changelog = new ArrayList<String>();
            	
            	for (JsonElement element : data.get("changelog").getAsJsonArray())       		
            		changelog.add(element.getAsString());
            	
            	EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            	
            	ITextComponent 
            	updateMessage = new TextComponentString("[ReBind] " + I18n.format("rebind.update.newVersion") + " [" + ReBindMain.VERSION + "/" + availableVersion + "]"),
            	pageMessage = new TextComponentString(I18n.format("rebind.update.projectPage") + ": "),
            	urlMessage = new TextComponentString(ReBindMain.PROJECT_URL);
            
            	updateMessage.getStyle().setColor(TextFormatting.AQUA);
            	pageMessage.getStyle().setColor(TextFormatting.AQUA);
            	urlMessage.getStyle().setColor(TextFormatting.WHITE);
            	
            	urlMessage.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, urlMessage.getUnformattedText()));
            	
            	player.addChatMessage(updateMessage);
            	player.addChatMessage(pageMessage.appendSibling(urlMessage));
            	
            	if (ConfigLoader.shouldShowChangeolog()) {
            		
            		ITextComponent changelogMessage = new TextComponentString("Changelog:");            		
            		changelogMessage.getStyle().setColor(TextFormatting.AQUA);            		
	            	player.addChatMessage(changelogMessage);
	            		            		
            		for (String line : changelog)            			            			
    	            	player.addChatMessage(new TextComponentString(" + " + line));
            	}
            }
		}
		
		catch (MalformedURLException exception) {
			
			exception.printStackTrace();
		}
		
		catch (FileNotFoundException exception) {
			
			ReBindMain.LOGGER.error("Update check failed, remote file is absent.");			
		}
		
		catch (IOException exception) {
			
			exception.printStackTrace();
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
