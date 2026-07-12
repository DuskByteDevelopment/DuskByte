package com.github.duskbyte.modules.impl.render;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class XRay extends Module {

    public static final XRay INSTANCE = new XRay();

    private XRay() {
        super("XRay", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        if (mc.level != null) {
            mc.levelRenderer.allChanged();
        }
    }

    @Override
    protected void onDisable() {
        if (mc.player != null) {
            mc.player.removeEffect(MobEffects.NIGHT_VISION);
            if (mc.level != null) {
                mc.levelRenderer.allChanged();
                for (Player player : mc.level.players()) {
                    if (player != mc.player) {
                        player.setGlowingTag(false);
                    }
                }
            }
        }
        super.onDisable();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;

        // Give night vision to see clearly
        mc.player.addEffect(new MobEffectInstance(
                MobEffects.NIGHT_VISION, 200, 1, false, false, false
        ));

        // Make all players visible through walls
        for (Player player : mc.level.players()) {
            if (player != mc.player) {
                player.setGlowingTag(true);
            }
        }
    }

}