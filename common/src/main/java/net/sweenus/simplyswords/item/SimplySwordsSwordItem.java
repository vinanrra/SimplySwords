package net.sweenus.simplyswords.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.sweenus.simplyswords.util.HelperMethods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimplySwordsSwordItem extends SwordItem {
    String[] repairIngredient;

    public SimplySwordsSwordItem(ToolMaterial toolMaterial, Settings settings, String... repairIngredient) {
        super(toolMaterial, settings);
        this.repairIngredient = repairIngredient;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        List<Item> potentialIngredients = new ArrayList<>(List.of());
        Arrays.stream(repairIngredient).toList().forEach(repIngredient ->
            potentialIngredients.add(
                    Registries.ITEM.get(Identifier.of(repIngredient))));


        return potentialIngredients.contains(ingredient.getItem());
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.getWorld().isClient()) {
            HelperMethods.playHitSounds(attacker, target);
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, TooltipContext tooltipContext, List<Text> tooltip, TooltipType type) {


        int rgbPrometheum = 0x3A6A56;
        int rgbCarmot = 0xE63E73;
        Style PROMETHEUM = Style.EMPTY.withColor(TextColor.fromRgb(rgbPrometheum));
        Style CARMOT = Style.EMPTY.withColor(TextColor.fromRgb(rgbCarmot));

        if (this.getName(itemStack).getString().contains("Prometheum"))
            tooltip.add(Text.translatable("item.simplyswords.compat.mythicmetals.regrowth").setStyle(PROMETHEUM));
        else if (this.getName(itemStack).getString().contains("Carmot"))
            tooltip.add(Text.translatable("item.simplyswords.compat.mythicmetals.looting").setStyle(CARMOT));

        super.appendTooltip(itemStack, tooltipContext, tooltip, type);
    }

}
