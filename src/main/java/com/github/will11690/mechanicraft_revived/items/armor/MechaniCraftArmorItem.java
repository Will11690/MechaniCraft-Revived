package com.github.will11690.mechanicraft_revived.items.armor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;

@SuppressWarnings("DataFlowIssue")
public class MechaniCraftArmorItem extends ArmorItem {

    //TODO Get rid of as many potion effects as possible with events and adjusting player stats instead

    private static final List<MobEffectInstance> GLASS_EFFECT_LIST =
            (new ImmutableList.Builder<MobEffectInstance>()
            .add(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 0))
            .add(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 400, 0))
            .add(new MobEffectInstance(MobEffects.DIG_SPEED, 400, 0))
            .build());

    private static final List<MobEffectInstance> ENDONIUM_EFFECT_LIST =
            (new ImmutableList.Builder<MobEffectInstance>()
                    .add(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 400, 1))
                    .add(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 400, 0))
                    .add(new MobEffectInstance(MobEffects.HEALTH_BOOST, 400, 3))
                    .add(new MobEffectInstance(MobEffects.WATER_BREATHING, 400, 0))
                    .add(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 1))
                    .add(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 400, 1))
                    .add(new MobEffectInstance(MobEffects.DIG_SPEED, 400, 1))
                    .add(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0))
                    .build());

    private static final List<MobEffectInstance> ENDONIUM_CRYSTAL_EFFECT_LIST =
            (new ImmutableList.Builder<MobEffectInstance>()
                    .add(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 400, 2))
                    .add(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 400, 0))
                    .add(new MobEffectInstance(MobEffects.HEALTH_BOOST, 400, 4))
                    .add(new MobEffectInstance(MobEffects.WATER_BREATHING, 400, 0))
                    .add(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 2))
                    .add(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 400, 2))
                    .add(new MobEffectInstance(MobEffects.DIG_SPEED, 400, 2))
                    .add(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0))
                    .build());

    private static final Map<ArmorMaterial, MobEffectInstance> MATERIAL_TO_SINGLE_EFFECT_MAP =
            (new ImmutableMap.Builder<ArmorMaterial, MobEffectInstance>()
                    .put(MechaniCraftArmorMaterials.EMERONIUM, new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 400, 0))
                    .put(MechaniCraftArmorMaterials.OBSIDIUM, new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 400, 0))
                    .put(MechaniCraftArmorMaterials.RUBONIUM, new MobEffectInstance(MobEffects.HEALTH_BOOST, 400, 1))
                    .put(MechaniCraftArmorMaterials.SAPHONIUM, new MobEffectInstance(MobEffects.WATER_BREATHING, 400, 0))
                    .build());


    private static final Map<ArmorMaterial, List<MobEffectInstance>> MATERIAL_TO_MULTI_EFFECT_MAP =
            (new ImmutableMap.Builder<ArmorMaterial, List<MobEffectInstance>>()
                    .put(MechaniCraftArmorMaterials.ENDONIUM, ENDONIUM_EFFECT_LIST)
                    .put(MechaniCraftArmorMaterials.ENDONIUM_CRYSTAL, ENDONIUM_CRYSTAL_EFFECT_LIST)
                    .put(MechaniCraftArmorMaterials.GLASS, GLASS_EFFECT_LIST)
                    .build());

    public MechaniCraftArmorItem(ArmorMaterial material, Type type, Properties properties) {

        super(material, type, properties);
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {

        if(!level.isClientSide() && playerHasFullMechaniCraftArmor(player)) {
            applyEffectsForMaterial(player);
        }
    }

    private boolean isWearingMatchedSet(ArmorMaterial armorMaterial, Player player) {

        MechaniCraftArmorItem helmet = (MechaniCraftArmorItem)player.getInventory().getArmor(3).getItem();
        MechaniCraftArmorItem chestplate = (MechaniCraftArmorItem)player.getInventory().getArmor(2).getItem();
        MechaniCraftArmorItem leggings = (MechaniCraftArmorItem)player.getInventory().getArmor(1).getItem();
        MechaniCraftArmorItem boots = (MechaniCraftArmorItem)player.getInventory().getArmor(0).getItem();

        return helmet.getMaterial().equals(armorMaterial) &&
               chestplate.getMaterial().equals(armorMaterial) &&
               leggings.getMaterial().equals(armorMaterial) &&
               boots.getMaterial().equals(armorMaterial);
    }

    private void applyEffectsForMaterial(Player player) {

        //Multi-Effect sets
        if(checkForMultiEffectList(player)) {

            for(Map.Entry<ArmorMaterial, List<MobEffectInstance>> entry : MATERIAL_TO_MULTI_EFFECT_MAP.entrySet()) {
                ArmorMaterial armorMaterial = entry.getKey();
                List<MobEffectInstance> effectInstances = entry.getValue();

                boolean playerHasHealthBoost = player.hasEffect(MobEffects.HEALTH_BOOST);
                int healthBoostDuration;
                int healthBoostAmplifier;

                if(playerHasHealthBoost) {

                    healthBoostDuration = player.getEffect(MobEffects.HEALTH_BOOST).getDuration();
                    healthBoostAmplifier = player.getEffect(MobEffects.HEALTH_BOOST).getAmplifier();

                } else {

                    healthBoostDuration = 0;
                    healthBoostAmplifier = 0;
                }

                if(isWearingMatchedSet(armorMaterial, player)) {

                    for(MobEffectInstance effectList : effectInstances) {

                        boolean playerHasEffect = player.hasEffect(effectList.getEffect());

                        /*Health boost checks(if swapping armor apply new effect right away or save health and re add once effect times out)
                        WORK AROUND FOR HEALTH BOOST RESETTING EVERY TIME EFFECT IS REAPPLIED*/
                        if(effectList.getEffect().equals(MobEffects.HEALTH_BOOST) && playerHasHealthBoost) {

                            if(effectList.getAmplifier() < healthBoostAmplifier && healthBoostDuration > 1) {

                                player.setHealth(player.getHealth() - (4.0F * (effectList.getAmplifier() - healthBoostAmplifier)));
                                float playerHealth = player.getHealth();
                                player.removeEffect(player.getEffect(MobEffects.HEALTH_BOOST).getEffect());
                                player.addEffect(new MobEffectInstance(effectList.getEffect(), effectList.getDuration(), effectList.getAmplifier()));
                                player.setHealth(playerHealth);
                            }

                            if(healthBoostDuration > 10 && effectList.getAmplifier() == healthBoostAmplifier) {

                                continue;

                            } else {

                                float playerHealth = player.getHealth();

                                player.addEffect(new MobEffectInstance(effectList.getEffect(), effectList.getDuration(), effectList.getAmplifier()));
                                player.setHealth(playerHealth);
                            }
                        }

                        //Remove effect if amplifiers are different and apply new effect(Health Boost handled above so skip it)
                        if(playerHasEffect && !effectList.getEffect().equals(MobEffects.HEALTH_BOOST) && effectList.getAmplifier() != player.getEffect(effectList.getEffect()).getAmplifier()) {

                            player.removeEffect(player.getEffect(effectList.getEffect()).getEffect());
                            player.addEffect(new MobEffectInstance(effectList.getEffect(), effectList.getDuration(), effectList.getAmplifier()));
                        }

                        //Re-apply effect based on time duration(prevent effect spam/night vision flicker)(Health Boost handled above so skip it)
                        if(playerHasEffect && !effectList.getEffect().equals(MobEffects.HEALTH_BOOST) && player.getEffect(effectList.getEffect()).getDuration() < 220) {

                            player.addEffect(new MobEffectInstance(effectList.getEffect(), effectList.getDuration(), effectList.getAmplifier()));
                        }

                        //Apply effect if player does not have effect
                        if(!playerHasEffect) {

                            player.addEffect(new MobEffectInstance(effectList.getEffect(), effectList.getDuration(), effectList.getAmplifier()));
                        }
                    }
                }
            }
        }

        else {
            //Singe effect sets
            for(Map.Entry<ArmorMaterial, MobEffectInstance> entry : MATERIAL_TO_SINGLE_EFFECT_MAP.entrySet()) {
                ArmorMaterial armorMaterial = entry.getKey();
                MobEffectInstance effectInstance = entry.getValue();

                boolean playerHasHealthBoost = player.hasEffect(MobEffects.HEALTH_BOOST);
                boolean playerHasEffect = player.hasEffect(effectInstance.getEffect());
                int healthBoostDuration;
                int healthBoostAmplifier;

                if(playerHasHealthBoost) {

                    healthBoostDuration = player.getEffect(MobEffects.HEALTH_BOOST).getDuration();
                    healthBoostAmplifier = player.getEffect(MobEffects.HEALTH_BOOST).getAmplifier();

                } else {

                    healthBoostDuration = 0;
                    healthBoostAmplifier = 0;
                }

                if(isWearingMatchedSet(armorMaterial, player)) {

                    /*Health boost checks(if swapping armor apply new effect right away or save health and re add once effect times out)
                    WORK AROUND FOR HEALTH BOOST RESETTING EVERY TIME EFFECT IS REAPPLIED*/
                    if(effectInstance.getEffect().equals(MobEffects.HEALTH_BOOST) && playerHasHealthBoost) {

                        if(effectInstance.getAmplifier() < healthBoostAmplifier && healthBoostDuration > 1) {

                            player.setHealth(player.getHealth() - (4.0F * (effectInstance.getAmplifier() - healthBoostAmplifier)));
                            float playerHealth = player.getHealth();
                            player.removeEffect(player.getEffect(MobEffects.HEALTH_BOOST).getEffect());
                            player.addEffect(new MobEffectInstance(effectInstance.getEffect(), effectInstance.getDuration(), effectInstance.getAmplifier()));
                            player.setHealth(playerHealth);
                        }

                        if(healthBoostDuration > 10 && effectInstance.getAmplifier() == healthBoostAmplifier) {

                            continue;

                        } else {

                            float playerHealth = player.getHealth();

                            player.addEffect(new MobEffectInstance(effectInstance.getEffect(), effectInstance.getDuration(), effectInstance.getAmplifier()));
                            player.setHealth(playerHealth);
                        }
                    }

                    //Remove effect if amplifiers are different and apply new effect(Health Boost handled above so skip it)
                    if(playerHasEffect && !effectInstance.getEffect().equals(MobEffects.HEALTH_BOOST) && effectInstance.getAmplifier() != player.getEffect(effectInstance.getEffect()).getAmplifier()) {

                        player.removeEffect(player.getEffect(effectInstance.getEffect()).getEffect());
                        player.addEffect(new MobEffectInstance(effectInstance.getEffect(), effectInstance.getDuration(), effectInstance.getAmplifier()));
                    }

                    //Re-apply effect based on time duration(prevent effect spam/night vision flicker)(Health Boost handled above so skip it)
                    if(playerHasEffect && !effectInstance.getEffect().equals(MobEffects.HEALTH_BOOST) && player.getEffect(effectInstance.getEffect()).getDuration() < 220) {

                        player.addEffect(new MobEffectInstance(effectInstance.getEffect(), effectInstance.getDuration(), effectInstance.getAmplifier()));
                    }

                    //Apply effect if player does not have effect
                    if(!playerHasEffect) {

                        player.addEffect(new MobEffectInstance(effectInstance.getEffect(), effectInstance.getDuration(), effectInstance.getAmplifier()));
                    }
                }
            }
        }
    }

    private boolean checkForMultiEffectList(Player player) {

        MechaniCraftArmorItem helmet = (MechaniCraftArmorItem)player.getInventory().getArmor(3).getItem();
        MechaniCraftArmorItem chestplate = (MechaniCraftArmorItem)player.getInventory().getArmor(2).getItem();
        MechaniCraftArmorItem leggings = (MechaniCraftArmorItem)player.getInventory().getArmor(1).getItem();
        MechaniCraftArmorItem boots = (MechaniCraftArmorItem)player.getInventory().getArmor(0).getItem();

        MechaniCraftArmorMaterials glass = MechaniCraftArmorMaterials.GLASS;
        MechaniCraftArmorMaterials endonium = MechaniCraftArmorMaterials.ENDONIUM;
        MechaniCraftArmorMaterials endoniumCrystal = MechaniCraftArmorMaterials.ENDONIUM_CRYSTAL;

        return (helmet.getMaterial().equals(glass) || helmet.getMaterial().equals(endonium) || helmet.getMaterial().equals(endoniumCrystal)) &&
               (chestplate.getMaterial().equals(glass) || chestplate.getMaterial().equals(endonium) || chestplate.getMaterial().equals(endoniumCrystal)) &&
               (leggings.getMaterial().equals(glass) || leggings.getMaterial().equals(endonium) || leggings.getMaterial().equals(endoniumCrystal)) &&
               (boots.getMaterial().equals(glass) || boots.getMaterial().equals(endonium) || boots.getMaterial().equals(endoniumCrystal));
    }

    private boolean playerHasFullMechaniCraftArmor(Player player) {

        ItemStack helmet = player.getInventory().getArmor(3);
        ItemStack chestplate = player.getInventory().getArmor(2);
        ItemStack leggings = player.getInventory().getArmor(1);
        ItemStack boots = player.getInventory().getArmor(0);

        return (!helmet.isEmpty() && helmet.getItem() instanceof MechaniCraftArmorItem) &&
               (!chestplate.isEmpty() && chestplate.getItem() instanceof MechaniCraftArmorItem) &&
               (!leggings.isEmpty() && leggings.getItem() instanceof MechaniCraftArmorItem) &&
               (!boots.isEmpty() && boots.getItem() instanceof MechaniCraftArmorItem);
    }
}