package ru.austeretony.rebind.main;

public enum EnumKeys {
	
	//Vanilla
	
	MINECRAFT("minecraft",
			new String[] {
			"key.attack",
			"key.use",
			"key.pickItem",			
			"key.forward",		
			"key.left",
			"key.back",
			"key.right",
			"key.jump",
			"key.sneak",
			"key.sprint",			
			"key.inventory",
			"key.hotbar.1",
			"key.hotbar.2",
			"key.hotbar.3",
			"key.hotbar.4",
			"key.hotbar.5",
			"key.hotbar.6",
			"key.hotbar.7",
			"key.hotbar.8",
			"key.hotbar.9",
			"key.drop",
			"key.quit",
			"key.hideHUD",
			"key.screenshot",
			"key.debugScreen",
			"key.disableShader",
			"key.togglePerspective",
			"key.fullscreen",
			"key.smoothCamera",
			"key.chat",
			"key.command",
			"key.playerlist",
			"key.streamStartStop",
			"key.streamPauseUnpause",
			"key.streamCommercial",
			"key.streamToggleMic"},
			new String[] {
			"mc_attack",
			"mc_use",
			"mc_pick_block",			
			"mc_forward",
			"mc_left",
			"mc_back",
			"mc_right",
			"mc_jump",
			"mc_sneak",
			"mc_sprint",		
			"mc_inventory",
			"mc_hotbar_1",
			"mc_hotbar_2",
			"mc_hotbar_3",
			"mc_hotbar_4",
			"mc_hotbar_5",
			"mc_hotbar_6",
			"mc_hotbar_7",
			"mc_hotbar_8",
			"mc_hotbar_9",
			"mc_drop",		
			"mc_quit",
			"mc_hide_hud",		
			"mc_screenshot",
			"mc_debug_screen",
			"mc_disable_shader",	
			"mc_toggle_perspective",
			"mc_fullscreen",
			"mc_smooth_camera",
			"mc_chat",
			"mc_command",			
			"mc_playerlist",
			"mc_stream_start_stop",
			"mc_stream_pause_unpause",
			"mc_stream_commercial",
			"mc_stream_toggle_mic"}),
	
	//Mods
	
	BAUBLES("Baubles", 
			new String[] {
			"Baubles Inventory"},
			new String[] {
			"ba_open_inventory"}),
	
	BB_WANDS("betterbuilderswands", 
			new String[] {
			"bbw.key.fluidmode",
			"bbw.key.mode"},
			new String[] {
			"bw_fluid_mode",
			"bw_mode"}),
	
	COFH_CORE("CoFHCore", 
			new String[] {
			"key.cofh.multimode", 
			"key.cofh.empower"},
			new String[] {
			"cc_multimode", 
		    "cc_empower"}),
	
	GRAVI_SUITE("GraviSuite", 
			new String[] {
			"Gravi Display Hud", 
			"Gravi Fly Key"},
			new String[] {
			"gs_display_hud", 
			"gs_toggle_fly"}),
	
	IC2("IC2",
			new String[] {
			"ALT Key", 
			"Boost Key", 
			"Hub Expand Key", 
			"Mode Switch Key", 
			"Side Inventory Key"},
			new String[] {
			"ic_alt", 
			"ic_boost", 
			"ic_hub_expand", 
			"ic_mode_switch", 
			"ic_side_inventory"}),
	
	INV_TWEAKS("inventorytweaks", 
			new String[] {
			"invtweaks.key.sort"},
			new String[] {
			"it_sort_inventory"}),
	
	IRON_BACKPACKS("ironbackpacks", 
			new String[] {
			"key_ironbackpacks_equipbackpack", 
			"key_ironbackpacks_openbackpack"},
			new String[] {
			"ib_equip_backpack", 
			"ib_open_backpack"}),
	
	OPTIFINE("optifine", 
			new String[] {
			"of.key.zoom"},
			new String[] {
			"of_zoom"}),
	
	RPG_INVENTORY("rpginventorymod", 
			new String[] {
			"RPG Inventory Key",
			"RPG Special Ability"},
			new String[] {
			"ri_open_inventory",
			"ri_spec_ability"}),
	
	THAUMCRAFT("Thaumcraft", 
			new String[] {
			"Activate Hover Harness",
			"Change Wand Focus", 
			"Misc Wand Toggle"},
			new String[] {
			"th_activate_harness",
			"th_change_focus", 
			"th_misc_toggle"}),
	
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
			"gui.xaero_waypoints_key"},
			new String[] {
			"xm_enlarge_map", 
			"xm_minimap_settings", 
			"xm_zoom_in", 
			"xm_zoom_out", 
			"xm_new_waypoint",
			"xm_instant_waypoint", 
			"xm_switch_waypoint_set", 
			"xm_toggle_grid",
			"xm_toggle_map",
			"xm_toggle_slime", 
			"xm_toggle_waypoints", 
			"xm_waypoints_key"});
	
	private String domain;
	
	private String[] bindsDefaultNames, bindsConfigKeys;
	
	EnumKeys(String domain, String[] bindsDefaultNames, String[] bindsConfigKeys) {
		
		this.domain = domain;
		this.bindsDefaultNames = bindsDefaultNames;
		this.bindsConfigKeys = bindsConfigKeys;
	}
	
	public String getDomain() {
		
		return this.domain;
	}
	
	public String[] getConfigKeys() {
		
		return this.bindsConfigKeys;
	}
	
	public String[] getDefaultNames() {
		
		return this.bindsDefaultNames;
	}
}
