package dev.jinyisama.datastructure;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class OctreeNode {
    private BlockPos position;
    private List<OctreeNode> children = new ArrayList<>();
    private List<BlockPos> positions = new ArrayList<>();

    public OctreeNode(BlockPos position) {
        this.position = position;
    }

    /**
     * 向八叉树中插入一个位置。
     *
     * @param pos 要插入的位置
     */
    public void insert(BlockPos pos) {
        if (children.isEmpty()) {
            positions.add(pos);
        } else {
            for (OctreeNode child : children) {
                if (child.isInside(pos)) {
                    child.insert(pos);
                    break;
                }
            }
        }
    }

    /**
     * 检查位置是否在当前节点范围内。
     *
     * @param pos 要检查的位置
     * @return 如果位置在范围内返回true，否则返回false
     */
    private boolean isInside(BlockPos pos) {
        return position.getX() <= pos.getX() && position.getZ() <= pos.getZ(); // 简化版
    }

    /**
     * 分裂当前节点为8个子节点。
     */
    public void split() {
        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();
        int halfX = x / 2;
        int halfY = y / 2;
        int halfZ = z / 2;

        // 创建子节点并添加到children列表中
        for (int i = 0; i < 8; i++) {
            BlockPos childPosition = new BlockPos(
                    halfX + ((i & 1) == 1 ? x - halfX : 0),
                    halfY + ((i & 2) == 2 ? y - halfY : 0),
                    halfZ + ((i & 4) == 4 ? z - halfZ : 0)
            );
            children.add(new OctreeNode(childPosition));
        }
    }

    /**
     * 获取包含在当前节点范围内的位置列表。
     *
     * @return 包含的位置列表
     */
    public List<BlockPos> getPositions() {
        return positions;
    }
}