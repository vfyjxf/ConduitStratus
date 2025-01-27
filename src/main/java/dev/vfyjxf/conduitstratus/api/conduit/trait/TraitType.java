package dev.vfyjxf.conduitstratus.api.conduit.trait;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import net.minecraft.resources.ResourceLocation;

//TODO:redesign
//TODO:Trait registry
public record TraitType(ResourceLocation id, HandleType handleType, TraitFactory factory) {


    public static final Codec<TraitType> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(TraitType::id),
            HandleType.CODEC.fieldOf("handleType").forGetter(TraitType::handleType)
    ).apply(ins, (id, handleType) -> {
        throw new UnsupportedOperationException("Not implemented yet");
    }));

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
