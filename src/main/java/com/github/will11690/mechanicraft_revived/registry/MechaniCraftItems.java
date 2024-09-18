package com.github.will11690.mechanicraft_revived.registry;

import com.github.will11690.mechanicraft_revived.MechaniCraftMain;
import com.github.will11690.mechanicraft_revived.items.armor.MechaniCraftArmorItem;
import com.github.will11690.mechanicraft_revived.items.armor.MechaniCraftArmorMaterials;
import com.github.will11690.mechanicraft_revived.items.tools.MechaniCraftToolTiers;
import net.minecraft.world.item.*;
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
    public static final RegistryObject<Item> StringMesh = ITEMS.register("string_mesh", () -> new Item(new Item.Properties().durability(32)));
    public static final RegistryObject<Item> ReinforcedStringMesh = ITEMS.register("reinforced_string_mesh", () -> new Item(new Item.Properties().durability(64)));
    public static final RegistryObject<Item> IronMesh = ITEMS.register("iron_mesh", () -> new Item(new Item.Properties().durability(128)));
    public static final RegistryObject<Item> ReinforcedIronMesh = ITEMS.register("reinforced_iron_mesh", () -> new Item(new Item.Properties().durability(256)));
    public static final RegistryObject<Item> SteelMesh = ITEMS.register("steel_mesh", () -> new Item(new Item.Properties().durability(512)));
    public static final RegistryObject<Item> ReinforcedSteelMesh = ITEMS.register("reinforced_steel_mesh", () -> new Item(new Item.Properties().durability(1024)));
    public static final RegistryObject<Item> DiamondMesh = ITEMS.register("diamond_mesh", () -> new Item(new Item.Properties().durability(2048)));
    public static final RegistryObject<Item> ReinforcedDiamondMesh = ITEMS.register("reinforced_diamond_mesh", () -> new Item(new Item.Properties().durability(4096)));
    public static final RegistryObject<Item> GemMesh = ITEMS.register("gem_mesh", () -> new Item(new Item.Properties().durability(8192).fireResistant()));
    public static final RegistryObject<Item> ReinforcedGemMesh = ITEMS.register("reinforced_gem_mesh", () -> new Item(new Item.Properties().durability(16384).fireResistant()));
    public static final RegistryObject<Item> EndoniumMesh = ITEMS.register("endonium_mesh", () -> new Item(new Item.Properties().durability(32768).fireResistant()));
    public static final RegistryObject<Item> ReinforcedEndoniumMesh = ITEMS.register("reinforced_endonium_mesh", () -> new Item(new Item.Properties().durability(65536).fireResistant()));

    //Tool
    //TODO modifiers/effects? i.e end crystal 3x3 mining?
    public static final RegistryObject<Item> EmeroniumAxe = ITEMS.register("emeronium_axe",
            () -> new AxeItem(MechaniCraftToolTiers.EMERONIUM, 1, 1, new Item.Properties()));
    public static final RegistryObject<Item> EmeroniumHoe = ITEMS.register("emeronium_hoe",
            () -> new HoeItem(MechaniCraftToolTiers.EMERONIUM, 1, 1, new Item.Properties()));
    public static final RegistryObject<Item> EmeroniumPickaxe = ITEMS.register("emeronium_pickaxe",
            () -> new PickaxeItem(MechaniCraftToolTiers.EMERONIUM, 1, 1, new Item.Properties()));
    public static final RegistryObject<Item> EmeroniumShovel = ITEMS.register("emeronium_shovel",
            () -> new ShovelItem(MechaniCraftToolTiers.EMERONIUM, 1, 1, new Item.Properties()));
    public static final RegistryObject<Item> EmeroniumSword = ITEMS.register("emeronium_sword",
            () -> new SwordItem(MechaniCraftToolTiers.EMERONIUM, 1, 1, new Item.Properties()));

    public static final RegistryObject<Item> EndoniumAxe = ITEMS.register("endonium_axe",
            () -> new AxeItem(MechaniCraftToolTiers.ENDONIUM, 1, 1, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> EndoniumHoe = ITEMS.register("endonium_hoe",
            () -> new HoeItem(MechaniCraftToolTiers.ENDONIUM, 1, 1, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> EndoniumPickaxe = ITEMS.register("endonium_pickaxe",
            () -> new PickaxeItem(MechaniCraftToolTiers.ENDONIUM, 1, 1, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> EndoniumShovel = ITEMS.register("endonium_shovel",
            () -> new ShovelItem(MechaniCraftToolTiers.ENDONIUM, 1, 1, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> EndoniumSword = ITEMS.register("endonium_sword",
            () -> new SwordItem(MechaniCraftToolTiers.ENDONIUM, 1, 1, new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> EndoniumCrystalAxe = ITEMS.register("endonium_crystal_axe",
            () -> new AxeItem(MechaniCraftToolTiers.ENDONIUM_CRYSTAL, 1, 1, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> EndoniumCrystalHoe = ITEMS.register("endonium_crystal_hoe",
            () -> new HoeItem(MechaniCraftToolTiers.ENDONIUM_CRYSTAL, 1, 1, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> EndoniumCrystalPickaxe = ITEMS.register("endonium_crystal_pickaxe",
            () -> new PickaxeItem(MechaniCraftToolTiers.ENDONIUM_CRYSTAL, 1, 1, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> EndoniumCrystalShovel = ITEMS.register("endonium_crystal_shovel",
            () -> new ShovelItem(MechaniCraftToolTiers.ENDONIUM_CRYSTAL, 1, 1, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> EndoniumCrystalSword = ITEMS.register("endonium_crystal_sword",
            () -> new SwordItem(MechaniCraftToolTiers.ENDONIUM_CRYSTAL, 1, 1, new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> GlassAxe = ITEMS.register("glass_axe",
            () -> new AxeItem(MechaniCraftToolTiers.GLASS, 1, 1, new Item.Properties()));
    public static final RegistryObject<Item> GlassHoe = ITEMS.register("glass_hoe",
            () -> new HoeItem(MechaniCraftToolTiers.GLASS, 1, 1, new Item.Properties()));
    public static final RegistryObject<Item> GlassPickaxe = ITEMS.register("glass_pickaxe",
            () -> new PickaxeItem(MechaniCraftToolTiers.GLASS, 1, 1, new Item.Properties()));
    public static final RegistryObject<Item> GlassShovel = ITEMS.register("glass_shovel",
            () -> new ShovelItem(MechaniCraftToolTiers.GLASS, 1, 1, new Item.Properties()));
    public static final RegistryObject<Item> GlassSword = ITEMS.register("glass_sword",
            () -> new SwordItem(MechaniCraftToolTiers.GLASS, 1, 1, new Item.Properties()));

    public static final RegistryObject<Item> ObsidiumAxe = ITEMS.register("obsidium_axe",
            () -> new AxeItem(MechaniCraftToolTiers.OBSIDIUM, 1, 1, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> ObsidiumHoe = ITEMS.register("obsidium_hoe",
            () -> new HoeItem(MechaniCraftToolTiers.OBSIDIUM, 1, 1, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> ObsidiumPickaxe = ITEMS.register("obsidium_pickaxe",
            () -> new PickaxeItem(MechaniCraftToolTiers.OBSIDIUM, 1, 1, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> ObsidiumShovel = ITEMS.register("obsidium_shovel",
            () -> new ShovelItem(MechaniCraftToolTiers.OBSIDIUM, 1, 1, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> ObsidiumSword = ITEMS.register("obsidium_sword",
            () -> new SwordItem(MechaniCraftToolTiers.OBSIDIUM, 1, 1, new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> RuboniumAxe = ITEMS.register("rubonium_axe",
            () -> new AxeItem(MechaniCraftToolTiers.RUBONIUM, 1, 1, new Item.Properties()));
    public static final RegistryObject<Item> RuboniumHoe = ITEMS.register("rubonium_hoe",
            () -> new HoeItem(MechaniCraftToolTiers.RUBONIUM, 1, 1, new Item.Properties()));
    public static final RegistryObject<Item> RuboniumPickaxe = ITEMS.register("rubonium_pickaxe",
            () -> new PickaxeItem(MechaniCraftToolTiers.RUBONIUM, 1, 1, new Item.Properties()));
    public static final RegistryObject<Item> RuboniumShovel = ITEMS.register("rubonium_shovel",
            () -> new ShovelItem(MechaniCraftToolTiers.RUBONIUM, 1, 1, new Item.Properties()));
    public static final RegistryObject<Item> RuboniumSword = ITEMS.register("rubonium_sword",
            () -> new SwordItem(MechaniCraftToolTiers.RUBONIUM, 1, 1, new Item.Properties()));

    public static final RegistryObject<Item> SaphoniumAxe = ITEMS.register("saphonium_axe",
            () -> new AxeItem(MechaniCraftToolTiers.SAPHONIUM, 1, 1, new Item.Properties()));
    public static final RegistryObject<Item> SaphoniumHoe = ITEMS.register("saphonium_hoe",
            () -> new HoeItem(MechaniCraftToolTiers.SAPHONIUM, 1, 1, new Item.Properties()));
    public static final RegistryObject<Item> SaphoniumPickaxe = ITEMS.register("saphonium_pickaxe",
            () -> new PickaxeItem(MechaniCraftToolTiers.SAPHONIUM, 1, 1, new Item.Properties()));
    public static final RegistryObject<Item> SaphoniumShovel = ITEMS.register("saphonium_shovel",
            () -> new ShovelItem(MechaniCraftToolTiers.SAPHONIUM, 1, 1, new Item.Properties()));
    public static final RegistryObject<Item> SaphoniumSword = ITEMS.register("saphonium_sword",
            () -> new SwordItem(MechaniCraftToolTiers.SAPHONIUM, 1, 1, new Item.Properties()));

    //Armor
    //TODO Armor Effects
    public static final RegistryObject<Item> EmeroniumHelmet = ITEMS.register("emeronium_helmet",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.EMERONIUM, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> EmeroniumChestplate = ITEMS.register("emeronium_chestplate",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.EMERONIUM, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> EmeroniumLeggings = ITEMS.register("emeronium_leggings",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.EMERONIUM, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> EmeroniumBoots = ITEMS.register("emeronium_boots",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.EMERONIUM, ArmorItem.Type.BOOTS, new Item.Properties()));

    public static final RegistryObject<Item> EndoniumHelmet = ITEMS.register("endonium_helmet",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.ENDONIUM, ArmorItem.Type.HELMET, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> EndoniumChestplate = ITEMS.register("endonium_chestplate",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.ENDONIUM, ArmorItem.Type.CHESTPLATE, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> EndoniumLeggings = ITEMS.register("endonium_leggings",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.ENDONIUM, ArmorItem.Type.LEGGINGS, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> EndoniumBoots = ITEMS.register("endonium_boots",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.ENDONIUM, ArmorItem.Type.BOOTS, new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> EndoniumCrystalHelmet = ITEMS.register("endonium_crystal_helmet",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.ENDONIUM_CRYSTAL, ArmorItem.Type.HELMET, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> EndoniumCrystalChestplate = ITEMS.register("endonium_crystal_chestplate",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.ENDONIUM_CRYSTAL, ArmorItem.Type.CHESTPLATE, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> EndoniumCrystalLeggings = ITEMS.register("endonium_crystal_leggings",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.ENDONIUM_CRYSTAL, ArmorItem.Type.LEGGINGS, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> EndoniumCrystalBoots = ITEMS.register("endonium_crystal_boots",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.ENDONIUM_CRYSTAL, ArmorItem.Type.BOOTS, new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> GlassHelmet = ITEMS.register("glass_helmet",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.GLASS, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> GlassChestplate = ITEMS.register("glass_chestplate",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.GLASS, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> GlassLeggings = ITEMS.register("glass_leggings",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.GLASS, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> GlassBoots = ITEMS.register("glass_boots",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.GLASS, ArmorItem.Type.BOOTS, new Item.Properties()));

    public static final RegistryObject<Item> ObsidiumHelmet = ITEMS.register("obsidium_helmet",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.OBSIDIUM, ArmorItem.Type.HELMET, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> ObsidiumChestplate = ITEMS.register("obsidium_chestplate",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.OBSIDIUM, ArmorItem.Type.CHESTPLATE, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> ObsidiumLeggings = ITEMS.register("obsidium_leggings",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.OBSIDIUM, ArmorItem.Type.LEGGINGS, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> ObsidiumBoots = ITEMS.register("obsidium_boots",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.OBSIDIUM, ArmorItem.Type.BOOTS, new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> RuboniumHelmet = ITEMS.register("rubonium_helmet",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.RUBONIUM, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> RuboniumChestplate = ITEMS.register("rubonium_chestplate",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.RUBONIUM, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> RuboniumLeggings = ITEMS.register("rubonium_leggings",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.RUBONIUM, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> RuboniumBoots = ITEMS.register("rubonium_boots",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.RUBONIUM, ArmorItem.Type.BOOTS, new Item.Properties()));

    public static final RegistryObject<Item> SaphoniumHelmet = ITEMS.register("saphonium_helmet",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.SAPHONIUM, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> SaphoniumChestplate = ITEMS.register("saphonium_chestplate",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.SAPHONIUM, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> SaphoniumLeggings = ITEMS.register("saphonium_leggings",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.SAPHONIUM, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> SaphoniumBoots = ITEMS.register("saphonium_boots",
            () -> new MechaniCraftArmorItem(MechaniCraftArmorMaterials.SAPHONIUM, ArmorItem.Type.BOOTS, new Item.Properties()));
    
    public static void registerItems(IEventBus eventBus) {

        ITEMS.register(eventBus);
    }
}