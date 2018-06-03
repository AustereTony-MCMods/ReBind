package ru.austeretony.rebind.main;

public enum EnumModsKeys {
	
	BAUBLES("Baubles", 
			new String[] {
			"Baubles Inventory"}),
	
	BB_WANDS("betterbuilderswands", 
			new String[] {
			"bbw.key.fluidmode",
			"bbw.key.mode"}),
	
	COFH_CORE("CoFHCore", 
			new String[] {
			"key.cofh.multimode", 
			"key.cofh.empower"}),
	
	GRAVI_SUITE("GraviSuite", 
			new String[] {
			"Gravi Display Hud", 
			"Gravi Fly Key"}),
	
	IC2("IC2",
			new String[] {
			"ALT Key", 
			"Boost Key", 
			"Hub Expand Key", 
			"Mode Switch Key", 
			"Side Inventory Key"}),
	
	INV_TWEAKS("inventorytweaks", 
			new String[] {
			"invtweaks.key.sort"}),
	
	IRON_BACKPACKS("ironbackpacks", 
			new String[] {
			"key_ironbackpacks_equipbackpack", 
			"key_ironbackpacks_openbackpack"}),
	
	RPG_INVENTORY("rpginventorymod", 
			new String[] {
			"RPG Inventory Key",
			"RPG Special Ability"}),
	
	THAUMCRAFT("Thaumcraft", 
			new String[] {
			"Activate Hover Harness",
			"Change Wand Focus", 
			"Misc Wand Toggle"}),
	
	XAERO_MINIMAP("XaeroMinimap", 
			new String[] {
			"gui.xaero_enlarge_map", 
			"gui.xaero_minimap_settings", 
			"gui.xaero_zoom_in", 
			"gui.xaero_zoom_out", 
			"gui.xaero_new_waypoint",
			"gui.xaero_instant_waypoint", 
			"gui.xaero_switch_waypoint_set", 
			"gui.xaero_toggle_grid",
			"gui.xaero_toggle_map",
			"gui.xaero_toggle_slime", 
			"gui.xaero_toggle_waypoints", 
			"gui.xaero_waypoints_key"});
	
	private String domain;
	
	private String[] modKeysNames;
	
	EnumModsKeys(String domain, String[] keyBindingNames) {
		
		this.domain = domain;
		this.modKeysNames = keyBindingNames;
	}
	
	public String getDomain() {
		
		return this.domain;
	}
	
	public String[] getKeysNames() {
		
		return this.modKeysNames;
	}
}
