package net.sweenus.simplyswords.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
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
import net.sweenus.simplyswords.registry.SoundRegistry;
import net.sweenus.simplyswords.util.HelperMethods;

import java.util.List;
import java.util.Optional;

public class EmberIreSwordItem extends UniqueSwordItem {
    public EmberIreSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    private static int stepMod = 0;
    private static DefaultParticleType particleWalk = ParticleTypes.FALLING_LAVA;
    private static DefaultParticleType particleSprint = ParticleTypes.FALLING_LAVA;
    private static DefaultParticleType particlePassive = ParticleTypes.SMOKE;

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
        if (!world.isClient && remainingUseTicks %10 == 0 && remainingUseTicks < getMaxUseTime(stack) - 5) {
            world.playSoundFromEntity(null, user, SoundRegistry.ELEMENTAL_BOW_RECHARGE.get(),
                    user.getSoundCategory(), 0.2f, 1.1f - (remainingUseTicks * 0.001f));

            if (remainingUseTicks < 20) {
                onStoppedUsing(stack, world, user, remainingUseTicks);
            }

        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!world.isClient && user.getEquippedStack(EquipmentSlot.MAINHAND) == stack) {
            Optional<LivingEntity> targetEntityReturn = HelperMethods.findClosestTarget(user, 18, 3);
            double damageAmount = HelperMethods.getAttackDamage(stack) * 0.3;
            if (targetEntityReturn.isPresent() && HelperMethods.checkFriendlyFire(targetEntityReturn.get(), user)) {
                LivingEntity targetEntity = targetEntityReturn.get();
                SoundEvent soundSelect = SoundRegistry.ELEMENTAL_BOW_FIRE_SHOOT_IMPACT_03.get();
                int particleCount = 20;
                HelperMethods.spawnWaistHeightParticles((ServerWorld) world, ParticleTypes.SMOKE, user, targetEntity, particleCount);
                HelperMethods.spawnWaistHeightParticles((ServerWorld) world, ParticleTypes.POOF, user, targetEntity, particleCount);
                HelperMethods.spawnWaistHeightParticles((ServerWorld) world, ParticleTypes.ASH, user, targetEntity, particleCount);
                world.playSound(null, user.getBlockPos(), soundSelect,
                        user.getSoundCategory(), 0.4f, 1.5f);
                DamageSource damageSource = user.getDamageSources().generic();
                if (user instanceof PlayerEntity player) {
                    damageSource = user.getDamageSources().playerAttack(player);
                    player.getItemCooldownManager().set(stack.getItem(), 10);
                }

                final float minAdditionalDamage = 0.0f;
                final float maxAdditionalDamage = (float) (HelperMethods.getAttackDamage(stack) * 3);
                float chargeRatio = 1.0f - ((float) remainingUseTicks / getMaxUseTime(stack));
                float additionalDamage = minAdditionalDamage + (maxAdditionalDamage - minAdditionalDamage) * chargeRatio;
                float finalDamage = (float) damageAmount + additionalDamage;
                targetEntity.timeUntilRegen = 0;
                targetEntity.damage(damageSource, finalDamage);

                world.playSound(null, targetEntity.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE,
                        user.getSoundCategory(), 0.4f, 1.1f);
                HelperMethods.spawnOrbitParticles((ServerWorld) world, targetEntity.getPos(), ParticleTypes.EXPLOSION, 1, 1 );
                HelperMethods.spawnOrbitParticles((ServerWorld) world, targetEntity.getPos(), ParticleTypes.POOF, 1, 20 );
                user.setVelocity(user.getRotationVector().negate().multiply(+1.1));
                user.setVelocity(user.getVelocity().x, 0, user.getVelocity().z);
                user.velocityModified = true;

                int fhitchance = (int) Config.getFloat("emberIreChance", "UniqueEffects", ConfigDefaultValues.emberIreChance);
                int fduration = (int) Config.getFloat("emberIreDuration", "UniqueEffects", ConfigDefaultValues.emberIreDuration);

                if (user.getRandom().nextInt((int) (250 - (chargeRatio * 100))) <= fhitchance) {
                    user.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, fduration, 0), user);
                    user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, fduration, 1), user);
                    user.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, fduration, 0), user);
                    world.playSoundFromEntity(null, user, SoundRegistry.MAGIC_SWORD_SPELL_01.get(),
                            user.getSoundCategory(), 0.5f, 2f);
                    particlePassive = ParticleTypes.LAVA;
                    particleWalk = ParticleTypes.CAMPFIRE_COSY_SMOKE;
                    particleSprint = ParticleTypes.CAMPFIRE_COSY_SMOKE;
                }

            }
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 80;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if ((entity instanceof PlayerEntity player)) {
            if (!player.hasStatusEffect(StatusEffects.STRENGTH) && !player.isOnFire()) {
                particlePassive = ParticleTypes.SMOKE;
                particleWalk = ParticleTypes.FALLING_LAVA;
                particleSprint = ParticleTypes.FALLING_LAVA;
            }
        }
        if (stepMod > 0) stepMod--;
        if (stepMod <= 0) stepMod = 7;
        HelperMethods.createFootfalls(entity, stack, world, stepMod, particleWalk, particleSprint, particlePassive, true);
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        Style RIGHTCLICK = HelperMethods.getStyle("rightclick");
        Style ABILITY = HelperMethods.getStyle("ability");
        Style TEXT = HelperMethods.getStyle("text");

        tooltip.add(Text.literal(""));
        tooltip.add(Text.translatable("item.simplyswords.emberiresworditem.tooltip1").setStyle(ABILITY));
        tooltip.add(Text.literal(""));
        tooltip.add(Text.translatable("item.simplyswords.onrightclickheld").setStyle(RIGHTCLICK));
        tooltip.add(Text.translatable("item.simplyswords.emberiresworditem.tooltip2").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.emberiresworditem.tooltip3").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.emberiresworditem.tooltip4").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.emberiresworditem.tooltip5").setStyle(TEXT));
        tooltip.add(Text.literal(""));
        tooltip.add(Text.translatable("item.simplyswords.emberiresworditem.tooltip6").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.emberiresworditem.tooltip7").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.emberiresworditem.tooltip8").setStyle(TEXT));
        tooltip.add(Text.literal(""));
        tooltip.add(Text.translatable("item.simplyswords.emberiresworditem.tooltip9").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.emberiresworditem.tooltip10").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.emberiresworditem.tooltip11").setStyle(TEXT));

        super.appendTooltip(itemStack, world, tooltip, tooltipContext);
    }
}
