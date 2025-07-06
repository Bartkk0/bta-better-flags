package xyz.bartkk.betterflags;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import turniplabs.halplibe.helper.TextureHelper;
import turniplabs.halplibe.util.ClientStartEntrypoint;

public class BetterFlags implements ModInitializer, ClientStartEntrypoint {
	public static final String MOD_ID = "betterflags";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Better flags initialized.");
	}

	@Override
	public void beforeClientStart() {
		try {
//			TextureRegistry.guiSpriteAtlas.init();
			TextureRegistry.initializeAllFiles(MOD_ID, TextureRegistry.guiSpriteAtlas,true);
//			TextureHelper.initializeAllFiles(MOD_ID, TextureRegistry.guiSpriteAtlas,3);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void afterClientStart() {

	}
}
