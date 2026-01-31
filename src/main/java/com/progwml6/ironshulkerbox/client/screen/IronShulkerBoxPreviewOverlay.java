package com.progwml6.ironshulkerbox.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.progwml6.ironshulkerbox.common.block.AbstractIronShulkerBoxBlock;
import com.progwml6.ironshulkerbox.common.block.IronShulkerBoxesTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class IronShulkerBoxPreviewOverlay {

  private static final int SLOT_SPACING = 18;
  private static final int SLOT_SIZE = 16;

  private static boolean active;
  private static @Nullable Screen ownerScreen;
  private static @Nullable IronShulkerBoxesTypes type;
  private static @Nullable NonNullList<ItemStack> items;
  private static @Nullable Component title;
  private static boolean renderingTooltip;

  private IronShulkerBoxPreviewOverlay() {
  }

  public static boolean isActive() {
    return active;
  }

  public static void open(Screen screen, ItemStack stack) {
    IronShulkerBoxesTypes resolvedType = AbstractIronShulkerBoxBlock.getTypeFromItem(stack.getItem());
    if (resolvedType.size <= 0 || resolvedType.rowLength <= 0) {
      return;
    }

    NonNullList<ItemStack> loadedItems = NonNullList.withSize(resolvedType.size, ItemStack.EMPTY);
    CompoundTag rootTag = BlockItem.getBlockEntityData(stack);

    if (rootTag != null && rootTag.contains("Items", CompoundTag.TAG_LIST)) {
      ContainerHelper.loadAllItems(rootTag, loadedItems);
    }

    active = true;
    ownerScreen = screen;
    type = resolvedType;
    items = loadedItems;
    title = stack.getHoverName();
    renderingTooltip = false;
  }

  public static void close() {
    active = false;
    ownerScreen = null;
    type = null;
    items = null;
    title = null;
    renderingTooltip = false;
  }

  public static boolean isMouseInside(Screen screen, double mouseX, double mouseY) {
    if (!active || ownerScreen != screen || type == null) {
      return false;
    }

    int x = getLeft(screen);
    int y = getTop(screen);

    return mouseX >= x && mouseX < x + type.xSize && mouseY >= y && mouseY < y + type.ySize;
  }

  public static boolean isRenderingTooltip() {
    return renderingTooltip;
  }

  public static void render(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY) {
    if (!active) {
      return;
    }

    if (ownerScreen != screen) {
      close();
      return;
    }

    if (type == null || items == null) {
      close();
      return;
    }

    guiGraphics.pose().pushPose();
    guiGraphics.pose().translate(0.0F, 0.0F, 500.0F);

    RenderSystem.disableDepthTest();
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();

    guiGraphics.fill(0, 0, screen.width, screen.height, 0x7F000000);

    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, type.guiTexture);

    int x = getLeft(screen);
    int y = getTop(screen);

    guiGraphics.blit(type.guiTexture, x, y, 0, 0, type.xSize, type.ySize, type.textureXSize, type.textureYSize);

    Font font = Minecraft.getInstance().font;
    Component titleToUse = title != null ? title : Component.translatable("container.shulkerBox");
    guiGraphics.drawString(font, titleToUse, x + 8, y + 6, 4210752, false);

    for (int index = 0; index < items.size(); index++) {
      ItemStack itemStack = items.get(index);
      if (itemStack.isEmpty()) {
        continue;
      }

      int row = index / type.rowLength;
      int col = index % type.rowLength;
      int slotX = x + 12 + col * SLOT_SPACING;
      int slotY = y + 18 + row * SLOT_SPACING;

      guiGraphics.renderItem(itemStack, slotX, slotY);
      guiGraphics.renderItemDecorations(font, itemStack, slotX, slotY);
    }

    ItemStack hovered = getItemAt(screen, mouseX, mouseY);
    if (!hovered.isEmpty()) {
      renderingTooltip = true;
      try {
        guiGraphics.renderTooltip(font, hovered, mouseX, mouseY);
      } finally {
        renderingTooltip = false;
      }
    }

    RenderSystem.disableBlend();
    RenderSystem.enableDepthTest();
    guiGraphics.pose().popPose();
  }

  private static int getLeft(Screen screen) {
    return (screen.width - type.xSize) / 2;
  }

  private static int getTop(Screen screen) {
    return (screen.height - type.ySize) / 2;
  }

  private static ItemStack getItemAt(Screen screen, int mouseX, int mouseY) {
    if (type == null || items == null) {
      return ItemStack.EMPTY;
    }

    int x = getLeft(screen);
    int y = getTop(screen);

    int relativeX = mouseX - x - 12;
    int relativeY = mouseY - y - 18;

    if (relativeX < 0 || relativeY < 0) {
      return ItemStack.EMPTY;
    }

    int col = relativeX / SLOT_SPACING;
    int row = relativeY / SLOT_SPACING;

    if (col >= type.rowLength || row >= type.getRowCount()) {
      return ItemStack.EMPTY;
    }

    int inSlotX = relativeX % SLOT_SPACING;
    int inSlotY = relativeY % SLOT_SPACING;

    if (inSlotX >= SLOT_SIZE || inSlotY >= SLOT_SIZE) {
      return ItemStack.EMPTY;
    }

    int index = row * type.rowLength + col;
    if (index < 0 || index >= items.size()) {
      return ItemStack.EMPTY;
    }

    return items.get(index);
  }
}
