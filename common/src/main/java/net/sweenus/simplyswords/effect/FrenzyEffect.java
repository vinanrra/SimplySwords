package net.sweenus.simplyswords.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.particle.ParticleTypes;
import net.sweenus.simplyswords.item.custom.WickpiercerSwordItem;
import net.sweenus.simplyswords.registry.EffectRegistry;

public class FrenzyEffect extends OrbitingEffect {
    public FrenzyEffect(StatusEffectCategory statusEffectCategory, int color) {
        super (statusEffectCategory, color);
        setParticleType(ParticleTypes.CLOUD);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.getWorld().isClient()) {
            if (!(livingEntity.getMainHandStack().getItem() instanceof WickpiercerSwordItem) && !(livingEntity.getOffHandStack().getItem() instanceof WickpiercerSwordItem))
                livingEntity.removeStatusEffect(EffectRegistry.FRENZY);
        }
        super.applyUpdateEffect(livingEntity, amplifier);
        return false;
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes) {

    }

    @Override
    public boolean canApplyUpdateEffect(int pDuration, int pAmplifier) {
        return super.canApplyUpdateEffect(pDuration, pAmplifier);
    }
}
