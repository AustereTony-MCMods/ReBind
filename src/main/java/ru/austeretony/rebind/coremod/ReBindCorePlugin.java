package ru.austeretony.rebind.coremod;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@TransformerExclusions({"ru.austeretony.rebind.coremod"})
public class ReBindCorePlugin implements IFMLLoadingPlugin {
		
    @Override
    public String[] getASMTransformerClass() {
    	
        return new String[] {ReBindClassTransformer.class.getName()};
    }

    @Override
    public String getModContainerClass() {
    	
        return ReBindModContainer.class.getName();
    }

    @Override
    public String getSetupClass() {
    	
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
    	
        return null;
    }
}
