package net.sweenus.simplyswords.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import net.sweenus.simplyswords.SimplySwords;
import net.sweenus.simplyswords.util.HelperMethods;

import java.util.List;

public class EmpoweredRemnantItem extends Item {

    public EmpoweredRemnantItem() {
        super( new Settings().arch$tab(SimplySwords.SIMPLYSWORDS).rarity(Rarity.EPIC).fireproof());
    }


    @Override
    public Text getName(ItemStack stack) {
        Style UNIQUE = HelperMethods.getStyle("unique");
        return Text.translatable(this.getTranslationKey(stack)).setStyle(UNIQUE);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {

        tooltip.add(Text.literal(""));
        tooltip.add(Text.translatable("item.simplyswords.remnant_description").formatted(Formatting.GRAY, Formatting.ITALIC));
        tooltip.add(Text.translatable("item.simplyswords.remnant_description2").formatted(Formatting.GRAY, Formatting.ITALIC));
        tooltip.add(Text.translatable("item.simplyswords.remnant_description3").formatted(Formatting.GRAY, Formatting.ITALIC));
        tooltip.add(Text.translatable("item.simplyswords.remnant_description4").formatted(Formatting.GRAY, Formatting.ITALIC));

    }
}
