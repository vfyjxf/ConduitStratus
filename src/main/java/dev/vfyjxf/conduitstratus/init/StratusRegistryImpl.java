package dev.vfyjxf.conduitstratus.init;

import dev.vfyjxf.conduitstratus.api.StratusRegistry;
import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.io.LogisticManager;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.MutableMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class StratusRegistryImpl implements StratusRegistry {

    public static StratusRegistryImpl INSTANCE = new StratusRegistryImpl();

    private final MutableMap<HandleType, LogisticManager<?, ?, ?>> logisticManagers = Maps.mutable.empty();

    @Nullable
    public LogisticManager<?, ?, ?> getLogisticManager(HandleType handleType) {
        return logisticManagers.get(handleType);
    }

    @Override
    public void registerLogisticManager(HandleType handleType, LogisticManager<?, ?, ?> logisticManager) {
        if (logisticManagers.containsKey(handleType)) {
            throw new IllegalStateException("LogisticManager for HandleType " + handleType + " is already registered");
        }
        if (logisticManager == null) {
            throw new IllegalArgumentException("LogisticManager cannot be null");
        }
        logisticManagers.put(handleType, logisticManager);
    }
}
