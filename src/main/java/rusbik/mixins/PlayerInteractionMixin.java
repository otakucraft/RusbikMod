package rusbik.mixins;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rusbik.Rusbik;
import rusbik.utils.KrusbibUtils;

/**
 * Mixin to record in the database when a player uses a block.
 */
@Mixin(ServerPlayerInteractionManager.class)
public class PlayerInteractionMixin {
    @Shadow public ServerWorld world;
    @Inject(method = "interactBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;shouldCancelInteraction()Z"))
    private void onRightClick(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (KrusbibUtils.shouldRegisterBlock(world.getBlockState(hitResult.getBlockPos()).getBlock(), player)){
            Rusbik.onBlockInteraction(
                    player.getName().getString(),
                    world.getBlockState(hitResult.getBlockPos()).getBlock().getTranslationKey(),
                    hitResult.getBlockPos(),
                    KrusbibUtils.getDim(world),
                    2
            );
        } else if (KrusbibUtils.shouldRegisterItem(player, stack)) {
            Rusbik.onBlockInteraction(
                    player.getName().getString(),
                    stack.getItem().getTranslationKey(),
                    hitResult.getBlockPos(),
                    KrusbibUtils.getDim(world),
                    2
            );
        }
    }
}
