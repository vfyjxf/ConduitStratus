package dev.vfyjxf.conduitstratus.api;

import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.io.LogisticManager;

public interface StratusRegistry {

    void registerLogisticManager(HandleType handleType, LogisticManager<?, ?, ?> logisticManager);

}
