package io.github.darealturtywurty.peasmod.common.items;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fmllegacy.RegistryObject;

public class ModBlockNamedItem extends BlockItem {

	private final RegistryObject<? extends Block> block;

	public ModBlockNamedItem(final RegistryObject<? extends Block> block, final Properties properties) {
		super(block.get(), properties);
		this.block = block;
	}

	@Override
	public Block getBlock() {
		return this.block.get();
	}

	@Override
	public String getDescriptionId() {
		return getOrCreateDescriptionId();
	}
}
