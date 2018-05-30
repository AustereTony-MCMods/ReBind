package ru.austeretony.rebind.main;

public class KeyBindingProperty {

	private final String name, category;
	
	private final int keyCode, order;
	
	private final boolean enabled;
	
	public KeyBindingProperty(String name, String category, int keyCode, int order, boolean enabled) {
		
		this.name = name;
		this.category = category;		
		this.keyCode = keyCode;
		this.order = order;
		this.enabled = enabled;
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
	
	public int getOrder() {
		
		return this.order;
	}
	
	public boolean isEnabled() {
		
		return this.enabled;
	}
}
