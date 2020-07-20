package Process;

import MiscObj.StaticObjects;
import Parse.*;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class TxnProcessor2 {

    public HashMap<String, Queue<Balance>> balances;

    public void setInitBalances(HashMap<String, Queue<Balance>> initBalances){
        this.balances = initBalances;
    }

    public void processTxns(EtherscanNormalTxn etherscanNormalTxn, EtherscanInternalTxn etherscanInternalTxn,
                            EtherscanERC20Txn etherscanERC20Txn, LinkedList<Form8949Obj> form8949) {
        try {
            if (etherscanNormalTxn.status.equals("1") && etherscanNormalTxn.message.equals("OK")) {
                //Iterate through each normal transaction and determine what transactions require further processing
                for (NormalTxn normalTxn : etherscanNormalTxn.result) {
                    //TODO Implement processing of transactions
                    if (StaticObjects.DEXEthAddresses.containsKey(normalTxn.to)) { //if txn is with a known uniswap contract, will not cover unknown but existing Uniswap contracts
                        //first element is buy side, second element is sell side
                        Balance[] txnPair = new Balance[2];

                        String exchangeTicker = StaticObjects.DEXEthAddresses.get(normalTxn.to);
                        //Add token into balance
                        if (!balances.containsKey(exchangeTicker)) { //check if there is already an existing balance for specific ticker
                            //create new hashmap with the queue
                            balances.put((exchangeTicker), new LinkedList<>());
                        }
                        String txnHash = normalTxn.hash;
                        double txnVal = Double.parseDouble(normalTxn.value);
                        if (txnVal != 0d) {  //if txn moves ETH into contract, e.g. DAI-ETH pair (i.e. buy token, sell ETH)
                            //TODO: Refactor with generics to make code cleaner
                            //iterate through all erc20 txns and look for matching txn hash from normal txn
                            //get value and timestamp and save information transferred into account
                            // TODO: Use HashMap because runtime is O(k*n) at the moment and can make it O(n)
                            for (ERC20Txn erc20Txn : etherscanERC20Txn.result) {
                                if (erc20Txn.hash.equals(txnHash)) {
                                    txnPair[0] = new Balance(Double.parseDouble(erc20Txn.value), Long.parseLong(erc20Txn.timeStamp), exchangeTicker);
                                    break;
                                }
                            }
                            txnPair[1] = new Balance(txnVal, Long.parseLong(normalTxn.timeStamp), "ETH");
//                            //Sell ETH and determine cost basis
//                            Queue <Balance> ethBalances = balances.get("ETH");
//                            //Keep processing transaction with each balance until there is no more ETH to process from balance
//                            Balance sellBalance = null;
//                            while (txnVal > 0d) {
//                                Balance headEthBalance = ethBalances.peek();
//                                if (headEthBalance == null) {
//                                    //TODO: Figure out better message when there is not enough ETH in balance to process sell
//                                    // Maybe add something info on date of txn or previous txn or starting ETH balance or how much is missing
//                                    // Just more info to give use to figure out where error has occured
//                                    throw new Exception(MessageFormat.format("Error when processing ETH transactions. Not enough ETH to sell in transaction.\n TxnHash: {0} \n Value (ETH): {1}", txnHash, normalTxn.value));
//                                }
//                                //TODO: Refactor with generics to have cleaner code
//                                //if fresh transaction and not continuing processing remainder of transaction
//                                if (sellBalance == null){
//                                    sellBalance = new Balance(txnVal, Long.parseLong(normalTxn.timeStamp),"ETH");
//                                }
//                                //If balance is less than txnVal, add to form8949 and process remainder in next item in balance queue
//                                if (headEthBalance.numOfUnits < txnVal){
//                                    form8949.add(new Form8949Obj(headEthBalance,sellBalance));
//                                    //subtract from txnVal so that remainder will be processed in next loop
//                                    txnVal -= headEthBalance.numOfUnits;
//                                    //update balance so that remainder is processed in next loop
//                                    sellBalance.numOfUnits = txnVal;
//                                    ethBalances.remove();
//                                }
//                                else
//                                {
//                                    form8949.add(new Form8949Obj(headEthBalance,sellBalance));
//                                    if (headEthBalance.numOfUnits == txnVal){
//                                        ethBalances.remove();
//                                    }
//                                    else{
//                                        headEthBalance.numOfUnits -= txnVal;
//                                    }
//                                    //set txnVal to 0 so that it exits while loop and null sellBalance
//                                    txnVal = 0;
//                                    sellBalance = null;
//                                }
//                            }
                        }
                        else { //some other txn involving ERC-20 being sent to contract, e.g. ETH-DAI or MKR-DAI (i.e. buy ETH, sell token, or token to token trade)
                            //Check for token to token swap txn first, then check for internal txn token for ETH Txn (ETH-DAI or ETH-DAI)
                            for (ERC20Txn erc20Txn : etherscanERC20Txn.result){
                                if (txnPair[0] != null && txnPair[1] != null){
                                    break;
                                }
                                if (erc20Txn.hash.equals(txnHash)){
                                    if (erc20Txn.to.equals(normalTxn.to)){  //if sending token to known contract address, i.e. selling this token for something else
                                        txnPair[1] = new Balance(Double.parseDouble(erc20Txn.value), Long.parseLong(erc20Txn.timeStamp), exchangeTicker);
                                    }
                                    else if(StaticObjects.DEXEthAddresses.containsKey(erc20Txn.from)){ //if receiving token from known contract address, i.e. buying this token
                                        String buyTicker = StaticObjects.DEXEthAddresses.get(erc20Txn.from);
                                        if (!balances.containsKey(exchangeTicker)) { //check if there is already an existing balance for specific ticker
                                            //create new hashmap with the queue
                                            balances.put((exchangeTicker), new LinkedList<>());
                                        }
                                        txnPair[0] = new Balance(Double.parseDouble(erc20Txn.value), Long.parseLong(erc20Txn.timeStamp), buyTicker);
                                    }
                                }
                            }
                            if (txnPair[0] == null) { //if didnt find buy side of internal txn, check internal txn for buy of ETH
                                for (InternalTxn internalTxn : etherscanInternalTxn.result) {
                                    if(StaticObjects.DEXEthAddresses.containsKey(internalTxn.from)){
                                        txnPair[0] = new Balance(Long.parseLong(internalTxn.value), Long.parseLong(internalTxn.timeStamp), "ETH");
                                        break;
                                    }
                                }
                            }
                        }

                        if (txnPair[0] == null || txnPair[1]== null){ //if whatever reason there is null in txnpair, throw exception
                            throw new Exception(MessageFormat.format("Error when processing transactions. Can't find part of transaction in list of transactions from EtherScan.  \n TxnHash: {0}", txnHash));
                        }
                        //Calculate transactions and determine balances
                        balances.get(txnPair[0].ticker).add(txnPair[0]); //add to buy balance
                        Balance sellBalance = txnPair[1];
                        Queue<Balance> sellSide = balances.get(sellBalance.ticker);
                        double remainingTxnVal = sellBalance.numOfUnits;
                        while (remainingTxnVal > 0d) {
                            Balance sellSideHead = sellSide.peek();
                            if (sellSideHead == null) {
//                                    //TODO: Figure out better message when there is not enough ETH in balance to process sell
//                                    // Maybe add something info on date of txn or previous txn or starting ETH balance or how much is missing
//                                    // Just more info to give use to figure out where error has occured
                                throw new Exception(MessageFormat.format("Error when processing ETH transactions. Not enough ETH to sell in transaction.\n TxnHash: {0} \n Value (ETH): {1}", txnHash, normalTxn.value));
                            }
//                                //TODO: Refactor with generics to have cleaner code
//                                //if fresh transaction and not continuing processing remainder of transaction
//                                if (sellBalance == null){
//                                    sellBalance = new Balance(txnVal, Long.parseLong(normalTxn.timeStamp),"ETH");
//                                }
//                                //If balance is less than txnVal, add to form8949 and process remainder in next item in balance queue
                            if (sellSideHead.numOfUnits < txnVal){
                                //subtract from txnVal so that remainder will be processed in next loop
                                remainingTxnVal -= sellSideHead.numOfUnits;
                                form8949.add(new Form8949Obj(sellSideHead,sellBalance));
                                //update balance so that remainder is processed in next loop
                                sellBalance.numOfUnits = txnVal;
                                sellSide.remove();
                            }
                            else
                            {
                                form8949.add(new Form8949Obj(sellSideHead,sellBalance));
                                if (sellSideHead.numOfUnits == txnVal){
                                    sellSide.remove();
                                }
                                else{
                                    sellSideHead.numOfUnits -= txnVal;
                                }
                                //set remainingTxnVal to 0 so that it exits while loop and null sellBalance
                                remainingTxnVal = 0;
                                sellBalance = null;
                            }
                        }
                    }
                }
            }
            else {
                System.out.println("Error with normal transactions from Etherscan\nMessage: " + etherscanNormalTxn.message
                        + "\nCannot continue further");
            }

        }
        //TODO Figure out exceptions to catch and deal with.
        catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.out.println("Did not process transactions");
        }
    }
}
