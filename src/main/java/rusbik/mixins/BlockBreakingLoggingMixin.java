package rusbik.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rusbik.Rusbik;
import rusbik.utils.KrusbibUtils;

/**
 * Mixin to record in the database when a player breaks a block.
 */
@Mixin(Block.class)
public abstract class BlockBreakingLoggingMixin {
    @Inject(method = "onBreak", at = @At("HEAD"))
    private void broken(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) {
        Rusbik.onBlockInteraction(
                player.getName().getString(),
                state.getBlock().getTranslationKey(),
                pos,
                KrusbibUtils.getDim(world),
                0
        );
    }
}
