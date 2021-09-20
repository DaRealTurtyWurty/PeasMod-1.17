package io.github.darealturtywurty.peasmod;

import io.github.darealturtywurty.peasmod.core.init.BlockInit;
import io.github.darealturtywurty.peasmod.core.init.FeatureInit;
import io.github.darealturtywurty.peasmod.core.init.ItemInit;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PeasMod.MODID)
public class PeasMod {

	public static final String MODID = "peasmod";

	public static final CreativeModeTab TAB = new CreativeModeTab(MODID) {

		@Override
		public ItemStack makeIcon() {
			return ItemInit.PEA.get().getDefaultInstance();
		}
	};

	public PeasMod() {
		final var bus = FMLJavaModLoadingContext.get().getModEventBus();

		ItemInit.ITEMS.register(bus);
		BlockInit.BLOCKS.register(bus);
		FeatureInit.FEATURES.register(bus);
	}
}
