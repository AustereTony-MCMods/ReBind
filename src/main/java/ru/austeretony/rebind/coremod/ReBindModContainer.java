package ru.austeretony.rebind.coremod;

import java.util.Arrays;

import com.google.common.eventbus.EventBus;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

public class ReBindModContainer extends DummyModContainer {

	 public ReBindModContainer() {

		 super(new ModMetadata());

		 ModMetadata meta = getMetadata();

		 meta.modId = "rebindcore";
		 meta.name = "ReBind Core";
		 meta.version = "1.0"; 
		 meta.credits = "";
		 meta.authorList = Arrays.asList("AustereTony");
		 meta.description = "Coremod for ReBind modification.";
		 meta.url = "";
		 meta.screenshots = new String[0];
		 meta.logoFile = "";
	 }
	 
	 @Override
	 public boolean registerBus(EventBus bus, LoadController controller) {

		 bus.register(this);
		 return true;
	 }
}
