package com.github.will11690.mechanicraft_revived.items.armor;

import com.github.will11690.mechanicraft_revived.MechaniCraftMain;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftItems;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

import java.util.EnumMap;
import java.util.function.Supplier;

public enum MechaniCraftArmorMaterials implements ArmorMaterial {

    EMERONIUM("emeronium", 27, Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {

        map.put(ArmorItem.Type.BOOTS, 3);
        map.put(ArmorItem.Type.LEGGINGS, 6);
        map.put(ArmorItem.Type.CHESTPLATE, 8);
        map.put(ArmorItem.Type.HELMET, 3);
    }), 15, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0F, 0.0F, () -> {
        return Ingredient.of(MechaniCraftItems.EmeroniumIngot.get());
    }),

    ENDONIUM("endonium", 37, Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {

        map.put(ArmorItem.Type.BOOTS, 3);
        map.put(ArmorItem.Type.LEGGINGS, 6);
        map.put(ArmorItem.Type.CHESTPLATE, 8);
        map.put(ArmorItem.Type.HELMET, 3);
    }), 18, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F, () -> {
        return Ingredient.of(MechaniCraftItems.EndoniumIngot.get());
    }),

    ENDONIUM_CRYSTAL("endonium_crystal", 40, Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {

        map.put(ArmorItem.Type.BOOTS, 3);
        map.put(ArmorItem.Type.LEGGINGS, 6);
        map.put(ArmorItem.Type.CHESTPLATE, 8);
        map.put(ArmorItem.Type.HELMET, 3);
    }), 20, SoundEvents.ARMOR_EQUIP_NETHERITE, 4.0F, 0.2F, () -> {
        return Ingredient.of(MechaniCraftItems.EndoniumCrystal.get());
    }),

    GLASS("glass", 5, Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {

        map.put(ArmorItem.Type.BOOTS, 1);
        map.put(ArmorItem.Type.LEGGINGS, 4);
        map.put(ArmorItem.Type.CHESTPLATE, 5);
        map.put(ArmorItem.Type.HELMET, 2);
    }), 6, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0F, 0.0F, () -> {
        return Ingredient.of(Tags.Items.GLASS);
    }),

    OBSIDIUM("obsidium", 15, Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {

        map.put(ArmorItem.Type.BOOTS, 3);
        map.put(ArmorItem.Type.LEGGINGS, 6);
        map.put(ArmorItem.Type.CHESTPLATE, 8);
        map.put(ArmorItem.Type.HELMET, 3);
    }), 13, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.5F, 0.1F, () -> {
        return Ingredient.of(MechaniCraftItems.ObsidiumIngot.get());
    }),

    RUBONIUM("rubonium", 30, Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {

        map.put(ArmorItem.Type.BOOTS, 3);
        map.put(ArmorItem.Type.LEGGINGS, 6);
        map.put(ArmorItem.Type.CHESTPLATE, 8);
        map.put(ArmorItem.Type.HELMET, 3);
    }), 12, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0F, 0.0F, () -> {
        return Ingredient.of(MechaniCraftItems.RuboniumIngot.get());
    }),

    SAPHONIUM("saphonium", 30, Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {

        map.put(ArmorItem.Type.BOOTS, 3);
        map.put(ArmorItem.Type.LEGGINGS, 6);
        map.put(ArmorItem.Type.CHESTPLATE, 8);
        map.put(ArmorItem.Type.HELMET, 3);
    }), 12, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0F, 0.0F, () -> {
        return Ingredient.of(MechaniCraftItems.SaphoniumIngot.get());
    });


    private static final EnumMap<ArmorItem.Type, Integer> DURABILITY_MAP = Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {
        map.put(ArmorItem.Type.BOOTS, 13);
        map.put(ArmorItem.Type.LEGGINGS, 15);
        map.put(ArmorItem.Type.CHESTPLATE, 16);
        map.put(ArmorItem.Type.HELMET, 11);
    });

    private final String name;
    private final int durabilityMultiplier;
    private final EnumMap<ArmorItem.Type, Integer> protectionFunctionForType;
    private final int enchantmentValue;
    private final SoundEvent sound;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairIngredient;

    private MechaniCraftArmorMaterials(String name, int durabilityMultiplier, EnumMap<ArmorItem.Type, Integer> protectionFunctionForType, int enchantmentValue, SoundEvent sound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionFunctionForType = protectionFunctionForType;
        this.enchantmentValue = enchantmentValue;
        this.sound = sound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = repairIngredient;
    }

    public int getDurabilityForType(ArmorItem.Type type) {

        return DURABILITY_MAP.get(type) * this.durabilityMultiplier;
    }

    public int getDefenseForType(ArmorItem.Type type) {

        return this.protectionFunctionForType.get(type);
    }

    public int getEnchantmentValue() {

        return this.enchantmentValue;
    }

    public SoundEvent getEquipSound() {

        return this.sound;
    }

    public Ingredient getRepairIngredient() {

        return this.repairIngredient.get();
    }

    public String getName() {
        return MechaniCraftMain.MODID + ":" + this.name;
    }

    public float getToughness() {

        return this.toughness;
    }

    public float getKnockbackResistance() {

        return this.knockbackResistance;
    }

    public String getSerializedName() {

        return MechaniCraftMain.MODID + ":" + this.name;
    }
}
