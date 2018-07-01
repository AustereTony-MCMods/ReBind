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
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;
import ru.austeretony.rebind.main.ReBindMain;

public class ReBindClassTransformer implements IClassTransformer {

	public static final Logger LOGGER = LogManager.getLogger("ReBind Core");
	
	public ReBindClassTransformer() {
				
		ReBindMain.CONFIG_LOADER.loadConfiguration();
	}

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {    	
    	
    	switch (name) { 	
    	
    		case "net.minecraftforge.fml.client.FMLClientHandler":									
    			return patchFMLClientHandler(basicClass);
				
				
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
				return patchGuiScreen(basicClass, true, false);
		
			case "net.minecraft.client.gui.GuiScreen":							
	    		return patchGuiScreen(basicClass, false, false);	
	    	
	    		
			case "bmg":					
				return patchGuiScreen(basicClass, true, true);
		
			case "net.minecraft.client.gui.inventory.GuiContainer":							
	    		return patchGuiScreen(basicClass, false, true);	
    	}
    	
		return basicClass;
    }
    
	private byte[] patchFMLClientHandler(byte[] basicClass) {
		
	    ClassNode classNode = new ClassNode();
	    ClassReader classReader = new ClassReader(basicClass);
	    classReader.accept(classNode, 0);
	 		    
        boolean isSuccessful = false;
	 		    
	    for (MethodNode methodNode : classNode.methods) {
	    	
			if (methodNode.name.equals("finishMinecraftLoading") && methodNode.desc.equals("()V")) {
												
	            AbstractInsnNode currentInsn = null;
	            
	            Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
	           
	            while (insnIterator.hasNext()) {
	            	
	                currentInsn = insnIterator.next(); 
	                
	                if (currentInsn.getOpcode() == Opcodes.ICONST_0) {
	                	
                    	InsnList nodesList = new InsnList();
                
                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "removeHiddenKeyBindings", "()V", false));
                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "rewriteControlsSettings", "()V", false));

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
	    LOGGER.info("<FMLClientHandler.class> transformed!");   
	    	    
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
                        
		for (MethodNode methodNode : classNode.methods) {
			
			if (methodNode.name.equals("<init>") && methodNode.desc.equals("(L" + stringClassName + ";IL" + stringClassName + ";)V")) {
												
                AbstractInsnNode currentInsn = null;
                
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
				
                AbstractInsnNode currentInsn = null;
                
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
               
                while (insnIterator.hasNext()) {
                	
                    currentInsn = insnIterator.next(); 
                    
                    if (!descChanged && currentInsn.getOpcode() == Opcodes.INVOKESPECIAL) {                   	
                    	
                    	InsnList nodesList = new InsnList();
                    	
                    	nodesList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "getKeyBindingName", "(L" + stringClassName + ";)L" + stringClassName + ";", false));
                    	nodesList.add(new VarInsnNode(Opcodes.ASTORE, 1));    
                    	
                    	nodesList.add(new VarInsnNode(Opcodes.ALOAD, 3));
                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "getKeyBindingKeyModifier", "(L" + keyModifierClassName + ";)L" + keyModifierClassName + ";", false));
                    	nodesList.add(new VarInsnNode(Opcodes.ASTORE, 3));
                    	
                    	nodesList.add(new VarInsnNode(Opcodes.ILOAD, 4));
                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "getKeyBindingKeyCode", "(I)I", false));
                    	nodesList.add(new VarInsnNode(Opcodes.ISTORE, 4));
                    	
                    	nodesList.add(new VarInsnNode(Opcodes.ALOAD, 5));
                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "getKeyBindingCategory", "(L" + stringClassName + ";)L" + stringClassName + ";", false));
                    	nodesList.add(new VarInsnNode(Opcodes.ASTORE, 5));                                
                    	
                    	methodNode.instructions.insert(currentInsn, nodesList); 
                    
                    	descChanged = true;
                    }
                    
                    if (currentInsn.getOpcode() == Opcodes.RETURN) {    
                    	
                    	InsnList nodesList = new InsnList();

                    	nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "storeKeybinding", "(L" + keyBindingClassName + ";)V", false));
                    	
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
        LOGGER.info("<KeyBinding.class> transformed!");   
	            
        return writer.toByteArray();				
	}
	
	private byte[] patchGuiKeyBindingList(byte[] basicClass, boolean obfuscated, boolean flag) {
        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String keyBindingClassName = obfuscated ? "bhy" : "net/minecraft/client/settings/KeyBinding";
        
        boolean isSuccessful = false;
                        
		for (MethodNode methodNode : classNode.methods) {
			
			if (methodNode.name.equals("<init>")) {
												
                AbstractInsnNode currentInsn = null;
                
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
               
                while (insnIterator.hasNext()) {
                	
                    currentInsn = insnIterator.next(); 
                    
                    if (currentInsn.getOpcode() == Opcodes.ACONST_NULL) {                   	
                    	
                    	InsnList nodesList = new InsnList();
                    	
                    	nodesList.add(new VarInsnNode(Opcodes.ALOAD, 3));
                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "sortKeyBindings", "([L" + keyBindingClassName + ";)[L" + keyBindingClassName + ";", false));
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
	    	LOGGER.info("<GuiKeyBindingList.class> transformed!");  
	    	else
		    LOGGER.info("<GuiNewKeyBindingList.class> transformed!");  
		}
	            
        return writer.toByteArray();				
	}

	private byte[] patchMinecraft(byte[] basicClass, boolean obfuscated) {
		        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String targetMethodName = obfuscated ? "aD" : "runTickKeyboard";
	 	
        int 
        bipushCount = 0,
        iconstCount = 0;
        
        boolean isSuccessful = false;
                        
		for (MethodNode methodNode : classNode.methods) {
			
			if (methodNode.name.equals(targetMethodName) && methodNode.desc.equals("()V")) {
												
                AbstractInsnNode currentInsn = null;
                
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
               
                while (insnIterator.hasNext()) {
                	
                    currentInsn = insnIterator.next(); 
                    
                    if (currentInsn.getOpcode() == Opcodes.BIPUSH) {
                    	
                    	bipushCount++;
                    	
                    	if (bipushCount == 2 || bipushCount == 4 || bipushCount == 6 || bipushCount == 10) {
                    		
                            methodNode.instructions.insert(currentInsn.getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "getDebugMenuKeyCode", "()I", false)); 
                    		
                    		insnIterator.remove();
                    		
                    		if (bipushCount == 10) {
                    			
                    			break; 
                    		}
                    	}
                    	
                    	if (bipushCount == 5) {
                    		
                            methodNode.instructions.insert(currentInsn.getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "getSwitchShaderKeyCode", "()I", false)); 
                    		
                    		insnIterator.remove();
                    	}
                    	
                    	if (bipushCount == 7) {
                        	                                                   	
                            methodNode.instructions.insert(currentInsn.getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "getHideHUDKeyCode", "()I", false)); 
                            
                            insnIterator.remove();               
                        } 
                    }
                    
                    if (currentInsn.getOpcode() == Opcodes.ICONST_1) {
                    	
                    	iconstCount++;
                    	
                    	if (iconstCount == 2) {
                    		
                            methodNode.instructions.insert(currentInsn.getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "getQuitKeyCode", "()I", false)); 
                    		
                    		insnIterator.remove();
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
        LOGGER.info("<Minecraft.class> transformed!");   
	            
        return writer.toByteArray();				
	}
	
	private byte[] patchGuiScreen(byte[] basicClass, boolean obfuscated, boolean isContainer) {
        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String targetMethodName = obfuscated ? "a" : "keyTyped"; 
	 	
        boolean isSuccessful = false;
        
		for (MethodNode methodNode : classNode.methods) {
			
			if (methodNode.name.equals(targetMethodName) && methodNode.desc.equals("(CI)V")) {
												
                AbstractInsnNode currentInsn = null;
                
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
               
                while (insnIterator.hasNext()) {
                	
                    currentInsn = insnIterator.next(); 
                    
                    if (currentInsn.getOpcode() == Opcodes.ICONST_1) {
                    		
                        methodNode.instructions.insert(currentInsn.getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "getQuitKeyCode", "()I", false)); 
                    		
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

	    	if (!isContainer) 
	    	LOGGER.info("<GuiScreen.class> transformed!");   
	    	else
	    	LOGGER.info("<GuiContainer.class> transformed!"); 
	    }

        return writer.toByteArray();				
	}
}
