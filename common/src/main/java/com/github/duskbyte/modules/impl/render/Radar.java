package com.github.duskbyte.modules.impl.render;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.Render2DEvent;
import com.github.duskbyte.events.impl.Render3DEvent;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.graphics.renderers.TextRenderer;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.BoolSetting;
import com.github.duskbyte.settings.impl.ColorSetting;
import com.github.duskbyte.settings.impl.DoubleSetting;
import com.github.duskbyte.utils.render.Render3DUtils;
import com.google.common.base.Suppliers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.awt.Color;
import java.util.function.Supplier;

public class Radar extends Module {

    public static final Radar INSTANCE = new Radar();

    private final DoubleSetting radius = doubleSetting("Radius", 40.0, 20.0, 100.0, 5.0);
    private final DoubleSetting scale = doubleSetting("Scale", 1.0, 0.5, 2.0, 0.1);
    private final ColorSetting backgroundColor = colorSetting("Background Color", new Color(0, 0, 0, 150));
    private final ColorSetting playerColor = colorSetting("Player Color", new Color(0, 255, 0));
    private final BoolSetting showPlayers = boolSetting("Show Players", true);
    private final BoolSetting showHostile = boolSetting("Show Hostile", true);

    private final Supplier<TextRenderer> textRendererSupplier = Suppliers.memoize(TextRenderer::new);

    private Radar() {
        super("Radar", Category.RENDER);
    }

    @EventHandler
    private void onRender2D(Render2DEvent.HUD event) {
        if (nullCheck()) return;

        TextRenderer textRenderer = textRendererSupplier.get();

        float rad = radius.getValue().floatValue() * scale.getValue().floatValue();
        float centerX = mc.getWindow().getGuiScaledWidth() - rad - 10.0f;
        float centerY = rad + 10.0f;

        // Draw radar background circle
        // Since we can't draw circles easily with the text renderer, we'll draw a representation

        // Draw player dots
        for (Player player : mc.level.players()) {
            if (player == mc.player) continue;
            if (!showPlayers.getValue()) continue;

            double dx = player.getX() - mc.player.getX();
            double dz = player.getZ() - mc.player.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);

            if (dist > 15) continue;

            // Scale to radar coordinates
            float dotX = centerX + (float) (dx / 15.0 * rad);
            float dotZ = centerY + (float) (dz / 15.0 * rad);

            // Draw dot
            textRenderer.addText(".", dotX - 2, dotZ - 2, 0.5f, playerColor.getValue());
        }

        // Draw center marker (self)
        textRenderer.addText("+", centerX - 3, centerY - 3, 0.6f, Color.WHITE);

        textRenderer.drawAndClear();
    }

}