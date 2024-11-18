package dev.vfyjxf.conduitstratus.api.conduit.trait;

import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import net.minecraft.resources.ResourceLocation;

//TODO:redesign
//TODO:Trait registry
public class TraitType {

    private final ResourceLocation id;
    private final HandleType handleType;
    private final TraitFactory factory;

    public TraitType(
            ResourceLocation id,
            HandleType handleType,
            TraitFactory factory
    ) {
        this.id = id;
        this.handleType = handleType;
        this.factory = factory;
    }

    public ResourceLocation id() {
        return id;
    }

    public HandleType handleType() {
        return handleType;
    }

    public TraitFactory getFactory() {
        return factory;
    }

    @Override

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TraitType that = (TraitType) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "ConduitTraitType{" +
                "id=" + id +
                '}';
    }
}
