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
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.google.gson.JsonSyntaxException;

import net.minecraft.launchwrapper.IClassTransformer;
import ru.austeretony.rebind.main.ReBindMain;

public class ReBindClassTransformer implements IClassTransformer {

	public static final Logger LOGGER = LogManager.getLogger("ReBind Core");

	public ReBindClassTransformer() {
		
		try {
			
			ReBindMain.CONFIG_LOADER.loadConfiguration();
		}
		
		catch (JsonSyntaxException exception) {
			
			LOGGER.error("Config parsing failure! This will cause mess up in controls. Fix syntax errors!");
			
			exception.printStackTrace();
		}
	}
	
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {    	
    	
    	switch (name) {
    	
			case "bbj":					
				return patchGameSettings(basicClass, true);

			case "net.minecraft.client.settings.GameSettings":							
				return patchGameSettings(basicClass, false);
				
			
			case "bal":									
				return patchKeyBinding(basicClass, true);

			case "net.minecraft.client.settings.KeyBinding":		
				return patchKeyBinding(basicClass, false);
    	
			
			case "bes":					
				return patchGuiKeyBindingList(basicClass, true, false);

			case "net.minecraft.client.gui.GuiKeyBindingList":							
				return patchGuiKeyBindingList(basicClass, false, false);	
				
			case "us.getfluxed.controlsearch.client.gui.GuiNewKeyBindingList":							
				return patchGuiKeyBindingList(basicClass, true, true);
				
			
			case "bev":									
				return patchKeyEntry(basicClass, true, false);

			case "net.minecraft.client.gui.GuiKeyBindingList$KeyEntry":		
				return patchKeyEntry(basicClass, false, false);	
				
			case "us.getfluxed.controlsearch.client.gui.GuiNewKeyBindingList$KeyEntry":		
				return patchKeyEntry(basicClass, true, true);	
					
					
			case "bew":									
				return patchGuiControls(basicClass, true, false);

			case "net.minecraft.client.gui.GuiControls":		
				return patchGuiControls(basicClass, false, false);	
				
			case "us.getfluxed.controlsearch.client.gui.GuiNewControls":		
				return patchGuiControls(basicClass, true, true);	
				
			
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
	    		
	    		
			case "blk":					
				return patchEntityPlayerSP(basicClass, true);
		
			case "net.minecraft.client.entity.EntityPlayerSP":							
	    		return patchEntityPlayerSP(basicClass, false);
    	}
    	
		return basicClass;
    }
    
	private byte[] patchGameSettings(byte[] basicClass, boolean obfuscated) {
		
	    ClassNode classNode = new ClassNode();
	    ClassReader classReader = new ClassReader(basicClass);
	    classReader.accept(classNode, 0);
	    
	 	String 
	 	loadOptionsMethodName = obfuscated ? "a" : "loadOptions",
	 	saveOptionsMethodName = obfuscated ? "b" : "saveOptions",
	    stringClassName = "java/lang/String",
	    keyBindingClassName = obfuscated ? "bal" : "net/minecraft/client/settings/KeyBinding",
	 	printWriterClassName = "java/io/PrintWriter";
	 		 		    
        boolean 
        isFirstInserted = false,
        isSuccessful = false;
	 		    
	    for (MethodNode methodNode : classNode.methods) {
	    	
			if (methodNode.name.equals(loadOptionsMethodName) && methodNode.desc.equals("()V")) {
												
	            AbstractInsnNode currentInsn = null;
	            
	            Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
	           
	            while (insnIterator.hasNext()) {
	            	
	                currentInsn = insnIterator.next(); 
	                
	                if (!isFirstInserted && currentInsn.getOpcode() == Opcodes.ALOAD) {
	                	
                    	InsnList nodesList = new InsnList();
                    	
                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "removeHiddenKeyBindings", "()V", false));
                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "rewriteControlsSettings", "()V", false));
                        
                        methodNode.instructions.insertBefore(currentInsn, nodesList);
                        
                        isFirstInserted = true;
	                }
	                
	                if (currentInsn.getOpcode() == Opcodes.IF_ICMPGE) {
	                	
                    	InsnList nodesList = new InsnList();
                    	
                    	nodesList.add(new VarInsnNode(Opcodes.ALOAD, 3));
                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "loadControlsFromOptionsFile", "([L" + stringClassName + ";)Z", false));                 	
                        nodesList.add(new JumpInsnNode(Opcodes.IFEQ, ((JumpInsnNode) currentInsn).label));
                    	
                        methodNode.instructions.insertBefore(currentInsn.getPrevious().getPrevious(), nodesList);
                                                	                        
                        break;
	                }
	            }	
			}
			
			if (methodNode.name.equals(saveOptionsMethodName) && methodNode.desc.equals("()V")) {
				
	            AbstractInsnNode currentInsn = null;
	            
	            Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
	           
	            while (insnIterator.hasNext()) {
	            	
	                currentInsn = insnIterator.next(); 
	                
	                if (currentInsn.getOpcode() == Opcodes.IF_ICMPGE) {
	                	
                    	InsnList nodesList = new InsnList();
                    	
                    	nodesList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "saveControlsToOptionsFile", "(L" + printWriterClassName + ";)Z", false));          
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
	    LOGGER.info("<GameSettings.class> patched!");   
	    	    
	    return writer.toByteArray();	
	}
	
	private byte[] patchKeyBinding(byte[] basicClass, boolean obfuscated) {
        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String 
	 	hashFieldName = obfuscated ? "b" : "hash",
	 	onTickMethodName = obfuscated ? "a" : "onTick",
	 	isKeyPressedMethodName = obfuscated ? "d" : "getIsKeyPressed",
	 	setKeyBindStateMethodName = obfuscated ? "a" : "setKeyBindState",
	 	stringClassName = "java/lang/String",
	 	keyBindingClassName = obfuscated ? "bal" : "net/minecraft/client/settings/KeyBinding",
	 	intHashMapClassName = obfuscated ? "pz" : "net/minecraft/util/IntHashMap";

        boolean 
        descChanged = false,
        isSuccessful = false;
                        
		for (MethodNode methodNode : classNode.methods) {
			
			if (methodNode.name.equals(onTickMethodName) && methodNode.desc.equals("(I)V")) {
				
                AbstractInsnNode currentInsn = null;
                
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
               
                while (insnIterator.hasNext()) {
                	
                    currentInsn = insnIterator.next(); 
                    
                    if (currentInsn.getOpcode() == Opcodes.IFNULL) {    
                    	
                    	InsnList nodesList = new InsnList();

                    	nodesList.add(new VarInsnNode(Opcodes.ILOAD, 0));
                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "lookupActive", "(I)L" + keyBindingClassName + ";", false));
                    	nodesList.add(new VarInsnNode(Opcodes.ASTORE, 1));
                    	
                    	methodNode.instructions.insertBefore(currentInsn.getPrevious(), nodesList); 
                    	
                    	break;
                    }
                }
			}
			
			if (methodNode.name.equals(setKeyBindStateMethodName) && methodNode.desc.equals("(IZ)V")) {
				
                AbstractInsnNode currentInsn = null;
                
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
               
                while (insnIterator.hasNext()) {
                	
                    currentInsn = insnIterator.next(); 
                    
                    if (currentInsn.getOpcode() == Opcodes.ILOAD) {    
                    	
                    	InsnList nodesList = new InsnList();

                    	nodesList.add(new VarInsnNode(Opcodes.ILOAD, 0));
                    	nodesList.add(new VarInsnNode(Opcodes.ILOAD, 1));
                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "setKeybindingsState", "(IZ)V", false));
                    	nodesList.add(new InsnNode(Opcodes.RETURN));
                    	
                    	methodNode.instructions.insertBefore(currentInsn, nodesList); 
                    	
                    	break;
                    }
                }
			}
			
			if (methodNode.name.equals("<init>") && methodNode.desc.equals("(L" + stringClassName + ";IL" + stringClassName + ";)V")) {
												
                AbstractInsnNode currentInsn = null;
                
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
               
                while (insnIterator.hasNext()) {
                	
                    currentInsn = insnIterator.next(); 
                    
                    if (!descChanged && currentInsn.getOpcode() == Opcodes.INVOKESPECIAL) {                   	
                    	
                    	InsnList nodesList = new InsnList();
                    	
                    	nodesList.add(new FieldInsnNode(Opcodes.GETSTATIC, keyBindingClassName, hashFieldName, "L" + intHashMapClassName + ";"));
                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "getKeyBindingsHash", "(L" + intHashMapClassName + ";)V", false));
                    	
                    	nodesList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "getKeyBindingName", "(L" + stringClassName + ";)L" + stringClassName + ";", false));
                    	nodesList.add(new VarInsnNode(Opcodes.ASTORE, 1));   
                    	
                    	nodesList.add(new VarInsnNode(Opcodes.ILOAD, 2));
                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "getKeyBindingKeyCode", "(I)I", false));
                    	nodesList.add(new VarInsnNode(Opcodes.ISTORE, 2));
                    	
                    	nodesList.add(new VarInsnNode(Opcodes.ALOAD, 3));
                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "getKeyBindingCategory", "(L" + stringClassName + ";)L" + stringClassName + ";", false));
                    	nodesList.add(new VarInsnNode(Opcodes.ASTORE, 3));                   	               
                    	
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
			}
			
			if (methodNode.name.equals(isKeyPressedMethodName) && methodNode.desc.equals("()Z")) {
				
                AbstractInsnNode currentInsn = null;
                
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
               
                while (insnIterator.hasNext()) {
                	
                    currentInsn = insnIterator.next(); 
                    
                    if (currentInsn.getOpcode() == Opcodes.ALOAD) {    
                    	
                    	InsnList nodesList = new InsnList();

                    	nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "isKeyPressed", "(L" + keyBindingClassName + ";)Z", false));
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
        LOGGER.info("<KeyBinding.class> patched!");   
	            
        return writer.toByteArray();				
	}
	
	private byte[] patchGuiKeyBindingList(byte[] basicClass, boolean obfuscated, boolean flag) {
        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String keyBindingClassName = obfuscated ? "bal" : "net/minecraft/client/settings/KeyBinding";
        
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
	    	LOGGER.info("<GuiKeyBindingList.class> patched!");  
	    	else
		    LOGGER.info("<GuiNewKeyBindingList.class> (Controlling) patched!");  
		}
	            
        return writer.toByteArray();				
	}
	
	private byte[] patchKeyEntry(byte[] basicClass, boolean obfuscated, boolean flag) {
				        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String
	 	changeButtonFieldName = obfuscated ? "d" : "btnChangeKeyBinding",
	 	resetButtonFieldName = obfuscated ? "e" : "btnReset",
	 	keyBindingFieldName = obfuscated ? "b" : "field_148282_b",
	 	drawEntryMethodName = obfuscated ? "a" : "drawEntry",
	    mousePressedMethodName = obfuscated ? "a" : "mousePressed",
	 	keyEntryClassName = obfuscated ? "bev" : "net/minecraft/client/gui/GuiKeyBindingList$KeyEntry",
	    tesselatorClassName = obfuscated ? "bmh" : "net/minecraft/client/renderer/Tessellator",
	    guiButtonClassName = obfuscated ? "bcb" : "net/minecraft/client/gui/GuiButton",
	 	keyBindingClassName = obfuscated ? "bal" : "net/minecraft/client/settings/KeyBinding";
        
	 	if (flag) {
	 		
		 	changeButtonFieldName = "btnChangeKeyBinding";
		    resetButtonFieldName = "btnReset";
		 	keyBindingFieldName = "keybinding";
		 	drawEntryMethodName = "func_148279_a";
		 	mousePressedMethodName = "func_148278_a";
	 		keyEntryClassName = "us/getfluxed/controlsearch/client/gui/GuiNewKeyBindingList$KeyEntry";
	 	}
	 	
        boolean isSuccessful = false;
        
        int
        ifeqCount = 0,
        aloadCount = 0;
                        
		for (MethodNode methodNode : classNode.methods) {
			
			if (methodNode.name.equals(drawEntryMethodName) && methodNode.desc.equals("(IIIIIL" + tesselatorClassName + ";IIZ)V")) {
																
                AbstractInsnNode currentInsn = null;
                
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
                        	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "drawCuiControlsKeyEntry", "(L" + guiButtonClassName + ";L" + guiButtonClassName + ";L" + keyBindingClassName + ";ZIIII)V", false));

	                    	nodesList.add(new InsnNode(Opcodes.RETURN));
                        	
                        	methodNode.instructions.insertBefore(currentInsn, nodesList); 
                        	
                        	break;
                    	}
                    }
                }
			}
			
			if (methodNode.name.equals(mousePressedMethodName) && methodNode.desc.equals("(IIIIII)Z")) {
								
                AbstractInsnNode currentInsn = null;
                
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
               
                while (insnIterator.hasNext()) {
                	
                    currentInsn = insnIterator.next(); 
                    
                    if (currentInsn.getOpcode() == Opcodes.IFEQ) {    
                    	
                    	ifeqCount++;
                    	
                    	if (ifeqCount == 2) {
                    		
                        	InsnList nodesList = new InsnList();
                        	
                        	nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
	                    	nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, keyEntryClassName, keyBindingFieldName, "L" + keyBindingClassName + ";"));
                        	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "setToDefault", "(L" + keyBindingClassName + ";)V", false));
                        	
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
	    
	    if (isSuccessful) {	    	
	    	
	    	if (!flag)
	    	LOGGER.info("<GuiKeyBindingList.KeyEntry.class> patched!");  
	    	else
		    LOGGER.info("<GuiNewKeyBindingList.KeyEntry.class> (Controlling) patched!");  
	    }
	            
        return writer.toByteArray();				
	}
    
	private byte[] patchGuiControls(byte[] basicClass, boolean obfuscated, boolean flag) {
        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String 
	 	buttonIdFieldName = obfuscated ? "f" : "buttonId",
	 	resetButtonFieldName = obfuscated ? "t" : "field_146493_s",
	 	actionPerformedMethodName = obfuscated ? "a" : "actionPerformed",
	 	mouseClickedMethodName = obfuscated ? "a" : "mouseClicked",
	 	keyTypedMethodName = obfuscated ? "a" : "keyTyped",
	 	drawScreenMethodName = obfuscated ? "a" : "drawScreen",
	 	guiScreenClassName = obfuscated ? "bdw" : "net/minecraft/client/gui/GuiScreen",
	 	guiButtonClassName = obfuscated ? "bcb" : "net/minecraft/client/gui/GuiButton",
	 	keyModifierClassName = "ru/austeretony/rebind/main/EnumKeyModifier",
	 	guiControlsClassName = obfuscated ? "bew" : "net/minecraft/client/gui/GuiControls",
	 	keyBindingClassName = obfuscated ? "bal" : "net/minecraft/client/settings/KeyBinding";
	 	
	 	if (flag) {
	 		
		 	buttonIdFieldName = "buttonId";
		 	resetButtonFieldName = "buttonReset";
		 	actionPerformedMethodName = "func_146284_a";
		 	mouseClickedMethodName = "func_73864_a";
		 	keyTypedMethodName = "func_73869_a";
		 	drawScreenMethodName = "func_73863_a";
	 		guiControlsClassName = "us/getfluxed/controlsearch/client/gui/GuiNewControls";
	 	}
	 	
        boolean isSuccessful = false;
        
        int aloadCount = 0;
        
        AbstractInsnNode currentInsn = null;
	 		
		for (MethodNode methodNode : classNode.methods) {
			
			if (methodNode.name.equals(actionPerformedMethodName) && methodNode.desc.equals("(L" + guiButtonClassName + ";)V")) {
                
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
               
                while (insnIterator.hasNext()) {
                	
                    currentInsn = insnIterator.next(); 
                    
                    if (currentInsn.getOpcode() == Opcodes.ALOAD) {

                    	aloadCount++;
                    	
                    	if (aloadCount == 5) {
                    		
                        	InsnList nodesList = new InsnList();
                        	
                        	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "resetAllKeys", "()V", false));
	                    	
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
                        	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "setKeyModifierAndCode", "(L" + keyBindingClassName + ";L" + keyModifierClassName + ";I)V", false));
                        	
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
                        	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "setKeyModifierAndCode", "(L" + keyBindingClassName + ";L" + keyModifierClassName + ";I)V", false));
                        	
                        	methodNode.instructions.insertBefore(currentInsn, nodesList); 
                    	}  
                    	
                    	if (aloadCount == 4) {
                    		
                        	InsnList nodesList = new InsnList();
                        	
                        	nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        	nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, guiControlsClassName, buttonIdFieldName, "L" + keyBindingClassName + ";"));
                        	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, keyModifierClassName, "getActiveModifier", "()L" + keyModifierClassName + ";", false));
                        	nodesList.add(new VarInsnNode(Opcodes.ILOAD, 2));
                        	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "setKeyModifierAndCode", "(L" + keyBindingClassName + ";L" + keyModifierClassName + ";I)V", false));
                        	
                        	methodNode.instructions.insertBefore(currentInsn, nodesList); 
                    		
                        	break;
                    	}   
                    }
                    
                    if (currentInsn.getOpcode() == Opcodes.ACONST_NULL) {
                    	
                    	InsnList nodesList = new InsnList();

                    	nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    	nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, guiControlsClassName, buttonIdFieldName, "L" + keyBindingClassName + ";"));
                    	nodesList.add(new VarInsnNode(Opcodes.ILOAD, 2));
                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "resetKeyBinding", "(L" + keyBindingClassName + ";I)L" + keyBindingClassName + ";", false));
                                                                        
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
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "setResetButtonState", "(L" + guiButtonClassName + ";)V", false));
                        
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
	    
	    if (isSuccessful) {
	    	
	    	if (!flag)
	    	LOGGER.info("<GuiControls.class> patched!");   
	    	else
		    LOGGER.info("<GuiNewControls.class> (Controlling) patched!");   
	    }
        
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
	    LOGGER.info("<Minecraft.class> patched!");
	            
        return writer.toByteArray();				
	}
	
	private byte[] patchGuiScreen(byte[] basicClass, boolean obfuscated, boolean flag) {
        
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

	    	if (!flag) 
	    	LOGGER.info("<GuiScreen.class> patched!");   
	    	else
	    	LOGGER.info("<GuiContainer.class> patched!"); 
	    }
        
        return writer.toByteArray();				
	}
	
	private byte[] patchEntityPlayerSP(byte[] basicClass, boolean obfuscated) {
        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String targetMethodName = obfuscated ? "e" : "onLivingUpdate";  
	 	
        boolean isSuccessful = false;
        
        int iconstCount = 0;
	 		
		for (MethodNode methodNode : classNode.methods) {
			
			if (methodNode.name.equals(targetMethodName) && methodNode.desc.equals("()V")) {
												
                AbstractInsnNode currentInsn = null;
                
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
               
                while (insnIterator.hasNext()) {
                	
                    currentInsn = insnIterator.next(); 
                    
                    if (currentInsn.getOpcode() == Opcodes.ICONST_1) {
                    	
                    	iconstCount++;
                    	
                    	if (iconstCount == 6) {
                    		
                            methodNode.instructions.insert(currentInsn.getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "isDoubleTapForwardSprintAllowed", "()Z", false)); 
                    		
                        	insnIterator.remove();
                    	}
                    	
                    	if (iconstCount == 7) {
                    		
                            methodNode.instructions.insert(currentInsn.getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/austeretony/rebind/coremod/ReBindHooks", "isPlayerSprintAllowed", "()Z", false)); 
                    		
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
