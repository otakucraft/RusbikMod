/*Fallen <3*/
package com.kahzerx.rubik.mixins;

import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.mob.DrownedEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DrownedEntity.class)
public interface DrownedEntityAccessor {
    @Accessor
    SwimNavigation getWaterNavigation();

    @Accessor
    MobNavigation getLandNavigation();
}
