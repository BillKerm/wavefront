package me.jebkerm.wavefront;

import me.jebkerm.wavefront.block.ModBlocks;
import me.jebkerm.wavefront.item.ModItemGroups;
import me.jebkerm.wavefront.item.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Wavefront implements ModInitializer {
	public static final String MOD_ID = "wavefront";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Wavefront... hold on tight!");

		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();

		LOGGER.info("Wavefront successfully initialized!");
	}
}
