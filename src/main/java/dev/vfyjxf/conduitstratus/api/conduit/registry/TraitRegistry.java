
package dev.vfyjxf.conduitstratus.api.conduit.registry;

import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.io.LogisticManager;

public interface TraitRegistry {

    static void registerLogisticManager(HandleType handleType, LogisticManager<?, ?,?> logisticManager) {

    }

}