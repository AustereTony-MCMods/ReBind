package ru.austeretony.rebind.core;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
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

import com.google.gson.JsonSyntaxException;

import net.minecraft.launchwrapper.IClassTransformer;
import ru.austeretony.rebind.config.ConfigLoader;

public class ReBindClassTransformer implements IClassTransformer {

	public static final Logger CORE_LOGGER = LogManager.getLogger("ReBind Core");
	
	private static final String HOOKS_CLASS = "ru/austeretony/rebind/core/ReBindHooks";

	public ReBindClassTransformer() {
		
		try {
			
			ConfigLoader.loadConfiguration();
		}
		
		catch (JsonSyntaxException exception) {
			
			CORE_LOGGER.error("Config parsing failure! This will cause mess up in controls. Fix syntax errors!");
			
			exception.printStackTrace();
		}
	}
	 
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {    	
    	
    	switch (transformedName) {
    	
			case "net.minecraft.client.settings.GameSettings":							
				return patchGameSettings(basicClass);
			case "net.minecraft.client.settings.KeyBinding":		
				return patchKeyBinding(basicClass);
			case "net.minecraft.client.gui.GuiKeyBindingList":							
				return patchGuiKeyBindingList(basicClass);					
			case "net.minecraft.client.gui.GuiKeyBindingList$KeyEntry":								
				return patchKeyEntry(basicClass);					
			case "net.minecraft.client.gui.GuiControls":		
				return patchGuiControls(basicClass);							
			case "net.minecraft.client.Minecraft":							
	    		return patchMinecraft(basicClass);			
			case "net.minecraft.client.gui.GuiScreen":							
	    		return patchGui(basicClass, true);			
			case "net.minecraft.client.gui.inventory.GuiContainer":							
	    		return patchGui(basicClass, false);	
			case "net.minecraft.client.entity.EntityPlayerSP":							
	    		return patchEntityPlayerSP(basicClass);
    	}
    	
		return basicClass;
    }
    
	private byte[] patchGameSettings(byte[] basicClass) {
		
	    ClassNode classNode = new ClassNode();
	    ClassReader classReader = new ClassReader(basicClass);
	    classReader.accept(classNode, 0);
	    
	 	String 
	 	loadOptionsMethodName = ReBindCorePlugin.isObfuscated() ? "a" : "loadOptions",
	 	saveOptionsMethodName = ReBindCorePlugin.isObfuscated() ? "b" : "saveOptions",
	    stringClassName = "java/lang/String",
	    keyBindingClassName = ReBindCorePlugin.isObfuscated() ? "avb" : "net/minecraft/client/settings/KeyBinding",
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

	    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);	    
	    classNode.accept(writer);
	    
	    if (isSuccessful)
	    	CORE_LOGGER.info("<GameSettings.class> patched!");   
	    	    
	    return writer.toByteArray();	
	}
	
	private byte[] patchKeyBinding(byte[] basicClass) {
        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String 
	 	onTickMethodName = ReBindCorePlugin.isObfuscated() ? "a" : "onTick",
	 	setKeyBindStateMethodName = ReBindCorePlugin.isObfuscated() ? "a" : "setKeyBindState",
	    isKeyDownMethodName = ReBindCorePlugin.isObfuscated() ? "d" : "isKeyDown",
	 	isPressedMethodName = ReBindCorePlugin.isObfuscated() ? "f" : "isPressed",
	 	stringClassName = "java/lang/String",
	 	keyBindingClassName = ReBindCorePlugin.isObfuscated() ? "avb" : "net/minecraft/client/settings/KeyBinding";

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
                
                
                isSuccessful = true;
                
				break;
			}
		}
		
	    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);	    
	    classNode.accept(writer);
	    
	    if (isSuccessful)
	    	CORE_LOGGER.info("<KeyBinding.class> patched!");   
	            
        return writer.toByteArray();				
	}
	
	private byte[] patchGuiKeyBindingList(byte[] basicClass) {
        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String keyBindingClassName = ReBindCorePlugin.isObfuscated() ? "avb" : "net/minecraft/client/settings/KeyBinding";
        
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
                    
                    	break;
                    }
                }
                
                isSuccessful = true;
                
				break;
			}
		}
		
	    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);	    
	    classNode.accept(writer);
	    
	    if (isSuccessful)
	    	CORE_LOGGER.info("<GuiKeyBindingList.class> patched!");  
			            
        return writer.toByteArray();				
	}
	
	private byte[] patchKeyEntry(byte[] basicClass) {
				        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String
	 	changeButtonFieldName = ReBindCorePlugin.isObfuscated() ? "d" : "btnChangeKeyBinding",
	 	resetButtonFieldName = ReBindCorePlugin.isObfuscated() ? "e" : "btnReset",
	 	keyBindingFieldName = ReBindCorePlugin.isObfuscated() ? "b" : "keybinding",
	 	drawEntryMethodName = ReBindCorePlugin.isObfuscated() ? "a" : "drawEntry",
	    mousePressedMethodName = ReBindCorePlugin.isObfuscated() ? "a" : "mousePressed",
	 	keyEntryClassName = ReBindCorePlugin.isObfuscated() ? "ayi$b" : "net/minecraft/client/gui/GuiKeyBindingList$KeyEntry",
	    guiButtonClassName = ReBindCorePlugin.isObfuscated() ? "avs" : "net/minecraft/client/gui/GuiButton",
	 	keyBindingClassName = ReBindCorePlugin.isObfuscated() ? "avb" : "net/minecraft/client/settings/KeyBinding";
	 	
        boolean isSuccessful = false;
        
        int
        ifeqCount = 0,
        aloadCount = 0;
        
        AbstractInsnNode currentInsn;
                        
		for (MethodNode methodNode : classNode.methods) {
			
			if (methodNode.name.equals(drawEntryMethodName) && methodNode.desc.equals("(IIIIIIIZ)V")) {
																                
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
                        	nodesList.add(new VarInsnNode(Opcodes.ILOAD, 9));
                        	nodesList.add(new VarInsnNode(Opcodes.ILOAD, 2));
                        	nodesList.add(new VarInsnNode(Opcodes.ILOAD, 3));
                        	nodesList.add(new VarInsnNode(Opcodes.ILOAD, 6));
                        	nodesList.add(new VarInsnNode(Opcodes.ILOAD, 7));
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
                    	}
                    }
                }
                
                isSuccessful = true;
                
				break;
			}
		}
		
	    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);	    
	    classNode.accept(writer);
	    
	    if (isSuccessful)
	    	CORE_LOGGER.info("<GuiKeyBindingList.KeyEntry.class> patched!");  
	            
        return writer.toByteArray();				
	}
    
	private byte[] patchGuiControls(byte[] basicClass) {
        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String 
	 	buttonIdFieldName = ReBindCorePlugin.isObfuscated() ? "f" : "buttonId",
	 	resetButtonFieldName = ReBindCorePlugin.isObfuscated() ? "t" : "buttonReset",
	 	actionPerformedMethodName = ReBindCorePlugin.isObfuscated() ? "a" : "actionPerformed",
	 	mouseClickedMethodName = ReBindCorePlugin.isObfuscated() ? "a" : "mouseClicked",
	 	keyTypedMethodName = ReBindCorePlugin.isObfuscated() ? "a" : "keyTyped",
	 	drawScreenMethodName = ReBindCorePlugin.isObfuscated() ? "a" : "drawScreen",
	 	guiScreenClassName = ReBindCorePlugin.isObfuscated() ? "axu" : "net/minecraft/client/gui/GuiScreen",
	 	guiButtonClassName = ReBindCorePlugin.isObfuscated() ? "avs" : "net/minecraft/client/gui/GuiButton",
	 	keyModifierClassName = "ru/austeretony/rebind/main/EnumKeyModifier",
	 	guiControlsClassName = ReBindCorePlugin.isObfuscated() ? "ayj" : "net/minecraft/client/gui/GuiControls",
	 	keyBindingClassName = ReBindCorePlugin.isObfuscated() ? "avb" : "net/minecraft/client/settings/KeyBinding";
	 	
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
                    	}   
                    	
                    	if (aloadCount == 6) {
                    		
                        	InsnList nodesList = new InsnList();
                        	
                        	nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        	nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, guiControlsClassName, buttonIdFieldName, "L" + keyBindingClassName + ";"));
                        	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, keyModifierClassName, "getActiveModifier", "()L" + keyModifierClassName + ";", false));
                        	nodesList.add(new VarInsnNode(Opcodes.ILOAD, 1));                       	
                        	nodesList.add(new IntInsnNode(Opcodes.BIPUSH, 256));
                        	nodesList.add(new InsnNode(Opcodes.IADD));                       	
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
		
	    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);	    
	    classNode.accept(writer);
	    
	    if (isSuccessful)
	    	CORE_LOGGER.info("<GuiControls.class> patched!");   
        
        return writer.toByteArray();				
	}
	
	private byte[] patchMinecraft(byte[] basicClass) {
		        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String runTickMethodName = ReBindCorePlugin.isObfuscated() ? "s" : "runTick";
	 	
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
                    	
                    	if (ifeqCount == 9) {
                    		
	                        InsnList nodesList = new InsnList();
	                    	                    	
	                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isHotbarScrollingAllowed", "()Z", false));          
	                        nodesList.add(new JumpInsnNode(Opcodes.IFEQ, ((JumpInsnNode) currentInsn).label));
	                        
	                        methodNode.instructions.insertBefore(currentInsn.getPrevious().getPrevious().getPrevious(), nodesList); 
	                    	
	                    	first = true;
                    	}
                    }
                    
                    if (currentInsn.getOpcode() == Opcodes.BIPUSH) {
                    	
                    	bipushCount++;
                    	
                    	if (bipushCount == 4 || bipushCount == 6 || bipushCount == 9 || bipushCount == 11 || bipushCount == 13 || bipushCount == 15 || bipushCount == 17 || bipushCount == 19 || bipushCount == 21 || bipushCount == 23 || bipushCount == 25 || bipushCount == 27 || bipushCount == 29 || bipushCount == 31 || bipushCount == 33 || bipushCount == 35) {
                    		
                            methodNode.instructions.insertBefore(currentInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "getDebugScreenKeyCode", "()I", false)); 
                    		
                    		insnIterator.remove();
                    		
                    		if (bipushCount == 35) {
                    			
                    			break; 
                    		}
                    	}
                    	
                    	if (bipushCount == 7) {
                    		
                            methodNode.instructions.insertBefore(currentInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "getDisableShaderKeyCode", "()I", false)); 
                    		
                    		insnIterator.remove();
                    	}
                    	                   	
                    	if (bipushCount == 34) {
                        	                                                   	
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
		
	    ClassWriter writer = new ClassWriter(0);	    
	    classNode.accept(writer);
	    
	    if (isSuccessful)
	    	CORE_LOGGER.info("<Minecraft.class> patched!");
	            
        return writer.toByteArray();				
	}
	
	private byte[] patchGui(byte[] basicClass, boolean flag) {
        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
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
		
	    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);	    
	    classNode.accept(writer);
	    
	    if (isSuccessful) {
	    	
	    	if (flag)
	    		CORE_LOGGER.info("<GuiScreen.class> patched!");   
	    	else
	    		CORE_LOGGER.info("<GuiScreen.class> patched!");   
	    }
        
        return writer.toByteArray();				
	}
	
	private byte[] patchEntityPlayerSP(byte[] basicClass) {
        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String targetMethodName = ReBindCorePlugin.isObfuscated() ? "m" : "onLivingUpdate";  
	 	
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
		
	    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);	    
	    classNode.accept(writer);
	    
	    if (isSuccessful)
	    	CORE_LOGGER.info("<EntityPlayerSP.class> patched!");   
        
        return writer.toByteArray();				
	}
}
