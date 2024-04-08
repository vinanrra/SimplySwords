package net.sweenus.simplyswords.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.sweenus.simplyswords.config.Config;
import net.sweenus.simplyswords.config.ConfigDefaultValues;
import net.sweenus.simplyswords.item.UniqueSwordItem;
import net.sweenus.simplyswords.util.HelperMethods;

import java.util.List;

public class MagibladeSwordItem extends UniqueSwordItem {
    public MagibladeSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
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
        ItemStack itemStack = user.getStackInHand(hand);
        if (itemStack.getDamage() >= itemStack.getMaxDamage() - 1) {
            return TypedActionResult.fail(itemStack);
        }
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!world.isClient) {
            if ((remainingUseTicks %5 == 0 && remainingUseTicks < getMaxUseTime(stack) - 5)) {
                if (remainingUseTicks < 10) {
                    onStoppedUsing(stack, world, user, remainingUseTicks);
                }

            }
            if (remainingUseTicks == getMaxUseTime(stack)-1) {
                world.playSoundFromEntity(null, user,  SoundEvents.ENTITY_WARDEN_SONIC_CHARGE,
                        user.getSoundCategory(), 0.6f, 1.4f);
            }
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 40;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!user.getWorld().isClient() && user instanceof  PlayerEntity player) {
            int skillCooldown = (int) Config.getFloat("magibladeCooldown", "UniqueEffects", ConfigDefaultValues.magibladeCooldown);
            float damageModifier = Config.getFloat("magibladeDamageModifier", "UniqueEffects", ConfigDefaultValues.magibladeDamageModifier);
            float damage = (float) (HelperMethods.getAttackDamage(stack) * damageModifier);
            float distance = Config.getFloat("magibladeSonicDistance", "UniqueEffects", ConfigDefaultValues.magibladeSonicDistance);
            DamageSource damageSource = player.getDamageSources().playerAttack(player);

            if (remainingUseTicks < 11) {
                world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_WARDEN_SONIC_BOOM,
                        user.getSoundCategory(), 0.8f, 1.1f);
                HelperMethods.spawnDirectionalParticles((ServerWorld) world, ParticleTypes.SONIC_BOOM, player, 10, distance);
                HelperMethods.damageEntitiesInTrajectory((ServerWorld) world, player, distance, damage, damageSource);
                user.setVelocity(user.getRotationVector().negate().multiply(+1.1));
                user.setVelocity(user.getVelocity().x, 0, user.getVelocity().z);
            }
        player.getItemCooldownManager().set(this, skillCooldown);

        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (stepMod > 0) stepMod--;
        if (stepMod <= 0) stepMod = 7;
        HelperMethods.createFootfalls(entity, stack, world, stepMod, ParticleTypes.ENCHANT,
                ParticleTypes.ENCHANT, ParticleTypes.ASH, true);
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        Style RIGHTCLICK = HelperMethods.getStyle("rightclick");
        Style ABILITY = HelperMethods.getStyle("ability");
        Style TEXT = HelperMethods.getStyle("text");

        tooltip.add(Text.literal(""));
        tooltip.add(Text.translatable("item.simplyswords.magibladesworditem.tooltip1").setStyle(ABILITY));
        tooltip.add(Text.translatable("item.simplyswords.magibladesworditem.tooltip2").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.magibladesworditem.tooltip3").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.magibladesworditem.tooltip4").setStyle(TEXT));
        tooltip.add(Text.literal(""));
        tooltip.add(Text.translatable("item.simplyswords.onrightclickheld").setStyle(RIGHTCLICK));
        tooltip.add(Text.translatable("item.simplyswords.magibladesworditem.tooltip5").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.magibladesworditem.tooltip6").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.magibladesworditem.tooltip7").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.magibladesworditem.tooltip8").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.magibladesworditem.tooltip9").setStyle(TEXT));

        super.appendTooltip(itemStack, world, tooltip, tooltipContext);
    }
}
