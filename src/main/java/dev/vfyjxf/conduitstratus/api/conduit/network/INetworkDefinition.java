package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.conduit.network.NetworkDefinition;
import net.minecraft.resources.ResourceLocation;

public interface INetworkDefinition {

    static INetworkDefinition create(ResourceLocation uid) {
        return new NetworkDefinition(uid);
    }

    ResourceLocation getUniqueId();

}
