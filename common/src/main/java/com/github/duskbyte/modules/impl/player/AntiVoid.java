package com.github.duskbyte.modules.impl.player;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.PacketEvent;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.DoubleSetting;
import com.github.duskbyte.settings.impl.EnumSetting;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class AntiVoid extends Module {

    public static final AntiVoid INSTANCE = new AntiVoid();

    private enum Mode {
        Packet,
        Motion
    }

    private final EnumSetting<Mode> mode = enumSetting("Mode", Mode.Packet);
    private final DoubleSetting fallDist = doubleSetting("Fall Distance", 5.0, 1.0, 20.0, 0.5);

    private double lastGroundY;
    private final List<ServerboundMovePlayerPacket> queuedPackets = new ArrayList<>();

    private AntiVoid() {
        super("Anti Void", Category.PLAYER);
    }

    @Override
    protected void onDisable() {
        queuedPackets.clear();
        lastGroundY = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;

        if (mc.player.onGround()) {
            lastGroundY = mc.player.getY();
            if (!queuedPackets.isEmpty()) {
                queuedPackets.forEach(p -> mc.player.connection.send(p));
                queuedPackets.clear();
            }
            return;
        }

        if (mc.player.fallDistance < fallDist.getValue().floatValue()) return;
        if (isBlockUnder()) return;

        if (mode.getValue() == Mode.Motion) {
            mc.player.setDeltaMovement(mc.player.getDeltaMovement().x, 0.42, mc.player.getDeltaMovement().z);
            mc.player.fallDistance = 0;
        }
    }

    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        if (nullCheck() || mode.getValue() != Mode.Packet) return;

        if (mc.player.onGround()) return;
        if (mc.player.fallDistance < fallDist.getValue().floatValue()) return;
        if (isBlockUnder()) return;

        if (event.getPacket() instanceof ServerboundMovePlayerPacket playerPacket) {
            event.setCancelled(true);
            if (!queuedPackets.contains(playerPacket)) {
                queuedPackets.add(playerPacket);
            }
        }
    }

    private boolean isBlockUnder() {
        if (mc.player.getY() < mc.level.getMinY()) return false;
        for (int offset = 0; offset < (int) mc.player.getY() + 2; offset += 2) {
            AABB bb = mc.player.getBoundingBox().move(0, -offset, 0);
            if (mc.level.getCollisions(mc.player, bb).iterator().hasNext()) {
                return true;
            }
        }
        return false;
    }

}