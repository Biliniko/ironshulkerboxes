package com.progwml6.ironshulkerbox.client;

import com.progwml6.ironshulkerbox.IronShulkerBoxes;
import com.progwml6.ironshulkerbox.client.render.IronShulkerBoxRenderer;
import com.progwml6.ironshulkerbox.client.screen.IronShulkerBoxScreen;
import com.progwml6.ironshulkerbox.common.registraton.IronShulkerBoxesBlockEntityTypes;
import com.progwml6.ironshulkerbox.common.registraton.IronShulkerBoxesMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = IronShulkerBoxes.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class IronShulkerBoxesClientRegistration {

  @SubscribeEvent
  public static void registerScreens(RegisterMenuScreensEvent event) {
    event.register(IronShulkerBoxesMenuTypes.IRON_SHULKER_BOX.get(), IronShulkerBoxScreen::new);
    event.register(IronShulkerBoxesMenuTypes.GOLD_SHULKER_BOX.get(), IronShulkerBoxScreen::new);
    event.register(IronShulkerBoxesMenuTypes.DIAMOND_SHULKER_BOX.get(), IronShulkerBoxScreen::new);
    event.register(IronShulkerBoxesMenuTypes.CRYSTAL_SHULKER_BOX.get(), IronShulkerBoxScreen::new);
    event.register(IronShulkerBoxesMenuTypes.COPPER_SHULKER_BOX.get(), IronShulkerBoxScreen::new);
    event.register(IronShulkerBoxesMenuTypes.OBSIDIAN_SHULKER_BOX.get(), IronShulkerBoxScreen::new);
  }

  @SubscribeEvent
  public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(IronShulkerBoxesBlockEntityTypes.IRON_SHULKER_BOX.get(), IronShulkerBoxRenderer::new);
    event.registerBlockEntityRenderer(IronShulkerBoxesBlockEntityTypes.GOLD_SHULKER_BOX.get(), IronShulkerBoxRenderer::new);
    event.registerBlockEntityRenderer(IronShulkerBoxesBlockEntityTypes.DIAMOND_SHULKER_BOX.get(), IronShulkerBoxRenderer::new);
    event.registerBlockEntityRenderer(IronShulkerBoxesBlockEntityTypes.CRYSTAL_SHULKER_BOX.get(), IronShulkerBoxRenderer::new);
    event.registerBlockEntityRenderer(IronShulkerBoxesBlockEntityTypes.COPPER_SHULKER_BOX.get(), IronShulkerBoxRenderer::new);
    event.registerBlockEntityRenderer(IronShulkerBoxesBlockEntityTypes.OBSIDIAN_SHULKER_BOX.get(), IronShulkerBoxRenderer::new);
  }
}
