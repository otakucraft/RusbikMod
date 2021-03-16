package com.kahzerx.rubik.mixins;

import net.minecraft.util.UserCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(UserCache.class)
public class UserCacheWhitelistMixin {
    @Redirect(method = "findProfileByName", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/UserCache;shouldUseRemote()Z"))
    private static boolean findProfileByName() {
        return true;
    }
}
