package io.github.darealturtywurty.peasmod.core.init;

import java.util.function.Supplier;

import io.github.darealturtywurty.peasmod.PeasMod;
import io.github.darealturtywurty.peasmod.core.world.PeaPlantFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.blockplacers.SimpleBlockPlacer;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class FeatureInit {

	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES,
			PeasMod.MODID);

	public static final RegistryObject<Feature<RandomPatchConfiguration>> PEA_PLANT = FEATURES.register("pea_plant",
			() -> new PeaPlantFeature(RandomPatchConfiguration.CODEC));

	public static final Supplier<ConfiguredFeature<?, ?>> CONFIGURED_PEA_PLANT = () -> PEA_PLANT.get()
			.configured(new RandomPatchConfiguration.GrassConfigurationBuilder(
					new SimpleStateProvider(BlockInit.PEA_PLANT.get().defaultBlockState()), new SimpleBlockPlacer()).build())
			.squared().count(5).rarity(5);

	private FeatureInit() {
		throw new IllegalAccessError("Attempted to construct initialization class.");
	}
}
