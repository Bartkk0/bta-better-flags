package xyz.bartkk.betterflags;

import net.minecraft.core.item.ItemStack;

public interface TooltipFlagRenderer {
	void betterflags$renderFlag(String string, int x, int y, int offsetX, int offsetY, ItemStack itemStack);
}
