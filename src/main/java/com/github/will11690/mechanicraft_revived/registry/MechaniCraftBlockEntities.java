package com.github.will11690.mechanicraft_revived.registry;

import com.github.will11690.mechanicraft_revived.MechaniCraftMain;
import com.github.will11690.mechanicraft_revived.blocks.basic.solidfuelgen.BasicSolidFuelGeneratorBE;
import com.github.will11690.mechanicraft_revived.blocks.pipes.energy.basic.BasicEnergyPipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.powered.miningwell.MiningWellBE;
import com.github.will11690.mechanicraft_revived.blocks.primitive.infuser.PrimitiveInfuserBE;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlocks.BasicEnergyPipe;

public class MechaniCraftBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MechaniCraftMain.MODID);

    public static final RegistryObject<BlockEntityType<PrimitiveInfuserBE>> PrimitiveInfuserBE = BLOCK_ENTITIES.register
            ("primitive_metallic_infuser_be", () -> BlockEntityType.Builder.of(PrimitiveInfuserBE::new, MechaniCraftBlocks.PrimitiveInfuser.get()).build(null));

    public static final RegistryObject<BlockEntityType<MiningWellBE>> MiningWellBE = BLOCK_ENTITIES.register
            ("mining_well_be", () -> BlockEntityType.Builder.of(MiningWellBE::new, MechaniCraftBlocks.MiningWell.get()).build(null));

    public static final RegistryObject<BlockEntityType<BasicSolidFuelGeneratorBE>> BasicSolidFuelGeneratorBE = BLOCK_ENTITIES.register
            ("basic_solid_fuel_generator_be", () -> BlockEntityType.Builder.of(BasicSolidFuelGeneratorBE::new, MechaniCraftBlocks.BasicSolidFuelGenerator.get()).build(null));

    public static final RegistryObject<BlockEntityType<BasicEnergyPipeBlockEntity>> BasicEnergyPipeBE = BLOCK_ENTITIES.register
            ("basic_energy_pipe", () -> BlockEntityType.Builder.of(BasicEnergyPipeBlockEntity::new, BasicEnergyPipe.get()).build(null));

    public static void register(IEventBus eventBus) {

        BLOCK_ENTITIES.register(eventBus);
    }
}