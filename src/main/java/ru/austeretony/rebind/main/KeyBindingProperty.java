package ru.austeretony.rebind.main;

public class KeyBindingProperty {
	
	private final String domain, defaultName, name, category;
	
	private final int keyCode;
	
	private final boolean enabled;
		
	public KeyBindingProperty(String domain, String defaultName, String name, String category, int keyCode, boolean enabled) {
					
		this.domain = domain;
		this.defaultName = defaultName;
		this.name = name;
		this.category = category;		
		this.keyCode = keyCode;
		this.enabled = enabled;
	}
	
	public String getDomain() {
		
		return this.domain;
	}
	
	public String getDefaultName() {
		
		return this.defaultName;
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
	
	public boolean isEnabled() {
		
		return this.enabled;
	}
}