package ru.austeretony.rebind.main;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = ReBindMain.MODID, name = ReBindMain.NAME, version = ReBindMain.VERSION)
public class ReBindMain {
	
    public static final String 
	MODID = "rebind",
    NAME = "ReBind",
    VERSION = "2.1.0",
    COREMOD_VERSION = "1.1.0";
    
    public static final ConfigLoader CONFIG_LOADER = new ConfigLoader();
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    			        
        if (event.getSide() == Side.CLIENT)	{
        	
        	this.CONFIG_LOADER.loadConfiguration();     
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    	
    	if (event.getSide() == Side.CLIENT)	{    
    		
    		GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
    		
    		this.clearVanillaKeyBindings(gameSettings);
    		this.updateKeyBindings(gameSettings);   		
    		this.setKeysConflictContext(gameSettings);
    		
    		KeyRegistry.registerInternalVanillaKeys();
    		
    		this.removeUnusedCategories(gameSettings);
    	}
    }
    
    @SideOnly(Side.CLIENT)
    private void clearVanillaKeyBindings(GameSettings gameSettings) {
    	    	
		try {
			
			Field[] fields = KeyBinding.class.getDeclaredFields();
			
			for (int i = 0; i < fields.length; i++) {
								
				if (fields[i].getName().equals("KEYBIND_ARRAY") || fields[i].getName().equals("field_74516_a")) {
				
					fields[i].setAccessible(true);
							
					List<KeyBinding> bindingsArray = (List<KeyBinding>) fields[i].get(null);
										
					//Mouse
												
					bindingsArray.remove(gameSettings.keyBindAttack);												
					bindingsArray.remove(gameSettings.keyBindUseItem);												
					bindingsArray.remove(gameSettings.keyBindPickBlock);
					
					//Keyboard
							
					bindingsArray.remove(gameSettings.keyBindForward);							
					bindingsArray.remove(gameSettings.keyBindLeft);							
					bindingsArray.remove(gameSettings.keyBindBack);							
					bindingsArray.remove(gameSettings.keyBindRight);							
					bindingsArray.remove(gameSettings.keyBindJump);							
					bindingsArray.remove(gameSettings.keyBindSneak);							
					bindingsArray.remove(gameSettings.keyBindSprint);							
					bindingsArray.remove(gameSettings.keyBindDrop);							
					bindingsArray.remove(gameSettings.keyBindSwapHands);							
					bindingsArray.remove(gameSettings.keyBindInventory);							
					bindingsArray.remove(gameSettings.keyBindChat);							
					bindingsArray.remove(gameSettings.keyBindPlayerList);							
					bindingsArray.remove(gameSettings.keyBindCommand);							
					bindingsArray.remove(gameSettings.keyBindScreenshot);						
					bindingsArray.remove(gameSettings.keyBindTogglePerspective);						
					bindingsArray.remove(gameSettings.keyBindSmoothCamera);							
					bindingsArray.remove(gameSettings.keyBindFullscreen);							
					bindingsArray.remove(gameSettings.keyBindSpectatorOutlines);							
					bindingsArray.remove(gameSettings.keyBindsHotbar[0]);									
					bindingsArray.remove(gameSettings.keyBindsHotbar[1]);								
					bindingsArray.remove(gameSettings.keyBindsHotbar[2]);											
					bindingsArray.remove(gameSettings.keyBindsHotbar[3]);										
					bindingsArray.remove(gameSettings.keyBindsHotbar[4]);										
					bindingsArray.remove(gameSettings.keyBindsHotbar[5]);									
					bindingsArray.remove(gameSettings.keyBindsHotbar[6]);										
					bindingsArray.remove(gameSettings.keyBindsHotbar[7]);											
					bindingsArray.remove(gameSettings.keyBindsHotbar[8]);
					
					break;
				}
			}
		} 
		
		catch (SecurityException exception) {
		
			exception.printStackTrace();
		} 
		
		catch (IllegalAccessException exception) {
		
			exception.printStackTrace();		
		} 
		
		catch (IllegalArgumentException exception) {
		
			exception.printStackTrace();		
		}
			
    	KeyBinding.getKeybinds().remove("key.categories.gameplay");
    	KeyBinding.getKeybinds().remove("key.categories.movement");
    	KeyBinding.getKeybinds().remove("key.categories.multiplayer");   	
    	KeyBinding.getKeybinds().remove("key.categories.misc");    	
    	KeyBinding.getKeybinds().remove("key.categories.inventory");
    	
		KeyBinding.resetKeyBindingArrayAndHash();
    }
    
    @SideOnly(Side.CLIENT)
    private void updateKeyBindings(GameSettings gameSettings) {
    	
		List<KeyBinding> bindingsList = new ArrayList<KeyBinding>(Arrays.asList(gameSettings.keyBindings));

		//Mouse
		
		bindingsList.remove(gameSettings.keyBindAttack);												
		bindingsList.remove(gameSettings.keyBindUseItem);												
		bindingsList.remove(gameSettings.keyBindPickBlock);
		
		//Keyboard
				
		bindingsList.remove(gameSettings.keyBindForward);							
		bindingsList.remove(gameSettings.keyBindLeft);							
		bindingsList.remove(gameSettings.keyBindBack);							
		bindingsList.remove(gameSettings.keyBindRight);							
		bindingsList.remove(gameSettings.keyBindJump);							
		bindingsList.remove(gameSettings.keyBindSneak);							
		bindingsList.remove(gameSettings.keyBindSprint);							
		bindingsList.remove(gameSettings.keyBindDrop);							
		bindingsList.remove(gameSettings.keyBindSwapHands);							
		bindingsList.remove(gameSettings.keyBindInventory);							
		bindingsList.remove(gameSettings.keyBindChat);							
		bindingsList.remove(gameSettings.keyBindPlayerList);							
		bindingsList.remove(gameSettings.keyBindCommand);							
		bindingsList.remove(gameSettings.keyBindScreenshot);						
		bindingsList.remove(gameSettings.keyBindTogglePerspective);						
		bindingsList.remove(gameSettings.keyBindSmoothCamera);							
		bindingsList.remove(gameSettings.keyBindFullscreen);							
		bindingsList.remove(gameSettings.keyBindSpectatorOutlines);								
		bindingsList.remove(gameSettings.keyBindsHotbar[0]);									
		bindingsList.remove(gameSettings.keyBindsHotbar[1]);								
		bindingsList.remove(gameSettings.keyBindsHotbar[2]);											
		bindingsList.remove(gameSettings.keyBindsHotbar[3]);										
		bindingsList.remove(gameSettings.keyBindsHotbar[4]);										
		bindingsList.remove(gameSettings.keyBindsHotbar[5]);									
		bindingsList.remove(gameSettings.keyBindsHotbar[6]);										
		bindingsList.remove(gameSettings.keyBindsHotbar[7]);											
		bindingsList.remove(gameSettings.keyBindsHotbar[8]);
		
		List<KeyBinding> updatedBindingsList = new ArrayList<KeyBinding>();
						
		//Mouse
				
		gameSettings.keyBindAttack = new KeyBinding("key.attack", CONFIG_LOADER.keyCodeAttack, getCat(CONFIG_LOADER.categoryAttack));
		
		if (this.CONFIG_LOADER.enableAttack)
		updatedBindingsList.add(gameSettings.keyBindAttack);
				
		gameSettings.keyBindUseItem = new KeyBinding("key.use", CONFIG_LOADER.keyCodeUseItem, getCat(CONFIG_LOADER.categoryUseItem));
		
		if (this.CONFIG_LOADER.enableUseItem)
		updatedBindingsList.add(gameSettings.keyBindUseItem);
				
		gameSettings.keyBindPickBlock = new KeyBinding("key.pickItem", CONFIG_LOADER.keyCodePickBlock, getCat(CONFIG_LOADER.categoryPickBlock));
		
		if (this.CONFIG_LOADER.enablePickBlock)
		updatedBindingsList.add(gameSettings.keyBindPickBlock);
		
		//Keyboard
				
		gameSettings.keyBindForward = new KeyBinding("key.forward", CONFIG_LOADER.keyCodeForward, getCat(CONFIG_LOADER.categoryForward));
		
		if (this.CONFIG_LOADER.enableForward)
		updatedBindingsList.add(gameSettings.keyBindForward);
				
		gameSettings.keyBindLeft = new KeyBinding("key.left", CONFIG_LOADER.keyCodeLeft, getCat(CONFIG_LOADER.categoryLeft));
		
		if (this.CONFIG_LOADER.enableLeft)
		updatedBindingsList.add(gameSettings.keyBindLeft);
				
		gameSettings.keyBindBack = new KeyBinding("key.back", CONFIG_LOADER.keyCodeBack, getCat(CONFIG_LOADER.categoryBack));
		
		if (this.CONFIG_LOADER.enableBack)
		updatedBindingsList.add(gameSettings.keyBindBack);
				
		gameSettings.keyBindRight = new KeyBinding("key.right", CONFIG_LOADER.keyCodeRight, getCat(CONFIG_LOADER.categoryRight));
		
		if (this.CONFIG_LOADER.enableRight)
		updatedBindingsList.add(gameSettings.keyBindRight);
				
		gameSettings.keyBindJump = new KeyBinding("key.jump", CONFIG_LOADER.keyCodeJump, getCat(CONFIG_LOADER.categoryJump));

		if (this.CONFIG_LOADER.enableJump)
		updatedBindingsList.add(gameSettings.keyBindJump);
				
		gameSettings.keyBindSneak = new KeyBinding("key.sneak", CONFIG_LOADER.keyCodeSneak, getCat(CONFIG_LOADER.categorySneak));

		if (this.CONFIG_LOADER.enableSneak)
		updatedBindingsList.add(gameSettings.keyBindSneak);
				
		gameSettings.keyBindSprint = new KeyBinding("key.sprint", CONFIG_LOADER.keyCodeSprint, getCat(CONFIG_LOADER.categorySprint));

		if (this.CONFIG_LOADER.enableSprint)
		updatedBindingsList.add(gameSettings.keyBindSprint);
				
		gameSettings.keyBindDrop = new KeyBinding("key.drop", CONFIG_LOADER.keyCodeDrop, getCat(CONFIG_LOADER.categoryDrop));

		if (this.CONFIG_LOADER.enableDrop)
		updatedBindingsList.add(gameSettings.keyBindDrop);
				
		gameSettings.keyBindSwapHands = new KeyBinding("key.swapHands", CONFIG_LOADER.keyCodeSwapHands, getCat(CONFIG_LOADER.categorySwapHands));

		if (this.CONFIG_LOADER.enableSwapHands)
		updatedBindingsList.add(gameSettings.keyBindSwapHands);
				
		gameSettings.keyBindInventory = new KeyBinding("key.inventory", CONFIG_LOADER.keyCodeInventory, getCat(CONFIG_LOADER.categoryInventory));

		if (this.CONFIG_LOADER.enableInventory)
		updatedBindingsList.add(gameSettings.keyBindInventory);
				
		gameSettings.keyBindChat = new KeyBinding("key.chat", CONFIG_LOADER.keyCodeChat, getCat(CONFIG_LOADER.categoryChat));

		if (this.CONFIG_LOADER.enableChat)
		updatedBindingsList.add(gameSettings.keyBindChat);
				
		gameSettings.keyBindPlayerList = new KeyBinding("key.playerlist", CONFIG_LOADER.keyCodePlayerList, getCat(CONFIG_LOADER.categoryPlayerList));

		if (this.CONFIG_LOADER.enablePlayerList)
		updatedBindingsList.add(gameSettings.keyBindPlayerList);
				
		gameSettings.keyBindCommand = new KeyBinding("key.command", CONFIG_LOADER.keyCodeCommand, getCat(CONFIG_LOADER.categoryCommand));

		if (this.CONFIG_LOADER.enableCommand)
		updatedBindingsList.add(gameSettings.keyBindCommand);
				
		gameSettings.keyBindScreenshot = new KeyBinding("key.screenshot", CONFIG_LOADER.keyCodeScreenshot, getCat(CONFIG_LOADER.categoryScreenshot));

		if (this.CONFIG_LOADER.enableScreenshot)
		updatedBindingsList.add(gameSettings.keyBindScreenshot);
				
		gameSettings.keyBindTogglePerspective = new KeyBinding("key.togglePerspective", CONFIG_LOADER.keyCodeTogglePerspective, getCat(CONFIG_LOADER.categoryTogglePerspective));

		if (this.CONFIG_LOADER.enableTogglePerspective)
		updatedBindingsList.add(gameSettings.keyBindTogglePerspective);
				
		gameSettings.keyBindSmoothCamera = new KeyBinding("key.smoothCamera", CONFIG_LOADER.keyCodeSmoothCamera, getCat(CONFIG_LOADER.categorySmoothCamera));

		if (this.CONFIG_LOADER.enableSmoothCamera)
		updatedBindingsList.add(gameSettings.keyBindSmoothCamera);
				
		gameSettings.keyBindFullscreen = new KeyBinding("key.fullscreen", CONFIG_LOADER.keyCodeFullscreen, getCat(CONFIG_LOADER.categoryFullscreen));

		if (this.CONFIG_LOADER.enableFullscreen)
		updatedBindingsList.add(gameSettings.keyBindFullscreen);
				
		gameSettings.keyBindSpectatorOutlines = new KeyBinding("key.spectatorOutlines", CONFIG_LOADER.keyCodeSpectatorOutlines, getCat(CONFIG_LOADER.categorySpectatorOutlines));

		if (this.CONFIG_LOADER.enableSpectatorOutlines)
		updatedBindingsList.add(gameSettings.keyBindSpectatorOutlines);
						
		gameSettings.keyBindsHotbar[0] = new KeyBinding("key.hotbar.1", CONFIG_LOADER.keyCodeHotbar1, getCat(CONFIG_LOADER.categoryHotbar1));
				
		if (this.CONFIG_LOADER.enableHotbar1)			
		updatedBindingsList.add(gameSettings.keyBindsHotbar[0]);
				
		gameSettings.keyBindsHotbar[1] = new KeyBinding("key.hotbar.2", CONFIG_LOADER.keyCodeHotbar2, getCat(CONFIG_LOADER.categoryHotbar2));
		
		if (this.CONFIG_LOADER.enableHotbar2)			
		updatedBindingsList.add(gameSettings.keyBindsHotbar[1]);
		
		gameSettings.keyBindsHotbar[2] = new KeyBinding("key.hotbar.3", CONFIG_LOADER.keyCodeHotbar3, getCat(CONFIG_LOADER.categoryHotbar3));
		
		if (this.CONFIG_LOADER.enableHotbar3)			
		updatedBindingsList.add(gameSettings.keyBindsHotbar[2]);
					
		gameSettings.keyBindsHotbar[3] = new KeyBinding("key.hotbar.4", CONFIG_LOADER.keyCodeHotbar4, getCat(CONFIG_LOADER.categoryHotbar4));
		
		if (this.CONFIG_LOADER.enableHotbar4)			
		updatedBindingsList.add(gameSettings.keyBindsHotbar[3]);
					
		gameSettings.keyBindsHotbar[4] = new KeyBinding("key.hotbar.5", CONFIG_LOADER.keyCodeHotbar5, getCat(CONFIG_LOADER.categoryHotbar5));
		
		if (this.CONFIG_LOADER.enableHotbar5)			
		updatedBindingsList.add(gameSettings.keyBindsHotbar[4]);
					
		gameSettings.keyBindsHotbar[5] = new KeyBinding("key.hotbar.6", CONFIG_LOADER.keyCodeHotbar6, getCat(CONFIG_LOADER.categoryHotbar6));
		
		if (this.CONFIG_LOADER.enableHotbar6)			
		updatedBindingsList.add(gameSettings.keyBindsHotbar[5]);
					
		gameSettings.keyBindsHotbar[6] = new KeyBinding("key.hotbar.7", CONFIG_LOADER.keyCodeHotbar7, getCat(CONFIG_LOADER.categoryHotbar7));
		
		if (this.CONFIG_LOADER.enableHotbar7)			
		updatedBindingsList.add(gameSettings.keyBindsHotbar[6]);
					
		gameSettings.keyBindsHotbar[7] = new KeyBinding("key.hotbar.8", CONFIG_LOADER.keyCodeHotbar8, getCat(CONFIG_LOADER.categoryHotbar8));
		
		if (this.CONFIG_LOADER.enableHotbar8)			
		updatedBindingsList.add(gameSettings.keyBindsHotbar[7]);
					
		gameSettings.keyBindsHotbar[8] = new KeyBinding("key.hotbar.9", CONFIG_LOADER.keyCodeHotbar9, getCat(CONFIG_LOADER.categoryHotbar9));
		
		if (this.CONFIG_LOADER.enableHotbar9)			
		updatedBindingsList.add(gameSettings.keyBindsHotbar[8]);
		
		updatedBindingsList.addAll(bindingsList);
				
		gameSettings.keyBindings = updatedBindingsList.toArray(new KeyBinding[updatedBindingsList.size()]);
	}
    
    @SideOnly(Side.CLIENT)
    private void setKeysConflictContext(GameSettings gameSettings) {
    			
        gameSettings.keyBindAttack.setKeyConflictContext(KeyConflictContext.IN_GAME);
        
		gameSettings.keyBindForward.setKeyConflictContext(KeyConflictContext.IN_GAME);
		gameSettings.keyBindLeft.setKeyConflictContext(KeyConflictContext.IN_GAME);
        gameSettings.keyBindBack.setKeyConflictContext(KeyConflictContext.IN_GAME);
        gameSettings.keyBindRight.setKeyConflictContext(KeyConflictContext.IN_GAME);
        gameSettings.keyBindJump.setKeyConflictContext(KeyConflictContext.IN_GAME);
        gameSettings.keyBindSneak.setKeyConflictContext(KeyConflictContext.IN_GAME);
        gameSettings.keyBindSprint.setKeyConflictContext(KeyConflictContext.IN_GAME);
        gameSettings.keyBindChat.setKeyConflictContext(KeyConflictContext.IN_GAME);
        gameSettings.keyBindPlayerList.setKeyConflictContext(KeyConflictContext.IN_GAME);
        gameSettings.keyBindCommand.setKeyConflictContext(KeyConflictContext.IN_GAME);
        gameSettings.keyBindTogglePerspective.setKeyConflictContext(KeyConflictContext.IN_GAME);
        gameSettings.keyBindSmoothCamera.setKeyConflictContext(KeyConflictContext.IN_GAME);
        gameSettings.keyBindSwapHands.setKeyConflictContext(KeyConflictContext.IN_GAME);
    }
    
    @SideOnly(Side.CLIENT)
    private void removeUnusedCategories(GameSettings gameSettings) {
    	
    	Set<String> occurrences = new HashSet<String>();
    	    	    	
		for (KeyBinding keyBinding : gameSettings.keyBindings) {
							
			occurrences.add(keyBinding.getKeyCategory());
		}
		
		KeyBinding.getKeybinds().retainAll(occurrences);
    }
    
    public static String getCat(String categoryName) {
    	
    	return "key.categories." + categoryName;
    }
}


