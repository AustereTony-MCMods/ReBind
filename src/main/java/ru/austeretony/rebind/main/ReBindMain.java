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
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = ReBindMain.MODID, name = ReBindMain.NAME, version = ReBindMain.VERSION)
public class ReBindMain {
	
    public static final String 
	MODID = "rebind",
    NAME = "ReBind",
    VERSION = "2.2.0",
    COREMOD_VERSION = "1.2.0";
    
    public static final ConfigLoader CONFIG_LOADER = new ConfigLoader();
    
    @SideOnly(Side.CLIENT)
    public KeyBinding keyBindQuit, keyBindHideHUD, keyBindDebugScreen, keyBindSwitchShader;
    
    @Instance(MODID)
    public static ReBindMain instance;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    			        
        if (event.getSide() == Side.CLIENT)	{
        	
        	CONFIG_LOADER.loadConfiguration();     
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    	
    	if (event.getSide() == Side.CLIENT)	{    
    		
    		GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
    		
    		this.clearVanillaKeyBindings(gameSettings);
    		this.updateKeyBindings(gameSettings);   		
    		this.setKeysConflictContext(gameSettings);
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
				
		gameSettings.keyBindAttack = new KeyBinding(getName(CONFIG_LOADER.propertyAttack.getName()), CONFIG_LOADER.propertyAttack.getKeyCode(), getCategory(CONFIG_LOADER.propertyAttack.getCategory()));
		
		if (CONFIG_LOADER.propertyAttack.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindAttack);
				
		gameSettings.keyBindUseItem = new KeyBinding(getName(CONFIG_LOADER.propertyUseItem.getName()), CONFIG_LOADER.propertyUseItem.getKeyCode(), getCategory(CONFIG_LOADER.propertyUseItem.getCategory()));
		
		if (CONFIG_LOADER.propertyUseItem.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindUseItem);
				
		gameSettings.keyBindPickBlock = new KeyBinding(getName(CONFIG_LOADER.propertyPickBlock.getName()), CONFIG_LOADER.propertyPickBlock.getKeyCode(), getCategory(CONFIG_LOADER.propertyPickBlock.getCategory()));
		
		if (CONFIG_LOADER.propertyPickBlock.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindPickBlock);
		
		//Keyboard
				
		gameSettings.keyBindForward = new KeyBinding(getName(CONFIG_LOADER.propertyForward.getName()), CONFIG_LOADER.propertyForward.getKeyCode(), getCategory(CONFIG_LOADER.propertyForward.getCategory()));
		
		if (CONFIG_LOADER.propertyForward.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindForward);
				
		gameSettings.keyBindLeft = new KeyBinding(getName(CONFIG_LOADER.propertyLeft.getName()), CONFIG_LOADER.propertyLeft.getKeyCode(), getCategory(CONFIG_LOADER.propertyLeft.getCategory()));
		
		if (CONFIG_LOADER.propertyLeft.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindLeft);
				
		gameSettings.keyBindBack = new KeyBinding(getName(CONFIG_LOADER.propertyBack.getName()), CONFIG_LOADER.propertyBack.getKeyCode(), getCategory(CONFIG_LOADER.propertyBack.getCategory()));
		
		if (CONFIG_LOADER.propertyBack.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindBack);
				
		gameSettings.keyBindRight = new KeyBinding(getName(CONFIG_LOADER.propertyRight.getName()), CONFIG_LOADER.propertyRight.getKeyCode(), getCategory(CONFIG_LOADER.propertyRight.getCategory()));
		
		if (CONFIG_LOADER.propertyRight.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindRight);
				
		gameSettings.keyBindJump = new KeyBinding(getName(CONFIG_LOADER.propertyJump.getName()), CONFIG_LOADER.propertyJump.getKeyCode(), getCategory(CONFIG_LOADER.propertyJump.getCategory()));

		if (CONFIG_LOADER.propertyJump.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindJump);
				
		gameSettings.keyBindSneak = new KeyBinding(getName(CONFIG_LOADER.propertySneak.getName()), CONFIG_LOADER.propertySneak.getKeyCode(), getCategory(CONFIG_LOADER.propertySneak.getCategory()));

		if (CONFIG_LOADER.propertySneak.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindSneak);
				
		gameSettings.keyBindSprint = new KeyBinding(getName(CONFIG_LOADER.propertySprint.getName()), CONFIG_LOADER.propertySprint.getKeyCode(), getCategory(CONFIG_LOADER.propertySprint.getCategory()));

		if (CONFIG_LOADER.propertySprint.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindSprint);
				
		gameSettings.keyBindDrop = new KeyBinding(getName(CONFIG_LOADER.propertyDrop.getName()), CONFIG_LOADER.propertyDrop.getKeyCode(), getCategory(CONFIG_LOADER.propertyDrop.getCategory()));

		if (CONFIG_LOADER.propertyDrop.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindDrop);
				
		gameSettings.keyBindSwapHands = new KeyBinding(getName(CONFIG_LOADER.propertySwapHands.getName()), CONFIG_LOADER.propertySwapHands.getKeyCode(), getCategory(CONFIG_LOADER.propertySwapHands.getCategory()));

		if (CONFIG_LOADER.propertySwapHands.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindSwapHands);
				
		gameSettings.keyBindInventory = new KeyBinding(getName(CONFIG_LOADER.propertyInventory.getName()), CONFIG_LOADER.propertyInventory.getKeyCode(), getCategory(CONFIG_LOADER.propertyInventory.getCategory()));

		if (CONFIG_LOADER.propertyInventory.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindInventory);
				
		gameSettings.keyBindChat = new KeyBinding(getName(CONFIG_LOADER.propertyChat.getName()), CONFIG_LOADER.propertyChat.getKeyCode(), getCategory(CONFIG_LOADER.propertyChat.getCategory()));

		if (CONFIG_LOADER.propertyChat.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindChat);
				
		gameSettings.keyBindPlayerList = new KeyBinding(getName(CONFIG_LOADER.propertyPlayerList.getName()), CONFIG_LOADER.propertyPlayerList.getKeyCode(), getCategory(CONFIG_LOADER.propertyPlayerList.getCategory()));

		if (CONFIG_LOADER.propertyPlayerList.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindPlayerList);
				
		gameSettings.keyBindCommand = new KeyBinding(getName(CONFIG_LOADER.propertyCommand.getName()), CONFIG_LOADER.propertyCommand.getKeyCode(), getCategory(CONFIG_LOADER.propertyCommand.getCategory()));

		if (CONFIG_LOADER.propertyCommand.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindCommand);
				
		gameSettings.keyBindScreenshot = new KeyBinding(getName(CONFIG_LOADER.propertyScreenshot.getName()), CONFIG_LOADER.propertyScreenshot.getKeyCode(), getCategory(CONFIG_LOADER.propertyScreenshot.getCategory()));

		if (CONFIG_LOADER.propertyScreenshot.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindScreenshot);
				
		gameSettings.keyBindTogglePerspective = new KeyBinding(getName(CONFIG_LOADER.propertyTogglePerspective.getName()), CONFIG_LOADER.propertyTogglePerspective.getKeyCode(), getCategory(CONFIG_LOADER.propertyTogglePerspective.getCategory()));

		if (CONFIG_LOADER.propertyTogglePerspective.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindTogglePerspective);
				
		gameSettings.keyBindSmoothCamera = new KeyBinding(getName(CONFIG_LOADER.propertySmoothCamera.getName()), CONFIG_LOADER.propertySmoothCamera.getKeyCode(), getCategory(CONFIG_LOADER.propertySmoothCamera.getCategory()));

		if (CONFIG_LOADER.propertySmoothCamera.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindSmoothCamera);
				
		gameSettings.keyBindFullscreen = new KeyBinding(getName(CONFIG_LOADER.propertyFullscreen.getName()), CONFIG_LOADER.propertyFullscreen.getKeyCode(), getCategory(CONFIG_LOADER.propertyFullscreen.getCategory()));

		if (CONFIG_LOADER.propertyFullscreen.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindFullscreen);
				
		gameSettings.keyBindSpectatorOutlines = new KeyBinding(getName(CONFIG_LOADER.propertySpectatorOutlines.getName()), CONFIG_LOADER.propertySpectatorOutlines.getKeyCode(), getCategory(CONFIG_LOADER.propertySpectatorOutlines.getCategory()));

		if (CONFIG_LOADER.propertySpectatorOutlines.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindSpectatorOutlines);
						
		gameSettings.keyBindsHotbar[0] = new KeyBinding(getName(CONFIG_LOADER.propertyHotbar1.getName()), CONFIG_LOADER.propertyHotbar1.getKeyCode(), getCategory(CONFIG_LOADER.propertyHotbar1.getCategory()));
				
		if (CONFIG_LOADER.propertyHotbar1.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindsHotbar[0]);
				
		gameSettings.keyBindsHotbar[1] = new KeyBinding(getName(CONFIG_LOADER.propertyHotbar2.getName()), CONFIG_LOADER.propertyHotbar2.getKeyCode(), getCategory(CONFIG_LOADER.propertyHotbar2.getCategory()));
		
		if (CONFIG_LOADER.propertyHotbar2.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindsHotbar[1]);
		
		gameSettings.keyBindsHotbar[2] = new KeyBinding(getName(CONFIG_LOADER.propertyHotbar3.getName()), CONFIG_LOADER.propertyHotbar3.getKeyCode(), getCategory(CONFIG_LOADER.propertyHotbar3.getCategory()));
		
		if (CONFIG_LOADER.propertyHotbar3.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindsHotbar[2]);
					
		gameSettings.keyBindsHotbar[3] = new KeyBinding(getName(CONFIG_LOADER.propertyHotbar4.getName()), CONFIG_LOADER.propertyHotbar4.getKeyCode(), getCategory(CONFIG_LOADER.propertyHotbar4.getCategory()));
		
		if (CONFIG_LOADER.propertyHotbar4.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindsHotbar[3]);
					
		gameSettings.keyBindsHotbar[4] = new KeyBinding(getName(CONFIG_LOADER.propertyHotbar5.getName()), CONFIG_LOADER.propertyHotbar5.getKeyCode(), getCategory(CONFIG_LOADER.propertyHotbar5.getCategory()));
		
		if (CONFIG_LOADER.propertyHotbar5.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindsHotbar[4]);
					
		gameSettings.keyBindsHotbar[5] = new KeyBinding(getName(CONFIG_LOADER.propertyHotbar6.getName()), CONFIG_LOADER.propertyHotbar6.getKeyCode(), getCategory(CONFIG_LOADER.propertyHotbar6.getCategory()));
		
		if (CONFIG_LOADER.propertyHotbar6.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindsHotbar[5]);
					
		gameSettings.keyBindsHotbar[6] = new KeyBinding(getName(CONFIG_LOADER.propertyHotbar7.getName()), CONFIG_LOADER.propertyHotbar7.getKeyCode(), getCategory(CONFIG_LOADER.propertyHotbar7.getCategory()));
		
		if (CONFIG_LOADER.propertyHotbar7.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindsHotbar[6]);
					
		gameSettings.keyBindsHotbar[7] = new KeyBinding(getName(CONFIG_LOADER.propertyHotbar8.getName()), CONFIG_LOADER.propertyHotbar8.getKeyCode(), getCategory(CONFIG_LOADER.propertyHotbar8.getCategory()));
		
		if (CONFIG_LOADER.propertyHotbar8.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindsHotbar[7]);
					
		gameSettings.keyBindsHotbar[8] = new KeyBinding(getName(CONFIG_LOADER.propertyHotbar9.getName()), CONFIG_LOADER.propertyHotbar9.getKeyCode(), getCategory(CONFIG_LOADER.propertyHotbar9.getCategory()));
		
		if (CONFIG_LOADER.propertyHotbar9.isEnabled())
		updatedBindingsList.add(gameSettings.keyBindsHotbar[8]);
		
		this.keyBindQuit = new KeyBinding(getName(CONFIG_LOADER.propertyQuit.getName()), CONFIG_LOADER.propertyQuit.getKeyCode(), getCategory(CONFIG_LOADER.propertyQuit.getCategory()));
		
		if (CONFIG_LOADER.propertyQuit.isEnabled())
		updatedBindingsList.add(this.keyBindQuit);
					
		this.keyBindHideHUD = new KeyBinding(getName(CONFIG_LOADER.propertyHideGUI.getName()), CONFIG_LOADER.propertyHideGUI.getKeyCode(), getCategory(CONFIG_LOADER.propertyHideGUI.getCategory()));
		
		if (CONFIG_LOADER.propertyHideGUI.isEnabled())
		updatedBindingsList.add(this.keyBindHideHUD);
					
		this.keyBindDebugScreen = new KeyBinding(getName(CONFIG_LOADER.propertyDebugMenu.getName()), CONFIG_LOADER.propertyDebugMenu.getKeyCode(), getCategory(CONFIG_LOADER.propertyDebugMenu.getCategory()));
		
		if (CONFIG_LOADER.propertyDebugMenu.isEnabled())
		updatedBindingsList.add(this.keyBindDebugScreen);
					
		this.keyBindSwitchShader = new KeyBinding(getName(CONFIG_LOADER.propertySwitchShader.getName()), CONFIG_LOADER.propertySwitchShader.getKeyCode(), getCategory(CONFIG_LOADER.propertySwitchShader.getCategory()));
		
		if (CONFIG_LOADER.propertySwitchShader.isEnabled())
		updatedBindingsList.add(this.keyBindSwitchShader);
		
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
    
    private String getName(String name) {
    	
    	return "key." + name;
    }
    
    private String getCategory(String category) {
    	
    	return "key.categories." + category;
    }
}
