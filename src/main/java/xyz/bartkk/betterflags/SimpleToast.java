package xyz.bartkk.betterflags;

import net.minecraft.client.gui.toasts.IToastable;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimpleToast implements IToastable {
	public final String title;
	public final String message;
	private long startTime;

	public SimpleToast(String title, String message) {
		this.title = title;
		this.message = message;
	}

	@Override
	public boolean messageOnly(long l) {
		return this.title.isEmpty();
	}

	@Override
	public String getTitle(long l) {
		return title;
	}

	@Override
	public int nameColor(long l) {
		return -1;
	}

	@Override
	public String getMessage(long l) {
		return message;
	}

	@Override
	public int descriptionColor(long l) {
		return -1;
	}

	@Override
	public double getAnimationProgress(long runtime) {
		runtime = System.currentTimeMillis() - this.startTime;

		return (double)runtime / 3000.0;
	}

	@Override
	public String getTexture(long l) {
		return "minecraft:gui/toast";
	}

	@Override
	public void onToastStart() {
		this.startTime = System.currentTimeMillis();
	}

	@Override
	public void onToastEnd() {

	}

	@Override
	public boolean isEquivalentToast(@NotNull IToastable iToastable) {
		if (iToastable instanceof SimpleToast) {
			SimpleToast simpleToast = (SimpleToast) iToastable;
			return this.title.equals(simpleToast.title) && this.message.equals(simpleToast.message);
		}
		return false;
	}

	@Override
	public @Nullable ItemStack getIcon(long l) {
		return Item.getItem(Items.TOOL_PICKAXE_IRON.id).getDefaultStack();
	}
}
