package com.ourbit.model;

import com.ourbit.util.MerkelTreeUtils;
import com.ourbit.util.MerkleCommonUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Consumer;

/**
 * MerkleProof
 * @author OurbitOfficial
 * @date 2025/02/25 23:40
 */
public class MerkleProof {
    public static final TreeSet<String> STATIC_ALL_ENABLED_CURRRENCIES = new TreeSet<>();
    private List<TreeNode> path;
    private TreeNode self;

    private List<String> allCurrencies;

    public List<TreeNode> getPath() {
        return path;
    }

    public TreeNode getSelf() {
        return self;
    }

    public void setPath(List<TreeNode> path) {
        this.path = path;
    }

    public void setSelf(TreeNode self) {
        this.self = self;
    }

    public List<String> getAllCurrencies() {
        return allCurrencies;
    }

    public void setAllCurrencies(List<String> allCurrencies) {
        this.allCurrencies = allCurrencies;
    }

    /**
     * validate
     * A new root is being introduced through the path and self provided by the user. Compare with the root in the path
     * @return {@link boolean }
     * @author OurbitOfficial
     * @date 2025/02/27 11:54
     */
    public boolean validate() {
        TreeNode newRoot = new MerkleTree().buildMerkelTreeRoot(path, self);
        TreeNode oldRoot = path.get(path.size() - 1);

        Map<String, BigDecimal> newRootBalanceAdded = MerkleCommonUtils.getBalanceMapForCalcHash(newRoot.getBalances(), false) ;
        Map<String, BigDecimal> oldRootBalanceAdded = MerkleCommonUtils.getBalanceMapForCalcHash(oldRoot.getBalances(), false) ;

        STATIC_ALL_ENABLED_CURRRENCIES.forEach(new Consumer<String>() {
            @Override
            public void accept(String curreny) {
                System.out.println(curreny+": Generate balance "+newRootBalanceAdded.get(curreny)+", balance from file "+oldRootBalanceAdded.get(curreny));
            }
        });

        System.out.println("Generated hash "+newRoot.getHash()+", hash from file "+oldRoot.getHash());

        if (newRoot.getHash().equals(oldRoot.getHash()) && newRoot.validateEqualsBalances(oldRoot) && newRoot.getLevel().equals(oldRoot.getLevel())) {
            return true;
        }
        return false;
    }

}
