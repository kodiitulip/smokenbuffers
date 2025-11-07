package io.github.kodiitulip.smokenbuffers.registry;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import io.github.kodiitulip.smokenbuffers.SmokeBuffers;
import io.github.kodiitulip.smokenbuffers.content.smokestack.blockentities.SmokeStackRootBlockEntity;

public class SmokeBuffersBlockEntities {

    public static final CreateRegistrate REGISTRATE = SmokeBuffers.registrate();

    public static final BlockEntityEntry<SmokeStackRootBlockEntity> SMOKE_STACK_BE = REGISTRATE
            .blockEntity("smoke_stack", SmokeStackRootBlockEntity::new)
            .validBlocks(SmokeBuffersBlocks.SMOKE_STACK)
            .register();

    public static void register() {}
}
