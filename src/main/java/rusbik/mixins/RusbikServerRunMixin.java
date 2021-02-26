package rusbik.mixins;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rusbik.Rusbik;
import rusbik.database.RusbikDatabase;
import rusbik.utils.FileManager;
import rusbik.discord.utils.DiscordListener;

import java.sql.SQLException;
import java.util.function.BooleanSupplier;


/**
 * Mixin that initializes all processes.
 */
@Mixin(MinecraftServer.class)
public class RusbikServerRunMixin {
    @Shadow @Final protected LevelStorage.Session session;

    @Inject(method = "runServer", at = @At("HEAD"))
    public void run (CallbackInfo ci){
        RusbikDatabase.initializeDB(session.getDirectoryName());  // Crear si fuera necesario y establecer con conexión con la base de datos.
        FileManager.directoryName = session.getDirectoryName();
        try {
            FileManager.initializeJson();  // Cargar la configuración del archivo .yaml
            if (Rusbik.config.getChatChannelId() != 0 && !Rusbik.config.getDiscordToken().equals("")) {
                if (Rusbik.config.isRunning()) {  // Iniciar el bot de discord.
                    try {
                        DiscordListener.connect((MinecraftServer) (Object) this, Rusbik.config.getDiscordToken(), String.valueOf(Rusbik.config.getChatChannelId()));
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

    /**
     * Server shut down
     * Stops the bot and the database connection when shutting down the server.
     * Stops all threads
     */
    @Inject(method = "runServer", at = @At("RETURN"))
    public void stop (CallbackInfo ci) throws SQLException, InterruptedException {
        if (RusbikDatabase.logger.isAlive()) {
            RusbikDatabase.logger.running = false; // Needs database connection until it stops
            RusbikDatabase.logger.join();
        }
        if (RusbikDatabase.c != null) RusbikDatabase.c.close();
        if (DiscordListener.chatBridge) DiscordListener.stop(); 
    }

    /**
     * Server regular tasks
     */
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;saveAllPlayerData()V"))
    public void onSave(BooleanSupplier shouldKeepTicking, CallbackInfo ci) throws SQLException {
        DiscordListener.checkSub(RusbikDatabase.getIDs());
        RusbikDatabase.clearLogger();
    }
}
