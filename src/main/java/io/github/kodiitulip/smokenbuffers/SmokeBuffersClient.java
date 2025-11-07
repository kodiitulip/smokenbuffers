package io.github.kodiitulip.smokenbuffers;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = SmokeBuffers.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = SmokeBuffers.MODID, value = Dist.CLIENT)
public class SmokeBuffersClient {
  public SmokeBuffersClient(ModContainer container) {
    container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
  }

  @SubscribeEvent
  static void onClientSetup(FMLClientSetupEvent event) {
    SmokeBuffers.LOGGER.info("[" + SmokeBuffers.MODNAME + "]: Loading on the Client");
  }
}
