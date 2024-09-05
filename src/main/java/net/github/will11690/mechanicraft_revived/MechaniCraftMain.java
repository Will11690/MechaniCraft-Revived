package net.github.will11690.mechanicraft_revived;

import com.mojang.logging.LogUtils;
import net.github.will11690.mechanicraft_revived.registry.MechaniCraftItems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
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

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MechaniCraftMain.MODID)
public class MechaniCraftMain {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "mechanicraft_revived";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public MechaniCraftMain() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        //Register my items
        MechaniCraftItems.registerItems(modEventBus);

        //Register my blocks

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {


    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

        //TODO Custom Creative Tabs(placeholder is vanilla tabs)

        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {

            event.accept(MechaniCraftItems.AuFeIngot);
            event.accept(MechaniCraftItems.BronzeIngot);
            event.accept(MechaniCraftItems.EmeroniumIngot);
            event.accept(MechaniCraftItems.EnderIngot);
            event.accept(MechaniCraftItems.EndoniumIngot);
            event.accept(MechaniCraftItems.LeadIngot);
            event.accept(MechaniCraftItems.RuboniumIngot);
            event.accept(MechaniCraftItems.SaphoniumIngot);
            event.accept(MechaniCraftItems.SilverIngot);
            event.accept(MechaniCraftItems.SteelIngot);
            event.accept(MechaniCraftItems.TinIngot);
            event.accept(MechaniCraftItems.ObsidiumIngot);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
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