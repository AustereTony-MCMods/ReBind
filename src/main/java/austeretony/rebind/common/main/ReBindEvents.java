package austeretony.rebind.common.main;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class ReBindEvents {

    @SubscribeEvent
    public void onPlayerUpdate(LivingUpdateEvent event) {
        if (event.entityLiving instanceof EntityPlayer)
            if (event.entityLiving.stepHeight != 1.0F)
                event.entityLiving.stepHeight = 1.0F;
    }
}
