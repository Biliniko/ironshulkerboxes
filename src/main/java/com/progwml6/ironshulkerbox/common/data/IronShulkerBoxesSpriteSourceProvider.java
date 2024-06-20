package com.progwml6.ironshulkerbox.common.data;

import com.progwml6.ironshulkerbox.IronShulkerBoxes;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;

import java.util.concurrent.CompletableFuture;

public class IronShulkerBoxesSpriteSourceProvider extends SpriteSourceProvider {

  public IronShulkerBoxesSpriteSourceProvider(PackOutput output, ExistingFileHelper fileHelper, CompletableFuture<HolderLookup.Provider> lookupProvider) {
    super(output, lookupProvider, IronShulkerBoxes.MODID, fileHelper);
  }

  @Override
  protected void gather() {
    atlas(SHULKER_BOXES_ATLAS).addSource(new DirectoryLister("model", "model/"));
  }
}
