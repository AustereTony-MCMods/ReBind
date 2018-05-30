package ru.austeretony.rebind.coremod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.nbt.NBTTagCompound;
import ru.austeretony.rebind.main.KeyBindingProperty;
import ru.austeretony.rebind.main.ReBindMain;

public class ReBindHooks {
	
	public static boolean loadControlsFromOptionsFile(NBTTagCompound optionsTagCompound) {
				
		boolean wasLoadedBefore = optionsTagCompound.getKeySet().contains("key_key.quit");
				
		if (wasLoadedBefore) {
			
			return true;
		}
		
		else if (ReBindMain.CONFIG_LOADER.enableControlsRewriting) {
			
			return false;
		}
										
		return true;
	}
	
	public static KeyBinding[] sortKeyBindings(KeyBinding[] bindingsArray) {
		
		Map<String, Integer> nativeOrder = new HashMap<String, Integer>();		
		
		Multimap<String, Integer> bindsByCategory = HashMultimap.<String, Integer>create();
		
		List<KeyBinding> bindingsList = new ArrayList<KeyBinding>(Arrays.asList(bindingsArray));
		
		List<KeyBinding> orderedBindings = new ArrayList<KeyBinding>();
		
		for (int bindIndex = 0; bindIndex < bindingsArray.length; bindIndex++) {
			
			nativeOrder.put(bindingsArray[bindIndex].getKeyDescription().substring(4), bindIndex);
			
			if (isVanillaCategory(bindingsArray[bindIndex].getKeyCategory()))
			bindsByCategory.put(bindingsArray[bindIndex].getKeyCategory().substring(15), bindIndex);
		}
		
		int propIndex,knownBindNumber, unknownBindIndex;
		
		KeyBinding curKnownBinding, curUnknownBinding;
		
		List<KeyBindingProperty> props = ReBindMain.CONFIG_LOADER.orderedProperties;
		
		Iterator<Integer> iterator;
		
		for (KeyBindingProperty property : props) {
			
			propIndex = props.indexOf(property);
			
			knownBindNumber = nativeOrder.get(property.getName());
			
			curKnownBinding = bindingsArray[knownBindNumber];
						
			orderedBindings.add(curKnownBinding);	
			
			bindingsList.remove(curKnownBinding);
			
			bindsByCategory.remove(property.getCategory(), knownBindNumber);
			
			if ((propIndex + 1 < props.size() && !props.get(propIndex + 1).getCategory().equals(property.getCategory())) || propIndex + 1 == props.size()) {
				
				while (bindsByCategory.containsKey(property.getCategory())) {
					
					iterator = bindsByCategory.get(property.getCategory()).iterator();
					
					while (iterator.hasNext()) {
						
						unknownBindIndex = iterator.next();
						
						curUnknownBinding = bindingsArray[unknownBindIndex];
						
						orderedBindings.add(curUnknownBinding);
						
						bindingsList.remove(curUnknownBinding);
						
						iterator.remove();
					}
				}
			}
		}
		
		orderedBindings.addAll(bindingsList);
		
		return orderedBindings.toArray(new KeyBinding[orderedBindings.size()]);		
	}
	
	private static boolean isVanillaCategory(String string) {
				
		return string.equals("key.categories.gameplay") || 
				string.equals("key.categories.movement") || 
				string.equals("key.categories.inventory") ||
				string.equals("key.categories.misc") ||
				string.equals("key.categories.multiplayer");
	}
	
	public static int getQuitKeyCode() {
		
		return ReBindMain.instance.keyBindQuit.getKeyCode();
	}

	public static int getHideHUDKeyCode() {
		
		return ReBindMain.instance.keyBindHideHUD.getKeyCode();
	}
	
	public static int getDebugMenuKeyCode() {
		
		return ReBindMain.instance.keyBindDebugScreen.getKeyCode();
	}
	
	public static int getSwitchShaderKeyCode() {
		
		return ReBindMain.instance.keyBindSwitchShader.getKeyCode();
	}
}