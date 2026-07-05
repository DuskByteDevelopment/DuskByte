package com.github.duskbyte.gui.panel.component.setting;

import com.github.duskbyte.graphics.renderers.TextRenderer;
import com.github.duskbyte.graphics.text.StaticFontLoader;
import com.github.duskbyte.gui.panel.MD3Theme;
import com.github.duskbyte.gui.panel.PanelLayout;
import com.github.duskbyte.gui.panel.component.PanelElements;
import com.github.duskbyte.gui.panel.component.SettingRow;
import com.github.duskbyte.gui.panel.dsl.PanelUiTree;
import com.github.duskbyte.settings.impl.EnumSetting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public class EnumSettingRow extends SettingRow<EnumSetting<?>> {

    private static final String DROPDOWN_ICON = "v";

    public EnumSettingRow(EnumSetting<?> setting) {
        super(setting);
    }

    @Override
    public void buildUi(PanelUiTree.Scope scope, GuiGraphicsExtractor guiGraphics, TextRenderer textRenderer, PanelLayout.Rect bounds, float hoverProgress, int mouseX, int mouseY, float partialTick) {
        float labelScale = 0.68f;
        float labelY = bounds.y() + (bounds.height() - textRenderer.getHeight(labelScale)) / 2.0f - 1.0f;
        float chipTextScale = 0.60f;
        scope.roundRect(bounds.x(), bounds.y(), bounds.width(), bounds.height(), MD3Theme.CARD_RADIUS, MD3Theme.rowSurface(hoverProgress));
        scope.text(setting.getDisplayName(), bounds.x() + MD3Theme.ROW_CONTENT_INSET, labelY, labelScale, MD3Theme.TEXT_PRIMARY);
        PanelLayout.Rect chipBounds = getChipBounds(textRenderer, bounds);
        scope.chip(chipBounds, setting.getTranslatedValue(), chipTextScale, MD3Theme.SECONDARY_CONTAINER, MD3Theme.ON_SECONDARY_CONTAINER,
                DROPDOWN_ICON, 0.58f, StaticFontLoader.ICONS);
    }

    public PanelLayout.Rect getChipBounds(TextRenderer textRenderer, PanelLayout.Rect bounds) {
        return PanelElements.measureAssistChipBounds(textRenderer, bounds, setting.getTranslatedValue(), 0.60f, 8.0f, 18.0f, 96.0f);
    }

    @Override
    public boolean mouseClicked(PanelLayout.Rect bounds, MouseButtonEvent event, boolean isDoubleClick) {
        return bounds.contains(event.x(), event.y()) && event.button() == 0;
    }

}
