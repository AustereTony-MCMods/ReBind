package ru.austeretony.rebind.main;

import net.minecraft.client.Minecraft;

public enum EnumKeyConflictContext {
	
    UNIVERSAL {
    	
        @Override
        public boolean isActive() {
        	
            return true;
        }

        @Override
        public boolean conflicts(EnumKeyConflictContext other) {
        	
            return true;
        }
    },

    GUI {
    	
        @Override
        public boolean isActive() {
        	
            return Minecraft.getMinecraft().currentScreen != null;
        }

        @Override
        public boolean conflicts(EnumKeyConflictContext other) {
        	
            return this == other;
        }
    },

    IN_GAME {
    	
        @Override
        public boolean isActive() {
        	
            return !GUI.isActive();
        }

        @Override
        public boolean conflicts(EnumKeyConflictContext other) {
        	
            return this == other;
        }
    };
    
    public abstract boolean isActive();

    public abstract boolean conflicts(EnumKeyConflictContext other);
}