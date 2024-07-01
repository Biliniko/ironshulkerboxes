package com.progwml6.ironshulkerbox.common.data.loot;

import com.google.common.collect.ImmutableSet;
import com.progwml6.ironshulkerbox.common.registraton.IronShulkerBoxesBlocks;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class IronShulkerBoxesBlockLoot extends BlockLootSubProvider {

  private static final Set<Item> EXPLOSION_RESISTANT = getExplosionResistance().stream().map(ItemLike::asItem).collect(Collectors.toSet());
  private final Set<Block> knownBlocks = new ReferenceOpenHashSet<>();

  public IronShulkerBoxesBlockLoot(HolderLookup.Provider provider) {
    super(EXPLOSION_RESISTANT, FeatureFlags.REGISTRY.allFlags(), provider);
  }

  @Override
  protected void generate() {
    this.add(IronShulkerBoxesBlocks.IRON_SHULKER_BOX.get(), this::createShulkerBoxDrop);
    this.add(IronShulkerBoxesBlocks.GOLD_SHULKER_BOX.get(), this::createShulkerBoxDrop);
    this.add(IronShulkerBoxesBlocks.DIAMOND_SHULKER_BOX.get(), this::createShulkerBoxDrop);
    this.add(IronShulkerBoxesBlocks.COPPER_SHULKER_BOX.get(), this::createShulkerBoxDrop);
    this.add(IronShulkerBoxesBlocks.CRYSTAL_SHULKER_BOX.get(), this::createShulkerBoxDrop);
    this.add(IronShulkerBoxesBlocks.OBSIDIAN_SHULKER_BOX.get(), this::createShulkerBoxDrop);

    for (DyeColor color : DyeColor.values()) {
      this.add(IronShulkerBoxesBlocks.IRON_SHULKER_BOXES.get(color).get(), this::createShulkerBoxDrop);
      this.add(IronShulkerBoxesBlocks.GOLD_SHULKER_BOXES.get(color).get(), this::createShulkerBoxDrop);
      this.add(IronShulkerBoxesBlocks.DIAMOND_SHULKER_BOXES.get(color).get(), this::createShulkerBoxDrop);
      this.add(IronShulkerBoxesBlocks.COPPER_SHULKER_BOXES.get(color).get(), this::createShulkerBoxDrop);
      this.add(IronShulkerBoxesBlocks.CRYSTAL_SHULKER_BOXES.get(color).get(), this::createShulkerBoxDrop);
      this.add(IronShulkerBoxesBlocks.OBSIDIAN_SHULKER_BOXES.get(color).get(), this::createShulkerBoxDrop);
    }
  }

  @Override
  protected void add(@NotNull Block block, @NotNull LootTable.Builder table) {
    //Overwrite the core register method to add to our list of known blocks
    super.add(block, table);
    knownBlocks.add(block);
  }

  @Override
  protected Iterable<Block> getKnownBlocks() {
    return knownBlocks;
  }

  protected static Set<Block> getExplosionResistance() {
    ImmutableSet.Builder<Block> blocks = new ImmutableSet.Builder<>();

    blocks.add(IronShulkerBoxesBlocks.IRON_SHULKER_BOX.get());
    blocks.add(IronShulkerBoxesBlocks.GOLD_SHULKER_BOX.get());
    blocks.add(IronShulkerBoxesBlocks.DIAMOND_SHULKER_BOX.get());
    blocks.add(IronShulkerBoxesBlocks.COPPER_SHULKER_BOX.get());
    blocks.add(IronShulkerBoxesBlocks.CRYSTAL_SHULKER_BOX.get());
    blocks.add(IronShulkerBoxesBlocks.OBSIDIAN_SHULKER_BOX.get());

    IronShulkerBoxesBlocks.IRON_SHULKER_BOXES.forEach((dyeColor, block) -> blocks.add(block.get()));
    IronShulkerBoxesBlocks.GOLD_SHULKER_BOXES.forEach((dyeColor, block) -> blocks.add(block.get()));
    IronShulkerBoxesBlocks.DIAMOND_SHULKER_BOXES.forEach((dyeColor, block) -> blocks.add(block.get()));
    IronShulkerBoxesBlocks.COPPER_SHULKER_BOXES.forEach((dyeColor, block) -> blocks.add(block.get()));
    IronShulkerBoxesBlocks.CRYSTAL_SHULKER_BOXES.forEach((dyeColor, block) -> blocks.add(block.get()));
    IronShulkerBoxesBlocks.OBSIDIAN_SHULKER_BOXES.forEach((dyeColor, block) -> blocks.add(block.get()));

    return blocks.build();
  }
}
