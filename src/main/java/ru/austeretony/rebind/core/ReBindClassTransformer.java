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
    	
    		case "net.minecraftforge.fml.client.FMLClientHandler":									
    			return patchFMLClientHandler(basicClass);
			case "net.minecraft.client.settings.KeyBinding":		
				return patchKeyBinding(basicClass);
			case "net.minecraft.client.gui.GuiKeyBindingList":							
				return patchGuiKeyBindingList(basicClass, false);
			case "us.getfluxed.controlsearch.client.gui.GuiNewKeyBindingList":							
				return patchGuiKeyBindingList(basicClass, true);	
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
    
	private byte[] patchFMLClientHandler(byte[] basicClass) {
		
	    ClassNode classNode = new ClassNode();
	    ClassReader classReader = new ClassReader(basicClass);
	    classReader.accept(classNode, 0);
	 		    
        boolean isSuccessful = false;
        
        int iconstCount = 0;
        
        AbstractInsnNode currentInsn;
	 		    
	    for (MethodNode methodNode : classNode.methods) {
	    	
			if (methodNode.name.equals("finishMinecraftLoading") && methodNode.desc.equals("()V")) {
													            
	            Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
	           
	            while (insnIterator.hasNext()) {
	            	
	                currentInsn = insnIterator.next(); 
	                
	                if (currentInsn.getOpcode() == Opcodes.ICONST_0) {
	                	
	                	iconstCount++;
	                	
	                	if (iconstCount == 3) {
	                	
	                    	InsnList nodesList = new InsnList();
	                
	                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "removeHiddenKeyBindings", "()V", false));
	                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "removeControlsSettings", "()V", false));
	
	                        methodNode.instructions.insert(currentInsn.getNext(), nodesList); 
	                        
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
	    	CORE_LOGGER.info("<FMLClientHandler.class> patched!");   
	    	    
	    return writer.toByteArray();	
	}
	
	private byte[] patchKeyBinding(byte[] basicClass) {
        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String 
	 	stringClassName = "java/lang/String",
	 	isKeyDownMethodName = ReBindCorePlugin.isObfuscated() ? "e" : "isKeyDown",
	 	isPressedMethodName = ReBindCorePlugin.isObfuscated() ? "g" : "isPressed",
	 	isActiveAndMatchesMethodName = "isActiveAndMatches", 
	 	keyBindingClassName = ReBindCorePlugin.isObfuscated() ? "bca" : "net/minecraft/client/settings/KeyBinding",
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
		
	    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);	    
	    classNode.accept(writer);
	    
	    if (isSuccessful)
	    	CORE_LOGGER.info("<KeyBinding.class> patched!");   
	            
        return writer.toByteArray();				
	}
	
	private byte[] patchGuiKeyBindingList(byte[] basicClass, boolean flag) {
        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String keyBindingClassName = ReBindCorePlugin.isObfuscated() ? "bca" : "net/minecraft/client/settings/KeyBinding";
        
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
	    
	    if (isSuccessful) {
	    	
	    	if (!flag)
	    		CORE_LOGGER.info("<GuiKeyBindingList.class> patched!");  
	    	else
	    		CORE_LOGGER.info("<GuiNewKeyBindingList.class> patched!");  
		}
	            
        return writer.toByteArray();				
	}

	private byte[] patchMinecraft(byte[] basicClass) {
		        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String 
	 	runTickKeyboardMethodName = ReBindCorePlugin.isObfuscated() ? "az" : "runTickKeyboard",
	 	runTickMouseMethodName = ReBindCorePlugin.isObfuscated() ? "aB" : "runTickMouse";
	 	
        int 
        bipushCount = 0,
        iconstCount = 0,
        iloadCount = 0;
        
        boolean isSuccessful = false;
        
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
                            
                    		if (bipushCount == 10) {
                    			
                    			break; 
                    		}
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
                
                isSuccessful = true;
                
				break;
			}
		}
		
	    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);	    
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
	    		CORE_LOGGER.info("<GuiContainer.class> patched!");
	    }

        return writer.toByteArray();				
	}
	
	private byte[] patchEntityPlayerSP(byte[] basicClass) {
        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
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

