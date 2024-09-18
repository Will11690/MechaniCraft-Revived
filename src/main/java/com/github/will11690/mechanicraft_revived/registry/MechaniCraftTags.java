package com.github.will11690.mechanicraft_revived.registry;

import com.github.will11690.mechanicraft_revived.MechaniCraftMain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class MechaniCraftTags {

    public static class Items {

        private static TagKey<Item> tag(String name) {

            return ItemTags.create(new ResourceLocation(MechaniCraftMain.MODID, name));
        }

        private static TagKey<Item> forgeTag(String name) {

            return ItemTags.create(new ResourceLocation("forge", name));
        }
    }

    public static class Blocks {

        //ALL TAGS INTENTIONALLY BLANK AND SET TO REPLACE TRUE FOR EQUAL TIERS TO DIAMOND!
        public static final TagKey<Block> NEEDS_GLASS_TOOL = tag("needs_glass_tool");
        public static final TagKey<Block> NEEDS_OBSIDIUM_TOOL = tag("needs_obsidium_tool");
        public static final TagKey<Block> NEEDS_SAPHONIUM_TOOL = tag("needs_saphonium_tool");
        public static final TagKey<Block> NEEDS_RUBONIUM_TOOL = tag("needs_rubonium_tool");
        public static final TagKey<Block> NEEDS_EMERONIUM_TOOL = tag("needs_emeronium_tool");
        public static final TagKey<Block> NEEDS_ENDONIUM_TOOL = tag("needs_endonium_tool");
        public static final TagKey<Block> NEEDS_ENDONIUM_CRYSTAL_TOOL = tag("needs_endonium_crystal_tool");

        private static TagKey<Block> tag(String name) {

            return BlockTags.create(new ResourceLocation(MechaniCraftMain.MODID, name));
        }

        private static TagKey<Block> forgeTag(String name) {

            return BlockTags.create(new ResourceLocation("forge", name));
        }
    }
}