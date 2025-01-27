package dev.vfyjxf.conduitstratus.api.conduit;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record HandleType(ResourceLocation id) {


    public static final Codec<HandleType> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(HandleType::id)
    ).apply(ins, HandleType::new));

    public static final StreamCodec<ByteBuf, HandleType> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            HandleType::id,
            HandleType::new
    );


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
