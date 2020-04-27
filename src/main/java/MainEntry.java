import Parse.EtherscanERC20Txn;
import Parse.EtherscanInternalTxn;
import Parse.EtherscanNormalTxn;
import Parse.TxnParser;
import Process.TxnProcessor;
import Process.Balance;
import MiscObj.StaticObjects;

import java.util.*;

public class MainEntry {

    public static void main(String[] args) {
        System.out.println("DEXTaxTool App");
        Scanner in = new Scanner(System.in);
        System.out.println("Input valid Ethereum address:");
        //TODO:
        // Determine if valid Eth address or not
        // Pass address to TxnParser to be used in string builder
        String s = in.nextLine();
        //For now, use default ETH address for development, see StaticObjects for default string
        if (!s.equals("")) {
            StaticObjects.ethAddress = s;
        }
        //TODO:
        //  Ask user input for Etherscan API key and pass into TxnParser
        //Parse transactions from Etherscan and create Java objects
        System.out.println("Begin parsing transactions from Etherscan");
        TxnParser txnParser = new TxnParser();
        //TODO:
        //  Get user input for EtherScan API key an Account pass into parser
        //  Code to test connection before connection to the API
        //  Add a check to what is returned to see if APIs returned something good to work with else break and
        //  API requests will only return 10000 transactions only, if there is more need another more specific request
        EtherscanNormalTxn etherscanNormalTxn =
                (EtherscanNormalTxn) txnParser.httpRequest(TxnParser.etherScanTxnEnum.NormalTxn);
        if (!etherscanNormalTxn.status.equals("1") || !etherscanNormalTxn.message.equals("OK")){
            System.out.println("Error with getting normal transactions from Etherscan\nMessage: " + etherscanNormalTxn.message
                    + "\nCannot continue further");
        }
        else {
            EtherscanERC20Txn etherscanERC20Txn =
                    (EtherscanERC20Txn) txnParser.httpRequest(TxnParser.etherScanTxnEnum.ERC20Txn);
            if (!etherscanERC20Txn.status.equals("1") || !etherscanERC20Txn.message.equals("OK")) {
                System.out.println("Error with getting ERC20 transactions from Etherscan\nMessage: " + etherscanERC20Txn.message);
            }

            EtherscanInternalTxn etherscanInternalTxn =
                    (EtherscanInternalTxn) txnParser.httpRequest(TxnParser.etherScanTxnEnum.InternalTxn);
            if (!etherscanInternalTxn.status.equals("1") || !etherscanInternalTxn.message.equals("OK")) {
                System.out.println("Error with internal Transactions from Etherscan\nMessage: " + etherscanInternalTxn.message);
            }

            //Process transactions
            System.out.println("Begin processing transactions");
            TxnProcessor txnProcessor = new TxnProcessor();
            //TODO: Ask user to input initial balances or determine initial balance before financial year going in
            //For MVP purpose, initial balance for financial year 0.3 ETH (init balance of ETH address being examined)
            Queue<Balance> tempBalance= new LinkedList<Balance>();
            tempBalance.add(new Balance(300000000000000000L,1582259618, "ETH"));
            HashMap<String, Queue<Balance>> tempInitBalance = new HashMap<String, Queue<Balance>>();
            txnProcessor.setInitBalances(new HashMap<>());


        }
        System.out.println("Exiting DEXTaxTool");
    }
}