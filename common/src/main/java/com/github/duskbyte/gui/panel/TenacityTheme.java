package com.github.duskbyte.gui.panel;

import java.awt.*;

/**
 * Tenacity 风格的暗色主题 — 用于 ClickGUI 面板。
 * 颜色取自 Tenacity ModernClickGui：
 *   background  = (30, 31, 35)
 *   category    = (47, 49, 54)
 *   module      = (68, 71, 78)
 *   text primary = 白色
 *   text muted   = (128, 134, 141)
 */
public class TenacityTheme {

    // ── 核心颜色 ──────────────────────────────────────────────
    public static final Color BACKGROUND    = new Color(30, 31, 35);      // 主面板背景
    public static final Color CATEGORY_BG   = new Color(47, 49, 54);      // 分类侧栏背景
    public static final Color MODULE_BG     = new Color(68, 71, 78);      // 模块方块背景
    public static final Color MODULE_ICON_BG = new Color(47, 49, 54);     // 模块图标背景
    public static final Color DIVIDER       = new Color(68, 71, 78);      // 分割线

    public static final Color SURFACE            = new Color(30, 31, 35, 246);
    public static final Color SURFACE_DIM        = new Color(35, 36, 40, 236);
    public static final Color SURFACE_CONTAINER_LOW  = new Color(40, 42, 46, 240);
    public static final Color SURFACE_CONTAINER      = new Color(47, 49, 54, 248);
    public static final Color SURFACE_CONTAINER_HIGH = new Color(55, 57, 62, 252);
    public static final Color SURFACE_CONTAINER_HIGHEST = new Color(65, 67, 72, 255);

    public static final Color OUTLINE      = new Color(114, 137, 218, 200);
    public static final Color OUTLINE_SOFT = new Color(114, 137, 218, 80);

    public static final Color PRIMARY              = new Color(114, 137, 218);
    public static final Color ON_PRIMARY           = new Color(255, 255, 255);
    public static final Color PRIMARY_CONTAINER    = new Color(60, 80, 140, 236);
    public static final Color ON_PRIMARY_CONTAINER = new Color(200, 210, 255);

    public static final Color SECONDARY              = new Color(150, 150, 150);
    public static final Color ON_SECONDARY           = new Color(255, 255, 255);
    public static final Color SECONDARY_CONTAINER    = new Color(60, 62, 68, 236);
    public static final Color ON_SECONDARY_CONTAINER = new Color(210, 210, 220);

    public static final Color TERTIARY              = new Color(100, 180, 100);
    public static final Color ON_TERTIARY           = new Color(255, 255, 255);
    public static final Color TERTIARY_CONTAINER    = new Color(40, 100, 40, 236);
    public static final Color ON_TERTIARY_CONTAINER = new Color(200, 255, 200);

    public static final Color TEXT_PRIMARY   = Color.WHITE;
    public static final Color TEXT_SECONDARY = new Color(200, 200, 200);
    public static final Color TEXT_MUTED     = new Color(128, 134, 141);
    public static final Color TEXT_HIGHLIGHT = new Color(114, 137, 218);

    public static final Color ACCENT_PRIMARY   = new Color(114, 137, 218);
    public static final Color ACCENT_SECONDARY = new Color(255, 65, 65);
    public static final Color ERROR            = new Color(240, 70, 70);
    public static final Color SUCCESS          = new Color(100, 200, 100);

    // ── 圆角半径 ──────────────────────────────────────────────
    public static final int PANEL_RADIUS   = 10;
    public static final int SECTION_RADIUS = 6;
    public static final int CARD_RADIUS    = 5;
    public static final int CAT_RADIUS     = 5;
    public static final int CHIP_RADIUS    = 999;
    public static final int CONTROL_RADIUS = 3;

    // ── 间距 ──────────────────────────────────────────────────
    public static final float OUTER_PADDING          = 5.0f;
    public static final float SECTION_GAP            = 3.0f;
    public static final float INNER_PADDING          = 5.0f;
    public static final float ROW_GAP                = 3.0f;
    public static final float PANEL_TITLE_INSET      = 6.0f;
    public static final float PANEL_VIEWPORT_INSET   = 3.0f;
    public static final float ROW_CONTENT_INSET      = 5.0f;
    public static final float ROW_TRAILING_INSET     = 5.0f;
    public static final float RAIL_COLLAPSED_WIDTH   = 42.0f;
    public static final float RAIL_EXPANDED_WIDTH    = 140.0f;
    public static final float CONTROL_HEIGHT         = 20.0f;
    public static final float COMPACT_CHIP_HEIGHT    = 16.0f;
    public static final float SWITCH_WIDTH           = 26.0f;
    public static final float SWITCH_HEIGHT          = 16.0f;
    public static final float SWITCH_HANDLE_SIZE_OFF = 8.0f;
    public static final float SWITCH_HANDLE_SIZE_ON  = 12.0f;
    public static final float SWITCH_HANDLE_INSET_OFF = 4.0f;
    public static final float SWITCH_HANDLE_INSET_ON = 2.0f;
    public static final float SWITCH_STATE_LAYER_SIZE = 20.0f;
    public static final float PANEL_WIDTH_RATIO      = 0.55f;
    public static final float PANEL_HEIGHT_RATIO     = 0.65f;

    private TenacityTheme() {}

    // ── 工具方法 ──────────────────────────────────────────────
    public static Color withAlpha(Color color, int alpha) {
        int a = Math.max(0, Math.min(255, alpha));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), a);
    }

    public static Color lerp(Color start, Color end, float delta) {
        float t = Math.max(0, Math.min(1, delta));
        int r = (int) (start.getRed() + (end.getRed() - start.getRed()) * t);
        int g = (int) (start.getGreen() + (end.getGreen() - start.getGreen()) * t);
        int b = (int) (start.getBlue() + (end.getBlue() - start.getBlue()) * t);
        int a = (int) (start.getAlpha() + (end.getAlpha() - start.getAlpha()) * t);
        return new Color(r, g, b, a);
    }

    public static Color stateLayer(Color source, float progress, int targetAlpha) {
        int a = (int) (targetAlpha * Math.max(0, Math.min(1, progress)));
        return new Color(source.getRed(), source.getGreen(), source.getBlue(), a);
    }

    public static Color rowSurface(float hoverProgress) {
        return lerp(MODULE_BG, MODULE_ICON_BG, hoverProgress);
    }

    public static Color filledFieldContent(boolean focused) {
        return focused ? TEXT_PRIMARY : TEXT_MUTED;
    }

    public static Color filledFieldCaret(boolean focused) {
        return focused ? TEXT_PRIMARY : TEXT_MUTED;
    }

    public static Color textFieldBackground() {
        return MODULE_ICON_BG;
    }

    public static float pulse(long timeMs, float periodMs) {
        return (float) ((Math.sin(timeMs / periodMs * Math.PI * 2.0) + 1.0) / 2.0);
    }

    public static Color[] neonGlowLayers(Color base, int alpha) {
        return new Color[] {
                withAlpha(base, alpha),
                withAlpha(base, alpha / 2),
                withAlpha(base, alpha / 4)
        };
    }

    public static Color glitchChannelA(Color base) {
        return base;
    }

    public static Color glitchChannelB(Color base) {
        return base;
    }

    public static boolean isLightTheme() {
        return false;
    }
}