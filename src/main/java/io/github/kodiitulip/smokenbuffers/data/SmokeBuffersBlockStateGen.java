package io.github.kodiitulip.smokenbuffers.data;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import io.github.kodiitulip.smokenbuffers.SmokeBuffers;
import io.github.kodiitulip.smokenbuffers.content.smokestack.blocks.AbstractSmokeStackRootBlock;
import io.github.kodiitulip.smokenbuffers.content.smokestack.blocks.extenders.AbstractSmokeStackExtenderBlock;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;

public class SmokeBuffersBlockStateGen {

    public static <P extends AbstractSmokeStackRootBlock>
    NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> smokeStack() {
        return (c, p) -> {
            BlockModelProvider models = p.models();
            String basePath = "block/smoke_stacks/coal_burner/";
            MultiPartBlockStateBuilder builder = p.getMultipartBuilder(c.get());

            ModelFile.ExistingModelFile topRimS = models.getExistingFile(SmokeBuffers.asResource(basePath + "top_rim"));
            ModelFile.ExistingModelFile singleS = models.getExistingFile(SmokeBuffers.asResource(basePath + "single"));
            ModelFile.ExistingModelFile doubleS = models.getExistingFile(SmokeBuffers.asResource(basePath + "double"));

            builder.part()
                    .modelFile(topRimS)
                    .addModel()
                    .condition(AbstractSmokeStackRootBlock.SHAPE, AbstractSmokeStackRootBlock.SmokeStackBaseShape.DOUBLE)
                    .end()
                    .part()
                    .modelFile(singleS)
                    .addModel()
                    .condition(AbstractSmokeStackRootBlock.SHAPE, AbstractSmokeStackRootBlock.SmokeStackBaseShape.SINGLE)
                    .end()
                    .part()
                    .modelFile(doubleS)
                    .addModel()
                    .condition(AbstractSmokeStackRootBlock.SHAPE,
                            AbstractSmokeStackRootBlock.SmokeStackBaseShape.DOUBLE,
                            AbstractSmokeStackRootBlock.SmokeStackBaseShape.CONNECTED)
                    .end();
        };
    }

    public static <P extends AbstractSmokeStackExtenderBlock>
    NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> smokeStackExtender() {
        return (c, p) -> {
            BlockModelProvider models = p.models();
            String basePath = "block/smoke_stacks/coal_burner/";
            MultiPartBlockStateBuilder builder = p.getMultipartBuilder(c.get());

            ModelFile.ExistingModelFile topRimS = models.getExistingFile(SmokeBuffers.asResource(basePath + "top_rim"));
            ModelFile.ExistingModelFile singleS = models.getExistingFile(SmokeBuffers.asResource(basePath + "single"));
            ModelFile.ExistingModelFile doubleS = models.getExistingFile(SmokeBuffers.asResource(basePath + "double"));

            builder.part()
                    .modelFile(topRimS)
                    .addModel()
                    .condition(AbstractSmokeStackExtenderBlock.SHAPE, AbstractSmokeStackRootBlock.SmokeStackBaseShape.DOUBLE)
                    .end()
                    .part()
                    .modelFile(singleS)
                    .addModel()
                    .condition(AbstractSmokeStackExtenderBlock.SHAPE, AbstractSmokeStackRootBlock.SmokeStackBaseShape.SINGLE)
                    .end()
                    .part()
                    .modelFile(doubleS)
                    .addModel()
                    .condition(AbstractSmokeStackExtenderBlock.SHAPE,
                            AbstractSmokeStackRootBlock.SmokeStackBaseShape.DOUBLE,
                            AbstractSmokeStackRootBlock.SmokeStackBaseShape.CONNECTED)
                    .end();
        };
    }
}
