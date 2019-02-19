package austeretony.rebind.common.reference;

import java.io.File;

import cpw.mods.fml.relauncher.FMLInjectionData;
import net.minecraftforge.common.MinecraftForge;

public class CommonReference {

    public static String getGameFolder() {
        return ((File) (FMLInjectionData.data()[6])).getAbsolutePath();
    }

    public static void registerEvent(Object eventClazz) {
        MinecraftForge.EVENT_BUS.register(eventClazz);
    }
}
