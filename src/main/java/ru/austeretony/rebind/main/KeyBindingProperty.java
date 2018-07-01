package ru.austeretony.rebind.main;

public class KeyBindingProperty {
	
	private final String configKey, name, category, modifier;
	
	private final int keyCode;
	
	private final boolean enabled;
		
	public KeyBindingProperty(String configKey, String name, String category, int keyCode, String modifier, boolean enabled) {
					
		this.configKey = configKey;
		this.name = name;
		this.category = category;		
		this.keyCode = keyCode;
		this.modifier = modifier;
		this.enabled = enabled;
	}
	
	public String getConfigKey() {
		
		return this.configKey;
	}
	
	public String getName() {
		
		return this.name;
	}
	
	public String getCategory() {
		
		return this.category;
	}
	
	public int getKeyCode() {
		
		return this.keyCode;
	}
	
	public String getModifier() {
		
		return this.modifier;
	}
	
	public boolean isEnabled() {
		
		return this.enabled;
	}
}
