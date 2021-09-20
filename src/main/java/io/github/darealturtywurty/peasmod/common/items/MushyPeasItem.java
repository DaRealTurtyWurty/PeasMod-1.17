package io.github.darealturtywurty.peasmod.common.items;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MushyPeasItem extends Item {

	public MushyPeasItem(final Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack finishUsingItem(final ItemStack stack, final Level world, final LivingEntity living) {
		if (!world.isClientSide() && living instanceof Player) {
			final var serverPlayer = (ServerPlayer) living;
			final Stat<?> thisStat = Stats.ITEM_USED.get(this);
			serverPlayer.awardStat(thisStat);
			if (serverPlayer.getStats().getValue(thisStat) % 5 == 0) {
				serverPlayer.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 120, 5));
				serverPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 90, 2));
			}
		}
		return super.finishUsingItem(stack, world, living);
	}

	@Override
	public int getUseDuration(final ItemStack stack) {
		return 48;
	}
}
