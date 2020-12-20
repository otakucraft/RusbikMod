package rusbik.mixins;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rusbik.Rusbik;
import rusbik.database.RusbikDatabase;
import rusbik.utils.FileManager;
import rusbik.utils.DiscordListener;

import java.sql.SQLException;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
// Mixin que inicializa todos los procesos.
public class RusbikServerRunMixin {
    @Inject(method = "runServer", at = @At("HEAD"))
    public void run (CallbackInfo ci){
        RusbikDatabase.initializeDB();  // Crear si fuera necesario y establecer con conexión con la base de datos.
        try {
            FileManager.initializeYaml();  // Cargar la configuración del archivo .yaml
            if (Rusbik.config.chatChannelId != 0 && !Rusbik.config.discordToken.equals("")) {
                if (Rusbik.config.isRunning) {  // Iniciar el bot de discord.
                    try {
                        DiscordListener.connect((MinecraftServer) (Object) this, Rusbik.config.discordToken, String.valueOf(Rusbik.config.chatChannelId));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("config file not created");
        }
    }

    // Detiene el bot y la conexión con la base de datos al cerrar el servidor.
    @Inject(method = "runServer", at = @At("RETURN"))
    public void stop (CallbackInfo ci) throws SQLException {
        if (RusbikDatabase.c != null) RusbikDatabase.c.close();
        if (DiscordListener.chatBridge) DiscordListener.stop();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;saveAllPlayerData()V"))
    public void onSave(BooleanSupplier shouldKeepTicking, CallbackInfo ci) throws SQLException {
        DiscordListener.checkSub(RusbikDatabase.getIDs());
    }
}
