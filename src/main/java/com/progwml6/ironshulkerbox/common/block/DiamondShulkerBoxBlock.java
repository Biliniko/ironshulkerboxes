package com.progwml6.ironshulkerbox.common.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.progwml6.ironshulkerbox.common.block.entity.DiamondShulkerBoxBlockEntity;
import com.progwml6.ironshulkerbox.common.registraton.IronShulkerBoxesBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DiamondShulkerBoxBlock extends AbstractIronShulkerBoxBlock {

  public static final MapCodec<DiamondShulkerBoxBlock> CODEC = RecordCodecBuilder.mapCodec(
    p_308835_ -> p_308835_.group(DyeColor.CODEC.optionalFieldOf("color").forGetter(p_304373_ -> Optional.ofNullable(p_304373_.color)), propertiesCodec())
      .apply(p_308835_, (p_304374_, p_304375_) -> new DiamondShulkerBoxBlock(p_304375_, p_304374_.orElse(null)))
  );

  public DiamondShulkerBoxBlock(Properties properties, @Nullable DyeColor color) {
    super(properties, color, IronShulkerBoxesBlockEntityTypes.DIAMOND_SHULKER_BOX::get, IronShulkerBoxesTypes.DIAMOND);
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
    return new DiamondShulkerBoxBlockEntity(this.color, pPos, pState);
  }

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return CODEC;
  }
}
