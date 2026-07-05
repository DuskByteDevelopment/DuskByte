package com.github.duskbyte.modules.impl.movement;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.KeyboardInputEvent;
import com.github.duskbyte.events.impl.SendPositionEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.DoubleSetting;
import com.github.duskbyte.settings.impl.EnumSetting;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

public class NoFall extends Module {

    public static final NoFall INSTANCE = new NoFall();

    private NoFall() {
        super("No Fall", Category.MOVEMENT);
    }

    private enum Mode {
        GroundSpoof,
        Packet,
        GrimMotion
    }

    private final EnumSetting<Mode> mode = enumSetting("Mode", Mode.GroundSpoof);
    private final DoubleSetting fallDistance = doubleSetting("Fall Distance", 3, 3, 16, 1);

    private boolean flag;
    private boolean jump;

    @EventHandler
    private void onMotion(SendPositionEvent event) {
        if (nullCheck()) return;

        if (mc.player.fallDistance > fallDistance.getValue()) {
            flag = true;
        }

        if (flag && mc.player.onGround()) {
            switch (mode.getValue()) {
                case GroundSpoof -> event.setOnGround(false);
                case Packet -> mc.getConnection().send(new ServerboundMovePlayerPacket.StatusOnly(false, false));
                case GrimMotion -> {
                    event.setY(event.getY() + 0.1);
                    jump = true;
                }
            }
            flag = false;
        }
    }

    @EventHandler
    private void onMovementInputEvent(KeyboardInputEvent event) {
        if (jump) {
            event.setJump(true);
            jump = false;
        }
    }

}
