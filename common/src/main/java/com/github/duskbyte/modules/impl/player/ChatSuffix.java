package com.github.duskbyte.modules.impl.player;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.PacketEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.StringSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundChatPacket;

public class ChatSuffix extends Module {

    public static final ChatSuffix INSTANCE = new ChatSuffix();

    public final StringSetting suffix = stringSetting("Suffix", " | DuskByte");
    public final StringSetting prefix = stringSetting("Prefix", "");

    private ChatSuffix() {
        super("Chat Suffix", Category.PLAYER);
    }

    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        if (!isEnabled()) return;
        if (!(event.getPacket() instanceof ServerboundChatPacket chatPacket)) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        String message = chatPacket.message();
        if (message.startsWith("/")) return;

        String prefixText = prefix.getValue();
        String suffixText = suffix.getValue();

        StringBuilder sb = new StringBuilder();
        if (!prefixText.isEmpty()) {
            sb.append(prefixText).append(" ");
        }
        sb.append(message);
        if (!suffixText.isEmpty()) {
            sb.append(" ").append(suffixText);
        }

        String result = sb.toString();
        // Minecraft chat limit is 256 characters
        if (result.length() > 256) {
            result = result.substring(0, 256);
        }

        event.setPacket(new ServerboundChatPacket(
                result,
                chatPacket.timeStamp(),
                chatPacket.salt(),
                chatPacket.signature(),
                chatPacket.lastSeenMessages()
        ));
    }
}
