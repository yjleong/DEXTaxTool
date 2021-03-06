package MiscObj;

import java.util.HashMap;
import java.util.HashSet;

public class StaticObjects {
    public static String ethAddress ="0x2032caAE5f25e61E79FaFCDBf53B6D1F1c483ec9";  //Eth address to examine
    public static HashMap<String, String> DEXEthAddresses;
    public static HashMap<String, String> CBProTickers;
    static{
        //Known Uniswap addresses and associated tokens
        DEXEthAddresses = new HashMap<>();
        DEXEthAddresses.put("0x2a1530c4c41db0b0b2bb646cb5eb1a67b7158667", "DAI");
        DEXEthAddresses.put("0x2c4bd064b998838076fa341a83d007fc2fa50957", "MKR");

        //Known CoinbasePro directly available prices in USD
        CBProTickers = new HashMap<>();
        CBProTickers.put("ETH","ETH-USD");
        CBProTickers.put("DAI", "DAI-USDC");
        CBProTickers.put("MKR", "MKR-USD");
    }

}
