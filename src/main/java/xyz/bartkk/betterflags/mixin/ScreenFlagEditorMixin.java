package xyz.bartkk.betterflags.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ButtonElement;
import net.minecraft.client.gui.DrawableSurfaceElement;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.gui.container.ScreenFlagEditor;
import net.minecraft.core.block.entity.TileEntityFlag;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.bartkk.betterflags.BetterFlags;
import xyz.bartkk.betterflags.FlagUtils;
import xyz.bartkk.betterflags.SimpleToast;

import java.util.Base64;

@Mixin(value = ScreenFlagEditor.class, remap = false)
public abstract class ScreenFlagEditorMixin extends ScreenContainerAbstract {
	@Shadow
	@Final
	private TileEntityFlag flagEntity;

	@Shadow
	DrawableSurfaceElement flagSurface;

	public ScreenFlagEditorMixin(MenuAbstract container) {
		super(container);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void onInit(Player player, TileEntityFlag flagTileEntity, CallbackInfo ci) {
	}

	@Inject(method = "init", at = @At(value = "TAIL"))
	public void init(CallbackInfo ci) {
		int x = (this.width - this.xSize) / 2 + this.xSize + 8;
		int y = (this.height - this.ySize) / 2;

		// Arrows
		this.buttons.add(new ButtonElement(201, x + 18, y + 6, 12, 12, "").setTextures("betterflags:gui/arrow_up",
			"betterflags:gui/arrow_up_highlighted",
			"betterflags:gui/arrow_up_disabled"));

		this.buttons.add(new ButtonElement(202, x + 6, y + 18, 12, 12, "").setTextures("betterflags:gui/arrow_left",
			"betterflags:gui/arrow_left_highlighted",
			"betterflags:gui/arrow_left_disabled"));

		this.buttons.add(new ButtonElement(203, x + 30, y + 18, 12, 12, "").setTextures("betterflags:gui/arrow_right",
			"betterflags:gui/arrow_right_highlighted",
			"betterflags:gui/arrow_right_disabled"));
		this.buttons.add(new ButtonElement(204, x + 18, y + 30, 12, 12, "").setTextures("betterflags:gui/arrow_down",
			"betterflags:gui/arrow_down_highlighted",
			"betterflags:gui/arrow_down_disabled"));


		this.buttons.add(new ButtonElement(205,
			x + 6,
			y + 46,
			36,
			12,
			I18n.getInstance().translateKey("toast.betterflags.clear")));

		this.buttons.add(new ButtonElement(206,
			x + 6,
			y + 46 + 16,
			36,
			12,
			I18n.getInstance().translateKey("toast.betterflags.copy")));

		this.buttons.add(new ButtonElement(207,
			x + 6,
			y + 46 + 16 + 4 + 12,
			36,
			12,
			I18n.getInstance().translateKey("toast.betterflags.paste")));
	}

	@Inject(method = "drawGuiContainerBackgroundLayer", at = @At("TAIL"))
	protected void drawGuiContainerBackgroundLayer(float f, CallbackInfo ci) {
		this.mc.textureManager.loadTexture("/assets/betterflags/textures/gui/container/flag_editor_extras.png").bind();
		int j = (this.width - this.xSize) / 2 + this.xSize + 8;
		int k = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(j, k, 0, 0, 48, 100, 1, 1f);
	}

	@Inject(method = "buttonClicked", at = @At("TAIL"))
	protected void buttonClicked(ButtonElement button, CallbackInfo ci) {
		if (button.id == 201) {
			move(0, 1);
		} else if (button.id == 202) {
			move(-1, 0);
		} else if (button.id == 203) {
			move(1, 0);
		} else if (button.id == 204) {
			move(0, -1);
		}

		if (button.id == 205) {
			flagSurface.clear();
			flagEntity.isDirty = true;
		}

		if (button.id == 206) { // Copy
			byte[] packed = FlagUtils.packFlagColors(flagSurface.surfaceData);

			Base64.Encoder encoder = Base64.getEncoder();
			String base64 = encoder.encodeToString(packed);
			String data = "BTAFLAG_" + base64;

			GLFW.glfwSetClipboardString(Minecraft.getMinecraft().gameWindow.getHandle(), data);
			Minecraft.getMinecraft().guiToasts.addToast(new SimpleToast("",
				I18n.getInstance().translateKey("toast.betterflags.copied")));
		} else if (button.id == 207) { // Paste
			String clipboard = GLFW.glfwGetClipboardString(Minecraft.getMinecraft().gameWindow.getHandle());

			if(clipboard == null) {
				return;
			}

			if (!clipboard.startsWith("BTAFLAG_")) {
				Minecraft.getMinecraft().guiToasts.addToast(new SimpleToast("",
					I18n.getInstance().translateKey("toast.betterflags.invalid_data")));
				return;
			}

			byte[] unpacked;
			try{
				String base64 = clipboard.substring("BTAFLAG_".length());
				Base64.Decoder decoder = Base64.getDecoder();
				byte[] decoded = decoder.decode(base64);

				unpacked = FlagUtils.unpackFlagColors(decoded);
			} catch (RuntimeException e) {
				BetterFlags.LOGGER.error(e.toString());
				Minecraft.getMinecraft().guiToasts.addToast(new SimpleToast("",
					I18n.getInstance().translateKey("toast.betterflags.invalid_data")));
				return;
			}

			if(unpacked.length != 384){
				Minecraft.getMinecraft().guiToasts.addToast(new SimpleToast("",
					I18n.getInstance().translateKey("toast.betterflags.invalid_data")));
				return;
			}

			System.arraycopy(unpacked, 0, flagSurface.surfaceData, 0, unpacked.length);
			flagEntity.isDirty = true;
		}
	}

	@Unique
	private void move(int offsetX, int offsetY) {
		flagEntity.isDirty = true;
		int width = flagSurface.getWidth();
		int height = flagSurface.getHeight();

		byte[] data = new byte[width * height];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				data[i * width + j] = flagSurface.getPixelValue(j, i);
				flagSurface.setPixelValue(j, i, (byte) 0);
			}
		}

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int y = i - offsetY;
				int x = j + offsetX;

				flagSurface.setPixelValue(x, y, data[i * width + j]);
			}
		}
	}
}
