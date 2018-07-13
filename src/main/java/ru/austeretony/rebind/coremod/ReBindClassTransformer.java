package ru.austeretony.rebind.coremod;

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

import net.minecraft.launchwrapper.IClassTransformer;
import ru.austeretony.rebind.main.ReBindMain;

public class ReBindClassTransformer implements IClassTransformer {

	public static final Logger LOGGER = LogManager.getLogger("ReBind Core");
	
	private static final String HOOKS_CLASS = "ru/austeretony/rebind/coremod/ReBindHooks";
	
	public ReBindClassTransformer() {
				
		ReBindMain.CONFIG_LOADER.loadConfiguration();
	}

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {    	
    	
    	switch (name) { 	
    	
    		case "net.minecraftforge.fml.client.FMLClientHandler":									
    			return patchFMLClientHandler(basicClass, true);
				
				
			case "bhy":									
				return patchKeyBinding(basicClass, true);

			case "net.minecraft.client.settings.KeyBinding":		
				return patchKeyBinding(basicClass, false);
				
				
			case "bmd":					
				return patchGuiKeyBindingList(basicClass, true, false);

			case "net.minecraft.client.gui.GuiKeyBindingList":							
				return patchGuiKeyBindingList(basicClass, false, false);		

			case "us.getfluxed.controlsearch.client.gui.GuiNewKeyBindingList":							
				return patchGuiKeyBindingList(basicClass, false, true);
				
				
			case "bib":					
				return patchMinecraft(basicClass, true);
		
			case "net.minecraft.client.Minecraft":							
	    		return patchMinecraft(basicClass, false);	
	    	
	    		
			case "blk":					
				return patchGuiScreen(basicClass, true);
		
			case "net.minecraft.client.gui.GuiScreen":							
	    		return patchGuiScreen(basicClass, false);	
	    	
	    		
			case "bmg":					
				return patchGuiContainer(basicClass, true);
		
			case "net.minecraft.client.gui.inventory.GuiContainer":							
	    		return patchGuiContainer(basicClass, false);	
	    		
	    		
			case "bud":					
				return patchEntityPlayerSP(basicClass, true);
		
			case "net.minecraft.client.entity.EntityPlayerSP":							
	    		return patchEntityPlayerSP(basicClass, false);
    	}
    	
		return basicClass;
    }
    
	private byte[] patchFMLClientHandler(byte[] basicClass, boolean obfuscated) {
		
	    ClassNode classNode = new ClassNode();
	    ClassReader classReader = new ClassReader(basicClass);
	    classReader.accept(classNode, 0);
	 		    
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

	    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);	    
	    classNode.accept(writer);
	    
	    if (isSuccessful)
	    	LOGGER.info("<FMLClientHandler.class> patched!");   
	    	    
	    return writer.toByteArray();	
	}
	
	private byte[] patchKeyBinding(byte[] basicClass, boolean obfuscated) {
        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String 
	 	stringClassName = "java/lang/String",
	 	keyBindingClassName = obfuscated ? "bhy" : "net/minecraft/client/settings/KeyBinding",
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
                
                isSuccessful = true;
                
				break;
			}
		}
		
	    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);	    
	    classNode.accept(writer);
	    
	    if (isSuccessful)
	    	LOGGER.info("<KeyBinding.class> patched!");   
	            
        return writer.toByteArray();				
	}
	
	private byte[] patchGuiKeyBindingList(byte[] basicClass, boolean obfuscated, boolean flag) {
        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String keyBindingClassName = obfuscated ? "bhy" : "net/minecraft/client/settings/KeyBinding";
        
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
	    		LOGGER.info("<GuiKeyBindingList.class> patched!");  
	    	else
	    		LOGGER.info("<GuiNewKeyBindingList.class> patched!");  
		}
	            
        return writer.toByteArray();				
	}

	private byte[] patchMinecraft(byte[] basicClass, boolean obfuscated) {
		        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String 
	 	runTickKeyboardMethodName = obfuscated ? "aD" : "runTickKeyboard",
	 	runTickMouseMethodName = obfuscated ? "aG" : "runTickMouse",
	 	dispatchKeypressesMethodName = obfuscated ? "W" : "dispatchKeypresses";
	 	
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
	    	LOGGER.info("<Minecraft.class> patched!");   
	            
        return writer.toByteArray();				
	}
	
	private byte[] patchGuiScreen(byte[] basicClass, boolean obfuscated) {
        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String keyTypedMethodName = obfuscated ? "a" : "keyTyped"; 
	 	
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
	    
	    if (isSuccessful)
	    	LOGGER.info("<GuiScreen.class> patched!");   

        return writer.toByteArray();				
	}
	
	private byte[] patchGuiContainer(byte[] basicClass, boolean obfuscated) {
        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String 
	 	keyTypedMethodName = obfuscated ? "a" : "keyTyped",
	 	handleMouseClickMethodName = obfuscated ? "a" : "handleMouseClick",
	 	clickTypeClassName = obfuscated ? "afw" : "net/minecraft/inventory/ClickType",
	 	slotClassName = obfuscated ? "agr" : "net/minecraft/inventory/Slot";  
	 	
        boolean isSuccessful = false;
        
        int aloadCount = 0;
        
        AbstractInsnNode currentInsn;
        
		for (MethodNode methodNode : classNode.methods) {
			
			if (methodNode.name.equals(handleMouseClickMethodName) && methodNode.desc.equals("(L" + slotClassName + ";IIL" + clickTypeClassName + ";)V")) {
                
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
               
                while (insnIterator.hasNext()) {
                	
                    currentInsn = insnIterator.next(); 
                    
                    if (currentInsn.getOpcode() == Opcodes.ALOAD) {
                    	
                    	aloadCount++;
                    		
                    	if (aloadCount == 3) {
                    		
	                        InsnList nodesList = new InsnList();
	                    	
	                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 4));
	                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "verifyClickAction", "(L" + clickTypeClassName + ";)L" + clickTypeClassName + ";", false));          
	                        nodesList.add(new VarInsnNode(Opcodes.ASTORE, 4));
	                        
	                        methodNode.instructions.insertBefore(currentInsn, nodesList); 
	                    	
	                    	break;
                    	}
                    }
                }               
			}
			
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
	    
	    if (isSuccessful)
	    	LOGGER.info("<GuiContainer.class> patched!"); 

        return writer.toByteArray();				
	}
	
	private byte[] patchEntityPlayerSP(byte[] basicClass, boolean obfuscated) {
        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String targetMethodName = obfuscated ? "n" : "onLivingUpdate";  
	 	
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
	    	LOGGER.info("<EntityPlayerSP.class> patched!");   
        
        return writer.toByteArray();				
	}
}

