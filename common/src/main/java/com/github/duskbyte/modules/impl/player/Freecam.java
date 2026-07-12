package com.github.duskbyte.modules.impl.player;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.PacketEvent;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class Freecam extends Module {

    public static final Freecam INSTANCE = new Freecam();

    private double startX, startY, startZ;
    private float startYaw, startPitch;
    private Player dummyPlayer;

    private Freecam() {
        super("Freecam", Category.PLAYER);
    }

    @Override
    protected void onEnable() {
        if (nullCheck()) return;
        startX = mc.player.getX();
        startY = mc.player.getY();
        startZ = mc.player.getZ();
        startYaw = mc.player.getYRot();
        startPitch = mc.player.getXRot();

        mc.player.setNoGravity(true);
        // Make the real player invisible and still
        mc.player.setInvisible(true);
    }

    @Override
    protected void onDisable() {
        if (mc.player == null) return;
        mc.player.setNoGravity(false);
        mc.player.setInvisible(false);
        mc.player.setPos(startX, startY, startZ);
        mc.player.setYRot(startYaw);
        mc.player.setXRot(startPitch);
        mc.player.setYHeadRot(startYaw);

        // Reset motion
        mc.player.setDeltaMovement(0, 0, 0);
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;

        // Free movement
        double speed = 0.5;
        if (mc.options.keyShift.isDown()) speed *= 5;
        if (mc.options.keySprint.isDown()) speed *= 2;

        // The player can move freely in the air
        mc.player.setNoGravity(true);

        // Prevent the player from being pushed
        mc.player.noPhysics = true;
    }

    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof ServerboundMovePlayerPacket) {
            event.setCancelled(true);
        }
    }

}