package io.github.darealturtywurty.peasmod.core.world;

import com.mojang.serialization.Codec;

import io.github.darealturtywurty.peasmod.core.init.BlockInit;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.Tags.Blocks;
import net.minecraftforge.common.util.Constants.BlockFlags;

public class PeaPlantFeature extends Feature<RandomPatchConfiguration> {

	private static boolean isNearWater(final WorldGenLevel world, final BlockPos pos) {
		for (final BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
			if (world.getFluidState(blockpos).is(FluidTags.WATER))
				return true;
		}

		return FarmlandWaterManager.hasBlockWaterTicket(world, pos);
	}

	public PeaPlantFeature(final Codec<RandomPatchConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(final FeaturePlaceContext<RandomPatchConfiguration> context) {
		final var level = context.level();
		final var config = context.config();
		final var pos = context.origin();
		final var rand = context.random();
		if (rand.nextInt(5) == 0) {
			BlockPos blockpos = null;
			if (config.project) {
				blockpos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, pos);
			} else {
				blockpos = pos;
			}

			var quantity = 0;
			final var mutablePos = new BlockPos.MutableBlockPos();

			for (var tries = 0; tries < config.tries; ++tries) {
				mutablePos.setWithOffset(blockpos, rand.nextInt(config.xspread + 1) - rand.nextInt(config.xspread + 1),
						rand.nextInt(config.yspread + 1) - rand.nextInt(config.yspread + 1),
						rand.nextInt(config.zspread + 1) - rand.nextInt(config.zspread + 1));
				final BlockPos cropPos = mutablePos;
				final BlockPos belowPos = mutablePos.below();
				final var crop = level.getBlockState(cropPos);
				final var below = level.getBlockState(belowPos);
				if (!crop.getMaterial().isSolid() && crop.getFluidState().isEmpty() && below.is(Blocks.DIRT)
						&& isNearWater(level, belowPos)) {
					level.setBlock(belowPos, net.minecraft.world.level.block.Blocks.FARMLAND.defaultBlockState(),
							BlockFlags.BLOCK_UPDATE);
					level.setBlock(cropPos, BlockInit.PEA_PLANT.get().defaultBlockState(), BlockFlags.BLOCK_UPDATE);
					++quantity;
				}
			}

			return quantity > 0;
		}
		return false;
	}
}
