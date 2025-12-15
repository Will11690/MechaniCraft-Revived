package com.github.will11690.mechanicraft_revived.registry;

import com.github.will11690.mechanicraft_revived.MechaniCraftMain;
import com.github.will11690.mechanicraft_revived.blocks.generators.basic.solidfuelgen.BasicSolidFuelGeneratorContainer;
import com.github.will11690.mechanicraft_revived.blocks.machines.advanced.infuser.AdvancedInfuserContainer;
import com.github.will11690.mechanicraft_revived.blocks.machines.basic.infuser.BasicInfuserContainer;
import com.github.will11690.mechanicraft_revived.blocks.machines.elite.infuser.EliteInfuserContainer;
import com.github.will11690.mechanicraft_revived.blocks.machines.enhanced.infuser.EnhancedInfuserContainer;
import com.github.will11690.mechanicraft_revived.blocks.machines.misc.miningwell.MiningWellContainer;
import com.github.will11690.mechanicraft_revived.blocks.machines.primitive.infuser.PrimitiveInfuserContainer;
import com.github.will11690.mechanicraft_revived.blocks.machines.superior.infuser.SuperiorInfuserContainer;
import com.github.will11690.mechanicraft_revived.blocks.machines.ultimate.infuser.UltimateInfuserContainer;
import com.github.will11690.mechanicraft_revived.blocks.storages.base.energycube.gui.EnergyCubeContainer;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.gui.PipeConfigContainer;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.gui.PipeFilterContainer;
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

    //STORAGES
    public static final RegistryObject<MenuType<EnergyCubeContainer>> EnergyCubeCont =
            registerContainer(EnergyCubeContainer::new, "energy_cube_container");

    //MACHINES
    public static final RegistryObject<MenuType<PrimitiveInfuserContainer>> PrimitiveInfuserCont =
            registerContainer(PrimitiveInfuserContainer::new, "primitive_metallic_infuser_container");

    public static final RegistryObject<MenuType<BasicInfuserContainer>> BasicInfuserCont =
            registerContainer(BasicInfuserContainer::new, "basic_metallic_infuser_container");

    public static final RegistryObject<MenuType<EnhancedInfuserContainer>> EnhancedInfuserCont =
            registerContainer(EnhancedInfuserContainer::new, "enhanced_metallic_infuser_container");

    public static final RegistryObject<MenuType<AdvancedInfuserContainer>> AdvancedInfuserCont =
            registerContainer(AdvancedInfuserContainer::new, "advanced_metallic_infuser_container");

    public static final RegistryObject<MenuType<EliteInfuserContainer>> EliteInfuserCont =
            registerContainer(EliteInfuserContainer::new, "elite_metallic_infuser_container");

    public static final RegistryObject<MenuType<SuperiorInfuserContainer>> SuperiorInfuserCont =
            registerContainer(SuperiorInfuserContainer::new, "superior_metallic_infuser_container");

    public static final RegistryObject<MenuType<UltimateInfuserContainer>> UltimateInfuserCont =
            registerContainer(UltimateInfuserContainer::new, "ultimate_metallic_infuser_container");

    public static final RegistryObject<MenuType<MiningWellContainer>> MiningWellContainer =
            registerContainer(MiningWellContainer::new, "mining_well_container");

    public static final RegistryObject<MenuType<BasicSolidFuelGeneratorContainer>> BasicSolidFuelGeneratorContainer =
            registerContainer(BasicSolidFuelGeneratorContainer::new, "basic_solid_fuel_generator");

    //CONFIG PIPES
    public static final RegistryObject<MenuType<PipeConfigContainer>> PipeConfigCont =
            registerContainer(PipeConfigContainer::new, "pipe_config_container");

    //FILTER PIPES
    public static final RegistryObject<MenuType<PipeFilterContainer>> PipeFilterCont =
            registerContainer(PipeFilterContainer::new, "pipe_filter_container");

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerContainer(IContainerFactory<T> factory, String name) {
        return CONTAINERS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {

        CONTAINERS.register(eventBus);
    }
}
