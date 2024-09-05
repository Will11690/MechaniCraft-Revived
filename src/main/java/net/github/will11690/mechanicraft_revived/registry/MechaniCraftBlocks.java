package net.github.will11690.mechanicraft_revived.registry;

import net.github.will11690.mechanicraft_revived.MechaniCraftMain;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MechaniCraftBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MechaniCraftMain.MODID);
    //public static final DeferredRegister<Item> BLOCKITEM = DeferredRegister.create(ForgeRegistries.ITEMS, MechaniCraftMain.MODID);

    //public static final RegistryObject<Block> TestBlock = BLOCKS.register("test_block", () -> new Block(new Block.Properties()));
    //public static final RegistryObject<BlockItem> TestBlockItem = BLOCKITEM.register("test_block", () -> new BlockItem(TestBlock, ))

    public static void registerBlocks(IEventBus eventBus) {

        BLOCKS.register(eventBus);
    }
}
