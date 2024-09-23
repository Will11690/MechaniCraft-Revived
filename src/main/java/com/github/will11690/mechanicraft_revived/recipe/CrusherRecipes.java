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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class CrusherRecipes implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final ItemStack output;
    private final ItemStack secondaryOutput;
    private final Ingredient input;
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

    public CrusherRecipes(ResourceLocation id, Ingredient input, ItemStack output, ItemStack secondaryOutput, int secondaryChanceMax) {

        this.id = id;
        this.input = input;
        this.output = output;
        this.secondaryOutput = secondaryOutput;
        this.secondaryChanceMax = secondaryChanceMax;
    }

    @Override
    public boolean matches(SimpleContainer inv, Level level) {

        return this.input.test(inv.getItem(0));
    }

    @Override
    public ItemStack assemble(SimpleContainer inv, RegistryAccess access) {

        secondaryChance();

        return this.output.copy();
    }

    public ItemStack assembleSecondary(SimpleContainer inv) {

        if(secondaryOutputChance) {

            if(secondaryOutput.getItem().equals(Items.AIR)) {

                return ItemStack.EMPTY;

            } else return this.secondaryOutput.copy();
        }
        else return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {

        return this.output;
    }

    public ItemStack getSecondaryResultItem() {

        if(secondaryOutputChance) {

            return this.secondaryOutput;

        } else return ItemStack.EMPTY;
    }

    public ItemStack getSecondaryResultJEI() {

        return this.secondaryOutput;
    }

    public Ingredient getInput() {
        return this.input;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {

        NonNullList<Ingredient> nonnulllist = NonNullList.create();

        nonnulllist.add(this.input);

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

        return CrusherSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {

        return CrusherType.INSTANCE;
    }

    public ItemStack makeIcon() {

        //TODO update when rest of machines added
        return new ItemStack(MechaniCraftBlocks.MachineBlock.get());
    }

    public static class CrusherType implements RecipeType<CrusherRecipes> {

        private CrusherType() {}
        public static final CrusherType INSTANCE = new CrusherType();
        public static final String ID = "crusher_recipes";
    }

    public static class CrusherSerializer implements RecipeSerializer<CrusherRecipes> {

        public static final CrusherSerializer INSTANCE = new CrusherSerializer();
        public static final ResourceLocation ID = new ResourceLocation(MechaniCraftMain.MODID, "crusher_recipes");

        @Override
        public CrusherRecipes fromJson(ResourceLocation id, JsonObject json) {

            final JsonElement inputEl = GsonHelper.isArrayNode(json, "input") ? GsonHelper.getAsJsonArray(json, "input") : GsonHelper.getAsJsonObject(json, "input");
            final Ingredient input = Ingredient.fromJson(inputEl);

            final ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));
            final ItemStack secondaryOutput = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "secondaryOutput"));
            final int secondaryChanceMax = GsonHelper.getAsInt(json.get("secondaryOutput").getAsJsonObject(), "weight", 0);

            return new CrusherRecipes(id, input, output, secondaryOutput, secondaryChanceMax);
        }

        @Nullable
        @Override
        public CrusherRecipes fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {

            final Ingredient input = Ingredient.fromNetwork(buffer);

            final ItemStack output = buffer.readItem();
            final ItemStack secondaryOutput = buffer.readItem();
            final int secondaryChanceMax = buffer.readInt();

            return new CrusherRecipes(recipeId, input, output, secondaryOutput, secondaryChanceMax);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CrusherRecipes recipe) {

            recipe.input.toNetwork(buffer);

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