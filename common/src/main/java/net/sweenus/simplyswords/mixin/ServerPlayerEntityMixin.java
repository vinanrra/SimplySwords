package net.sweenus.simplyswords.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.sweenus.simplyswords.config.Config;
import net.sweenus.simplyswords.config.ConfigDefaultValues;
import net.sweenus.simplyswords.registry.EffectRegistry;
import net.sweenus.simplyswords.registry.ItemsRegistry;
import net.sweenus.simplyswords.registry.SoundRegistry;
import net.sweenus.simplyswords.util.HelperMethods;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Inject(at = @At("HEAD"), method = "damage", cancellable = true)
    public void simplyswords$damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player instanceof ServerPlayerEntity serverPlayer) {

            //Effect Resilience
            if (serverPlayer.hasStatusEffect(EffectRegistry.RESILIENCE.get())) {
                HelperMethods.decrementStatusEffect(serverPlayer, EffectRegistry.RESILIENCE.get());
                cir.setReturnValue(false);
                serverPlayer.getWorld().playSoundFromEntity(null, serverPlayer, SoundRegistry.MAGIC_SWORD_PARRY_03.get(),
                        SoundCategory.PLAYERS, 0.7f, 0.5f + (serverPlayer.getRandom().nextBetween(1, 5) * 0.1f));
            }

            // Magiscythe creation
            if (source.toString().contains("sonic_boom")) {
                for (int i = 0; i < serverPlayer.getInventory().size(); i++) {
                    ItemStack stackInSlot = serverPlayer.getInventory().getStack(i);
                    if (stackInSlot.isOf(ItemsRegistry.DECAYING_RELIC.get())) {
                        ItemStack newItemStack = new ItemStack(ItemsRegistry.MAGISCYTHE.get());
                        serverPlayer.getInventory().setStack(i, newItemStack);
                        serverPlayer.getWorld().playSoundFromEntity(null, serverPlayer, SoundRegistry.ELEMENTAL_BOW_SCIFI_SHOOT_IMPACT_02.get(),
                                serverPlayer.getSoundCategory(), 0.6f, 0.6f);
                        serverPlayer.sendMessageToClient(Text.translatable("item.simplyswords.magicythe.event"), true);
                        break;
                    }
                }
            }

        }
    }


    @Inject(at = @At("HEAD"), method = "tick")
    public void simplyswords$tick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player instanceof ServerPlayerEntity serverPlayer) {

            //Ribboncleaver movespeed debuff
            if (serverPlayer.getMainHandStack().isOf(ItemsRegistry.RIBBONCLEAVER.get()) || serverPlayer.getMainHandStack().isOf(ItemsRegistry.ENIGMA.get())) {
                int frequency = 6;
                if (serverPlayer.age % 20 == 0 && serverPlayer.getMainHandStack().isOf(ItemsRegistry.RIBBONCLEAVER.get()))
                    serverPlayer.addStatusEffect(new StatusEffectInstance(EffectRegistry.RIBBONWRATH.get(),
                            30, 0, true, false, false));

                if (player.age % frequency == 0 && player.isSprinting() && player.isOnGround()) {
                    float volume = 0.3f;
                    float pitch = 1.0f + player.getRandom().nextBetween(1, 5) * 0.1f;
                    player.getWorld().playSound(null, player.getBlockPos(),
                            SoundRegistry.OBJECT_IMPACT_THUD.get(), SoundCategory.PLAYERS,volume, pitch);
                }
            }

            // Contained Remnant logic
            int frequency = 6000; // 5m
            if (serverPlayer.age % frequency == 0) {
                ItemStack containedRemnant = ItemsRegistry.CONTAINED_REMNANT.get().asItem().getDefaultStack();
                ItemStack tamperedRemnant = ItemsRegistry.TAMPERED_REMNANT.get().asItem().getDefaultStack();
                ItemStack decayingRelic = ItemsRegistry.DECAYING_RELIC.get().asItem().getDefaultStack();
                ItemStack runicTablet = ItemsRegistry.RUNIC_TABLET.get().asItem().getDefaultStack();
                Random random = new Random();
                TagKey<Item> desiredItemsTag = TagKey.of(Registries.ITEM.getKey(), new Identifier("simplyswords", "conditional_uniques_type_1"));
                TagKey<Item> endItemsTag = TagKey.of(Registries.ITEM.getKey(), new Identifier("simplyswords", "conditional_uniques_type_2"));

                for (int i = 0; i < serverPlayer.getInventory().size(); i++) {
                    ItemStack stackInSlot = serverPlayer.getInventory().getStack(i);

                    if (stackInSlot.isOf(containedRemnant.getItem()) || stackInSlot.isOf(tamperedRemnant.getItem())) {
                        int chance = random.nextInt(100);
                        if (chance < 6 && Config.getBoolean("enableContainedRemnants", "Loot", ConfigDefaultValues.enableContainedRemnants)) {
                            List<Item> itemsFromTag = Registries.ITEM.stream()
                                    .filter(item -> item.getDefaultStack().isIn(desiredItemsTag))
                                    .toList();
                            List<Item> itemsFromTagEnd = Registries.ITEM.stream()
                                    .filter(item -> item.getDefaultStack().isIn(endItemsTag))
                                    .toList();

                            if (!itemsFromTag.isEmpty() && !itemsFromTagEnd.isEmpty()) {
                                Item randomItem = ItemsRegistry.TAMPERED_REMNANT.get();
                                if (serverPlayer.getWorld().getRegistryKey().equals(World.END)
                                        && serverPlayer.getInventory().getStack(i).isOf(containedRemnant.getItem())
                                        && serverPlayer.getInventory().contains(runicTablet)) {
                                    serverPlayer.getWorld().playSoundFromEntity(null, serverPlayer, SoundRegistry.ELEMENTAL_BOW_SCIFI_SHOOT_IMPACT_03.get(),
                                            serverPlayer.getSoundCategory(), 0.3f, 0.6f);
                                    serverPlayer.sendMessageToClient(Text.translatable("item.simplyswords.contained_remnant.event2"), true);
                                } else {
                                    if (serverPlayer.getInventory().getStack(i).isOf(ItemsRegistry.CONTAINED_REMNANT.get()))
                                        randomItem = itemsFromTag.get(random.nextInt(itemsFromTag.size()));
                                    else if (serverPlayer.getInventory().getStack(i).isOf(ItemsRegistry.TAMPERED_REMNANT.get()))
                                        randomItem = itemsFromTagEnd.get(random.nextInt(itemsFromTagEnd.size()));
                                    serverPlayer.getWorld().playSoundFromEntity(null, serverPlayer, SoundRegistry.ELEMENTAL_BOW_SCIFI_SHOOT_IMPACT_02.get(),
                                            serverPlayer.getSoundCategory(), 0.3f, 0.6f);
                                    serverPlayer.sendMessageToClient(Text.translatable("item.simplyswords.contained_remnant.event"), true);
                                }
                                ItemStack newItemStack = new ItemStack(randomItem);
                                serverPlayer.getInventory().setStack(i, newItemStack);
                                break;
                            }
                        }
                    }
                    BlockState playerStandingBlock = serverPlayer.getSteppingBlockState();
                    if (stackInSlot.isOf(decayingRelic.getItem()) && playerStandingBlock.isOf(Blocks.SCULK)) {
                        serverPlayer.sendMessageToClient(Text.translatable("item.simplyswords.magicythe.event2"), true);
                    }
                }
            }
        }
    }


    @Inject(at = @At("TAIL"), method = "attack")
    public void simplyswords$attack(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player instanceof ServerPlayerEntity serverPlayer) {
            if (target.isAttackable()) {
                if (!target.handleAttack(player)) {
                    ServerWorld serverWorld = (ServerWorld) player.getWorld();
                    //Ribboncleaver Cleave buff
                    if (serverPlayer.hasStatusEffect(EffectRegistry.RIBBONCLEAVE.get())) {
                        serverPlayer.removeStatusEffect(EffectRegistry.RIBBONCLEAVE.get());
                        HelperMethods.spawnOrbitParticles(serverWorld, target.getPos().add(0, 0.3, 0),
                                ParticleTypes.POOF, 0.5, 6);
                        HelperMethods.spawnOrbitParticles(serverWorld, target.getPos().add(0, 0.5, 0),
                                ParticleTypes.ENCHANTED_HIT, 0.5, 6);
                        serverWorld.playSound(null, target.getBlockPos(),
                                SoundRegistry.MAGIC_SWORD_PARRY_01.get(), SoundCategory.PLAYERS,0.8f, 1.0f);
                    }

                }
            }
        }
    }


}
