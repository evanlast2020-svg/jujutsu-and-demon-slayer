package com.teambridge;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Stores UUID → team ("jjk" or "ds") in world/data/teambridge.dat
 * Survives server restarts.
 */
public class TeamData {

    public static final String TEAM_JJK = "jjk";
    public static final String TEAM_DS  = "ds";
    public static final String TEAM_NONE = "none";

    private static final Logger LOG = LogManager.getLogger("teambridge");
    private static final Map<UUID, String> TEAMS = new HashMap<>();
    private static File saveFile;

    public static void load(MinecraftServer server) {
        saveFile = new File(server.getWorldPath(
            net.minecraft.world.level.storage.LevelResource.ROOT).toFile(),
            "data/teambridge.dat");
        TEAMS.clear();
        if (!saveFile.exists()) return;
        try {
            CompoundTag tag = NbtIo.read(saveFile);
            if (tag == null) return;
            CompoundTag players = tag.getCompound("players");
            for (String key : players.getAllKeys()) {
                try {
                    TEAMS.put(UUID.fromString(key), players.getString(key));
                } catch (Exception ignored) {}
            }
            LOG.info("[TeamBridge] Loaded {} team assignments", TEAMS.size());
        } catch (Exception e) {
            LOG.error("[TeamBridge] Failed to load team data: {}", e.getMessage());
        }
    }

    public static void save() {
        if (saveFile == null) return;
        try {
            saveFile.getParentFile().mkdirs();
            CompoundTag root = new CompoundTag();
            CompoundTag players = new CompoundTag();
            for (Map.Entry<UUID, String> e : TEAMS.entrySet()) {
                players.putString(e.getKey().toString(), e.getValue());
            }
            root.put("players", players);
            NbtIo.write(root, saveFile);
        } catch (Exception e) {
            LOG.error("[TeamBridge] Failed to save team data: {}", e.getMessage());
        }
    }

    public static String getTeam(UUID uuid) {
        return TEAMS.getOrDefault(uuid, TEAM_NONE);
    }

    public static void setTeam(UUID uuid, String team) {
        TEAMS.put(uuid, team);
        save();
    }

    public static boolean isJJK(UUID uuid) { return TEAM_JJK.equals(getTeam(uuid)); }
    public static boolean isDS(UUID uuid)  { return TEAM_DS.equals(getTeam(uuid)); }
    public static boolean hasTeam(UUID uuid) { return !TEAM_NONE.equals(getTeam(uuid)); }
}
