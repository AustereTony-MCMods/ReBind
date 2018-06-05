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
			"key.swapHands",			
			"key.quit",
			"key.hideHUD",
			"key.screenshot",
			"key.debugScreen",
			"key.switchShader",
			"key.togglePerspective",
			"key.fullscreen",
			"key.advancements",
			"key.smoothCamera",
			"key.spectatorOutlines",			
			"key.saveToolbarActivator",
			"key.loadToolbarActivator",			
			"key.chat",
			"key.command",
			"key.playerlist"},
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
			"mc_swap_hands",			
			"mc_quit",
			"mc_hide_hud",		
			"mc_screenshot",
			"mc_debug_screen",
			"mc_switch_shader",	
			"mc_toggle_perspective",
			"mc_fullscreen",
			"mc_advancements",
			"mc_smooth_camera",
			"mc_spectator_outlines",			
			"mc_save_toolbar",
			"mc_load_toolbar",		
			"mc_chat",
			"mc_command",			
			"mc_playerlist"}),
	
	//Mods
	
	ABYSSAL_CRAFT("abyssalcraft", 
			new String[] {
			"key.staff_mode.desc"},			
			new String[] {
			"ac_staff_mode"}),

	BAUBLES("baubles", 
			new String[] {
			"keybind.baublesinventory"},
			new String[] {
			"ba_open_inventory"}),
	
	BB_WANDS("betterbuilderswands", 
			new String[] {
			"bbw.key.fluidmode",
			"bbw.key.mode"},
			new String[] {
			"bw_fluid_mode",
			"bw_mode"}),
	
	JEI("jei", 
			new String[] {
			"key.jei.focusSearch", 
			"key.jei.showRecipe", 
			"key.jei.showUses", 
			"key.jei.nextPage", 
			"key.jei.previousPage",
			"key.jei.recipeBack", 
			"key.jei.toggleCheatMode", 
			"key.jei.toggleOverlay"},
			new String[] {
			"je_focus_search", 
			"je_show_recipe", 
			"je_show_uses", 
			"je_next_page", 
			"je_previous_page",
			"je_recipe_back", 
			"je_toggle_cheat_mode", 
			"je_toggle_overlay"}),
	
	JOURNEY_MAP("journeymap", 
			new String[] {
			"key.journeymap.minimap_type", 
			"key.journeymap.create_waypoint", 
			"key.journeymap.map_toggle_alt", 
			"key.journeymap.minimap_toggle_alt", 
			"key.journeymap.minimap_preset",
			"key.journeymap.fullscreen_waypoints", 
			"key.journeymap.zoom_in", 
			"key.journeymap.zoom_out",			
			"key.journeymap.fullscreen_chat_position", 
			"key.journeymap.fullscreen_create_waypoint", 
			"key.journeymap.fullscreen_options", 
			"key.journeymap.fullscreen.east", 
			"key.journeymap.fullscreen.north",
			"key.journeymap.fullscreen.south", 
			"key.journeymap.fullscreen.west"},
			new String[] {
			"jm_minimap_type", 
			"jm_create_waypoint", 
			"jm_map_toggle_alt", 
			"jm_minimap_toggle_alt", 
			"jm_minimap_preset",
			"jm_fullscreen_waypoints", 
			"jm_zoom_in", 
			"jm_zoom_out",			
			"jm_full_chat_position", 
			"jm_full_create_waypoint", 
			"jm_full_options", 
			"jm_full_east", 
			"jm_full_north",
			"jm_full_south", 
			"jm_full_west"}),
	
	IC2("ic2", 
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
	
	OPTIFINE("optifine", 
			new String[] {
			"of.key.zoom"},
			new String[] {
			"of_zoom"}),
	
	RPG_INVENTORY("rpginventory", 
			new String[] {
			"RPG Inventory Key"},
			new String[] {
			"ri_open_inventory"}),
	
	THAUMCRAFT("thaumcraft", 
			new String[] {
			"Change Caster Focus", 
			"Misc Caster Toggle"},
			new String[] {
			"th_change_focus", 
			"th_misc_toggle"}),
	
	XAERO_MINIMAP("xaerominimap", 
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
