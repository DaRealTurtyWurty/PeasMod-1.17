package io.github.darealturtywurty.peasmod.core.events;

import io.github.darealturtywurty.peasmod.PeasMod;
import io.github.darealturtywurty.peasmod.core.init.FeatureInit;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public final class CommonEvents {

	@EventBusSubscriber(modid = PeasMod.MODID, bus = Bus.FORGE)
	public static final class ForgeEvents {
		@SubscribeEvent
		public static void biomeLoading(final BiomeLoadingEvent event) {
			event.getGeneration().getFeatures(Decoration.TOP_LAYER_MODIFICATION).add(FeatureInit.CONFIGURED_PEA_PLANT::get);
		}

		@SubscribeEvent
		public static void commonSetup(final FMLCommonSetupEvent event) {
			Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation(PeasMod.MODID, "pea_plant"),
					FeatureInit.CONFIGURED_PEA_PLANT.get());
		}
	}

	@EventBusSubscriber(modid = PeasMod.MODID, bus = Bus.MOD)
	public static final class ModEvents {

	}

	private CommonEvents() {
		throw new IllegalAccessError("Attempted to construct event subscriber's parent class!");
	}
}
