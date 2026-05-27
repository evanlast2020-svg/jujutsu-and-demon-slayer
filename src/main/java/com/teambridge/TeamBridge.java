package com.teambridge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("teambridge")
public class TeamBridge {

    public static final String MODID = "teambridge";
    public static final Logger LOG   = LogManager.getLogger("teambridge");

    public TeamBridge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(TeamCommands.class);
        MinecraftForge.EVENT_BUS.register(TeamEnforcer.class);
        MinecraftForge.EVENT_BUS.register(TeamItemGuard.class);
        modBus.addListener(TeamCommands::onRegisterCommands);
    }
}
