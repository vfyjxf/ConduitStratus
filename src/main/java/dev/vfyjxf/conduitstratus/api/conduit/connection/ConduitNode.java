package dev.vfyjxf.conduitstratus.api.conduit.connection;

import dev.vfyjxf.conduitstratus.conduit.network.NetworkHolder;

import java.util.List;

public interface ConduitNode extends NetworkHolder {
    // 导管的位置和坐标
    ConduitNodeId conduitId();
    // 直接连接的管道，必须保证是双向的
    List<ConduitNodeId> neighbors();

    // 是否有效，连接的时候识别到无效节点会造成连接失败，并对连接的节点调用 setValid(false)
    boolean valid();

    // 是否正在初始化，会在初始化的时候推迟连接
    boolean initializing();

    // 设置节点是否有效，无效节点会被标记并通知更新
    void setValid(boolean valid);

    // 刷新节点，在连接到的节点更新的时候会调用，触发节点刷新自身的连接
    void refresh();
}
