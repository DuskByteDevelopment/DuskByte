package com.github.duskbyte.mixins;

import com.github.duskbyte.DuskByte;
import com.mojang.blaze3d.platform.IconSet;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.InputStream;
import java.util.List;

@Mixin(Window.class)
public class MixinWindow {

    @Redirect(method = "setIcon", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/IconSet;getStandardIcons(Lnet/minecraft/server/packs/PackResources;)Ljava/util/List;"))
    private List<IoSupplier<InputStream>> onSetIcon(IconSet instance, PackResources resources) {
        final InputStream stream16 = DuskByte.class.getResourceAsStream("/assets/duskbyte/textures/icons/icon_16x16.png");
        final InputStream stream32 = DuskByte.class.getResourceAsStream("/assets/duskbyte/textures/icons/icon_32x32.png");
        return stream16 != null && stream32 != null ?
                List.of(() -> stream16, () -> stream32) :
                List.of();
    }

}
