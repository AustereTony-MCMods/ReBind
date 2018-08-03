package ru.austeretony.rebind.core;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@TransformerExclusions({"ru.austeretony.rebind.core"})
public class ReBindCorePlugin implements IFMLLoadingPlugin {
	
    private static boolean isObfuscated;
		
    @Override
    public String[] getASMTransformerClass() {
    	
        return new String[] {ReBindClassTransformer.class.getName()};
    }

    @Override
    public String getModContainerClass() {
    	
        return null;
    }

    @Override
    public String getSetupClass() {
    	
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    	
    	isObfuscated = (boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass() {
    	
        return null;
    }
    
    public static boolean isObfuscated() {
    	
    	return isObfuscated;
    }
}
