package rusbik;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import rusbik.commands.BackCommand;
import rusbik.commands.BlockInfoCommand;
import rusbik.commands.RusbisCameraCommand;
import rusbik.commands.RusbisSurvivalCommand;
import rusbik.commands.DiscordCommand;
import rusbik.commands.HereCommand;
import rusbik.commands.HomeCommand;
import rusbik.commands.SetHomeCommand;
import rusbik.commands.PermsCommand;
import rusbik.commands.PitoCommand;
import rusbik.commands.RandomTpCommand;
import rusbik.commands.SBCommand;
import rusbik.commands.SpoofCommand;
import rusbik.commands.AdminTeleportCommand;
import rusbik.commands.CustomTeleportCommand;
import rusbik.settings.RubiConfig;

public class Rusbik {

    public static RubiConfig config;

    public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        RandomTpCommand.register(dispatcher);
        SetHomeCommand.register(dispatcher);
        HomeCommand.register(dispatcher);
        PermsCommand.register(dispatcher);
        CustomTeleportCommand.register(dispatcher);
        AdminTeleportCommand.register(dispatcher);
        BackCommand.register(dispatcher);
        DiscordCommand.register(dispatcher);
        RusbisCameraCommand.register(dispatcher);
        RusbisSurvivalCommand.register(dispatcher);
        HereCommand.register(dispatcher);
        SpoofCommand.register(dispatcher);
        PitoCommand.register(dispatcher);
        BlockInfoCommand.register(dispatcher);
        SBCommand.register(dispatcher);
    }
}
