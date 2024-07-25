package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitColor;
import dev.vfyjxf.conduitstratus.api.conduit.IConduit;
import net.minecraft.core.Direction;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.set.ImmutableSet;

import javax.annotation.Nullable;

public interface INetworkNode {

    IConduit getConduit();

    void setConduit(IConduit conduit);

    INetwork getNetwork();

    void setNetwork(INetwork network);

    INetworkConnection createConnection(INetworkNode left, INetworkNode right);

    /**
     * @return the directions of existing connections.
     */
    ImmutableSet<Direction> getDirections();

    ImmutableMap<Direction, INetworkConnection> getConnectionsMap();

    @Nullable
    INetworkConnection getConnection(Direction direction);

    ImmutableList<INetworkConnection> getConnections();

    void rejectDirection(Direction direction);

    void removeRejection(Direction direction);

    boolean connectable(Direction direction);

    boolean connected(Direction direction);

    void disconnect(Direction direction);

    default ConduitColor getColor() {
        return getConduit().getColor();
    }

    void tick();

}
