package net.sweenus.simplyswords.item.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
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
import java.util.Random;

public class MagiscytheSwordItem extends UniqueSwordItem {
    public MagiscytheSwordItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 0;
    }

    private static int stepMod = 0;
    public static boolean scalesWithSpellPower;

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.getWorld().isClient()) {
            HelperMethods.playHitSounds(attacker, target);
            ServerWorld world = (ServerWorld) attacker.getWorld();

            if (attacker.hasStatusEffect(EffectRegistry.MAGISTORM)) {
                world.playSound(null, attacker.getBlockPos(), SoundRegistry.ELEMENTAL_BOW_SCIFI_SHOOT_IMPACT_03.get(),
                        attacker.getSoundCategory(), 0.1f, 1.9f);

                float repairChance = Config.getFloat("magistormRepairChance", "UniqueEffects", ConfigDefaultValues.magistormRepairChance);
                Random random = new Random();
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR || slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
                        ItemStack item = attacker.getEquippedStack(slot);
                        if (!item.isEmpty() && random.nextFloat() < repairChance && item.getDamage() > 0) {
                            item.setDamage((int) (item.getDamage() - HelperMethods.getEntityAttackDamage(attacker)));
                            break;
                        }
                    }
                }
            }
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        int skillCooldown = (int) Config.getFloat("magistormCooldown", "UniqueEffects", ConfigDefaultValues.magistormCooldown);
        int baseEffectDuration = (int) Config.getFloat("magistormDuration", "UniqueEffects", ConfigDefaultValues.magistormDuration);

        world.playSound(null, user.getBlockPos(), SoundRegistry.MAGIC_SHAMANIC_NORDIC_22.get(),
                user.getSoundCategory(), 0.2f, 1.1f);
        user.addStatusEffect(new StatusEffectInstance(EffectRegistry.MAGISTORM, baseEffectDuration, 1));
        user.getItemCooldownManager().set(this, skillCooldown);

        return super.use(world, user, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (HelperMethods.commonSpellAttributeScaling(0.5f, entity, "arcane") > 0) {
            scalesWithSpellPower = true;
        }
        if (stepMod > 0) stepMod--;
        if (stepMod <= 0) stepMod = 7;
        HelperMethods.createFootfalls(entity, stack, world, stepMod, ParticleTypes.ENCHANT,
                ParticleTypes.ENCHANT, ParticleTypes.ASH, true);
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, TooltipContext tooltipContext, List<Text> tooltip, TooltipType type) {
        Style RIGHTCLICK = HelperMethods.getStyle("rightclick");
        Style ABILITY = HelperMethods.getStyle("ability");
        Style TEXT = HelperMethods.getStyle("text");

        tooltip.add(Text.literal(""));
        tooltip.add(Text.translatable("item.simplyswords.magiscythesworditem.tooltip1").setStyle(ABILITY));
        tooltip.add(Text.translatable("item.simplyswords.magiscythesworditem.tooltip2").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.magiscythesworditem.tooltip3").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.magiscythesworditem.tooltip4").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.magiscythesworditem.tooltip5").setStyle(TEXT));
        tooltip.add(Text.literal(""));
        tooltip.add(Text.translatable("item.simplyswords.onrightclick").setStyle(RIGHTCLICK));
        tooltip.add(Text.translatable("item.simplyswords.magiscythesworditem.tooltip6").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.magiscythesworditem.tooltip7").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.magiscythesworditem.tooltip8").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.magiscythesworditem.tooltip9").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.magiscythesworditem.tooltip10").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.magiscythesworditem.tooltip11").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.magiscythesworditem.tooltip12").setStyle(TEXT));
        if (scalesWithSpellPower) {
            tooltip.add(Text.literal(""));
            tooltip.add(Text.translatable("item.simplyswords.compat.scaleArcane"));
        }

        super.appendTooltip(itemStack, tooltipContext, tooltip, type);
    }
}
