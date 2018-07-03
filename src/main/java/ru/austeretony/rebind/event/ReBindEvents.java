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
import ru.austeretony.rebind.main.ConfigLoader;
import ru.austeretony.rebind.main.ReBindMain;

public class ReBindEvents {

	@SubscribeEvent
	public void onPlayerJoinedWorld(EntityJoinWorldEvent event) {
		
		if (event.getWorld().isRemote && event.getEntity() instanceof EntityPlayer) {
						
			this.checkForUpdates();
		}
	}
	
	@SubscribeEvent
	public void onPlayerUpdate(LivingUpdateEvent event) {
		
		if (event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof EntityPlayer) {
						
			if (ConfigLoader.isAutoJumpEnabled()) {
				
				if (event.getEntityLiving().stepHeight != 1.0F) {
					
					event.getEntityLiving().stepHeight = 1.0F;
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
	            
	            JsonObject data = remoteData.get("1.12.2").getAsJsonObject();
	            
	            String newVersion = data.get("available").getAsString();
	            	            
	            int 
	            availableVersion = Integer.valueOf(newVersion.replace(".", "")),
	            currentVersion = Integer.valueOf(ReBindMain.VERSION.replace(".", ""));
	            
	            if (currentVersion < availableVersion) {
	            	
	            	List<String> changelog = new ArrayList<String>();
	            	
	            	for (JsonElement element : data.get("changelog").getAsJsonArray()) {
	            		
	            		changelog.add(element.getAsString());
	            	}
	            	
	            	EntityPlayer player = Minecraft.getMinecraft().player;
	            	
	            	ITextComponent 
	            	updateMessage = new TextComponentString("[ReBind] " + I18n.format("rebind.update.newVersion") + " [" + ReBindMain.VERSION + " / " + newVersion + "]"),
	            	pageMessage = new TextComponentString(I18n.format("rebind.update.projectPage") + ": "),
	            	urlMessage = new TextComponentString("https://www.curseforge.com/minecraft/mc-mods/rebind");
	            
	            	updateMessage.getStyle().setColor(TextFormatting.AQUA);
	            	pageMessage.getStyle().setColor(TextFormatting.AQUA);
	            	urlMessage.getStyle().setColor(TextFormatting.WHITE);
	            	
	            	urlMessage.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, urlMessage.getUnformattedText()));
	            	
	            	player.sendMessage(updateMessage);
	            	player.sendMessage(pageMessage.appendSibling(urlMessage));
	            	
	            	if (ConfigLoader.shouldShowChangeolog()) {
	            		
	            		ITextComponent 
	            		changelogMessage = new TextComponentString("Changelog:"),
	            		changelogLine;
	            		
	            		changelogMessage.getStyle().setColor(TextFormatting.AQUA);
	            		
		            	player.sendMessage(changelogMessage);
		            	
		            	player.isRiding();
	            		
	            		for (String line : changelog) {
	            			
	            			changelogLine = new TextComponentString(" + " + line);
	            			
	    	            	player.sendMessage(changelogLine);
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
