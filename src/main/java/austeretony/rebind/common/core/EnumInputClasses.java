package austeretony.rebind.common.core;

import java.util.Iterator;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import austeretony.rebind.common.config.EnumConfigSettings;

public enum EnumInputClasses {

    MC_LOCALE("Minecraft", "Locale", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),
    MC_KEY_BINDING("Minecraft", "KeyBinding", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),
    MC_GUI_KEY_BINDING_LIST("Minecraft", "GuiKeyBindingList", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),
    MC_MINECRAFT("Minecraft", "Minecraft", 0, 0),
    MC_GAME_SETTINGS("Minecraft", "GameSettings", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),
    MC_GUI_SCREEN("Minecraft", "GuiScreen", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),
    MC_GUI_CONTROLS("Minecraft", "GuiControls", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),
    MC_KEY_ENTRY("Minecraft", "GuiNewKeyBindingList$KeyEntry", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),
    MC_GUI_CONTAINER("Minecraft", "GuiContainer", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),    
    MC_ENTITY_PLAYER_SP("Minecraft", "EntityPlayerSP", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),

    CONTROLING_GUI_NEW_KEY_BINDING_LIST("Controling", "GuiNewKeyBindingList", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),

    MM_KEYBOARD_HANDLER("MineMenu", "KeyboardHandler", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

    private static final String HOOKS_CLASS = "austeretony/rebind/common/core/ReBindHooks";

    public final String domain, clazz;

    public final int readerFlags, writerFlags;

    EnumInputClasses(String domain, String clazz, int readerFlags, int writerFlags) {
        this.domain = domain;
        this.clazz = clazz;
        this.readerFlags = readerFlags;
        this.writerFlags = writerFlags;
    }

    public boolean patch(ClassNode classNode) {
        switch (this) {
        case MC_LOCALE:
            return pathcMCLocale(classNode);
        case MC_KEY_BINDING:
            return patchMCKeyBinding(classNode);
        case MC_GUI_KEY_BINDING_LIST:
            return patchMCGuiKeyBindingList(classNode);
        case MC_MINECRAFT:
            return patchMCMinecraft(classNode);
        case MC_GAME_SETTINGS:
            return patchMCGameSettings(classNode);
        case MC_GUI_SCREEN:
            return patchMCGuiScreen(classNode);
        case MC_GUI_CONTROLS:
            return patchMCGuiControls(classNode);
        case MC_KEY_ENTRY:
            return patchMCKeyEntry(classNode);
        case MC_GUI_CONTAINER:
            return patchMCGuiContainer(classNode);
        case MC_ENTITY_PLAYER_SP:
            return patchMCEntityPlayerSP(classNode);

        case CONTROLING_GUI_NEW_KEY_BINDING_LIST:
            return patchControlingGuiKeyBindingList(classNode);

        case MM_KEYBOARD_HANDLER:
            return EnumConfigSettings.FIX_MM_KEYBINDING.isEnabled() ? patchMMKeyboardHanndler(classNode) : false;
        }
        return false;
    }

    private boolean pathcMCLocale(ClassNode classNode) {
        String
        propertiesFieldName = ReBindCorePlugin.isObfuscated() ? "a" : "field_135032_a",
                loadLocaleDataFilesMethodName = ReBindCorePlugin.isObfuscated() ? "a" : "loadLocaleDataFiles",
                        localeClassName = ReBindCorePlugin.isObfuscated() ? "brs" : "net/minecraft/client/resources/Locale",
                                iResourceManagerClassName = ReBindCorePlugin.isObfuscated() ? "bqy" : "net/minecraft/client/resources/IResourceManager",
                                        listClassName = "java/util/List",
                                        mapClassName = "java/util/Map";
        boolean isSuccessful = false;   
        int invokespecialCount = 0;
        AbstractInsnNode currentInsn;

        for (MethodNode methodNode : classNode.methods) {               
            if (methodNode.name.equals(loadLocaleDataFilesMethodName) && methodNode.desc.equals("(L" + iResourceManagerClassName + ";L" + listClassName + ";)V")) {                         
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();              
                while (insnIterator.hasNext()) {                        
                    currentInsn = insnIterator.next();                  
                    if (currentInsn.getOpcode() == Opcodes.INVOKESPECIAL) {    
                        invokespecialCount++;
                        if (invokespecialCount == 3) {
                            InsnList nodesList = new InsnList();   
                            nodesList.add(new VarInsnNode(Opcodes.ALOAD, 2));
                            nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, localeClassName, propertiesFieldName, "L" + mapClassName + ";"));
                            nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "loadCustomLocalization", "(L" + listClassName + ";L" + mapClassName + ";)V", false));
                            methodNode.instructions.insertBefore(currentInsn.getPrevious(), nodesList); 
                            isSuccessful = true;                        
                            break;
                        }
                    }
                }    
                break;
            }
        }
        return isSuccessful;
    }

    private boolean patchMCKeyBinding(ClassNode classNode) {
        String 
        onTickMethodName = ReBindCorePlugin.isObfuscated() ? "a" : "onTick",
                isKeyPressedMethodName = ReBindCorePlugin.isObfuscated() ? "d" : "getIsKeyPressed",
                        setKeyBindStateMethodName = ReBindCorePlugin.isObfuscated() ? "a" : "setKeyBindState",
                                isPressedMethodName = ReBindCorePlugin.isObfuscated() ? "f" : "isPressed",
                                        stringClassName = "java/lang/String",
                                        keyBindingClassName = ReBindCorePlugin.isObfuscated() ? "bal" : "net/minecraft/client/settings/KeyBinding";
        boolean 
        descChanged = false,
        isSuccessful = false;
        AbstractInsnNode currentInsn;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(onTickMethodName) && methodNode.desc.equals("(I)V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.ILOAD) {    
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ILOAD, 0));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "onTick", "(I)V", false));
                        nodesList.add(new InsnNode(Opcodes.RETURN));
                        methodNode.instructions.insertBefore(currentInsn.getPrevious(), nodesList); 
                        break;
                    }
                }
            }
            if (methodNode.name.equals(setKeyBindStateMethodName) && methodNode.desc.equals("(IZ)V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.ILOAD) {    
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ILOAD, 0));
                        nodesList.add(new VarInsnNode(Opcodes.ILOAD, 1));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "setKeyBindState", "(IZ)V", false));
                        nodesList.add(new InsnNode(Opcodes.RETURN));
                        methodNode.instructions.insertBefore(currentInsn, nodesList); 
                        break;
                    }
                }
            }
            if (methodNode.name.equals("<init>") && methodNode.desc.equals("(L" + stringClassName + ";IL" + stringClassName + ";)V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (!descChanged && currentInsn.getOpcode() == Opcodes.INVOKESPECIAL) {                     
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "getKeyBindingName", "(L" + stringClassName + ";)L" + stringClassName + ";", false));
                        nodesList.add(new VarInsnNode(Opcodes.ASTORE, 1));   
                        nodesList.add(new VarInsnNode(Opcodes.ILOAD, 2));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "getKeyBindingKeyCode", "(I)I", false));
                        nodesList.add(new VarInsnNode(Opcodes.ISTORE, 2));
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 3));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "getKeyBindingCategory", "(L" + stringClassName + ";)L" + stringClassName + ";", false));
                        nodesList.add(new VarInsnNode(Opcodes.ASTORE, 3));                                     
                        methodNode.instructions.insert(currentInsn, nodesList); 
                        descChanged = true;
                    }
                    if (currentInsn.getOpcode() == Opcodes.RETURN) {    
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "storeKeybinding", "(L" + keyBindingClassName + ";)V", false));
                        methodNode.instructions.insertBefore(currentInsn, nodesList); 
                        break;
                    }
                }                
            }                       
            if (methodNode.name.equals(isKeyPressedMethodName) && methodNode.desc.equals("()Z")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.ALOAD) {    
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isKeyPressed", "(L" + keyBindingClassName + ";)Z", false));
                        nodesList.add(new InsnNode(Opcodes.IRETURN));
                        methodNode.instructions.insertBefore(currentInsn, nodesList); 
                        break;
                    }
                }
            }
            if (methodNode.name.equals(isPressedMethodName) && methodNode.desc.equals("()Z")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.ALOAD) {    
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isPressed", "(L" + keyBindingClassName + ";)Z", false));
                        nodesList.add(new InsnNode(Opcodes.IRETURN));
                        methodNode.instructions.insertBefore(currentInsn, nodesList); 
                        break;
                    }
                }
                isSuccessful = true;
                break;
            }
        }
        return isSuccessful;
    }

    private boolean patchMCGuiKeyBindingList(ClassNode classNode) {
        String keyBindingClassName = ReBindCorePlugin.isObfuscated() ? "bal" : "net/minecraft/client/settings/KeyBinding";
        boolean isSuccessful = false;
        AbstractInsnNode currentInsn;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("<init>")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.ASTORE) {                    
                        InsnList nodesList = new InsnList();
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "sortKeyBindings", "()[L" + keyBindingClassName + ";", false));
                        nodesList.add(new VarInsnNode(Opcodes.ASTORE, 3));
                        methodNode.instructions.insert(currentInsn, nodesList);                    
                    } else if (currentInsn.getOpcode() == Opcodes.ACONST_NULL) {
                        InsnList nodesList = new InsnList();
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "sortKeyBindings", "()[L" + keyBindingClassName + ";", false));
                        nodesList.add(new VarInsnNode(Opcodes.ASTORE, 3));
                        methodNode.instructions.insertBefore(currentInsn, nodesList); 
                        break;
                    }
                }
                isSuccessful = true;
                break;
            }
        }
        return isSuccessful;
    }

    private boolean patchMCMinecraft(ClassNode classNode) {
        String runTickMethodName = ReBindCorePlugin.isObfuscated() ? "p" : "runTick";
        int 
        bipushCount = 0,
        iconstCount = 0,
        ifeqCount =0;
        boolean 
        isSuccessful = false,
        first = false;
        AbstractInsnNode currentInsn;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(runTickMethodName) && methodNode.desc.equals("()V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (!first && currentInsn.getOpcode() == Opcodes.IFEQ) {
                        ifeqCount++;
                        if (ifeqCount == 7) {
                            InsnList nodesList = new InsnList();
                            nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isHotbarScrollingAllowed", "()Z", false));          
                            nodesList.add(new JumpInsnNode(Opcodes.IFEQ, ((JumpInsnNode) currentInsn).label));
                            methodNode.instructions.insertBefore(currentInsn.getPrevious(), nodesList); 
                            first = true;
                        }
                    }
                    if (currentInsn.getOpcode() == Opcodes.BIPUSH) {
                        bipushCount++;
                        if (bipushCount == 4 || bipushCount == 6 || bipushCount == 9 || bipushCount == 11 || bipushCount == 13 || bipushCount == 17 || bipushCount == 19 || bipushCount == 21 || bipushCount == 23 || bipushCount == 25) {
                            methodNode.instructions.insertBefore(currentInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "getDebugScreenKeyCode", "()I", false)); 
                            insnIterator.remove();
                            if (bipushCount == 25)
                                break; 
                        }
                        if (bipushCount == 7) {
                            methodNode.instructions.insertBefore(currentInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "getDisableShaderKeyCode", "()I", false)); 
                            insnIterator.remove();
                        }
                        if (bipushCount == 24) {
                            methodNode.instructions.insertBefore(currentInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isHideHUDKeyPressed", "(I)Z", false)); 
                            ((JumpInsnNode) currentInsn.getNext()).setOpcode(Opcodes.IFEQ);
                            insnIterator.remove();               
                        } 
                    }                                                                       
                    if (currentInsn.getOpcode() == Opcodes.ICONST_1) {
                        iconstCount++;
                        if (iconstCount == 4) {
                            methodNode.instructions.insertBefore(currentInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isQuitKeyPressed", "(I)Z", false)); 
                            ((JumpInsnNode) currentInsn.getNext()).setOpcode(Opcodes.IFEQ);
                            insnIterator.remove();
                        }
                    }
                }
                isSuccessful = true;
                break;
            }
        }
        return isSuccessful;
    }

    private boolean patchMCGameSettings(ClassNode classNode) {
        String 
        loadOptionsMethodName = ReBindCorePlugin.isObfuscated() ? "a" : "loadOptions",
                saveOptionsMethodName = ReBindCorePlugin.isObfuscated() ? "b" : "saveOptions",
                        stringClassName = "java/lang/String",
                        keyBindingClassName = ReBindCorePlugin.isObfuscated() ? "bal" : "net/minecraft/client/settings/KeyBinding",
                                printWriterClassName = "java/io/PrintWriter";
        boolean 
        isFirstInserted = false,
        isSuccessful = false;
        AbstractInsnNode currentInsn;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(loadOptionsMethodName) && methodNode.desc.equals("()V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (!isFirstInserted && currentInsn.getOpcode() == Opcodes.ALOAD) {
                        InsnList nodesList = new InsnList();
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "removeHiddenKeyBindings", "()V", false));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "removeControlsSettings", "()V", false));
                        methodNode.instructions.insertBefore(currentInsn, nodesList);
                        isFirstInserted = true;
                    }
                    if (currentInsn.getOpcode() == Opcodes.IF_ICMPGE) {
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 3));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "loadControlsFromOptionsFile", "([L" + stringClassName + ";)Z", false));                    
                        nodesList.add(new JumpInsnNode(Opcodes.IFEQ, ((JumpInsnNode) currentInsn).label));
                        methodNode.instructions.insertBefore(currentInsn.getPrevious().getPrevious(), nodesList);
                        break;
                    }
                }   
            }
            if (methodNode.name.equals(saveOptionsMethodName) && methodNode.desc.equals("()V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.IF_ICMPGE) {
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "saveControlsToOptionsFile", "(L" + printWriterClassName + ";)Z", false));          
                        nodesList.add(new JumpInsnNode(Opcodes.IFEQ, ((JumpInsnNode) currentInsn).label));
                        methodNode.instructions.insertBefore(currentInsn.getPrevious().getPrevious(), nodesList);
                        break;
                    }
                }   
                isSuccessful = true;
                break;
            }
        }
        return isSuccessful;
    }

    private boolean patchMCGuiScreen(ClassNode classNode) {
        String keyTypedMethodName = ReBindCorePlugin.isObfuscated() ? "a" : "keyTyped";  
        boolean isSuccessful = false;
        AbstractInsnNode currentInsn;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(keyTypedMethodName) && methodNode.desc.equals("(CI)V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.ICONST_1) {
                        methodNode.instructions.insertBefore(currentInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "getQuitKeyCode", "()I", false)); 
                        insnIterator.remove();
                        break;
                    }
                }
                isSuccessful = true;
                break;
            }
        }
        return isSuccessful;
    }

    private boolean patchMCGuiControls(ClassNode classNode) {
        String
        buttonIdFieldName = ReBindCorePlugin.isObfuscated() ? "f" : "buttonId",
                resetButtonFieldName = ReBindCorePlugin.isObfuscated() ? "t" : "field_146493_s",
                        actionPerformedMethodName = ReBindCorePlugin.isObfuscated() ? "a" : "actionPerformed",
                                mouseClickedMethodName = ReBindCorePlugin.isObfuscated() ? "a" : "mouseClicked",
                                        keyTypedMethodName = ReBindCorePlugin.isObfuscated() ? "a" : "keyTyped",
                                                drawScreenMethodName = ReBindCorePlugin.isObfuscated() ? "a" : "drawScreen",
                                                        guiScreenClassName = ReBindCorePlugin.isObfuscated() ? "bdw" : "net/minecraft/client/gui/GuiScreen",
                                                                guiButtonClassName = ReBindCorePlugin.isObfuscated() ? "bcb" : "net/minecraft/client/gui/GuiButton",
                                                                        keyModifierClassName = "austeretony/rebind/client/keybinding/EnumKeyModifier",
                                                                        guiControlsClassName = ReBindCorePlugin.isObfuscated() ? "bew" : "net/minecraft/client/gui/GuiControls",
                                                                                keyBindingClassName = ReBindCorePlugin.isObfuscated() ? "bal" : "net/minecraft/client/settings/KeyBinding";
        boolean isSuccessful = false;
        int aloadCount = 0;
        AbstractInsnNode currentInsn;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(actionPerformedMethodName) && methodNode.desc.equals("(L" + guiButtonClassName + ";)V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.ALOAD) {
                        aloadCount++;
                        if (aloadCount == 5) {
                            InsnList nodesList = new InsnList();
                            nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "resetAllKeys", "()V", false));
                            nodesList.add(new InsnNode(Opcodes.RETURN));
                            methodNode.instructions.insertBefore(currentInsn, nodesList); 
                            break;
                        }                       
                    }
                }               
            }
            if (methodNode.name.equals(mouseClickedMethodName) && methodNode.desc.equals("(III)V")) {
                aloadCount = 0;
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.ALOAD) {
                        aloadCount++;
                        if (aloadCount == 2) {
                            InsnList nodesList = new InsnList();
                            nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, guiControlsClassName, buttonIdFieldName, "L" + keyBindingClassName + ";"));
                            nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, keyModifierClassName, "getActiveModifier", "()L" + keyModifierClassName + ";", false));
                            nodesList.add(new IntInsnNode(Opcodes.BIPUSH, - 100));
                            nodesList.add(new VarInsnNode(Opcodes.ILOAD, 3));
                            nodesList.add(new InsnNode(Opcodes.IADD));
                            nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "setKeyModifierAndCode", "(L" + keyBindingClassName + ";L" + keyModifierClassName + ";I)V", false));
                            methodNode.instructions.insertBefore(currentInsn, nodesList); 
                            break;
                        }                       
                    }
                }               
            }
            if (methodNode.name.equals(keyTypedMethodName) && methodNode.desc.equals("(CI)V")) {
                aloadCount = 0;
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.ALOAD) {
                        aloadCount++;
                        if (aloadCount == 2) {
                            InsnList nodesList = new InsnList();
                            nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, guiControlsClassName, buttonIdFieldName, "L" + keyBindingClassName + ";"));
                            nodesList.add(new FieldInsnNode(Opcodes.GETSTATIC, keyModifierClassName, "NONE", "L" + keyModifierClassName + ";"));
                            nodesList.add(new IntInsnNode(Opcodes.BIPUSH, 0));
                            nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "setKeyModifierAndCode", "(L" + keyBindingClassName + ";L" + keyModifierClassName + ";I)V", false));
                            methodNode.instructions.insertBefore(currentInsn, nodesList); 
                        }  
                        if (aloadCount == 4) {
                            InsnList nodesList = new InsnList();
                            nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, guiControlsClassName, buttonIdFieldName, "L" + keyBindingClassName + ";"));
                            nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, keyModifierClassName, "getActiveModifier", "()L" + keyModifierClassName + ";", false));
                            nodesList.add(new VarInsnNode(Opcodes.ILOAD, 2));
                            nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "setKeyModifierAndCode", "(L" + keyBindingClassName + ";L" + keyModifierClassName + ";I)V", false));
                            methodNode.instructions.insertBefore(currentInsn, nodesList); 
                            break;
                        }   
                    }
                    if (currentInsn.getOpcode() == Opcodes.ACONST_NULL) {
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, guiControlsClassName, buttonIdFieldName, "L" + keyBindingClassName + ";"));
                        nodesList.add(new VarInsnNode(Opcodes.ILOAD, 2));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "resetKeyBinding", "(L" + keyBindingClassName + ";I)L" + keyBindingClassName + ";", false));
                        methodNode.instructions.insertBefore(currentInsn, nodesList); 
                        insnIterator.remove();
                        break;
                    }
                }
            }
            if (methodNode.name.equals(drawScreenMethodName) && methodNode.desc.equals("(IIF)V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.ISTORE) {
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, guiControlsClassName, resetButtonFieldName, "L" + guiButtonClassName + ";"));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "setResetButtonState", "(L" + guiButtonClassName + ";)V", false));
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        nodesList.add(new VarInsnNode(Opcodes.ILOAD, 1));
                        nodesList.add(new VarInsnNode(Opcodes.ILOAD, 2));
                        nodesList.add(new VarInsnNode(Opcodes.FLOAD, 3));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, guiScreenClassName, drawScreenMethodName, "(IIF)V", false));
                        nodesList.add(new InsnNode(Opcodes.RETURN));
                        methodNode.instructions.insertBefore(currentInsn.getPrevious(), nodesList); 
                        break;
                    }
                }  
                isSuccessful = true;
                break;
            }
        }
        return isSuccessful;
    }

    private boolean patchMCKeyEntry(ClassNode classNode) {
        String
        changeButtonFieldName = ReBindCorePlugin.isObfuscated() ? "d" : "btnChangeKeyBinding",
                resetButtonFieldName = ReBindCorePlugin.isObfuscated() ? "e" : "btnReset",
                        keyBindingFieldName = ReBindCorePlugin.isObfuscated() ? "b" : "field_148282_b",
                                drawEntryMethodName = ReBindCorePlugin.isObfuscated() ? "a" : "drawEntry",
                                        mousePressedMethodName = ReBindCorePlugin.isObfuscated() ? "a" : "mousePressed",
                                                keyEntryClassName = ReBindCorePlugin.isObfuscated() ? "bev" : "net/minecraft/client/gui/GuiKeyBindingList$KeyEntry",
                                                        tesselatorClassName = ReBindCorePlugin.isObfuscated() ? "bmh" : "net/minecraft/client/renderer/Tessellator",
                                                                guiButtonClassName = ReBindCorePlugin.isObfuscated() ? "bcb" : "net/minecraft/client/gui/GuiButton",
                                                                        keyBindingClassName = ReBindCorePlugin.isObfuscated() ? "bal" : "net/minecraft/client/settings/KeyBinding";
        boolean isSuccessful = false;
        int
        ifeqCount = 0,
        aloadCount = 0;
        AbstractInsnNode currentInsn;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(drawEntryMethodName) && methodNode.desc.equals("(IIIIIL" + tesselatorClassName + ";IIZ)V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.ALOAD) {    
                        aloadCount++;
                        if (aloadCount == 7) {
                            InsnList nodesList = new InsnList();
                            nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, keyEntryClassName, changeButtonFieldName, "L" + guiButtonClassName + ";"));
                            nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, keyEntryClassName, resetButtonFieldName, "L" + guiButtonClassName + ";"));
                            nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, keyEntryClassName, keyBindingFieldName, "L" + keyBindingClassName + ";"));
                            nodesList.add(new VarInsnNode(Opcodes.ILOAD, 10));
                            nodesList.add(new VarInsnNode(Opcodes.ILOAD, 2));
                            nodesList.add(new VarInsnNode(Opcodes.ILOAD, 3));
                            nodesList.add(new VarInsnNode(Opcodes.ILOAD, 7));
                            nodesList.add(new VarInsnNode(Opcodes.ILOAD, 8));
                            nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "drawCuiControlsKeyEntry", "(L" + guiButtonClassName + ";L" + guiButtonClassName + ";L" + keyBindingClassName + ";ZIIII)V", false));
                            nodesList.add(new InsnNode(Opcodes.RETURN));
                            methodNode.instructions.insertBefore(currentInsn, nodesList); 
                            break;
                        }
                    }
                }
            }
            if (methodNode.name.equals(mousePressedMethodName) && methodNode.desc.equals("(IIIIII)Z")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.IFEQ) {    
                        ifeqCount++;
                        if (ifeqCount == 2) {
                            InsnList nodesList = new InsnList();
                            nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, keyEntryClassName, keyBindingFieldName, "L" + keyBindingClassName + ";"));
                            nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "setToDefault", "(L" + keyBindingClassName + ";)V", false));
                            methodNode.instructions.insertBefore(currentInsn.getNext(), nodesList); 
                            isSuccessful = true;
                            break;
                        }
                    }
                }
                break;
            }
        }
        return isSuccessful;
    }

    private boolean patchMCGuiContainer(ClassNode classNode) {
        return patchMCGuiScreen(classNode);
    }

    private boolean patchMCEntityPlayerSP(ClassNode classNode) {
        String targetMethodName = ReBindCorePlugin.isObfuscated() ? "e" : "onLivingUpdate";  
        boolean isSuccessful = false;
        int iconstCount = 0;
        AbstractInsnNode currentInsn;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(targetMethodName) && methodNode.desc.equals("()V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.ICONST_1) {
                        iconstCount++;
                        if (iconstCount == 6) {
                            methodNode.instructions.insert(currentInsn.getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isDoubleTapForwardSprintAllowed", "()Z", false)); 
                            insnIterator.remove();
                        }
                        if (iconstCount == 7) {
                            methodNode.instructions.insert(currentInsn.getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isPlayerSprintAllowed", "()Z", false)); 
                            insnIterator.remove();
                            break;
                        }
                    }
                }
                isSuccessful = true;
                break;
            }
        }
        return isSuccessful;
    }

    private boolean patchControlingGuiKeyBindingList(ClassNode classNode) {
        return patchMCGuiKeyBindingList(classNode);
    }

    private boolean patchMMKeyboardHanndler(ClassNode classNode) {
        String
        wheelFieldName = "WHEEL",
        onClientTickMethodName = "onClientTick",
        keyboardHandlerClassname = "dmillerw/menu/handler/KeyboardHandler",
        keyBindingClassName = "net/minecraft/client/settings/KeyBinding";
        boolean isSuccessful = false;
        AbstractInsnNode currentInsn;    

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(onClientTickMethodName)) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next();
                    if (currentInsn.getOpcode() == Opcodes.IF_ICMPEQ) {
                        InsnList nodesList = new InsnList();
                        nodesList.add(new FieldInsnNode(Opcodes.GETSTATIC, keyboardHandlerClassname, wheelFieldName, "L" + keyBindingClassName + ";"));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isMineMenuKeyPressed", "(L" + keyBindingClassName + ";)Z", false));
                        nodesList.add(new JumpInsnNode(Opcodes.IFEQ, ((JumpInsnNode) currentInsn).label));
                        methodNode.instructions.insertBefore(currentInsn.getPrevious().getPrevious().getPrevious(), nodesList);
                        methodNode.instructions.remove(currentInsn.getPrevious().getPrevious().getPrevious());
                        methodNode.instructions.remove(currentInsn.getPrevious().getPrevious());
                        methodNode.instructions.remove(currentInsn.getPrevious());
                        methodNode.instructions.remove(currentInsn);
                        isSuccessful = true;
                        break;
                    }
                }
                break;
            }
        }
        return isSuccessful;
    }
}
