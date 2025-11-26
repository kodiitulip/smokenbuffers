package io.github.kodiitulip.smokenbuffers.data;

import net.createmod.catnip.lang.Lang;
import net.minecraft.util.StringRepresentable;

public enum SmokeStackShapeEnum implements StringRepresentable {
    SINGLE, DOUBLE, CONNECTED;

    @Override
    public String getSerializedName() {
        return Lang.asId(name());
    }
}
