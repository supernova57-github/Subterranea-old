package supernova57.subterranea.world.gen.structure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import supernova57.subterranea.main.Subterranea;
import supernova57.subterranea.registry.SBTRStructureRegistry;

public class MountainFortressStructure extends Structure {
	
	public static final HashSet<Class<?>> REPLACEABLE_BLOCK_TYPES 
		= new HashSet<>(Set.of(AirBlock.class, LiquidBlock.class, BushBlock.class,
				GrassBlock.class, DoublePlantBlock.class,
				TallGrassBlock.class, StairBlock.class, DirtPathBlock.class)); 
	
	public static final HashSet<Class<?>> GROUND_BLOCK_TYPES 
		= new HashSet<>(Set.of(GrassBlock.class, LiquidBlock.class)); 
	
	public static final Codec<MountainFortressStructure> CODEC 
		= RecordCodecBuilder.create(configInstance 
			-> configInstance.group(
					settingsCodec(configInstance),
					StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter((fortress) -> fortress.startPool),
					Codec.intRange(0, 15).fieldOf("size").forGetter((fortress) -> fortress.size)
				)
				.apply(configInstance, MountainFortressStructure::new
			));
	
	public static final StructureType<MountainFortressStructure> STRUCTURE_TYPE = () -> CODEC;
	
	private final Holder<StructureTemplatePool> startPool;
	private final int size;

	public MountainFortressStructure(Structure.StructureSettings settings, 
			Holder<StructureTemplatePool> startPool,
			int size) {
		
		super(settings);
		
		this.startPool = startPool;
		this.size = size;

	}
	

	@Override
	public Decoration step() {
		return Decoration.SURFACE_STRUCTURES;
	}

	
	public void afterPlace(WorldGenLevel world, StructureManager manager, ChunkGenerator generator, RandomSource random, BoundingBox boundary, ChunkPos chunkPos, PiecesContainer container) {
		
		generateFoundation(world, manager, generator, random, boundary, chunkPos, container);
		generateStaircase(world, manager, generator, random, boundary, chunkPos, container);
	}
	
	public static void generateFoundation(WorldGenLevel world, StructureManager manager, ChunkGenerator generator, 
			RandomSource random, BoundingBox boundary, ChunkPos chunkPos, PiecesContainer container) {
		
		StructurePiece startingPiece = container.pieces().get(0);
		BoundingBox startingPieceBoundary = startingPiece.getBoundingBox();

		int startingX; 
		final int startingY = container.calculateBoundingBox().maxY() - 22;
		int startingZ;
		
		switch (startingPiece.getRotation()) {
			case NONE: // SOUTH-facing
				startingX = startingPieceBoundary.minX() - 17;
				startingZ = startingPieceBoundary.minZ() - 18;
				break;
			case CLOCKWISE_90: // WEST-facing
				startingX = startingPieceBoundary.minX();
				startingZ = startingPieceBoundary.minZ() - 17;
				break;
			case CLOCKWISE_180: // NORTH-facing
				startingX = startingPieceBoundary.minX() - 17;
				startingZ = startingPieceBoundary.minZ();
				break;
			case COUNTERCLOCKWISE_90: // EAST-facing
				startingX = startingPieceBoundary.minX() - 18;
				startingZ = startingPieceBoundary.minZ() - 17;
				break;
			default:
				startingX = startingPieceBoundary.minX() + 17;
				startingZ = startingPieceBoundary.minZ();
				break;
		}
		
		int y = startingY;
		BlockPos pos; 
		
		HashSet<Integer> terminatedColumns = new HashSet<>();
		
		while (y >= 60) {
			for (int i = 0; i < SBTRStructureData.MOUNTAIN_FORTRESS_FOUNDATION_BLOCKS.length; i++) {
						
				if (terminatedColumns.contains(i) || SBTRStructureData.MOUNTAIN_FORTRESS_FOUNDATION_BLOCKS[i] == 0) continue;
				
				pos = new BlockPos(startingX + (i % 58), y, startingZ + (int)(i / 58));
				
				if (chunkPos.equals(new ChunkPos(pos))) {
					if (REPLACEABLE_BLOCK_TYPES.contains(world.getBlockState(pos).getBlock().getClass())) {
						world.setBlock(pos, Blocks.STONE_BRICKS.defaultBlockState(), 2);
					} else if (world.getBlockState(pos).getBlock().equals(Blocks.WHITE_WOOL)) {
						/* Replaces the white wool marker block. (Prevents the loop from terminating
						before the coal block is replaced). */
						world.setBlock(pos, Blocks.STONE_BRICKS.defaultBlockState(), 2);

					} else if (world.getBlockState(pos).getBlock().equals(Blocks.COAL_BLOCK)) {
						// Replaces the coal marker block indicating an important cavity for redstone.
						world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
					} else {
						terminatedColumns.add(i);
					}
				} else {
					terminatedColumns.add(i);
				}
			}
			y--;
			if (terminatedColumns.size() == SBTRStructureData.MOUNTAIN_FORTRESS_FOUNDATION_BLOCKS.length) break;
		}	
		

	}
	
	/** 
	 * Generate a staircase for the Mountain Fortress.
	 */
	public static void generateStaircase(WorldGenLevel world, StructureManager manager, ChunkGenerator generator, 
			RandomSource random, BoundingBox boundary, ChunkPos chunkPos, PiecesContainer container) {
		
		// Get the "Castle Gate" piece (it is the first to be generated, so it has index 0).
		StructurePiece startingPiece = container.pieces().get(0);
		BoundingBox startingPieceBoundary = startingPiece.getBoundingBox(); // Get the piece's boundaries.

		// Declare some local variables to store the starting point for staircase generation.
		int startingX; 
		int startingZ;
		
		/*
		 *  Declare some more local variables to track the direction of generation on each axis.
		 *  Initialize both of them to zero.
		 */
		int xIncrement = 0;
		int zIncrement = 0;
		
		/*
		 *  Based upon the rotation of the castle gate, populate the starting point
		 *  variables to a block just outside the gate and set the direction of generation.
		 */
		switch (startingPiece.getRotation()) {
			case NONE: // SOUTH-facing
				startingX = startingPieceBoundary.minX() + 7;
				startingZ = startingPieceBoundary.maxZ() - 1;
				zIncrement = 1;
				break;
			case CLOCKWISE_90: // WEST-facing
				startingX = startingPieceBoundary.minX() + 1;
				startingZ = startingPieceBoundary.minZ() + 7;
				xIncrement = -1;
				break;
			case CLOCKWISE_180: // NORTH-facing
				startingX = startingPieceBoundary.minX() + 7;
				startingZ = startingPieceBoundary.minZ() + 1;
				zIncrement = -1;
				break;
			case COUNTERCLOCKWISE_90: // EAST-facing
				startingX = startingPieceBoundary.maxX() - 1;
				startingZ = startingPieceBoundary.minZ() + 7;
				xIncrement = 1;
				break;
			default:
				startingX = startingPieceBoundary.minX() + 7;
				startingZ = startingPieceBoundary.maxZ() - 1;
				zIncrement = 1;
				break;
		}
		
		/* 
		 * Declare a local variable to store the y-level we are currently generating at.
		 * 
		 * Initialize to 20 blocks below the top of the structure (i.e., one block above
		 * the floor of the castle gate).
		 */
		int y = container.calculateBoundingBox().maxY() - 20;
		
		/*
		 *  Declare a local variable to track the number of the layer we are generating
		 *  as we go downwards.
		 *  
		 *  Initialize to 1.
		 */
		int layer = 1;
		
		// Declare a local variable to store the y-level at which ground is found.
		int minY = 0;
		
				
		/*
		 *  Declare a local HashSet to store the x-z coordinates of columns we want
		 *  to stop generating.
		 */
		HashSet<List<Integer>> terminatedColumns = new HashSet<>();		
		
		
		// Start a loop to descend y-levels (stop no matter what after 45 layers).
//		staircaseGeneration:
		while (y >= container.calculateBoundingBox().maxY() - 20 - 45) {
			
			// Run this code if the castle gate is facing north or south.
			if (zIncrement == 0) {
				/*
				 * Start a loop to generate rows along the width of the castle gate.
				 * 
				 * Restrict z so that we don't generate stairs that connect to the 
				 * towers too!
				 */
				for (int z = startingZ; z <= startingPieceBoundary.maxZ() - 7; z++) {
					
					/*
					 *  Check the position of this row to make sure we want to generate
					 *  stairs rather than the sides of the staircase.
					 */
					if (z != startingZ && z != startingPieceBoundary.maxZ() - 7) {
						
						/*
						 *  Only generate the "stairs" of the staircase after the first layer,
						 *  which is used only to start the sides of the staircase.
						 */
						if (layer > 1) {
							// If we haven't hit ground yet, keep generating stairs.
							if (y >= minY && z > startingZ && z < startingPieceBoundary.maxZ() - 7) {
								if (generateStaircaseRow(startingX, y, z, layer, world, chunkPos, xIncrement, zIncrement)) {
									minY = y;
									Subterranea.LOGGER.info("GROUND FOUND AT Y = " + y);

								}
							} else { // If we have hit the ground, generate a foundation for those stairs.
								generateStaircaseFoundation(startingX, y, z, layer, world, chunkPos, xIncrement, zIncrement, terminatedColumns, false);
							}
						}
					} else { // If this isn't the place to generate stairs, generate a side of the staircase!
						if (y >= minY) {
							generateStaircaseSide(startingX, y, z, layer, world, chunkPos, xIncrement, zIncrement);
						} else {
							generateStaircaseFoundation(startingX, y, z, layer, world, chunkPos, xIncrement, zIncrement, terminatedColumns, true);
						}
					}
				}
			} else { // Run this code if the castle gate is facing east or west.
				
				/*
				 * Start a loop to generate rows along the width of the castle gate.
				 * 
				 * Restrict x so that we don't generate stairs that connect to the 
				 * towers too!
				 */
				for (int x = startingX; x <= startingPieceBoundary.maxX() - 7; x++) {
					
					/*
					 *  Check the position of this row to make sure we want to generate
					 *  stairs rather than the sides of the staircase.
					 */
					if (x != startingX && x != startingPieceBoundary.maxX() - 7) {
						
						/*
						 *  Only generate the "stairs" of the staircase after the first layer,
						 *  which needs to be used for the sides of the staircase.
						 */
						if (layer > 1) {
							// If we haven't hit ground yet, keep generating stairs.
							if (y >= minY && x > startingX && x < startingPieceBoundary.maxX() - 7) {
								if (generateStaircaseRow(x, y, startingZ, layer, world, chunkPos, xIncrement, zIncrement)) {
									minY = y;
									
									
									Subterranea.LOGGER.info("GROUND FOUND AT Y = " + y);
								}
							} else { // If we have hit the ground, generate a foundation for those stairs.
								generateStaircaseFoundation(x, y, startingZ, layer, world, chunkPos, xIncrement, zIncrement, terminatedColumns, false);
							}
						} 
					} else { // If this isn't the place to generate stairs, generate a side of the staircase!
						if (y >= minY) {
							generateStaircaseSide(x, y, startingZ, layer, world, chunkPos, xIncrement, zIncrement);
						} else {
							generateStaircaseFoundation(x, y, startingZ, layer, world, chunkPos, xIncrement, zIncrement, terminatedColumns, true);
						}
					}
				}
			}
			
			
			y--; // Descend one y-level.
			layer++; // Increase the layer counter by one.
		}	

	}

	public static boolean generateStaircaseRow(int startingX, int y, int startingZ, int layer, WorldGenLevel world, ChunkPos chunkPos, 
			int xIncrement, int zIncrement) {
						
		int x = 0;
		int z = 0;
		BlockPos pos;
		
		//Subterranea.LOGGER.info(deadChunks);
		
		boolean hasHitGround = false;
		
		BlockState currentBlockState;
		Block currentBlock;
		
		boolean isInProperChunk;
		
		// Generate "stairs" of staircase.
		
		for (int i = 0; i <= (layer * 2 + 7); i++) {
			
			isInProperChunk = true;
			
			pos = new BlockPos(startingX + x, y, startingZ + z);
			
			ChunkPos currentPos = new ChunkPos(pos);
			if (Math.abs(chunkPos.x - currentPos.x) > 3 
					|| Math.abs(chunkPos.z - currentPos.z) > 3) break;
			
			//Subterranea.LOGGER.info("XYZ:" + pos + "HEIGHT:" + world.getHeight(Types.WORLD_SURFACE, pos.getX(), pos.getZ()));

			//if (i == layer * 2 + 7) Subterranea.LOGGER.info("CP:" + chunkPos +  " XYZ:" + pos + "HEIGHT (WG):" + world.getHeight(Types.WORLD_SURFACE_WG, pos.getX(), pos.getZ()));
			//world.getHeight(Types.WORLD_SURFACE_WG, pos.getX(), pos.getZ());
			
			if (!chunkPos.equals(new ChunkPos(pos))) {
				isInProperChunk = false;
			} 
			
			currentBlockState = isInProperChunk ? world.getBlockState(pos) : null;
			currentBlock = currentBlockState != null ? currentBlockState.getBlock() : null;
			
			
			
			if (/*isInProperChunk && */(currentBlock instanceof AirBlock || currentBlock instanceof SnowyDirtBlock || currentBlock instanceof BushBlock)
					&& world.getBlockState(pos.above(50)).getBlock() instanceof AirBlock) {	
				if (i <= layer * 2)  {
					//if (REPLACEABLE_BLOCK_TYPES.contains(world.getBlockState(pos).getBlock().getClass())) {
						if (i == layer * 2) {
							world.setBlock(pos, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 2);
							for (int j = 1; j <= 15; j++) {
								BlockState state = world.getBlockState(pos.above(j));
								if (!state.is(BlockTags.STONE_BRICKS)
										&& state.getBlock() != Blocks.STONE_BRICK_SLAB
										&& state.getBlock() != Blocks.MOSSY_STONE_BRICK_SLAB) {
									world.setBlock(pos.above(j), Blocks.AIR.defaultBlockState(), 2);
								}
							}
						} else {
							world.setBlock(pos, Blocks.STONE_BRICKS.defaultBlockState(), 2);
						}
					//}
					
				} else {
					world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
					if (currentBlock.equals(Blocks.DIRT)) world.setBlock(pos.below(), Blocks.GRASS_BLOCK.defaultBlockState(), 2);
				} 

			} 
			
			if (i == (layer * 2) - 1) {
				if (layer != 1 && world.getHeight(Types.WORLD_SURFACE_WG, pos.getX(), pos.getZ()) >= pos.getY()) {
					hasHitGround = true;
					//break; TODO
				} 
			}
			
			x += xIncrement;
			z += zIncrement;
		}
		
		return hasHitGround;

	}
	
	public static void generateStaircaseSide(int startingX, int y, int startingZ, int layer, WorldGenLevel world, ChunkPos chunkPos, 
			int xIncrement, int zIncrement) {
				
		int x = 0;
		int z = 0;
		BlockPos pos;
		
		// Generate "side" of staircase.
		
		for (int i = 0; i < layer * 2 + 4; i++) {
			
			
			pos = new BlockPos(startingX + x, y, startingZ + z);
			
			if (chunkPos.equals(new ChunkPos(pos)) 
					&& (world.getBlockState(pos).getBlock() instanceof AirBlock
						|| world.getBlockState(pos).getBlock() instanceof SnowyDirtBlock)
					&& world.getBlockState(pos.above(50)).getBlock() instanceof AirBlock) {	
				/*
				if (deadChunks.contains(new ChunkPos(pos.north((16))))
						|| deadChunks.contains(new ChunkPos(pos.east((16))))
						|| deadChunks.contains(new ChunkPos(pos.south((16))))
						|| deadChunks.contains(new ChunkPos(pos.west((16)))) ) {
					continue;
				}*/
				
				Direction facingDirection;
				
				if (zIncrement == 0) {
					
					/*
					if (deadChunks.contains(new ChunkPos(pos.north((16 * zIncrement))))) {
						if (!deadChunks.contains(chunkPos)) deadChunks.add(chunkPos);
						continue;
					}
					*/
					
					facingDirection = (xIncrement == 1 ? Direction.WEST: Direction.EAST);
					
				} else {
					
					/*
					if (deadChunks.contains(new ChunkPos(pos.west((16 * xIncrement))))) {
						if (!deadChunks.contains(chunkPos)) deadChunks.add(chunkPos);
						continue;
					}
					*/
					facingDirection = (zIncrement == 1 ? Direction.NORTH : Direction.SOUTH);
				}
				
				if (layer == 1) {
					if (i % 2 == 0) {
						world.setBlock(pos, Blocks.STONE_BRICK_STAIRS.defaultBlockState()
								.setValue(StairBlock.FACING, facingDirection)
								.setValue(StairBlock.HALF, Half.TOP),
						2);
					} else {
						if (i == 5) {
							world.setBlock(pos, Blocks.STONE_BRICK_STAIRS.defaultBlockState()
									.setValue(StairBlock.FACING, facingDirection), 2);
							for (int j = 1; j <= 15; j++) {
								world.setBlock(pos.above(j), Blocks.AIR.defaultBlockState(), 2);
							}
						} else {
							world.setBlock(pos, Blocks.STONE_BRICKS.defaultBlockState(), 2);
						}
					}
				} else {
				
					if (i == (layer * 2) + 2) {
						world.setBlock(pos, Blocks.STONE_BRICK_STAIRS.defaultBlockState()
								.setValue(StairBlock.FACING, facingDirection)
								.setValue(StairBlock.HALF, Half.TOP),
						2);
					} else if (i == (layer * 2) + 3) {
						world.setBlock(pos, Blocks.STONE_BRICK_STAIRS.defaultBlockState()
								.setValue(StairBlock.FACING, facingDirection), 2);
						for (int j = 1; j <= 15; j++) {
							world.setBlock(pos.above(j), Blocks.AIR.defaultBlockState(), 2);
						}
					} else {
						if (layer % 2 == 1 && i % 2 == 1 && i <= layer * 2) {
							world.setBlock(pos, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 2);
						} else {
							world.setBlock(pos, Blocks.STONE_BRICKS.defaultBlockState(), 2);
						}
					}
				
				}

			} 
			
			x += xIncrement;
			z += zIncrement;
		}

	}
	
	public static void generateStaircaseFoundation(int startingX, int y, int startingZ, int layer, WorldGenLevel world, ChunkPos chunkPos, 
			int xIncrement, int zIncrement, HashSet<List<Integer>> terminatedColumns, boolean patterned) {
		
		
		int x = 0;
		int z = 0;
		BlockPos pos;
		
		// Generate "foundation" of staircase.
		
		for (int i = 0; i <= (layer * 2); i++) {
			
			
			pos = new BlockPos(startingX + x, y, startingZ + z);
			
			List<Integer> blockColumn = new ArrayList<>(2);
			blockColumn.add(pos.getX());
			blockColumn.add(pos.getZ());

			if (i == (layer * 2)) Subterranea.LOGGER.info("Y VALUE (B): " + y);

			if (terminatedColumns.contains(blockColumn)) continue;
			if (i == (layer * 2)) Subterranea.LOGGER.info("Y VALUE: " + y);

			if (chunkPos.equals(new ChunkPos(pos))) {
				if ((REPLACEABLE_BLOCK_TYPES.contains(world.getBlockState(pos).getBlock().getClass())
							|| world.getBlockState(pos).getBlock().equals(Blocks.DIRT))
						&& !(world.getBlockState(pos.above()).getBlock() instanceof AirBlock)
						&& (world.getBlockState(pos.above(5)).is(BlockTags.STONE_BRICKS)
							|| world.getBlockState(pos.above(50)).getBlock() instanceof AirBlock)) {
				
					if (patterned && layer % 2 == 1 && i % 2 == 1 && i <= layer * 2) {
						world.setBlock(pos, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 2);
					} else {
						world.setBlock(pos, Blocks.STONE_BRICKS.defaultBlockState(), 2);

					}
				} else {
					terminatedColumns.add(blockColumn);
				}
			}
			
			x += xIncrement;
			z += zIncrement;
		}
		
	}

	@Override
	public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
		
		int x = context.chunkPos().getMiddleBlockX();
		int z = context.chunkPos().getMiddleBlockZ();
		int y = context.chunkGenerator().getBaseHeight(
				x,
				z, 
				Types.WORLD_SURFACE_WG,
				context.heightAccessor(),
				context.randomState()
			);

		int[][] testPositions = {{-50, 0}, {50, 0}, {0, -50}, {0, 50}};
		for (int[] coordPair : testPositions) {
			int testY = context.chunkGenerator().getBaseHeight(
					x + coordPair[0],
					z + coordPair[1], 
					Types.WORLD_SURFACE_WG,
					context.heightAccessor(),
					context.randomState()
				);
			// Make sure the ground is relatively level.
			if (testY < y - 13 || testY > y + 13) return Optional.empty();	
		}
				
		if (y < 80) return Optional.empty();
		
		return JigsawPlacement.addPieces(
				context, 
				this.startPool, 
				Optional.empty(), 
				this.size, // Depth
				new BlockPos(x, y, z), 
				false, 
				Optional.empty(), 
				128 // Max dist. from center
			);
	}

	@Override
	public StructureType<?> type() {
		return SBTRStructureRegistry.MOUNTAIN_FORTRESS.get();
	}

}
