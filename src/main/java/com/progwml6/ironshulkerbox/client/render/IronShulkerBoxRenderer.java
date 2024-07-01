package com.progwml6.ironshulkerbox.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.progwml6.ironshulkerbox.client.model.IronShulkerBoxesModels;
import com.progwml6.ironshulkerbox.client.model.inventory.ModelItem;
import com.progwml6.ironshulkerbox.common.block.AbstractIronShulkerBoxBlock;
import com.progwml6.ironshulkerbox.common.block.IronShulkerBoxesTypes;
import com.progwml6.ironshulkerbox.common.block.entity.AbstractIronShulkerBoxBlockEntity;
import com.progwml6.ironshulkerbox.common.block.entity.ICrystalShulkerBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class IronShulkerBoxRenderer implements BlockEntityRenderer<AbstractIronShulkerBoxBlockEntity> {

  private final ShulkerModel<?> model;

  private final BlockEntityRenderDispatcher renderer;

  private static final List<ModelItem> MODEL_ITEMS = Arrays.asList(
    new ModelItem(new Vector3f(0.3F, 0.45F, 0.3F), 3.0F),
    new ModelItem(new Vector3f(0.7F, 0.45F, 0.3F), 3.0F),
    new ModelItem(new Vector3f(0.3F, 0.45F, 0.7F), 3.0F),
    new ModelItem(new Vector3f(0.7F, 0.45F, 0.7F), 3.0F),
    new ModelItem(new Vector3f(0.3F, 0.1F, 0.3F), 3.0F),
    new ModelItem(new Vector3f(0.7F, 0.1F, 0.3F), 3.0F),
    new ModelItem(new Vector3f(0.3F, 0.1F, 0.7F), 3.0F),
    new ModelItem(new Vector3f(0.7F, 0.1F, 0.7F), 3.0F),
    new ModelItem(new Vector3f(0.5F, 0.32F, 0.5F), 3.0F)
  );

  public IronShulkerBoxRenderer(BlockEntityRendererProvider.Context context) {
    this.model = new ShulkerModel<>(context.bakeLayer(ModelLayers.SHULKER));
    this.renderer = context.getBlockEntityRenderDispatcher();
  }

  @Override
  public void render(AbstractIronShulkerBoxBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
    Direction direction = Direction.UP;

    if (pBlockEntity.hasLevel() && pBlockEntity.getLevel() != null) {
      BlockState blockstate = pBlockEntity.getLevel().getBlockState(pBlockEntity.getBlockPos());
      if (blockstate.getBlock() instanceof AbstractIronShulkerBoxBlock) {
        direction = blockstate.getValue(AbstractIronShulkerBoxBlock.FACING);
      }
    }

    Level level = pBlockEntity.getLevel();
    boolean useTileEntityBlockState = level != null;

    BlockState blockState = useTileEntityBlockState ? pBlockEntity.getBlockState() : pBlockEntity.getBlockToUse().defaultBlockState().setValue(AbstractIronShulkerBoxBlock.FACING, Direction.UP);
    Block block = blockState.getBlock();

    IronShulkerBoxesTypes boxType = IronShulkerBoxesTypes.IRON;
    IronShulkerBoxesTypes typeFromTileEntity = pBlockEntity.getShulkerBoxType();
    IronShulkerBoxesTypes typeFromBlock = AbstractIronShulkerBoxBlock.getTypeFromBlock(block);

    if (typeFromTileEntity != null) {
      boxType = typeFromTileEntity;
    }

    if (boxType != typeFromBlock || typeFromTileEntity != typeFromBlock) {
      if (typeFromBlock != null) {
        boxType = typeFromBlock;
      }
    }

    DyeColor dyecolor = pBlockEntity.getColor();
    Material material;

    if (dyecolor == null) {
      material = new Material(Sheets.SHULKER_SHEET, IronShulkerBoxesModels.chooseShulkerBoxTexture(boxType));
    } else {
      material = new Material(Sheets.SHULKER_SHEET, IronShulkerBoxesModels.chooseShulkerBoxTexture(boxType, dyecolor.getId()));
    }

    pPoseStack.pushPose();
    pPoseStack.translate(0.5F, 0.5F, 0.5F);
    pPoseStack.scale(0.9995F, 0.9995F, 0.9995F);
    pPoseStack.mulPose(direction.getRotation());
    pPoseStack.scale(1.0F, -1.0F, -1.0F);
    pPoseStack.translate(0.0F, -1.0F, 0.0F);
    ModelPart modelpart = this.model.getLid();
    modelpart.setPos(0.0F, 24.0F - pBlockEntity.getProgress(pPartialTick) * 0.5F * 16.0F, 0.0F);
    modelpart.yRot = 270.0F * pBlockEntity.getProgress(pPartialTick) * (float) (Math.PI / 180.0);
    VertexConsumer vertexconsumer = material.buffer(pBuffer, RenderType::entityCutoutNoCull);
    this.model.renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay);
    pPoseStack.popPose();

    if (boxType.isTransparent() && pBlockEntity instanceof ICrystalShulkerBox crystalShulkerBox && Vec3.atCenterOf(pBlockEntity.getBlockPos()).closerThan(this.renderer.camera.getPosition(), 128d)) {
      float rotation = (float) (360D * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL) - pPartialTick;

      for (int j = 0; j < MODEL_ITEMS.size() - 1; j++) {
        renderItem(pPoseStack, pBuffer, crystalShulkerBox.getTopItems().get(j), MODEL_ITEMS.get(j), rotation, pPackedLight);
      }
    }
  }

  /**
   * Renders a single item in a TESR
   *
   * @param matrices  Matrix stack instance
   * @param buffer    Buffer instance
   * @param item      Item to render
   * @param modelItem Model items for render information
   * @param light     Model light
   */
  public static void renderItem(PoseStack matrices, MultiBufferSource buffer, ItemStack item, ModelItem modelItem, float rotation, int light) {
    // if no stack, skip
    if (item.isEmpty()) return;

    // start rendering
    matrices.pushPose();
    Vector3f center = modelItem.getCenter();
    matrices.translate(center.x(), center.y(), center.z());

    matrices.mulPose(Axis.YP.rotationDegrees(rotation));

    // scale
    float scale = modelItem.getSizeScaled();
    matrices.scale(scale, scale, scale);

    // render the actual item
    Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemDisplayContext.NONE, light, OverlayTexture.NO_OVERLAY, matrices, buffer, null, 0);

    matrices.popPose();
  }

  @Override
  public AABB getRenderBoundingBox(AbstractIronShulkerBoxBlockEntity blockEntity) {
    BlockPos pos = blockEntity.getBlockPos();
    return new AABB(pos.getX() - 0.5, pos.getY() - 0.5, pos.getZ() - 0.5, pos.getX() + 1.5, pos.getY() + 1.5, pos.getZ() + 1.5);
  }
}
