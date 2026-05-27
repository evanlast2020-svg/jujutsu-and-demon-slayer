package com.teambridge;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Every second (20 ticks), checks each online player's team and resets
 * any progress gained in the mod they're NOT on.
 *
 * This is the reliable enforcement method — MCreator mods store all their
 * data in capability fields which get updated every tick by their own systems.
 * We can't cancel those updates, but we can zero them out right after.
 */
public class TeamEnforcer {

    private static int tickCount = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCount++;
        // Run every 20 ticks (1 second) — frequent enough to prevent any gain,
        // infrequent enough to not hammer reflection
        if (tickCount % 20 != 0) return;

        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            String team = TeamData.getTeam(player.getUUID());
            if (TeamData.TEAM_NONE.equals(team)) continue;

            if (TeamData.TEAM_DS.equals(team)) {
                // DS player: lock all JJK progression
                ModReflect.lockJJKProgress(player);
            } else if (TeamData.TEAM_JJK.equals(team)) {
                // JJK player: lock all KNY progression
                ModReflect.lockKNYProgress(player);
            }
        }
    }
}
