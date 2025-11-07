package io.github.kodiitulip.smokenbuffers.content.smokestack.blocks;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.tterrag.registrate.util.entry.BlockEntry;
import io.github.kodiitulip.smokenbuffers.content.smokestack.blockentities.SmokeStackRootBlockEntity;
import io.github.kodiitulip.smokenbuffers.content.smokestack.blocks.extenders.AbstractSmokeStackExtenderBlock;
import io.github.kodiitulip.smokenbuffers.registry.SmokeBuffersBlockEntities;
import io.github.kodiitulip.smokenbuffers.registry.SmokeBuffersBlocks;
import io.github.kodiitulip.smokenbuffers.registry.SmokeBuffersShapes;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbstractSmokeStackRootBlock extends Block implements IWrenchable, ProperWaterloggedBlock,
        IBE<SmokeStackRootBlockEntity> {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;
    public static final EnumProperty<SmokeStackBaseShape> SHAPE = EnumProperty.create("shape",
            SmokeStackBaseShape.class);

    @Override
    public Class<SmokeStackRootBlockEntity> getBlockEntityClass() {
        return SmokeStackRootBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SmokeStackRootBlockEntity> getBlockEntityType() {
        return SmokeBuffersBlockEntities.SMOKE_STACK_BE.get();
    }

    public enum SmokeStackBaseShape implements StringRepresentable {
        SINGLE, DOUBLE, CONNECTED;

        @Override
        public @NotNull String getSerializedName() {
            return Lang.asId(name());
        }
    }

    public AbstractSmokeStackRootBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(ENABLED, true)
                .setValue(WATERLOGGED, false)
                .setValue(SHAPE, SmokeStackBaseShape.SINGLE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(ENABLED, WATERLOGGED, SHAPE));
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (context.getClickLocation().y < pos.getY() + 0.5f || state.getValue(SHAPE) == SmokeStackBaseShape.SINGLE)
            return IWrenchable.super.onSneakWrenched(state, context);

        if (!(world instanceof ServerLevel))
            return InteractionResult.SUCCESS;

        world.setBlock(pos, state.setValue(SHAPE, SmokeStackBaseShape.SINGLE), Block.UPDATE_ALL);
        IWrenchable.playRemoveSound(world, pos);
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null || !canSurvive(state, context.getLevel(), context.getClickedPos()))
            return null;
        return withWater(state, context);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level,
                                                        @NotNull BlockPos pos, @NotNull Player player,
                                                        @NotNull BlockHitResult hitResult) {
        level.setBlock(pos, state.cycle(ENABLED), Block.UPDATE_ALL);
        return InteractionResult.SUCCESS;
    }

    @Override
    @NotNull
    public ItemInteractionResult useItemOn(
            @NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
            @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {

        return useItemOn(stack, level, pos, SmokeBuffersBlocks.SMOKE_STACK, SmokeBuffersBlocks.SMOKE_STACK_EXTENDER);
    }

    @SuppressWarnings("SameParameterValue")
    protected <B extends BlockEntry<? extends AbstractSmokeStackRootBlock>, E extends BlockEntry<?
            extends AbstractSmokeStackExtenderBlock>> ItemInteractionResult useItemOn(
            @NotNull ItemStack stack, @NotNull Level level, @NotNull BlockPos pos, @NotNull B rootBlockEntry,
            @NotNull E extenderBlockEntry) {
        if (rootBlockEntry.isIn(stack)) {
            incrementSize(level, pos, extenderBlockEntry);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private <B extends BlockEntry<? extends AbstractSmokeStackExtenderBlock>> void incrementSize(
            @NotNull LevelAccessor level, BlockPos pos, @NotNull B extenderBlockEntry) {
        BlockState blockState = level.getBlockState(pos);
        if (blockState.getValue(SHAPE) == SmokeStackBaseShape.SINGLE){
            level.setBlock(pos, blockState.setValue(SHAPE, SmokeStackBaseShape.DOUBLE), Block.UPDATE_ALL);
            return;
        }

        BlockPos above = pos.above();
        for (int i = 0; i < 7; i++) {
            blockState = level.getBlockState(above);

            if (extenderBlockEntry.has(blockState)) {
                if (blockState.getValue(SHAPE) == SmokeStackBaseShape.SINGLE) {
                    level.setBlock(above, blockState.setValue(SHAPE, SmokeStackBaseShape.DOUBLE), Block.UPDATE_ALL);
                    return;
                }
                above = above.above();
                continue;
            }

            if (!blockState.canBeReplaced())
                return;

            level.setBlock(above, SmokeBuffersBlocks.SMOKE_STACK_EXTENDER.getDefaultState(), Block.UPDATE_ALL);
            return;
        }
    }

    @Override
    protected @NotNull BlockState updateShape(
            @NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState,
            @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        if (direction.getAxis() != Direction.Axis.Y)
            return state;

        if (direction == Direction.UP) {
            boolean connected = state.getValue(SHAPE) == SmokeStackBaseShape.CONNECTED;
            boolean shouldConnect =
                    level.getBlockState(pos.above()).getBlock() instanceof AbstractSmokeStackExtenderBlock;
            if (!connected && shouldConnect)
                return state.setValue(SHAPE, SmokeStackBaseShape.CONNECTED);
            if (connected && !shouldConnect)
                return state.setValue(SHAPE, SmokeStackBaseShape.DOUBLE);
            return state;
        }
        return !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : state;
    }

    @Override
    protected @NotNull VoxelShape getShape(
            @NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return switch (state.getValue(SHAPE)) {
            case SINGLE -> SmokeBuffersShapes.SMOKE_STACK_SINGLE;
            case DOUBLE -> SmokeBuffersShapes.SMOKE_STACK_DOUBLE;
            case CONNECTED -> SmokeBuffersShapes.SMOKE_STACK_CONNECTED;
        };
    }

    @Override
    protected @NotNull FluidState getFluidState(@NotNull BlockState state) {
        return fluidState(state);
    }

    @Override
    protected boolean isPathfindable(@NotNull BlockState state, @NotNull PathComputationType pathComputationType) {
        return false;
    }

    public static BlockPos findTop(@NotNull LevelAccessor level, @NotNull BlockPos pos) {
        while (level.getBlockState(pos.above()).getBlock() instanceof AbstractSmokeStackExtenderBlock) {
            pos = pos.above();
        }
        return pos;
    }
}
