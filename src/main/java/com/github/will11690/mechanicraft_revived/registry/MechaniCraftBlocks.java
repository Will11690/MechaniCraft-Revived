package com.github.will11690.mechanicraft_revived.registry;

import com.github.will11690.mechanicraft_revived.MechaniCraftMain;
import com.github.will11690.mechanicraft_revived.blocks.basic.solidfuelgen.BasicSolidFuelGenerator;
import com.github.will11690.mechanicraft_revived.blocks.pipes.energy.basic.BasicEnergyPipe;
import com.github.will11690.mechanicraft_revived.blocks.powered.miningwell.MiningWell;
import com.github.will11690.mechanicraft_revived.blocks.powered.miningwell.miningpipe.MiningPipe;
import com.github.will11690.mechanicraft_revived.blocks.primitive.infuser.PrimitiveInfuser;
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
    public static final RegistryObject<Block> BasicGearBox = registerBlock("basic_gear_box", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> EnhancedGearBox = registerBlock("enhanced_gear_box", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> AdvancedGearBox = registerBlock("advanced_gear_box", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> EliteGearBox = registerBlock("elite_gear_box", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> SuperiorGearBox = registerBlock("superior_gear_box", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> UltimateGearBox = registerBlock("ultimate_gear_box", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));

    /* TODO List (Blocks with custom block bases and/or tiles)
     *
     *  public static final RegistryObject<Block> BasicEnergyCube = registerBlock("basic_energy_cube", () -> new BasicEnergyCube(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> EnhancedEnergyCube = registerBlock("enhanced_energy_cube", () -> new EnhancedEnergyCube(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> AdvancedEnergyCube = registerBlock("advanced_energy_cube", () -> new AdvancedEnergyCube(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> EliteEnergyCube = registerBlock("elite_energy_cube", () -> new EliteEnergyCube(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> SuperiorEnergyCube = registerBlock("superior_energy_cube", () -> new SuperiorEnergyCube(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> UltimateEnergyCube = registerBlock("ultimate_energy_cube", () -> new UltimateEnergyCube(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *
     * 	public static final RegistryObject<Block> BasicCrate = registerBlock("basic_crate", () -> new BasicCrate(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> EnhancedCrate = registerBlock("enhanced_crate", () -> new EnhancedCrate(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> AdvancedCrate = registerBlock("advanced_crate", () -> new AdvancedCrate(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> EliteCrate = registerBlock("elite_crate", () -> new EliteCrate(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> SuperiorCrate = registerBlock("superior_crate", () -> new SuperiorCrate(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> UltimateCrate = registerBlock("ultimate_crate", () -> new UltimateCrate(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> VoidCrate = registerBlock("void_crate", () -> new VoidCrate(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *
     * 	public static final RegistryObject<Block> BasicFluidTank = registerBlock("basic_fluid_tank", () -> new BasicFluidTank(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> EnhancedFluidTank = registerBlock("enhanced_fluid_tank", () -> new EnhancedFluidTank(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> AdvancedFluidTank = registerBlock("advanced_fluid_tank", () -> new AdvancedFluidTank(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> EliteFluidTank = registerBlock("elite_fluid_tank", () -> new EliteFluidTank(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> SuperiorFluidTank = registerBlock("superior_fluid_tank", () -> new SuperiorFluidTank(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> UltimateFluidTank = registerBlock("ultimate_fluid_tank", () -> new UltimateFluidTank(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> VoidFluidTank = registerBlock("void_fluid_tank", () -> new VoidFluidTank(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *
     * 	public static final RegistryObject<Block> BasicCanister = registerBlock("basic_canister", () -> new BasicCanister(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> EnhancedCanister = registerBlock("enhanced_canister", () -> new EnhancedCanister(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> AdvancedCanister = registerBlock("advanced_canister", () -> new AdvancedCanister(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> EliteCanister = registerBlock("elite_canister", () -> new EliteCanister(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> SuperiorCanister = registerBlock("superior_canister", () -> new SuperiorCanister(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> UltimateCanister = registerBlock("ultimate_canister", () -> new UltimateCanister(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     * 	public static final RegistryObject<Block> VoidCanister = registerBlock("void_canister", () -> new VoidCanister(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     */
     public static final RegistryObject<Block> BasicEnergyPipe = registerBlock("basic_energy_pipe", () -> new BasicEnergyPipe(Block.Properties.of().strength(1.0F).noOcclusion()));
     /* public static final RegistryObject<Block> EnhancedEnergyPipe = registerBlock("enhanced_energy_pipe", () -> new EnhancedEnergyPipe(Block.Properties.of().strength(1.0F).noOcclusion()));
     * 	public static final RegistryObject<Block> AdvancedEnergyPipe = registerBlock("advanced_energy_pipe", () -> new AdvancedEnergyPipe(Block.Properties.of().strength(1.0F).noOcclusion()));
     * 	public static final RegistryObject<Block> EliteEnergyPipe = registerBlock("elite_energy_pipe", () -> new EliteEnergyPipe(Block.Properties.of().strength(1.0F).noOcclusion()));
     * 	public static final RegistryObject<Block> SuperiorEnergyPipe = registerBlock("superior_energy_pipe", () -> new SuperiorEnergyPipe(Block.Properties.of().strength(1.0F).noOcclusion()));
     * 	public static final RegistryObject<Block> UltimateEnergyPipe = registerBlock("ultimate_energy_pipe", () -> new UltimateEnergyPipe(Block.Properties.of().strength(1.0F).noOcclusion()));*/

    /* public static final RegistryObject<Block> BasicItemPipe = registerBlock("basic_item_pipe", () -> new BasicItemPipe(Block.Properties.of().strength(1.0F).noOcclusion()));
     *  public static final RegistryObject<Block> EnhancedItemPipe = registerBlock("enhanced_item_pipe", () -> new EnhancedItemPipe(Block.Properties.of().strength(1.0F).noOcclusion()));
     * 	public static final RegistryObject<Block> AdvancedItemPipe = registerBlock("advanced_item_pipe", () -> new AdvancedItemPipe(Block.Properties.of().strength(1.0F).noOcclusion()));
     * 	public static final RegistryObject<Block> EliteItemPipe = registerBlock("elite_item_pipe", () -> new EliteItemPipe(Block.Properties.of().strength(1.0F).noOcclusion()));
     * 	public static final RegistryObject<Block> SuperiorItemPipe = registerBlock("superior_item_pipe", () -> new SuperiorItemPipe(Block.Properties.of().strength(1.0F).noOcclusion()));
     * 	public static final RegistryObject<Block> UltimateItemPipe = registerBlock("ultimate_item_pipe", () -> new UltimateItemPipe(Block.Properties.of().strength(1.0F).noOcclusion()));*/

    /* public static final RegistryObject<Block> BasicFluidPipe = registerBlock("basic_fluid_pipe", () -> new BasicFluidPipe(Block.Properties.of().strength(1.0F).noOcclusion()));
     *  public static final RegistryObject<Block> EnhancedFluidPipe = registerBlock("enhanced_fluid_pipe", () -> new EnhancedFluidPipe(Block.Properties.of().strength(1.0F).noOcclusion()));
     * 	public static final RegistryObject<Block> AdvancedFluidPipe = registerBlock("advanced_fluid_pipe", () -> new AdvancedFluidPipe(Block.Properties.of().strength(1.0F).noOcclusion()));
     * 	public static final RegistryObject<Block> EliteFluidPipe = registerBlock("elite_fluid_pipe", () -> new EliteFluidPipe(Block.Properties.of().strength(1.0F).noOcclusion()));
     * 	public static final RegistryObject<Block> SuperiorFluidPipe = registerBlock("superior_fluid_pipe", () -> new SuperiorFluidPipe(Block.Properties.of().strength(1.0F).noOcclusion()));
     * 	public static final RegistryObject<Block> UltimateFluidPipe = registerBlock("ultimate_fluid_pipe", () -> new UltimateFluidPipe(Block.Properties.of().strength(1.0F).noOcclusion()));*/

    /* public static final RegistryObject<Block> BasicGasPipe = registerBlock("basic_gas_pipe", () -> new BasicGasPipe(Block.Properties.of().strength(1.0F).noOcclusion()));
     *  public static final RegistryObject<Block> EnhancedGasPipe = registerBlock("enhanced_gas_pipe", () -> new EnhancedGasPipe(Block.Properties.of().strength(1.0F).noOcclusion()));
     * 	public static final RegistryObject<Block> AdvancedGasPipe = registerBlock("advanced_gas_pipe", () -> new AdvancedGasPipe(Block.Properties.of().strength(1.0F).noOcclusion()));
     * 	public static final RegistryObject<Block> EliteGasPipe = registerBlock("elite_gas_pipe", () -> new EliteGasPipe(Block.Properties.of().strength(1.0F).noOcclusion()));
     * 	public static final RegistryObject<Block> SuperiorGasPipe = registerBlock("superior_gas_pipe", () -> new SuperiorGasPipe(Block.Properties.of().strength(1.0F).noOcclusion()));
     * 	public static final RegistryObject<Block> UltimateGasPipe = registerBlock("ultimate_gas_pipe", () -> new UltimateGasPipe(Block.Properties.of().strength(1.0F).noOcclusion()));*/

    public static final RegistryObject<Block> PrimitiveInfuser = registerBlock("primitive_metallic_infuser", () -> new PrimitiveInfuser(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));

     /*
     *  public static final RegistryObject<Block> TestBlock = registerBlock("advanced_coal_generator", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("advanced_furnace", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     */
    public static final RegistryObject<Block> BasicSolidFuelGenerator = registerBlock("basic_solid_fuel_generator", () -> new BasicSolidFuelGenerator(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     /*  public static final RegistryObject<Block> TestBlock = registerBlock("basic_smelter", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("basic_ore_washer", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("basic_slurry_processor", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("basic_crusher", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("basic_metallic_infuser", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("basic_powered_sieve", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("basic_press", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *
     *  public static final RegistryObject<Block> TestBlock = registerBlock("enhanced_ore_washer", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("enhanced_slurry_processor", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("enhanced_crusher", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("enhanced_metallic_infuser", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("enhanced_powered_sieve", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("enhanced_press", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *
     *  public static final RegistryObject<Block> TestBlock = registerBlock("advanced_ore_washer", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("advanced_slurry_processor", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("advanced_crusher", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("advanced_metallic_infuser", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("advanced_powered_sieve", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("advanced_press", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *
     *  public static final RegistryObject<Block> TestBlock = registerBlock("elite_ore_washer", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("elite_slurry_processor", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("elite_crusher", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("elite_metallic_infuser", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("elite_powered_sieve", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("elite_press", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *
     *  public static final RegistryObject<Block> TestBlock = registerBlock("superior_ore_washer", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("superior_slurry_processor", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("superior_crusher", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("superior_metallic_infuser", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("superior_powered_sieve", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("superior_press", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *
     *  public static final RegistryObject<Block> TestBlock = registerBlock("ultimate_ore_washer", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("ultimate_slurry_processor", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("ultimate_crusher", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("ultimate_metallic_infuser", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("ultimate_powered_sieve", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     *  public static final RegistryObject<Block> TestBlock = registerBlock("ultimate_press", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
     */

    public static final RegistryObject<Block> MiningWell = registerBlock("mining_well", () -> new MiningWell(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    public static final RegistryObject<Block> MiningPipe = registerBlock("mining_pipe", () -> new MiningPipe(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));


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
