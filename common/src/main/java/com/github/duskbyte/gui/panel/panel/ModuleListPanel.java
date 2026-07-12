package com.github.duskbyte.gui.panel.panel;

import com.github.duskbyte.assets.holders.TranslateHolder;
import com.github.duskbyte.assets.i18n.DuskTranslate;
import com.github.duskbyte.assets.i18n.TranslateComponent;
import com.github.duskbyte.graphics.renderers.RectRenderer;
import com.github.duskbyte.graphics.renderers.RoundRectRenderer;
import com.github.duskbyte.graphics.renderers.ShadowRenderer;
import com.github.duskbyte.graphics.renderers.TextRenderer;
import com.github.duskbyte.gui.panel.TenacityTheme;
import com.github.duskbyte.gui.panel.PanelLayout;
import com.github.duskbyte.gui.panel.PanelState;
import com.github.duskbyte.gui.panel.adapter.ModuleViewModel;
import com.github.duskbyte.gui.panel.component.ModuleRow;
import com.github.duskbyte.gui.panel.dsl.PanelUiCompiler;
import com.github.duskbyte.gui.panel.dsl.PanelUiTree;
import com.github.duskbyte.gui.panel.utils.*;
import com.github.duskbyte.managers.sound.SoundKey;
import com.github.duskbyte.managers.sound.SoundManager;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.utils.render.animation.Animation;
import com.github.duskbyte.utils.render.animation.Easing;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * 妯″潡鍒楄〃闈㈡澘銆? * <p>
 * 璐熻矗娓叉煋鍒嗙被涓嬬殑妯″潡鍒楄〃銆佹悳绱㈡銆佹粴鍔ㄨ鍙ｄ笌妯″潡琛岀紦瀛橈紝
 * 骞剁淮鎶や笌鍒楄〃鍐呭鐩稿叧鐨勮緭鍏ョ姸鎬併€佹粴鍔ㄧ姸鎬佸拰閲嶅缓绛惧悕銆? */
public class ModuleListPanel {

    protected final PanelState state;
    private final RoundRectRenderer roundRectRenderer;
    private final RectRenderer rectRenderer;
    private final TextRenderer textRenderer;
    private final PanelContentBuffer contentBuffer = new PanelContentBuffer();
    private final PanelContentInvalidationState contentState = new PanelContentInvalidationState();
    private PanelLayout.Rect bounds;
    private int guiHeight;
    private final List<ModuleRow> rows = new ArrayList<>();
    private final Map<Module, Animation> hoverAnimations = new HashMap<>();
    private final Map<Module, Animation> selectionAnimations = new HashMap<>();
    private final Map<Module, Animation> toggleAnimations = new HashMap<>();
    private final Map<Module, Animation> toggleHoverAnimations = new HashMap<>();
    private float lastModuleScroll = Float.NaN;
    private String lastSearchQuery = "";
    private boolean lastSearchFocused;
    private CategorySnapshot lastCategorySnapshot;
    private String lastSelectedModuleName = "";
    private final Animation searchHoverAnimation = new Animation(Easing.EASE_OUT_CUBIC, 120L);
    private final Animation searchFocusAnimation = new Animation(Easing.EASE_OUT_CUBIC, 120L);
    private final ScrollBarDragState scrollBarDrag = new ScrollBarDragState();
    private boolean searchFocused;
    private int searchCursorIndex;
    private long lastContentSignature = Long.MIN_VALUE;

    private static final TranslateComponent searchComponent = DuskTranslate.create("gui", "search");
    private static final TranslateComponent modulesComponent = DuskTranslate.create("gui", "modules");

    public ModuleListPanel(PanelState state, RoundRectRenderer roundRectRenderer, RectRenderer rectRenderer, ShadowRenderer shadowRenderer, TextRenderer textRenderer) {
        this.state = state;
        this.roundRectRenderer = roundRectRenderer;
        this.rectRenderer = rectRenderer;
        this.textRenderer = textRenderer;
        this.searchHoverAnimation.setStartValue(0.0f);
        this.searchFocusAnimation.setStartValue(0.0f);
    }

    /**
     * 鎻愬彇骞剁紪璇戞ā鍧楀垪琛ㄩ潰鏉垮綋鍓嶅抚鐨?UI銆?     * <p>
     * 闈㈡澘鏍囬涓庢悳绱㈡浼氱洿鎺ュ啓鍏ヤ富鎵规锛涙粴鍔ㄥ垪琛ㄥ唴瀹瑰垯鍐欏叆鐙珛鐨?viewport 缂撳啿锛?     * 骞跺湪涔嬪悗鐨勭粺涓€ flush 闃舵杈撳嚭銆?     */
    public void render(GuiGraphicsExtractor GuiGraphicsExtractor, PanelLayout.Rect bounds, int mouseX, int mouseY, float partialTick) {
        this.bounds = bounds;
        this.guiHeight = GuiGraphicsExtractor.guiHeight();

        PanelLayout.Rect viewport = getViewport();
        List<Module> modules = state.getVisibleModules();
        float contentHeight = modules.size() * (ModuleRow.HEIGHT + TenacityTheme.ROW_GAP);
        state.setMaxModuleScroll(contentHeight - viewport.height());
        float maxModuleScroll = Math.max(0, contentHeight - viewport.height());
        boolean hasScrollBar = maxModuleScroll > 0;
        float rowWidth = hasScrollBar ? viewport.width() - ScrollBarUtils.TOTAL_WIDTH : viewport.width();
        long contentSignature = buildContentSignature(modules);
        boolean rebuildContent = shouldRebuildContent(bounds, mouseX, mouseY, modules, GuiGraphicsExtractor.guiHeight(), contentSignature);

        if (rebuildContent) {
            rows.clear();
            contentBuffer.clear();
            contentState.beginRebuild();
        }

        PanelUiTree tree = PanelUiTree.build(scope -> {
            scope.text(state.getSelectedCategory().getName(), bounds.x() + TenacityTheme.PANEL_TITLE_INSET, bounds.y() + 10.0f, 0.78f, TenacityTheme.TEXT_PRIMARY);
            scope.text(modulesComponent.getTranslatedName(), bounds.x() + TenacityTheme.PANEL_TITLE_INSET, bounds.y() + 21.0f, 0.56f, TenacityTheme.TEXT_SECONDARY);
            buildSearchField(scope, mouseX, mouseY);
            scope.viewport(contentBuffer, viewport, guiHeight, state.getModuleScroll(), maxModuleScroll, contentHeight, content -> {
                if (!rebuildContent) {
                    return;
                }
                float y = viewport.y() - state.getModuleScroll();
                for (Module module : modules) {
                    ModuleRow row = new ModuleRow(ModuleViewModel.from(module), new PanelLayout.Rect(viewport.x(), y, rowWidth, ModuleRow.HEIGHT));
                    rows.add(row);
                    Animation hoverAnimation = hoverAnimations.computeIfAbsent(module, ignored -> new Animation(Easing.EASE_OUT_CUBIC, 120L));
                    Animation selectionAnimation = selectionAnimations.computeIfAbsent(module, ignored -> new Animation(Easing.EASE_OUT_CUBIC, 160L));
                    Animation toggleAnimation = toggleAnimations.computeIfAbsent(module, ignored -> new Animation(Easing.DYNAMIC_ISLAND, 220L));
                    Animation toggleHoverAnimation = toggleHoverAnimations.computeIfAbsent(module, ignored -> new Animation(Easing.EASE_OUT_CUBIC, 120L));
                    hoverAnimation.run(row.getBounds().contains(mouseX, mouseY) ? 1.0f : 0.0f);
                    selectionAnimation.run(state.getSelectedModule() == module ? 1.0f : 0.0f);
                    toggleAnimation.run(module.isEnabled() ? 1.0f : 0.0f);
                    toggleHoverAnimation.run(row.getToggleBounds().contains(mouseX, mouseY) ? 1.0f : 0.0f);
                    boolean marqueeActive = row.hasOverflowingKeybind(textRenderer);
                    contentState.noteAnimation(!hoverAnimation.isFinished()
                            || !selectionAnimation.isFinished()
                            || !toggleAnimation.isFinished()
                            || !toggleHoverAnimation.isFinished()
                            || marqueeActive);
                    row.buildUi(content, textRenderer, hoverAnimation.getValue(), selectionAnimation.getValue(), toggleAnimation.getValue(), toggleHoverAnimation.getValue());
                    y += ModuleRow.HEIGHT + TenacityTheme.ROW_GAP;
                }
            });
        });
        PanelUiCompiler.render(tree, roundRectRenderer, rectRenderer, textRenderer);

        if (rebuildContent) {
            rememberSnapshot(bounds, mouseX, mouseY, modules, GuiGraphicsExtractor.guiHeight(), contentSignature);
        }

        // 清理不再显示的模块的动画缓存，防止内存泄漏
        Set<Module> moduleSet = new HashSet<>(modules);
        hoverAnimations.keySet().retainAll(moduleSet);
        selectionAnimations.keySet().retainAll(moduleSet);
        toggleAnimations.keySet().retainAll(moduleSet);
        toggleHoverAnimations.keySet().retainAll(moduleSet);
    }

    /**
     * 杈撳嚭骞舵竻绌哄垪琛ㄨ鍙ｇ紦鍐蹭腑鐨勫唴瀹广€?     */
    public void flushContent() {
        contentBuffer.flush();
    }

    /**
     * 灏嗗垪琛ㄥ唴瀹规爣璁颁负鑴忥紝浠ヤ究鍦ㄤ笅娆℃覆鏌撴椂瑙﹀彂閲嶅缓銆?     */
    public void markDirty() {
        contentState.markDirty();
    }

    /**
     * 杩斿洖鍒楄〃鍐呭鏄惁浠嶅寘鍚湭缁撴潫鐨勫姩鐢汇€?     */
    public boolean hasActiveAnimations() {
        return contentState.hasActiveAnimations();
    }

    /**
     * 澶勭悊鍒楄〃鍖哄煙涓殑鐐瑰嚮浜嬩欢銆?     * <p>
     * 璇ユ柟娉曚細浼樺厛澶勭悊婊氬姩鏉℃嫋鎷斤紝鍏舵澶勭悊鎼滅储妗嗚仛鐒︼紝鏈€鍚庡鐞嗘ā鍧楄閫夋嫨涓庡惎鐢ㄥ垏鎹€?     */
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        if (bounds == null || event.button() != 0) {
            return false;
        }
        // Scrollbar drag
        PanelLayout.Rect viewport = getViewport();
        float maxScroll = state.getMaxModuleScroll();
        if (scrollBarDrag.mouseClicked(event.x(), event.y(), viewport, state.getModuleScroll(), maxScroll)) {
            float newScroll = scrollBarDrag.mouseDragged(event.y(), viewport, maxScroll);
            if (newScroll >= 0) {
                state.setModuleScroll(newScroll);
            }
            markDirty();
            return true;
        }
        PanelLayout.Rect searchBounds = getSearchBounds();
        if (searchBounds.contains(event.x(), event.y())) {
            searchFocused = true;
            searchCursorIndex = state.getSearchQuery().length();
            IMEFocusHelper.activate();
            markDirty();
            return true;
        }
        for (ModuleRow row : rows) {
            if (!row.getBounds().contains(event.x(), event.y())) {
                continue;
            }
            if (row.getToggleBounds().contains(event.x(), event.y())) {
                row.getModule().module().toggle();
                SoundManager.INSTANCE.playInUi(row.getModule().module().isEnabled() ? SoundKey.SETTINGS_OPEN : SoundKey.SETTINGS_CLOSE);
            } else {
                state.setSelectedModule(row.getModule().module());
            }
            markDirty();
            return true;
        }
        return false;
    }

    public boolean mouseReleased(MouseButtonEvent event) {
        if (scrollBarDrag.mouseReleased()) {
            markDirty();
            return true;
        }
        return false;
    }

    public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY) {
        if (scrollBarDrag.isDragging()) {
            PanelLayout.Rect viewport = getViewport();
            float newScroll = scrollBarDrag.mouseDragged(event.y(), viewport, state.getMaxModuleScroll());
            if (newScroll >= 0) {
                state.setModuleScroll(newScroll);
            }
            markDirty();
            return true;
        }
        return false;
    }

    /**
     * 褰撻紶鏍囦綅浜庡垪琛ㄨ鍙ｅ唴鏃讹紝澶勭悊婊氳疆婊氬姩銆?     */
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        PanelLayout.Rect viewport = getViewport();
        if (bounds != null && viewport.contains(mouseX, mouseY)) {
            state.scrollModules(-scrollY * 20.0f);
            markDirty();
            return true;
        }
        return false;
    }

    /**
     * 澶勭悊鎼滅储妗嗗浜庣劍鐐圭姸鎬佹椂鐨勯敭鐩樹簨浠躲€?     */
    public boolean keyPressed(KeyEvent event) {
        if (!searchFocused) {
            return false;
        }
        String query = state.getSearchQuery();
        return switch (event.key()) {
            case 257, 335 -> true;
            case 256 -> {
                searchFocused = false;
                IMEFocusHelper.deactivate();
                yield true;
            }
            case 259 -> {
                if (searchCursorIndex > 0 && !query.isEmpty()) {
                    state.setSearchQuery(query.substring(0, searchCursorIndex - 1) + query.substring(searchCursorIndex));
                    searchCursorIndex--;
                    markDirty();
                }
                yield true;
            }
            case 261 -> {
                if (searchCursorIndex < query.length()) {
                    state.setSearchQuery(query.substring(0, searchCursorIndex) + query.substring(searchCursorIndex + 1));
                    markDirty();
                }
                yield true;
            }
            case 263 -> {
                searchCursorIndex = Math.max(0, searchCursorIndex - 1);
                markDirty();
                yield true;
            }
            case 262 -> {
                searchCursorIndex = Math.min(state.getSearchQuery().length(), searchCursorIndex + 1);
                markDirty();
                yield true;
            }
            default -> false;
        };
    }

    /**
     * 澶勭悊鎼滅储妗嗙殑瀛楃杈撳叆銆?     */
    public boolean charTyped(CharacterEvent event) {
        if (!searchFocused || !event.isAllowedChatCharacter()) {
            return false;
        }
        String query = state.getSearchQuery();
        String typed = event.codepointAsString();
        state.setSearchQuery(query.substring(0, searchCursorIndex) + typed + query.substring(searchCursorIndex));
        searchCursorIndex++;
        markDirty();
        return true;
    }

    /**
     * 澶勭悊鏉ヨ嚜闈㈡澘澶栧眰鐨勫叏灞€鐐瑰嚮閫氱煡銆?     * <p>
     * 鑻ョ偣鍑讳綅缃笉鍦ㄦ悳绱㈡鍐咃紝鍒欎細鍙栨秷鎼滅储妗嗙劍鐐广€?     */
    public void handleGlobalClick(double mouseX, double mouseY) {
        if (bounds == null) {
            return;
        }
        if (!getSearchBounds().contains(mouseX, mouseY)) {
            searchFocused = false;
            IMEFocusHelper.deactivate();
            markDirty();
        }
    }

    private boolean shouldRebuildContent(PanelLayout.Rect bounds, int mouseX, int mouseY, List<Module> modules, int currentGuiHeight, long contentSignature) {
        if (contentState.needsRebuild(bounds, mouseX, mouseY, currentGuiHeight, contentSignature)) {
            return true;
        }
        if (Float.compare(lastModuleScroll, state.getModuleScroll()) != 0) {
            return true;
        }
        if (!Objects.equals(lastSearchQuery, state.getSearchQuery()) || lastSearchFocused != searchFocused) {
            return true;
        }
        String selectedModuleName = state.getSelectedModule() == null ? "" : state.getSelectedModule().getName();
        if (!Objects.equals(lastSelectedModuleName, selectedModuleName)) {
            return true;
        }
        if (!Objects.equals(lastCategorySnapshot, CategorySnapshot.of(state.getSelectedCategory().name(), modules))) {
            return true;
        }
        return lastContentSignature != contentSignature;
    }

    private void rememberSnapshot(PanelLayout.Rect bounds, int mouseX, int mouseY, List<Module> modules, int currentGuiHeight, long contentSignature) {
        contentState.rememberSnapshot(bounds, mouseX, mouseY, currentGuiHeight, contentSignature);
        lastModuleScroll = state.getModuleScroll();
        lastSearchQuery = state.getSearchQuery();
        lastSearchFocused = searchFocused;
        lastCategorySnapshot = CategorySnapshot.of(state.getSelectedCategory().name(), modules);
        lastSelectedModuleName = state.getSelectedModule() == null ? "" : state.getSelectedModule().getName();
        lastContentSignature = contentSignature;
    }

    private long buildContentSignature(List<Module> modules) {
        long signature = 17L;
        signature = signature * 31L + TranslateHolder.INSTANCE.getRevision();
        signature = signature * 31L + state.getSelectedCategory().name().hashCode();
        signature = signature * 31L + state.getSearchQuery().hashCode();
        signature = signature * 31L + (searchFocused ? 1 : 0);
        signature = signature * 31L + (state.getSelectedModule() == null ? 0 : state.getSelectedModule().getName().hashCode());
        signature = signature * 31L + Float.floatToIntBits(state.getModuleScroll());
        for (Module module : modules) {
            signature = signature * 31L + module.getName().hashCode();
            signature = signature * 31L + module.getKeyBind();
            signature = signature * 31L + (module.isEnabled() ? 1 : 0);
        }
        return signature;
    }

    private record CategorySnapshot(String categoryName, List<String> moduleIds) {
        private static CategorySnapshot of(String categoryName, List<Module> modules) {
            List<String> ids = modules.stream().map(Module::getName).toList();
            return new CategorySnapshot(categoryName, ids);
        }
    }

    private PanelLayout.Rect getViewport() {
        return new PanelLayout.Rect(bounds.x() + TenacityTheme.PANEL_VIEWPORT_INSET, bounds.y() + 34.0f, bounds.width() - TenacityTheme.PANEL_VIEWPORT_INSET * 2.0f, bounds.height() - 40.0f);
    }

    private PanelLayout.Rect getSearchBounds() {
        return new PanelLayout.Rect(bounds.right() - TenacityTheme.PANEL_TITLE_INSET - 76.0f, bounds.y() + 8.0f, 76.0f, 18.0f);
    }

    private void buildSearchField(PanelUiTree.Scope scope, int mouseX, int mouseY) {
        PanelLayout.Rect searchBounds = getSearchBounds();
        float hoverProgress = scope.animate(searchHoverAnimation, searchBounds.contains(mouseX, mouseY));
        float focusProgress = scope.animate(searchFocusAnimation, searchFocused);
        float fieldHover = Math.max(hoverProgress, focusProgress * 0.85f);

        String query = state.getSearchQuery();
        boolean showPlaceholder = query.isEmpty() && !searchFocused;
        String display = showPlaceholder ? searchComponent.getTranslatedName() : query;
        float scale = 0.52f;
        Color textColor = showPlaceholder
                ? TenacityTheme.lerp(TenacityTheme.TEXT_MUTED, TenacityTheme.filledFieldContent(searchFocused), focusProgress)
                : TenacityTheme.filledFieldContent(searchFocused);
        scope.input(searchBounds, searchFocused, fieldHover,
                8.0f, display, scale, textColor,
                searchFocused ? searchCursorIndex : null, searchFocused ? TenacityTheme.filledFieldCaret(true) : null,
                null, 0.0f, null);

        // Clear search button (X) when query is not empty
        if (!query.isEmpty()) {
            float clearSize = 12.0f;
            float clearX = searchBounds.right() - clearSize - 4.0f;
            float clearY = searchBounds.y() + (searchBounds.height() - clearSize) / 2.0f;
            boolean clearHovered = mouseX >= clearX && mouseX <= clearX + clearSize && mouseY >= clearY && mouseY <= clearY + clearSize;
            Color clearColor = clearHovered ? TenacityTheme.TEXT_PRIMARY : TenacityTheme.TEXT_MUTED;
            scope.text("✕", clearX, clearY, 0.5f, clearColor);
        }

        if (searchFocused) {
            float textY = searchBounds.y() + (searchBounds.height() - textRenderer.getHeight(scale)) / 2.0f - 1.0f;
            float textX = searchBounds.x() + 8.0f;
            float caretX = textX + textRenderer.getWidth(query.substring(0, Math.min(searchCursorIndex, query.length())), scale);
            IMEFocusHelper.updateCursorPos(caretX, textY);
        }
    }
}

