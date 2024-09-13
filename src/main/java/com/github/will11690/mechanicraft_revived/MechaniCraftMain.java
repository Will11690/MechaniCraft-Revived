package com.github.will11690.mechanicraft_revived;

import com.mojang.logging.LogUtils;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlocks;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftItems;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftTabs;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkDataEvent;
import net.minecraftforge.event.level.PistonEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(MechaniCraftMain.MODID)
public class MechaniCraftMain {

    //TODO List

    //TODO figure out why emeronium tools can mine ender ore but not rub, saph, or obsisium(BUG)

    //DO NEXT - Start re-adding machines

    //Block properties(remove property copies where appropriate and change copies to proper block types)

    //TODO Ideas
    //Void storages(large, upgradable, tiered, single item storage blocks[lockable?])
    //Machining Table(5x5 powered crafting table for tier 2-4 machines)
    //Ultimate Machining Table(7x7 powered crafting table for tier 5-6 machines)
    //Ultimate Smiting Table(For Endonium Crystal tier tools, armor, gears, and ingots) AUTOMATE-ABLE!!!
    //Chunkloader block(configurable range? chickenbones loader replacement)
    //Schematics for machining tables(loot with custom structures?)
    //revive larger chests from 1.7.10??(create a chest network addon with a central controller that is powered)

    public static final String MODID = "mechanicraft_revived";

    private static final Logger LOGGER = LogUtils.getLogger();

    public MechaniCraftMain() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        //Register items
        MechaniCraftItems.registerItems(modEventBus);

        //Register blocks
        MechaniCraftBlocks.registerBlocks(modEventBus);

        //Register creative tabs
        MechaniCraftTabs.register(modEventBus);

        //Register commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        //Register for server and other game events
        MinecraftForge.EVENT_BUS.register(this);

        //Register our ForgeConfigSpec so Forge can create and load config file
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {


    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    @SubscribeEvent
    public void tickEvent(PistonEvent event) {

        //TODO remove when done with tool testing

        if(!event.getLevel().isClientSide()) {

            for (int i = 0; i < 13; i++) {

                System.out.println(TierSortingRegistry.getSortedTiers().get(i).toString());
            }
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}