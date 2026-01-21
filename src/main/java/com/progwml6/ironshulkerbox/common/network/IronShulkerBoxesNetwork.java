package com.progwml6.ironshulkerbox.common.network;

import com.progwml6.ironshulkerbox.IronShulkerBoxes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class IronShulkerBoxesNetwork {

  private static final int NPC_VERSION = 1;

  public static SimpleChannel INSTANCE;

  public static void setup() {
    String protocolVersion = Integer.toString(NPC_VERSION);
    INSTANCE = NetworkRegistry.newSimpleChannel(
      new ResourceLocation(IronShulkerBoxes.MOD_ID, "network"),
      () -> protocolVersion,
      protocolVersion::equals,
      protocolVersion::equals
    );

    INSTANCE.messageBuilder(PacketTopStacksSync.class, 0)
      .encoder(PacketTopStacksSync::encode)
      .decoder(PacketTopStacksSync::decode)
      .consumerNetworkThread(PacketTopStacksSync::handle)
      .add();
  }
}
