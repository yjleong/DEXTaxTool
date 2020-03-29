package Parse;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

//This class handles the parsing of JSON objects and turns them into Java objects
public class TxnParser {

    public enum etherScanTxnEnum{
        NormalTxn, ERC20Txn, InternalTxn
    }

    private AbstractEtherscanTxn deserializeJSON(String str, etherScanTxnEnum txnEnum) {
        switch (txnEnum) {
            case NormalTxn:
                return new Gson().fromJson(str, EtherscanNormalTxn.class);
            case ERC20Txn:
                return new Gson().fromJson(str, EtherscanERC20Txn.class);
            case InternalTxn:
                return new Gson().fromJson(str, EtherscanInternalTxn.class);
        }
        return null;
    }

    //TODO:
    //  Implement StringBuilder to take in ETH address and Etherscan API key from console in MainEntry.java
    //  API requests will only return 10000 transactions only, if there is more need another more specific request
    public AbstractEtherscanTxn httpRequest(etherScanTxnEnum txnEnum){
        String uri = "";
        switch (txnEnum){
            //For development purposes, use default API key. Will require user input for API key in the future.
            case NormalTxn:
                uri = "http://api.etherscan.io/api?module=account&action=txlist&address=0x2032caAE5f25e61E79FaFCDBf53B6D1F1c483ec9&startblock=0&endblock=99999999&sort=asc&apikey=FDE2GYHDIWC3W85J4JFCTSQIDJIHTNA676";
                break;
            case ERC20Txn:
                uri = "http://api.etherscan.io/api?module=account&action=tokentx&address=0x2032caAE5f25e61E79FaFCDBf53B6D1F1c483ec9&startblock=0&endblock=999999999&sort=asc&apikey=FDE2GYHDIWC3W85J4JFCTSQIDJIHTNA676";
                break;
            case InternalTxn:
                uri = "http://api.etherscan.io/api?module=account&action=txlistinternal&address=0x2032caAE5f25e61E79FaFCDBf53B6D1F1c483ec9&startblock=0&endblock=99999999&sort=asc&apikey=FDE2GYHDIWC3W85J4JFCTSQIDJIHTNA676";
                break;
        }
        //Create client
        HttpClient client = HttpClient.newHttpClient();
        //Build request
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri)).build();
        //send request, asynchrously and tell server that want to receive as string
        String str = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                //once asynch is done, want to apply method in parenthesis to the result, this is a lambda expression with :: sign
                //use body method in HttpResponse class on the result (i.e. get the result)
                .thenApply(HttpResponse::body)
                //join is returning the result from CompletableFuture.
                .join();
        return deserializeJSON(str,txnEnum);
    }


}
