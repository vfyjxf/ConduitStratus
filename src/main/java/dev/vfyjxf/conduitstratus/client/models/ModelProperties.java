package dev.vfyjxf.conduitstratus.client.models;

import dev.vfyjxf.conduitstratus.conduit.ConduitConnections;
import net.neoforged.neoforge.client.model.data.ModelProperty;

public final class ModelProperties {

    public static final ModelProperty<ConduitConnections> CONDUIT_CONNECTION = new ModelProperty<>();

    private ModelProperties() {
    }
}
