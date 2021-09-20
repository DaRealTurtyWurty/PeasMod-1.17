package io.github.darealturtywurty.peasmod.common.items;

import io.github.darealturtywurty.peasmod.core.init.ItemInit;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FrozenPeasItem extends Item {

	private static void spawnItemParticles(final ItemStack stack, final LivingEntity living, final int amount) {
		for (var i = 0; i < amount; ++i) {
			var vec3_0 = new Vec3((living.getRandom().nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
			vec3_0 = vec3_0.xRot(-living.getXRot() * ((float) Math.PI / 180F));
			vec3_0 = vec3_0.yRot(-living.getYRot() * ((float) Math.PI / 180F));
			final double d0 = -living.getRandom().nextFloat() * 0.6D - 0.3D;
			var vec3_1 = new Vec3((living.getRandom().nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
			vec3_1 = vec3_1.xRot(-living.getXRot() * ((float) Math.PI / 180F));
			vec3_1 = vec3_1.yRot(-living.getYRot() * ((float) Math.PI / 180F));
			vec3_1 = vec3_1.add(living.getX(), living.getEyeY(), living.getZ());
			if (living.level instanceof ServerLevel) {
				((ServerLevel) living.level).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack), vec3_1.x,
						vec3_1.y, vec3_1.z, 1, vec3_0.x, vec3_0.y + 0.05D, vec3_0.z, 0.0D);
			} else {
				living.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), vec3_1.x, vec3_1.y, vec3_1.z,
						vec3_0.x, vec3_0.y + 0.05D, vec3_0.z);
			}
		}
	}

	public FrozenPeasItem(final Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
		if (player.isHurt()) {
			player.setHealth(player.getHealth() + player.getMaxHealth() / 2f);
			final ItemStack stack = player.getMainHandItem();
			player.getMainHandItem().hurtAndBreak(1, player, plr -> {
				if (!plr.isSilent() && level.isClientSide()) {
					plr.level.playLocalSound(plr.getX(), plr.getY(), plr.getZ(), SoundEvents.EGG_THROW, plr.getSoundSource(),
							0.8F, 0.8F + plr.level.random.nextFloat() * 0.4F, false);
				}
				spawnItemParticles(stack, plr, 5);
				plr.setItemInHand(hand, new ItemStack(ItemInit.PEA.get(), 8));
				System.out.println("im here!");
			});
			return InteractionResultHolder.success(stack);
		}
		return super.use(level, player, hand);
	}
}
