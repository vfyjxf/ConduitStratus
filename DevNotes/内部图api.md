# 内部 graph 需要访问的数据
    

## 网络需要的API
- 增删节点的方法(?
- 检验节点有效性的方法
- onNodeAdd event
- onNodeRemove event
- 计算节点空间距离的快捷方法
- 合并两个网络的方法(? 类似于两套本身不连在一起的管道网络突然被连在一起的需求)
- getNodes()
- getNodesWithContext(T context, Color color)//获取某种操作类型的某一频道下的节点

## 节点需要的api

- onNodeAddToNetWork event
- onNodeRemovedFromNetwork event
- rejectNodes(Predicate<Node> p) [或许并不需要这个，可以在上层api设计]
- getPos()
- 获取已经有链接的方向(net.minecraft.core.Direction)
- 获取某个方向的链接
- 获取所在网络
- 尝试在本网络中获取自身某个方向上的node
- connected(Node)
- connectable(Node)
- disconnect(Node/Direction)
- onConnectTo(Node) event
