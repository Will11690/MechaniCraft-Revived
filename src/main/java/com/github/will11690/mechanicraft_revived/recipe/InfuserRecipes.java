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

public class InfuserRecipes implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final ItemStack output;
    private final Ingredient input1;
    private final Ingredient input2;

    public InfuserRecipes(ResourceLocation id, Ingredient input1, Ingredient input2, ItemStack output) {

        this.id = id;
        this.input1 = input1;
        this.input2 = input2;
        this.output = output;
    }

    @Override
    public boolean matches(SimpleContainer inv, Level level) {

        if(level.isClientSide()) {

            return false;
        }

        if(this.input1.test(inv.getItem(0))) {

            return this.input2.test(inv.getItem(1));
        }

        if(this.input2.test(inv.getItem(0))) {

            return this.input1.test(inv.getItem(1));
        }
        return false;
    }

    @Override
    public ItemStack assemble(SimpleContainer inv, RegistryAccess access) {

        return this.output.copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {

        return this.output;
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

        return InfuserSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {

        return InfuserType.INSTANCE;
    }

    public ItemStack makeIcon() {

        return new ItemStack(MechaniCraftBlocks.PrimitiveInfuser.get());
    }

    public static class InfuserType implements RecipeType<InfuserRecipes> {

        private InfuserType() {}
        public static final InfuserType INSTANCE = new InfuserType();
        public static final String ID = "infuser_recipes";
    }

    public static class InfuserSerializer implements RecipeSerializer<InfuserRecipes> {

        public static final InfuserSerializer INSTANCE = new InfuserSerializer();
        public static final ResourceLocation ID = new ResourceLocation(MechaniCraftMain.MODID, "infuser_recipes");

        @Override
        public InfuserRecipes fromJson(ResourceLocation id, JsonObject json) {

            final JsonElement input1El = GsonHelper.isArrayNode(json, "input1") ? GsonHelper.getAsJsonArray(json, "input1") : GsonHelper.getAsJsonObject(json, "input1");
            final JsonElement input2El = GsonHelper.isArrayNode(json, "input2") ? GsonHelper.getAsJsonArray(json, "input2") : GsonHelper.getAsJsonObject(json, "input2");
            final Ingredient input1 = Ingredient.fromJson(input1El);
            final Ingredient input2 = Ingredient.fromJson(input2El);

            final ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));

            return new InfuserRecipes(id, input1, input2, output);
        }

        @Nullable
        @Override
        public InfuserRecipes fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {

            final Ingredient input1 = Ingredient.fromNetwork(buffer);
            final Ingredient input2 = Ingredient.fromNetwork(buffer);

            final ItemStack output = buffer.readItem();

            return new InfuserRecipes(recipeId, input1, input2, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, InfuserRecipes recipe) {

            recipe.input1.toNetwork(buffer);
            recipe.input2.toNetwork(buffer);

            buffer.writeItem(recipe.output);
        }
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}