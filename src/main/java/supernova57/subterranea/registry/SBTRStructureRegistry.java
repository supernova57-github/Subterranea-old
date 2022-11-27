package supernova57.subterranea.registry;

import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import supernova57.subterranea.main.Subterranea;
import supernova57.subterranea.world.gen.structure.MountainFortressStructure;

public class SBTRStructureRegistry {
	
	public static final RegistryObject<StructureType<MountainFortressStructure>> MOUNTAIN_FORTRESS 
		= Subterranea.STRUCTURE_TYPES.register("mountain_fortress", () -> new MountainFortressFeature(Reference.BIG_STRUCTURE_CODEC));
	
	public static void register() {
		Subterranea.STRUCTURE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
}
