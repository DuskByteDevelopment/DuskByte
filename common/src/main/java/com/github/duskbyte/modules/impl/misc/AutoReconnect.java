package com.github.duskbyte.modules.impl.misc;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.IntSetting;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;

public class AutoReconnect extends Module {

    public static final AutoReconnect INSTANCE = new AutoReconnect();

    private final IntSetting delay = intSetting("Delay", 3, 0, 30, 1);

    private int tickCounter = 0;
    private boolean disconnected = false;

    private AutoReconnect() {
        super("Auto Reconnect", Category.MISC);
    }

    @Override
    protected void onDisable() {
        tickCounter = 0;
        disconnected = false;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player != null && mc.level != null) {
            disconnected = false;
            tickCounter = 0;
            return;
        }

        if (!(mc.screen instanceof DisconnectedScreen) && !disconnected) return;

        if (!disconnected) {
            disconnected = true;
            tickCounter = 0;
        }

        tickCounter++;
        if (tickCounter >= delay.getValue() * 20) {
            mc.setScreen(new JoinMultiplayerScreen(null));
            disconnected = false;
            tickCounter = 0;
        }
    }
}
