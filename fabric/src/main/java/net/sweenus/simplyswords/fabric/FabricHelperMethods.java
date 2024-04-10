package net.sweenus.simplyswords.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchools;

public class FabricHelperMethods {


    //Compatibility with Spell Power Attributes
    public static float useSpellAttributeScaling(float damageModifier, PlayerEntity player, String magicSchool) {
        if (FabricLoader.getInstance().isModLoaded("spell_power")) {
            if (player != null && !player.getWorld().isClient) {

                double attributePower = 0;
                double damageOutput = 0.1;

                // Fetch attributes (crit damage/chance is now handled internally in API via randomValue)
                double lightningPower = SpellPower.getSpellPower(SpellSchools.LIGHTNING, player).randomValue();
                double firePower =      SpellPower.getSpellPower(SpellSchools.FIRE, player).randomValue();
                double frostPower =     SpellPower.getSpellPower(SpellSchools.FROST, player).randomValue();
                double arcanePower =    SpellPower.getSpellPower(SpellSchools.ARCANE, player).randomValue();
                double soulPower =      SpellPower.getSpellPower(SpellSchools.SOUL, player).randomValue();
                double healingPower =   SpellPower.getSpellPower(SpellSchools.HEALING, player).randomValue();

                if (magicSchool.contains("lightning"))
                    attributePower = lightningPower;
                else if (magicSchool.contains("fire"))
                    attributePower = firePower;
                else if (magicSchool.contains("frost"))
                    attributePower = frostPower;
                else if (magicSchool.contains("arcane"))
                    attributePower = arcanePower;
                else if (magicSchool.contains("soul"))
                    attributePower = soulPower;
                else if (magicSchool.contains("healing"))
                    attributePower = healingPower;


                damageOutput = (damageModifier * attributePower);

                return (float) damageOutput;
            }
        }
        return 0;
    }
}
