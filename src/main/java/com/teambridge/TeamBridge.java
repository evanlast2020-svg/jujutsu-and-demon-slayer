package com.teambridge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("teambridge")
public class TeamBridge {

    public static final String MODID = "teambridge";
    public static final Logger LOG   = LogManager.getLogger("teambridge");

    public TeamBridge() {
        // ALL three classes only use Forge bus events:
        //   RegisterCommandsEvent  → Forge bus
        //   ServerStartingEvent    → Forge bus
        //   ServerTickEvent        → Forge bus
        //   EntityItemPickupEvent  → Forge bus
        //   PlayerInteractEvent    → Forge bus
        //   LivingEntityUseItemEvent → Forge bus
        // So all go on MinecraftForge.EVENT_BUS only.
        MinecraftForge.EVENT_BUS.register(TeamCommands.class);
        MinecraftForge.EVENT_BUS.register(TeamEnforcer.class);
        MinecraftForge.EVENT_BUS.register(TeamItemGuard.class);
    }
}
