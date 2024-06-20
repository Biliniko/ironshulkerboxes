package com.progwml6.ironshulkerbox.common.item;

import com.progwml6.ironshulkerbox.client.model.inventory.IronShulkerBoxItemStackRenderer;
import com.progwml6.ironshulkerbox.common.block.AbstractIronShulkerBoxBlock;
import com.progwml6.ironshulkerbox.common.block.IronShulkerBoxesTypes;
import com.progwml6.ironshulkerbox.common.block.entity.CopperShulkerBoxBlockEntity;
import com.progwml6.ironshulkerbox.common.block.entity.CrystalShulkerBoxBlockEntity;
import com.progwml6.ironshulkerbox.common.block.entity.DiamondShulkerBoxBlockEntity;
import com.progwml6.ironshulkerbox.common.block.entity.GoldShulkerBoxBlockEntity;
import com.progwml6.ironshulkerbox.common.block.entity.IronShulkerBoxBlockEntity;
import com.progwml6.ironshulkerbox.common.block.entity.ObsidianShulkerBoxBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class IronShulkerBoxBlockItem extends BlockItem {

  protected IronShulkerBoxesTypes type;
  protected DyeColor color;

  public IronShulkerBoxBlockItem(Block block, Properties properties, IronShulkerBoxesTypes type, DyeColor color) {
    super(block, properties);

    this.type = type;
    this.color = color;
  }

  @Override
  public void initializeClient(Consumer<IClientItemExtensions> consumer) {
    super.initializeClient(consumer);

    consumer.accept(new IClientItemExtensions() {
      @Override
      public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        Supplier<BlockEntity> modelToUse;

        DyeColor dyeColor;

        if (color != null) {
          dyeColor = color;
        } else {
          dyeColor = null;
        }

        switch (type) {
          case GOLD -> modelToUse = () -> new GoldShulkerBoxBlockEntity(BlockPos.ZERO, IronShulkerBoxesTypes.get(type, dyeColor).defaultBlockState());
          case DIAMOND -> modelToUse = () -> new DiamondShulkerBoxBlockEntity(BlockPos.ZERO, IronShulkerBoxesTypes.get(type, dyeColor).defaultBlockState());
          case COPPER -> modelToUse = () -> new CopperShulkerBoxBlockEntity(BlockPos.ZERO, IronShulkerBoxesTypes.get(type, dyeColor).defaultBlockState());
          case CRYSTAL -> modelToUse = () -> new CrystalShulkerBoxBlockEntity(BlockPos.ZERO, IronShulkerBoxesTypes.get(type, dyeColor).defaultBlockState());
          case OBSIDIAN -> modelToUse = () -> new ObsidianShulkerBoxBlockEntity(BlockPos.ZERO, IronShulkerBoxesTypes.get(type, dyeColor).defaultBlockState());
          default -> modelToUse = () -> new IronShulkerBoxBlockEntity(BlockPos.ZERO, IronShulkerBoxesTypes.get(type, dyeColor).defaultBlockState());
        }

        return new IronShulkerBoxItemStackRenderer<>(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels(), modelToUse);
      }
    });
  }

  @Override
  public boolean canFitInsideContainerItems() {
    return !(this.getBlock() instanceof AbstractIronShulkerBoxBlock);
  }
}
