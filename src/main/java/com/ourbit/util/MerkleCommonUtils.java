package com.ourbit.util;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import static com.ourbit.constants.MerkelTreeConstants.DATE_FORMAT_YMD_MSG;
import static com.ourbit.model.MerkleProof.STATIC_ALL_ENABLED_CURRRENCIES;

public class MerkleCommonUtils {

    /**
     * 根据快照日期生成 保证金证明id
     *
     * @param snapshotDate 快照日期
     */
    public static String genProofId(Date snapshotDate) {
        return "PR-" + DateFormatUtils.format(snapshotDate, DATE_FORMAT_YMD_MSG);
    }

    public static String genProofId(String yymmddFormatDate) {
        return "PR-" + yymmddFormatDate;
    }

    /**
     * 获取用于计算hashId的balanceMap，
     *  1. 以币种维度聚合 不区分子系统类型
     *  2. 针对叶子节点，如果余额为0，则不会纳入到balanceSb的输入中。 如果非叶子节点，如果余额为0，也会纳入到balanceSb的输入中
     */
    public static TreeMap<String, BigDecimal> getBalanceMapForCalcHash(TreeMap<String, BigDecimal> navigableMap,boolean isLeaf) {
        if (MapUtils.isEmpty(navigableMap)) {
            return new TreeMap<>();
        }
        TreeMap<String, BigDecimal> map = new TreeMap<>();
        STATIC_ALL_ENABLED_CURRRENCIES.forEach(currency -> map.put(currency, BigDecimal.ZERO));


        navigableMap.forEach((key, bigDecimal) -> {
            //以冒号做分隔符，第一个为币种
            String[] split = key.split(":");
            String currency = null;
            if (split.length == 2) {
                currency = split[0];
            }else if (split.length ==1 ){
                currency = split[0];
            }else{
                throw new RuntimeException("error format of balance key "+ key);
            }
            currency = split[0];
            BigDecimal oldAmount = map.get(currency);
            BigDecimal newAmountAfterAdding = BigDecimalUtils.safeSumBigDecimal(oldAmount, bigDecimal);

            map.put(currency, newAmountAfterAdding);
        });


        //当前仅当叶子节点, 如果为0，不会加入到map.
        if(isLeaf){
            map.entrySet().removeIf(entry -> entry.getValue().compareTo(BigDecimal.ZERO) == 0);
        }

        return map;
    }
}
