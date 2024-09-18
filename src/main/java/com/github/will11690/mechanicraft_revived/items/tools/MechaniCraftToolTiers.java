package com.github.will11690.mechanicraft_revived.items.tools;

import com.github.will11690.mechanicraft_revived.MechaniCraftMain;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftItems;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.TierSortingRegistry;

import java.util.List;

public class MechaniCraftToolTiers {

    public static final Tier GLASS = TierSortingRegistry.registerTier( new ForgeTier(
                    /*harvest level*/2,
                    /*durability*/30,
                    /*efficiency*/3.00f,
                    /*damage*/8.00f,
                    /*enchantability*/6,
                    /*mining level tag*/MechaniCraftTags.Blocks.NEEDS_GLASS_TOOL,
                    () -> Ingredient.of(Tags.Items.GLASS)),
            new ResourceLocation(MechaniCraftMain.MODID, "glass"), List.of(Tiers.STONE), List.of(Tiers.IRON));

    public static final Tier OBSIDIUM = TierSortingRegistry.registerTier( new ForgeTier(
                    /*harvest level*/3,
                    /*durability*/1261,
                    /*efficiency*/9.00f,
                    /*damage*/4.00f,
                    /*enchantability*/13,
                    /*mining level tag*/MechaniCraftTags.Blocks.NEEDS_OBSIDIUM_TOOL,
                    () -> Ingredient.of(MechaniCraftItems.ObsidiumIngot.get())),
            new ResourceLocation(MechaniCraftMain.MODID, "obsidium"), List.of(Tiers.DIAMOND), List.of(Tiers.NETHERITE));

    public static final Tier EMERONIUM = TierSortingRegistry.registerTier( new ForgeTier(
                /*harvest level*/3,
                /*durability*/1431,
                /*efficiency*/8.50f,
                /*damage*/3.50f,
                /*enchantability*/15,
                /*mining level tag*/MechaniCraftTags.Blocks.NEEDS_EMERONIUM_TOOL,
                () -> Ingredient.of(MechaniCraftItems.EmeroniumIngot.get())),
        new ResourceLocation(MechaniCraftMain.MODID, "emeronium"), List.of(MechaniCraftToolTiers.OBSIDIUM), List.of(Tiers.NETHERITE));

    public static final Tier RUBONIUM = TierSortingRegistry.registerTier( new ForgeTier(
                /*harvest level*/3,
                /*durability*/1431,
                /*efficiency*/8.50f,
                /*damage*/3.50f,
                /*enchantability*/12,
                /*mining level tag*/MechaniCraftTags.Blocks.NEEDS_RUBONIUM_TOOL,
                () -> Ingredient.of(MechaniCraftItems.RuboniumIngot.get())),
        new ResourceLocation(MechaniCraftMain.MODID, "rubonium"), List.of(MechaniCraftToolTiers.EMERONIUM), List.of(Tiers.NETHERITE));

    public static final Tier SAPHONIUM = TierSortingRegistry.registerTier( new ForgeTier(
                /*harvest level*/3,
                /*durability*/1431,
                /*efficiency*/8.50f,
                /*damage*/3.50f,
                /*enchantability*/12,
                /*mining level tag*/MechaniCraftTags.Blocks.NEEDS_SAPHONIUM_TOOL,
                () -> Ingredient.of(MechaniCraftItems.SaphoniumIngot.get())),
        new ResourceLocation(MechaniCraftMain.MODID, "saphonium"), List.of(MechaniCraftToolTiers.RUBONIUM), List.of(Tiers.NETHERITE));

    public static final Tier ENDONIUM = TierSortingRegistry.registerTier( new ForgeTier(
                    /*harvest level*/4,
                    /*durability*/1796,
                    /*efficiency*/9.50f,
                    /*damage*/5.00f,
                    /*enchantability*/18,
                    /*mining level tag*/MechaniCraftTags.Blocks.NEEDS_ENDONIUM_TOOL,
                    () -> Ingredient.of(MechaniCraftItems.EndoniumIngot.get())),
            new ResourceLocation(MechaniCraftMain.MODID, "endonium"), List.of(Tiers.NETHERITE), List.of());

    public static final Tier ENDONIUM_CRYSTAL = TierSortingRegistry.registerTier( new ForgeTier(
                    /*harvest level*/5,
                    /*durability*/2501,
                    /*efficiency*/10.00f,
                    /*damage*/6.00f,
                    /*enchantability*/20,
                    /*mining level tag*/MechaniCraftTags.Blocks.NEEDS_ENDONIUM_CRYSTAL_TOOL,
                    () -> Ingredient.of(MechaniCraftItems.EndoniumCrystal.get())),
            new ResourceLocation(MechaniCraftMain.MODID, "endonium_crystal"), List.of(MechaniCraftToolTiers.ENDONIUM), List.of());

}