package ru.austeretony.rebind.event;

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
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import ru.austeretony.rebind.coremod.ReBindClassTransformer;
import ru.austeretony.rebind.main.ConfigLoader;
import ru.austeretony.rebind.main.ReBindMain;

public class ReBindEvents {

	@SubscribeEvent
	public void onPlayerJoinedWorld(EntityJoinWorldEvent event) {
		
		if (event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer) {
					
			if (ConfigLoader.isUpdateCheckerEnabled())				
				this.checkForUpdate();
		}
	}
	
	@SubscribeEvent
	public void onPlayerUpdate(LivingUpdateEvent event) {
		
		if (event.entityLiving.worldObj.isRemote && event.entityLiving instanceof EntityPlayer) {
						
			if (ConfigLoader.isAutoJumpEnabled()) {
				
				if (event.entityLiving.stepHeight != 1.0F)					
					event.entityLiving.stepHeight = 1.0F;
			}
		}
	}
	
	private void checkForUpdate() {
							
		try {
			
			URL versionsURL = new URL(ReBindMain.VERSIONS_URL);
			
			InputStream inputStream = null;
			
			try {
				
				inputStream = versionsURL.openStream();
			}
			
			catch (UnknownHostException exception) {
														
				ReBindClassTransformer.LOGGER.error("Update check failed, no internet connection.");
				
				return;
			}
			
            JsonObject remoteData = (JsonObject) new JsonParser().parse(new InputStreamReader(inputStream, "UTF-8"));  			
			
            inputStream.close();
            
            JsonObject data = remoteData.get(ReBindMain.GAME_VERSION).getAsJsonObject();                     	        
            
            String availableVersion = data.get("available").getAsString();
            
            if (this.compareVersions(ReBindMain.VERSION, availableVersion)) {	
            	            	
            	List<String> changelog = new ArrayList<String>();
            	
            	for (JsonElement element : data.get("changelog").getAsJsonArray())       		
            		changelog.add(element.getAsString());
            	
            	EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            	
            	IChatComponent 
            	updateMessage = new ChatComponentText("[ReBind] " + I18n.format("rebind.update.newVersion") + " [" + ReBindMain.VERSION + "/" + availableVersion + "]"),
            	pageMessage = new ChatComponentText(I18n.format("rebind.update.projectPage") + ": "),
            	urlMessage = new ChatComponentText(ReBindMain.PROJECT_URL);
            
            	updateMessage.getChatStyle().setColor(EnumChatFormatting.AQUA);
            	pageMessage.getChatStyle().setColor(EnumChatFormatting.AQUA);
            	urlMessage.getChatStyle().setColor(EnumChatFormatting.WHITE);
            	
            	urlMessage.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, urlMessage.getUnformattedText()));
            	
            	player.addChatMessage(updateMessage);
            	player.addChatMessage(pageMessage.appendSibling(urlMessage));
            	
            	if (ConfigLoader.shouldShowChangeolog()) {
            		
            		IChatComponent changelogMessage = new ChatComponentText("Changelog:");
            		
            		changelogMessage.getChatStyle().setColor(EnumChatFormatting.AQUA);
            		
	            	player.addChatMessage(changelogMessage);
	            		            		
            		for (String line : changelog)            			            			
    	            	player.addChatMessage(new ChatComponentText(" + " + line));
            	}
            }
		}
		
		catch (MalformedURLException exception) {
			
			exception.printStackTrace();
		}
		
		catch (FileNotFoundException exception) {
			
			ReBindClassTransformer.LOGGER.error("Update check failed, remote file is absent.");			
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
