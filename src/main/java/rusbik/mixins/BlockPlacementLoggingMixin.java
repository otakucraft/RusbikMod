package rusbik.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import rusbik.utils.KrusbibUtils;

import java.sql.SQLException;
import rusbik.database.RusbikBlockActionPerformLog;
import rusbik.database.RusbikDatabase;

@Mixin(BlockItem.class)
/**
 * Mixin to record in the database when a player places a block.
 */
public class BlockPlacementLoggingMixin {
    @Redirect(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onPlaced(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)V"))
    private void onPlace(Block block, World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) throws SQLException {
        if (placer instanceof ServerPlayerEntity) {
            RusbikDatabase.logger.addBlockActionPerformLog(new RusbikBlockActionPerformLog(
                        placer.getName().getString(),
                        state.getBlock().getTranslationKey(),
                        pos.getX(), pos.getY(), pos.getZ(), KrusbibUtils.getDim(world),
                        1,
                        KrusbibUtils.getDate()
                )
            );
        }
        block.onPlaced(world, pos, state, placer, itemStack);
    }
}
