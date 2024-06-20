package com.progwml6.ironshulkerbox.common.registraton;

import com.google.common.collect.ImmutableMap;
import com.progwml6.ironshulkerbox.IronShulkerBoxes;
import com.progwml6.ironshulkerbox.common.item.IronShulkerBoxUpgradeItem;
import com.progwml6.ironshulkerbox.common.item.IronShulkerBoxesUpgradeType;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class IronShulkerBoxesItems {

  public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(IronShulkerBoxes.MODID);

  public static final ImmutableMap<IronShulkerBoxesUpgradeType, DeferredItem<IronShulkerBoxUpgradeItem>> UPGRADES = ImmutableMap.copyOf(Arrays.stream(IronShulkerBoxesUpgradeType.values()).collect(Collectors.toMap(Function.identity(), type -> register(type.name().toLowerCase(Locale.ROOT) + "_shulker_box_upgrade", () -> new IronShulkerBoxUpgradeItem(type, new Item.Properties().stacksTo(1))))));

  private static <T extends Item> DeferredItem<T> register(final String name, final Supplier<T> sup) {
    return ITEMS.register(name, sup);
  }
}
