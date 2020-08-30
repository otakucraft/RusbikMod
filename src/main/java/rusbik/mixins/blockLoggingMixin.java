package rusbik.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rusbik.Rusbik;
import rusbik.database.RusbikDatabase;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mixin(Block.class)
public class blockLoggingMixin {
    @Inject(method = "onBreak", at = @At("HEAD"))
    private void broken(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) throws SQLException {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        RusbikDatabase.blockLogging(player.getName().getString(), state.getBlock().getTranslationKey(), pos.getX(), pos.getY(), pos.getZ(), Rusbik.getDim(world), 0, date.format(format));
    }

    @Inject(method = "onPlaced", at = @At("HEAD"))
    private void placed(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo ci) throws SQLException {
        if (placer instanceof ServerPlayerEntity){
            LocalDateTime date = LocalDateTime.now();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

            RusbikDatabase.blockLogging(placer.getName().getString(), state.getBlock().getTranslationKey(), pos.getX(), pos.getY(), pos.getZ(), Rusbik.getDim(world), 1, date.format(format));
        }
    }
}
