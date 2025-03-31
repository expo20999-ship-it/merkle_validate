package com.ourbit.util;

import com.ourbit.model.TreeNode;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.NavigableMap;
import java.util.Objects;

/**
 * MerkelTreeUtils
 * @author OurbitOBOfficial
 * @date 2025/02/25 23:11
 */

@Slf4j
public class MerkelTreeUtils {

    /**
     * 生成节点hash
     * @param treeNode
     */
    public static String createMerkelNodeLeaf(TreeNode treeNode) {
        String content;

        StringBuilder balanceSb = new StringBuilder();

        boolean isLeaf = treeNode.getLevel() == 0;
        //以币种维度聚合资产，hash计算只以币种维度的资产计算
        NavigableMap<String, BigDecimal> balanceMapForCalcHash = MerkleCommonUtils.getBalanceMapForCalcHash(treeNode.getBalances(),isLeaf);
        balanceMapForCalcHash.forEach((token, balance) -> {
            balanceSb.append(token).append(":")
                    .append(BigDecimalUtils.getBigDecimalPlainStr(balance)).append(",");
        });
        if (balanceSb.length() > 1) {
            balanceSb.setLength(balanceSb.length() - 1);
        }

        String rightChildHash = Objects.isNull(treeNode.getRightChildHashId())?"":treeNode.getRightChildHashId();

        if (treeNode.getUid() != null) {
            content = MerkleCommonUtils.genProofId(treeNode.getSnapshotDate()) + "," + treeNode.getUid() + "," + balanceSb;
        } else {
            content = treeNode.getLeftChildHashId() + "," + rightChildHash + "," + balanceSb;
        }


        String hashCode = genSha256(content);
        return hashCode != null ? hashCode.substring(0, 32) : "";
    }

    static String genSha256(String content) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(content.getBytes(StandardCharsets.UTF_8));
            return byte2hex(messageDigest.digest());
        } catch (Exception e) {
//            log.error("failure to calculate hash id!", e);
            return null;
        }
    }


    public static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String temp;
        for (int n = 0; b != null && n < b.length; n++) {
            temp = Integer.toHexString(b[n] & 0XFF);
            if (temp.length() == 1) {
                hs.append('0');
            }
            hs.append(temp);
        }
        return hs.toString();
    }
}
