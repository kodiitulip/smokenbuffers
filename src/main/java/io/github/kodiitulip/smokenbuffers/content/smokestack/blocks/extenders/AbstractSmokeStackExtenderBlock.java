package io.github.kodiitulip.smokenbuffers.content.smokestack.blocks.extenders;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.tterrag.registrate.util.entry.BlockEntry;
import io.github.kodiitulip.smokenbuffers.content.smokestack.blocks.AbstractSmokeStackRootBlock;
import io.github.kodiitulip.smokenbuffers.registry.SmokeBuffersBlocks;
import io.github.kodiitulip.smokenbuffers.registry.SmokeBuffersShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class AbstractSmokeStackExtenderBlock extends Block implements IWrenchable {

    public static final EnumProperty<AbstractSmokeStackRootBlock.SmokeStackBaseShape> SHAPE = EnumProperty.create("shape",
            AbstractSmokeStackRootBlock.SmokeStackBaseShape.class);

    public AbstractSmokeStackExtenderBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(SHAPE, AbstractSmokeStackRootBlock.SmokeStackBaseShape.SINGLE));
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, @NotNull UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (context.getClickLocation().y < pos.getY() + 0.5f || state.getValue(SHAPE) == AbstractSmokeStackRootBlock.SmokeStackBaseShape.SINGLE)
            return IWrenchable.super.onSneakWrenched(state, context);

        if (!(world instanceof ServerLevel))
            return InteractionResult.SUCCESS;

        world.setBlock(pos, state.setValue(SHAPE, AbstractSmokeStackRootBlock.SmokeStackBaseShape.SINGLE), Block.UPDATE_ALL);
        IWrenchable.playRemoveSound(world, pos);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(SHAPE));
    }

    @Override
    protected boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return (below.getBlock() instanceof AbstractSmokeStackExtenderBlock || SmokeBuffersBlocks.SMOKE_STACK.has(below))
                && below.getValue(SHAPE) != AbstractSmokeStackRootBlock.SmokeStackBaseShape.SINGLE;
    }

    @Override
    protected @NotNull VoxelShape getShape(
            BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return switch (state.getValue(SHAPE)) {
            case SINGLE -> SmokeBuffersShapes.SMOKE_STACK_SINGLE;
            case DOUBLE -> SmokeBuffersShapes.SMOKE_STACK_DOUBLE;
            case CONNECTED -> SmokeBuffersShapes.SMOKE_STACK_CONNECTED;
        };
    }


    @Override
    protected @NotNull ItemInteractionResult useItemOn(
            @NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
            @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        return useItemOn(stack, level, pos, player, hand, hitResult, SmokeBuffersBlocks.SMOKE_STACK);
    }

    @SuppressWarnings("SameParameterValue")
    protected @NotNull <T extends BlockEntry<? extends AbstractSmokeStackRootBlock>> ItemInteractionResult useItemOn(
           @NotNull ItemStack stack, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player,
           @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult, @NotNull T blockEntry) {
        if (!blockEntry.isIn(stack))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        BlockPos root = findRoot(level, pos);
        BlockState rootState = level.getBlockState(root);
        if (rootState.getBlock() instanceof AbstractSmokeStackRootBlock smokeStack) {
            return smokeStack.useItemOn(stack, rootState, level, root, player, hand,
                    new BlockHitResult(hitResult.getLocation(), hitResult.getDirection(), root, hitResult.isInside()));
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, @NotNull UseOnContext context) {
        Level level = context.getLevel();
        BlockPos root = findRoot(level, context.getClickedPos());
        BlockState rootState = level.getBlockState(root);
        if (rootState.getBlock() instanceof AbstractSmokeStackRootBlock smokeStack)
            return smokeStack.onWrenched(rootState, relocateContext(context, root));
        return IWrenchable.super.onWrenched(state, context);
    }

    @Override
    protected @NotNull BlockState updateShape(
            @NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState,
            @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        if (direction.getAxis() != Direction.Axis.Y)
            return state;

        if (direction == Direction.UP) {
            boolean connected = state.getValue(SHAPE) == AbstractSmokeStackRootBlock.SmokeStackBaseShape.CONNECTED;
            boolean shouldConnect = level.getBlockState(pos.above()).is(this);
            if (!connected && shouldConnect)
                return state.setValue(SHAPE, AbstractSmokeStackRootBlock.SmokeStackBaseShape.CONNECTED);
            if (connected && !shouldConnect)
                return state.setValue(SHAPE, AbstractSmokeStackRootBlock.SmokeStackBaseShape.DOUBLE);
            return state;
        }
        return !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : state;
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(
            @NotNull BlockState state, @NotNull HitResult target, @NotNull LevelReader level, @NotNull BlockPos pos,
            @NotNull Player player) {
        return SmokeBuffersBlocks.SMOKE_STACK.asStack();
    }

    private BlockPos findRoot(@NotNull LevelAccessor level, @NotNull BlockPos pos) {
        BlockPos current = pos.below();
        while (!SmokeBuffersBlocks.SMOKE_STACK.has(level.getBlockState(current))) {
            current = current.below();
        }
        return current;
    }

    protected UseOnContext relocateContext(@NotNull UseOnContext context, BlockPos pos) {
        assert context.getPlayer() != null;
        return new UseOnContext(context.getPlayer(), context.getHand(), new BlockHitResult(context.getClickLocation()
                , context.getClickedFace(), pos, context.isInside()));
    }
}
