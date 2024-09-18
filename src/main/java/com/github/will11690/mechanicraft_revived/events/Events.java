package com.github.will11690.mechanicraft_revived.events;

import com.github.will11690.mechanicraft_revived.MechaniCraftMain;
import com.github.will11690.mechanicraft_revived.items.armor.MechaniCraftArmorItem;
import com.github.will11690.mechanicraft_revived.items.armor.MechaniCraftArmorMaterials;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MechaniCraftMain.MODID)
public class Events {

    //TODO single flight item
    //TODO move as many armor potion effects to LivingEquipmentChange stat changes instead

    @SubscribeEvent
    public static void equipmentChange(LivingEquipmentChangeEvent event) {

        if (event.getEntity() instanceof Player player) {

            ItemStack helmetStack = player.getInventory().getArmor(3);
            ItemStack chestplateStack = player.getInventory().getArmor(2);
            ItemStack leggingsStack = player.getInventory().getArmor(1);
            ItemStack bootsStack = player.getInventory().getArmor(0);

            if(!player.isCreative() && !player.isSpectator()) {

                player.getAbilities().mayfly =
                        helmetStack.getItem() instanceof MechaniCraftArmorItem &&
                        ((MechaniCraftArmorItem) helmetStack.getItem()).getMaterial().equals(MechaniCraftArmorMaterials.ENDONIUM_CRYSTAL) &&
                        chestplateStack.getItem() instanceof MechaniCraftArmorItem &&
                        ((MechaniCraftArmorItem) chestplateStack.getItem()).getMaterial().equals(MechaniCraftArmorMaterials.ENDONIUM_CRYSTAL) &&
                        leggingsStack.getItem() instanceof MechaniCraftArmorItem &&
                        ((MechaniCraftArmorItem) leggingsStack.getItem()).getMaterial().equals(MechaniCraftArmorMaterials.ENDONIUM_CRYSTAL) &&
                        bootsStack.getItem() instanceof MechaniCraftArmorItem &&
                        ((MechaniCraftArmorItem) bootsStack.getItem()).getMaterial().equals(MechaniCraftArmorMaterials.ENDONIUM_CRYSTAL);

                if (!player.getAbilities().mayfly && player.getAbilities().flying) {

                    player.getPersistentData().putBoolean("wasFlying", true);
                    player.getAbilities().flying = false;
                }

                player.onUpdateAbilities();
            }
        }
    }

    @SubscribeEvent
    public static void logInAddNBT(PlayerEvent.PlayerLoggedInEvent event) {

        if(event.getEntity() instanceof Player) {

            Player player = event.getEntity();
            CompoundTag playerData = player.getPersistentData();

            if(playerData.get("wasFlying") == null) {

                playerData.putBoolean("wasFlying", false);
                player.onUpdateAbilities();
            }
        }
    }

    @SubscribeEvent
    public static void respawnAddNBT(PlayerEvent.PlayerRespawnEvent event) {

        if(event.getEntity() instanceof Player) {

            Player player = event.getEntity();
            CompoundTag playerData = player.getPersistentData();
            Tag wasFlying = playerData.get("wasFlying");

            if(wasFlying == null) {

                playerData.putBoolean("wasFlying", false);
                player.onUpdateAbilities();
            }
        }
    }

    @SubscribeEvent
    public static void cancelFlyingFallDamage(LivingFallEvent event) {

        if(event.getEntity() instanceof Player player) {

            if(player.getPersistentData().contains("wasFlying")) {

                if(player.getPersistentData().getBoolean("wasFlying")) {

                    player.getPersistentData().putBoolean("wasFlying", false);
                    player.onUpdateAbilities();
                    event.setCanceled(true);
                }
            }
        }
    }
}