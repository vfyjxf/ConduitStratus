package dev.vfyjxf.conduitstratus.api.conduit.connection;

import dev.vfyjxf.conduitstratus.conduit.network.BaseNetworkHolder;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

//参与路径计算和连接的节点
public interface ConduitNode extends BaseNetworkHolder {
    // 导管的位置和坐标
    ConduitNodeId conduitId();

    // 直接连接的管道，必须保证是双向的
    List<ConduitNodeId> adjacentNodes();

    Collection<Direction> connectedDirections();

    // 是否接受远程连接，需要保证双向连接
    boolean acceptsRemote(ConduitNodeId remote);

    // 是否接受邻居连接，需要保证双向连接
    boolean acceptsNeighbor(Direction direction);

    // 当前节点从世界中删除
    void removeFromLevel();

    // 远程连接发生变化
    void onRemoteChanged();

    // 刷新远程连接
    boolean refreshRemote();

    // 刷新本地连接
    boolean refreshNeighbor();

    /**
     * @return 节点是否联网, 即节点是否属于一个有效的的网络
     */
    boolean online();

    default boolean offline() {
        return !online();
    }

    // 节点是否有效，即当前的管道连接目标是否仍然存在
    boolean isInvalid();

    @ApiStatus.Internal
    boolean validate();

    // 在指定延迟后刷新当前节点的网络
    void scheduleNetwork(int delay);

    // called when the network is tool large
    void setInvalid();
}
