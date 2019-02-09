package austeretony.rebind.common.reference;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.FMLInjectionData;

public class CommonReference {

    public static String getGameFolder() {
        return ((File) (FMLInjectionData.data()[6])).getAbsolutePath();
    }

    public static void registerEvent(Object eventClazz) {
        MinecraftForge.EVENT_BUS.register(eventClazz);
    }
}
