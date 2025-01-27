package dev.vfyjxf.conduitstratus.api.conduit.connection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record ConduitNodeId(ResourceKey<Level> dimension, BlockPos pos) {


    public static final Codec<ConduitNodeId> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(ConduitNodeId::dimension),
            BlockPos.CODEC.fieldOf("pos").forGetter(ConduitNodeId::pos)
    ).apply(ins, ConduitNodeId::new));

    public static final StreamCodec<ByteBuf, ConduitNodeId> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION),
            ConduitNodeId::dimension,
            BlockPos.STREAM_CODEC,
            ConduitNodeId::pos,
            ConduitNodeId::new
    );

    public Tag toTag() {
        return CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();
    }

    public static ConduitNodeId fromTag(Tag tag) {
        return CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow();
    }
}
