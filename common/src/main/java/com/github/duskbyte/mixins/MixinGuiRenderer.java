package com.github.duskbyte.mixins;

import com.github.duskbyte.events.bus.EventBus;
import com.github.duskbyte.events.impl.Render2DEvent;
import com.github.duskbyte.graphics.shaders.BlurShader;
import com.github.duskbyte.utils.render.DuskGui;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRenderer.class)
public class MixinGuiRenderer {

    @Shadow
    @Final
    private MultiBufferSource.BufferSource bufferSource;

    @Shadow
    @Final
    private SubmitNodeCollector submitNodeCollector;

    @Shadow
    @Final
    private FeatureRenderDispatcher featureRenderDispatcher;

    @Unique
    private GuiRenderState duskbyte$levelRenderState;

    @Unique
    private DuskGui duskbyte$levelGuiRenderer;

    @Unique
    private GuiRenderState duskbyte$renderState;

    @Unique
    private DuskGui duskbyte$guiRenderer;

    @Inject(method = "draw", at = @At("HEAD"))
    private void onDrawHead(GpuBufferSlice fogBuffer, CallbackInfo ci) {
        BlurShader.INSTANCE.beginFrame();

        duskbyte$ensureRenderers();

        Minecraft mc = Minecraft.getInstance();
        int mouseX = (int) mc.mouseHandler.getScaledXPos(mc.getWindow());
        int mouseY = (int) mc.mouseHandler.getScaledYPos(mc.getWindow());

        GuiGraphicsExtractor levelGuiGraphics = new GuiGraphicsExtractor(mc, duskbyte$levelRenderState, mouseX, mouseY);
        EventBus.INSTANCE.post(new Render2DEvent.Level(levelGuiGraphics));
        duskbyte$levelGuiRenderer.render(fogBuffer);
        duskbyte$levelGuiRenderer.endFrame();

        GuiGraphicsExtractor guiGraphics = new GuiGraphicsExtractor(mc, duskbyte$renderState, mouseX, mouseY);
        EventBus.INSTANCE.post(new Render2DEvent.HUD(guiGraphics));

        duskbyte$guiRenderer.render(fogBuffer);

        duskbyte$guiRenderer.endFrame();
    }

    @Unique
    private void duskbyte$ensureRenderers() {
        if (duskbyte$levelRenderState == null || duskbyte$levelGuiRenderer == null) {
            this.duskbyte$levelRenderState = new GuiRenderState();
            this.duskbyte$levelGuiRenderer = new DuskGui(
                    this.duskbyte$levelRenderState,
                    this.bufferSource,
                    this.submitNodeCollector,
                    this.featureRenderDispatcher
            );
        }
        if (duskbyte$renderState == null || duskbyte$guiRenderer == null) {
            this.duskbyte$renderState = new GuiRenderState();
            this.duskbyte$guiRenderer = new DuskGui(
                    this.duskbyte$renderState,
                    this.bufferSource,
                    this.submitNodeCollector,
                    this.featureRenderDispatcher
            );
        }
    }

}
