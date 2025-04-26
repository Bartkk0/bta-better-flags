package xyz.bartkk.betterflags;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.GameStartEntrypoint;

public class BetterFlags implements ModInitializer, GameStartEntrypoint {
	public static final String MOD_ID = "betterflags";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Better flags initialized.");
	}


	@Override
	public void beforeGameStart() {
		try {
			TextureRegistry.initializeAllFiles(MOD_ID, TextureRegistry.guiSpriteAtlas,true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void afterGameStart() {

	}
}
