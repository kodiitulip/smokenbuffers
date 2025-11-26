package io.github.kodiitulip.smokenbuffers.content.smokestack.blockentities;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import io.github.kodiitulip.smokenbuffers.content.smokestack.blocks.AbstractSmokeStackRootBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SmokeStackRootBlockEntity extends SmartBlockEntity {
    public SmokeStackRootBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    @Override
    public void tick() {
        super.tick();

        if (level == null || !level.isClientSide())
            return;

        if (!getBlockState().getValue(AbstractSmokeStackRootBlock.ENABLED))
            return;

        RandomSource random = level.getRandom();
        Vec3 top = AbstractSmokeStackRootBlock.findTop(level, getBlockPos());
        level.addAlwaysVisibleParticle(
            ParticleTypes.CAMPFIRE_COSY_SMOKE, true,
            top.x() + random.nextDouble() / (random.nextBoolean() ? 3D : -3D),
            top.y() + random.nextDouble() + random.nextDouble(),
            top.z() + random.nextDouble() / (random.nextBoolean() ? 3D : -3D),
            0.0D, 0.07D, 0.0D
        );
    }
}
