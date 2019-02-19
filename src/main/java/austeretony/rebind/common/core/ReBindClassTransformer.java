package austeretony.rebind.common.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import com.google.gson.JsonSyntaxException;

import austeretony.rebind.common.config.ConfigLoader;
import net.minecraft.launchwrapper.IClassTransformer;

public class ReBindClassTransformer implements IClassTransformer {

    public static final Logger CORE_LOGGER = LogManager.getLogger("ReBind Core");

    public ReBindClassTransformer() {
        try {
            ConfigLoader.load();
        } catch (JsonSyntaxException exception) {
            CORE_LOGGER.error("Config parsing failure! This will cause mess up in controls. Fix syntax errors!");
            exception.printStackTrace();
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        switch (transformedName) {
        case "net.minecraft.client.resources.Locale":                    
            return patch(basicClass, EnumInputClasses.MC_LOCALE);
        case "net.minecraft.client.settings.KeyBinding":
            return patch(basicClass, EnumInputClasses.MC_KEY_BINDING);
        case "net.minecraft.client.gui.GuiKeyBindingList":
            return patch(basicClass, EnumInputClasses.MC_GUI_KEY_BINDING_LIST);
        case "net.minecraft.client.Minecraft":
            return patch(basicClass, EnumInputClasses.MC_MINECRAFT);
        case "net.minecraft.client.settings.GameSettings":
            return patch(basicClass, EnumInputClasses.MC_GAME_SETTINGS);
        case "net.minecraft.client.gui.GuiScreen":
            return patch(basicClass, EnumInputClasses.MC_GUI_SCREEN);
        case "net.minecraft.client.gui.GuiControls":
            return patch(basicClass, EnumInputClasses.MC_GUI_CONTROLS);
        case "net.minecraft.client.gui.GuiKeyBindingList$KeyEntry":
            return patch(basicClass, EnumInputClasses.MC_KEY_ENTRY);
        case "net.minecraft.client.gui.inventory.GuiContainer":
            return patch(basicClass, EnumInputClasses.MC_GUI_CONTAINER);
        case "net.minecraft.client.entity.EntityPlayerSP":
            return patch(basicClass, EnumInputClasses.MC_ENTITY_PLAYER_SP);

            //case "us.getfluxed.controlsearch.client.gui.GuiNewKeyBindingList":
            //return patch(basicClass, EnumInputClasses.CONTROLING_GUI_NEW_KEY_BINDING_LIST);

        case "dmillerw.menu.handler.KeyboardHandler":
            return patch(basicClass, EnumInputClasses.MM_KEYBOARD_HANDLER);
        }
        return basicClass;
    }

    private byte[] patch(byte[] basicClass, EnumInputClasses enumInput) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, enumInput.readerFlags);
        if (enumInput.patch(classNode))
            CORE_LOGGER.info(enumInput.domain + " <" + enumInput.clazz + ".class> patched!");
        ClassWriter writer = new ClassWriter(enumInput.writerFlags);        
        classNode.accept(writer);
        return writer.toByteArray();    
    }
}
