package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.network.INetworkDefinition;
import net.minecraft.resources.ResourceLocation;

public class NetworkDefinition implements INetworkDefinition {

    private final ResourceLocation uid;

    public NetworkDefinition(ResourceLocation uid) {
        this.uid = uid;
    }

    @Override
    public ResourceLocation getUniqueId() {
        return uid;
    }
}
