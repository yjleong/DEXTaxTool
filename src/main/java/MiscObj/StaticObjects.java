package MiscObj;

import java.util.HashMap;

public class StaticObjects {
    public static String ethAddress ="0x2032caAE5f25e61E79FaFCDBf53B6D1F1c483ec9";  //Eth address to examine
    public static HashMap<String, String> DEXEthAddresses;
    //Put in any other known Uniswap addresses in here
    static{
        DEXEthAddresses = new HashMap<>();
        DEXEthAddresses.put("0x2a1530c4c41db0b0b2bb646cb5eb1a67b7158667", "DAI");

    }

}