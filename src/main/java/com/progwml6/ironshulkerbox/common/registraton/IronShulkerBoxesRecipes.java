package com.progwml6.ironshulkerbox.common.registraton;

import com.progwml6.ironshulkerbox.IronShulkerBoxes;
import com.progwml6.ironshulkerbox.common.recipes.IronShulkerBoxesColoringRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class IronShulkerBoxesRecipes {

  public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, IronShulkerBoxes.MODID);

  public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<?>> SHULKER_BOX_COLORING = RECIPE_SERIALIZERS.register("shulker_box_coloring", () -> new SimpleCraftingRecipeSerializer<>(IronShulkerBoxesColoringRecipe::new));
}
