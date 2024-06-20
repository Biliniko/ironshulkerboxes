package com.progwml6.ironshulkerbox;

import com.progwml6.ironshulkerbox.common.block.AbstractIronShulkerBoxBlock;
import com.progwml6.ironshulkerbox.common.block.IronShulkerBoxesTypes;
import com.progwml6.ironshulkerbox.common.creativetabs.IronShulkerBoxesCreativeTabs;
import com.progwml6.ironshulkerbox.common.data.IronShulkerBoxesBlockTags;
import com.progwml6.ironshulkerbox.common.data.IronShulkerBoxesLanguageProvider;
import com.progwml6.ironshulkerbox.common.data.IronShulkerBoxesRecipeProvider;
import com.progwml6.ironshulkerbox.common.data.IronShulkerBoxesSpriteSourceProvider;
import com.progwml6.ironshulkerbox.common.data.loot.IronShulkerBoxesLootTableProvider;
import com.progwml6.ironshulkerbox.common.item.IronShulkerBoxItemStackInvWrapper;
import com.progwml6.ironshulkerbox.common.network.TopStacksSyncPacket;
import com.progwml6.ironshulkerbox.common.registraton.IronShulkerBoxesBlockEntityTypes;
import com.progwml6.ironshulkerbox.common.registraton.IronShulkerBoxesBlocks;
import com.progwml6.ironshulkerbox.common.registraton.IronShulkerBoxesItems;
import com.progwml6.ironshulkerbox.common.registraton.IronShulkerBoxesMenuTypes;
import com.progwml6.ironshulkerbox.common.registraton.IronShulkerBoxesRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.dispenser.ShulkerBoxDispenseBehavior;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
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
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

import java.util.concurrent.CompletableFuture;

@Mod(IronShulkerBoxes.MODID)
public class IronShulkerBoxes {

  public static final String MODID = "ironshulkerbox";

  private static CauldronInteraction SHULKER_BOX = (blockState, level, blockPos, player, interactionHand, itemStack) -> {
    Block block = Block.byItem(itemStack.getItem());

    if (!(block instanceof AbstractIronShulkerBoxBlock shulkerBoxBlock)) {
      return InteractionResult.PASS;
    } else {
      if (!level.isClientSide) {
        IronShulkerBoxesTypes type = AbstractIronShulkerBoxBlock.getTypeFromBlock(block);
        ItemStack itemstack = new ItemStack(Blocks.SHULKER_BOX);

        if (type != null) {
          itemstack = switch (type) {
            case IRON -> new ItemStack(IronShulkerBoxesBlocks.IRON_SHULKER_BOX.get());
            case GOLD -> new ItemStack(IronShulkerBoxesBlocks.GOLD_SHULKER_BOX.get());
            case DIAMOND -> new ItemStack(IronShulkerBoxesBlocks.DIAMOND_SHULKER_BOX.get());
            case COPPER -> new ItemStack(IronShulkerBoxesBlocks.COPPER_SHULKER_BOX.get());
            case CRYSTAL -> new ItemStack(IronShulkerBoxesBlocks.CRYSTAL_SHULKER_BOX.get());
            case OBSIDIAN -> new ItemStack(IronShulkerBoxesBlocks.OBSIDIAN_SHULKER_BOX.get());
            case VANILLA -> new ItemStack(Blocks.SHULKER_BOX);
          };
        }

        if (itemStack.hasTag()) {
          itemstack.setTag(itemStack.getTag().copy());
        }

        player.setItemInHand(interactionHand, itemstack);
        player.awardStat(Stats.CLEAN_SHULKER_BOX);

        LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos);
      }

      return InteractionResult.sidedSuccess(level.isClientSide);
    }
  };

  public IronShulkerBoxes(IEventBus modEventBus) {
    // General mod setup
    modEventBus.addListener(this::setup);
    modEventBus.addListener(this::gatherData);
    modEventBus.addListener(this::setupPackets);

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

    gen.addProvider(event.includeServer(), new IronShulkerBoxesLootTableProvider(packOutput));

    gen.addProvider(event.includeClient(), new IronShulkerBoxesRecipeProvider(packOutput));
    gen.addProvider(event.includeClient(), new IronShulkerBoxesSpriteSourceProvider(packOutput, ext, lookupProvider));
    gen.addProvider(event.includeClient(), new IronShulkerBoxesBlockTags(packOutput, lookupProvider, ext));
    gen.addProvider(event.includeClient(), new IronShulkerBoxesLanguageProvider(packOutput, "en_us"));
  }

  public void setupPackets(RegisterPayloadHandlerEvent event) {
    IPayloadRegistrar registrar = event.registrar(MODID).versioned("1.0.0").optional();

    registrar.play(TopStacksSyncPacket.ID, TopStacksSyncPacket::new, payload -> payload.client(TopStacksSyncPacket::handle));
  }

  public void registerCapabilities(RegisterCapabilitiesEvent event) {
    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, IronShulkerBoxesBlockEntityTypes.IRON_SHULKER_BOX.get(), (shulkerBox, side) -> new SidedInvWrapper(shulkerBox, null));
    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, IronShulkerBoxesBlockEntityTypes.GOLD_SHULKER_BOX.get(), (shulkerBox, side) -> new SidedInvWrapper(shulkerBox, null));
    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, IronShulkerBoxesBlockEntityTypes.DIAMOND_SHULKER_BOX.get(), (shulkerBox, side) -> new SidedInvWrapper(shulkerBox, null));
    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, IronShulkerBoxesBlockEntityTypes.COPPER_SHULKER_BOX.get(), (shulkerBox, side) -> new SidedInvWrapper(shulkerBox, null));
    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, IronShulkerBoxesBlockEntityTypes.CRYSTAL_SHULKER_BOX.get(), (shulkerBox, side) -> new SidedInvWrapper(shulkerBox, null));
    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, IronShulkerBoxesBlockEntityTypes.OBSIDIAN_SHULKER_BOX.get(), (shulkerBox, side) -> new SidedInvWrapper(shulkerBox, null));

    IronShulkerBoxesBlocks.IRON_SHULKER_BOXES.forEach((dyeColor, block) -> event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new IronShulkerBoxItemStackInvWrapper(stack, IronShulkerBoxesTypes.IRON), block.get()));
    IronShulkerBoxesBlocks.GOLD_SHULKER_BOXES.forEach((dyeColor, block) -> event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new IronShulkerBoxItemStackInvWrapper(stack, IronShulkerBoxesTypes.GOLD), block.get()));
    IronShulkerBoxesBlocks.DIAMOND_SHULKER_BOXES.forEach((dyeColor, block) -> event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new IronShulkerBoxItemStackInvWrapper(stack, IronShulkerBoxesTypes.DIAMOND), block.get()));
    IronShulkerBoxesBlocks.COPPER_SHULKER_BOXES.forEach((dyeColor, block) -> event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new IronShulkerBoxItemStackInvWrapper(stack, IronShulkerBoxesTypes.COPPER), block.get()));
    IronShulkerBoxesBlocks.CRYSTAL_SHULKER_BOXES.forEach((dyeColor, block) -> event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new IronShulkerBoxItemStackInvWrapper(stack, IronShulkerBoxesTypes.CRYSTAL), block.get()));
    IronShulkerBoxesBlocks.OBSIDIAN_SHULKER_BOXES.forEach((dyeColor, block) -> event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new IronShulkerBoxItemStackInvWrapper(stack, IronShulkerBoxesTypes.OBSIDIAN), block.get()));

    event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new IronShulkerBoxItemStackInvWrapper(stack, IronShulkerBoxesTypes.IRON), IronShulkerBoxesBlocks.IRON_SHULKER_BOX.get());
    event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new IronShulkerBoxItemStackInvWrapper(stack, IronShulkerBoxesTypes.GOLD), IronShulkerBoxesBlocks.GOLD_SHULKER_BOX.get());
    event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new IronShulkerBoxItemStackInvWrapper(stack, IronShulkerBoxesTypes.DIAMOND), IronShulkerBoxesBlocks.DIAMOND_SHULKER_BOX.get());
    event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new IronShulkerBoxItemStackInvWrapper(stack, IronShulkerBoxesTypes.COPPER), IronShulkerBoxesBlocks.COPPER_SHULKER_BOX.get());
    event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new IronShulkerBoxItemStackInvWrapper(stack, IronShulkerBoxesTypes.CRYSTAL), IronShulkerBoxesBlocks.CRYSTAL_SHULKER_BOX.get());
    event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new IronShulkerBoxItemStackInvWrapper(stack, IronShulkerBoxesTypes.OBSIDIAN), IronShulkerBoxesBlocks.OBSIDIAN_SHULKER_BOX.get());
  }
}
