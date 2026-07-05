package com.github.duskbyte.assets.i18n;

import com.github.duskbyte.assets.holders.TextureCacheHolder;
import com.github.duskbyte.assets.holders.TranslateHolder;
import com.github.duskbyte.gui.panel.dsl.PanelUiTree;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class LanguageReloadListener implements PreparableReloadListener {

    @Override
    public CompletableFuture<Void> reload(SharedState sharedState, Executor exectutor, PreparationBarrier barrier, Executor applyExectutor) {
        return CompletableFuture.completedFuture(null)
                .thenCompose(barrier::wait)
                .thenRunAsync(() -> {

                    TranslateHolder.INSTANCE.refresh();
                    PanelUiTree.clearMemoCache();
                    TextureCacheHolder.INSTANCE.clearCache();

                }, applyExectutor);
    }

}
