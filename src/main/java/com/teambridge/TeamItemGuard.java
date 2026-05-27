package com.teambridge;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Blocks team-specific items.
 *
 * DS players: cannot pick up/use JJK items (EXCEPT JJK swords — they can use those)
 *             cannot drink Muzan's blood (blood_of_muzan from KNY)
 *
 * JJK players: cannot pick up/use KNY items
 *              CAN use KNY swords (nichirinsword_*) — they're just swords
 *
 * OP players (permission level 2+) bypass all restrictions.
 *
 * Item IDs confirmed by extracting KimetsunoyaibaModItems and JujutsucraftModItems
 * constant pools from the respective JAR files.
 */
public class TeamItemGuard {

    // ── JJK items blocked for DS players ─────────────────────────────────────
    // Does NOT include jujutsucraft swords — DS players can use those.
    private static final Set<String> JJK_BLOCKED_FOR_DS = new HashSet<>(Arrays.asList(
        // Technique changers (also in jjcommands banned list, but belt-and-suspenders)
        "jujutsucraft:cursed_technique_changer",
        "jujutsucraft:cursed_technique_starter",
        "jujutsucraft:profession_changer",
        "jujutsucraft:copied_cursed_technique",

        // Progression / grade items
        "jujutsucraft:sukuna_finger",
        "jujutsucraft:death_painting",
        "jujutsucraft:demotion_1",
        "jujutsucraft:recommendation_1",
        "jujutsucraft:recommendation_2",

        // Master skill items (JJK-exclusive abilities)
        "jujutsucraft:item_master_domain_amplification",
        "jujutsucraft:item_master_domain_expansion",
        "jujutsucraft:item_master_falling_blossom_emotion",
        "jujutsucraft:item_master_physical_gifted",
        "jujutsucraft:item_master_reverse_cursed_technique",
        "jujutsucraft:item_master_simple_domain",
        "jujutsucraft:item_master_six_eyes",

        // Armors (JJK-universe only)
        "jujutsucraft:uniform_gojo_boots",
        "jujutsucraft:uniform_gojo_chestplate",
        "jujutsucraft:uniform_gojo_helmet",
        "jujutsucraft:uniform_gojo_leggings",
        "jujutsucraft:uniform_itadori_chestplate",
        "jujutsucraft:uniform_itadori_leggings",
        "jujutsucraft:uniform_okkotsu_chestplate",
        "jujutsucraft:uniform_normal_boots",
        "jujutsucraft:uniform_normal_chestplate",
        "jujutsucraft:uniform_normal_helmet",
        "jujutsucraft:uniform_normal_leggings",
        "jujutsucraft:uniform_higuruma_chestplate",
        "jujutsucraft:uniform_higuruma_leggings",
        "jujutsucraft:uniform_q_chestplate",
        "jujutsucraft:uniform_q_helmet",
        "jujutsucraft:uniform_q_leggings",
        "jujutsucraft:uniform_geto_leggings",
        "jujutsucraft:uniform_rengoku_boots",
        "jujutsucraft:uniform_rengoku_chestplate",
        "jujutsucraft:uniform_shinazugawa_chestplate",
        "jujutsucraft:uniform_tanjiro_chestplate",
        "jujutsucraft:uniform_tomioka_chestplate",
        "jujutsucraft:uniform_uzui_boots",
        "jujutsucraft:uniform_uzui_chestplate",
        "jujutsucraft:uniform_uzui_leggings",
        "jujutsucraft:armor_instant_spirit_bodyof_distorted_killing_chestplate",
        "jujutsucraft:armor_instant_spirit_bodyof_distorted_killing_helmet",
        "jujutsucraft:armor_instant_spirit_bodyof_distorted_killing_leggings",
        "jujutsucraft:cursed_spirit_armoury_chestplate",
        "jujutsucraft:insect_armor_chestplate",
        "jujutsucraft:insect_armor_helmet",
        "jujutsucraft:insect_armor_leggings",
        "jujutsucraft:mahoraga_body_chestplate",
        "jujutsucraft:mahoraga_body_helmet",
        "jujutsucraft:mahoraga_body_leggings",
        "jujutsucraft:rika_body_chestplate",
        "jujutsucraft:rika_body_helmet",
        "jujutsucraft:muscular_body_chestplate",
        "jujutsucraft:kurourushi_body_chestplate",
        "jujutsucraft:kurourushi_body_helmet",
        "jujutsucraft:kurourushi_body_leggings",
        "jujutsucraft:naoya_cursed_spirit_body_chestplate",
        "jujutsucraft:sukuna_body_chestplate",
        "jujutsucraft:wing_king_chestplate",
        "jujutsucraft:mahoraga_wheel_helmet",
        "jujutsucraft:mythical_beast_amber_helmet",
        "jujutsucraft:head_jet_helmet",
        "jujutsucraft:head_propeller_helmet",
        "jujutsucraft:blindfold_bandage_helmet",
        "jujutsucraft:sunglasses_helmet",
        "jujutsucraft:hanami_eye_helmet",
        "jujutsucraft:itadori_yuji_paper_helmet",

        // Cursed tools / special weapons (JJK-universe, not generic swords)
        "jujutsucraft:playful_cloud",
        "jujutsucraft:inverted_spear_of_heaven",
        "jujutsucraft:split_soul_katana",
        "jujutsucraft:fester_life_blade",
        "jujutsucraft:blazing_courage",
        "jujutsucraft:black_rope",
        "jujutsucraft:g_staff",
        "jujutsucraft:nyoi_staff",
        "jujutsucraft:daitengu_fan",
        "jujutsucraft:gavel_big",
        "jujutsucraft:gavel_long",
        "jujutsucraft:mei_mei_axe",
        "jujutsucraft:weapon_nanami",
        "jujutsucraft:garuda_item",
        "jujutsucraft:garuda_item_ball",
        "jujutsucraft:supreme_martial_solution",
        "jujutsucraft:debugger_jr",
        "jujutsucraft:pendant_todo_aoi",
        "jujutsucraft:bullet_ball",
        "jujutsucraft:bullet_flame",
        "jujutsucraft:item_bullet",
        "jujutsucraft:item_doll",
        "jujutsucraft:item_insect",
        "jujutsucraft:cursed_spirit_ball",
        "jujutsucraft:dragon_bone",
        "jujutsucraft:human_stock",
        "jujutsucraft:human_stock_car",
        "jujutsucraft:itadori_arm",
        "jujutsucraft:hanami_hand",
        "jujutsucraft:mahito_hand_1",
        "jujutsucraft:mahito_hand_2"

        // NOTE: JJK swords (sword_black, sword_kusakabe, sword_miwa_kasumi,
        // sword_of_extermination, sword_ogi, sword_okkotsu_yuta, sword_option,
        // sword_shigemo, executioners_sword) are NOT in this list —
        // DS players CAN pick up and use JJK swords.
    ));

    // ── KNY items blocked for JJK players ────────────────────────────────────
    // Includes ALL KNY items EXCEPT nichirinsword_* (those are just swords)
    private static final Set<String> KNY_BLOCKED_FOR_JJK = new HashSet<>(Arrays.asList(
        // Muzan's blood — explicitly blocked for JJK players
        "kimetsunoyaiba:blood_of_muzan",

        // Breathing skill items
        "kimetsunoyaiba:demon_slayers_resolve",
        "kimetsunoyaiba:demon_slayer_mark_mist",
        "kimetsunoyaiba:demon_slayer_mark_water",
        "kimetsunoyaiba:demon_slayer_mark_wind",
        "kimetsunoyaiba:item_constant_flux",
        "kimetsunoyaiba:item_direction_arrow",
        "kimetsunoyaiba:item_flame_dragon",
        "kimetsunoyaiba:item_serpent",
        "kimetsunoyaiba:suigokubati_item",

        // Blood demon art items
        "kimetsunoyaiba:blooddemonart_akaza",
        "kimetsunoyaiba:blooddemonart_demon_king",
        "kimetsunoyaiba:blooddemonart_enmu",
        "kimetsunoyaiba:blooddemonart_gyokko",
        "kimetsunoyaiba:blooddemonart_kamanue",
        "kimetsunoyaiba:blooddemonart_nakime",
        "kimetsunoyaiba:blooddemonart_nezuko",
        "kimetsunoyaiba:blooddemonart_nezuko_clear",
        "kimetsunoyaiba:blooddemonart_rokuro",
        "kimetsunoyaiba:blooddemonart_rui",
        "kimetsunoyaiba:blooddemonart_rui_sister",
        "kimetsunoyaiba:blooddemonart_yahaba",
        "kimetsunoyaiba:blooddemonart_zohakuten",
        "kimetsunoyaiba:black_blood_brambles",
        "kimetsunoyaiba:coccon_item",
        "kimetsunoyaiba:muzan_hand",
        "kimetsunoyaiba:demon_hand",
        "kimetsunoyaiba:urogi_hand",
        "kimetsunoyaiba:urogi_wings_chestplate",

        // Medicines / demon consumables
        "kimetsunoyaiba:medicine_aging",
        "kimetsunoyaiba:medicine_cell_destruction",
        "kimetsunoyaiba:medicine_division_inhibition",
        "kimetsunoyaiba:medicine_human_return",

        // KNY armors
        "kimetsunoyaiba:armor_ravaged_war_formation_wolf_chestplate",
        "kimetsunoyaiba:armor_ravaged_war_formation_wolf_helmet",
        "kimetsunoyaiba:armor_ravaged_war_formation_wolf_leggings",
        "kimetsunoyaiba:clothes_akaza_chestplate",
        "kimetsunoyaiba:clothes_akaza_helmet",
        "kimetsunoyaiba:clothes_akaza_leggings",
        "kimetsunoyaiba:clothes_bamboo_boots",
        "kimetsunoyaiba:clothes_bamboo_chestplate",
        "kimetsunoyaiba:clothes_cherry_blossom_chestplate",
        "kimetsunoyaiba:clothes_doma_chestplate",
        "kimetsunoyaiba:clothes_doma_leggings",
        "kimetsunoyaiba:clothes_gyutaro_chestplate",
        "kimetsunoyaiba:clothes_gyutaro_leggings",
        "kimetsunoyaiba:clothes_hand_demon_chestplate",
        "kimetsunoyaiba:clothes_himejima_chestplate",
        "kimetsunoyaiba:clothes_iguro_chestplate",
        "kimetsunoyaiba:clothes_kaigaku_chestplate",
        "kimetsunoyaiba:clothes_kocho_chestplate",
        "kimetsunoyaiba:clothes_kokushibo_chestplate",
        "kimetsunoyaiba:clothes_rengoku_chestplate",
        "kimetsunoyaiba:clothes_rui_chestplate",
        "kimetsunoyaiba:clothes_shinazugawa_chestplate",
        "kimetsunoyaiba:clothes_tanjiro_chestplate",
        "kimetsunoyaiba:clothes_tomioka_chestplate",
        "kimetsunoyaiba:clothes_yorichi_chestplate",
        "kimetsunoyaiba:clothes_yorichi_leggings",
        "kimetsunoyaiba:clothes_zenitsu_chestplate",
        "kimetsunoyaiba:daki_kimono_chestplate",
        "kimetsunoyaiba:drum_armor_chestplate",
        "kimetsunoyaiba:hairo_mant_chestplate",
        "kimetsunoyaiba:kokushibo_tentacles_chestplate",
        "kimetsunoyaiba:muzan_tentacles_chestplate",
        "kimetsunoyaiba:muzan_tentacles_helmet",
        "kimetsunoyaiba:tanjiro_tentacles_chestplate",
        "kimetsunoyaiba:panda_armor_boots",
        "kimetsunoyaiba:panda_armor_chestplate",
        "kimetsunoyaiba:panda_armor_helmet",
        "kimetsunoyaiba:panda_armor_leggings",
        "kimetsunoyaiba:mukimuki_chestplate",
        "kimetsunoyaiba:uniform_boots",
        "kimetsunoyaiba:uniform_chestplate",
        "kimetsunoyaiba:uniform_genya_chestplate",
        "kimetsunoyaiba:uniform_iguro_chestplate",
        "kimetsunoyaiba:uniform_kocho_boots",
        "kimetsunoyaiba:uniform_kocho_chestplate",
        "kimetsunoyaiba:uniform_leggings",
        "kimetsunoyaiba:uniform_muichiro_chestplate",
        "kimetsunoyaiba:uniform_muichiro_helmet",
        "kimetsunoyaiba:uniform_muichiro_leggings",
        "kimetsunoyaiba:uniform_rengoku_boots",
        "kimetsunoyaiba:uniform_rengoku_chestplate",
        "kimetsunoyaiba:uniform_shinazugawa_chestplate",
        "kimetsunoyaiba:uniform_tanjiro_chestplate",
        "kimetsunoyaiba:uniform_tomioka_chestplate",
        "kimetsunoyaiba:uniform_uzui_boots",
        "kimetsunoyaiba:uniform_uzui_chestplate",
        "kimetsunoyaiba:uniform_uzui_leggings",
        "kimetsunoyaiba:uniform_zenitsu_boots",
        "kimetsunoyaiba:uniform_zenitsu_chestplate",
        "kimetsunoyaiba:dice_steak_head_helmet",
        "kimetsunoyaiba:dice_steak_arm",
        "kimetsunoyaiba:dice_steak_body",
        "kimetsunoyaiba:dice_steak_leg",
        "kimetsunoyaiba:dice_steak_senior_item",
        "kimetsunoyaiba:doma_hat",
        "kimetsunoyaiba:gyutaro_head",
        "kimetsunoyaiba:akaza_head",
        "kimetsunoyaiba:muzan_hat_helmet",
        "kimetsunoyaiba:mask_hyottoko_helmet",
        "kimetsunoyaiba:mask_tengu_helmet",
        "kimetsunoyaiba:hair_muichiro_demon_slayer_mark_helmet",
        "kimetsunoyaiba:hair_ornament_cherry_blossom",
        "kimetsunoyaiba:hair_tsugikuni",
        "kimetsunoyaiba:panda_hood_helmet",
        "kimetsunoyaiba:sabito_mask_helmet",
        "kimetsunoyaiba:spider_head_helmet",
        "kimetsunoyaiba:item_sneak_helmet",
        "kimetsunoyaiba:item_sneak_red_helmet",
        "kimetsunoyaiba:zenitsu_head_helmet",
        "kimetsunoyaiba:kaburamaru_helmet",
        "kimetsunoyaiba:uzui_decoration_helmet",
        "kimetsunoyaiba:sword_hairo",
        "kimetsunoyaiba:sword_kokushibo_body",
        "kimetsunoyaiba:bamboo_sword",
        "kimetsunoyaiba:chigama",
        "kimetsunoyaiba:kitchen_knife",
        "kimetsunoyaiba:scarlet_ironsand",
        "kimetsunoyaiba:scarlet_ore",
        "kimetsunoyaiba:scarlet_ore_rare",
        "kimetsunoyaiba:snow_crystal_clear",
        "kimetsunoyaiba:tengu_handfan",
        "kimetsunoyaiba:farewell_note",
        "kimetsunoyaiba:school_bag",
        "kimetsunoyaiba:vase_item"

        // NOTE: nichirinsword_* are NOT in this list —
        // JJK players CAN use nichirin swords (they're just swords).
    ));

    // ── Muzan's blood — blocked for JJK players specifically ─────────────────
    private static final String MUZAN_BLOOD = "kimetsunoyaiba:blood_of_muzan";

    // ─────────────────────────────────────────────────────────────────────────

    private static boolean isBlocked(ServerPlayer player, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (player.hasPermissions(2)) return false;  // OP bypass

        var rl = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (rl == null) return false;
        String id = rl.toString().toLowerCase();

        String team = TeamData.getTeam(player.getUUID());
        if (TeamData.TEAM_NONE.equals(team)) return false;

        if (TeamData.TEAM_DS.equals(team)) {
            // DS player: block JJK-specific items + Muzan blood
            return JJK_BLOCKED_FOR_DS.contains(id) || MUZAN_BLOOD.equals(id);
        } else if (TeamData.TEAM_JJK.equals(team)) {
            // JJK player: block KNY-specific items (nichirinswords are allowed)
            return KNY_BLOCKED_FOR_JJK.contains(id);
        }
        return false;
    }

    private static void deny(ServerPlayer player, ItemStack stack) {
        player.sendSystemMessage(Component.literal(
            "§8[§cTeamBridge§8] §cYou cannot use §e"
            + stack.getDisplayName().getString()
            + "§c — it belongs to the other faction."));
    }

    @SubscribeEvent
    public static void onPickup(EntityItemPickupEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ItemStack stack = event.getItem().getItem();
        if (isBlocked(player, stack)) {
            event.setCanceled(true);
            deny(player, stack);
            event.getItem().discard();
        }
    }

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (isBlocked(player, event.getItemStack())) {
            event.setCanceled(true);
            deny(player, event.getItemStack());
        }
    }

    @SubscribeEvent
    public static void onUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (isBlocked(player, event.getItem())) {
            event.setCanceled(true);
        }
    }

    /** Called when a player switches teams — remove blocked items from inventory. */
    public static void clearBannedItems(ServerPlayer player) {
        String team = TeamData.getTeam(player.getUUID());
        player.getInventory().items.replaceAll(stack -> {
            if (isBlocked(player, stack)) {
                player.sendSystemMessage(Component.literal(
                    "§8[§cTeamBridge§8] §7Removed §e"
                    + stack.getDisplayName().getString()
                    + "§7 (wrong faction item)."));
                return ItemStack.EMPTY;
            }
            return stack;
        });
    }
}
