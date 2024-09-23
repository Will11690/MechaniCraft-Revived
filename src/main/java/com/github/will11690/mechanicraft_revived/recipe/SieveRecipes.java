package com.github.will11690.mechanicraft_revived.recipe;

import com.github.will11690.mechanicraft_revived.MechaniCraftMain;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlocks;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class SieveRecipes implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final ItemStack output;
    private final ItemStack secondaryOutput;
    private final Ingredient input1;
    private final Ingredient input2;
    private int secondaryChanceMin = 1;
    private final int secondaryChanceMax;
    private boolean secondaryOutputChance;

    int randomWithRange(int min, int max) {

        min = secondaryChanceMin;

        if(secondaryChanceMax < 2) {

            max = 2;

        } else {

            max = secondaryChanceMax;
        }

        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }

    public void secondaryChance() {

        int min = secondaryChanceMin;
        int max;

        if(secondaryChanceMax < 2) {

            max = 2;
        }

        if(secondaryChanceMax > 100) {

            max = 100;

        } else {

            max = secondaryChanceMax;
        }

        int rand = randomWithRange(min, max);

        if(rand == 1) {

            secondaryOutputChance = true;

        } else secondaryOutputChance = false;
    }

    public SieveRecipes(ResourceLocation id, Ingredient input1, Ingredient input2, ItemStack output, ItemStack secondaryOutput, int secondaryChanceMax) {

        this.id = id;
        this.input1 = input1;
        this.input2 = input2;
        this.output = output;
        this.secondaryOutput = secondaryOutput;
        this.secondaryChanceMax = secondaryChanceMax;
    }

    @Override
    public boolean matches(SimpleContainer inv, Level world) {

        if(this.input1.test(inv.getItem(0))) {

            return this.input2.test(inv.getItem(1));
        }

        if(this.input2.test(inv.getItem(1))) {

            return this.input1.test(inv.getItem(0));
        }
        return false;
    }

    @Override
    public ItemStack assemble(SimpleContainer inv, RegistryAccess access) {

        secondaryChance();

        return this.output.copy();
    }

    public ItemStack assembleSecondary(SimpleContainer inv) {

        if(secondaryOutputChance == true) {

            return this.secondaryOutput.copy();
        }
        else return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {

        return this.output;
    }

    public ItemStack getSecondaryResultItem() {

        if(secondaryOutputChance == true) {

            return this.secondaryOutput;

        } else return ItemStack.EMPTY;
    }

    public ItemStack getSecondaryResultJEI() {

        return this.secondaryOutput;
    }

    public Ingredient getInput1() {
        return this.input1;
    }

    public Ingredient getInput2() {
        return this.input2;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {

        NonNullList<Ingredient> nonnulllist = NonNullList.create();

        nonnulllist.add(this.input1);
        nonnulllist.add(this.input2);

        return nonnulllist;
    }

    @Override
    public ResourceLocation getId() {

        return this.id;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {

        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {

        return SieveSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {

        return SieveType.INSTANCE;
    }

    public ItemStack makeIcon() {

        //TODO update when rest of machines added
        return new ItemStack(MechaniCraftBlocks.MachineBlock.get());
    }

    public static class SieveType implements RecipeType<SieveRecipes> {

        private SieveType() {}
        public static final SieveType INSTANCE = new SieveType();
        public static final String ID = "sieve_recipes";
    }

    public static class SieveSerializer implements RecipeSerializer<SieveRecipes> {

        public static final SieveSerializer INSTANCE = new SieveSerializer();
        public static final ResourceLocation ID = new ResourceLocation(MechaniCraftMain.MODID, "sieve_recipes");

        @Override
        public SieveRecipes fromJson(ResourceLocation id, JsonObject json) {

            final JsonElement input1El = GsonHelper.isArrayNode(json, "input1") ? GsonHelper.getAsJsonArray(json, "input1") : GsonHelper.getAsJsonObject(json, "input1");
            final JsonElement input2El = GsonHelper.isArrayNode(json, "input2") ? GsonHelper.getAsJsonArray(json, "input2") : GsonHelper.getAsJsonObject(json, "input2");
            final Ingredient input1 = Ingredient.fromJson(input1El);
            final Ingredient input2 = Ingredient.fromJson(input2El);

            final ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));
            final ItemStack secondaryOutput = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "secondaryOutput"));
            final int secondaryChanceMax = GsonHelper.getAsInt(json.get("secondaryOutput").getAsJsonObject(), "weight", 0);

            return new SieveRecipes(id, input1, input2, output, secondaryOutput, secondaryChanceMax);
        }

        @Nullable
        @Override
        public SieveRecipes fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {

            final Ingredient input1 = Ingredient.fromNetwork(buffer);
            final Ingredient input2 = Ingredient.fromNetwork(buffer);

            final ItemStack output = buffer.readItem();
            final ItemStack secondaryOutput = buffer.readItem();
            final int secondaryChanceMax = buffer.readInt();

            return new SieveRecipes(recipeId, input1, input2, output, secondaryOutput, secondaryChanceMax);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SieveRecipes recipe) {

            recipe.input1.toNetwork(buffer);
            recipe.input2.toNetwork(buffer);

            buffer.writeItem(recipe.output);
            buffer.writeItem(recipe.secondaryOutput);
            buffer.writeInt(recipe.secondaryChanceMax);
        }
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}