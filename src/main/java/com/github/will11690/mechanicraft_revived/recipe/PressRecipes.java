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

public class PressRecipes implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final ItemStack output;
    private final Ingredient input1;
    private final Ingredient input2;
    private final Ingredient input3;
    private final Ingredient input4;
    private final Ingredient input5;
    private final Ingredient input6;
    private final Ingredient input7;
    private final Ingredient input8;
    private final Ingredient input9;

    public PressRecipes(ResourceLocation id, Ingredient input1, Ingredient input2, Ingredient input3, Ingredient input4, Ingredient input5, Ingredient input6, Ingredient input7, Ingredient input8, Ingredient input9, ItemStack output) {

        this.id = id;
        this.input1 = input1;
        this.input2 = input2;
        this.input3 = input3;
        this.input4 = input4;
        this.input5 = input5;
        this.input6 = input6;
        this.input7 = input7;
        this.input8 = input8;
        this.input9 = input9;
        this.output = output;
    }

    @Override
    public boolean matches(SimpleContainer inv, Level world) {

        if(this.input9.test(inv.getItem(8))) {

            return this.input1.test(inv.getItem(0)) && this.input2.test(inv.getItem(1)) && this.input3.test(inv.getItem(2)) && this.input4.test(inv.getItem(3)) &&
                    this.input5.test(inv.getItem(4)) && this.input6.test(inv.getItem(5)) && this.input7.test(inv.getItem(6)) && this.input8.test(inv.getItem(7));
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

    public Ingredient getInput3() {

        return this.input3;
    }

    public Ingredient getInput4() {

        return this.input4;
    }

    public Ingredient getInput5() {

        return this.input5;
    }

    public Ingredient getInput6() {

        return this.input6;
    }

    public Ingredient getInput7() {

        return this.input7;
    }

    public Ingredient getInput8() {

        return this.input8;
    }

    public Ingredient getInput9() {

        return this.input9;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {

        NonNullList<Ingredient> nonnulllist = NonNullList.create();

        nonnulllist.add(this.input1);
        nonnulllist.add(this.input2);
        nonnulllist.add(this.input3);
        nonnulllist.add(this.input4);
        nonnulllist.add(this.input5);
        nonnulllist.add(this.input6);
        nonnulllist.add(this.input7);
        nonnulllist.add(this.input8);
        nonnulllist.add(this.input9);

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

        return PressSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {

        return PressType.INSTANCE;
    }

    public ItemStack makeIcon() {

        //TODO update when rest of machines added
        return new ItemStack(MechaniCraftBlocks.MachineBlock.get());
    }

    public static class PressType implements RecipeType<PressRecipes> {

        private PressType() {}
        public static final PressType INSTANCE = new PressType();
        public static final String ID = "press_recipes";
    }

    public static class PressSerializer implements RecipeSerializer<PressRecipes> {

        public static final PressSerializer INSTANCE = new PressSerializer();
        public static final ResourceLocation ID = new ResourceLocation(MechaniCraftMain.MODID, "press_recipes");

        @Override
        public PressRecipes fromJson(ResourceLocation id, JsonObject json) {

            final JsonElement input1El = GsonHelper.isArrayNode(json, "input1") ? GsonHelper.getAsJsonArray(json, "input1") : GsonHelper.getAsJsonObject(json, "input1");
            final JsonElement input2El = GsonHelper.isArrayNode(json, "input2") ? GsonHelper.getAsJsonArray(json, "input2") : GsonHelper.getAsJsonObject(json, "input2");
            final JsonElement input3El = GsonHelper.isArrayNode(json, "input3") ? GsonHelper.getAsJsonArray(json, "input3") : GsonHelper.getAsJsonObject(json, "input3");
            final JsonElement input4El = GsonHelper.isArrayNode(json, "input4") ? GsonHelper.getAsJsonArray(json, "input4") : GsonHelper.getAsJsonObject(json, "input4");
            final JsonElement input5El = GsonHelper.isArrayNode(json, "input5") ? GsonHelper.getAsJsonArray(json, "input5") : GsonHelper.getAsJsonObject(json, "input5");
            final JsonElement input6El = GsonHelper.isArrayNode(json, "input6") ? GsonHelper.getAsJsonArray(json, "input6") : GsonHelper.getAsJsonObject(json, "input6");
            final JsonElement input7El = GsonHelper.isArrayNode(json, "input7") ? GsonHelper.getAsJsonArray(json, "input7") : GsonHelper.getAsJsonObject(json, "input7");
            final JsonElement input8El = GsonHelper.isArrayNode(json, "input8") ? GsonHelper.getAsJsonArray(json, "input8") : GsonHelper.getAsJsonObject(json, "input8");
            final JsonElement input9El = GsonHelper.isArrayNode(json, "input9") ? GsonHelper.getAsJsonArray(json, "input9") : GsonHelper.getAsJsonObject(json, "input9");
            final Ingredient input1 = Ingredient.fromJson(input1El);
            final Ingredient input2 = Ingredient.fromJson(input2El);
            final Ingredient input3 = Ingredient.fromJson(input3El);
            final Ingredient input4 = Ingredient.fromJson(input4El);
            final Ingredient input5 = Ingredient.fromJson(input5El);
            final Ingredient input6 = Ingredient.fromJson(input6El);
            final Ingredient input7 = Ingredient.fromJson(input7El);
            final Ingredient input8 = Ingredient.fromJson(input8El);
            final Ingredient input9 = Ingredient.fromJson(input9El);

            final ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));

            return new PressRecipes(id, input1, input2, input3, input4, input5, input6, input7, input8, input9, output);
        }

        @Nullable
        @Override
        public PressRecipes fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {

            final Ingredient input1 = Ingredient.fromNetwork(buffer);
            final Ingredient input2 = Ingredient.fromNetwork(buffer);
            final Ingredient input3 = Ingredient.fromNetwork(buffer);
            final Ingredient input4 = Ingredient.fromNetwork(buffer);
            final Ingredient input5 = Ingredient.fromNetwork(buffer);
            final Ingredient input6 = Ingredient.fromNetwork(buffer);
            final Ingredient input7 = Ingredient.fromNetwork(buffer);
            final Ingredient input8 = Ingredient.fromNetwork(buffer);
            final Ingredient input9 = Ingredient.fromNetwork(buffer);

            final ItemStack output = buffer.readItem();

            return new PressRecipes(recipeId, input1, input2, input3, input4, input5, input6, input7, input8, input9, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, PressRecipes recipe) {

            recipe.input1.toNetwork(buffer);
            recipe.input2.toNetwork(buffer);
            recipe.input3.toNetwork(buffer);
            recipe.input4.toNetwork(buffer);
            recipe.input5.toNetwork(buffer);
            recipe.input6.toNetwork(buffer);
            recipe.input7.toNetwork(buffer);
            recipe.input8.toNetwork(buffer);
            recipe.input9.toNetwork(buffer);

            buffer.writeItem(recipe.output);
        }
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}