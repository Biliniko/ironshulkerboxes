package com.progwml6.ironshulkerbox.common.registraton;

import com.progwml6.ironshulkerbox.IronShulkerBoxes;
import com.progwml6.ironshulkerbox.common.inventory.IronShulkerBoxMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class IronShulkerBoxesMenuTypes {

  public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, IronShulkerBoxes.MODID);

  public static final DeferredHolder<MenuType<?>, MenuType<IronShulkerBoxMenu>> IRON_SHULKER_BOX = MENU_TYPES.register("iron_shulker_box", () -> new MenuType<>(IronShulkerBoxMenu::createIronContainer, FeatureFlags.REGISTRY.allFlags()));
  public static final DeferredHolder<MenuType<?>, MenuType<IronShulkerBoxMenu>> GOLD_SHULKER_BOX = MENU_TYPES.register("gold_shulker_box", () -> new MenuType<>(IronShulkerBoxMenu::createGoldContainer, FeatureFlags.REGISTRY.allFlags()));
  public static final DeferredHolder<MenuType<?>, MenuType<IronShulkerBoxMenu>> DIAMOND_SHULKER_BOX = MENU_TYPES.register("diamond_shulker_box", () -> new MenuType<>(IronShulkerBoxMenu::createDiamondContainer, FeatureFlags.REGISTRY.allFlags()));
  public static final DeferredHolder<MenuType<?>, MenuType<IronShulkerBoxMenu>> CRYSTAL_SHULKER_BOX = MENU_TYPES.register("crystal_shulker_box", () -> new MenuType<>(IronShulkerBoxMenu::createCrystalContainer, FeatureFlags.REGISTRY.allFlags()));
  public static final DeferredHolder<MenuType<?>, MenuType<IronShulkerBoxMenu>> COPPER_SHULKER_BOX = MENU_TYPES.register("copper_shulker_box", () -> new MenuType<>(IronShulkerBoxMenu::createCopperContainer, FeatureFlags.REGISTRY.allFlags()));
  public static final DeferredHolder<MenuType<?>, MenuType<IronShulkerBoxMenu>> OBSIDIAN_SHULKER_BOX = MENU_TYPES.register("obsidian_shulker_box", () -> new MenuType<>(IronShulkerBoxMenu::createObsidianContainer, FeatureFlags.REGISTRY.allFlags()));
}
