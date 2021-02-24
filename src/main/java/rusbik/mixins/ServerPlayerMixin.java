package rusbik.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rusbik.database.RusbikDatabase;
import rusbik.discord.utils.DiscordListener;
import rusbik.utils.KrusbibUtils;

import java.sql.SQLException;

@Mixin(ServerPlayerEntity.class)
// Mixin para notificar por discord cuando un jugador muere, asi como actualizar su posición de muerte en la base de datos y enviar la posición por privado.
public abstract class ServerPlayerMixin extends PlayerEntity {

    public ServerPlayerMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/CombatEventS2CPacket;<init>(Lnet/minecraft/entity/damage/DamageTracker;Lnet/minecraft/network/packet/s2c/play/CombatEventS2CPacket$Type;Lnet/minecraft/text/Text;)V", ordinal = 0))
    public void onPlayerDies(DamageSource source, CallbackInfo ci) throws SQLException {
        if (DiscordListener.chatBridge){
            DiscordListener.sendMessage(":skull_crossbones: **" + this.getDamageTracker().getDeathMessage().getString().replace("_", "\\_") + "**");
        }

        this.sendMessage(new LiteralText(String.format("RIP ;( %s %s", KrusbibUtils.getDimensionWithColor(this.world), KrusbibUtils.formatCoords(this.getPos().x, this.getPos().y, this.getPos().z))), false);

        if (RusbikDatabase.userExists(this.getEntityName())) {
            RusbikDatabase.updatePlayerInformation(this.getEntityName(), this.getX(), this.getY(), this.getZ(), KrusbibUtils.getDim(this.world));
        }
    }
}
