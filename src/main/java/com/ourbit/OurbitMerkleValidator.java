package com.ourbit;

import com.alibaba.fastjson.JSONObject;
import com.ourbit.model.MerkleProof;
import com.ourbit.util.CollectionUtils;
import com.ourbit.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Verify whether the account assets are included in the Merkle tree published by Ourbit
 *
 * @author OurbitOfficial
 */
public class OurbitMerkleValidator {
    /**
     * merkel_tree_ourbit.json
     **/
    private static final String MERKLE_TREE_BG_FILE_PATH = "merkel_tree_ourbit.json";

    public static void main(String[] args) {
        System.out.println("Merkel tree path validation start");
        String merkleJsonFile = getMerkleJsonFile();
        if (StringUtils.isBlank(merkleJsonFile)) {
            System.out.println("Merkel tree path validation failed, invalid merkle proof file");
            return;
        }

        // 获得默克尔树证明对象
        MerkleProof merkleProof = JSONObject.parseObject(merkleJsonFile, MerkleProof.class);

        MerkleProof.STATIC_ALL_ENABLED_CURRRENCIES.clear();
        MerkleProof.STATIC_ALL_ENABLED_CURRRENCIES.addAll(merkleProof.getAllCurrencies());

        // 默克尔树参数验证
        if(validate(merkleProof)){
            System.out.println("Calculated hash is consistent with the Merkle tree root hash provided in file. The verification succeeds");
        }else {
            System.out.println("Calculated hash is inconsistent with the merkle tree root hash provided in file. The verification fails");
        }
    }

    /**
     * validate
     * @param merkleProof
     * @return
     * @author OurbitOfficial
     * @date 2025/03/25 20:29
     */
    private static boolean validate(MerkleProof merkleProof){

        // self节点不能为空 并且 path节点也不能为空
        if(merkleProof.getSelf() == null || CollectionUtils.isEmpty(merkleProof.getPath())){
            return false;
        }

        // 验证self数据一致性
        if(!merkleProof.getSelf().validateSelf()){
            return false;
        }

        // 验证path参数验证
        if(!merkleProof.getPath().get(0).validatePath()){
            System.out.println("Self hash verification fails");
            return false;
        }

        if(merkleProof.getPath().get(0).getRole().intValue() == merkleProof.getSelf().getRole().intValue()){
            return false;
        }

        return merkleProof.validate();
    }


    /**
     * get demo_merkel_tree_ourbit.json content
     *
     * @author OurbitOfficial
     * @date 2025/02/25 16:53
     */
    private static String getMerkleJsonFile() {
        StringBuilder builder = new StringBuilder();
        try {
            Path path = Path.of(MERKLE_TREE_BG_FILE_PATH);
            if(!Files.exists(path)){
                System.out.println( MERKLE_TREE_BG_FILE_PATH+" file not exists");
            }

            Files.readAllLines(path, StandardCharsets.UTF_8).forEach(builder::append);
            return builder.toString();
        } catch (IOException e) {
            throw new RuntimeException(MERKLE_TREE_BG_FILE_PATH + " file does not exist");
        }
    }
}


