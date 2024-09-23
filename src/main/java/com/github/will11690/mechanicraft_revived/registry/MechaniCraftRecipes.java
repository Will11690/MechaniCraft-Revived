package com.github.will11690.mechanicraft_revived.registry;

import com.github.will11690.mechanicraft_revived.MechaniCraftMain;
import com.github.will11690.mechanicraft_revived.recipe.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MechaniCraftRecipes {

    //TODO update crusher, washer, press, sieve, slurry recipes as the blocks are added
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MechaniCraftMain.MODID);

    public static final RegistryObject<RecipeSerializer<CrusherRecipes>> CrusherRecipe =
            RECIPES.register("crusher_recipes", () -> CrusherRecipes.CrusherSerializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<InfuserRecipes>> InfuserRecipe =
            RECIPES.register("infuser_recipes", () -> InfuserRecipes.InfuserSerializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<PressRecipes>> PressRecipe =
            RECIPES.register("press_recipes", () -> PressRecipes.PressSerializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<SieveRecipes>> SieveRecipe =
            RECIPES.register("sieve_recipes", () -> SieveRecipes.SieveSerializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<SlurryRecipes>> SlurryRecipe =
            RECIPES.register("slurry_recipes", () -> SlurryRecipes.SlurrySerializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<WasherRecipes>> WasherRecipe =
            RECIPES.register("washer_recipes", () -> WasherRecipes.WasherSerializer.INSTANCE);

    public static void register(IEventBus eventBus) {

        RECIPES.register(eventBus);
    }
}
