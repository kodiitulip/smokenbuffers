package io.github.kodiitulip.smokenbuffers;

import com.simibubi.create.foundation.data.CreateRegistrate;
import io.github.kodiitulip.smokenbuffers.registry.SmokeBuffersBlockEntities;
import io.github.kodiitulip.smokenbuffers.registry.SmokeBuffersBlocks;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(SmokeBuffers.MODID)
public class SmokeBuffers {
  public static final String MODID = "smokenbuffers";
  public static final String MODNAME = "Create: Smoke & Buffers";
  public static final Logger LOGGER = LogUtils.getLogger();
  public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

  public SmokeBuffers(IEventBus modEventBus, ModContainer modContainer) {
    REGISTRATE.registerEventListeners(modEventBus);
    modEventBus.addListener(this::commonSetup);

    SmokeBuffersBlocks.register();
    SmokeBuffersBlockEntities.register();

    modContainer.registerConfig(ModConfig.Type.COMMON, SmokeBuffersConfig.SPEC);
  }

  private void commonSetup(FMLCommonSetupEvent event) {
    LOGGER.info("[" + MODNAME + "]: Loading...");
  }

  public static CreateRegistrate registrate() {
    return REGISTRATE;
  }

  public static ResourceLocation asResource(String path) {
    return ResourceLocation.fromNamespaceAndPath(MODID, path);
  }
}
