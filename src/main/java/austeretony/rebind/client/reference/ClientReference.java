package austeretony.rebind.client.reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientReference {

    @SideOnly(Side.CLIENT)
    public static void registerCommand(ICommand command) {
        ClientCommandHandler.instance.registerCommand(command);
    }

    @SideOnly(Side.CLIENT)
    public static void registerKeyBinding(KeyBinding keyBinding) {
        ClientRegistry.registerKeyBinding(keyBinding);          
    }

    @SideOnly(Side.CLIENT)
    public static Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }

    @SideOnly(Side.CLIENT)
    public static EntityPlayer getClientPlayer() {
        return getMinecraft().player;
    }

    @SideOnly(Side.CLIENT)
    public static void showChatMessageClient(ITextComponent chatComponent) {
        getClientPlayer().sendMessage(chatComponent);
    }
}
