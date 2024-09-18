package net.sweenus.simplyswords.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class ResilienceEffect extends StatusEffect {
    public ResilienceEffect(StatusEffectCategory statusEffectCategory, int color) {
        super (statusEffectCategory, color);
    }
    @Override
    public boolean applyUpdateEffect(LivingEntity pLivingEntity, int pAmplifier) {

        super.applyUpdateEffect(pLivingEntity, pAmplifier);

        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int pDuration, int pAmplifier) {
        return true;
    }
}
