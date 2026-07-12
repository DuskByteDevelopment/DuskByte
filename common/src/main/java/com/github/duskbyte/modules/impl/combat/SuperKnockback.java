package com.github.duskbyte.modules.impl.combat;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.AttackEntityEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class SuperKnockback extends Module {

    public static final SuperKnockback INSTANCE = new SuperKnockback();

    private SuperKnockback() {
        super("Super Knockback", Category.COMBAT);
    }

    @EventHandler
    private void onAttack(AttackEntityEvent event) {
        if (nullCheck()) return;
        if (!(event.getEntity() instanceof LivingEntity living)) return;

        // Apply a brief strength effect to increase knockback
        living.addEffect(new MobEffectInstance(
                MobEffects.WEAKNESS, 1, 250, false, false, false
        ));
    }

}