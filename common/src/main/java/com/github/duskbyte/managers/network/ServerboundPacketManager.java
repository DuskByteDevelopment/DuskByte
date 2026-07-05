package com.github.duskbyte.managers.network;

import com.github.duskbyte.events.bus.EventBus;
import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.WorldEvent;
import com.github.duskbyte.utils.player.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.Packet;

import java.util.concurrent.LinkedBlockingQueue;

// This is BlinkManager
public class ServerboundPacketManager {

    public static final ServerboundPacketManager INSTANCE = new ServerboundPacketManager();

    private ServerboundPacketManager() {
        EventBus.INSTANCE.subscribe(this);
    }

    public final LinkedBlockingQueue<Packet<?>> packets = new LinkedBlockingQueue<>();

    public boolean blinking = false;
    static boolean forceFlush;

    @EventHandler
    private void onWorldChange(WorldEvent event) {
        forceFlush = true;
        blinking = false;
    }

    public void flush() {
        while (!packets.isEmpty()) {
            try {
                Minecraft.getInstance().getConnection().send(packets.poll());
            } catch (Exception e) {
                ChatUtils.addChatMessage("failed to flush serverbound packets: " + e.getMessage());
            }
        }
    }

    public void stopBlinking() {
        blinking = false;
    }

    public void startBlinking() {
        blinking = true;
    }

    public boolean onPacketSend(Packet<?> packet) {
        if (forceFlush) {
            flush();
            forceFlush = false;
            return false;
        }

        if (!blinking) return false;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return false;

        packets.add(packet);
        return true;
    }

}
