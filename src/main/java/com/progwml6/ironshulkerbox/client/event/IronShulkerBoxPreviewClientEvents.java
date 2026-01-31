package com.progwml6.ironshulkerbox.client.event;

import com.mojang.blaze3d.platform.InputConstants;
import com.progwml6.ironshulkerbox.IronShulkerBoxes;
import com.progwml6.ironshulkerbox.client.screen.IronShulkerBoxPreviewOverlay;
import com.progwml6.ironshulkerbox.common.block.AbstractIronShulkerBoxBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = IronShulkerBoxes.MOD_ID, value = Dist.CLIENT)
public final class IronShulkerBoxPreviewClientEvents {

  private IronShulkerBoxPreviewClientEvents() {
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void onRender(ScreenEvent.Render.Post event) {
    if (IronShulkerBoxPreviewOverlay.isActive()) {
      IronShulkerBoxPreviewOverlay.render(event.getScreen(), event.getGuiGraphics(), event.getMouseX(), event.getMouseY());
    }
  }

  @SubscribeEvent
  public static void onKeyPressed(ScreenEvent.KeyPressed.Pre event) {
    if (!IronShulkerBoxPreviewOverlay.isActive()) {
      return;
    }

    if (event.getKeyCode() == GLFW.GLFW_KEY_ESCAPE) {
      IronShulkerBoxPreviewOverlay.close();
    }

    event.setCanceled(true);
  }

  @SubscribeEvent
  public static void onMouseScrolled(ScreenEvent.MouseScrolled.Pre event) {
    if (IronShulkerBoxPreviewOverlay.isActive()) {
      event.setCanceled(true);
    }
  }

  @SubscribeEvent
  public static void onMouseButtonPressed(ScreenEvent.MouseButtonPressed.Pre event) {
    if (IronShulkerBoxPreviewOverlay.isActive()) {
      if (!IronShulkerBoxPreviewOverlay.isMouseInside(event.getScreen(), event.getMouseX(), event.getMouseY())) {
        IronShulkerBoxPreviewOverlay.close();
      }

      event.setCanceled(true);
      return;
    }

    if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
      return;
    }

    if (!isAltDown()) {
      return;
    }

    if (tryOpenPreview(event.getScreen())) {
      event.setCanceled(true);
    }
  }

  @SubscribeEvent
  public static void onRenderTooltip(RenderTooltipEvent.Pre event) {
    if (!IronShulkerBoxPreviewOverlay.isActive()) {
      return;
    }

    if (!IronShulkerBoxPreviewOverlay.isRenderingTooltip()) {
      event.setCanceled(true);
    }
  }

  private static boolean tryOpenPreview(Screen screen) {
    if (!(screen instanceof AbstractContainerScreen<?> containerScreen)) {
      return false;
    }

    Slot slot = containerScreen.getSlotUnderMouse();
    if (slot == null) {
      return false;
    }

    ItemStack stack = slot.getItem();
    if (stack.isEmpty()) {
      return false;
    }

    Block block = Block.byItem(stack.getItem());
    if (!(block instanceof AbstractIronShulkerBoxBlock)) {
      return false;
    }

    IronShulkerBoxPreviewOverlay.open(containerScreen, stack);
    return true;
  }

  private static boolean isAltDown() {
    long window = Minecraft.getInstance().getWindow().getWindow();
    return InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_ALT) || InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_ALT);
  }
}
