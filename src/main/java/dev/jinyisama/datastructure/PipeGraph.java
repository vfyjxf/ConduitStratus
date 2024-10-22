package dev.jinyisama.datastructure;

import dev.vfyjxf.conduitstratus.api.conduit.network.ChannelColor;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectDoublePair;
import net.minecraft.core.BlockPos;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PipeGraph {

    public static final int MAX_NODES = 65536;

    // 存储节点及其邻接节点的列表
    private final MutableMap<BlockPos, LinkedList<BlockPos>> adjacencyList;

    // 并查集用于检测连通性
    private UnionFind uf;

    // 位置到索引的映射，用于并查集操作
    private final Object2IntOpenHashMap<BlockPos> positionToIndex;

    // 节点的颜色映射
    private final MutableMap<BlockPos, ChannelColor> nodeColors;

    // 存储每种颜色节点之间的距离
    private final MutableMap<ChannelColor, List<ObjectDoublePair<BlockPos>>> colorDistances;

    // 用于索引的新节点ID
    private int index = 0;

    // 八叉树根节点，用于快速查找相邻节点
    private OctreeNode octreeRoot;

    public PipeGraph() {
        this.adjacencyList = Maps.mutable.empty();
        this.uf = new UnionFind(MAX_NODES); // 假设最多有65536个节点
        this.positionToIndex = new Object2IntOpenHashMap<>();
        this.nodeColors = Maps.mutable.empty();
        this.colorDistances = Maps.mutable.empty();
        this.octreeRoot = new OctreeNode(new BlockPos(0, 0, 0)); // 假设以原点为中心
    }

    public int graphLimitation() {
        return MAX_NODES;
    }

    /**
     * 添加一个新的节点到图中，并设置其颜色。
     *
     * @param pos   节点的位置
     * @param color 节点的颜色
     */
    public void addVertex(BlockPos pos, ChannelColor color) {
        if (!adjacencyList.containsKey(pos)) {
            adjacencyList.put(pos, new LinkedList<>());
            if (!positionToIndex.containsKey(pos)) {
                positionToIndex.put(pos, index++);
            }
            nodeColors.put(pos, color);
            updateColorDistances(pos, color, false);
            octreeRoot.insert(pos); // 插入到八叉树中
        }
    }

    /**
     * 添加一条边到图中，并更新颜色距离列表。
     *
     * @param v1 边的一个端点
     * @param v2 边的另一个端点
     */
    public void addEdge(BlockPos v1, BlockPos v2) {
        // 检查节点是否已经存在
        if (!adjacencyList.containsKey(v1) || !adjacencyList.containsKey(v2)) {
            throw new IllegalArgumentException("Both vertices must be in the graph before adding an edge.");
        }

        // 确保颜色一致
        ChannelColor color1 = nodeColors.get(v1);
        ChannelColor color2 = nodeColors.get(v2);
        if (color1 != null && color2 != null && color1 != color2) {
            throw new IllegalArgumentException("Cannot add an edge between nodes with different colors.");
        }

        // 添加边
        adjacencyList.get(v1).add(v2);
        adjacencyList.get(v2).add(v1);
        uf.union(positionToIndex.getInt(v1), positionToIndex.getInt(v2));

        // 更新颜色距离列表
        updateColorDistances(v1, color1, false);
        updateColorDistances(v2, color2, false);
    }

    /**
     * 移除图中的一条边。
     *
     * @param v1 边的一个端点
     * @param v2 边的另一个端点
     */
    public void removeEdge(BlockPos v1, BlockPos v2) {
        adjacencyList.get(v1).remove(v2);
        adjacencyList.get(v2).remove(v1);

        // 更新颜色距离列表
        updateColorDistances(v1, nodeColors.get(v1), true);
        updateColorDistances(v2, nodeColors.get(v2), true);
    }

    /**
     * 移除图中的一个节点及其相关的边。
     *
     * @param pos 要移除的节点的位置
     */
    public void removeVertex(BlockPos pos) {
        if (adjacencyList.containsKey(pos)) {
            for (BlockPos neighbor : adjacencyList.get(pos)) {
                removeEdge(pos, neighbor);
            }
            adjacencyList.remove(pos);
            nodeColors.remove(pos);
            updateColorDistances(pos, nodeColors.get(pos), true);
        }
    }

    /**
     * 获取指定颜色的节点之间的距离列表。
     *
     * @param color 颜色
     * @return 颜色相同的节点对及其距离
     */
    public List<ObjectDoublePair<BlockPos>> getColorDistances(ChannelColor color) {
        return colorDistances.getOrDefault(color, Collections.emptyList());
    }

    /**
     * 获取指定同色节点对之间的路径长度。
     *
     * @param start 起始节点位置
     * @param end   结束节点位置
     * @param color 颜色
     * @return 路径长度，如果不存在路径则返回-1
     */
    public int getPathLengthBetweenSameColorNodes(BlockPos start, BlockPos end, ChannelColor color) {
        if (nodeColors.get(start) != (color) || nodeColors.get(end) != (color)) {
            return -1; // 颜色不匹配或节点不存在
        }

        Queue<BlockPos> queue = new LinkedList<>();
        Object2IntOpenHashMap<BlockPos> distanceMap = new Object2IntOpenHashMap<>();
        MutableSet<BlockPos> visited = Sets.mutable.empty();

        queue.offer(start);
        distanceMap.put(start, 0);
        visited.add(start);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            if (current.equals(end)) {
                return distanceMap.getInt(current);
            }
            LinkedList<BlockPos> list = adjacencyList.get(current);
            if (list == null) continue;
            for (BlockPos neighbor : list) {
                if (!visited.contains(neighbor) && nodeColors.get(neighbor) == color) {
                    queue.offer(neighbor);
                    distanceMap.put(neighbor, distanceMap.getInt(current) + 1);
                    visited.add(neighbor);
                }
            }
        }

        return -1; // 无路径
    }

    /**
     * 添加一个带有多个连接的新节点，并检查其邻居是否已存在于网络中。
     *
     * @param newNode   新节点的位置
     * @param neighbors 邻居节点的位置集合
     * @param color     新节点的颜色
     * @return 如果插入成功返回true，否则返回false
     */
    public boolean addNodeWithMultipleConnections(BlockPos newNode, Collection<BlockPos> neighbors, ChannelColor color) {
        // 检查是否有至少一个邻居节点存在于网络中
        boolean hasAdjacentNeighbor = false;
        for (BlockPos neighbor : neighbors) {
            if (adjacencyList.containsKey(neighbor)) {
                hasAdjacentNeighbor = true;
                break;
            }
        }

        if (!hasAdjacentNeighbor) {
            // 没有相邻的邻居节点，插入失败
            return false;
        }

        // 添加新节点
        addVertex(newNode, color);

        // 尝试连接新节点到所有邻居
        for (BlockPos neighbor : neighbors) {
            try {
                addEdge(newNode, neighbor);
            } catch (IllegalArgumentException e) {
                // 如果无法连接，则删除新节点
                removeVertex(newNode);
                return false;
            }
        }

        // 合并连通分量
        int initialRoot = uf.find(positionToIndex.getInt(neighbors.iterator().next()));
        for (BlockPos neighbor : neighbors) {
            int neighborRoot = uf.find(positionToIndex.getInt(neighbor));
            if (neighborRoot != initialRoot) {
                uf.union(initialRoot, neighborRoot);
            }
        }

        return true;
    }

    /**
     * 更新颜色距离列表。
     *
     * @param pos    节点的位置
     * @param color  节点的颜色
     * @param remove 是否移除节点
     */
    private void updateColorDistances(BlockPos pos, ChannelColor color, boolean remove) {
        if (color != null) {
            List<ObjectDoublePair<BlockPos>> distances = colorDistances.computeIfAbsent(color, k -> new ArrayList<>());

            if (remove) {
                distances.removeIf(pair -> pair.key().equals(pos));
            } else {
                for (BlockPos otherPos : adjacencyList.keySet()) {
                    if (otherPos.equals(pos)) continue;
                    double distance = pos.distSqr(otherPos);
                    if (nodeColors.get(otherPos) == color) {
                        distances.add(ObjectDoublePair.of(otherPos, distance));
                    }
                }
            }
        }
    }

    /**
     * 清空图的所有数据。
     */
    public void clear() {
        adjacencyList.clear();
        uf = new UnionFind(MAX_NODES);
        positionToIndex.clear();
        nodeColors.clear();
        colorDistances.clear();
        index = 0;
        octreeRoot = new OctreeNode(new BlockPos(0, 0, 0));
    }
}