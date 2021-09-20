package io.github.darealturtywurty.peasmod.common.blocks;

import java.util.Random;

import io.github.darealturtywurty.peasmod.core.init.ItemInit;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.PlantType;

public class PeaPlantBlock extends BushBlock implements BonemealableBlock {
	public static final EnumProperty<PeaGrowthStage> STAGE = EnumProperty.create("stage", PeaGrowthStage.class);
	private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[] { Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 11.0D, 16.0D), Shapes.block(), Block.box(0, 0, 0, 16, 11, 16), Shapes.block(),
			Shapes.block(), Shapes.block(), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 13.0D, 16.0D) };

	public PeaPlantBlock(final Properties properties) {
		super(properties);
	}

	@Override
	public boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
		boolean b = super.canSurvive(state, level, pos);
		if (state.getValue(STAGE) == PeaGrowthStage.TOP0) {
			final var stateBelow = level.getBlockState(pos.below());
			b = stateBelow.getBlock() instanceof PeaPlantBlock
					&& stateBelow.getValue(STAGE).ordinal() >= PeaGrowthStage.BOTTOM3.ordinal();
		}
		return b;
	}

	@Override
	protected void createBlockStateDefinition(final Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(STAGE);
	}

	public float getGrowthSpeed(final Level level, final BlockPos pos, final float light) {
		float growth = 0.125f * (light - 11);
		if (level.canSeeSky(pos)) {
			growth += 2f;
		}

		final var soil = level.getBlockState(pos.below());
		if (soil.getBlock().isFertile(soil, level, pos.below())) {
			growth *= 1.5f;
		}

		return 1f + growth;
	}

	public PeaGrowthStage getMaxStage() {
		return PeaGrowthStage.BOTTOM6;
	}

	public PeaGrowthStage getMinStage() {
		return PeaGrowthStage.BOTTOM0;
	}

	@Override
	public ItemStack getPickBlock(final BlockState state, final HitResult target, final BlockGetter level,
			final BlockPos pos, final Player player) {
		return ItemInit.PEA.get().getDefaultInstance();
	}

	@Override
	public PlantType getPlantType(final BlockGetter level, final BlockPos pos) {
		return PlantType.CROP;
	}

	@Override
	public VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos,
			final CollisionContext context) {
		return SHAPE_BY_AGE[state.getValue(STAGE).ordinal()];
	}

	@Override
	public float getSpeedFactor() {
		return 0.75f;
	}

	@Override
	public boolean isBonemealSuccess(final Level level, final Random rand, final BlockPos pos, final BlockState state) {
		return isValidBonemealTarget(level, pos, state, level.isClientSide);
	}

	@Override
	public boolean isValidBonemealTarget(final BlockGetter level, final BlockPos pos, final BlockState state,
			final boolean isClient) {
		final PeaGrowthStage stage = state.getValue(STAGE);
		if (stage != getMaxStage() && stage != PeaGrowthStage.TOP0)
			return true;
		return stage == PeaGrowthStage.BOTTOM6 && level.getBlockState(pos.above()).getBlock() != this;
	}

	@Override
	protected boolean mayPlaceOn(final BlockState state, final BlockGetter level, final BlockPos pos) {
		return state.getBlock() == Blocks.FARMLAND;
	}

	@Override
	public void onNeighborChange(final BlockState state, final LevelReader level, final BlockPos pos,
			final BlockPos neighbor) {
		super.onNeighborChange(state, level, pos, neighbor);
		if (level instanceof Level) {
			final var realLevel = (Level) level;
			if (pos.getX() == neighbor.getX() && pos.getZ() == neighbor.getZ() && pos.getY() == neighbor.getY() + 1
					&& realLevel.getBlockState(neighbor).getValue(STAGE) == PeaGrowthStage.TOP0) {
				realLevel.destroyBlock(pos, true);
				realLevel.destroyBlock(pos.above(), true);
			}

			if (realLevel.getBlockState(pos).getValue(STAGE) != PeaGrowthStage.TOP0) {
				realLevel.updateNeighborsAt(pos.above(), this);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(final BlockState state, final Level level, final BlockPos pos, final BlockState newState,
			final boolean isMoving) {
		super.onRemove(state, level, pos, newState, isMoving);
		if (state.getValue(STAGE) == PeaGrowthStage.TOP0) {
			level.destroyBlock(pos.below(), true);
		}
	}

	@Override
	public void performBonemeal(final ServerLevel level, final Random rand, final BlockPos pos, final BlockState state) {
		PeaGrowthStage stage = state.getValue(STAGE);
		if (stage != PeaGrowthStage.TOP0 && stage != PeaGrowthStage.BOTTOM6) {
			final int span = getMaxStage().ordinal() - stage.ordinal();
			final int growBy = this.RANDOM.nextInt(span) + 1;
			PeaGrowthStage newStage = stage;
			for (var stageAdd = 0; stageAdd < growBy; stageAdd++) {
				newStage = newStage.next();
			}

			level.setBlockAndUpdate(pos, state.setValue(STAGE, newStage));
			stage = newStage;
		}

		if (level.isEmptyBlock(pos.above()) && stage.ordinal() >= PeaGrowthStage.BOTTOM3.ordinal()
				&& stage != PeaGrowthStage.TOP0) {
			level.setBlockAndUpdate(pos.above(), defaultBlockState().setValue(STAGE, PeaGrowthStage.TOP0));
		}
	}

	@Override
	public void randomTick(final BlockState state, final ServerLevel level, final BlockPos pos, final Random random) {
		final int light = level.getMaxLocalRawBrightness(pos);
		if (light >= 12) {
			final PeaGrowthStage stage = state.getValue(STAGE);
			final float speed = getGrowthSpeed(level, pos, light);
			if (random.nextInt((int) (50f / speed) + 1) == 0) {
				level.setBlockAndUpdate(pos, state.setValue(STAGE, stage.next()));

				if (stage.ordinal() >= PeaGrowthStage.BOTTOM3.ordinal() && level.isEmptyBlock(pos.above())
						&& stage != PeaGrowthStage.TOP0) {
					level.setBlockAndUpdate(pos.above(), defaultBlockState().setValue(STAGE, PeaGrowthStage.TOP0));
				}
			}
		}
	}
}
