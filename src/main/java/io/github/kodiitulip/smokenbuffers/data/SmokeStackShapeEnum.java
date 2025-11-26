package io.github.kodiitulip.smokenbuffers.data;

import net.createmod.catnip.lang.Lang;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum SmokeStackShapeEnum implements StringRepresentable {
    SINGLE, DOUBLE, CONNECTED;

    @Override
    public @NotNull String getSerializedName() {
        return Lang.asId(name());
    }
}
