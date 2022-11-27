package supernova57.subterranea.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import supernova57.subterranea.registry.SBTREffectRegistry;

@Mod.EventBusSubscriber()
public class SBTRCommonEventHandler {

	@SubscribeEvent
	public static void applyEffects(LivingTickEvent event) {
		LivingEntity entity = event.getEntity();
		
		if (entity != null && entity.getEffect(SBTREffectRegistry.FROSTBITE.get()) != null && entity.canFreeze()) {
			entity.setTicksFrozen(200);	
		}
	}
	
}
