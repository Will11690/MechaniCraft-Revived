package com.github.will11690.mechanicraft_revived.registry;

import com.github.will11690.mechanicraft_revived.MechaniCraftMain;
import com.github.will11690.mechanicraft_revived.blocks.generators.basic.solidfuelgen.BasicSolidFuelGeneratorBE;
import com.github.will11690.mechanicraft_revived.blocks.machines.advanced.infuser.AdvancedInfuserBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.machines.basic.infuser.BasicInfuserBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.machines.elite.infuser.EliteInfuserBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.machines.enhanced.infuser.EnhancedInfuserBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.machines.superior.infuser.SuperiorInfuserBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.machines.ultimate.infuser.UltimateInfuserBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.storages.basic.energycube.BasicEnergyCubeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.energy.advanced.AdvancedEnergyPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.energy.basic.BasicEnergyPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.energy.elite.EliteEnergyPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.energy.enhanced.EnhancedEnergyPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.energy.superior.SuperiorEnergyPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.energy.ultimate.UltimateEnergyPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.machines.misc.miningwell.MiningWellBE;
import com.github.will11690.mechanicraft_revived.blocks.machines.primitive.infuser.PrimitiveInfuserBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.fluid.advanced.AdvancedFluidPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.fluid.basic.BasicFluidPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.fluid.elite.EliteFluidPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.fluid.enhanced.EnhancedFluidPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.fluid.superior.SuperiorFluidPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.fluid.ultimate.UltimateFluidPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.item.advanced.AdvancedItemPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.item.basic.BasicItemPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.item.elite.EliteItemPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.item.enhanced.EnhancedItemPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.item.superior.SuperiorItemPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.item.ultimate.UltimateItemPipeBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlocks.*;

public class MechaniCraftBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MechaniCraftMain.MODID);

    public static final RegistryObject<BlockEntityType<PrimitiveInfuserBlockEntity>> PrimitiveInfuserBE = BLOCK_ENTITIES.register
            ("primitive_metallic_infuser_be", () -> BlockEntityType.Builder.of(PrimitiveInfuserBlockEntity::new, MechaniCraftBlocks.PrimitiveInfuser.get()).build(null));


    //STORAGES
    public static final RegistryObject<BlockEntityType<BasicEnergyCubeBlockEntity>> BasicEnergyCubeBE = BLOCK_ENTITIES.register
            ("basic_energy_cube", () -> BlockEntityType.Builder.of(BasicEnergyCubeBlockEntity::new, MechaniCraftBlocks.BasicEnergyCube.get()).build(null));

    //MACHINES
    public static final RegistryObject<BlockEntityType<BasicInfuserBlockEntity>> BasicInfuserBE = BLOCK_ENTITIES.register
            ("basic_metallic_infuser_be", () -> BlockEntityType.Builder.of(BasicInfuserBlockEntity::new, MechaniCraftBlocks.BasicInfuser.get()).build(null));

    public static final RegistryObject<BlockEntityType<EnhancedInfuserBlockEntity>> EnhancedInfuserBE = BLOCK_ENTITIES.register
            ("enhanced_metallic_infuser_be", () -> BlockEntityType.Builder.of(EnhancedInfuserBlockEntity::new, MechaniCraftBlocks.EnhancedInfuser.get()).build(null));

    public static final RegistryObject<BlockEntityType<AdvancedInfuserBlockEntity>> AdvancedInfuserBE = BLOCK_ENTITIES.register
            ("advanced_metallic_infuser_be", () -> BlockEntityType.Builder.of(AdvancedInfuserBlockEntity::new, MechaniCraftBlocks.AdvancedInfuser.get()).build(null));

    public static final RegistryObject<BlockEntityType<EliteInfuserBlockEntity>> EliteInfuserBE = BLOCK_ENTITIES.register
            ("elite_metallic_infuser_be", () -> BlockEntityType.Builder.of(EliteInfuserBlockEntity::new, MechaniCraftBlocks.EliteInfuser.get()).build(null));

    public static final RegistryObject<BlockEntityType<SuperiorInfuserBlockEntity>> SuperiorInfuserBE = BLOCK_ENTITIES.register
            ("superior_metallic_infuser_be", () -> BlockEntityType.Builder.of(SuperiorInfuserBlockEntity::new, MechaniCraftBlocks.SuperiorInfuser.get()).build(null));

    public static final RegistryObject<BlockEntityType<UltimateInfuserBlockEntity>> UltimateInfuserBE = BLOCK_ENTITIES.register
            ("ultimate_metallic_infuser_be", () -> BlockEntityType.Builder.of(UltimateInfuserBlockEntity::new, MechaniCraftBlocks.UltimateInfuser.get()).build(null));

    public static final RegistryObject<BlockEntityType<MiningWellBE>> MiningWellBE = BLOCK_ENTITIES.register
            ("mining_well_be", () -> BlockEntityType.Builder.of(MiningWellBE::new, MechaniCraftBlocks.MiningWell.get()).build(null));

    public static final RegistryObject<BlockEntityType<BasicSolidFuelGeneratorBE>> BasicSolidFuelGeneratorBE = BLOCK_ENTITIES.register
            ("basic_solid_fuel_generator_be", () -> BlockEntityType.Builder.of(BasicSolidFuelGeneratorBE::new, MechaniCraftBlocks.BasicSolidFuelGenerator.get()).build(null));

    //ENERGY PIPES
    public static final RegistryObject<BlockEntityType<BasicEnergyPipeBlockEntity>> BasicEnergyPipeBE = BLOCK_ENTITIES.register
            ("basic_energy_pipe", () -> BlockEntityType.Builder.of(BasicEnergyPipeBlockEntity::new, BasicEnergyPipe.get()).build(null));

    public static final RegistryObject<BlockEntityType<EnhancedEnergyPipeBlockEntity>> EnhancedEnergyPipeBE = BLOCK_ENTITIES.register
            ("enhanced_energy_pipe", () -> BlockEntityType.Builder.of(EnhancedEnergyPipeBlockEntity::new, EnhancedEnergyPipe.get()).build(null));

    public static final RegistryObject<BlockEntityType<AdvancedEnergyPipeBlockEntity>> AdvancedEnergyPipeBE = BLOCK_ENTITIES.register
            ("advanced_energy_pipe", () -> BlockEntityType.Builder.of(AdvancedEnergyPipeBlockEntity::new, AdvancedEnergyPipe.get()).build(null));

    public static final RegistryObject<BlockEntityType<EliteEnergyPipeBlockEntity>> EliteEnergyPipeBE = BLOCK_ENTITIES.register
            ("elite_energy_pipe", () -> BlockEntityType.Builder.of(EliteEnergyPipeBlockEntity::new, EliteEnergyPipe.get()).build(null));

    public static final RegistryObject<BlockEntityType<SuperiorEnergyPipeBlockEntity>> SuperiorEnergyPipeBE = BLOCK_ENTITIES.register
            ("superior_energy_pipe", () -> BlockEntityType.Builder.of(SuperiorEnergyPipeBlockEntity::new, SuperiorEnergyPipe.get()).build(null));

    public static final RegistryObject<BlockEntityType<UltimateEnergyPipeBlockEntity>> UltimateEnergyPipeBE = BLOCK_ENTITIES.register
            ("ultimate_energy_pipe", () -> BlockEntityType.Builder.of(UltimateEnergyPipeBlockEntity::new, UltimateEnergyPipe.get()).build(null));

    //ITEM PIPES
    public static final RegistryObject<BlockEntityType<BasicItemPipeBlockEntity>> BasicItemPipeBE = BLOCK_ENTITIES.register
            ("basic_item_pipe", () -> BlockEntityType.Builder.of(BasicItemPipeBlockEntity::new, BasicItemPipe.get()).build(null));
    public static final RegistryObject<BlockEntityType<EnhancedItemPipeBlockEntity>> EnhancedItemPipeBE = BLOCK_ENTITIES.register
            ("enhanced_item_pipe", () -> BlockEntityType.Builder.of(EnhancedItemPipeBlockEntity::new, EnhancedItemPipe.get()).build(null));
    public static final RegistryObject<BlockEntityType<AdvancedItemPipeBlockEntity>> AdvancedItemPipeBE = BLOCK_ENTITIES.register
            ("advanced_item_pipe", () -> BlockEntityType.Builder.of(AdvancedItemPipeBlockEntity::new, AdvancedItemPipe.get()).build(null));
    public static final RegistryObject<BlockEntityType<EliteItemPipeBlockEntity>> EliteItemPipeBE = BLOCK_ENTITIES.register
            ("elite_item_pipe", () -> BlockEntityType.Builder.of(EliteItemPipeBlockEntity::new, EliteItemPipe.get()).build(null));
    public static final RegistryObject<BlockEntityType<SuperiorItemPipeBlockEntity>> SuperiorItemPipeBE = BLOCK_ENTITIES.register
            ("superior_item_pipe", () -> BlockEntityType.Builder.of(SuperiorItemPipeBlockEntity::new, SuperiorItemPipe.get()).build(null));
    public static final RegistryObject<BlockEntityType<UltimateItemPipeBlockEntity>> UltimateItemPipeBE = BLOCK_ENTITIES.register
            ("ultimate_item_pipe", () -> BlockEntityType.Builder.of(UltimateItemPipeBlockEntity::new, UltimateItemPipe.get()).build(null));

    //FLUID PIPES
    public static final RegistryObject<BlockEntityType<BasicFluidPipeBlockEntity>> BasicFluidPipeBE = BLOCK_ENTITIES.register
            ("basic_fluid_pipe", () -> BlockEntityType.Builder.of(BasicFluidPipeBlockEntity::new, BasicFluidPipe.get()).build(null));
    public static final RegistryObject<BlockEntityType<EnhancedFluidPipeBlockEntity>> EnhancedFluidPipeBE = BLOCK_ENTITIES.register
            ("enhanced_fluid_pipe", () -> BlockEntityType.Builder.of(EnhancedFluidPipeBlockEntity::new, EnhancedFluidPipe.get()).build(null));
    public static final RegistryObject<BlockEntityType<AdvancedFluidPipeBlockEntity>> AdvancedFluidPipeBE = BLOCK_ENTITIES.register
            ("advanced_fluid_pipe", () -> BlockEntityType.Builder.of(AdvancedFluidPipeBlockEntity::new, AdvancedFluidPipe.get()).build(null));
    public static final RegistryObject<BlockEntityType<EliteFluidPipeBlockEntity>> EliteFluidPipeBE = BLOCK_ENTITIES.register
            ("elite_fluid_pipe", () -> BlockEntityType.Builder.of(EliteFluidPipeBlockEntity::new, EliteFluidPipe.get()).build(null));
    public static final RegistryObject<BlockEntityType<SuperiorFluidPipeBlockEntity>> SuperiorFluidPipeBE = BLOCK_ENTITIES.register
            ("superior_fluid_pipe", () -> BlockEntityType.Builder.of(SuperiorFluidPipeBlockEntity::new, SuperiorFluidPipe.get()).build(null));
    public static final RegistryObject<BlockEntityType<UltimateFluidPipeBlockEntity>> UltimateFluidPipeBE = BLOCK_ENTITIES.register
            ("ultimate_fluid_pipe", () -> BlockEntityType.Builder.of(UltimateFluidPipeBlockEntity::new, UltimateFluidPipe.get()).build(null));

    public static void register(IEventBus eventBus) {

        BLOCK_ENTITIES.register(eventBus);
    }
}