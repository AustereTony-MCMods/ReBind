package ru.austeretony.rebind.coremod;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class ReBindClassTransformer implements IClassTransformer {

	public static final Logger LOGGER = LogManager.getLogger("ReBind");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {    	
    	
    	switch (name) {
    	
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

	private byte[] patchMinecraft(byte[] basicClass, boolean obfuscated) {
		        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String targetMethodName = obfuscated ? "az" : "runTickKeyboard";
	 	
        int 
        bipushCount = 0,
        iconstCount = 0;
                
        LOGGER.info("<Minecraft> transformation started...");   
        
		for (MethodNode methodNode : classNode.methods) {
			
			if (methodNode.name.equals(targetMethodName) && methodNode.desc.equals("()V")) {
				
		        LOGGER.info("Target method found.");   
								
                AbstractInsnNode currentNode = null;
                
                Iterator<AbstractInsnNode> iteratorNode = methodNode.instructions.iterator();
               
                while (iteratorNode.hasNext()) {
                	
                    currentNode = iteratorNode.next(); 
                    
                    if (currentNode.getOpcode() == Opcodes.BIPUSH) {
                    	
                    	bipushCount++;
                    	
                    	if (bipushCount == 2 || bipushCount == 4 || bipushCount == 6 || bipushCount == 10) {
                    		
                            methodNode.instructions.insert(currentNode.getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "getDebugMenuKeyCode", "()I", false)); 
                    		
                    		iteratorNode.remove();
                    		
                    		if (bipushCount == 10) {
                    			
                    			break; 
                    		}
                    	}
                    	
                    	if (bipushCount == 5) {
                    		
                            methodNode.instructions.insert(currentNode.getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "getSwitchShaderKeyCode", "()I", false)); 
                    		
                    		iteratorNode.remove();
                    	}
                    	
                    	if (bipushCount == 7) {
                        	                                                   	
                            methodNode.instructions.insert(currentNode.getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "getHideHUDKeyCode", "()I", false)); 
                            
                            iteratorNode.remove();               
                        } 
                    }
                    
                    if (currentNode.getOpcode() == Opcodes.ICONST_1) {
                    	
                    	iconstCount++;
                    	
                    	if (iconstCount == 2) {
                    		
                            methodNode.instructions.insert(currentNode.getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "getQuitKeyCode", "()I", false)); 
                    		
                    		iteratorNode.remove();
                    	}
                    }
                }
				
				break;
			}
		}
		
	    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);	    
	    classNode.accept(writer);
	    
        LOGGER.info("<Minecraft> transformation successful!");   
        
        return writer.toByteArray();				
	}
	
	private byte[] patchGuiScreen(byte[] basicClass, boolean obfuscated, boolean isContainer) {
        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String targetMethodName = obfuscated ? "a" : "keyTyped";
                
	 	if (!isContainer)
	 		LOGGER.info("<GuiScreen> transformation started...");   
	 	else
	 		LOGGER.info("<GuiContainer> transformation started...");   
        
		for (MethodNode methodNode : classNode.methods) {
			
			if (methodNode.name.equals(targetMethodName) && methodNode.desc.equals("(CI)V")) {
				
		        LOGGER.info("Target method found.");   
								
                AbstractInsnNode currentNode = null;
                
                Iterator<AbstractInsnNode> iteratorNode = methodNode.instructions.iterator();
               
                while (iteratorNode.hasNext()) {
                	
                    currentNode = iteratorNode.next(); 
                    
                    if (currentNode.getOpcode() == Opcodes.ICONST_1) {
                    		
                        methodNode.instructions.insert(currentNode.getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "getQuitKeyCode", "()I", false)); 
                    		
                    	iteratorNode.remove();
                    	
                    	break;
                    }
                }
				
				break;
			}
		}
		
	    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);	    
	    classNode.accept(writer);
	    
	    if (!isContainer)
	    	LOGGER.info("<GuiScreen> transformation successful!");   
	    else
	    	LOGGER.info("<GuiContainer> transformation successful!");   
        
        return writer.toByteArray();				
	}
}
