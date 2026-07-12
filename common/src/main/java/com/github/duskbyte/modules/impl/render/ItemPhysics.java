package com.github.duskbyte.modules.impl.render;

import com.github.duskbyte.events.bus.EventHandler;
import com.github.duskbyte.events.impl.TickEvent;
import com.github.duskbyte.modules.Category;
import com.github.duskbyte.modules.Module;
import com.github.duskbyte.settings.impl.BoolSetting;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;

public class ItemPhysics extends Module {

    public static final ItemPhysics INSTANCE = new ItemPhysics();

    private final BoolSetting despawnTime = boolSetting("Show Despawn Time", true);

    private ItemPhysics() {
        super("Item Physics", Category.RENDER);
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (nullCheck()) return;

        // Make dropped items float
        AABB range = mc.player.getBoundingBox().inflate(16);
        mc.level.getEntities(EntityType.ITEM, range, item -> true)
                .forEach(itemEntity -> {
                    // Apply a slight upward motion to make items float
                    if (itemEntity.isAlive() && !itemEntity.isInWater()) {
                        if (itemEntity.getDeltaMovement().y < 0.1) {
                            itemEntity.setDeltaMovement(
                                    itemEntity.getDeltaMovement().x,
                                    itemEntity.getDeltaMovement().y + 0.002,
                                    itemEntity.getDeltaMovement().z
                            );
                        }
                    }
                });
    }

}