package supernova57.subterranea.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import supernova57.subterranea.registry.SBTRBlockRegistry;
import supernova57.subterranea.registry.SBTREffectRegistry;
import supernova57.subterranea.registry.SBTREntityTypeRegistry;
import supernova57.subterranea.registry.SBTRItemRegistry;
import supernova57.subterranea.registry.SBTRParticleTypeRegistry;
import supernova57.subterranea.registry.SBTRStructureRegistry;

@Mod(Reference.MODID)
public class Subterranea {

	public static final Logger LOGGER = LogManager.getLogger();
        
    //Registries!
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Reference.MODID);
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Reference.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Reference.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MODID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Reference.MODID);
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registry.STRUCTURE_TYPE_REGISTRY, Reference.MODID);
    public static final DeferredRegister<Structure> STRUCTURES = DeferredRegister.create(Registry.STRUCTURE_REGISTRY, Reference.MODID);
    
    public Subterranea() {

    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        
        MinecraftForge.EVENT_BUS.register(this);
        
        SBTRBlockRegistry.register();
        SBTREffectRegistry.register();
        SBTREntityTypeRegistry.register();
        SBTRItemRegistry.register();
        SBTRParticleTypeRegistry.register();
        //SBTRStructureRegistry.register();
        
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    	SBTRParticleTypeRegistry.modifyParticles();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
    	SBTRBlockRegistry.setRenderTypes();
    }

}
