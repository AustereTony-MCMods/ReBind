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
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class ReBindClassTransformer implements IClassTransformer {

	public static final Logger LOGGER = LogManager.getLogger("ReBind");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {    	
    	
    	switch (name) {
    	
			case "bbj":					
				return patchGameSettings(basicClass, true);

			case "net.minecraft.client.settings.GameSettings":							
				return patchGameSettings(basicClass, false);
    	
			case "bao":					
				return patchMinecraft(basicClass, true);
		
			case "net.minecraft.client.Minecraft":							
	    		return patchMinecraft(basicClass, false);	
	    		
			case "bdw":					
				return patchGuiScreen(basicClass, true, false);
		
			case "net.minecraft.client.gui.GuiScreen":							
	    		return patchGuiScreen(basicClass, false, false);	
	    		
			case "bex":					
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
	    optionsFileFieldName = obfuscated ? "aX" : "optionsFile",
	    gameSettingsClassName = obfuscated ? "bbj" : "net/minecraft/client/settings/GameSettings",
	 	fileClassName = "java/io/File";
	 		    
        boolean isSuccessful = false;
	 		    
	    for (MethodNode methodNode : classNode.methods) {
	    	
			if (methodNode.name.equals(targetMethodName) && methodNode.desc.equals("()V")) {
												
	            AbstractInsnNode currentInsn = null;
	            
	            Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
	           
	            while (insnIterator.hasNext()) {
	            	
	                currentInsn = insnIterator.next(); 
	                	                
	                if (currentInsn.getOpcode() == Opcodes.IF_ICMPGE) {
	          		
                    	InsnList nodesList = new InsnList();
                    	
                    	nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    	nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, gameSettingsClassName, optionsFileFieldName, "L" + fileClassName + ";"));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "loadOptionsControls", "(L" + fileClassName + ";)Z", false));
                        nodesList.add(new JumpInsnNode(Opcodes.IFEQ, ((JumpInsnNode) currentInsn).label));
                    	
                        methodNode.instructions.insertBefore(currentInsn.getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious(), nodesList); 
                        	                        
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
	    LOGGER.info("<GameSettings.class> transformed!");   
	    	    
	    return writer.toByteArray();	
	}
    
	private byte[] patchMinecraft(byte[] basicClass, boolean obfuscated) {
		        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String targetMethodName = obfuscated ? "p" : "runTick";
	 	
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
                    	
                    	if (bipushCount == 4 || bipushCount == 6 || bipushCount == 9 || bipushCount == 11 || bipushCount == 13 || bipushCount == 17 || bipushCount == 19 || bipushCount == 21 || bipushCount == 23 || bipushCount == 25) {
                    		
                            methodNode.instructions.insert(currentInsn.getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "getDebugMenuKeyCode", "()I", false)); 
                    		
                    		insnIterator.remove();
                    		
                    		if (bipushCount == 25) {
                    			
                    			break; 
                    		}
                    	}
                    	
                    	if (bipushCount == 7) {
                    		
                            methodNode.instructions.insert(currentInsn.getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "getDisableShaderKeyCode", "()I", false)); 
                    		
                    		insnIterator.remove();
                    	}
                    	                   	
                    	if (bipushCount == 24) {
                        	                                                   	
                            methodNode.instructions.insert(currentInsn.getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "getHideHUDKeyCode", "()I", false)); 
                            
                            insnIterator.remove();               
                        } 
                    }                                                                       
                    
                    if (currentInsn.getOpcode() == Opcodes.ICONST_1) {
                    	
                    	iconstCount++;
                    	
                    	if (iconstCount == 4) {
                    		
                            methodNode.instructions.insert(currentInsn.getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "getQuitKeyCode", "()I", false)); 
                    		
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
		
	    ClassWriter writer = new ClassWriter(0);	    
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
