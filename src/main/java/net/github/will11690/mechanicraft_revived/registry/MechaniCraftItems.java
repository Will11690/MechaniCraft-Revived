package net.github.will11690.mechanicraft_revived.registry;

import net.github.will11690.mechanicraft_revived.MechaniCraftMain;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MechaniCraftItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MechaniCraftMain.MODID);

    //Raw

    //Dust

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

    //Nugget

    //Gem

    //Gear

    //Mesh

    public static void registerItems(IEventBus eventBus) {

        ITEMS.register(eventBus);
    }
}