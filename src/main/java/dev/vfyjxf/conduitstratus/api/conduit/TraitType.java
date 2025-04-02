package dev.vfyjxf.conduitstratus.api.conduit;

import dev.vfyjxf.conduitstratus.api.conduit.trait.TraitFactory;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public final class TraitType extends DeviceType {
    private final HandleType handleType;

    public TraitType(ResourceLocation id, HandleType handleType, TraitFactory factory) {
        super(id, true, factory);
        this.handleType = handleType;
    }

    public HandleType handleType() {
        return handleType;
    }

    @Override
    public TraitFactory factory() {
        return (TraitFactory) super.factory();
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;

        TraitType traitType = (TraitType) object;
        return handleType.equals(traitType.handleType);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + handleType.hashCode();
        return result;
    }
}
