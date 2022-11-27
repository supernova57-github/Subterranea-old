package supernova57.subterranea.event.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import supernova57.subterranea.client.renderer.entity.SBTRModelLayers;
import supernova57.subterranea.client.renderer.entity.model.SnowflakeModel;
import supernova57.subterranea.main.Reference;

@Mod.EventBusSubscriber(modid = Reference.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SBTRLayerDefinitionHandler {

	@SubscribeEvent
	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(SBTRModelLayers.SNOWFLAKE, SnowflakeModel::createMainLayer);
	}
	
}
