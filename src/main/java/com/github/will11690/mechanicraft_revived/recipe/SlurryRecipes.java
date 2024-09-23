package com.github.will11690.mechanicraft_revived.recipe;

import com.github.will11690.mechanicraft_revived.MechaniCraftMain;
import com.github.will11690.mechanicraft_revived.registry.MechaniCraftBlocks;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public class SlurryRecipes implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final ItemStack outputStack;
    private final FluidStack outputFluid;
    private final FluidStack inputFluid1;
    private final FluidStack inputFluid2;

    public SlurryRecipes(ResourceLocation id, FluidStack inputFluid1, FluidStack inputFluid2, ItemStack outputStack, FluidStack outputFluid) {

        this.id = id;
        this.inputFluid1 = inputFluid1;
        this.inputFluid2 = inputFluid2;
        this.outputStack = outputStack;
        this.outputFluid = outputFluid;
    }

    public boolean matchesFluid(FluidStack input1, FluidStack input2) {

        if(input1.containsFluid(inputFluid1) && input2.containsFluid(inputFluid2)) {

            return true;

        } else return false;
    }

    @Override
    public boolean matches(SimpleContainer inv, Level level) {

        ItemStack fluidBucket1 = new ItemStack(inputFluid1.getFluid().getBucket());
        ItemStack fluidBucket2 = new ItemStack(inputFluid2.getFluid().getBucket());

        if(Ingredient.of(fluidBucket1).test(inv.getItem(0))) {

            return Ingredient.of(fluidBucket2).test(inv.getItem(1));
        } else return false;
    }

    public ItemStack assembleStack(FluidStack input1, FluidStack input2) {

        if(matchesFluid(input1, input2)) {

            return this.outputStack.copy();

        } else

            return ItemStack.EMPTY;
    }

    public FluidStack assembleFluid(FluidStack input1, FluidStack input2) {

        if(matchesFluid(input1, input2)) {

            return this.outputFluid.copy();

        } else

            return FluidStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {

        return this.outputStack;
    }

    public FluidStack getResultFluid() {

        return this.outputFluid;
    }

    public FluidStack getResultFluidWithInputs(FluidStack input1, FluidStack input2) {

        if(input1.containsFluid(inputFluid1) && input2.containsFluid(inputFluid2))
            return this.outputFluid;

        return FluidStack.EMPTY;
    }

    public FluidStack getInputFluid1() {
        return this.inputFluid1;
    }

    public FluidStack getInputFluid2() {
        return this.inputFluid2;
    }

    public Map<FluidStack, Integer> getInputFluids() {

        Map<FluidStack, Integer> inputFluids = new LinkedHashMap<>();

        inputFluids.put(inputFluid1, inputFluid2.getAmount());
        inputFluids.put(inputFluid2, inputFluid2.getAmount());

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

        return SlurrySerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {

        return SlurryType.INSTANCE;
    }

    public ItemStack makeIcon() {

        //TODO update when rest of machines added
        return new ItemStack(MechaniCraftBlocks.MachineBlock.get());
    }

    public static class SlurryType implements RecipeType<SlurryRecipes> {

        private SlurryType() {}
        public static final SlurryType INSTANCE = new SlurryType();
        public static final String ID = "slurry_recipes";
    }

    public static class SlurrySerializer implements RecipeSerializer<SlurryRecipes> {

        public static final SlurrySerializer INSTANCE = new SlurrySerializer();
        public static final ResourceLocation ID = new ResourceLocation(MechaniCraftMain.MODID, "slurry_recipes");

        @Override
        public SlurryRecipes fromJson(ResourceLocation id, JsonObject json) {

            ResourceLocation inputFluid1ResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("inputFluid1").getAsJsonObject(), "fluid", "minecraft:empty"), ':');
            int inputFluid1Amount = GsonHelper.getAsInt(json.get("inputFluid1").getAsJsonObject(), "amount", 0);

            ResourceLocation inputFluid2ResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("inputFluid2").getAsJsonObject(), "fluid", "minecraft:empty"), ':');
            int inputFluid2Amount = GsonHelper.getAsInt(json.get("inputFluid2").getAsJsonObject(), "amount", 0);

            ResourceLocation outputFluidResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("outputFluid").getAsJsonObject(), "fluid", "minecraft:empty"), ':');
            int outputFluidAmount = GsonHelper.getAsInt(json.get("outputFluid").getAsJsonObject(), "amount", 0);

            final FluidStack inputFluid1 = new FluidStack(ForgeRegistries.FLUIDS.getValue(inputFluid1ResourceLocation), inputFluid1Amount);
            final FluidStack inputFluid2 = new FluidStack(ForgeRegistries.FLUIDS.getValue(inputFluid2ResourceLocation), inputFluid2Amount);

            final ItemStack outputStack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "outputStack"));
            final FluidStack outputFluid = new FluidStack(ForgeRegistries.FLUIDS.getValue(outputFluidResourceLocation), outputFluidAmount);

            return new SlurryRecipes(id, inputFluid1, inputFluid2, outputStack, outputFluid);
        }

        @Nullable
        @Override
        public SlurryRecipes fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {

            final FluidStack inputFluid1 = buffer.readFluidStack();
            final FluidStack inputFluid2 = buffer.readFluidStack();

            final ItemStack outputStack = buffer.readItem();
            final FluidStack outputFluid = buffer.readFluidStack();

            return new SlurryRecipes(recipeId, inputFluid1, inputFluid2, outputStack, outputFluid);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SlurryRecipes recipe) {

            buffer.writeFluidStack(recipe.getInputFluid1());
            buffer.writeFluidStack(recipe.getInputFluid2());

            buffer.writeItem(recipe.outputStack);
            buffer.writeFluidStack(recipe.outputFluid);
        }
    }

    /**
     * EXISTS ONLY TO REMOVE ERRORS
     * MACHINE HAS NO ITEMSTACK OUTPUT
     * DO NOT USE
     * ALWAYS RETURNS ItemStack.EMPTY
     */
    @Override
    public ItemStack assemble(SimpleContainer inv, RegistryAccess access){return ItemStack.EMPTY;}

    @Override
    public boolean isSpecial() {
        return true;
    }
}