package net.github.will11690.mechanicraft_revived.registry;

import net.github.will11690.mechanicraft_revived.MechaniCraftMain;
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

    //TODO recreate blocks(skip block entities/machines for now)

    public static final RegistryObject<Block> TestBlock = registerBlock("test_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));


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
