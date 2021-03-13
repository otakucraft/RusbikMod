// by: slowik -- https://github.com/SlowikGZ

package com.kahzerx.rubik.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.network.MessageType;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.level.ServerWorldProperties;

import java.io.File;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class SBCommand {
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> literalargumentbuilder = literal("sb")
                .then(literal("broken")
                        .then(argument("item", ItemStackArgumentType.itemStack())
                                .executes(ctx -> showSidebar(
                                        ctx.getSource(),
                                        ItemStackArgumentType.getItemStackArgument(ctx, "item"),
                                        "broken"))))
                .then(literal("crafted")
                        .then(argument("item", ItemStackArgumentType.itemStack())
                                .executes(ctx -> showSidebar(
                                        ctx.getSource(),
                                        ItemStackArgumentType.getItemStackArgument(ctx, "item"),
                                        "crafted"))))
                .then(literal("mined")
                        .then(argument("item", ItemStackArgumentType.itemStack())
                                .executes(ctx -> showSidebar(
                                        ctx.getSource(),
                                        ItemStackArgumentType.getItemStackArgument(ctx, "item"),
                                        "mined"))))
                .then(literal("used")
                        .then(argument("item", ItemStackArgumentType.itemStack())
                                .executes(ctx -> showSidebar(
                                        ctx.getSource(),
                                        ItemStackArgumentType.getItemStackArgument(ctx, "item"),
                                        "used"))))
                .then(literal("clear")
                        .executes(ctx -> clearSidebar(ctx.getSource())));

        dispatcher.register(literalargumentbuilder);
    }

    private SBCommand() { }

    public static int showSidebar(final ServerCommandSource source, final ItemStackArgument item, final String type) {
        Scoreboard scoreboard = source.getMinecraftServer().getScoreboard();
        Item minecraftItem = item.getItem();
        String objectiveName = type + "." + Item.getRawId(minecraftItem);
        ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(objectiveName);

        Entity entity = source.getEntity();
        Text text;

        if (scoreboardObjective != null) {
            if (scoreboard.getObjectiveForSlot(1) == scoreboardObjective) {
                text = new LiteralText("Ya se está mostrando ese scoreboard.");
            } else {
                assert entity != null;
                text = new LiteralText(
                        entity.getEntityName()
                                + " ha seleccionado el scoreboard "
                                + Formatting.GOLD
                                + "["
                                + scoreboardObjective.getDisplayName().asString()
                                + "]");
                scoreboard.setObjectiveSlot(1, scoreboardObjective);
            }
        } else {
            String criteriaName = "minecraft." + type + ":minecraft." + item.getItem().toString();
            String capitalize = type.substring(0, 1).toUpperCase() + type.substring(1);
            String displayName = capitalize + " " + minecraftItem.toString().replaceAll("_", " ");
            ScoreboardCriterion criteria = null;

            if (ScoreboardCriterion.createStatCriterion(criteriaName).isPresent()) {
                criteria = ScoreboardCriterion.createStatCriterion(criteriaName).get();
            }

            assert criteria != null;

            scoreboard.addObjective(objectiveName,
                    criteria,
                    new LiteralText(displayName).formatted(Formatting.GOLD),
                    criteria.getCriterionType());

            ScoreboardObjective newScoreboardObjective = scoreboardObjective = scoreboard.getNullableObjective(objectiveName);

            try {
                initialize(source, newScoreboardObjective, minecraftItem, type);
            } catch (Exception e) {
                scoreboard.removeObjective(newScoreboardObjective);
                text = new LiteralText(
                        "Ha ocurrido un error al momento de seleccionar "
                                + "un scoreboard, inténtelo de nuevo.").
                        formatted(Formatting.RED);
                assert entity != null;
                source.getMinecraftServer().getPlayerManager().broadcastChatMessage(
                        text,
                        MessageType.CHAT,
                        entity.getUuid());

                return Command.SINGLE_SUCCESS;
            }

            scoreboard.setObjectiveSlot(1, newScoreboardObjective);

            assert entity != null;
            assert scoreboardObjective != null;
            text = new LiteralText(
                    entity.getEntityName()
                            + " ha seleccionado el scoreboard "
                            + Formatting.GOLD
                            + "["
                            + scoreboardObjective.getDisplayName().asString()
                            + "]");
        }
        assert entity != null;
        source.getMinecraftServer().getPlayerManager().broadcastChatMessage(text,
                MessageType.CHAT,
                entity.getUuid());

        return Command.SINGLE_SUCCESS;
    }

    public static int clearSidebar(final ServerCommandSource source) throws CommandSyntaxException {
        Scoreboard sc = source.getMinecraftServer().getScoreboard();
        if (sc.getObjectiveForSlot(1) == null) {
            source.sendFeedback(new LiteralText("No hay ningún scoreboard."), false);
        } else {
            sc.setObjectiveSlot(1, null);
            Text text = new LiteralText(
                    String.format(
                            "%s ha eliminado el scoreboard.",
                            source.getPlayer().getName().asString()));
            source.getMinecraftServer().getPlayerManager().broadcastChatMessage(
                    text,
                    MessageType.CHAT,
                    source.getPlayer().getUuid());
        }

        return 1;
    }

    public static void initialize(final ServerCommandSource source, final ScoreboardObjective scoreboardObjective, final Item item, final String type) {
        Scoreboard scoreboard = source.getMinecraftServer().getScoreboard();
        MinecraftServer server = source.getMinecraftServer();

        File file = new File(((ServerWorldProperties) server.getOverworld().getLevelProperties()).getLevelName(), "stats");
        File[] stats = file.listFiles();

        for (File stat: stats) {
            String fileName = stat.getName();
            String uuidString = fileName.substring(0, fileName.lastIndexOf(".json"));

            UUID uuid = UUID.fromString(uuidString);
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);

            Stat<?> finalStat = null;

            if (type.equalsIgnoreCase("broken")) {
                finalStat = Stats.BROKEN.getOrCreateStat(item);
            } else if (type.equalsIgnoreCase("crafted")) {
                finalStat = Stats.CRAFTED.getOrCreateStat(item);
            } else if (type.equalsIgnoreCase("mined")) {
                finalStat = Stats.MINED.getOrCreateStat(Block.getBlockFromItem(item));
            } else if (type.equalsIgnoreCase("used")) {
                finalStat = Stats.USED.getOrCreateStat(item);
            }

            String playerName;
            int value;

            if (player != null) {
                value = player.getStatHandler().getStat(finalStat);
                playerName = player.getEntityName();
            } else {
                ServerStatHandler serverStatHandler = new ServerStatHandler(server, stat);
                value = serverStatHandler.getStat(finalStat);
                GameProfile gameProfile = server.getUserCache().getByUuid(uuid);
                if (gameProfile != null) {
                    playerName = gameProfile.getName();
                } else {
                    continue;
                }
            }

            if (value == 0) {
                continue;
            }

            ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(playerName, scoreboardObjective);
            scoreboardPlayerScore.setScore(value);
        }
    }
}
