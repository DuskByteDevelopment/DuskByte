package com.github.duskbyte.gui.panel;

import com.github.duskbyte.assets.holders.TranslateHolder;
import com.github.duskbyte.graphics.LuminRenderSystem;
import com.github.duskbyte.graphics.renderers.*;
import com.github.duskbyte.gui.panel.dsl.PanelRenderBatch;
import com.github.duskbyte.gui.panel.input.PanelInputRouter;
import com.github.duskbyte.gui.panel.panel.CategoryRailPanel;
import com.github.duskbyte.gui.panel.panel.ClientSettingPanel;
import com.github.duskbyte.gui.panel.panel.ModuleDetailPanel;
import com.github.duskbyte.gui.panel.panel.ModuleListPanel;
import com.github.duskbyte.gui.panel.popup.PanelPopupHost;
import com.github.duskbyte.gui.panel.utils.IMEFocusHelper;
import com.github.duskbyte.modules.impl.ClientSetting;
import java.awt.Color;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.IMEPreeditOverlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.PreeditEvent;
import net.minecraft.network.chat.Component;

/**
 * 闈㈡澘 UI 鐨勪富灞忓箷瀹夸富銆? * <p>
 * 瀹冭礋璐ｇ淮鎶ゅ叏灞€鐘舵€併€佽皟搴﹀悇瀛愰潰鏉跨殑 extract 闃舵銆佺粺涓€ flush renderer锛? * 骞跺皢杈撳叆浜嬩欢璺敱鍒?rail銆佹ā鍧楀垪琛ㄣ€佽鎯呴潰鏉裤€佸鎴风璁剧疆闈㈡澘鍜屽脊绐楀涓汇€? */
public class PanelScreen extends Screen {

    public static final PanelScreen INSTANCE = new PanelScreen();

    private final PanelState state = new PanelState();
    private final PanelDirtyState dirtyState = new PanelDirtyState();
    private final TextRenderer textRenderer = new TextRenderer();
    private final RectRenderer rectRenderer = new RectRenderer();
    private final RoundRectRenderer roundRectRenderer = new RoundRectRenderer();
    private final RoundRectOutlineRenderer roundRectOutlineRenderer = new RoundRectOutlineRenderer();
    private final ShadowRenderer shadowRenderer = new ShadowRenderer();
    private final PanelRenderBatch renderBatch = new PanelRenderBatch(shadowRenderer, roundRectRenderer, roundRectOutlineRenderer, rectRenderer, textRenderer);
    private final PanelPopupHost popupHost = new PanelPopupHost();
    private final PanelInputRouter inputRouter = new PanelInputRouter();
    private final CategoryRailPanel categoryRailPanel = new CategoryRailPanel(state, rectRenderer, roundRectRenderer, textRenderer);
    private final ModuleListPanel moduleListPanel = new ModuleListPanel(state, roundRectRenderer, rectRenderer, shadowRenderer, textRenderer);
    private final ModuleDetailPanel moduleDetailPanel = new ModuleDetailPanel(state, roundRectRenderer, rectRenderer, shadowRenderer, textRenderer, popupHost);
    private final ClientSettingPanel clientSettingPanel = new ClientSettingPanel(state, roundRectRenderer, rectRenderer, shadowRenderer, textRenderer, popupHost);
    private int lastWidth = -1;
    private int lastHeight = -1;
    private String lastSelectedCategory = "";
    private String lastSelectedModule = "";
    private String lastSearchQuery = "";
    private boolean lastSidebarExpanded;
    private boolean lastClientSettingMode;
    private long lastI18nRevision = Long.MIN_VALUE;

    private IMEPreeditOverlay preeditOverlay;

    private LuminRenderSystem.LuminRenderTarget renderTarget;

    private PanelScreen() {
        super(Component.literal("PanelGui"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /**
     * 鎻愬彇闈㈡澘褰撳墠甯х殑娓叉煋鐘舵€併€?     * <p>
     * 璇ユ柟娉曚細璁＄畻甯冨眬銆佹帹鍔ㄥ姩鐢汇€佽鍚勪釜瀛愰潰鏉挎妸 UI 缂栬瘧杩涘叡浜壒娆★紝
     * 鏈€鍚庡湪缁熶竴鐨?render 鎻愪氦闃舵鎵ц flush銆?     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {

        final var window = minecraft.getWindow();
        if (renderTarget == null) {
            renderTarget = LuminRenderSystem.LuminRenderTarget.create("click-gui", window.getWidth(), window.getHeight());
        }
        renderTarget.clear();
        renderTarget.resize(window.getWidth(), window.getHeight());

        LuminRenderSystem.setActiveTarget(renderTarget);

        String currentCategory = state.getSelectedCategory().name();
        String currentModule = state.getSelectedModule() == null ? "" : state.getSelectedModule().getName();
        String currentQuery = state.getSearchQuery();
        boolean sidebarExpanded = state.isSidebarExpanded();
        boolean clientSettingMode = state.isClientSettingMode();
        long currentI18nRevision = TranslateHolder.INSTANCE.getRevision();
        if (!lastSelectedCategory.equals(currentCategory)
                || !lastSelectedModule.equals(currentModule)
                || !lastSearchQuery.equals(currentQuery)
                || lastSidebarExpanded != sidebarExpanded
                || lastClientSettingMode != clientSettingMode
                || lastI18nRevision != currentI18nRevision) {
            dirtyState.markAllDirty();
            lastSelectedCategory = currentCategory;
            lastSelectedModule = currentModule;
            lastSearchQuery = currentQuery;
            lastSidebarExpanded = sidebarExpanded;
            lastClientSettingMode = clientSettingMode;
            lastI18nRevision = currentI18nRevision;
        }

        if (categoryRailPanel.hasActiveAnimations()
                || moduleListPanel.hasActiveAnimations()
                || moduleDetailPanel.hasActiveAnimations()
                || clientSettingPanel.hasActiveAnimations()) {
            dirtyState.markAllDirty();
        }

        if (width != lastWidth || height != lastHeight) {
            dirtyState.markLayoutDirty();
            lastWidth = width;
            lastHeight = height;
        }

        if (dirtyState.consumeModuleListDirty()) {
            moduleListPanel.markDirty();
        }
        if (dirtyState.consumeDetailDirty()) {
            moduleDetailPanel.markDirty();
        }
        if (dirtyState.consumeClientSettingDirty()) {
            clientSettingPanel.markDirty();
        }

        // Tenacity 主题无需动态同步
        float railWidth = categoryRailPanel.getAnimatedWidth();
        PanelLayout.Layout layout = PanelLayout.compute(width, height, railWidth);
        popupHost.setOverlayBounds(layout.panel());

        drawChrome(layout);
        categoryRailPanel.render(guiGraphics, layout.rail(), mouseX, mouseY, partialTick);
        if (state.isClientSettingMode()) {
            PanelLayout.Rect clientSettingsBounds = new PanelLayout.Rect(
                    layout.modules().x(), layout.modules().y(),
                    layout.detail().right() - layout.modules().x(),
                    layout.modules().height()
            );
            clientSettingPanel.render(guiGraphics, clientSettingsBounds, mouseX, mouseY, partialTick);
        } else {
            moduleListPanel.render(guiGraphics, layout.modules(), mouseX, mouseY, partialTick);
            moduleDetailPanel.render(guiGraphics, layout.detail(), mouseX, mouseY, partialTick);
        }

        popupHost.render(guiGraphics, mouseX, mouseY, partialTick);

        flushQueuedRenderers();

        LuminRenderSystem.setActiveTarget(null);

        if (preeditOverlay != null) {
            this.preeditOverlay.updateInputPosition((int) IMEFocusHelper.activeCursorX, (int) IMEFocusHelper.activeCursorY);
            guiGraphics.setPreeditOverlay(this.preeditOverlay);
        }
        guiGraphics.blit(renderTarget.getIdentifier(), 0, 0, window.getGuiScaledWidth(), window.getGuiScaledHeight(), 0, 1, 1, 0);
    }

    private void drawChrome(PanelLayout.Layout layout) {
        // Tenacity 风格：纯暗色背景，无霓虹/扫描线效果
        roundRectRenderer.addRoundRect(0, 0, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight(), 0,
                TenacityTheme.withAlpha(new Color(0, 0, 0), 152));

        // 主面板背景 — 纯色圆角矩形，无辉光
        roundRectRenderer.addRoundRect(layout.panel().x(), layout.panel().y(), layout.panel().width(), layout.panel().height(),
                TenacityTheme.PANEL_RADIUS, TenacityTheme.BACKGROUND);

        // 分类侧栏背景
        roundRectRenderer.addRoundRect(layout.rail().x(), layout.rail().y(), layout.rail().width(), layout.rail().height(),
                TenacityTheme.CAT_RADIUS, TenacityTheme.CATEGORY_BG);

        if (state.isClientSettingMode()) {
            float csX = layout.modules().x();
            float csY = layout.modules().y();
            float csW = layout.detail().right() - layout.modules().x();
            float csH = layout.modules().height();
            roundRectRenderer.addRoundRect(csX, csY, csW, csH, TenacityTheme.CARD_RADIUS, TenacityTheme.CATEGORY_BG);
        } else {
            // 模块列表背景
            roundRectRenderer.addRoundRect(layout.modules().x(), layout.modules().y(), layout.modules().width(), layout.modules().height(),
                    TenacityTheme.CARD_RADIUS, TenacityTheme.CATEGORY_BG);
            // 详情面板背景
            roundRectRenderer.addRoundRect(layout.detail().x(), layout.detail().y(), layout.detail().width(), layout.detail().height(),
                    TenacityTheme.CARD_RADIUS, TenacityTheme.CATEGORY_BG);
        }
    }

    private void flushQueuedRenderers() {
        renderBatch.flushAndClear();
        if (state.isClientSettingMode()) {
            clientSettingPanel.flushContent();
        } else {
            moduleListPanel.flushContent();
            moduleDetailPanel.flushContent();
        }
        categoryRailPanel.flushClippedText();
        popupHost.flush();
    }


    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        if (event.button() != 0) {
            if (state.getListeningKeyBindModule() != null && moduleDetailPanel.mouseClicked(event, isDoubleClick)) {
                dirtyState.markAllDirty();
                return true;
            }
            if (state.getListeningKeybindSetting() != null) {
                boolean handledListening = state.isClientSettingMode() ? clientSettingPanel.mouseClicked(event, isDoubleClick) : moduleDetailPanel.mouseClicked(event, isDoubleClick);
                if (handledListening) {
                    dirtyState.markAllDirty();
                    return true;
                }
            }
            return super.mouseClicked(event, isDoubleClick);
        }

        if (popupHost.getActivePopup() != null) {
            return inputRouter.routeMouseClicked(event, isDoubleClick, popupHost, moduleDetailPanel, moduleListPanel, categoryRailPanel, clientSettingPanel, state.isClientSettingMode())
                    || super.mouseClicked(event, isDoubleClick);
        }

        PanelLayout.Layout layout = PanelLayout.compute(width, height, categoryRailPanel.getAnimatedWidth());
        if (!layout.panel().contains(mouseX, mouseY)) {
            if (ClientSetting.INSTANCE.closeOnOutside.getValue()) minecraft.setScreen(null);
            return true;
        }
        if (!state.isClientSettingMode()) {
            moduleListPanel.handleGlobalClick(mouseX, mouseY);
        }
        boolean handled = inputRouter.routeMouseClicked(event, isDoubleClick, popupHost, moduleDetailPanel, moduleListPanel, categoryRailPanel, clientSettingPanel, state.isClientSettingMode());
        if (handled) {
            dirtyState.markAllDirty();
        }
        return handled || super.mouseClicked(event, isDoubleClick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (popupHost.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            dirtyState.markAllDirty();
            return true;
        }
        if (state.isClientSettingMode()) {
            if (clientSettingPanel.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
                dirtyState.markClientSettingDirty();
                return true;
            }
        } else {
            if (moduleListPanel.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
                dirtyState.markModuleListDirty();
                return true;
            }
            if (moduleDetailPanel.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
                dirtyState.markDetailDirty();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (inputRouter.routeMouseReleased(event, popupHost, moduleDetailPanel, moduleListPanel, clientSettingPanel, state.isClientSettingMode())) {
            dirtyState.markAllDirty();
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY) {
        if (inputRouter.routeMouseDragged(event, mouseX, mouseY, popupHost, moduleDetailPanel, moduleListPanel, clientSettingPanel, state.isClientSettingMode())) {
            dirtyState.markAllDirty();
            return true;
        }
        return super.mouseDragged(event, mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (inputRouter.routeKeyPressed(event, popupHost, moduleDetailPanel, moduleListPanel, clientSettingPanel, state.isClientSettingMode())) {
            dirtyState.markAllDirty();
            return true;
        }
        if (event.key() == 256) {
            onClose();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (inputRouter.routeCharTyped(event, popupHost, moduleDetailPanel, moduleListPanel, clientSettingPanel, state.isClientSettingMode())) {
            dirtyState.markAllDirty();
            return true;
        }
        return super.charTyped(event);
    }

    @Override
    public boolean preeditUpdated(PreeditEvent event) {
        this.preeditOverlay = event != null ? new IMEPreeditOverlay(event, this.font, 10) : null;
        return true;
    }

    @Override
    public void onClose() {
        IMEFocusHelper.deactivate();
        super.onClose();
    }

    /**
     * 杩斿洖褰撳墠闈㈡澘浣跨敤鐨勭灞忔覆鏌撶洰鏍囥€?     *
     * @return 褰撳墠娓叉煋鐩爣锛涢娆℃覆鏌撳墠鍙兘涓?{@code null}
     */
    public LuminRenderSystem.LuminRenderTarget getRenderTarget() {
        return renderTarget;
    }
}

