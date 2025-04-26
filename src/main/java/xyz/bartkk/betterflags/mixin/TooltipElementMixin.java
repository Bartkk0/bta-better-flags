package xyz.bartkk.betterflags.mixin;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DrawableSurfaceElement;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.TooltipElement;
import net.minecraft.client.render.Font;
import net.minecraft.client.util.helper.Colors;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.net.command.TextFormatting;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import xyz.bartkk.betterflags.FlagUtils;
import xyz.bartkk.betterflags.TooltipFlagRenderer;

@Mixin(value = TooltipElement.class,remap = false)
public abstract class TooltipElementMixin extends Gui implements TooltipFlagRenderer {
	@Shadow
	protected abstract int[] drawBackground(int minX, int minY, int maxX, int maxY);

	@Shadow
	public abstract int getPadding();

	@Shadow
	Font fr;

	@Shadow
	Minecraft mc;

	@Unique
	private ItemStack lastItemStack;
	@Unique
	private DrawableSurfaceElement lastDrawableSurfaceElement;

	@Override
	public void betterflags$renderFlag(String string, int x, int y, int offsetX, int offsetY, ItemStack stack) {
		String[] lines = string.split("\n");
		if (lines.length != 0) {
			int width = 0;

			for (String line : lines) {
				width = Math.max(width, this.fr.getStringWidth(line));
			}

			int flagScale = 2;
			int height = lines.length * 8 + (lines.length - 1) * 3 + 16* flagScale + getPadding(); // add flag height
			if (x + offsetX + width + 6 > this.mc.resolution.getScaledWidthScreenCoords()) {
				offsetX = -offsetX - width;
			}

			offsetY -= Math.max(0, y + height + offsetY + 6 - this.mc.resolution.getScaledHeightScreenCoords());
			x += offsetX;
			y += offsetY;
			int padding = this.getPadding();
			this.drawBackground(x, y, x + width + padding * 2, y + height + padding * 2);

			for (int i = 0; i < lines.length; i++) {
				this.fr.drawStringWithShadow(lines[i], x + padding, y + padding + i * 11, 16777215);
			}

			// Render flag
			y = y + padding + lines.length * 11;
			DrawableSurfaceElement flagSurface = getFlagDrawable(stack, flagScale);

			this.mc.textureManager.bindTexture(this.mc.textureManager.loadTexture("/assets/minecraft/textures/gui/container/flag_editor.png"));

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexturedModalRect((double)x+padding, y, 27, 8, 24*2, 16* flagScale,96,64);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_ONE_MINUS_SRC_COLOR, GL11.GL_SRC_COLOR);
			flagSurface.render(x + padding, y);
			GL11.glDisable(GL11.GL_BLEND);
		}
	}

	@Unique
	public final DrawableSurfaceElement getFlagDrawable(ItemStack stack, int scale) {
		if(lastDrawableSurfaceElement != null) {
			if(lastItemStack == stack) {
				return lastDrawableSurfaceElement;
			}
		}

		CompoundTag flagData = stack.getData().getCompound("FlagData");
		byte[] colors = flagData.getByteArray("Colors");
		ListTag items = flagData.getList("Items");
		byte[] unpackedColors = FlagUtils.unpackFlagColors(colors);

		DrawableSurfaceElement flagSurface = new DrawableSurfaceElement(24, 16, scale, unpackedColors);

		int[] dyeColors = new int[5];
		for (int i = 0; i < items.tagCount(); i++) {
			CompoundTag compound = (CompoundTag)items.tagAt(i);
			int slot = compound.getByte("Slot");
			ItemStack colorItemStack = ItemStack.readItemStackFromNbt(compound);

			if (colorItemStack != null && colorItemStack.getItem() == Items.DYE) {
				dyeColors[slot+1] = Colors.allFlagColors[TextFormatting.get(15 - colorItemStack.getMetadata()).id].getARGB();
			}
		}

		dyeColors[4] = -1;
		flagSurface.colors = dyeColors;

		lastDrawableSurfaceElement = flagSurface;
		lastItemStack = stack;

		return flagSurface;
	}
}
