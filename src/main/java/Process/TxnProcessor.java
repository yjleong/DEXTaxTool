package Process;

import MiscObj.StaticObjects;
import Parse.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class TxnProcessor {

    public HashMap<String, Queue<Balance>> balances;

    public void setInitBalances(HashMap<String, Queue<Balance>> initBalances){
        this.balances = initBalances;
    }

    public void processTxns(EtherscanNormalTxn etherscanNormalTxn, EtherscanInternalTxn etherscanInternalTxn,
                            EtherscanERC20Txn etherscanERC20Txn) {
        if (etherscanNormalTxn.status.equals("1") && etherscanNormalTxn.message.equals("OK")) {
            //Iterate through each normal transaction and determine what transactions require further processing
            for (NormalTxn normalTxn : etherscanNormalTxn.result
            ) {
                //TODO Implement processing of transactions
                if (StaticObjects.DEXEthAddresses.containsKey(normalTxn.to)){ //if txn is with a known uniswap contract
                    String exchangeTicker = StaticObjects.DEXEthAddresses.get(normalTxn.to);
                    String txnHash = normalTxn.hash;
                    if (!normalTxn.value.equals("0")){  //if txn moves ETH into contract, e.g. DAI-ETH pair (i.e. buy token, sell ETH)
                        //Add buy of token into balance
                        if (!balances.containsKey(exchangeTicker)){ //check if there is already an existing balance for specific ticker
                            //create new hashmap with the queue
                            balances.put((exchangeTicker),new LinkedList<>());
                        }
                        //iterate through all erc20 txns and look for matching txn hash from normal txn
                        //get value and timestamp and save information transferred into account
                        for (ERC20Txn erc20Txn : etherscanERC20Txn.result){
                            if (erc20Txn.hash.equals(txnHash)){
                                balances.get(exchangeTicker).add
                                        (new Balance(Long.parseLong(erc20Txn.value), Long.parseLong(erc20Txn.timeStamp)));
                                break;
                            }
                        }
                        //Sell ETH and determine
                    }
                }
            }
        }
        else{
            System.out.println("Error with normal transactions from Etherscan\nMessage: " + etherscanNormalTxn.message
                    + "\nCannot continue further");
        }

    }
}
