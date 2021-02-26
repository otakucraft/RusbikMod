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
import rusbik.database.RusbikDatabase;
import rusbik.utils.KrusbibUtils;

import java.sql.SQLException;
import rusbik.database.RusbikBlockAccionPerformLog;

@Mixin(Block.class)
/**
 * Mixin to record in the database when a player breaks a block.
 */
public abstract class BlockBreakingLoggingMixin {
    @Inject(method = "onBreak", at = @At("HEAD"))
    private void broken(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) throws SQLException {
        RusbikDatabase.logger.addBlockAccionPerformLog(
            new RusbikBlockAccionPerformLog(
                    player.getName().getString(),
                    state.getBlock().getTranslationKey(),
                    pos.getX(), pos.getY(), pos.getZ(), KrusbibUtils.getDim(world),
                    0,
                    KrusbibUtils.getDate()
            )
        );
    }
}
