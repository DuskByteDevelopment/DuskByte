package com.github.duskbyte.modules.impl.misc;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.PacketEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.BoolSetting;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;

public class AntiSpam extends Module {

    public static final AntiSpam INSTANCE = new AntiSpam();

    private final BoolSetting hideDuplicates = boolSetting("Hide Duplicates", true);
    private String lastMessage = "";

    private AntiSpam() {
        super("Anti Spam", Category.MISC);
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event) {
        if (nullCheck()) return;

        String message = "";

        if (event.getPacket() instanceof ClientboundPlayerChatPacket chatPacket) {
            message = chatPacket.body().content();
        } else if (event.getPacket() instanceof ClientboundSystemChatPacket systemPacket) {
            message = systemPacket.content().getString();
        }

        if (!message.isEmpty()) {
            if (hideDuplicates.getValue() && message.equals(lastMessage)) {
                event.setCancelled(true);
            }
            lastMessage = message;
        }
    }
}
