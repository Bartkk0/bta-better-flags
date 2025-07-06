package xyz.bartkk.betterflags.mixin;

import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.util.Map;
import java.util.zip.ZipError;

// This is necessary until HalpLibe updates
@Mixin(value = TextureRegistry.class, remap = false)
public class TextureRegistryMixin {
	@Redirect(method = "getFilesAndSubFiles", at = @At(value = "INVOKE", target = "Ljava/nio/file/FileSystems;newFileSystem(Ljava/net/URI;Ljava/util/Map;)Ljava/nio/file/FileSystem;"))
	private static FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
		FileSystem ret;
		try {
			ret = FileSystems.getFileSystem(uri);
		} catch (FileSystemNotFoundException ignore) {
			try {
				ret = FileSystems.newFileSystem(uri, env);
			} catch (FileSystemAlreadyExistsException ignore2) {
				ret = FileSystems.getFileSystem(uri);
			} catch (IOException | ZipError e) {
				throw new IOException("Error accessing "+uri+": "+e, e);
			}
		}
		return ret;
	}
}
