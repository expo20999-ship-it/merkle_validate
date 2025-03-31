package com.ourbit.model;

import com.google.common.collect.Maps;
import com.ourbit.constants.TreeNodeRoleConstants;
import com.ourbit.util.MerkelTreeUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.ourbit.constants.TreeNodeRoleConstants.ROOT_NODE;

/**
 * 默克尔树
 * @author OurbitOBOfficial
 * @date 2025/02/26 00:04
 */
public class MerkleTree {


    /**
     * build merkelTree root node
     * @param path
     * @param self
     * @return {@link TreeNode }
     * @author OurbitOBOfficial
     * @date 2025/02/27 11:56
     */
    public TreeNode buildMerkelTreeRoot(List<TreeNode> path, TreeNode self) {
        if (path.size() <= 1) {
            throw new IllegalArgumentException("Must be at least two leafs to construct a Merkle tree");
        }

        TreeNode parent = createParentTreeNode(path.get(0), self);

        for (int i = 1; i < path.size() - 1; i++) {
            self = parent;
            parent = createParentTreeNode(path.get(i), self);
        }

        parent.setRole(ROOT_NODE);

        return parent;
    }

    /**
     * createParentTreeNode
     * @param friend
     * @param self
     * @return {@link TreeNode }
     * @author OurbitOBOfficial
     * @date 2025/02/27 11:57
     */
    TreeNode createParentTreeNode(TreeNode friend, TreeNode self) {
        TreeNode parent;
        if (TreeNodeRoleConstants.LEFT_NODE.equals(friend.getRole())) {
            // friend 是左节点
            parent = constructInternalNode(friend, self);
        }else {
            // friend 是右节点或者空节点
            parent = constructInternalNode(self, friend);
        }

        return parent;
    }

    /**
     * constructInternalNode
     * @param left
     * @param right
     * @return
     */
    private TreeNode constructInternalNode(TreeNode left, TreeNode right) {
        TreeNode parent = new TreeNode();

        if (right == null) {
            right = createEmptyTreeNode(left);
        }
        parent.mergeAsset(left);
        parent.mergeAsset(right);

        parent.setLeftChildHashId(left.getHash());
        parent.setRightChildHashId(right.getHash());

        // 左节点hash+右节点hash+parent资产+左节点层级
        parent.setLevel(left.getLevel() + 1);
        parent.setSnapshotDate(left.getSnapshotDate());

        parent.setHash(MerkelTreeUtils.createMerkelNodeLeaf(parent));

        return parent;
    }

    /**
     * clearAssetsMap
     * @param right
     * @return {@link java.util.Map<java.lang.String,java.math.BigDecimal> }
     * @author OurbitOBOfficial
     * @date 2025/02/27 11:59
     */
    private static TreeMap<String, BigDecimal> clearAssetsMap(TreeNode right) {
        Map<String, BigDecimal> assetsMap = right.getBalances();
        TreeMap<String, BigDecimal> result = Maps.newTreeMap();
        assetsMap.keySet().forEach(coinName -> result.put(coinName, BigDecimal.ZERO));
        result.putAll(result);
        return result;
    }

    /**
     * createEmptyTreeNode
     * @param source
     * @return {@link TreeNode }
     * @author OurbitOBOfficial
     * @date 2025/02/25 23:40
     */
    private TreeNode createEmptyTreeNode(TreeNode source){
        TreeNode target = new TreeNode();

        target.setSnapshotDate(source.getSnapshotDate());
        target.setHash(source.getHash());
        target.setLevel(source.getLevel());
        target.setRole(source.getRole());
        target.setUid(source.getUid());
        target.setRole(TreeNodeRoleConstants.EMPTY_NODE);
        target.setBalances(clearAssetsMap(target));

        return target;
    }

}
