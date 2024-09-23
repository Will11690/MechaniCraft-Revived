package com.github.will11690.mechanicraft_revived.registry;

import com.github.will11690.mechanicraft_revived.MechaniCraftMain;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MechaniCraftTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVEMODETABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MechaniCraftMain.MODID);

    public static final RegistryObject<CreativeModeTab> ItemsTab = CREATIVEMODETABS.register("items_tab", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(MechaniCraftItems.AuFeIngot.get()))
            .title(Component.translatable("creativemodetab.mechanicraft_revived.items_tab"))
            .displayItems((itemDisplayParameters, output) -> {

                output.accept(MechaniCraftItems.RawLead.get());
                output.accept(MechaniCraftItems.RawSilver.get());
                output.accept(MechaniCraftItems.RawTin.get());

                output.accept(MechaniCraftItems.AuFeIngot.get());
                output.accept(MechaniCraftItems.BronzeIngot.get());
                output.accept(MechaniCraftItems.EmeroniumIngot.get());
                output.accept(MechaniCraftItems.EnderIngot.get());
                output.accept(MechaniCraftItems.EndoniumIngot.get());
                output.accept(MechaniCraftItems.LeadIngot.get());
                output.accept(MechaniCraftItems.RuboniumIngot.get());
                output.accept(MechaniCraftItems.SaphoniumIngot.get());
                output.accept(MechaniCraftItems.SilverIngot.get());
                output.accept(MechaniCraftItems.SteelIngot.get());
                output.accept(MechaniCraftItems.TinIngot.get());
                output.accept(MechaniCraftItems.ObsidiumIngot.get());

                output.accept(MechaniCraftItems.EndoniumCrystal.get());
                output.accept(MechaniCraftItems.DiamoniumCrystal.get());
                output.accept(MechaniCraftItems.RubyGem.get());
                output.accept(MechaniCraftItems.SapphireGem.get());

                output.accept(MechaniCraftItems.AuFeNugget.get());
                output.accept(MechaniCraftItems.BronzeNugget.get());
                output.accept(MechaniCraftItems.CopperNugget.get());
                output.accept(MechaniCraftItems.EmeroniumNugget.get());
                output.accept(MechaniCraftItems.EnderNugget.get());
                output.accept(MechaniCraftItems.EndoniumNugget.get());
                output.accept(MechaniCraftItems.LeadNugget.get());
                output.accept(MechaniCraftItems.RuboniumNugget.get());
                output.accept(MechaniCraftItems.SaphoniumNugget.get());
                output.accept(MechaniCraftItems.SilverNugget.get());
                output.accept(MechaniCraftItems.SteelNugget.get());
                output.accept(MechaniCraftItems.TinNugget.get());
                output.accept(MechaniCraftItems.ObsidiumNugget.get());

                output.accept(MechaniCraftItems.EndoniumCrystalNugget.get());
                output.accept(MechaniCraftItems.DiamoniumCrystalNugget.get());
                output.accept(MechaniCraftItems.DiamondNugget.get());
                output.accept(MechaniCraftItems.EmeraldNugget.get());
                output.accept(MechaniCraftItems.RubyNugget.get());
                output.accept(MechaniCraftItems.SapphireNugget.get());

                output.accept(MechaniCraftItems.AuFeDust.get());
                output.accept(MechaniCraftItems.BronzeDust.get());
                output.accept(MechaniCraftItems.CopperDust.get());
                output.accept(MechaniCraftItems.DiamoniumCrystalDust.get());
                output.accept(MechaniCraftItems.DiamondDust.get());
                output.accept(MechaniCraftItems.EmeraldDust.get());
                output.accept(MechaniCraftItems.EmeroniumDust.get());
                output.accept(MechaniCraftItems.EnderDust.get());
                output.accept(MechaniCraftItems.EndoniumCrystalDust.get());
                output.accept(MechaniCraftItems.EndoniumDust.get());
                output.accept(MechaniCraftItems.GoldDust.get());
                output.accept(MechaniCraftItems.IronDust.get());
                output.accept(MechaniCraftItems.LeadDust.get());
                output.accept(MechaniCraftItems.ObsidianDust.get());
                output.accept(MechaniCraftItems.ObsidiumDust.get());
                output.accept(MechaniCraftItems.RuboniumDust.get());
                output.accept(MechaniCraftItems.RubyDust.get());
                output.accept(MechaniCraftItems.SaphoniumDust.get());
                output.accept(MechaniCraftItems.SapphireDust.get());
                output.accept(MechaniCraftItems.SilverDust.get());
                output.accept(MechaniCraftItems.SteelDust.get());
                output.accept(MechaniCraftItems.TinDust.get());

                output.accept(MechaniCraftItems.CopperDirtyChunks.get());
                output.accept(MechaniCraftItems.GoldDirtyChunks.get());
                output.accept(MechaniCraftItems.IronDirtyChunks.get());
                output.accept(MechaniCraftItems.LeadDirtyChunks.get());
                output.accept(MechaniCraftItems.SilverDirtyChunks.get());
                output.accept(MechaniCraftItems.TinDirtyChunks.get());

                output.accept(MechaniCraftItems.CopperRefinedChunks.get());
                output.accept(MechaniCraftItems.GoldRefinedChunks.get());
                output.accept(MechaniCraftItems.IronRefinedChunks.get());
                output.accept(MechaniCraftItems.LeadRefinedChunks.get());
                output.accept(MechaniCraftItems.SilverRefinedChunks.get());
                output.accept(MechaniCraftItems.TinRefinedChunks.get());

                output.accept(MechaniCraftItems.CopperPureChunks.get());
                output.accept(MechaniCraftItems.GoldPureChunks.get());
                output.accept(MechaniCraftItems.IronPureChunks.get());
                output.accept(MechaniCraftItems.LeadPureChunks.get());
                output.accept(MechaniCraftItems.SilverPureChunks.get());
                output.accept(MechaniCraftItems.TinPureChunks.get());

                output.accept(MechaniCraftItems.AuFeGear.get());
                //output.accept(MechaniCraftItems.BronzeGear.get());
                //output.accept(MechaniCraftItems.CopperGear.get());
                //output.accept(MechaniCraftItems.DiamondGear.get());
                output.accept(MechaniCraftItems.DiamoniumGear.get());
                //output.accept(MechaniCraftItems.EmeraldGear.get());
                output.accept(MechaniCraftItems.EmeroniumGear.get());
                output.accept(MechaniCraftItems.EndoniumGear.get());
                output.accept(MechaniCraftItems.IronGear.get());
                //output.accept(MechaniCraftItems.LeadGear.get());
                output.accept(MechaniCraftItems.ObsidiumGear.get());
                output.accept(MechaniCraftItems.RuboniumGear.get());
                output.accept(MechaniCraftItems.SaphoniumGear.get());
                output.accept(MechaniCraftItems.StoneGear.get());
                //output.accept(MechaniCraftItems.TinGear.get());
                output.accept(MechaniCraftItems.WoodenGear.get());

                output.accept(MechaniCraftItems.StringMesh.get());
                output.accept(MechaniCraftItems.ReinforcedStringMesh.get());
                output.accept(MechaniCraftItems.IronMesh.get());
                output.accept(MechaniCraftItems.ReinforcedIronMesh.get());
                output.accept(MechaniCraftItems.SteelMesh.get());
                output.accept(MechaniCraftItems.ReinforcedSteelMesh.get());
                output.accept(MechaniCraftItems.DiamondMesh.get());
                output.accept(MechaniCraftItems.ReinforcedDiamondMesh.get());
                output.accept(MechaniCraftItems.GemMesh.get());
                output.accept(MechaniCraftItems.ReinforcedGemMesh.get());
                output.accept(MechaniCraftItems.EndoniumMesh.get());
                output.accept(MechaniCraftItems.ReinforcedEndoniumMesh.get());

                output.accept(MechaniCraftItems.PressDieGear.get());
                output.accept(MechaniCraftItems.PressDiePlate.get());
                output.accept(MechaniCraftItems.PressDieRod.get());

                output.accept(MechaniCraftItems.CapacityUpgrade.get());
                output.accept(MechaniCraftItems.CreativeUpgrade.get());
                output.accept(MechaniCraftItems.EfficiencyUpgrade.get());
                output.accept(MechaniCraftItems.SpeedUpgrade.get());
                output.accept(MechaniCraftItems.TransferUpgrade.get());
            }).build()
    );

    public static final RegistryObject<CreativeModeTab> BlocksTab = CREATIVEMODETABS.register("blocks_tab", () -> CreativeModeTab.builder()
            .withTabsAfter(MechaniCraftTabs.ItemsTab.getKey())
            .icon(() -> new ItemStack(MechaniCraftBlocks.AuFeBlock.get()))
            .title(Component.translatable("creativemodetab.mechanicraft_revived.blocks_tab"))
            .displayItems((itemDisplayParameters, output) -> {

                output.accept(MechaniCraftBlocks.EnderOre.get());
                output.accept(MechaniCraftBlocks.LeadOre.get());
                output.accept(MechaniCraftBlocks.RubyOre.get());
                output.accept(MechaniCraftBlocks.SapphireOre.get());
                output.accept(MechaniCraftBlocks.SilverOre.get());
                output.accept(MechaniCraftBlocks.TinOre.get());

                output.accept(MechaniCraftBlocks.DeepslateLeadOre.get());
                output.accept(MechaniCraftBlocks.DeepslateRubyOre.get());
                output.accept(MechaniCraftBlocks.DeepslateSapphireOre.get());
                output.accept(MechaniCraftBlocks.DeepslateSilverOre.get());
                output.accept(MechaniCraftBlocks.DeepslateTinOre.get());

                output.accept(MechaniCraftBlocks.RawLeadBlock.get());
                output.accept(MechaniCraftBlocks.RawSilverBlock.get());
                output.accept(MechaniCraftBlocks.RawTinBlock.get());

                output.accept(MechaniCraftBlocks.AuFeBlock.get());
                output.accept(MechaniCraftBlocks.BronzeBlock.get());
                output.accept(MechaniCraftBlocks.EmeroniumBlock.get());
                output.accept(MechaniCraftBlocks.EnderBlock.get());
                output.accept(MechaniCraftBlocks.EnderDustBlock.get());
                output.accept(MechaniCraftBlocks.EndoniumBlock.get());
                output.accept(MechaniCraftBlocks.EndoniumCrystalBlock.get());
                output.accept(MechaniCraftBlocks.LeadBlock.get());
                output.accept(MechaniCraftBlocks.ObsidiumBlock.get());
                output.accept(MechaniCraftBlocks.RuboniumBlock.get());
                output.accept(MechaniCraftBlocks.RubyBlock.get());
                output.accept(MechaniCraftBlocks.SaphoniumBlock.get());
                output.accept(MechaniCraftBlocks.SapphireBlock.get());
                output.accept(MechaniCraftBlocks.SilverBlock.get());
                output.accept(MechaniCraftBlocks.SteelBlock.get());
                output.accept(MechaniCraftBlocks.TinBlock.get());
            }).build()
    );

    public static final RegistryObject<CreativeModeTab> MachinesTab = CREATIVEMODETABS.register("machines_tab", () -> CreativeModeTab.builder()
            .withTabsAfter(MechaniCraftTabs.BlocksTab.getKey())
            .icon(() -> new ItemStack(MechaniCraftBlocks.T1GearBox.get()))
            .title(Component.translatable("creativemodetab.mechanicraft_revived.machines_tab"))
            .displayItems((itemDisplayParameters, output) -> {

                output.accept(MechaniCraftBlocks.MachineBlock.get());
                output.accept(MechaniCraftBlocks.T1GearBox.get());
                output.accept(MechaniCraftBlocks.T2GearBox.get());
                output.accept(MechaniCraftBlocks.T3GearBox.get());
                output.accept(MechaniCraftBlocks.T4GearBox.get());
                output.accept(MechaniCraftBlocks.T5GearBox.get());
                output.accept(MechaniCraftBlocks.T6GearBox.get());

                output.accept(MechaniCraftBlocks.PrimitiveInfuser.get());
            }).build()
    );

    public static final RegistryObject<CreativeModeTab> ArmorTab = CREATIVEMODETABS.register("armor_tab", () -> CreativeModeTab.builder()
            .withTabsAfter(MechaniCraftTabs.MachinesTab.getKey())
            .icon(() -> new ItemStack(MechaniCraftItems.EndoniumCrystalChestplate.get()))
            .title(Component.translatable("creativemodetab.mechanicraft_revived.armor_tab"))
            .displayItems((itemDisplayParameters, output) -> {

                output.accept(MechaniCraftItems.EmeroniumHelmet.get());
                output.accept(MechaniCraftItems.EmeroniumChestplate.get());
                output.accept(MechaniCraftItems.EmeroniumLeggings.get());
                output.accept(MechaniCraftItems.EmeroniumBoots.get());

                output.accept(MechaniCraftItems.EndoniumHelmet.get());
                output.accept(MechaniCraftItems.EndoniumChestplate.get());
                output.accept(MechaniCraftItems.EndoniumLeggings.get());
                output.accept(MechaniCraftItems.EndoniumBoots.get());

                output.accept(MechaniCraftItems.EndoniumCrystalHelmet.get());
                output.accept(MechaniCraftItems.EndoniumCrystalChestplate.get());
                output.accept(MechaniCraftItems.EndoniumCrystalLeggings.get());
                output.accept(MechaniCraftItems.EndoniumCrystalBoots.get());

                output.accept(MechaniCraftItems.GlassHelmet.get());
                output.accept(MechaniCraftItems.GlassChestplate.get());
                output.accept(MechaniCraftItems.GlassLeggings.get());
                output.accept(MechaniCraftItems.GlassBoots.get());

                output.accept(MechaniCraftItems.ObsidiumHelmet.get());
                output.accept(MechaniCraftItems.ObsidiumChestplate.get());
                output.accept(MechaniCraftItems.ObsidiumLeggings.get());
                output.accept(MechaniCraftItems.ObsidiumBoots.get());

                output.accept(MechaniCraftItems.RuboniumHelmet.get());
                output.accept(MechaniCraftItems.RuboniumChestplate.get());
                output.accept(MechaniCraftItems.RuboniumLeggings.get());
                output.accept(MechaniCraftItems.RuboniumBoots.get());

                output.accept(MechaniCraftItems.SaphoniumHelmet.get());
                output.accept(MechaniCraftItems.SaphoniumChestplate.get());
                output.accept(MechaniCraftItems.SaphoniumLeggings.get());
                output.accept(MechaniCraftItems.SaphoniumBoots.get());
            }).build()
    );

    public static final RegistryObject<CreativeModeTab> ToolsTab = CREATIVEMODETABS.register("tools_tab", () -> CreativeModeTab.builder()
            .withTabsAfter(MechaniCraftTabs.ArmorTab.getKey())
            .icon(() -> new ItemStack(MechaniCraftItems.EndoniumCrystalPickaxe.get()))
            .title(Component.translatable("creativemodetab.mechanicraft_revived.tools_tab"))
            .displayItems((itemDisplayParameters, output) -> {

                output.accept(MechaniCraftItems.EmeroniumAxe.get());
                output.accept(MechaniCraftItems.EmeroniumHoe.get());
                output.accept(MechaniCraftItems.EmeroniumPickaxe.get());
                output.accept(MechaniCraftItems.EmeroniumShovel.get());
                output.accept(MechaniCraftItems.EmeroniumSword.get());

                output.accept(MechaniCraftItems.EndoniumAxe.get());
                output.accept(MechaniCraftItems.EndoniumHoe.get());
                output.accept(MechaniCraftItems.EndoniumPickaxe.get());
                output.accept(MechaniCraftItems.EndoniumShovel.get());
                output.accept(MechaniCraftItems.EndoniumSword.get());

                output.accept(MechaniCraftItems.EndoniumCrystalAxe.get());
                output.accept(MechaniCraftItems.EndoniumCrystalHoe.get());
                output.accept(MechaniCraftItems.EndoniumCrystalPickaxe.get());
                output.accept(MechaniCraftItems.EndoniumCrystalShovel.get());
                output.accept(MechaniCraftItems.EndoniumCrystalSword.get());

                output.accept(MechaniCraftItems.GlassAxe.get());
                output.accept(MechaniCraftItems.GlassHoe.get());
                output.accept(MechaniCraftItems.GlassPickaxe.get());
                output.accept(MechaniCraftItems.GlassShovel.get());
                output.accept(MechaniCraftItems.GlassSword.get());

                output.accept(MechaniCraftItems.ObsidiumAxe.get());
                output.accept(MechaniCraftItems.ObsidiumHoe.get());
                output.accept(MechaniCraftItems.ObsidiumPickaxe.get());
                output.accept(MechaniCraftItems.ObsidiumShovel.get());
                output.accept(MechaniCraftItems.ObsidiumSword.get());

                output.accept(MechaniCraftItems.RuboniumAxe.get());
                output.accept(MechaniCraftItems.RuboniumHoe.get());
                output.accept(MechaniCraftItems.RuboniumPickaxe.get());
                output.accept(MechaniCraftItems.RuboniumShovel.get());
                output.accept(MechaniCraftItems.RuboniumSword.get());

                output.accept(MechaniCraftItems.SaphoniumAxe.get());
                output.accept(MechaniCraftItems.SaphoniumHoe.get());
                output.accept(MechaniCraftItems.SaphoniumPickaxe.get());
                output.accept(MechaniCraftItems.SaphoniumShovel.get());
                output.accept(MechaniCraftItems.SaphoniumSword.get());
            }).build()
    );

    public static void register(IEventBus outputBus) {

        CREATIVEMODETABS.register(outputBus);
    }
}