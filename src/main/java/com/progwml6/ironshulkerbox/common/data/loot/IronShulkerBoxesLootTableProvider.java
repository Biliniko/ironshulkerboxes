package com.progwml6.ironshulkerbox.common.data.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class IronShulkerBoxesLootTableProvider extends LootTableProvider {

  public IronShulkerBoxesLootTableProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> provider) {
    super(pOutput, Collections.emptySet(), List.of(new SubProviderEntry(IronShulkerBoxesBlockLoot::new, LootContextParamSets.BLOCK)), provider);
  }
}
