package com.github.duskbyte.gui.panel.panel;

import com.github.duskbyte.gui.panel.PanelLayout;
import com.github.duskbyte.gui.panel.PanelState;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public class TopBarPanel {

    protected final PanelState state;

    public TopBarPanel(PanelState state) {
        this.state = state;
    }

    public void render(GuiGraphicsExtractor GuiGraphicsExtractor, PanelLayout.Rect bounds, int mouseX, int mouseY, float partialTick) {
    }

    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        return false;
    }
}
