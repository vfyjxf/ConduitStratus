package dev.vfyjxf.conduitstratus.debug;

import dev.vfyjxf.conduitstratus.Constants;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record DebugPackage(
        List<Entry> entries
) implements CustomPacketPayload {

    public void handle(IPayloadContext iPayloadContext) {
        NetworkDebugRender.entries = entries;
    }

    public record Entry(
            BlockPos pos,
            ResourceKey<Level> dimension,
            int distance
    ) {
    }

    public static final CustomPacketPayload.Type<DebugPackage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "network_debug"));


    public static final StreamCodec<ByteBuf, DebugPackage> STREAM_CODEC = StreamCodec.composite(
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC,
                    Entry::pos,
                    ResourceKey.streamCodec(Registries.DIMENSION),
                    Entry::dimension,
                    ByteBufCodecs.VAR_INT,
                    Entry::distance,
                    Entry::new
            ).apply(ByteBufCodecs.collection(ArrayList::new)),
            DebugPackage::entries,
            DebugPackage::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
