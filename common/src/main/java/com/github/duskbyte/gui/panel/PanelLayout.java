package com.github.duskbyte.gui.panel;

public class PanelLayout {

    private PanelLayout() {
    }

    public static Layout compute(int screenWidth, int screenHeight, float railWidth) {
        // Tenacity 风格：固定比例居中面板，更紧凑
        float panelWidth = Math.min(screenWidth * TenacityTheme.PANEL_WIDTH_RATIO, screenWidth - 20f);
        float panelHeight = Math.min(screenHeight * TenacityTheme.PANEL_HEIGHT_RATIO, screenHeight - 20f);
        panelWidth = Math.max(panelWidth, 450.0f);
        panelHeight = Math.max(panelHeight, 300.0f);

        float x = (screenWidth - panelWidth) / 2.0f;
        float y = (screenHeight - panelHeight) / 2.0f;

        float gap = TenacityTheme.SECTION_GAP;
        float columnHeight = panelHeight - TenacityTheme.OUTER_PADDING * 2.0f;
        float railX = x + TenacityTheme.OUTER_PADDING;
        float modulesX = railX + railWidth + gap;
        float maxContentRight = x + panelWidth - TenacityTheme.OUTER_PADDING;
        // Tenacity 风格：模块区更宽，设置区更窄
        float moduleWidth = Math.min(280.0f, panelWidth * 0.50f);
        float detailX = modulesX + moduleWidth + gap;
        float detailWidth = maxContentRight - detailX;

        Rect panel = new Rect(x, y, panelWidth, panelHeight);
        Rect rail = new Rect(railX, y + TenacityTheme.OUTER_PADDING, railWidth, columnHeight);
        Rect modules = new Rect(modulesX, y + TenacityTheme.OUTER_PADDING, moduleWidth, columnHeight);
        Rect detail = new Rect(detailX, y + TenacityTheme.OUTER_PADDING, detailWidth, columnHeight);

        return new Layout(panel, rail, modules, detail);
    }

    public record Layout(Rect panel, Rect rail, Rect modules, Rect detail) {
    }

    public record Rect(float x, float y, float width, float height) {
        public float right() {
            return x + width;
        }

        public float bottom() {
            return y + height;
        }

        public float centerX() {
            return x + width / 2.0f;
        }

        public float centerY() {
            return y + height / 2.0f;
        }

        public boolean contains(double px, double py) {
            return px >= x && px <= right() && py >= y && py <= bottom();
        }

        public Rect inset(float amount) {
            return new Rect(x + amount, y + amount, width - amount * 2.0f, height - amount * 2.0f);
        }
    }

}