# Proof of Reserves Licensed to OurbitOBOfficial 
## Background

Ourbit launches Proof of Reserve (PoR) to improve the security and transparency of user assets. These tools will allow you to independently audit Ourbit’s Proof of Reserves as well as verify that Ourbit’s reserves have exceeded the exchange’s known liabilities to all users to confirm Ourbit’s solvency.

## Introduction
### Build from source
Download the latest version for your operating system and architecture. Also, you can build the source code yourself.

[Download] (https://www.oracle.com/java/technologies/downloads/)Install JDK(Java Development Kit)  
[Download] (https://maven.apache.org/download.cgi.)Install Maven build tool

The minimum prerequisite to build this project requires Java version >= 11, Maven version >= 3.8.4

### Package and compile source code
#### Enter the path for the project
`cd ~/Downloads/proof-of-reserves`

#### Install dependencies
`mvn clean install`

#### Start up
`java -jar proof-of-reserves.jar`

# Technical Description
## What is the Merkle Tree?
Merkle Tree is a data structure, also known as a Hash Tree. Merkle tree stores data in the leaf nodes of the tree structure, and by hashing the data step by step up to the top root node, any changes in the data of the leaf nodes will be passed to the higher level nodes and eventually displayed as changes in the root of the tree.

### 1. The roles of Merkle tree
- Zero-knowledge proof
- Ensure data immutability
- Ensures data privacy
### 2. Ourbit Limited Merkle Tree Definition
#### 2.1 Node Information
Information stored in every tree node includes:
1. hash value;
2. the number of coins in specific system contained in the user's asset snapshot (BTC, ETH, USDT for example);
```
   hash value,{"BTC:spot":"BTC amount in spot","BTC:swap":"BTC amount in swap","USDT:spot":"USDT amount in spot","USDT:swap":"USDT amount in swap"}
   076443bd106667e5f91d872e02c0c3c6,{
        "BTC:spot": 1.9011819612789163,
        "BTC:swap": 0.8926240301795941,
        "ETH:spot": 5.226372086098455,
        "ETH:swap": 11.948656559881854,
        "USDT:swap": 7977.290325012773
      }
```
#### 2.2 Hash Rules
##### Leaf nodes (except padding nodes)
`hash=sha256Function(ProofId,uid,balance_string).substring(0,32)`
- ProofId: "PR-" + snapshotDate, PR-2025-03-15 for example
- uid: UID of the user
- balances: json string composed of the number of coins in the user's asset snapshot, (note: here same coin in different systems will be added)
    - For example：
  ```json
  {"BTC":2.7938059915,"ETH":17.175028646,"USDT":7977.290325012773}
   ```  
  ##### Parent node
  ```
  Parent node's hash = sha256Function(leftHash+','+rightHash,balanceString).substring(0,32)
   ```
- leftHash: hash of the left child node of the current node,
- rightHash: hash of the right child node of the current node,
- balanceString: balance that adds left child balance and right child balance

**Definition of tree node level**：A complete Merkle Tree (full binary tree) requires 2^n leaf node data, leaf node level = n + 1, parent node level = child node level - 1, root node level = 1, leaf node level is the maximum

##### Padding node rules
A complete Merkle Tree (full binary tree) requires 2^n leaf node data, but the actual number of data may not satisfy and may be odd. In such a case, if a node k has no sibling node, then auto padding generates a sibling node k', and`hash(k')=hash(k)`, and the number of coins of node k' is set to zero.


###### For example：
| Hash   | balances  |
|--------| -------------|
| hash1  | {"BTC":1,"ETH": 6,"USDT":10}|
| hash2  | {"BTC":2,"ETH":4,"USDT":8}|
| hash3  | {"BTC":5,"ETH":9,"USDT":74}|

Then the padding node hash4 = hash3, stored balances are `{"BTC": 0, "ETH": 0,"USDT": 0}`

```
Parent node's hash = sha256Function(hash1+','+hash2,{"BTC":left BTC amount+right BTC amount,"ETH":left ETH amount+right ETH amount,"USDT":left USDT amount+right USDT amount}).substring(0,32)
```  
Thus：
`hash6 = SHA256(hash3 + hash3, {BTC: (2+0), ETH:(1+0), USDT:(12+0)}).substring(0,32)`

### Verification Principle
#### 1、Verification principle:
According to the definition of Ourbit Limited Merkle tree, the hash value of the parent node is calculated from the user's own leaf node up to the root node, and the hash value of the root node is compared with the hash value of the Merkle tree in "Verification Step - Step 1", if the two are equal, the verification passes, if not, the verification fails.

#### 2、Example：
Merkle tree path data json text:
```json
{
  "self": {
    "snapshotDate": "2025-03-18",
    "balances": {
      "ETH:spot": 5.226372086098455,
      "ETH:swap": 11.948656559881854,
      "BTC:swap": 0.8926240301795941,
      "BTC:spot": 1.9011819612789163,
      "USDT:swap": 7977.290325012773
    },
    "uid": "65750647",
    "level": 0,
    "role": 2,
    "hash": "076443bd106667e5f91d872e02c0c3c6"
  },
  "path": [
    {
      "snapshotDate": "2025-03-18",
      "balances": {
        "ETH:spot": 10.726875946794577,
        "ETH:swap": 5.756264710268435,
        "BTC:swap": 2.7640575728510774,
        "BTC:spot": 0.2334225290622807,
        "USDT:swap": 6347.948619109149
      },
      "uid": null,
      "level": 0,
      "role": 1,
      "hash": "01bdbf85a79864354f102bd6448908db"
    },
    {
      "snapshotDate": "2025-03-18",
      "balances": null,
      "uid": null,
      "level": 1,
      "role": 0,
      "hash": null
    },
    {
      "snapshotDate": "2025-03-18",
      "balances": null,
      "uid": null,
      "level": 2,
      "role": 0,
      "hash": null
    },
    {
      "snapshotDate": "2025-03-18",
      "balances": null,
      "uid": null,
      "level": 3,
      "role": 0,
      "hash": null
    },
    {
      "snapshotDate": "2025-03-18",
      "balances": {
        "ETH:spot": 164.360450187444173,
        "ETH:swap": 168.056420319368986,
        "BTC:swap": 27.84444705368519193,
        "BTC:spot": 27.10846579341694724,
        "USDT:spot": 0,
        "USDT:swap": 129056.0706001438396
      },
      "uid": null,
      "level": 4,
      "role": 1,
      "hash": "7a563faa83d034a786c4e08eed06c399"
    },
    {
      "snapshotDate": "2025-03-18",
      "balances": {
        "ETH:spot": 341.5901107223544625,
        "ETH:swap": 304.67450645259991,
        "BTC:swap": 54.52309954497256353,
        "BTC:spot": 51.8722544603005677,
        "USDT:spot": 0,
        "USDT:swap": 219360.7419038539005
      },
      "uid": null,
      "level": 5,
      "role": 1,
      "hash": "c1d884b061de03acb2b01e303d8aaa8d"
    },
    {
      "snapshotDate": "2025-03-18",
      "balances": {
        "ETH:spot": 521.9038089426916675,
        "ETH:swap": 490.435848042119185,
        "BTC:swap": 86.02422820168842696,
        "BTC:spot": 81.11532474405871194,
        "USDT:spot": 0,
        "USDT:swap": 362742.0514481196621
      },
      "uid": null,
      "level": 6,
      "role": 3,
      "hash": "df1d34512abd50712b9f5713b51fad8e"
    }
  ]
}
```

#### Verification Steps
1. Take the executable verifier that you need to download on the Ourbit platform for your operating system and architecture.
- proof-of-reserves-linux-amd64-v1.0.2.zip
- proof-of-reserves-linux-arm64-v1.0.2.zip
- proof-of-reserves-macos-v1.0.2.zip
- proof-of-reserves-windows-v1.0.2.zip
2. Unzip the file to a specified directory, for example:
   `~/Downloads/proof-of-reserves-*`
3. Download the file merkel_tree_ourbit.json and substitute the file with the same name under your directory`~/Downloads/proof-of-reserves-*`
4. Run start file `sh start.sh` or Click the `start.bat` file
5. View results  
   1）If your data are correct and the verification passed, then the result is "Consistent with the Merkle tree root hash. The verification succeeds".
   2）If your data are wrong and the verification fails, the result is "Inconsistent with the Merkle tree root hash. The verification fails".
6. You can also refer to the Ourbit Limited open source verification tool code and Merkle tree definition (refer to the "What is the Merkle Tree" section) and write your own program to verify the path data obtained in step 2, or check to make sure your assets are included in the Merkel tree generated by this audit.