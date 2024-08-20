package dev.vfyjxf.conduitstratus.api.conduit;

import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTrait;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Predicate;

public record HandleType(ResourceLocation uid, Predicate<ConduitTrait<?>> interoperability) {

    public HandleType(ResourceLocation uid) {
        this(uid, trait -> true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HandleType that = (HandleType) o;
        return uid.equals(that.uid);
    }

    @Override
    public int hashCode() {
        return uid.hashCode();
    }
}
