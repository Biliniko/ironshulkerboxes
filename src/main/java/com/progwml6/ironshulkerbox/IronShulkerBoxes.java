package com.progwml6.ironshulkerbox;

import com.progwml6.ironshulkerbox.common.block.AbstractIronShulkerBoxBlock;
import com.progwml6.ironshulkerbox.common.block.IronShulkerBoxesTypes;
import com.progwml6.ironshulkerbox.common.creativetabs.IronShulkerBoxesCreativeTabs;
import com.progwml6.ironshulkerbox.common.data.IronShulkerBoxesBlockTags;
import com.progwml6.ironshulkerbox.common.data.IronShulkerBoxesLanguageProvider;
import com.progwml6.ironshulkerbox.common.data.IronShulkerBoxesRecipeProvider;
import com.progwml6.ironshulkerbox.common.data.IronShulkerBoxesSpriteSourceProvider;
import com.progwml6.ironshulkerbox.common.data.loot.IronShulkerBoxesLootTableProvider;
import com.progwml6.ironshulkerbox.common.network.TopStacksSyncPacket;
import com.progwml6.ironshulkerbox.common.registraton.IronShulkerBoxesBlockEntityTypes;
import com.progwml6.ironshulkerbox.common.registraton.IronShulkerBoxesBlocks;
import com.progwml6.ironshulkerbox.common.registraton.IronShulkerBoxesItems;
import com.progwml6.ironshulkerbox.common.registraton.IronShulkerBoxesMenuTypes;
import com.progwml6.ironshulkerbox.common.registraton.IronShulkerBoxesRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.ShulkerBoxDispenseBehavior;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.stats.Stats;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.concurrent.CompletableFuture;

@Mod(IronShulkerBoxes.MODID)
public class IronShulkerBoxes {

  public static final String MODID = "ironshulkerbox";

  private static CauldronInteraction SHULKER_BOX = (blockState, level, blockPos, player, interactionHand, itemStack) -> {
    Block block = Block.byItem(itemStack.getItem());

    if (!(block instanceof AbstractIronShulkerBoxBlock)) {
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    } else {
      if (!level.isClientSide) {
        IronShulkerBoxesTypes type = AbstractIronShulkerBoxBlock.getTypeFromBlock(block);
        ItemStack newItemStack = itemStack.transmuteCopy(Blocks.SHULKER_BOX, 1);

        if (type != null) {
          newItemStack = switch (type) {
            case IRON -> itemStack.transmuteCopy(IronShulkerBoxesBlocks.IRON_SHULKER_BOX.get(), 1);
            case GOLD -> itemStack.transmuteCopy(IronShulkerBoxesBlocks.GOLD_SHULKER_BOX.get(), 1);
            case DIAMOND -> itemStack.transmuteCopy(IronShulkerBoxesBlocks.DIAMOND_SHULKER_BOX.get(), 1);
            case COPPER -> itemStack.transmuteCopy(IronShulkerBoxesBlocks.COPPER_SHULKER_BOX.get(), 1);
            case CRYSTAL -> itemStack.transmuteCopy(IronShulkerBoxesBlocks.CRYSTAL_SHULKER_BOX.get(), 1);
            case OBSIDIAN -> itemStack.transmuteCopy(IronShulkerBoxesBlocks.OBSIDIAN_SHULKER_BOX.get(), 1);
            case VANILLA -> itemStack.transmuteCopy(Blocks.SHULKER_BOX, 1);
          };
        }

        player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, newItemStack, false));
        player.awardStat(Stats.CLEAN_SHULKER_BOX);

        LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos);
      }

      return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
  };

  public IronShulkerBoxes(IEventBus modEventBus) {
    // General mod setup
    modEventBus.addListener(this::setup);
    modEventBus.addListener(this::gatherData);
    modEventBus.addListener(this::setupPackets);
    modEventBus.addListener(this::registerCapabilities);

    // Registry objects
    IronShulkerBoxesBlocks.BLOCKS.register(modEventBus);
    IronShulkerBoxesItems.ITEMS.register(modEventBus);
    IronShulkerBoxesBlockEntityTypes.BLOCK_ENTITIES.register(modEventBus);
    IronShulkerBoxesMenuTypes.MENU_TYPES.register(modEventBus);
    IronShulkerBoxesRecipes.RECIPE_SERIALIZERS.register(modEventBus);
    IronShulkerBoxesCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
  }

  private void setup(final FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
      DispenserBlock.registerBehavior(IronShulkerBoxesBlocks.IRON_SHULKER_BOX.get().asItem(), new ShulkerBoxDispenseBehavior());
      DispenserBlock.registerBehavior(IronShulkerBoxesBlocks.GOLD_SHULKER_BOX.get().asItem(), new ShulkerBoxDispenseBehavior());
      DispenserBlock.registerBehavior(IronShulkerBoxesBlocks.DIAMOND_SHULKER_BOX.get().asItem(), new ShulkerBoxDispenseBehavior());
      DispenserBlock.registerBehavior(IronShulkerBoxesBlocks.COPPER_SHULKER_BOX.get().asItem(), new ShulkerBoxDispenseBehavior());
      DispenserBlock.registerBehavior(IronShulkerBoxesBlocks.CRYSTAL_SHULKER_BOX.get().asItem(), new ShulkerBoxDispenseBehavior());
      DispenserBlock.registerBehavior(IronShulkerBoxesBlocks.OBSIDIAN_SHULKER_BOX.get().asItem(), new ShulkerBoxDispenseBehavior());

      for (DyeColor color : DyeColor.values()) {
        CauldronInteraction.WATER.map().put(IronShulkerBoxesBlocks.IRON_SHULKER_BOXES.get(color).get().asItem(), SHULKER_BOX);
        CauldronInteraction.WATER.map().put(IronShulkerBoxesBlocks.GOLD_SHULKER_BOXES.get(color).get().asItem(), SHULKER_BOX);
        CauldronInteraction.WATER.map().put(IronShulkerBoxesBlocks.DIAMOND_SHULKER_BOXES.get(color).get().asItem(), SHULKER_BOX);
        CauldronInteraction.WATER.map().put(IronShulkerBoxesBlocks.COPPER_SHULKER_BOXES.get(color).get().asItem(), SHULKER_BOX);
        CauldronInteraction.WATER.map().put(IronShulkerBoxesBlocks.CRYSTAL_SHULKER_BOXES.get(color).get().asItem(), SHULKER_BOX);
        CauldronInteraction.WATER.map().put(IronShulkerBoxesBlocks.OBSIDIAN_SHULKER_BOXES.get(color).get().asItem(), SHULKER_BOX);

        DispenserBlock.registerBehavior(IronShulkerBoxesBlocks.IRON_SHULKER_BOXES.get(color).get().asItem(), new ShulkerBoxDispenseBehavior());
        DispenserBlock.registerBehavior(IronShulkerBoxesBlocks.GOLD_SHULKER_BOXES.get(color).get().asItem(), new ShulkerBoxDispenseBehavior());
        DispenserBlock.registerBehavior(IronShulkerBoxesBlocks.DIAMOND_SHULKER_BOXES.get(color).get().asItem(), new ShulkerBoxDispenseBehavior());
        DispenserBlock.registerBehavior(IronShulkerBoxesBlocks.COPPER_SHULKER_BOXES.get(color).get().asItem(), new ShulkerBoxDispenseBehavior());
        DispenserBlock.registerBehavior(IronShulkerBoxesBlocks.CRYSTAL_SHULKER_BOXES.get(color).get().asItem(), new ShulkerBoxDispenseBehavior());
        DispenserBlock.registerBehavior(IronShulkerBoxesBlocks.OBSIDIAN_SHULKER_BOXES.get(color).get().asItem(), new ShulkerBoxDispenseBehavior());
      }
    });
  }

  private void gatherData(GatherDataEvent event) {
    ExistingFileHelper ext = event.getExistingFileHelper();
    DataGenerator gen = event.getGenerator();
    PackOutput packOutput = gen.getPackOutput();
    CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

    gen.addProvider(event.includeServer(), new IronShulkerBoxesLootTableProvider(packOutput, lookupProvider));

    gen.addProvider(event.includeClient(), new IronShulkerBoxesRecipeProvider(packOutput, lookupProvider));
    gen.addProvider(event.includeClient(), new IronShulkerBoxesSpriteSourceProvider(packOutput, ext, lookupProvider));
    gen.addProvider(event.includeClient(), new IronShulkerBoxesBlockTags(packOutput, lookupProvider, ext));
    gen.addProvider(event.includeClient(), new IronShulkerBoxesLanguageProvider(packOutput, "en_us"));
  }

  public void setupPackets(RegisterPayloadHandlersEvent event) {
    PayloadRegistrar registrar = event.registrar(MODID).versioned("1.0.0").optional();

    registrar.playBidirectional(TopStacksSyncPacket.TYPE, TopStacksSyncPacket.STREAM_CODEC, TopStacksSyncPacket::handle);
  }

  public void registerCapabilities(RegisterCapabilitiesEvent event) {
    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, IronShulkerBoxesBlockEntityTypes.IRON_SHULKER_BOX.get(), (shulkerBox, side) -> new SidedInvWrapper(shulkerBox, null));
    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, IronShulkerBoxesBlockEntityTypes.GOLD_SHULKER_BOX.get(), (shulkerBox, side) -> new SidedInvWrapper(shulkerBox, null));
    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, IronShulkerBoxesBlockEntityTypes.DIAMOND_SHULKER_BOX.get(), (shulkerBox, side) -> new SidedInvWrapper(shulkerBox, null));
    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, IronShulkerBoxesBlockEntityTypes.COPPER_SHULKER_BOX.get(), (shulkerBox, side) -> new SidedInvWrapper(shulkerBox, null));
    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, IronShulkerBoxesBlockEntityTypes.CRYSTAL_SHULKER_BOX.get(), (shulkerBox, side) -> new SidedInvWrapper(shulkerBox, null));
    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, IronShulkerBoxesBlockEntityTypes.OBSIDIAN_SHULKER_BOX.get(), (shulkerBox, side) -> new SidedInvWrapper(shulkerBox, null));

    IronShulkerBoxesBlocks.IRON_SHULKER_BOXES.forEach((dyeColor, block) -> event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new ComponentItemHandler(stack, DataComponents.CONTAINER, IronShulkerBoxesTypes.IRON.size), block.get()));
    IronShulkerBoxesBlocks.GOLD_SHULKER_BOXES.forEach((dyeColor, block) -> event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new ComponentItemHandler(stack, DataComponents.CONTAINER, IronShulkerBoxesTypes.GOLD.size), block.get()));
    IronShulkerBoxesBlocks.DIAMOND_SHULKER_BOXES.forEach((dyeColor, block) -> event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new ComponentItemHandler(stack, DataComponents.CONTAINER, IronShulkerBoxesTypes.DIAMOND.size), block.get()));
    IronShulkerBoxesBlocks.COPPER_SHULKER_BOXES.forEach((dyeColor, block) -> event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new ComponentItemHandler(stack, DataComponents.CONTAINER, IronShulkerBoxesTypes.COPPER.size), block.get()));
    IronShulkerBoxesBlocks.CRYSTAL_SHULKER_BOXES.forEach((dyeColor, block) -> event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new ComponentItemHandler(stack, DataComponents.CONTAINER, IronShulkerBoxesTypes.CRYSTAL.size), block.get()));
    IronShulkerBoxesBlocks.OBSIDIAN_SHULKER_BOXES.forEach((dyeColor, block) -> event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new ComponentItemHandler(stack, DataComponents.CONTAINER, IronShulkerBoxesTypes.OBSIDIAN.size), block.get()));

    event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new ComponentItemHandler(stack, DataComponents.CONTAINER, IronShulkerBoxesTypes.IRON.size), IronShulkerBoxesBlocks.IRON_SHULKER_BOX.get());
    event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new ComponentItemHandler(stack, DataComponents.CONTAINER, IronShulkerBoxesTypes.GOLD.size), IronShulkerBoxesBlocks.GOLD_SHULKER_BOX.get());
    event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new ComponentItemHandler(stack, DataComponents.CONTAINER, IronShulkerBoxesTypes.DIAMOND.size), IronShulkerBoxesBlocks.DIAMOND_SHULKER_BOX.get());
    event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new ComponentItemHandler(stack, DataComponents.CONTAINER, IronShulkerBoxesTypes.COPPER.size), IronShulkerBoxesBlocks.COPPER_SHULKER_BOX.get());
    event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new ComponentItemHandler(stack, DataComponents.CONTAINER, IronShulkerBoxesTypes.CRYSTAL.size), IronShulkerBoxesBlocks.CRYSTAL_SHULKER_BOX.get());
    event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new ComponentItemHandler(stack, DataComponents.CONTAINER, IronShulkerBoxesTypes.OBSIDIAN.size), IronShulkerBoxesBlocks.OBSIDIAN_SHULKER_BOX.get());
  }
}
