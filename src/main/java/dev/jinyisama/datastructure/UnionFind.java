package dev.jinyisama.datastructure;

public class UnionFind {
    private int[] parent;
    private int count;

    public UnionFind(int capacity) {
        parent = new int[capacity];
        count = capacity;
        for (int i = 0; i < capacity; i++) {
            parent[i] = i;
        }
    }

    /**
     * 查询节点所在集合的代表元素。
     *
     * @param p 节点索引
     * @return 代表元素的索引
     */
    public int find(int p) {
        if (p < 0 || p >= parent.length) {
            throw new IllegalArgumentException("p is out of bound.");
        }
        while (p != parent[p]) {
            parent[p] = parent[parent[p]]; // 路径压缩
            p = parent[p];
        }
        return p;
    }

    /**
     * 连接两个节点所在的集合。
     *
     * @param p 第一个节点索引
     * @param q 第二个节点索引
     */
    public void union(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);
        if (rootP == rootQ) {
            return;
        }
        parent[rootQ] = rootP;
        count--;
    }

    /**
     * 判断两个节点是否属于同一个集合。
     *
     * @param p 第一个节点索引
     * @param q 第二个节点索引
     * @return 如果属于同一个集合返回true，否则返回false
     */
    public boolean connected(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);
        return rootP == rootQ;
    }

    /**
     * 获取集合的数量。
     *
     * @return 集合数量
     */
    public int count() {
        return count;
    }
}