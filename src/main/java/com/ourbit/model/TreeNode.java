package com.ourbit.model;

import com.google.common.collect.Maps;
import com.ourbit.util.CollectionUtils;
import com.ourbit.util.MerkelTreeUtils;
import com.ourbit.util.StringUtils;
import org.apache.commons.collections.MapUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * TreeNode
 * @author OurbitOfficial
 * @date 2025/02/25 23:39
 */
public class TreeNode {

    private String snapshotDate;

    private TreeMap<String, BigDecimal> balances = Maps.newTreeMap();

    private String hash;

    private Integer level;

    /**
     * role 0-empty， 1-left， 2-right， 3-root
     */
    private Integer role;

    private String uid;

    /**
     * 左子节点 hashId
     */
    private String leftChildHashId;
    /**
     * 右子节点 hashId
     */
    private String rightChildHashId;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * 验证self节点
     * @return {@link boolean }
     * @author OurbitOfficial
     * @date 2025/02/25 23:44
     */
    public boolean validateSelf() {
        String merkelNodeLeaf = MerkelTreeUtils.createMerkelNodeLeaf(this);
        return hash.equals(merkelNodeLeaf);
    }

    /**
     * 验证Path节点数据
     * @return {@link boolean }
     * @author OurbitOfficial
     * @date 2025/02/25 23:45
     */
    public boolean validatePath() {
        if (!validateBalances() || StringUtils.isBlank(hash) || role < 0 || role > 3 || level < 0) {
            return false;
        }
        return true;
    }

    /**
     * 验证资产集合
     * @return {@link boolean }
     * @author OurbitOfficial
     * @date 2025/02/25 22:30        
     */
    public boolean validateBalances() {
        if(MapUtils.isEmpty(balances)){
            return false;
        }

        for (BigDecimal decimal : balances.values()) {
            if(decimal == null){
                return false;
            }
        }
        return true;
    }

    /**
     * mergeAsset
     * @param childNode
     */
    public void mergeAsset(TreeNode childNode){
        if (childNode == null) {
            return;
        }
        if(MapUtils.isNotEmpty(childNode.balances)){
            childNode.balances.forEach((coinName,amount)->{
                BigDecimal oldValue = balances.get(coinName);
                if (oldValue != null) {
                    BigDecimal newValue = oldValue.add(amount);
                    balances.put(coinName,newValue);
                }else{
                    balances.put(coinName, amount);
                }
            });
        }

    }

    /**
     * validateEqualsBalances
     * @return {@link boolean }
     * @author OurbitOfficial
     * @date 2025/02/25 22:30
     */
    public boolean validateEqualsBalances(TreeNode oldRoot) {
        Map<String, BigDecimal> oldBalances = oldRoot.getBalances();
        for (Map.Entry<String, BigDecimal> entry : balances.entrySet()) {
            if(oldBalances.get(entry.getKey()).compareTo(entry.getValue()) != 0){
                return false;
            }
        }
        return true;
    }

    public String getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(String snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    public String getLeftChildHashId() {
        return leftChildHashId;
    }

    public void setLeftChildHashId(String leftChildHashId) {
        this.leftChildHashId = leftChildHashId;
    }

    public String getRightChildHashId() {
        return rightChildHashId;
    }

    public void setRightChildHashId(String rightChildHashId) {
        this.rightChildHashId = rightChildHashId;
    }

    public TreeMap<String, BigDecimal> getBalances() {
        return balances;
    }

    public void setBalances(TreeMap<String, BigDecimal> balances) {
        this.balances = balances;
    }
}
