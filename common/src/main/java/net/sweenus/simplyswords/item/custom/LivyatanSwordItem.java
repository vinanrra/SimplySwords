package net.sweenus.simplyswords.item.custom;


import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.sweenus.simplyswords.config.SimplySwordsConfig;
import net.sweenus.simplyswords.item.UniqueSwordItem;
import net.sweenus.simplyswords.registry.EffectRegistry;
import net.sweenus.simplyswords.registry.SoundRegistry;
import net.sweenus.simplyswords.util.HelperMethods;

import java.util.List;

public class LivyatanSwordItem extends UniqueSwordItem {
    public LivyatanSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }
    private static int stepMod = 0;
    int radius = (int) (SimplySwordsConfig.getFloatValue("frostshatter_radius"));
    float abilityDamage = (SimplySwordsConfig.getFloatValue("frostshatter_damage"));
    int proc_chance = (int) (SimplySwordsConfig.getFloatValue("frostshatter_chance"));
    int shatter_timer_max = (int) (SimplySwordsConfig.getFloatValue("frostshatter_duration"));
    int shatter_timer;
    int shatter_bonus;
    int player_shatter_timer;
    double lastX;
    double lastY;
    double lastZ;



    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.getWorld().isClient()) {
            ServerWorld world = (ServerWorld) attacker.getWorld();
            HelperMethods.playHitSounds(attacker, target);


            //AOE freeze
            if (attacker.getRandom().nextInt(100) <= proc_chance) {
                Box box = new Box(target.getX() + radius, target.getY() + radius, target.getZ() + radius, target.getX() - radius, target.getY() - radius, target.getZ() - radius);
                for (Entity entities : world.getOtherEntities(attacker, box, EntityPredicates.VALID_LIVING_ENTITY)) {

                    if (entities != null) {
                        if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFire(le, (PlayerEntity) attacker)) {
                            le.addStatusEffect(new StatusEffectInstance(EffectRegistry.FREEZE.get(), shatter_timer_max + 10, 0), attacker);
                            le.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, shatter_timer_max - 10, 4), attacker);
                            world.playSoundFromEntity(null, le, SoundRegistry.ELEMENTAL_BOW_ICE_SHOOT_IMPACT_01.get(), SoundCategory.PLAYERS, 0.1f, 3f);
                            BlockPos pos = BlockPos.ofFloored(le.getX(), le.getY(), le.getZ());
                            BlockPos pos2 = BlockPos.ofFloored(le.getX(), le.getY() + 1, le.getZ());
                            BlockState state = Blocks.ICE.getDefaultState();
                            if (world.getBlockState(pos) == Blocks.AIR.getDefaultState())
                                world.setBlockState(pos, state);
                            if (world.getBlockState(pos2) == Blocks.AIR.getDefaultState())
                                world.setBlockState(pos2, state);
                        }
                    }
                }
                shatter_timer = shatter_timer_max;
            }


        }

            return super.postHit(stack, target, attacker);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {


        if (!user.getWorld().isClient()) {



            if (shatter_timer > 0) {
                shatter_bonus = shatter_timer / 5;
                shatter_timer = 2;
            }

        }
        return super.use(world, user, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (HelperMethods.commonSpellAttributeScaling(
                1.7f,
                entity,
                "frost") > 0)
            abilityDamage = HelperMethods.commonSpellAttributeScaling(
                    1.7f,
                    entity,
                    "frost");
        if (!world.isClient && (entity instanceof PlayerEntity player)) {
            if (shatter_timer > 0)
                shatter_timer --;

            if (shatter_timer == 1) {
                Box box = new Box(player.getX() + radius + 10, player.getY() + radius  + 10, player.getZ() + radius  + 10, player.getX() - radius  - 10, player.getY() - radius  - 10, player.getZ() - radius  - 10);
                for (Entity entities : world.getOtherEntities(player, box, EntityPredicates.VALID_LIVING_ENTITY)) {

                        //Ice shatter
                        if (entities != null) {
                            if (entities instanceof LivingEntity le) {

                                if (le.hasStatusEffect(EffectRegistry.FREEZE.get())) {
                                    le.removeStatusEffect(EffectRegistry.FREEZE.get());
                                    le.removeStatusEffect(StatusEffects.RESISTANCE);
                                    world.playSoundFromEntity(null, le, SoundRegistry.ELEMENTAL_BOW_ICE_SHOOT_IMPACT_02.get(), SoundCategory.PLAYERS, 0.2f, 3f);
                                    le.damage(player.getDamageSources().magic(), abilityDamage + shatter_bonus);
                                }


                                double xpos = le.getX() -2;
                                double ypos = le.getY();
                                double zpos = le.getZ() -2;


                                for (int i = 3; i > 0; i--) {
                                    for (int j = 3; j > 0; j--) {
                                        BlockPos poscheck = BlockPos.ofFloored(xpos+i, ypos, zpos+j);
                                        BlockPos poscheck2 = BlockPos.ofFloored(xpos+i, ypos + 1, zpos+j);
                                        BlockPos poscheck3 = BlockPos.ofFloored(xpos+i, ypos + 2, zpos+j);
                                        BlockPos poscheck4 = BlockPos.ofFloored(xpos+i, ypos - 1, zpos+j);

                                        BlockState currentState = world.getBlockState(poscheck);
                                        BlockState currentState2 = world.getBlockState(poscheck2);
                                        BlockState currentState3 = world.getBlockState(poscheck3);
                                        BlockState currentState4 = world.getBlockState(poscheck4);
                                        BlockState state = Blocks.AIR.getDefaultState();
                                        if (currentState == Blocks.ICE.getDefaultState() || currentState == Blocks.WATER.getDefaultState())
                                            world.setBlockState(poscheck, state);
                                        if (currentState2 == Blocks.ICE.getDefaultState() || currentState2 == Blocks.WATER.getDefaultState())
                                            world.setBlockState(poscheck2, state);
                                        if (currentState3 == Blocks.ICE.getDefaultState() || currentState3 == Blocks.WATER.getDefaultState())
                                            world.setBlockState(poscheck3, state);
                                        if (currentState4 == Blocks.ICE.getDefaultState() || currentState4 == Blocks.WATER.getDefaultState())
                                            world.setBlockState(poscheck4, state);
                                    }
                                }
                            }
                        }
                    }
                shatter_bonus = 0;
                }
        }

        if (stepMod > 0)
            stepMod --;
        if (stepMod <= 0)
            stepMod = 7;
        HelperMethods.createFootfalls(entity, stack, world, stepMod, ParticleTypes.SNOWFLAKE, ParticleTypes.SNOWFLAKE, ParticleTypes.WHITE_ASH, true);

        super.inventoryTick(stack, world, entity, slot, selected);
    }



    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        Style RIGHTCLICK = HelperMethods.getStyle("rightclick");
        Style ABILITY = HelperMethods.getStyle("ability");
        Style TEXT = HelperMethods.getStyle("text");

        tooltip.add(Text.literal(""));
        tooltip.add(Text.translatable("item.simplyswords.livyatansworditem.tooltip1").setStyle(ABILITY));
        tooltip.add(Text.translatable("item.simplyswords.livyatansworditem.tooltip2").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.livyatansworditem.tooltip3").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.livyatansworditem.tooltip4", shatter_timer_max /20, abilityDamage).setStyle(TEXT));
        tooltip.add(Text.literal(""));
        tooltip.add(Text.translatable("item.simplyswords.onrightclick").setStyle(RIGHTCLICK));
        tooltip.add(Text.translatable("item.simplyswords.livyatansworditem.tooltip5").setStyle(TEXT));
        tooltip.add(Text.translatable("item.simplyswords.livyatansworditem.tooltip6").setStyle(TEXT));
        tooltip.add(Text.literal(""));

        super.appendTooltip(itemStack,world, tooltip, tooltipContext);
    }

}
