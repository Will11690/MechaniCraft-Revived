package com.github.will11690.mechanicraft_revived.registry;

import com.github.will11690.mechanicraft_revived.MechaniCraftMain;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MechaniCraftItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MechaniCraftMain.MODID);

    //Raw
    public static final RegistryObject<Item> RawLead = ITEMS.register("raw_lead", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RawSilver = ITEMS.register("raw_silver", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RawTin = ITEMS.register("raw_tin", () -> new Item(new Item.Properties()));

    //Dust
    public static final RegistryObject<Item> AuFeDust = ITEMS.register("au_fe_dust", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BronzeDust = ITEMS.register("bronze_dust", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CopperDust = ITEMS.register("copper_dust", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DiamoniumCrystalDust = ITEMS.register("diamonium_crystal_dust", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DiamondDust = ITEMS.register("diamond_dust", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EmeraldDust = ITEMS.register("emerald_dust", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EmeroniumDust = ITEMS.register("emeronium_dust", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EnderDust = ITEMS.register("ender_dust", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EndoniumCrystalDust = ITEMS.register("endonium_crystal_dust", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EndoniumDust = ITEMS.register("endonium_dust", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GoldDust = ITEMS.register("gold_dust", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> IronDust = ITEMS.register("iron_dust", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LeadDust = ITEMS.register("lead_dust", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ObsidianDust = ITEMS.register("obsidian_dust", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ObsidiumDust = ITEMS.register("obsidium_dust", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RuboniumDust = ITEMS.register("rubonium_dust", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RubyDust = ITEMS.register("ruby_dust", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SaphoniumDust = ITEMS.register("saphonium_dust", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SapphireDust = ITEMS.register("sapphire_dust", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SilverDust = ITEMS.register("silver_dust", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SteelDust = ITEMS.register("steel_dust", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TinDust = ITEMS.register("tin_dust", () -> new Item(new Item.Properties()));

    //T1 Ore Chunk(doubling)
    public static final RegistryObject<Item> CopperDirtyChunks = ITEMS.register("copper_dirty_chunks", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GoldDirtyChunks = ITEMS.register("gold_dirty_chunks", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> IronDirtyChunks = ITEMS.register("iron_dirty_chunks", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LeadDirtyChunks = ITEMS.register("lead_dirty_chunks", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SilverDirtyChunks = ITEMS.register("silver_dirty_chunks", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TinDirtyChunks = ITEMS.register("tin_dirty_chunks", () -> new Item(new Item.Properties()));

    //T2 Ore Chunk(tripling)
    public static final RegistryObject<Item> CopperRefinedChunks = ITEMS.register("copper_refined_chunks", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GoldRefinedChunks = ITEMS.register("gold_refined_chunks", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> IronRefinedChunks = ITEMS.register("iron_refined_chunks", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LeadRefinedChunks = ITEMS.register("lead_refined_chunks", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SilverRefinedChunks = ITEMS.register("silver_refined_chunks", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TinRefinedChunks = ITEMS.register("tin_refined_chunks", () -> new Item(new Item.Properties()));

    //T3 Ore Chunk(quadrupling)
    public static final RegistryObject<Item> CopperPureChunks = ITEMS.register("copper_pure_chunks", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GoldPureChunks = ITEMS.register("gold_pure_chunks", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> IronPureChunks = ITEMS.register("iron_pure_chunks", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LeadPureChunks = ITEMS.register("lead_pure_chunks", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SilverPureChunks = ITEMS.register("silver_pure_chunks", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TinPureChunks = ITEMS.register("tin_pure_chunks", () -> new Item(new Item.Properties()));

    //Ingot
    public static final RegistryObject<Item> AuFeIngot = ITEMS.register("au_fe_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BronzeIngot = ITEMS.register("bronze_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EmeroniumIngot = ITEMS.register("emeronium_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EnderIngot = ITEMS.register("ender_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EndoniumIngot = ITEMS.register("endonium_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LeadIngot = ITEMS.register("lead_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RuboniumIngot = ITEMS.register("rubonium_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SaphoniumIngot = ITEMS.register("saphonium_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SilverIngot = ITEMS.register("silver_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SteelIngot = ITEMS.register("steel_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TinIngot = ITEMS.register("tin_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ObsidiumIngot = ITEMS.register("obsidium_ingot", () -> new Item(new Item.Properties()));

    //Gem
    public static final RegistryObject<Item> EndoniumCrystal = ITEMS.register("endonium_crystal", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DiamoniumCrystal = ITEMS.register("diamonium_crystal", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RubyGem = ITEMS.register("ruby_gem", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SapphireGem = ITEMS.register("sapphire_gem", () -> new Item(new Item.Properties()));

    //Nugget
    public static final RegistryObject<Item> AuFeNugget = ITEMS.register("au_fe_nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BronzeNugget = ITEMS.register("bronze_nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CopperNugget = ITEMS.register("copper_nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DiamondNugget = ITEMS.register("diamond_nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DiamoniumCrystalNugget = ITEMS.register("diamonium_crystal_nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EmeraldNugget = ITEMS.register("emerald_nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EmeroniumNugget = ITEMS.register("emeronium_nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EnderNugget = ITEMS.register("ender_nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EndoniumNugget = ITEMS.register("endonium_nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EndoniumCrystalNugget = ITEMS.register("endonium_crystal_nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LeadNugget = ITEMS.register("lead_nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RuboniumNugget = ITEMS.register("rubonium_nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RubyNugget = ITEMS.register("ruby_nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SaphoniumNugget = ITEMS.register("saphonium_nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SapphireNugget = ITEMS.register("sapphire_nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SilverNugget = ITEMS.register("silver_nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SteelNugget = ITEMS.register("steel_nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TinNugget = ITEMS.register("tin_nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ObsidiumNugget = ITEMS.register("obsidium_nugget", () -> new Item(new Item.Properties()));

    //Press Die
    public static final RegistryObject<Item> PressDieGear = ITEMS.register("gear_press_die", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PressDiePlate = ITEMS.register("plate_press_die", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PressDieRod = ITEMS.register("rod_press_die", () -> new Item(new Item.Properties()));

    //Gear
    public static final RegistryObject<Item> AuFeGear = ITEMS.register("au_fe_gear", () -> new Item(new Item.Properties()));
    //Bronze
    //Copper
    //Diamond
    public static final RegistryObject<Item> DiamoniumGear = ITEMS.register("diamonium_gear", () -> new Item(new Item.Properties()));
    //Emerald
    public static final RegistryObject<Item> EmeroniumGear = ITEMS.register("emeronium_gear", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EndoniumGear = ITEMS.register("endonium_gear", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> IronGear = ITEMS.register("iron_gear", () -> new Item(new Item.Properties()));
    //Lead
    public static final RegistryObject<Item> ObsidiumGear = ITEMS.register("obsidium_gear", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RuboniumGear = ITEMS.register("rubonium_gear", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SaphoniumGear = ITEMS.register("saphonium_gear", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> StoneGear = ITEMS.register("stone_gear", () -> new Item(new Item.Properties()));
    //Tin
    public static final RegistryObject<Item> WoodenGear = ITEMS.register("wooden_gear", () -> new Item(new Item.Properties()));

    //Plate
    //TODO
    //public static final RegistryObject<Item> AuFeGear = ITEMS.register("au_fe_gear", () -> new Item(new Item.Properties()));
    //Bronze
    //Copper
    //Diamond
    //public static final RegistryObject<Item> DiamoniumGear = ITEMS.register("diamonium_gear", () -> new Item(new Item.Properties()));
    //Emerald
    //public static final RegistryObject<Item> EmeroniumGear = ITEMS.register("emeronium_gear", () -> new Item(new Item.Properties()));
    //public static final RegistryObject<Item> EndoniumGear = ITEMS.register("endonium_gear", () -> new Item(new Item.Properties()));
    //public static final RegistryObject<Item> IronGear = ITEMS.register("iron_gear", () -> new Item(new Item.Properties()));
    //Lead
    //public static final RegistryObject<Item> ObsidiumGear = ITEMS.register("obsidium_gear", () -> new Item(new Item.Properties()));
    //public static final RegistryObject<Item> RuboniumGear = ITEMS.register("rubonium_gear", () -> new Item(new Item.Properties()));
    //public static final RegistryObject<Item> SaphoniumGear = ITEMS.register("saphonium_gear", () -> new Item(new Item.Properties()));
    //public static final RegistryObject<Item> StoneGear = ITEMS.register("stone_gear", () -> new Item(new Item.Properties()));
    //Tin
    //public static final RegistryObject<Item> WoodenGear = ITEMS.register("wooden_gear", () -> new Item(new Item.Properties()));

    //Rod
    //TODO
    //public static final RegistryObject<Item> AuFeGear = ITEMS.register("au_fe_gear", () -> new Item(new Item.Properties()));
    //Bronze
    //Copper
    //Diamond
    //public static final RegistryObject<Item> DiamoniumGear = ITEMS.register("diamonium_gear", () -> new Item(new Item.Properties()));
    //Emerald
    //public static final RegistryObject<Item> EmeroniumGear = ITEMS.register("emeronium_gear", () -> new Item(new Item.Properties()));
    //public static final RegistryObject<Item> EndoniumGear = ITEMS.register("endonium_gear", () -> new Item(new Item.Properties()));
    //public static final RegistryObject<Item> IronGear = ITEMS.register("iron_gear", () -> new Item(new Item.Properties()));
    //Lead
    //public static final RegistryObject<Item> ObsidiumGear = ITEMS.register("obsidium_gear", () -> new Item(new Item.Properties()));
    //public static final RegistryObject<Item> RuboniumGear = ITEMS.register("rubonium_gear", () -> new Item(new Item.Properties()));
    //public static final RegistryObject<Item> SaphoniumGear = ITEMS.register("saphonium_gear", () -> new Item(new Item.Properties()));
    //public static final RegistryObject<Item> StoneGear = ITEMS.register("stone_gear", () -> new Item(new Item.Properties()));
    //Tin
    //public static final RegistryObject<Item> WoodenGear = ITEMS.register("wooden_gear", () -> new Item(new Item.Properties()));

    //Upgrade
    //TODO
    public static final RegistryObject<Item> CapacityUpgrade = ITEMS.register("capacity_upgrade", () -> new Item(new Item.Properties()));
    //Chunkloader
    public static final RegistryObject<Item> CreativeUpgrade = ITEMS.register("creative_upgrade", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EfficiencyUpgrade = ITEMS.register("efficiency_upgrade", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SpeedUpgrade = ITEMS.register("speed_upgrade", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TransferUpgrade = ITEMS.register("transfer_upgrade", () -> new Item(new Item.Properties()));

    //Mesh(organized by durability not alphabetical)
    //TODO
    //public static final RegistryObject<Item> RawLead = ITEMS.register("string_mesh", () -> new Item(new Item.Properties()));
    //public static final RegistryObject<Item> RawLead = ITEMS.register("reinforced_string_mesh", () -> new Item(new Item.Properties()));
    //public static final RegistryObject<Item> RawLead = ITEMS.register("iron_mesh", () -> new Item(new Item.Properties()));
    //public static final RegistryObject<Item> RawLead = ITEMS.register("reinforced_iron_mesh", () -> new Item(new Item.Properties()));
    //public static final RegistryObject<Item> RawLead = ITEMS.register("steel_mesh", () -> new Item(new Item.Properties()));
    //public static final RegistryObject<Item> RawLead = ITEMS.register("reinforced_steel_mesh", () -> new Item(new Item.Properties()));
    //public static final RegistryObject<Item> RawLead = ITEMS.register("diamond_mesh", () -> new Item(new Item.Properties()));
    //public static final RegistryObject<Item> RawLead = ITEMS.register("reinforced_diamond_mesh", () -> new Item(new Item.Properties()));
    //public static final RegistryObject<Item> RawLead = ITEMS.register("gem_mesh", () -> new Item(new Item.Properties()));
    //public static final RegistryObject<Item> RawLead = ITEMS.register("reinforced_gem_mesh", () -> new Item(new Item.Properties()));
    //public static final RegistryObject<Item> RawLead = ITEMS.register("endonium_mesh", () -> new Item(new Item.Properties()));
    //public static final RegistryObject<Item> RawLead = ITEMS.register("reinforced_endonium_mesh", () -> new Item(new Item.Properties()));

    //Tool
    //TODO

    //Armor
    //TODO
    
    public static void registerItems(IEventBus eventBus) {

        ITEMS.register(eventBus);
    }
}