package com.progwml6.ironshulkerbox.common.registraton;

import com.google.common.collect.ImmutableMap;
import com.progwml6.ironshulkerbox.IronShulkerBoxes;
import com.progwml6.ironshulkerbox.common.block.CopperShulkerBoxBlock;
import com.progwml6.ironshulkerbox.common.block.CrystalShulkerBoxBlock;
import com.progwml6.ironshulkerbox.common.block.DiamondShulkerBoxBlock;
import com.progwml6.ironshulkerbox.common.block.GoldShulkerBoxBlock;
import com.progwml6.ironshulkerbox.common.block.IronShulkerBoxBlock;
import com.progwml6.ironshulkerbox.common.block.IronShulkerBoxesTypes;
import com.progwml6.ironshulkerbox.common.block.ObsidianShulkerBoxBlock;
import com.progwml6.ironshulkerbox.common.block.entity.AbstractIronShulkerBoxBlockEntity;
import com.progwml6.ironshulkerbox.common.item.IronShulkerBoxBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class IronShulkerBoxesBlocks {

  public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(IronShulkerBoxes.MODID);

  public static final DeferredRegister.Items ITEMS = IronShulkerBoxesItems.ITEMS;

  static BlockBehaviour.StatePredicate positionPredicate = (state, level, pos) -> {
    BlockEntity blockEntity = level.getBlockEntity(pos);

    if (!(blockEntity instanceof AbstractIronShulkerBoxBlockEntity shulkerBoxBlockEntity)) {
      return true;
    } else {
      return shulkerBoxBlockEntity.isClosed();
    }
  };

  private static final BlockBehaviour.Properties STANDARD = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F).dynamicShape().noOcclusion().isSuffocating(positionPredicate).isViewBlocking(positionPredicate).pushReaction(PushReaction.DESTROY);
  private static final BlockBehaviour.Properties REINFORCED = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 10000.0F).dynamicShape().noOcclusion().isSuffocating(positionPredicate).isViewBlocking(positionPredicate).pushReaction(PushReaction.DESTROY);

  //Default uncolored
  public static final DeferredBlock<IronShulkerBoxBlock> IRON_SHULKER_BOX = register("iron_shulker_box", () -> new IronShulkerBoxBlock(STANDARD, null), IronShulkerBoxesTypes.IRON, null);
  public static final DeferredBlock<GoldShulkerBoxBlock> GOLD_SHULKER_BOX = register("gold_shulker_box", () -> new GoldShulkerBoxBlock(STANDARD, null), IronShulkerBoxesTypes.GOLD, null);
  public static final DeferredBlock<DiamondShulkerBoxBlock> DIAMOND_SHULKER_BOX = register("diamond_shulker_box", () -> new DiamondShulkerBoxBlock(STANDARD, null), IronShulkerBoxesTypes.DIAMOND, null);
  public static final DeferredBlock<CopperShulkerBoxBlock> COPPER_SHULKER_BOX = register("copper_shulker_box", () -> new CopperShulkerBoxBlock(STANDARD, null), IronShulkerBoxesTypes.COPPER, null);
  public static final DeferredBlock<CrystalShulkerBoxBlock> CRYSTAL_SHULKER_BOX = register("crystal_shulker_box", () -> new CrystalShulkerBoxBlock(STANDARD, null), IronShulkerBoxesTypes.CRYSTAL, null);
  public static final DeferredBlock<ObsidianShulkerBoxBlock> OBSIDIAN_SHULKER_BOX = register("obsidian_shulker_box", () -> new ObsidianShulkerBoxBlock(REINFORCED, null), IronShulkerBoxesTypes.OBSIDIAN, null);

  public static final ImmutableMap<DyeColor, DeferredBlock<IronShulkerBoxBlock>> IRON_SHULKER_BOXES = ImmutableMap.copyOf(Arrays.stream(DyeColor.values()).collect(Collectors.toMap(Function.identity(), type -> register("iron_shulker_box_" + type.name().toLowerCase(Locale.ROOT), () -> new IronShulkerBoxBlock(STANDARD, type), IronShulkerBoxesTypes.IRON, type))));
  public static final ImmutableMap<DyeColor, DeferredBlock<GoldShulkerBoxBlock>> GOLD_SHULKER_BOXES = ImmutableMap.copyOf(Arrays.stream(DyeColor.values()).collect(Collectors.toMap(Function.identity(), type -> register("gold_shulker_box_" + type.name().toLowerCase(Locale.ROOT), () -> new GoldShulkerBoxBlock(STANDARD, type), IronShulkerBoxesTypes.GOLD, type))));
  public static final ImmutableMap<DyeColor, DeferredBlock<DiamondShulkerBoxBlock>> DIAMOND_SHULKER_BOXES = ImmutableMap.copyOf(Arrays.stream(DyeColor.values()).collect(Collectors.toMap(Function.identity(), type -> register("diamond_shulker_box_" + type.name().toLowerCase(Locale.ROOT), () -> new DiamondShulkerBoxBlock(STANDARD, type), IronShulkerBoxesTypes.DIAMOND, type))));
  public static final ImmutableMap<DyeColor, DeferredBlock<CopperShulkerBoxBlock>> COPPER_SHULKER_BOXES = ImmutableMap.copyOf(Arrays.stream(DyeColor.values()).collect(Collectors.toMap(Function.identity(), type -> register("copper_shulker_box_" + type.name().toLowerCase(Locale.ROOT), () -> new CopperShulkerBoxBlock(STANDARD, type), IronShulkerBoxesTypes.COPPER, type))));
  public static final ImmutableMap<DyeColor, DeferredBlock<CrystalShulkerBoxBlock>> CRYSTAL_SHULKER_BOXES = ImmutableMap.copyOf(Arrays.stream(DyeColor.values()).collect(Collectors.toMap(Function.identity(), type -> register("crystal_shulker_box_" + type.name().toLowerCase(Locale.ROOT), () -> new CrystalShulkerBoxBlock(STANDARD, type), IronShulkerBoxesTypes.CRYSTAL, type))));
  public static final ImmutableMap<DyeColor, DeferredBlock<ObsidianShulkerBoxBlock>> OBSIDIAN_SHULKER_BOXES = ImmutableMap.copyOf(Arrays.stream(DyeColor.values()).collect(Collectors.toMap(Function.identity(), type -> register("obsidian_shulker_box_" + type.name().toLowerCase(Locale.ROOT), () -> new ObsidianShulkerBoxBlock(REINFORCED, type), IronShulkerBoxesTypes.OBSIDIAN, type))));

  //HELPERS
  private static <T extends Block> DeferredBlock<T> register(String name, Supplier<? extends T> sup, IronShulkerBoxesTypes shulkerBoxesType, @Nullable DyeColor color) {
    return register(name, sup, block -> item(block, shulkerBoxesType, color));
  }

  private static <T extends Block> DeferredBlock<T> register(String name, Supplier<? extends T> sup, Function<DeferredBlock<T>, Supplier<? extends Item>> itemCreator) {
    DeferredBlock<T> ret = registerNoItem(name, sup);
    ITEMS.register(name, itemCreator.apply(ret));
    return ret;
  }

  private static <T extends Block> DeferredBlock<T> registerNoItem(String name, Supplier<? extends T> sup) {
    return BLOCKS.register(name, sup);
  }

  private static Supplier<BlockItem> item(final DeferredBlock<? extends Block> block, IronShulkerBoxesTypes shulkerBoxesType, @Nullable DyeColor color) {
    return () -> new IronShulkerBoxBlockItem(block.get(), new Item.Properties(), shulkerBoxesType, color);
  }
}
