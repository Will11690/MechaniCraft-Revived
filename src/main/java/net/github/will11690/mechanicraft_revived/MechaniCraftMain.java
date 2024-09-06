package net.github.will11690.mechanicraft_revived;

import com.mojang.logging.LogUtils;
import net.github.will11690.mechanicraft_revived.registry.MechaniCraftBlocks;
import net.github.will11690.mechanicraft_revived.registry.MechaniCraftItems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
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
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MechaniCraftMain.MODID)
public class MechaniCraftMain {

    //TODO List
    //Void storages(large, upgradable, tiered, single item storage blocks[lockable?])
    //Machining Table(5x5 powered crafting table for tier 2-4 machines)
    //Ultimate Machining Table(7x7 powered crafting table for tier 5-6 machines)
    //Ultimate Smiting Table(For Endonium Crystal tier tools, armor, gears, and ingots) AUTOMATE-ABLE!!!
    //Chunkloader block(configurable range? chickenbones loader replacement)

    // Define mod id in a common place for everything to reference
    public static final String MODID = "mechanicraft_revived";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public MechaniCraftMain() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        //Register my items
        MechaniCraftItems.registerItems(modEventBus);
        MechaniCraftBlocks.registerBlocks(modEventBus);

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

            event.accept(MechaniCraftItems.RawLead);
            event.accept(MechaniCraftItems.RawSilver);
            event.accept(MechaniCraftItems.RawTin);

            event.accept(MechaniCraftItems.AuFeDust);
            event.accept(MechaniCraftItems.BronzeDust);
            event.accept(MechaniCraftItems.CopperDust);
            event.accept(MechaniCraftItems.DiamoniumCrystalDust);
            event.accept(MechaniCraftItems.DiamondDust);
            event.accept(MechaniCraftItems.EmeraldDust);
            event.accept(MechaniCraftItems.EmeroniumDust);
            event.accept(MechaniCraftItems.EnderDust);
            event.accept(MechaniCraftItems.EndoniumCrystalDust);
            event.accept(MechaniCraftItems.EndoniumDust);
            event.accept(MechaniCraftItems.GoldDust);
            event.accept(MechaniCraftItems.IronDust);
            event.accept(MechaniCraftItems.LeadDust);
            event.accept(MechaniCraftItems.ObsidianDust);
            event.accept(MechaniCraftItems.ObsidiumDust);
            event.accept(MechaniCraftItems.RuboniumDust);
            event.accept(MechaniCraftItems.RubyDust);
            event.accept(MechaniCraftItems.SaphoniumDust);
            event.accept(MechaniCraftItems.SapphireDust);
            event.accept(MechaniCraftItems.SilverDust);
            event.accept(MechaniCraftItems.SteelDust);
            event.accept(MechaniCraftItems.TinDust);

            event.accept(MechaniCraftItems.CopperDirtyChunks);
            event.accept(MechaniCraftItems.GoldDirtyChunks);
            event.accept(MechaniCraftItems.IronDirtyChunks);
            event.accept(MechaniCraftItems.LeadDirtyChunks);
            event.accept(MechaniCraftItems.SilverDirtyChunks);
            event.accept(MechaniCraftItems.TinDirtyChunks);

            event.accept(MechaniCraftItems.CopperRefinedChunks);
            event.accept(MechaniCraftItems.GoldRefinedChunks);
            event.accept(MechaniCraftItems.IronRefinedChunks);
            event.accept(MechaniCraftItems.LeadRefinedChunks);
            event.accept(MechaniCraftItems.SilverRefinedChunks);
            event.accept(MechaniCraftItems.TinRefinedChunks);

            event.accept(MechaniCraftItems.CopperPureChunks);
            event.accept(MechaniCraftItems.GoldPureChunks);
            event.accept(MechaniCraftItems.IronPureChunks);
            event.accept(MechaniCraftItems.LeadPureChunks);
            event.accept(MechaniCraftItems.SilverPureChunks);
            event.accept(MechaniCraftItems.TinPureChunks);

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

            event.accept(MechaniCraftItems.EndoniumCrystal);
            event.accept(MechaniCraftItems.DiamoniumCrystal);
            event.accept(MechaniCraftItems.RubyGem);
            event.accept(MechaniCraftItems.SapphireGem);

            event.accept(MechaniCraftItems.AuFeNugget);
            event.accept(MechaniCraftItems.BronzeNugget);
            event.accept(MechaniCraftItems.CopperNugget);
            event.accept(MechaniCraftItems.DiamondNugget);
            event.accept(MechaniCraftItems.DiamoniumCrystalNugget);
            event.accept(MechaniCraftItems.EmeraldNugget);
            event.accept(MechaniCraftItems.EmeroniumNugget);
            event.accept(MechaniCraftItems.EnderNugget);
            event.accept(MechaniCraftItems.EndoniumNugget);
            event.accept(MechaniCraftItems.EndoniumCrystalNugget);
            event.accept(MechaniCraftItems.LeadNugget);
            event.accept(MechaniCraftItems.RuboniumNugget);
            event.accept(MechaniCraftItems.RubyNugget);
            event.accept(MechaniCraftItems.SaphoniumNugget);
            event.accept(MechaniCraftItems.SapphireNugget);
            event.accept(MechaniCraftItems.SilverNugget);
            event.accept(MechaniCraftItems.SteelNugget);
            event.accept(MechaniCraftItems.TinNugget);
            event.accept(MechaniCraftItems.ObsidiumNugget);

            event.accept(MechaniCraftItems.PressDieGear);
            event.accept(MechaniCraftItems.PressDiePlate);
            event.accept(MechaniCraftItems.PressDieRod);

            event.accept(MechaniCraftItems.AuFeGear);
            //event.accept(MechaniCraftItems.BronzeGear);
            //event.accept(MechaniCraftItems.CopperGear);
            //event.accept(MechaniCraftItems.DiamondGear);
            event.accept(MechaniCraftItems.DiamoniumGear);
            //event.accept(MechaniCraftItems.EmeraldGear);
            event.accept(MechaniCraftItems.EmeroniumGear);
            event.accept(MechaniCraftItems.EndoniumGear);
            event.accept(MechaniCraftItems.IronGear);
            //event.accept(MechaniCraftItems.LeadGear);
            event.accept(MechaniCraftItems.ObsidiumGear);
            event.accept(MechaniCraftItems.RuboniumGear);
            event.accept(MechaniCraftItems.SaphoniumGear);
            event.accept(MechaniCraftItems.StoneGear);
            //event.accept(MechaniCraftItems.TinGear);
            event.accept(MechaniCraftItems.WoodenGear);

            event.accept(MechaniCraftItems.CapacityUpgrade);
            event.accept(MechaniCraftItems.CreativeUpgrade);
            event.accept(MechaniCraftItems.EfficiencyUpgrade);
            event.accept(MechaniCraftItems.SpeedUpgrade);
            event.accept(MechaniCraftItems.TransferUpgrade);
        }

        if(event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {

            event.accept(MechaniCraftBlocks.TestBlock);
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