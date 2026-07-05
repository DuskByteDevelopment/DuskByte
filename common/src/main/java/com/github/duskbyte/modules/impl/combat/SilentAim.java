package com.github.duskbyte.modules.impl.combat;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.SwingHandEvent;
import com.github.duskbyte.managers.RotationManager;
import com.github.duskbyte.managers.TargetManager;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.BoolSetting;
import com.github.duskbyte.settings.impl.DoubleSetting;
import com.github.duskbyte.settings.impl.IntSetting;
import com.github.duskbyte.utils.rotation.Priority;
import com.github.duskbyte.utils.rotation.RotationUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.HitResult;
import org.joml.Vector2f;

public class SilentAim extends Module {

    public static final SilentAim INSTANCE = new SilentAim();

    private SilentAim() {
        super("Silent Aim", Category.COMBAT);
    }

    private final BoolSetting weaponOnly = boolSetting("Weapon Only", false);

    private final BoolSetting player = boolSetting("Player", true);
    private final BoolSetting mob = boolSetting("Mob", false);
    private final BoolSetting animal = boolSetting("Animal", false);
    private final BoolSetting villagers = boolSetting("Villagers", false);
    private final BoolSetting invisible = boolSetting("Invisible", false);

    private final DoubleSetting range = doubleSetting("Range", 3.0, 1.0, 6.0, 0.1);
    private final IntSetting fov = intSetting("FOV", 360, 10, 360, 1);

    private boolean redirecting;

    @EventHandler
    private void onSwingHand(SwingHandEvent event) {
        if (redirecting) return;

        if (weaponOnly.getValue() && !mc.player.getMainHandItem().has(DataComponents.WEAPON)) {
            return;
        }

        if (mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.MISS) {
            return;
        }

        LivingEntity target = TargetManager.INSTANCE.acquirePrimary(TargetManager.TargetRequest.of(
                range.getValue(), fov.getValue(), player.getValue(), mob.getValue(), animal.getValue(), villagers.getValue(), invisible.getValue(), 1
        ));
        if (target == null) return;

        event.setCancelled(true);
        redirecting = true;

        Vector2f rotations = RotationUtils.calculate(target.getEyePosition());

        RotationManager.INSTANCE.applyRotation(rotations, 10, Priority.High, _ -> {
            if (!target.isAlive() || target.isDeadOrDying() || nullCheck()) {
                redirecting = false;
                return;
            }

            mc.gameMode.attack(mc.player, target);
            mc.player.swing(InteractionHand.MAIN_HAND);

            redirecting = false;
        });
    }

}
