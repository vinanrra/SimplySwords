package net.sweenus.simplyswords.item.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.sweenus.simplyswords.config.Config;
import net.sweenus.simplyswords.config.ConfigDefaultValues;
import net.sweenus.simplyswords.item.UniqueSwordItem;
import net.sweenus.simplyswords.registry.EffectRegistry;
import net.sweenus.simplyswords.registry.SoundRegistry;
import net.sweenus.simplyswords.util.HelperMethods;

import java.util.List;

public class CaelestisSwordItem extends UniqueSwordItem {
    public CaelestisSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 0;
    }

    private static int stepMod = 0;

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.getWorld().isClient()) {

            HelperMethods.playHitSounds(attacker, target);

        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        int skillCooldown = (int) Config.getFloat("astralShiftCooldown", "UniqueEffects", ConfigDefaultValues.astralShiftCooldown);
        int duration = (int) Config.getFloat("astralShiftDuration", "UniqueEffects", ConfigDefaultValues.astralShiftDuration);


        world.playSound(null, user.getBlockPos(), SoundRegistry.ACTIVATE_PLINTH_03.get(),
                user.getSoundCategory(), 0.4f, 1.3f);

        user.addStatusEffect(new StatusEffectInstance(EffectRegistry.ASTRAL_SHIFT,
                duration, 0, false, false, true));
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS,
                duration, 0, false, false, true));
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS,
                duration, 0, false, false, true));
        user.getItemCooldownManager().set(this, skillCooldown);

        return super.use(world, user, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (stepMod > 0) stepMod--;
        if (stepMod <= 0) stepMod = 7;
        HelperMethods.createFootfalls(entity, stack, world, stepMod, ParticleTypes.ENCHANT,
                ParticleTypes.ENCHANT, ParticleTypes.MYCELIUM, true);
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        Style RIGHTCLICK = HelperMethods.getStyle("rightclick");
        Style ABILITY = HelperMethods.getStyle("ability");
        Style TEXT = HelperMethods.getStyle("text");

        tooltip.add(Text.literal(""));
        tooltip.add(Text.translatable("item.simplyswords.caelestissworditem.tooltip1").setStyle(ABILITY));
        tooltip.add(Text.translatable("item.simplyswords.caelestissworditem.tooltip2").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.caelestissworditem.tooltip3").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.caelestissworditem.tooltip4").setStyle(TEXT));
        tooltip.add(Text.literal(""));
        tooltip.add(Text.translatable("item.simplyswords.onrightclick").setStyle(RIGHTCLICK));
        tooltip.add(Text.translatable("item.simplyswords.caelestissworditem.tooltip5").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.caelestissworditem.tooltip6").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.caelestissworditem.tooltip7",
                (int) Config.getFloat("astralShiftDuration", "UniqueEffects", ConfigDefaultValues.astralShiftDuration) / 20).setStyle(TEXT));
        tooltip.add(Text.literal(""));
        tooltip.add(Text.translatable("item.simplyswords.caelestissworditem.tooltip8").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.caelestissworditem.tooltip9").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.caelestissworditem.tooltip10").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.caelestissworditem.tooltip11").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.caelestissworditem.tooltip12").setStyle(TEXT));

        super.appendTooltip(itemStack, world, tooltip, tooltipContext);
    }
}
