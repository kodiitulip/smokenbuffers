package io.github.kodiitulip.smokenbuffers.content.smokestack.behaviours;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class SmokeStackMovementBehaviour implements MovementBehaviour {

    private static class TemporaryData {
        boolean wasStopped = true;
        long movementStartTick = 0;

        public TemporaryData(MovementContext context) {
            this.movementStartTick = 0;
            startMoving(context);
        }

        public void startMoving(MovementContext context) {
            movementStartTick = context.world.getGameTime();
        }

        public long getMovementTicks(MovementContext context) {
            return context.world.getGameTime() - movementStartTick;
        }

        @OnlyIn(Dist.CLIENT)
        void moveParticles(MovementContext context) {

        }
    }

    private final boolean renderAsNormalBlockEntity;
    private final boolean createsSmoke;
    private final boolean spawnExtraSmoke;

    public SmokeStackMovementBehaviour() {
        this(true);
    }

    public SmokeStackMovementBehaviour(boolean spawnExtraSmoke) {
        this(false, true, spawnExtraSmoke);
    }

    public SmokeStackMovementBehaviour(boolean renderAsNormalBlockEntity, boolean createsSmoke, boolean spawnExtraSmoke) {
        this.renderAsNormalBlockEntity = renderAsNormalBlockEntity;
        this.createsSmoke = createsSmoke;
        this.spawnExtraSmoke = spawnExtraSmoke;
    }

    @Override
    public boolean disableBlockEntityRendering() {
        return renderAsNormalBlockEntity;
    }

    @Override
    public void startMoving(MovementContext context) {
        MovementBehaviour.super.startMoving(context);
        // temp data here
    }

    @Override
    public void onSpeedChanged(MovementContext context, Vec3 oldMotion, Vec3 motion) {
        MovementBehaviour.super.onSpeedChanged(context, oldMotion, motion);
        boolean isStopped = Mth.equal(motion.lengthSqr(), 0);
//        if (context.temporaryData instanceof TemporaryData temporaryData && isStopped != temporaryData.wasStopped) {
//            if (!isStopped)
//                temporaryData.startMoving(context);
//            temporaryData.wasStopped = isStopped;
//        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld,
                                    ContraptionMatrices matrices, MultiBufferSource buffer) {
        MovementBehaviour.super.renderInContraption(context, renderWorld, matrices, buffer);
    }

    //    @Environment(EnvType.CLIENT)
//    @Override
//    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
//        if (!(context.temporaryData instanceof TemporaryData temporaryData)) {
//            return;
//        }
//        temporaryData.moveParticles(context);
//        if (true) {
//            return;
//        }
//        ShaderInstance oldShader = RenderSystem.getShader();
//        float[] oldShaderColor = RenderSystem.getShaderColor();
//        {
//            ParticleRenderType renderType = ParticleRenderType.PARTICLE_SHEET_OPAQUE;
//            RenderSystem.setShader(GameRenderer::getParticleShader);
//            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//            Tesselator tesselator = Tesselator.getInstance();
//            BufferBuilder bufferBuilder = tesselator.getBuilder();
//            renderType.begin(bufferBuilder, Minecraft.getInstance().getTextureManager());
//
//            for (ChimneyPushParticle particle : temporaryData.getPushParticles()) {
//                particle.render(bufferBuilder, Minecraft.getInstance().gameRenderer.getMainCamera(), AnimationTickHolder.getPartialTicks(renderWorld));
//            }
//
//            renderType.end(tesselator);
//        }
//        RenderSystem.setShader(() -> oldShader);
//        RenderSystem.setShaderColor(oldShaderColor[0], oldShaderColor[1], oldShaderColor[2], oldShaderColor[3]);
//    }
}
