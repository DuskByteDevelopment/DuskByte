package com.github.duskbyte.modules.impl.combat;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.AttackEntityEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.BoolSetting;
import com.github.duskbyte.settings.impl.DoubleSetting;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.phys.Vec3;

public class Criticals extends Module {

    public static final Criticals INSTANCE = new Criticals();

    private final BoolSetting jump = boolSetting("Jump", true);
    private final DoubleSetting fallDistance = doubleSetting("Fall Distance", 0.42, 0.1, 1.0, 0.01);

    private Criticals() {
        super("Criticals", Category.COMBAT);
    }

    @EventHandler
    private void onAttack(AttackEntityEvent event) {
        if (nullCheck()) return;
        if (mc.player.isInLava() || mc.player.isInWater()) return;
        if (mc.player.getAbilities().flying) return;
        if (mc.player.fallDistance > 0.0f) return;
        if (!mc.player.onGround()) return;

        if (jump.getValue()) {
            Vec3 pos = mc.player.position();

            // Packet jumping to trigger critical hits server-side
            mc.getConnection().send(new ServerboundMovePlayerPacket.Pos(
                    pos.x, pos.y + 0.0625, pos.z, false, false
            ));
            mc.getConnection().send(new ServerboundMovePlayerPacket.Pos(
                    pos.x, pos.y - fallDistance.getValue().floatValue(), pos.z, false, false
            ));
            mc.getConnection().send(new ServerboundMovePlayerPacket.Pos(
                    pos.x, pos.y, pos.z, mc.player.onGround(), true
            ));
        }
    }
}
