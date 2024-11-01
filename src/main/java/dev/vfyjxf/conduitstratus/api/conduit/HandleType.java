package dev.vfyjxf.conduitstratus.api.conduit;

import net.minecraft.resources.ResourceLocation;

public record HandleType(ResourceLocation id) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HandleType that = (HandleType) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
