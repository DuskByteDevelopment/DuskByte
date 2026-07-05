package com.github.duskbyte.managers;

import com.github.duskbyte.assets.i18n.DuskTranslate;
import com.github.duskbyte.assets.i18n.TranslateComponent;
import com.github.duskbyte.events.bus.EventBus;
import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.KeyPressEvent;
import com.github.duskbyte.events.impl.MousePressEvent;
import com.github.duskbyte.events.impl.Render2DEvent;
import com.github.duskbyte.gui.hudeditor.HudEditorScreen;
import com.github.duskbyte.gui.panel.PanelScreen;
import com.github.duskbyte.managers.sound.SoundKey;
import com.github.duskbyte.managers.sound.SoundManager;
import com.github.duskbyte.modules.HudModule;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.modules.impl.ClientSetting;
import com.github.duskbyte.modules.impl.combat.*;
import com.github.duskbyte.modules.impl.misc.AntiSpam;
import com.github.duskbyte.modules.impl.misc.AutoReconnect;
import com.github.duskbyte.modules.impl.misc.TimerModule;
import com.github.duskbyte.modules.impl.movement.*;
import com.github.duskbyte.modules.impl.player.*;
import com.github.duskbyte.modules.impl.render.*;
import com.github.duskbyte.modules.impl.hud.*;
import com.github.duskbyte.modules.impl.hud.notification.NotificationsHud;
import com.github.duskbyte.modules.impl.movement.*;
import com.github.duskbyte.modules.impl.player.*;
import com.github.duskbyte.modules.impl.render.*;
import com.github.duskbyte.utils.client.ClientUtils;
import com.github.duskbyte.utils.client.KeybindUtils;
import com.github.duskbyte.utils.player.ChatUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {

    public static final ModuleManager INSTANCE = new ModuleManager();

    private List<Module> modules;

    private ModuleManager() {
        EventBus.INSTANCE.subscribe(this);
    }

    public void initModules() {
        modules = new ArrayList<>(List.of(

                ClientSetting.INSTANCE,

                // Combat
                AntiBot.INSTANCE,
                AutoClicker.INSTANCE,
                AutoDtap.INSTANCE,
                AutoHitCrystal.INSTANCE,
                AutoMend.INSTANCE,
                AutoTotem.INSTANCE,
                AutoWeapon.INSTANCE,
                Criticals.INSTANCE,
                Hitboxes.INSTANCE,
                ZealotCrystalPlus.INSTANCE,
                CrystalAura.INSTANCE,
                CrystalBlocker.INSTANCE,
                HoverTotem.INSTANCE,
                KillAura.INSTANCE,
                KeyPearl.INSTANCE,
                MaceAura.INSTANCE,
                PacketMine.INSTANCE,
                SafeAnchor.INSTANCE,
                SafeCrystal.INSTANCE,
                SilentAim.INSTANCE,
                SpearKill.INSTANCE,
                Surround.INSTANCE,
                TriggerBot.INSTANCE,

                // Player
                AntiAFK.INSTANCE,
                AutoEat.INSTANCE,
                AutoFirework.INSTANCE,
                AutoFish.INSTANCE,
                AutoKouZi.INSTANCE,
                AutoTool.INSTANCE,
                BreakCooldown.INSTANCE,
                ChatSuffix.INSTANCE,
                Disabler.INSTANCE,
                ElytraSwap.INSTANCE,
                FakePlayer.INSTANCE,
                GhostHand.INSTANCE,
                InvManager.INSTANCE,
                JumpCooldown.INSTANCE,
                MultiTask.INSTANCE,
                NoRotate.INSTANCE,
                PacketEat.INSTANCE,
                Stealer.INSTANCE,
                UseCooldown.INSTANCE,

                // Movement
                AirJump.INSTANCE,
                AutoSprint.INSTANCE,
                Blink.INSTANCE,
                ElytraFly.INSTANCE,
                FastWeb.INSTANCE,
                MovementFix.INSTANCE,
                NoFall.INSTANCE,
                NoSlow.INSTANCE,
                Phase.INSTANCE,
                SafeWalk.INSTANCE,
                Scaffold.INSTANCE,
                Spider.INSTANCE,
                Stuck.INSTANCE,
                VClip.INSTANCE,
                Velocity.INSTANCE,

                // Render
                AspectRatio.INSTANCE,
                Breadcrumbs.INSTANCE,
                CameraClip.INSTANCE,
                Chams.INSTANCE,
                ESP.INSTANCE,
                Fullbright.INSTANCE,
                HandsView.INSTANCE,
                Hat.INSTANCE,
                NameTags.INSTANCE,
                NoRender.INSTANCE,
                PopChams.INSTANCE,
                AntiAlias.INSTANCE,
                Filter.INSTANCE,
                Tracers.INSTANCE,
                Zoom.INSTANCE,

                // Hud
                NotificationsHud.INSTANCE,
                InventoryHud.INSTANCE,
                ModuleListHud.INSTANCE,
                PotionHud.INSTANCE,
                TargetHud.INSTANCE,
                WatermarkHud.INSTANCE,

                // Misc
                AntiSpam.INSTANCE,
                AutoReconnect.INSTANCE,
                TimerModule.INSTANCE

        ));

        // Initialize i18n for all duskbyte modules
        for (Module module : modules) {
            module.setAddonId("duskbyte");
            module.initI18n(DuskTranslate.create("modules", module.getName().toLowerCase()));
        }
    }

    public void registerAddonModule(String addonId, Module module, TranslateComponent moduleComponent) {
        module.setAddonId(addonId);
        module.initI18n(moduleComponent);
        modules.add(module);
    }

    public List<Module> getModules() {
        return modules;
    }

    @EventHandler
    private void onRender2D(Render2DEvent.HUD event) {
        Minecraft mc = Minecraft.getInstance();
        if (ClientUtils.isLoading() || mc.level == null || mc.screen instanceof HudEditorScreen) return;

        for (Module m : modules) {
            if (m instanceof HudModule module && module.isEnabled()) {
                DeltaTracker delta = mc.getDeltaTracker();
                module.updateLayout();
                module.render(event.getGuiGraphics(), delta);
            }
        }
    }

    @EventHandler
    private void onKeyPress(KeyPressEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.screen != null || event.getKey() == GLFW.GLFW_KEY_UNKNOWN) return;

        int keyCode = event.getKey();
        int action = event.getAction();

        if (keyCode == ClientSetting.INSTANCE.guiKeybind.getValue() && action == InputConstants.PRESS) {
            mc.setScreen(PanelScreen.INSTANCE);
        }

        dispatchKeyBind(keyCode, action);
    }

    @EventHandler
    private void onMousePress(MousePressEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.screen == null) {
            dispatchKeyBind(KeybindUtils.encodeMouseButton(event.getButton()), event.getAction());
        }
    }

    private void dispatchKeyBind(int keyCode, int action) {
        boolean isPress = action == InputConstants.PRESS;
        boolean isRelease = action == InputConstants.RELEASE;

        List<Module> affectedModules = new ArrayList<>();
        boolean hasEnabling = false;

        for (Module module : modules) {
            if (module.getKeyBind() != keyCode) continue;

            if (module.getBindMode() == Module.BindMode.Toggle && isPress) {
                if (!module.isEnabled()) {
                    hasEnabling = true;
                }
                affectedModules.add(module);
            } else if (module.getBindMode() == Module.BindMode.Hold) {
                if (isPress && !module.isEnabled()) {
                    hasEnabling = true;
                    affectedModules.add(module);
                } else if (isRelease && module.isEnabled()) {
                    affectedModules.add(module);
                }
            }
        }

        for (Module module : affectedModules) {
            if (module.getBindMode() == Module.BindMode.Toggle) {
                module.toggle();
            } else if (module.getBindMode() == Module.BindMode.Hold) {
                if (isPress && !module.isEnabled()) {
                    module.setEnabled(true);
                } else if (isRelease && module.isEnabled()) {
                    module.setEnabled(false);
                }
            }
            if (ClientSetting.INSTANCE.chatNotify.getValue()) {
                ChatUtils.addChatMessage(module.getTranslatedName() + " is now " + (module.isEnabled() ? "enabled" : "disabled"));
            }
        }

        if (!affectedModules.isEmpty() && ClientSetting.INSTANCE.soundNotify.getValue()) {
            if (hasEnabling) {
                SoundManager.INSTANCE.playInUi(SoundKey.ENABLE);
            } else {
                SoundManager.INSTANCE.playInUi(SoundKey.DISABLE);
            }
        }
    }

}
