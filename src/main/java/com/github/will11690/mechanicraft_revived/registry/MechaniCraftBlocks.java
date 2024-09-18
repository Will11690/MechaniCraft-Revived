package com.github.will11690.mechanicraft_revived.registry;

import com.github.will11690.mechanicraft_revived.MechaniCraftMain;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class MechaniCraftBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MechaniCraftMain.MODID);

    //TODO block properties

    public static final RegistryObject<Block> EnderOre = registerBlock("ender_ore", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> LeadOre = registerBlock("lead_ore", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> RubyOre = registerBlock("ruby_ore", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SapphireOre = registerBlock("sapphire_ore", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SilverOre = registerBlock("silver_ore", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> TinOre = registerBlock("tin_ore", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> DeepslateLeadOre = registerBlock("deepslate_lead_ore", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> DeepslateRubyOre = registerBlock("deepslate_ruby_ore", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> DeepslateSapphireOre = registerBlock("deepslate_sapphire_ore", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> DeepslateSilverOre = registerBlock("deepslate_silver_ore", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> DeepslateTinOre = registerBlock("deepslate_tin_ore", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> RawLeadBlock = registerBlock("raw_lead_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> RawSilverBlock = registerBlock("raw_silver_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> RawTinBlock = registerBlock("raw_tin_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));

    public static final RegistryObject<Block> AuFeBlock = registerBlock("au_fe_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> BronzeBlock = registerBlock("bronze_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> EmeroniumBlock = registerBlock("emeronium_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> EnderBlock = registerBlock("ender_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> EnderDustBlock = registerBlock("ender_dust_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> EndoniumBlock = registerBlock("endonium_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> EndoniumCrystalBlock = registerBlock("endonium_crystal_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> LeadBlock = registerBlock("lead_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> ObsidiumBlock = registerBlock("obsidium_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> RuboniumBlock = registerBlock("rubonium_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> RubyBlock = registerBlock("ruby_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> SaphoniumBlock = registerBlock("saphonium_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> SapphireBlock = registerBlock("sapphire_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> SilverBlock = registerBlock("silver_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> SteelBlock = registerBlock("steel_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> TinBlock = registerBlock("tin_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));

    public static final RegistryObject<Block> MachineBlock = registerBlock("machine_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> T1GearBox = registerBlock("t1_gear_box", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> T2GearBox = registerBlock("t2_gear_box", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> T3GearBox = registerBlock("t3_gear_box", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> T4GearBox = registerBlock("t4_gear_box", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> T5GearBox = registerBlock("t5_gear_box", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> T6GearBox = registerBlock("t6_gear_box", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));

    /* TODO List (Blocks with custom block bases and/or tiles)
     *
     * 	public static final RegistryObject<Block> TestBlock = registerBlock("basic_fluid_tank", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> TestBlock = registerBlock("advanced_fluid_tank", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> TestBlock = registerBlock("elite_fluid_tank", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> TestBlock = registerBlock("superior_fluid_tank", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> TestBlock = registerBlock("supreme_fluid_tank", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> TestBlock = registerBlock("ultimate_fluid_tank", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *
     * 	public static final RegistryObject<Block> TestBlock = registerBlock("t1_energy_chute", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> TestBlock = registerBlock("t2_energy_chute", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> TestBlock = registerBlock("t3_energy_chute", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> TestBlock = registerBlock("t4_energy_chute", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> TestBlock = registerBlock("t5_energy_chute", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> TestBlock = registerBlock("t6_energy_chute", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *
     *  public static final RegistryObject<Block> TestBlock = registerBlock("basic_metallic_infuser", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("basic_coal_generator", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("basic_furnace", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *
     *  public static final RegistryObject<Block> TestBlock = registerBlock("advanced_coal_generator", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("advanced_furnace", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t1_ore_washer", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t1_slurry_processor", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t1_crusher", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t1_energy_cube", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t1_metallic_infuser", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t1_powered_sieve", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t1_press", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t2_ore_washer", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t2_slurry_processor", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t2_crusher", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t2_energy_cube", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t2_metallic_infuser", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t2_powered_sieve", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t2_press", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t3_ore_washer", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t3_slurry_processor", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t3_crusher", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t3_energy_cube", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t3_metallic_infuser", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t3_powered_sieve", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t3_press", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t4_ore_washer", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t4_slurry_processor", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t4_crusher", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t4_energy_cube", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t4_metallic_infuser", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t4_powered_sieve", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t4_press", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t5_ore_washer", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t5_slurry_processor", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t5_crusher", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t5_energy_cube", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t5_metallic_infuser", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t5_powered_sieve", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t5_press", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t6_ore_washer", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t6_slurry_processor", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t6_crusher", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t6_energy_cube", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t6_metallic_infuser", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t6_powered_sieve", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("t6_press", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *
     *  public static final RegistryObject<Block> TestBlock = registerBlock("line_miner", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *
     */


    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {

        return MechaniCraftItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {

        RegistryObject<T> register = BLOCKS.register(name, block);
        registerBlockItem(name, register);

        return register;
    }

    public static void registerBlocks(IEventBus eventBus) {

        BLOCKS.register(eventBus);
    }
}
