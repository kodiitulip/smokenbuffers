package io.github.kodiitulip.smokenbuffers.registry;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.entry.BlockEntry;
import io.github.kodiitulip.smokenbuffers.SmokeBuffers;
import io.github.kodiitulip.smokenbuffers.content.smokestack.blocks.AbstractSmokeStackRootBlock;
import io.github.kodiitulip.smokenbuffers.content.smokestack.blocks.extenders.AbstractSmokeStackExtenderBlock;
import io.github.kodiitulip.smokenbuffers.data.SmokeBuffersBlockStateGen;

public class SmokeBuffersBlocks {

    public static final CreateRegistrate REGISTRATE = SmokeBuffers.registrate();

    public static final BlockEntry<AbstractSmokeStackRootBlock> SMOKE_STACK = REGISTRATE.block("smoke_stack",
            AbstractSmokeStackRootBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .transform(TagGen.pickaxeOnly())
            .loot(RegistrateBlockLootTables::dropSelf)
            .blockstate(SmokeBuffersBlockStateGen.smokeStack())
            .item()
            .model((c, p) ->
                    p.withExistingParent(c.getName(),
                        p.modLoc("block/smoke_stacks/coal_burner/single")))
            .build()
            .register();

    public static final BlockEntry<AbstractSmokeStackExtenderBlock> SMOKE_STACK_EXTENDER = REGISTRATE.block(
            "smoke_stack_extender", AbstractSmokeStackExtenderBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .transform(TagGen.pickaxeOnly())
            .blockstate(SmokeBuffersBlockStateGen.smokeStackExtender())
            .register();

    public static void register() {
        SmokeBuffers.LOGGER.info("[" + SmokeBuffers.MODNAME + "]: Registering Blocks");
    }
}
