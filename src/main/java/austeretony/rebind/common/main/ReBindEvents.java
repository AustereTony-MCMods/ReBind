package austeretony.rebind.common.main;

import austeretony.rebind.common.config.ConfigLoader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ReBindEvents {

    @SubscribeEvent
    public void onPlayerUpdate(LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer)
            if (ConfigLoader.isAutoJumpEnabled())
                if (event.getEntityLiving().stepHeight != 1.0F)
                    event.getEntityLiving().stepHeight = 1.0F;
    }
}
