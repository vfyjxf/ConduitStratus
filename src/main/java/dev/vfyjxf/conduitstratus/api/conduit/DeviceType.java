package dev.vfyjxf.conduitstratus.api.conduit;

import com.mojang.serialization.Codec;
import dev.vfyjxf.conduitstratus.api.StratusRegistries;
import dev.vfyjxf.conduitstratus.api.conduit.device.DeviceFactory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public sealed class DeviceType permits TraitType {

    private final ResourceLocation id;
    private final boolean attachable;
    private final DeviceFactory factory;

    public static final Codec<DeviceType> CODEC = Codec.lazyInitialized(StratusRegistries.DEVICE_TYPE_REGISTRY::byNameCodec);

    public static final StreamCodec<RegistryFriendlyByteBuf, DeviceType> STREAM_CODEC = StreamCodec.recursive(
            streamCodec -> ByteBufCodecs.registry(StratusRegistries.DEVICE_TYPE)
    );

    public DeviceType(ResourceLocation id, boolean attachable, DeviceFactory factory) {
        this.id = id;
        this.attachable = attachable;
        this.factory = factory;
    }

    public ResourceLocation id() {
        return id;
    }

    public DeviceFactory factory() {
        return factory;
    }

    public boolean attachable() {
        return attachable;
    }

    @Override
    public String toString() {
        return "DeviceType{" +
                "id=" + id +
                ", attachable=" + attachable +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        DeviceType that = (DeviceType) object;
        return attachable == that.attachable && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + Boolean.hashCode(attachable);
        return result;
    }
}
