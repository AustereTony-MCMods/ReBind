package ru.austeretony.rebind.event;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
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
import ru.austeretony.rebind.main.ConfigLoader;
import ru.austeretony.rebind.main.ReBindMain;

public class ReBindEvents {

	@SubscribeEvent
	public void onPlayerJoinedWorld(EntityJoinWorldEvent event) {
		
		if (event.world.isRemote && event.entity instanceof EntityPlayer) {
						
			this.checkForUpdates();
		}
	}
	
	@SubscribeEvent
	public void onPlayerUpdate(LivingUpdateEvent event) {
		
		if (event.entityLiving.worldObj.isRemote && event.entityLiving instanceof EntityPlayer) {
						
			if (ConfigLoader.isAutoJumpEnabled()) {
				
				if (event.entityLiving.stepHeight != 1.0F) {
					
					event.entityLiving.stepHeight = 1.0F;
				}
			}
		}
	}
	
	private void checkForUpdates() {
		
		if (ConfigLoader.isUpdateCheckerEnabled()) {
			
			try {
				
				URL versionsURL = new URL(ReBindMain.VERSIONS_URL);
				
				InputStream inputStream = versionsURL.openStream();
						
	            JsonObject remoteData = (JsonObject) new JsonParser().parse(new InputStreamReader(inputStream, "UTF-8"));  			
				
	            inputStream.close();
	            
	            JsonObject data = remoteData.get("1.7.10").getAsJsonObject();
	            
	            String newVersion = data.get("available").getAsString();
	            	            
	            int 
	            availableVersion = Integer.valueOf(newVersion.replace(".", "")),
	            currentVersion = Integer.valueOf(ReBindMain.VERSION.replace(".", ""));
	            
	            if (currentVersion < availableVersion) {
	            	
	            	List<String> changelog = new ArrayList<String>();
	            	
	            	for (JsonElement element : data.get("changelog").getAsJsonArray()) {
	            		
	            		changelog.add(element.getAsString());
	            	}
	            	
	            	EntityPlayer player = Minecraft.getMinecraft().thePlayer;
	            	
	            	IChatComponent 
	            	updateMessage = new ChatComponentText("[ReBind] " + I18n.format("rebind.update.newVersion") + " [" + ReBindMain.VERSION + " / " + newVersion + "]"),
	            	pageMessage = new ChatComponentText(I18n.format("rebind.update.projectPage") + ": "),
	            	urlMessage = new ChatComponentText("https://www.curseforge.com/minecraft/mc-mods/rebind");
	            
	            	updateMessage.getChatStyle().setColor(EnumChatFormatting.AQUA);
	            	pageMessage.getChatStyle().setColor(EnumChatFormatting.AQUA);
	            	urlMessage.getChatStyle().setColor(EnumChatFormatting.WHITE);
	            	
	            	urlMessage.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, urlMessage.getUnformattedText()));
	            	
	            	player.addChatMessage(updateMessage);
	            	player.addChatMessage(pageMessage.appendSibling(urlMessage));
	            	
	            	if (ConfigLoader.shouldShowChangeolog()) {
	            		
	            		IChatComponent 
	            		changelogMessage = new ChatComponentText("Changelog:"),
	            		changelogLine;
	            		
	            		changelogMessage.getChatStyle().setColor(EnumChatFormatting.AQUA);
	            		
		            	player.addChatMessage(changelogMessage);
		            	
		            	player.isRiding();
	            		
	            		for (String line : changelog) {
	            			
	            			changelogLine = new ChatComponentText(" + " + line);
	            			
	    	            	player.addChatMessage(changelogLine);
	            		}
	            	}
	            }
			}
			
			catch (MalformedURLException exception) {
				
				exception.printStackTrace();
			}
			
			catch (IOException exception) {
				
				exception.printStackTrace();
			}
		}
	}
}
