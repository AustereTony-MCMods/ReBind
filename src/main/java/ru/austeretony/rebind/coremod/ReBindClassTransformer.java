package ru.austeretony.rebind.coremod;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class ReBindClassTransformer implements IClassTransformer {

	public static final Logger LOGGER = LogManager.getLogger("ReBind Core");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {    	
    	
    	switch (name) {
    	
    		case "beu":					
				return patchGameSettings(basicClass, true);

			case "net.minecraft.client.settings.GameSettings":							
				return patchGameSettings(basicClass, false);
				
			case "bid":					
				return patchGuiKeyBindingList(basicClass, true);

			case "net.minecraft.client.gui.GuiKeyBindingList":							
				return patchGuiKeyBindingList(basicClass, false);	
    	
			case "bes":					
				return patchMinecraft(basicClass, true);
		
			case "net.minecraft.client.Minecraft":							
	    		return patchMinecraft(basicClass, false);	
	    		
			case "bho":					
				return patchGuiScreen(basicClass, true, false);
		
			case "net.minecraft.client.gui.GuiScreen":							
	    		return patchGuiScreen(basicClass, false, false);	
	    		
			case "big":					
				return patchGuiScreen(basicClass, true, true);
		
			case "net.minecraft.client.gui.inventory.GuiContainer":							
	    		return patchGuiScreen(basicClass, false, true);	
    	}
    	
		return basicClass;
    }
	
	private byte[] patchGameSettings(byte[] basicClass, boolean obfuscated) {
		
	    ClassNode classNode = new ClassNode();
	    ClassReader classReader = new ClassReader(basicClass);
	    classReader.accept(classNode, 0);
	    
	 	String 
	 	targetMethodName = obfuscated ? "a" : "loadOptions",
	 	nbtTagCompoundClassName = obfuscated ? "du" : "net/minecraft/nbt/NBTTagCompound";
	 	
	    int getfieldCount = 0;
	    
        boolean isSuccessful = false;
	 		    
	    for (MethodNode methodNode : classNode.methods) {
	    	
			if (methodNode.name.equals(targetMethodName) && methodNode.desc.equals("()V")) {
												
	            AbstractInsnNode currentInsn = null;
	            
	            Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
	           
	            while (insnIterator.hasNext()) {
	            	
	                currentInsn = insnIterator.next(); 
	                	                
	                if (currentInsn.getOpcode() == Opcodes.GETFIELD) {
	                		                	
	                	getfieldCount++;
	               	                	
	                	if (getfieldCount == 6) {
	          		
	                    	InsnList nodesList = new InsnList();
	                    	
	                    	nodesList.add(new VarInsnNode(Opcodes.ALOAD, 2));
	                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "loadControlsFromOptionsFile", "(L" + nbtTagCompoundClassName + ";)Z", false));
	                        nodesList.add(new JumpInsnNode(Opcodes.IFEQ, ((JumpInsnNode) currentInsn.getNext().getNext().getNext().getNext().getNext().getNext().getNext().getNext().getNext().getNext().getNext()).label));
	                    	
	                        methodNode.instructions.insertBefore(currentInsn.getPrevious(), nodesList); 	                     
	                        
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
	    LOGGER.info("<GameSettings.class> transformed!");   
	    	    
	    return writer.toByteArray();	
	}
	
	private byte[] patchGuiKeyBindingList(byte[] basicClass, boolean obfuscated) {
        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String 
	 	guiKeyBindingsListClassName = obfuscated ? "bid" : "net/minecraft/client/gui/GuiKeyBindingList",
	 	keyBindingClassName = obfuscated ? "bep" : "net/minecraft/client/settings/KeyBinding",
	 	guiControlsClassName = obfuscated ? "bie" : "net/minecraft/client/gui/GuiControls",
	 	minecraftClassName = obfuscated ? "bes" : "net/minecraft/client/Minecraft";
        
        boolean isSuccessful = false;
                        
		for (MethodNode methodNode : classNode.methods) {
			
			if (methodNode.desc.equals("(L" + guiControlsClassName + ";L" + minecraftClassName + ";)V")) {
												
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
	    
	    if (isSuccessful)
        LOGGER.info("<GuiKeyBindingList.class> transformed!");   
	            
        return writer.toByteArray();				
	}

	private byte[] patchMinecraft(byte[] basicClass, boolean obfuscated) {
		        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String targetMethodName = obfuscated ? "az" : "runTickKeyboard";
	 	
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
