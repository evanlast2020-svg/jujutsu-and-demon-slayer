package com.teambridge;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class TeamCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();

        // /jjk <player>
        dispatcher.register(Commands.literal("jjk")
            .requires(src -> src.hasPermission(2))
            .then(Commands.argument("player", EntityArgument.player())
                .executes(ctx -> assignTeam(ctx.getSource(),
                    EntityArgument.getPlayer(ctx, "player"), TeamData.TEAM_JJK))));

        // /demonslayer <player>
        dispatcher.register(Commands.literal("demonslayer")
            .requires(src -> src.hasPermission(2))
            .then(Commands.argument("player", EntityArgument.player())
                .executes(ctx -> assignTeam(ctx.getSource(),
                    EntityArgument.getPlayer(ctx, "player"), TeamData.TEAM_DS))));

        // /teamcheck <player> — see what team someone is on
        dispatcher.register(Commands.literal("teamcheck")
            .requires(src -> src.hasPermission(2))
            .then(Commands.argument("player", EntityArgument.player())
                .executes(ctx -> {
                    ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                    String team = TeamData.getTeam(target.getUUID());
                    ctx.getSource().sendSuccess(() -> Component.literal(
                        "§e" + target.getName().getString() + "§7 is on team: §b" + team), false);
                    return 1;
                })));

        // /teamreset <player> — remove from all teams (sets to none)
        dispatcher.register(Commands.literal("teamreset")
            .requires(src -> src.hasPermission(2))
            .then(Commands.argument("player", EntityArgument.player())
                .executes(ctx -> {
                    ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                    fullReset(target);
                    TeamData.setTeam(target.getUUID(), TeamData.TEAM_NONE);
                    ctx.getSource().sendSuccess(() -> Component.literal(
                        "§a" + target.getName().getString() + "§7 has been reset and removed from all teams."), true);
                    target.sendSystemMessage(Component.literal(
                        "§8[§6TeamBridge§8] §7You have been removed from all teams and your progress has been reset."));
                    return 1;
                })));
    }

    private static int assignTeam(CommandSourceStack src, ServerPlayer target, String newTeam) {
        String oldTeam = TeamData.getTeam(target.getUUID());
        boolean switching = !oldTeam.equals(TeamData.TEAM_NONE) && !oldTeam.equals(newTeam);

        if (oldTeam.equals(newTeam)) {
            src.sendSuccess(() -> Component.literal(
                "§e" + target.getName().getString() + "§7 is already on the §b" + newTeam + "§7 team."), false);
            return 0;
        }

        // Full reset if switching teams — clean slate
        if (switching) {
            fullReset(target);
            target.sendSystemMessage(Component.literal(
                "§8[§6TeamBridge§8] §cYour progress has been fully reset. You have switched from §e"
                + oldTeam.toUpperCase() + "§c to §e" + newTeam.toUpperCase() + "§c."));
        } else {
            // First assignment — just wipe the other mod's data
            if (newTeam.equals(TeamData.TEAM_JJK)) {
                ModReflect.wipeKNYProgress(target);
            } else {
                ModReflect.wipeJJKProgress(target);
            }
            target.sendSystemMessage(Component.literal(
                "§8[§6TeamBridge§8] §aYou have been assigned to the §e"
                + newTeam.toUpperCase() + "§a team!"));
        }

        TeamData.setTeam(target.getUUID(), newTeam);

        String teamDisplay = newTeam.equals(TeamData.TEAM_JJK)
            ? "§5§lJujutsuCraft §7(JJK)" : "§9§lDemon Slayer §7(DS)";
        src.sendSuccess(() -> Component.literal(
            "§a" + target.getName().getString() + "§7 has been assigned to " + teamDisplay), true);

        return 1;
    }

    /** Wipes ALL progress from BOTH mods. Used when switching teams. */
    static void fullReset(ServerPlayer player) {
        ModReflect.wipeJJKProgress(player);
        ModReflect.wipeKNYProgress(player);
        // Clear inventory of any team-locked items
        TeamItemGuard.clearBannedItems(player);
    }

    @SubscribeEvent
    public static void onServerStart(ServerStartingEvent event) {
        TeamData.load(event.getServer());
        TeamBridge.LOG.info("[TeamBridge] Team data loaded.");
    }
}
