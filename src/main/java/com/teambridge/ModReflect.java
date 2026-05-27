package com.teambridge;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Reflection access to JJK (JujutsuCraft ver50) and KNY (KimetsunoYaiba ver3)
 * capability/variable fields.
 *
 * JJK package: net.mcreator.jujutsucraft
 * KNY package: net.mcreator.kimetsunoyaiba
 *
 * All field names confirmed from class file constant pool inspection.
 */
public class ModReflect {

    // ── JJK ───────────────────────────────────────────────────────────────────
    private static boolean jjkTried, jjkOk;
    private static Capability<?> JJK_CAP;
    private static Field JJK_PlayerFame;
    private static Field JJK_PlayerExperience;
    private static Field JJK_PlayerLevel;
    private static Field JJK_PlayerCursePower;
    private static Field JJK_PlayerCursePowerMAX;
    private static Field JJK_PlayerCursePowerFormer;
    private static Field JJK_PlayerCursePowerChange;
    private static Field JJK_PlayerCharge;
    private static Field JJK_PlayerCurseTechnique;
    private static Field JJK_PlayerCurseTechnique2;
    private static Field JJK_PlayerSelectCurseTechnique;
    private static Field JJK_PlayerSelectCurseTechniqueName;
    private static Field JJK_PlayerSelectCurseTechniqueCost;
    private static Field JJK_PlayerSelectCurseTechniqueCostOrgin;
    private static Field JJK_PlayerProfession;
    private static Field JJK_PlayerTechniqueUsedNumber;
    private static Field JJK_PlayerFlag_A;
    private static Field JJK_PlayerFlag_B;
    private static Field JJK_FlagSixEyes;
    private static Field JJK_PhysicalAttack;
    private static Field JJK_PassiveTechnique;
    private static Field JJK_SecondTechnique;
    private static Field JJK_noChangeTechnique;
    private static Field JJK_flag_sukuna;
    private static Field JJK_flag_shift;
    private static Field JJK_cnt_curse1;
    private static Field JJK_friend_num_keep;
    private static Field JJK_use_mainSkill;
    private static Method JJK_SYNC;

    // ── KNY ───────────────────────────────────────────────────────────────────
    private static boolean knyTried, knyOk;
    private static Capability<?> KNY_CAP;
    private static Field KNY_PlayerLevel;
    private static Field KNY_KILL_POINT_1;
    private static Field KNY_KILL_POINT_2;
    private static Field KNY_kill_hashira;
    private static Field KNY_player_usedBreathingNum;
    private static Field KNY_NUM_SKILL;
    private static Field KNY_playerBack;
    private static Field KNY_player_receiveDamage;
    private static Field KNY_player_nichirincolor;
    private static Method KNY_SYNC;

    // ─────────────────────────────────────────────────────────────────────────

    public static boolean initJJK() {
        if (jjkTried) return jjkOk;
        jjkTried = true;
        try {
            Class<?> varsClass = Class.forName(
                "net.mcreator.jujutsucraft.network.JujutsucraftModVariables$PlayerVariables");
            Class<?> modClass  = Class.forName(
                "net.mcreator.jujutsucraft.network.JujutsucraftModVariables");

            // Get capability via static PLAYER_VARIABLES field on the mod vars class
            Field capField = tryField(modClass, "PLAYER_VARIABLES", "CAP", "CAPABILITY",
                "PLAYER_CAP", "playerVariablesCap");
            if (capField != null) JJK_CAP = (Capability<?>) capField.get(null);

            JJK_PlayerFame                       = f(varsClass, "PlayerFame");
            JJK_PlayerExperience                 = f(varsClass, "PlayerExperience");
            JJK_PlayerLevel                      = f(varsClass, "PlayerLevel");
            JJK_PlayerCursePower                 = f(varsClass, "PlayerCursePower");
            JJK_PlayerCursePowerMAX              = f(varsClass, "PlayerCursePowerMAX");
            JJK_PlayerCursePowerFormer           = f(varsClass, "PlayerCursePowerFormer");
            JJK_PlayerCursePowerChange           = f(varsClass, "PlayerCursePowerChange");
            JJK_PlayerCharge                     = f(varsClass, "PlayerCharge");
            JJK_PlayerCurseTechnique             = f(varsClass, "PlayerCurseTechnique");
            JJK_PlayerCurseTechnique2            = f(varsClass, "PlayerCurseTechnique2");
            JJK_PlayerSelectCurseTechnique       = f(varsClass, "PlayerSelectCurseTechnique");
            JJK_PlayerSelectCurseTechniqueName   = f(varsClass, "PlayerSelectCurseTechniqueName");
            JJK_PlayerSelectCurseTechniqueCost   = f(varsClass, "PlayerSelectCurseTechniqueCost");
            JJK_PlayerSelectCurseTechniqueCostOrgin = f(varsClass, "PlayerSelectCurseTechniqueCostOrgin");
            JJK_PlayerProfession                 = f(varsClass, "PlayerProfession");
            JJK_PlayerTechniqueUsedNumber        = f(varsClass, "PlayerTechniqueUsedNumber");
            JJK_PlayerFlag_A                     = f(varsClass, "PlayerFlag_A");
            JJK_PlayerFlag_B                     = f(varsClass, "PlayerFlag_B");
            JJK_FlagSixEyes                      = f(varsClass, "FlagSixEyes");
            JJK_PhysicalAttack                   = f(varsClass, "PhysicalAttack");
            JJK_PassiveTechnique                 = f(varsClass, "PassiveTechnique");
            JJK_SecondTechnique                  = f(varsClass, "SecondTechnique");
            JJK_noChangeTechnique                = f(varsClass, "noChangeTechnique");
            JJK_flag_sukuna                      = f(varsClass, "flag_sukuna");
            JJK_flag_shift                       = f(varsClass, "flag_shift");
            JJK_cnt_curse1                       = f(varsClass, "cnt_curse1");
            JJK_friend_num_keep                  = f(varsClass, "friend_num_keep");
            JJK_use_mainSkill                    = f(varsClass, "use_mainSkill");

            JJK_SYNC = varsClass.getDeclaredMethod("syncPlayerVariables",
                net.minecraft.world.entity.Entity.class);
            JJK_SYNC.setAccessible(true);

            jjkOk = true;
            TeamBridge.LOG.info("[TeamBridge] JJK hook OK");
        } catch (Exception e) {
            TeamBridge.LOG.warn("[TeamBridge] JJK hook failed: {}", e.getMessage());
        }
        return jjkOk;
    }

    public static boolean initKNY() {
        if (knyTried) return knyOk;
        knyTried = true;
        try {
            Class<?> varsClass = Class.forName(
                "net.mcreator.kimetsunoyaiba.network.KimetsunoyaibaModVariables$PlayerVariables");
            Class<?> modClass  = Class.forName(
                "net.mcreator.kimetsunoyaiba.network.KimetsunoyaibaModVariables");

            Field capField = tryField(modClass, "PLAYER_VARIABLES", "CAP", "CAPABILITY",
                "PLAYER_CAP", "playerVariablesCap");
            if (capField != null) KNY_CAP = (Capability<?>) capField.get(null);

            KNY_PlayerLevel            = f(varsClass, "PlayerLevel");
            KNY_KILL_POINT_1           = f(varsClass, "KILL_POINT_1");
            KNY_KILL_POINT_2           = f(varsClass, "KILL_POINT_2");
            KNY_kill_hashira           = f(varsClass, "kill_hashira");
            KNY_player_usedBreathingNum = f(varsClass, "player_usedBreathingNum");
            KNY_NUM_SKILL              = f(varsClass, "NUM_SKILL");
            KNY_playerBack             = f(varsClass, "playerBack");
            KNY_player_receiveDamage   = f(varsClass, "player_receiveDamage");
            KNY_player_nichirincolor   = f(varsClass, "player_nichirincolor");

            KNY_SYNC = varsClass.getDeclaredMethod("syncPlayerVariables",
                net.minecraft.world.entity.Entity.class);
            KNY_SYNC.setAccessible(true);

            knyOk = true;
            TeamBridge.LOG.info("[TeamBridge] KNY hook OK");
        } catch (Exception e) {
            TeamBridge.LOG.warn("[TeamBridge] KNY hook failed: {}", e.getMessage());
        }
        return knyOk;
    }

    // ── JJK operations ────────────────────────────────────────────────────────

    /** Zero out all JJK progression so a DS player can't earn JJK progress. */
    @SuppressWarnings("unchecked")
    public static void wipeJJKProgress(ServerPlayer player) {
        if (!initJJK() || JJK_CAP == null) return;
        player.getCapability((Capability<Object>) JJK_CAP).ifPresent(vars -> {
            try {
                setDouble(vars, JJK_PlayerFame,                 0.0);
                setDouble(vars, JJK_PlayerExperience,           0.0);
                setDouble(vars, JJK_PlayerLevel,                0.0);
                setDouble(vars, JJK_PlayerCursePower,           0.0);
                setDouble(vars, JJK_PlayerCursePowerMAX,        0.0);
                setDouble(vars, JJK_PlayerCursePowerFormer,     0.0);
                setDouble(vars, JJK_PlayerCursePowerChange,     0.0);
                setDouble(vars, JJK_PlayerCharge,               0.0);
                setDouble(vars, JJK_PlayerCurseTechnique,       0.0);
                setDouble(vars, JJK_PlayerCurseTechnique2,      0.0);
                setDouble(vars, JJK_PlayerSelectCurseTechnique, 0.0);
                setDouble(vars, JJK_PlayerTechniqueUsedNumber,  0.0);
                setDouble(vars, JJK_PlayerFlag_A,               0.0);
                setDouble(vars, JJK_PlayerFlag_B,               0.0);
                setDouble(vars, JJK_FlagSixEyes,                0.0);
                setDouble(vars, JJK_PhysicalAttack,             0.0);
                setDouble(vars, JJK_PassiveTechnique,           0.0);
                setDouble(vars, JJK_SecondTechnique,            0.0);
                setDouble(vars, JJK_noChangeTechnique,          0.0);
                setDouble(vars, JJK_flag_sukuna,                0.0);
                setDouble(vars, JJK_flag_shift,                 0.0);
                setDouble(vars, JJK_cnt_curse1,                 0.0);
                setDouble(vars, JJK_friend_num_keep,            0.0);
                setDouble(vars, JJK_use_mainSkill,              0.0);
                // Profession 0 = no sorcerer rank
                setDouble(vars, JJK_PlayerProfession,           0.0);
                if (JJK_PlayerSelectCurseTechniqueName != null)
                    JJK_PlayerSelectCurseTechniqueName.set(vars, "");
                JJK_SYNC.invoke(vars, player);
            } catch (Exception e) {
                TeamBridge.LOG.debug("[TeamBridge] wipeJJK error: {}", e.getMessage());
            }
        });
        // Also wipe the persistent data NBT that jjcommands uses
        wipeJJCNBT(player);
    }

    /** Zero only the progression fields each tick (not charge, not CE) for DS players. */
    @SuppressWarnings("unchecked")
    public static void lockJJKProgress(ServerPlayer player) {
        if (!initJJK() || JJK_CAP == null) return;
        player.getCapability((Capability<Object>) JJK_CAP).ifPresent(vars -> {
            try {
                boolean dirty = false;
                dirty |= forceDouble(vars, JJK_PlayerFame,        0.0);
                dirty |= forceDouble(vars, JJK_PlayerExperience,  0.0);
                dirty |= forceDouble(vars, JJK_PlayerLevel,       0.0);
                dirty |= forceDouble(vars, JJK_PlayerCursePower,  0.0);
                dirty |= forceDouble(vars, JJK_PlayerCursePowerMAX, 0.0);
                dirty |= forceDouble(vars, JJK_PlayerCursePowerFormer, 0.0);
                dirty |= forceDouble(vars, JJK_flag_sukuna,       0.0);
                dirty |= forceDouble(vars, JJK_FlagSixEyes,       0.0);
                if (dirty) JJK_SYNC.invoke(vars, player);
            } catch (Exception ignored) {}
        });
        // Also lock jjcommands NBT fields
        CompoundTag pd = player.getPersistentData();
        if (pd.getDouble("PlayerFame") != 0)          { pd.putDouble("PlayerFame", 0); }
        if (pd.getDouble("PlayerExperience") != 0)    { pd.putDouble("PlayerExperience", 0); }
    }

    // ── KNY operations ────────────────────────────────────────────────────────

    /** Zero all KNY progression for a JJK player. */
    @SuppressWarnings("unchecked")
    public static void wipeKNYProgress(ServerPlayer player) {
        if (!initKNY() || KNY_CAP == null) return;
        player.getCapability((Capability<Object>) KNY_CAP).ifPresent(vars -> {
            try {
                setDouble(vars, KNY_PlayerLevel,             0.0);
                setDouble(vars, KNY_KILL_POINT_1,            0.0);
                setDouble(vars, KNY_KILL_POINT_2,            0.0);
                setDouble(vars, KNY_kill_hashira,            0.0);
                setDouble(vars, KNY_player_usedBreathingNum, 0.0);
                setDouble(vars, KNY_NUM_SKILL,               0.0);
                setDouble(vars, KNY_playerBack,              0.0);
                setDouble(vars, KNY_player_receiveDamage,    0.0);
                setDouble(vars, KNY_player_nichirincolor,    0.0);
                KNY_SYNC.invoke(vars, player);
            } catch (Exception e) {
                TeamBridge.LOG.debug("[TeamBridge] wipeKNY error: {}", e.getMessage());
            }
        });
    }

    /** Lock KNY progression every tick for JJK players. */
    @SuppressWarnings("unchecked")
    public static void lockKNYProgress(ServerPlayer player) {
        if (!initKNY() || KNY_CAP == null) return;
        player.getCapability((Capability<Object>) KNY_CAP).ifPresent(vars -> {
            try {
                boolean dirty = false;
                dirty |= forceDouble(vars, KNY_PlayerLevel,             0.0);
                dirty |= forceDouble(vars, KNY_KILL_POINT_1,            0.0);
                dirty |= forceDouble(vars, KNY_KILL_POINT_2,            0.0);
                dirty |= forceDouble(vars, KNY_kill_hashira,            0.0);
                dirty |= forceDouble(vars, KNY_player_usedBreathingNum, 0.0);
                dirty |= forceDouble(vars, KNY_NUM_SKILL,               0.0);
                if (dirty) KNY_SYNC.invoke(vars, player);
            } catch (Exception ignored) {}
        });
    }

    // ── jjcommands NBT wipe ───────────────────────────────────────────────────

    private static void wipeJJCNBT(ServerPlayer player) {
        CompoundTag pd = player.getPersistentData();
        // jjcommands StatData keys
        for (String k : new String[]{ "jjc_str","jjc_spe","jjc_hel","jjc_def","jjc_cre",
            "jjc_points","jjc_total","jjc_prestige","jjc_lives",
            "jjc_tech_id","jjc_tech_name","jjc_tech_tier",
            // JJK mod own keys
            "PlayerFame","PlayerExperience","PlayerLevel","PlayerProfession" }) {
            pd.remove(k);
        }
    }

    // ── Reflection helpers ────────────────────────────────────────────────────

    private static Field f(Class<?> cls, String name) {
        try {
            Field field = cls.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            TeamBridge.LOG.debug("[TeamBridge] Field not found: {}.{}", cls.getSimpleName(), name);
            return null;
        }
    }

    private static Field tryField(Class<?> cls, String... names) {
        for (String name : names) {
            Field f = f(cls, name);
            if (f != null) return f;
        }
        return null;
    }

    private static void setDouble(Object obj, Field field, double value) throws Exception {
        if (field == null) return;
        field.setDouble(obj, value);
    }

    /** Returns true if the value was changed (dirty). */
    private static boolean forceDouble(Object obj, Field field, double value) {
        if (field == null) return false;
        try {
            double cur = field.getDouble(obj);
            if (Math.abs(cur - value) > 0.0001) {
                field.setDouble(obj, value);
                return true;
            }
        } catch (Exception ignored) {}
        return false;
    }
}
