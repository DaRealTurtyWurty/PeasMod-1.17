package io.github.darealturtywurty.peasmod.core.init;

import io.github.darealturtywurty.peasmod.PeasMod;
import io.github.darealturtywurty.peasmod.common.blocks.PeaPlantBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockInit {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, PeasMod.MODID);

	public static final RegistryObject<PeaPlantBlock> PEA_PLANT = BLOCKS.register("pea_plant",
			() -> new PeaPlantBlock(BlockBehaviour.Properties.copy(Blocks.CARROTS)));

	private BlockInit() {
		throw new IllegalAccessError("Attempted to construct initialization class.");
	}
}
