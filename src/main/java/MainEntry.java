import Parse.EtherscanERC20Txn;
import Parse.EtherscanInternalTxn;
import Parse.EtherscanNormalTxn;
import Parse.TxnParser;

public class MainEntry {


    public static void main(String[] args) {
        System.out.println("DEXTaxTool App");
        //Parse transactions from Etherscan and create Java objects
        TxnParser txnParser = new TxnParser();
        //To Do:
        //Get user input for EtherScan API key an Account pass into parser
        EtherscanNormalTxn etherscanNormalTxn =
                (EtherscanNormalTxn) txnParser.httpRequest(TxnParser.etherScanTxnEnum.NormalTxn);
        EtherscanERC20Txn etherscanERC20Txn =
                (EtherscanERC20Txn) txnParser.httpRequest(TxnParser.etherScanTxnEnum.ERC20Txn);
        EtherscanInternalTxn etherscanInternalTxn =
                (EtherscanInternalTxn) txnParser.httpRequest(TxnParser.etherScanTxnEnum.InternalTxn);

    }
}