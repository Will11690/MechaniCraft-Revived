package com.github.will11690.mechanicraft_revived.registry;

import com.github.will11690.mechanicraft_revived.MechaniCraftMain;
import com.github.will11690.mechanicraft_revived.blocks.primitive.infuser.PrimitiveInfuserContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MechaniCraftContainers {

    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MechaniCraftMain.MODID);

    public static final RegistryObject<MenuType<PrimitiveInfuserContainer>> PrimitiveInfuserCont =
            registerContainer(PrimitiveInfuserContainer::new, "primitive_metallic_infuser_container");

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerContainer(IContainerFactory<T> factory, String name) {
        return CONTAINERS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {

        CONTAINERS.register(eventBus);
    }
}
