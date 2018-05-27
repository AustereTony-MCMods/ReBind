package ru.austeretony.rebind.main;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

@Mod(modid = ReBindMain.MODID, name = ReBindMain.NAME, version = ReBindMain.VERSION)
public class ReBindMain {
	
    public static final String 
	MODID = "rebind",
    NAME = "ReBind",
    VERSION = "2.0.0",
    COREMOD_VERSION = "1.0.0", 
    	    
    CATEGORY_GAMEPLAY = "key.categories.gameplay",
    CATEGORY_MOVEMENT = "key.categories.movement",
    CATEGORY_INVENTORY = "key.categories.inventory",
    CATEGORY_MULTIPLAYER = "key.categories.multiplayer",
    CATEGORY_MISC = "key.categories.misc",
    CATEGORY_STREAM = "key.categories.stream";
    
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
    		
    		this.clearKeyBindings();

    		this.modifyKeyBindings();
    		
    		KeyRegistry.registerInternalKeys();
    		
    		this.removeUnusedCategories();
    	}
    }
    
    @SideOnly(Side.CLIENT)
    private void clearKeyBindings() {
    	
		try {
			
			Field[] fields = KeyBinding.class.getDeclaredFields();
			
			for (int i = 0; i < fields.length; i++) {
								
				if (fields[i].getName().equals("keybindArray") || fields[i].getName().equals("field_74516_a")) {
				
					fields[i].setAccessible(true);
							
					List list = (List) fields[i].get(null);
					
					list.clear();
										
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
		
		KeyBinding.getKeybinds().clear();
		
		KeyBinding.resetKeyBindingArrayAndHash();
    }
    
    @SideOnly(Side.CLIENT)
	private void modifyKeyBindings() {
		
		List<KeyBinding> modifiedKeys = new ArrayList<KeyBinding>();
		
		GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
				
		//Mouse
				
		gameSettings.keyBindAttack = new KeyBinding("key.attack", this.CONFIG_LOADER.keyCodeAttack, this.CATEGORY_GAMEPLAY);
		
		if (this.CONFIG_LOADER.enableAttack)
		modifiedKeys.add(gameSettings.keyBindAttack);
				
		gameSettings.keyBindUseItem = new KeyBinding("key.use", this.CONFIG_LOADER.keyCodeUseItem, this.CATEGORY_GAMEPLAY);
		
		if (this.CONFIG_LOADER.enableUseItem)
		modifiedKeys.add(gameSettings.keyBindUseItem);
				
		gameSettings.keyBindPickBlock = new KeyBinding("key.pickItem", this.CONFIG_LOADER.keyCodePickBlock, this.CATEGORY_GAMEPLAY);
		
		if (this.CONFIG_LOADER.enablePickBlock)
		modifiedKeys.add(gameSettings.keyBindPickBlock);
		
		//Keyboard
				
		gameSettings.keyBindForward = new KeyBinding("key.forward", this.CONFIG_LOADER.keyCodeForward, this.CATEGORY_MOVEMENT);
		
		if (this.CONFIG_LOADER.enableForward)
		modifiedKeys.add(gameSettings.keyBindForward);
				
		gameSettings.keyBindLeft = new KeyBinding("key.left", this.CONFIG_LOADER.keyCodeLeft, this.CATEGORY_MOVEMENT);
		
		if (this.CONFIG_LOADER.enableLeft)
		modifiedKeys.add(gameSettings.keyBindLeft);
				
		gameSettings.keyBindBack = new KeyBinding("key.back", this.CONFIG_LOADER.keyCodeBack, this.CATEGORY_MOVEMENT);
		
		if (this.CONFIG_LOADER.enableBack)
		modifiedKeys.add(gameSettings.keyBindBack);
				
		gameSettings.keyBindRight = new KeyBinding("key.right", this.CONFIG_LOADER.keyCodeRight, this.CATEGORY_MOVEMENT);
		
		if (this.CONFIG_LOADER.enableRight)
		modifiedKeys.add(gameSettings.keyBindRight);
				
		gameSettings.keyBindJump = new KeyBinding("key.jump", this.CONFIG_LOADER.keyCodeJump, this.CATEGORY_MOVEMENT);

		if (this.CONFIG_LOADER.enableJump)
		modifiedKeys.add(gameSettings.keyBindJump);
				
		gameSettings.keyBindSneak = new KeyBinding("key.sneak", this.CONFIG_LOADER.keyCodeSneak, this.CATEGORY_MOVEMENT);

		if (this.CONFIG_LOADER.enableSneak)
		modifiedKeys.add(gameSettings.keyBindSneak);
				
		gameSettings.keyBindSprint = new KeyBinding("key.sprint", this.CONFIG_LOADER.keyCodeSprint, this.CATEGORY_MOVEMENT);

		if (this.CONFIG_LOADER.enableSprint)
		modifiedKeys.add(gameSettings.keyBindSprint);
				
		gameSettings.keyBindDrop = new KeyBinding("key.drop", this.CONFIG_LOADER.keyCodeDrop, this.CATEGORY_INVENTORY);

		if (this.CONFIG_LOADER.enableDrop)
		modifiedKeys.add(gameSettings.keyBindDrop);
				
		gameSettings.keyBindInventory = new KeyBinding("key.inventory", this.CONFIG_LOADER.keyCodeInventory, this.CATEGORY_INVENTORY);

		if (this.CONFIG_LOADER.enableInventory)
		modifiedKeys.add(gameSettings.keyBindInventory);
				
		gameSettings.keyBindChat = new KeyBinding("key.chat", this.CONFIG_LOADER.keyCodeChat, this.CATEGORY_MULTIPLAYER);

		if (this.CONFIG_LOADER.enableChat)
		modifiedKeys.add(gameSettings.keyBindChat);
				
		gameSettings.keyBindPlayerList = new KeyBinding("key.playerlist", this.CONFIG_LOADER.keyCodePlayerList, this.CATEGORY_MULTIPLAYER);

		if (this.CONFIG_LOADER.enablePlayerList)
		modifiedKeys.add(gameSettings.keyBindPlayerList);
				
		gameSettings.keyBindCommand = new KeyBinding("key.command", this.CONFIG_LOADER.keyCodeCommand, this.CATEGORY_MULTIPLAYER);

		if (this.CONFIG_LOADER.enableCommand)
		modifiedKeys.add(gameSettings.keyBindCommand);
				
		gameSettings.keyBindScreenshot = new KeyBinding("key.screenshot", this.CONFIG_LOADER.keyCodeScreenshot, this.CATEGORY_MISC);

		if (this.CONFIG_LOADER.enableScreenshot)
		modifiedKeys.add(gameSettings.keyBindScreenshot);
				
		gameSettings.keyBindTogglePerspective = new KeyBinding("key.togglePerspective", this.CONFIG_LOADER.keyCodeTogglePerspective, this.CATEGORY_MISC);

		if (this.CONFIG_LOADER.enableTogglePerspective)
		modifiedKeys.add(gameSettings.keyBindTogglePerspective);
				
		gameSettings.keyBindSmoothCamera = new KeyBinding("key.smoothCamera", this.CONFIG_LOADER.keyCodeSmoothCamera, this.CATEGORY_MISC);

		if (this.CONFIG_LOADER.enableSmoothCamera)
		modifiedKeys.add(gameSettings.keyBindSmoothCamera);
				
		gameSettings.field_152395_am = new KeyBinding("key.fullscreen", this.CONFIG_LOADER.keyCodeFullscreen, this.CATEGORY_MISC);

		if (this.CONFIG_LOADER.enableFullscreen)
		modifiedKeys.add(gameSettings.field_152395_am);
		
		gameSettings.field_152396_an = new KeyBinding("key.streamStartStop", this.CONFIG_LOADER.keyCodeStreamStartStop, this.CATEGORY_STREAM);

		if (this.CONFIG_LOADER.enableStreamStartStop)
		modifiedKeys.add(gameSettings.field_152396_an);
		
		gameSettings.field_152397_ao = new KeyBinding("key.streamPauseUnpause", this.CONFIG_LOADER.keyCodeStreamPauseUnpause, this.CATEGORY_STREAM);

		if (this.CONFIG_LOADER.enableStreamPauseUnpause)
		modifiedKeys.add(gameSettings.field_152397_ao);
		
		gameSettings.field_152398_ap = new KeyBinding("key.streamCommercial", this.CONFIG_LOADER.keyCodeStreamCommercial, this.CATEGORY_STREAM);

		if (this.CONFIG_LOADER.enableStreamCommercial)
		modifiedKeys.add(gameSettings.field_152398_ap);
		
		gameSettings.field_152399_aq = new KeyBinding("key.streamToggleMic", this.CONFIG_LOADER.keyCodeStreamToggleMic, this.CATEGORY_STREAM);

		if (this.CONFIG_LOADER.enableStreamToggleMic)
		modifiedKeys.add(gameSettings.field_152399_aq);
						
		gameSettings.keyBindsHotbar[0] = new KeyBinding("key.hotbar.1", this.CONFIG_LOADER.keyCodeHotbar1, this.CATEGORY_INVENTORY);
				
		if (this.CONFIG_LOADER.enableHotbar1)			
		modifiedKeys.add(gameSettings.keyBindsHotbar[0]);
				
		gameSettings.keyBindsHotbar[1] = new KeyBinding("key.hotbar.2", this.CONFIG_LOADER.keyCodeHotbar2, this.CATEGORY_INVENTORY);
		
		if (this.CONFIG_LOADER.enableHotbar2)			
		modifiedKeys.add(gameSettings.keyBindsHotbar[1]);
		
		gameSettings.keyBindsHotbar[2] = new KeyBinding("key.hotbar.3", this.CONFIG_LOADER.keyCodeHotbar3, this.CATEGORY_INVENTORY);
		
		if (this.CONFIG_LOADER.enableHotbar3)			
		modifiedKeys.add(gameSettings.keyBindsHotbar[2]);
					
		gameSettings.keyBindsHotbar[3] = new KeyBinding("key.hotbar.4", this.CONFIG_LOADER.keyCodeHotbar4, this.CATEGORY_INVENTORY);
		
		if (this.CONFIG_LOADER.enableHotbar4)			
		modifiedKeys.add(gameSettings.keyBindsHotbar[3]);
					
		gameSettings.keyBindsHotbar[4] = new KeyBinding("key.hotbar.5", this.CONFIG_LOADER.keyCodeHotbar5, this.CATEGORY_INVENTORY);
		
		if (this.CONFIG_LOADER.enableHotbar5)			
		modifiedKeys.add(gameSettings.keyBindsHotbar[4]);
					
		gameSettings.keyBindsHotbar[5] = new KeyBinding("key.hotbar.6", this.CONFIG_LOADER.keyCodeHotbar6, this.CATEGORY_INVENTORY);
		
		if (this.CONFIG_LOADER.enableHotbar6)			
		modifiedKeys.add(gameSettings.keyBindsHotbar[5]);
					
		gameSettings.keyBindsHotbar[6] = new KeyBinding("key.hotbar.7", this.CONFIG_LOADER.keyCodeHotbar7, this.CATEGORY_INVENTORY);
		
		if (this.CONFIG_LOADER.enableHotbar7)			
		modifiedKeys.add(gameSettings.keyBindsHotbar[6]);
					
		gameSettings.keyBindsHotbar[7] = new KeyBinding("key.hotbar.8", this.CONFIG_LOADER.keyCodeHotbar8, this.CATEGORY_INVENTORY);
		
		if (this.CONFIG_LOADER.enableHotbar8)			
		modifiedKeys.add(gameSettings.keyBindsHotbar[7]);
					
		gameSettings.keyBindsHotbar[8] = new KeyBinding("key.hotbar.9", this.CONFIG_LOADER.keyCodeHotbar9, this.CATEGORY_INVENTORY);
		
		if (this.CONFIG_LOADER.enableHotbar9)			
		modifiedKeys.add(gameSettings.keyBindsHotbar[8]);
		
		KeyBinding[] newKeys = modifiedKeys.toArray(new KeyBinding[modifiedKeys.size()]);
		
		gameSettings.keyBindings = newKeys;
	}
    
    @SideOnly(Side.CLIENT)
    private void removeUnusedCategories() {
    	
    	if (!this.CONFIG_LOADER.enableAttack && !this.CONFIG_LOADER.enableUseItem && !this.CONFIG_LOADER.enablePickBlock) {
    		
    		KeyBinding.getKeybinds().remove(this.CATEGORY_GAMEPLAY);
    	}
    	
    	if (!this.CONFIG_LOADER.enableForward && !this.CONFIG_LOADER.enableLeft && !this.CONFIG_LOADER.enableBack 
    			&& !this.CONFIG_LOADER.enableRight && !this.CONFIG_LOADER.enableJump && !this.CONFIG_LOADER.enableSneak 
    			&& !this.CONFIG_LOADER.enableSprint) {
    		
    		KeyBinding.getKeybinds().remove(this.CATEGORY_MOVEMENT);
    	}
    	
    	if (!this.CONFIG_LOADER.enableChat && !this.CONFIG_LOADER.enablePlayerList && !this.CONFIG_LOADER.enableCommand) {
    		
    		KeyBinding.getKeybinds().remove(this.CATEGORY_MULTIPLAYER);
    	}
    	
    	if (!this.CONFIG_LOADER.enableScreenshot && !this.CONFIG_LOADER.enableFullscreen && !this.CONFIG_LOADER.enableTogglePerspective 
    			&& !this.CONFIG_LOADER.enableSmoothCamera && !this.CONFIG_LOADER.enableHideGUI && !this.CONFIG_LOADER.enableQuit 
    			&& !this.CONFIG_LOADER.enableDebugMenu && !this.CONFIG_LOADER.enableDisableShader) {
    		
    		KeyBinding.getKeybinds().remove(this.CATEGORY_MISC);
    	}
    	
    	if (!this.CONFIG_LOADER.enableDrop && !this.CONFIG_LOADER.enableInventory && !this.CONFIG_LOADER.enableHotbar1 
    			&& !this.CONFIG_LOADER.enableHotbar2 && !this.CONFIG_LOADER.enableHotbar3 && !this.CONFIG_LOADER.enableHotbar4 
    			&& !this.CONFIG_LOADER.enableHotbar5 && !this.CONFIG_LOADER.enableHotbar6 && !this.CONFIG_LOADER.enableHotbar7 
    			&& !this.CONFIG_LOADER.enableHotbar8 && !this.CONFIG_LOADER.enableHotbar9) {

    		KeyBinding.getKeybinds().remove(this.CATEGORY_INVENTORY);
    	}
    	
    	if (!this.CONFIG_LOADER.enableStreamStartStop && !this.CONFIG_LOADER.enableStreamPauseUnpause && !this.CONFIG_LOADER.enableStreamCommercial 
    			&& !this.CONFIG_LOADER.enableStreamToggleMic) {
    		
    		KeyBinding.getKeybinds().remove(this.CATEGORY_STREAM);
    	}
    }
}
