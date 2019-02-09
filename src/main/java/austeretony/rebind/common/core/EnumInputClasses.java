package austeretony.rebind.common.core;

import java.util.Iterator;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public enum EnumInputClasses {

    FORGE_FML_CLIENT_HANDLER("Forge", "FMLClientHandler", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),

    MC_KEY_BINDING("Minecraft", "KeyBinding", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),
    MC_GUI_KEY_BINDING_LIST("Minecraft", "GuiKeyBindingList", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),
    MC_MINECRAFT("Minecraft", "Minecraft", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),
    MC_GUI_SCREEN("Minecraft", "GuiScreen", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),
    MC_GUI_CONTAINER("Minecraft", "GuiContainer", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),    
    MC_ENTITY_PLAYER_SP("Minecraft", "EntityPlayerSP", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),

    CONTROLING_GUI_NEW_KEY_BINDING_LIST("Controling", "GuiNewKeyBindingList", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

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
        case FORGE_FML_CLIENT_HANDLER:
            return patchForgeFMLClientHandler(classNode);
        case MC_KEY_BINDING:
            return patchMCKeyBinding(classNode);
        case MC_GUI_KEY_BINDING_LIST:
            return patchMCGuiKeyBindingList(classNode);
        case MC_MINECRAFT:
            return patchMCMinecraft(classNode);
        case MC_GUI_SCREEN:
            return patchMCGuiScreen(classNode);
        case MC_GUI_CONTAINER:
            return patchMCGuiContainer(classNode);
        case MC_ENTITY_PLAYER_SP:
            return patchMCEntityPlayerSP(classNode);
        case CONTROLING_GUI_NEW_KEY_BINDING_LIST:
            return patchControlingGuiKeyBindingList(classNode);
        }
        return false;
    }

    private boolean patchForgeFMLClientHandler(ClassNode classNode) {
        boolean isSuccessful = false;
        AbstractInsnNode currentInsn;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("finishMinecraftLoading") && methodNode.desc.equals("()V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next();
                    if (currentInsn.getOpcode() == Opcodes.ICONST_0) {
                        InsnList nodesList = new InsnList();
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "removeHiddenKeyBindings", "()V", false));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "removeControlsSettings", "()V", false));
                        methodNode.instructions.insert(currentInsn.getNext(), nodesList);
                        isSuccessful = true;
                        break;
                    }
                }
                break;
            }
        }
        return isSuccessful;
    }

    private boolean patchMCKeyBinding(ClassNode classNode) {
        String 
        stringClassName = "java/lang/String",
        isKeyDownMethodName = ReBindCorePlugin.isObfuscated() ? "e" : "isKeyDown",
                isPressedMethodName = ReBindCorePlugin.isObfuscated() ? "g" : "isPressed",
                        isActiveAndMatchesMethodName = "isActiveAndMatches",
                        keyBindingClassName = ReBindCorePlugin.isObfuscated() ? "bhy" : "net/minecraft/client/settings/KeyBinding",
                                iKeyConflictContextClassName = "net/minecraftforge/client/settings/IKeyConflictContext",
                                keyConflictContextClassName = "net/minecraftforge/client/settings/KeyConflictContext",
                                keyModifierClassName = "net/minecraftforge/client/settings/KeyModifier";
        boolean 
        descChanged = false, 
        isSuccessful = false;
        AbstractInsnNode currentInsn;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("<init>") && methodNode.desc.equals("(L" + stringClassName + ";IL" + stringClassName + ";)V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next();
                    if (currentInsn.getOpcode() == Opcodes.ALOAD) {
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                        nodesList.add(new FieldInsnNode(Opcodes.GETSTATIC, keyConflictContextClassName, "UNIVERSAL", "L" + keyConflictContextClassName + ";"));
                        nodesList.add(new FieldInsnNode(Opcodes.GETSTATIC, keyModifierClassName, "NONE", "L" + keyModifierClassName + ";"));
                        nodesList.add(new VarInsnNode(Opcodes.ILOAD, 2));
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 3));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, keyBindingClassName, "<init>", "(L" + stringClassName + ";L" + iKeyConflictContextClassName + ";L" + keyModifierClassName + ";IL" + stringClassName + ";)V", false));
                        nodesList.add(new InsnNode(Opcodes.RETURN));
                        methodNode.instructions.insertBefore(currentInsn, nodesList);
                        break;
                    }
                }
            }
            if (methodNode.name.equals(isKeyDownMethodName) && methodNode.desc.equals("()Z")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next();
                    if (currentInsn.getOpcode() == Opcodes.ALOAD) {
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isKeyDown", "(L" + keyBindingClassName + ";)Z", false));
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
            }
            if (methodNode.name.equals("<init>") && methodNode.desc.equals("(L" + stringClassName + ";L" + iKeyConflictContextClassName + ";L" + keyModifierClassName + ";IL" + stringClassName + ";)V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next();
                    if (!descChanged && currentInsn.getOpcode() == Opcodes.INVOKESPECIAL) {
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "getKeyBindingName", "(L" + stringClassName + ";)L" + stringClassName + ";", false));
                        nodesList.add(new VarInsnNode(Opcodes.ASTORE, 1));
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 3));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "getKeyBindingKeyModifier", "(L" + keyModifierClassName + ";)L" + keyModifierClassName + ";", false));
                        nodesList.add(new VarInsnNode(Opcodes.ASTORE, 3));
                        nodesList.add(new VarInsnNode(Opcodes.ILOAD, 4));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "getKeyBindingKeyCode", "(I)I", false));
                        nodesList.add(new VarInsnNode(Opcodes.ISTORE, 4));
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 5));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "getKeyBindingCategory", "(L" + stringClassName + ";)L" + stringClassName + ";", false));
                        nodesList.add(new VarInsnNode(Opcodes.ASTORE, 5));
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
            if (methodNode.name.equals(isActiveAndMatchesMethodName) && methodNode.desc.equals("(I)Z")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next();
                    if (currentInsn.getOpcode() == Opcodes.ALOAD) {
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        nodesList.add(new VarInsnNode(Opcodes.ILOAD, 1));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isActiveAndMatches", "(L" + keyBindingClassName + ";I)Z", false));
                        nodesList.add(new InsnNode(Opcodes.IRETURN));
                        methodNode.instructions.insertBefore(currentInsn, nodesList);
                        break;
                    }
                }
            }
        }
        return isSuccessful;
    }


    private boolean patchMCGuiKeyBindingList(ClassNode classNode) {
        String keyBindingClassName = ReBindCorePlugin.isObfuscated() ? "bhy" : "net/minecraft/client/settings/KeyBinding";
        boolean isSuccessful = false;
        AbstractInsnNode currentInsn;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("<init>")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next();
                    if (currentInsn.getOpcode() == Opcodes.ACONST_NULL) {
                        InsnList nodesList = new InsnList();
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "sortKeyBindings", "()[L" + keyBindingClassName + ";", false));
                        nodesList.add(new VarInsnNode(Opcodes.ASTORE, 3));
                        methodNode.instructions.insertBefore(currentInsn, nodesList);
                        isSuccessful = true;
                        break;
                    }
                }
                break;
            }
        }
        return isSuccessful;
    }

    private boolean patchMCMinecraft(ClassNode classNode) {
        String 
        runTickKeyboardMethodName = ReBindCorePlugin.isObfuscated() ? "aD" : "runTickKeyboard",
                runTickMouseMethodName = ReBindCorePlugin.isObfuscated() ? "aG" : "runTickMouse",
                        dispatchKeypressesMethodName = ReBindCorePlugin.isObfuscated() ? "W" : "dispatchKeypresses";
        boolean isSuccessful = false;
        int 
        bipushCount = 0, 
        iconstCount = 0, 
        iloadCount = 0;
        AbstractInsnNode currentInsn;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(runTickKeyboardMethodName) && methodNode.desc.equals("()V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next();
                    if (currentInsn.getOpcode() == Opcodes.BIPUSH) {
                        bipushCount++;
                        if (bipushCount == 2 || bipushCount == 4 || bipushCount == 6 || bipushCount == 10) {
                            methodNode.instructions.insertBefore(currentInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "getDebugScreenKeyCode", "()I", false));
                            insnIterator.remove();
                            if (bipushCount == 10)
                                break;
                        }
                        if (bipushCount == 5) {
                            methodNode.instructions.insertBefore(currentInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "getSwitchShaderKeyCode", "()I", false));
                            insnIterator.remove();
                        }
                        if (bipushCount == 7) {
                            methodNode.instructions.insertBefore(currentInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isHideHUDKeyPressed", "(I)Z", false));
                            ((JumpInsnNode) currentInsn.getNext()).setOpcode(Opcodes.IFEQ);
                            insnIterator.remove();
                        }
                    }
                    if (currentInsn.getOpcode() == Opcodes.ICONST_1) {
                        iconstCount++;
                        if (iconstCount == 2) {
                            methodNode.instructions.insertBefore(currentInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isQuitKeyPressed", "(I)Z", false));
                            ((JumpInsnNode) currentInsn.getNext()).setOpcode(Opcodes.IFEQ);
                            insnIterator.remove();
                        }
                    }
                }
            }
            if (methodNode.name.equals(runTickMouseMethodName) && methodNode.desc.equals("()V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next();
                    if (currentInsn.getOpcode() == Opcodes.ILOAD) {
                        iloadCount++;
                        if (iloadCount == 8) {
                            InsnList nodesList = new InsnList();
                            nodesList.add(new VarInsnNode(Opcodes.ILOAD, 4));
                            nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isHotbarScrollingAllowed", "(I)I", false));
                            nodesList.add(new VarInsnNode(Opcodes.ISTORE, 4));
                            methodNode.instructions.insertBefore(currentInsn, nodesList);
                            break;
                        }
                    }
                }
            }
            if (methodNode.name.equals(dispatchKeypressesMethodName) && methodNode.desc.equals("()V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next();
                    if (currentInsn.getOpcode() == Opcodes.BIPUSH) {
                        InsnList nodesList = new InsnList();
                        methodNode.instructions.insertBefore(currentInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isNarratorKeyPressed", "(I)Z", false));
                        methodNode.instructions.remove(currentInsn.getNext().getNext());
                        methodNode.instructions.remove(currentInsn.getNext());
                        insnIterator.remove();
                        isSuccessful = true;
                        break;
                    }
                }
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
                        isSuccessful = true;
                        break;
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
        String targetMethodName = ReBindCorePlugin.isObfuscated() ? "n" : "onLivingUpdate";
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
                        if (iconstCount == 9) {
                            methodNode.instructions.insert(currentInsn.getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isDoubleTapForwardSprintAllowed", "()Z", false));
                            insnIterator.remove();
                        }
                        if (iconstCount == 10) {
                            methodNode.instructions.insert(currentInsn.getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isPlayerSprintAllowed", "()Z", false));
                            insnIterator.remove();
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

    private boolean patchControlingGuiKeyBindingList(ClassNode classNode) {
        return patchMCGuiKeyBindingList(classNode);
    }
}
