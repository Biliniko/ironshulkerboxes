package com.progwml6.ironshulkerbox.common.block;

import com.google.common.collect.Maps;
import com.progwml6.ironshulkerbox.common.block.entity.AbstractIronShulkerBoxBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class AbstractIronShulkerBoxBlock extends BaseEntityBlock {

  private static final Component UNKNOWN_CONTENTS = Component.translatable("container.shulkerBox.unknownContents");
  private static final VoxelShape UP_OPEN_AABB = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
  private static final VoxelShape DOWN_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
  private static final VoxelShape WES_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
  private static final VoxelShape EAST_OPEN_AABB = Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
  private static final VoxelShape NORTH_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
  private static final VoxelShape SOUTH_OPEN_AABB = Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
  private static final Map<Direction, VoxelShape> OPEN_SHAPE_BY_DIRECTION = Util.make(Maps.newEnumMap(Direction.class), p_258974_ -> {
    p_258974_.put(Direction.NORTH, NORTH_OPEN_AABB);
    p_258974_.put(Direction.EAST, EAST_OPEN_AABB);
    p_258974_.put(Direction.SOUTH, SOUTH_OPEN_AABB);
    p_258974_.put(Direction.WEST, WES_OPEN_AABB);
    p_258974_.put(Direction.UP, UP_OPEN_AABB);
    p_258974_.put(Direction.DOWN, DOWN_OPEN_AABB);
  });
  public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;
  public static final ResourceLocation CONTENTS = ResourceLocation.withDefaultNamespace("contents");

  private final IronShulkerBoxesTypes type;
  @Nullable
  protected final DyeColor color;
  protected final Supplier<BlockEntityType<? extends AbstractIronShulkerBoxBlockEntity>> blockEntityType;

  public AbstractIronShulkerBoxBlock(BlockBehaviour.Properties properties, @Nullable DyeColor color, Supplier<BlockEntityType<? extends AbstractIronShulkerBoxBlockEntity>> blockEntityType, IronShulkerBoxesTypes type) {
    super(properties);

    this.color = color;
    this.type = type;
    this.blockEntityType = blockEntityType;

    this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
  }

  public BlockEntityType<? extends AbstractIronShulkerBoxBlockEntity> blockEntityType() {
    return this.blockEntityType.get();
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
    return createTickerHelper(pBlockEntityType, this.blockEntityType(), AbstractIronShulkerBoxBlockEntity::tick);
  }

  /**
   * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
   * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
   *
   * @deprecated call via {@link net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#getRenderShape}
   * whenever possible. Implementing/overriding is fine.
   */
  @Override
  @Deprecated
  public RenderShape getRenderShape(BlockState pState) {
    return RenderShape.ENTITYBLOCK_ANIMATED;
  }

  @Override
  @Deprecated
  protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
    if (pLevel.isClientSide) {
      return InteractionResult.SUCCESS;
    } else if (pPlayer.isSpectator()) {
      return InteractionResult.CONSUME;
    } else {
      BlockEntity blockentity = pLevel.getBlockEntity(pPos);

      if (blockentity instanceof AbstractIronShulkerBoxBlockEntity ironShulkerBoxBlockEntity) {
        if (canOpen(pState, pLevel, pPos, ironShulkerBoxBlockEntity)) {
          pPlayer.openMenu(ironShulkerBoxBlockEntity);
          pPlayer.awardStat(Stats.OPEN_SHULKER_BOX);
          PiglinAi.angerNearbyPiglins(pPlayer, true);
        }

        return InteractionResult.CONSUME;
      } else {
        return InteractionResult.PASS;
      }
    }
  }

  private static boolean canOpen(BlockState pState, Level pLevel, BlockPos pPos, AbstractIronShulkerBoxBlockEntity pBlockEntity) {
    if (pBlockEntity.getAnimationStatus() != AbstractIronShulkerBoxBlockEntity.AnimationStatus.CLOSED) {
      return true;
    } else {
      AABB aabb = Shulker.getProgressDeltaAabb(1.0F, pState.getValue(FACING), 0.0F, 0.5F).move(pPos).deflate(1.0E-6);
      return pLevel.noCollision(aabb);
    }
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext pContext) {
    return this.defaultBlockState().setValue(FACING, pContext.getClickedFace());
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
    pBuilder.add(FACING);
  }

  /**
   * Called before the Block is set to air in the world. Called regardless of if the player's tool can actually collect
   * this block
   */
  @Override
  public BlockState playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
    BlockEntity blockentity = pLevel.getBlockEntity(pPos);

    if (blockentity instanceof AbstractIronShulkerBoxBlockEntity ironShulkerBoxBlockEntity) {
      if (!pLevel.isClientSide && pPlayer.isCreative() && !ironShulkerBoxBlockEntity.isEmpty()) {
        ItemStack itemstack = getColoredItemStack(this.getColor(), this.getType());
        itemstack.applyComponents(blockentity.collectComponents());
        ItemEntity itementity = new ItemEntity(pLevel, (double)pPos.getX() + 0.5, (double)pPos.getY() + 0.5, (double)pPos.getZ() + 0.5, itemstack);
        itementity.setDefaultPickUpDelay();
        pLevel.addFreshEntity(itementity);
      } else {
        ironShulkerBoxBlockEntity.unpackLootTable(pPlayer);
      }
    }

    return super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
  }

  @Override
  @Deprecated
  public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pParams) {
    BlockEntity blockentity = pParams.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
    if (blockentity instanceof AbstractIronShulkerBoxBlockEntity ironShulkerBoxBlockEntity) {
      pParams = pParams.withDynamicDrop(CONTENTS, itemStackConsumer -> {
        for (int i = 0; i < ironShulkerBoxBlockEntity.getContainerSize(); i++) {
          itemStackConsumer.accept(ironShulkerBoxBlockEntity.getItem(i));
        }
      });
    }

    return super.getDrops(pState, pParams);
  }

  @Override
  @Deprecated
  public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
    if (!pState.is(pNewState.getBlock())) {
      BlockEntity blockentity = pLevel.getBlockEntity(pPos);
      if (blockentity instanceof AbstractIronShulkerBoxBlockEntity) {
        pLevel.updateNeighbourForOutputSignal(pPos, pState.getBlock());
      }

      super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }
  }

  @Override
  public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTootipComponents, TooltipFlag pTooltipFlag) {
    super.appendHoverText(pStack, pContext, pTootipComponents, pTooltipFlag);
    if (pStack.has(DataComponents.CONTAINER_LOOT)) {
      pTootipComponents.add(UNKNOWN_CONTENTS);
    }

    int i = 0;
    int j = 0;

    for (ItemStack itemstack : pStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).nonEmptyItems()) {
      j++;
      if (i <= 4) {
        i++;
        pTootipComponents.add(Component.translatable("container.shulkerBox.itemCount", itemstack.getHoverName(), itemstack.getCount()));
      }
    }

    if (j - i > 0) {
      pTootipComponents.add(Component.translatable("container.shulkerBox.more", j - i).withStyle(ChatFormatting.ITALIC));
    }
  }

  @Override
  public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
    if (pReader.getBlockEntity(pPos) instanceof AbstractIronShulkerBoxBlockEntity ironShulkerBoxBlockEntity && !ironShulkerBoxBlockEntity.isClosed()) {
      return OPEN_SHAPE_BY_DIRECTION.get(pState.getValue(FACING).getOpposite());
    }

    return Shapes.block();
  }

  @Override
  public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
    BlockEntity blockentity = pLevel.getBlockEntity(pPos);
    return blockentity instanceof AbstractIronShulkerBoxBlockEntity ? Shapes.create(((AbstractIronShulkerBoxBlockEntity) blockentity).getBoundingBox(pState)) : Shapes.block();
  }

  @Override
  protected boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
    return false;
  }

  /**
   * @deprecated call via {@link
   * net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#hasAnalogOutputSignal} whenever possible.
   * Implementing/overriding is fine.
   */
  @Override
  @Deprecated
  public boolean hasAnalogOutputSignal(BlockState pState) {
    return true;
  }

  /**
   * @deprecated call via {@link
   * net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#getAnalogOutputSignal} whenever possible.
   * Implementing/overriding is fine.
   */
  @Override
  @Deprecated
  public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
    return AbstractContainerMenu.getRedstoneSignalFromContainer((Container) pLevel.getBlockEntity(pPos));
  }

  @Override
  public ItemStack getCloneItemStack(LevelReader pLevel, BlockPos pPos, BlockState pState) {
    ItemStack itemstack = super.getCloneItemStack(pLevel, pPos, pState);
    pLevel.getBlockEntity(pPos, this.blockEntityType()).ifPresent(shulkerBoxBlockEntity -> shulkerBoxBlockEntity.saveToItem(itemstack, pLevel.registryAccess()));
    return itemstack;
  }

  public static IronShulkerBoxesTypes getTypeFromItem(Item itemIn) {
    return getTypeFromBlock(Block.byItem(itemIn));
  }

  public static IronShulkerBoxesTypes getTypeFromBlock(Block blockIn) {
    return blockIn instanceof AbstractIronShulkerBoxBlock ? ((AbstractIronShulkerBoxBlock) blockIn).getType() : IronShulkerBoxesTypes.VANILLA;
  }

  public IronShulkerBoxesTypes getType() {
    return this.type;
  }

  @Nullable
  public static DyeColor getColorFromItem(Item itemIn) {
    return getColorFromBlock(Block.byItem(itemIn));
  }

  @Nullable
  public static DyeColor getColorFromBlock(Block pBlock) {
    return pBlock instanceof AbstractIronShulkerBoxBlock ? ((AbstractIronShulkerBoxBlock) pBlock).getColor() : null;
  }

  public static Block getBlockByColor(@Nullable DyeColor colorIn, IronShulkerBoxesTypes typeIn) {
    return IronShulkerBoxesTypes.get(typeIn, colorIn);
  }

  @Nullable
  public DyeColor getColor() {
    return this.color;
  }

  public static ItemStack getColoredItemStack(@Nullable DyeColor pColor, IronShulkerBoxesTypes typeIn) {
    return new ItemStack(getBlockByColor(pColor, typeIn));
  }

  /**
   * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
   * blockstate.
   *
   * @deprecated call via {@link net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#rotate} whenever
   * possible. Implementing/overriding is fine.
   */
  @Override
  @Deprecated
  public BlockState rotate(BlockState pState, Rotation pRot) {
    return pState.setValue(FACING, pRot.rotate(pState.getValue(FACING)));
  }

  /**
   * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
   * blockstate.
   *
   * @deprecated call via {@link net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#mirror} whenever
   * possible. Implementing/overriding is fine.
   */
  @Override
  @Deprecated
  public BlockState mirror(BlockState pState, Mirror pMirror) {
    return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
  }
}
