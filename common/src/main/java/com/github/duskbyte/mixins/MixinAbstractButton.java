package com.github.duskbyte.mixins;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.input.MouseButtonEvent;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractButton.class)
public class MixinAbstractButton {

    @Unique
    private float duskbyte$scaleAnim = 1.0f;

    @Unique
    private float duskbyte$ripple = -1.0f;

    @Unique
    private float duskbyte$hoverAnim = 0.0f;

    @Inject(method = "extractWidgetRenderState", at = @At("HEAD"))
    private void onExtractWidgetRenderStateHead(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        AbstractButton button = (AbstractButton) (Object) this;

        boolean hovered = button.active && (button.isHoveredOrFocused() || button.isFocused());

        // Background hover animation
        float targetHover = hovered ? 1.0f : 0.0f;
        duskbyte$hoverAnim += (targetHover - duskbyte$hoverAnim) * 0.12f;
        float hp = duskbyte$hoverAnim;

        // Scale animation
        float targetScale = hovered ? 1.06f : 1.0f;
        duskbyte$scaleAnim += (targetScale - duskbyte$scaleAnim) * 0.2f;
        float scale = duskbyte$scaleAnim;

        if (Math.abs(scale - 1.0f) > 0.001f) {
            Matrix3x2fStack pose = graphics.pose();
            pose.pushMatrix();
            float cx = button.getX() + button.getWidth() / 2.0f;
            float cy = button.getY() + button.getHeight() / 2.0f;
            pose.translate(cx, cy);
            pose.scale(scale, scale);
            pose.translate(-cx, -cy);
        }

        // Ripple animation
        if (duskbyte$ripple >= 0.0f) {
            duskbyte$ripple += 0.05f;
            if (duskbyte$ripple > 1.0f) {
                duskbyte$ripple = -1.0f;
            }
        }
    }

    @Inject(method = "extractWidgetRenderState", at = @At("RETURN"))
    private void onExtractWidgetRenderStateReturn(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        float scale = duskbyte$scaleAnim;
        if (Math.abs(scale - 1.0f) > 0.001f) {
            graphics.pose().popMatrix();
        }
    }

    @Inject(method = "extractDefaultSprite", at = @At("HEAD"), cancellable = true)
    private void onExtractDefaultSprite(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        AbstractButton button = (AbstractButton) (Object) this;

        int x = button.getX();
        int y = button.getY();
        int w = button.getWidth();
        int h = button.getHeight();

        boolean hovered = button.isHoveredOrFocused() || button.isFocused();
        float hp = duskbyte$hoverAnim;

        // Light blue -> pink smooth background
        int bgR = lerp(0x4A, 0xFF, hp);
        int bgG = lerp(0x7A, 0x69, hp);
        int bgB = lerp(0xB5, 0xB4, hp);
        int bgColor = button.active ? (0xFF000000 | (bgR << 16) | (bgG << 8) | bgB) : 0xFF2A4A6A;

        // Border: cyan -> pink
        int bR = lerp(0x00, 0xFF, hp);
        int bG = lerp(0xD4, 0x00, hp);
        int bB = lerp(0xFF, 0x66, hp);
        int borderColor = button.active ? (0xFF000000 | (bR << 16) | (bG << 8) | bB) : 0xFF1A1A3A;

        // Background
        graphics.fill(x, y, x + w, y + h, bgColor);

        // Ripple
        if (duskbyte$ripple >= 0.0f && button.active) {
            float p = duskbyte$ripple;
            int alpha = (int) ((1.0f - p) * 200);
            if (alpha > 4) {
                int rippleColor = (alpha << 24) | 0x00FFFFFF;
                float expand = 0.5f + p * 0.5f;
                int cx = x + w / 2;
                int cy = y + h / 2;
                int halfW = (int) (w / 2 * expand);
                int halfH = (int) (h / 2 * expand);
                graphics.fill(cx - halfW, cy - halfH, cx + halfW, cy + halfH, rippleColor);
            }
        }

        // Hover glow (pink)
        if (hovered && button.active) {
            int glowColor = 0x40FF0066;
            graphics.fill(x - 1, y - 1, x + w + 1, y, glowColor);
            graphics.fill(x - 1, y + h, x + w + 1, y + h + 1, glowColor);
            graphics.fill(x - 1, y, x, y + h, glowColor);
            graphics.fill(x + w, y, x + w + 1, y + h, glowColor);
        }
        // Border
        graphics.fill(x, y, x + w, y + 1, borderColor);
        graphics.fill(x, y + h - 1, x + w, y + h, borderColor);
        graphics.fill(x, y, x + 1, y + h, borderColor);
        graphics.fill(x + w - 1, y, x + w, y + h, borderColor);

        ci.cancel();
    }

    @Inject(method = "onClick", at = @At("HEAD"))
    private void onClick(MouseButtonEvent event, boolean isDoubleClick, CallbackInfo ci) {
        duskbyte$ripple = 0.0f;
    }

    @Unique
    private static int lerp(int a, int b, float t) {
        return (int) (a + (b - a) * t);
    }
}
