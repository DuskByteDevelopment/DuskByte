package com.github.duskbyte.modules.impl.combat;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.DoubleSetting;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Hitboxes extends Module {

    public static final Hitboxes INSTANCE = new Hitboxes();

    private final DoubleSetting expand = doubleSetting("Expand", 0.5, 0.1, 3.0, 0.1);

    private final Map<Entity, AABB> originalBoxes = new ConcurrentHashMap<>();

    private Hitboxes() {
        super("Hitboxes", Category.COMBAT);
    }

    @Override
    protected void onDisable() {
        // Restore all original bounding boxes
        for (Map.Entry<Entity, AABB> entry : originalBoxes.entrySet()) {
            if (entry.getKey().isAlive()) {
                entry.getKey().setBoundingBox(entry.getValue());
            }
        }
        originalBoxes.clear();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;

        float size = expand.getValue().floatValue();
        if (size == 0) return;

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof Player && entity != mc.player && entity.isAlive()) {
                // Store original box on first encounter
                if (!originalBoxes.containsKey(entity)) {
                    originalBoxes.put(entity, entity.getBoundingBox());
                }

                AABB box = entity.getBoundingBox();
                double dx = (box.maxX - box.minX) / 2.0;
                double dz = (box.maxZ - box.minZ) / 2.0;
                entity.setBoundingBox(new AABB(
                        box.minX - size + dx, box.minY, box.minZ - size + dz,
                        box.maxX + size - dx, box.maxY, box.maxZ + size - dz
                ));
            }
        }

        // Clean up dead entities
        originalBoxes.entrySet().removeIf(e -> !e.getKey().isAlive());
    }
}
