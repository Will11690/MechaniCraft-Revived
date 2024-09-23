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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public class WasherRecipes implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final FluidStack outputFluid;
    private final Ingredient inputStack;
    private final FluidStack inputFluid;

    public WasherRecipes(ResourceLocation id, Ingredient inputStack, FluidStack inputFluid, FluidStack outputFluid) {

        this.id = id;
        this.inputStack = inputStack;
        this.inputFluid = inputFluid;
        this.outputFluid = outputFluid;
    }

    @Override
    public boolean matches(SimpleContainer inv, Level world) {

        if(this.inputStack.test(inv.getItem(0))) {

            return true;
        }
        return false;
    }

    public FluidStack assembleFluid(FluidTank tank, ItemStackHandler stackHandler) {

        if(tank.getFluid().equals(inputFluid) && inputStack.test(stackHandler.getStackInSlot(0))) {

            return this.outputFluid.copy();
        }
        return FluidStack.EMPTY;
    }

    public FluidStack getResultFluid() {

        return this.outputFluid;
    }

    public Ingredient getInputStack() {
        return this.inputStack;
    }

    public FluidStack getInputFluid() {
        return this.inputFluid;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {

        NonNullList<Ingredient> nonnulllist = NonNullList.create();

        nonnulllist.add(this.inputStack);

        return nonnulllist;
    }

    public Map<FluidStack, Integer> getInputFluids() {

        Map<FluidStack, Integer> inputFluids = new LinkedHashMap<>();

        inputFluids.put(inputFluid, inputFluid.getAmount());

        return inputFluids;
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

        return WasherSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {

        return WasherType.INSTANCE;
    }

    public ItemStack makeIcon() {

        //TODO update when rest of machines added
        return new ItemStack(MechaniCraftBlocks.MachineBlock.get());
    }

    public static class WasherType implements RecipeType<WasherRecipes> {

        private WasherType() {}
        public static final WasherType INSTANCE = new WasherType();
        public static final String ID = "washer_recipes";
    }

    public static class WasherSerializer implements RecipeSerializer<WasherRecipes> {

        public static final WasherSerializer INSTANCE = new WasherSerializer();
        public static final ResourceLocation ID = new ResourceLocation(MechaniCraftMain.MODID, "washer_recipes");

        @Override
        public WasherRecipes fromJson(ResourceLocation id, JsonObject json) {

            final JsonElement inputStackEl = GsonHelper.isArrayNode(json, "inputStack") ? GsonHelper.getAsJsonArray(json, "inputStack") : GsonHelper.getAsJsonObject(json, "inputStack");

            ResourceLocation inputFluidResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("inputFluid").getAsJsonObject(), "fluid", "minecraft:empty"), ':');
            int inputFluidAmount = GsonHelper.getAsInt(json.get("inputFluid").getAsJsonObject(), "amount", 0);

            ResourceLocation outputFluidResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("outputFluid").getAsJsonObject(), "fluid", "minecraft:empty"), ':');
            int outputFluidAmount = GsonHelper.getAsInt(json.get("outputFluid").getAsJsonObject(), "amount", 0);

            final Ingredient inputStack = Ingredient.fromJson(inputStackEl);
            final FluidStack inputFluid = new FluidStack(ForgeRegistries.FLUIDS.getValue(inputFluidResourceLocation), inputFluidAmount);
            final FluidStack outputFluid = new FluidStack(ForgeRegistries.FLUIDS.getValue(outputFluidResourceLocation), outputFluidAmount);

            return new WasherRecipes(id, inputStack, inputFluid, outputFluid);
        }

        @Nullable
        @Override
        public WasherRecipes fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {

            final Ingredient inputStack = Ingredient.fromNetwork(buffer);
            final FluidStack inputFluid = buffer.readFluidStack();

            final FluidStack outputFluid = buffer.readFluidStack();

            return new WasherRecipes(recipeId, inputStack, inputFluid, outputFluid);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, WasherRecipes recipe) {

            recipe.inputStack.toNetwork(buffer);
            buffer.writeFluidStack(recipe.getInputFluid());

            buffer.writeFluidStack(recipe.outputFluid);
        }
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    /**
     * EXISTS ONLY TO REMOVE ERRORS
     * MACHINE HAS NO ITEMSTACK OUTPUT
     * DO NOT USE
     * ALWAYS RETURNS ItemStack.EMPTY
     */
    @Override
    public ItemStack assemble(SimpleContainer inv, RegistryAccess access){return ItemStack.EMPTY;}

    /**
     * EXISTS ONLY TO REMOVE ERRORS
     * MACHINE HAS NO ITEMSTACK OUTPUT
     * DO NOT USE
     * ALWAYS RETURNS ItemStack.EMPTY
     */
    @Override
    public ItemStack getResultItem(RegistryAccess access){return ItemStack.EMPTY;}
}