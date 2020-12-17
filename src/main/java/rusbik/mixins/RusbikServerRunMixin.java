package rusbik.mixins;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rusbik.Rusbik;
import rusbik.database.RusbikDatabase;
import rusbik.helpers.DiscordFileManager;
import rusbik.helpers.DiscordListener;

import java.sql.SQLException;

@Mixin(MinecraftServer.class)
public class RusbikServerRunMixin {
    @Inject(method = "runServer", at = @At("HEAD"))
    public void run (CallbackInfo ci){
        RusbikDatabase.initializeDB();
        try {
            DiscordFileManager.initializeYaml();
            if (Rusbik.config.chatChannelId != 0 && !Rusbik.config.discordToken.equals("")) {
                if (Rusbik.config.isRunning) {
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
    @Inject(method = "runServer", at = @At("RETURN"))
    public void stop (CallbackInfo ci) throws SQLException {
        if (RusbikDatabase.c != null) RusbikDatabase.c.close();
        if (DiscordListener.chatBridge) DiscordListener.stop();
    }
}
