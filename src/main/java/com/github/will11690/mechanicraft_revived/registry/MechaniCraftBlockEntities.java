package com.github.will11690.mechanicraft_revived.registry;

import com.github.will11690.mechanicraft_revived.MechaniCraftMain;
import com.github.will11690.mechanicraft_revived.blocks.primitive.infuser.PrimitiveInfuserBE;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MechaniCraftBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MechaniCraftMain.MODID);

    public static final RegistryObject<BlockEntityType<PrimitiveInfuserBE>> PrimitiveInfuserBE = BLOCK_ENTITIES.register("primitive_metallic_infuser_be",
            () -> BlockEntityType.Builder.of(PrimitiveInfuserBE::new, MechaniCraftBlocks.PrimitiveInfuser.get()).build(null));

    public static void register(IEventBus eventBus) {

        BLOCK_ENTITIES.register(eventBus);
    }
}