package supernova57.subterranea.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import supernova57.subterranea.client.renderer.entity.SBTRModelLayers;
import supernova57.subterranea.client.renderer.entity.SnowflakeRenderer;
import supernova57.subterranea.client.renderer.entity.model.SnowflakeModel;
import supernova57.subterranea.entity.projectile.SnowflakeEntity;
import supernova57.subterranea.main.Reference;
import supernova57.subterranea.main.Subterranea;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SBTREntityTypeRegistry {

	public static final RegistryObject<EntityType<SnowflakeEntity>> SNOWFLAKE = Subterranea.ENTITIES.register("snowflake", () -> EntityType.Builder.<SnowflakeEntity>of(SnowflakeEntity::new, MobCategory.MISC).clientTrackingRange(8).sized(0.5F, 0.5F).build("snowflake"));
	
	public static void register() {
		Subterranea.ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(SNOWFLAKE.get(), context -> new SnowflakeRenderer(context, new SnowflakeModel(context.bakeLayer(SBTRModelLayers.SNOWFLAKE))));
	}

	
}
