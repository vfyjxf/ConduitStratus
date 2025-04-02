package dev.vfyjxf.conduitstratus.init;

import dev.vfyjxf.conduitstratus.api.StratusRegistry;
import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.io.LogisticManager;
import org.eclipse.collections.api.map.ConcurrentMutableMap;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class StratusRegistryImpl implements StratusRegistry {

    public static StratusRegistryImpl INSTANCE = new StratusRegistryImpl();

    private final ConcurrentMutableMap<HandleType, LogisticManager<?, ?, ?>> logisticManagers = ConcurrentHashMap.newMap();

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
