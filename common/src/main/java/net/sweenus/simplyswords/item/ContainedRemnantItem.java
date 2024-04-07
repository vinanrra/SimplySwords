package net.sweenus.simplyswords.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import net.sweenus.simplyswords.SimplySwords;
import net.sweenus.simplyswords.registry.ItemsRegistry;
import net.sweenus.simplyswords.util.HelperMethods;

import java.util.List;

public class ContainedRemnantItem extends Item {

    public ContainedRemnantItem() {
        super( new Settings().arch$tab(SimplySwords.SIMPLYSWORDS).rarity(Rarity.EPIC).fireproof().maxCount(1));
    }


    @Override
    public Text getName(ItemStack stack) {
        Style LEGENDARY = HelperMethods.getStyle("legendary");
        return Text.translatable(this.getTranslationKey(stack)).setStyle(LEGENDARY);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {

        tooltip.add(Text.literal(""));
        tooltip.add(Text.translatable("item.simplyswords.contained_remnant_description").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.simplyswords.contained_remnant_description2").formatted(Formatting.GRAY));
        if (this.equals(ItemsRegistry.TAMPERED_REMNANT.get())) {
            tooltip.add(Text.translatable("item.simplyswords.tampered_remnant_description3").formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("item.simplyswords.tampered_remnant_description4").formatted(Formatting.GRAY));
        } else {
            tooltip.add(Text.translatable("item.simplyswords.contained_remnant_description3").formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("item.simplyswords.contained_remnant_description4").formatted(Formatting.GRAY));
        }
        tooltip.add(Text.literal(""));
        tooltip.add(Text.translatable("item.simplyswords.contained_remnant_description5").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.simplyswords.contained_remnant_description6").formatted(Formatting.GRAY));

    }
}
