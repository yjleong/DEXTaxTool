package MiscObj;

import java.util.HashMap;

public class StaticObjects {
    public static String ethAddress ="0x2032caAE5f25e61E79FaFCDBf53B6D1F1c483ec9";
    public static HashMap<String, String> DEXEthAddresses;
    //Put in any other known Uniswap addresses in here
    static{
        DEXEthAddresses = new HashMap<>();
        DEXEthAddresses.put("0x6B175474E89094C44Da98b954EedeAC495271d0F", "DAI");

    }

}
