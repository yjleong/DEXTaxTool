package Process;
//This class defines balances with necessary information to determine cost basis
public class Balance {
    public long numOfUnits; //Number of units of specific ticker
    public double price;    //
    public long dateEpoch;
    public String dateISO8601;

    Balance (long numOfUnits, long dateEpoch){
        this.numOfUnits = numOfUnits;
        this.dateEpoch = dateEpoch;
        //TODO:
        //  As part of initialization, get price and determine dates
        //getPrice();
        //convertDate();
    }
    public void getPrice() {
        //TODO:
        //  Implement getting of price from CoinBase
        // GET URI = https://api.pro.coinbase.com/products/ETH-USD/candles?start=2020-03-29T01:07Z&end=2020-03-29T01:08Z&granularity=60
        // start and end are in ISO 8601 format, granularity is in seconds, will return array of array mix of int and double
        //  Implement getting price from Uniswap if not directly available from Coinbase
//        this.price = ;
    }

    public String convertDate(String dateEpochOrISO8601){
        //TODO: Implement method to convert date time formats between epoch and ISO 8601
        return "Date in correct format";
    }


}
