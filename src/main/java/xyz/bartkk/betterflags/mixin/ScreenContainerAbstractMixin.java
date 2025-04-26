package xyz.bartkk.betterflags.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.TooltipElement;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.core.item.ItemFlag;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.player.inventory.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.bartkk.betterflags.TooltipFlagRenderer;

@Mixin(value = ScreenContainerAbstract.class, remap = false)
public class ScreenContainerAbstractMixin {
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/TooltipElement;render(Ljava/lang/String;IIII)V"))
	public void renderTooltip(TooltipElement instance, String string, int x, int y, int offsetX, int offsetY, @Local Slot slot) {
		if(slot.getItemStack() != null && slot.getItemStack().itemID == Items.FLAG.id){
			ItemStack itemStack = slot.getItemStack();
			ItemFlag flag = (ItemFlag) itemStack.getItem();

			if(flag.hasFlagBeenDrawnOn(itemStack)){
				((TooltipFlagRenderer)instance).betterflags$renderFlag(string, x, y, offsetX, offsetY,itemStack);
			} else {
				instance.render(string,x,y,offsetX,offsetY);
			}
		} else {
			instance.render(string,x,y,offsetX,offsetY);
		}
	}
}
