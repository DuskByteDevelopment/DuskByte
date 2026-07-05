package com.github.duskbyte.mixins;

import com.github.duskbyte.events.bus.EventBus;
import com.github.duskbyte.events.impl.RenderFrameEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderPre(DeltaTracker deltaTracker, boolean bl, CallbackInfo ci) {
        EventBus.INSTANCE.post(new RenderFrameEvent.Pre(deltaTracker));
    }

}
