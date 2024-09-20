package net.sweenus.simplyswords.item.custom;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.sweenus.simplyswords.item.UniqueSwordItem;
import net.sweenus.simplyswords.registry.ItemsRegistry;
import net.sweenus.simplyswords.util.HelperMethods;

import java.util.List;

public class DormantRelicSwordItem extends UniqueSwordItem {
    private static int stepMod = 0;

    public DormantRelicSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        HelperMethods.playHitSounds(attacker, target);
        return super.postHit(stack, target, attacker);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return super.use(world, user, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (stepMod > 0) stepMod--;
        if (stepMod <= 0) stepMod = 7;
        HelperMethods.createFootfalls(entity, stack, world, stepMod, ParticleTypes.MYCELIUM, ParticleTypes.MYCELIUM,
                ParticleTypes.MYCELIUM, true);
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, TooltipContext tooltipContext, List<Text> tooltip, TooltipType type) {
        Style TEXT = HelperMethods.getStyle("text");

        tooltip.add(Text.literal(""));
        tooltip.add(Text.translatable("item.simplyswords.dormantrelicsworditem.tooltip2").setStyle(TEXT));
        if (this.asItem().equals(ItemsRegistry.DECAYING_RELIC.get())) {
            tooltip.add(Text.literal(""));
            if (Screen.hasAltDown()) {
                tooltip.add(Text.translatable("item.simplyswords.decayingrelicsworditem.tooltip1").formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("item.simplyswords.decayingrelicsworditem.tooltip2").formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("item.simplyswords.decayingrelicsworditem.tooltip3").formatted(Formatting.GRAY));
            } else {
                tooltip.add(Text.translatable("item.simplyswords.common.showtooltip.info").formatted(Formatting.GRAY));
            }
        }

        super.appendTooltip(itemStack, tooltipContext, tooltip, type);
    }
}
