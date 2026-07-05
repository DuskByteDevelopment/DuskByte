package com.github.duskbyte.modules.impl.player;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.BoolSetting;
import com.github.duskbyte.settings.impl.EnumSetting;
import com.github.duskbyte.settings.impl.IntSetting;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.InteractionHand;

import java.util.Random;

public class AntiAFK extends Module {

    public static final AntiAFK INSTANCE = new AntiAFK();

    public enum ActionMode {
        RANDOM,
        SEQUENCE
    }

    private final EnumSetting<ActionMode> mode = enumSetting("Mode", ActionMode.RANDOM);
    private final IntSetting delay = intSetting("Delay", 12, 3, 60, 1);
    private final BoolSetting rotate = boolSetting("Rotate", true);
    private final BoolSetting jump = boolSetting("Jump", true);
    private final BoolSetting sneak = boolSetting("Sneak", false);
    private final BoolSetting swing = boolSetting("Swing", true);
    private final BoolSetting autoDisable = boolSetting("Auto Disable", false);

    private final Random random = new Random();
    private int tickCounter = 0;
    private int actionIndex = 0;

    private AntiAFK() {
        super("Anti AFK", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        tickCounter = 0;
        actionIndex = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;

        tickCounter++;
        int targetDelay = mode.getValue() == ActionMode.RANDOM
                ? (delay.getValue() + random.nextInt(Math.max(delay.getValue(), 3))) * 20
                : delay.getValue() * 20;

        if (tickCounter < targetDelay) return;
        tickCounter = 0;

        if (mc.player == null || mc.getConnection() == null) return;

        doAction();
    }

    private void doAction() {
        if (mode.getValue() == ActionMode.RANDOM) {
            int action = random.nextInt(4);

            switch (action) {
                case 0 -> doRotate();
                case 1 -> doJump();
                case 2 -> {
                    doRotate();
                    doJump();
                }
                case 3 -> {
                    if (sneak.getValue()) doSneak();
                    else doRotate();
                }
            }

            // 随机挥动手
            if (swing.getValue() && random.nextBoolean()) {
                mc.player.swing(InteractionHand.MAIN_HAND);
            }

            // 随机额外旋转
            if (rotate.getValue() && random.nextInt(3) == 0) {
                doRotate();
            }
        } else {
            // SEQUENCE 模式：轮流执行
            switch (actionIndex % 4) {
                case 0 -> doRotate();
                case 1 -> doJump();
                case 2 -> doRotate();
                case 3 -> {
                    if (sneak.getValue()) doSneak();
                    else doJump();
                }
            }

            if (swing.getValue()) {
                mc.player.swing(InteractionHand.MAIN_HAND);
            }

            actionIndex++;
        }
    }

    private void doRotate() {
        float yaw = mc.player.getYRot() + 60.0f + random.nextFloat() * 60.0f;
        mc.player.setYRot(yaw);
        mc.getConnection().send(new ServerboundMovePlayerPacket.Rot(
                yaw,
                mc.player.getXRot(),
                mc.player.onGround(),
                true
        ));
    }

    private void doJump() {
        if (mc.player.onGround()) {
            mc.player.jumpFromGround();
        }
    }

    private void doSneak() {
        mc.player.setShiftKeyDown(!mc.player.isShiftKeyDown());
    }
}
