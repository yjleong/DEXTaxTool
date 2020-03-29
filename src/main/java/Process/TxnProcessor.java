package Process;

import Parse.EtherscanERC20Txn;
import Parse.EtherscanInternalTxn;
import Parse.EtherscanNormalTxn;
import Parse.NormalTxn;

import java.util.HashMap;
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
                //if ()
            }
        }
        else{
            System.out.println("Error with normal transactions from Etherscan\nMessage: " + etherscanNormalTxn.message
                    + "\nCannot continue further");
        }

    }
}
