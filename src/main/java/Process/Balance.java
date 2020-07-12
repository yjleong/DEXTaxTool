package Process;

import java.sql.Time;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

import MiscObj.StaticObjects;
import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;


//This class defines balances with necessary information to determine cost basis
public class Balance {
    public double numOfUnits; //Number of units of specific ticker, in 10^18 units
    public double price;    //in USD
    public long dateEpoch;
    public String dateISO8601;
    public String ticker;

    public Balance(double numOfUnits, long dateEpoch, String ticker) throws Exception {
        this.numOfUnits = numOfUnits;
        this.dateEpoch = dateEpoch;
        this.dateISO8601 = convertEpochToISO8601();
        this.ticker = ticker;
        getPrice(ticker);
    }
    public void getPrice(String ticker) throws Exception {
        //TODO:
        //  Implement getting of price from CoinBase
        // GET URI = https://api.pro.coinbase.com/products/ETH-USD/candles?start=2020-03-29T01:07Z&end=2020-03-29T01:08Z&granularity=60
        // start and end are in ISO 8601 format, granularity is in seconds, will return array of array mix of int and double
        //  Implement getting price from Uniswap if not directly available from Coinbase
        if(StaticObjects.CBProTickers.containsKey(ticker)) {
            String uri = getURI(ticker);
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
            //Max rate is 3 request per second so wait for a little bit to not go over rate
            TimeUnit.MILLISECONDS.sleep(300);
            //Returns two dimentional array that contains one array in [time, low, high,open,close,volume]
            //TODO: Can exceed public rate of request from CBPro and might crash program
            // Figure out way to get around that
            try {
                double[][] priceArr = new Gson().fromJson(str, double[][].class);
                //TODO: Consider better way of determining price, check message
                // figure out how to deal with messages too, maybe try parse that first?
                //Get average price
                this.price = (priceArr[0][1] + priceArr[0][2] + priceArr[0][3] + priceArr[0][4])/4;
            }
            catch (Exception e){
                System.out.println("Failed to get price from CBPro. String returned: " + str);
                throw e;
            }
        }
        else{
            //TODO: Implement method to get price by scanning DEX for any traded ETH at a similar time period
            // in mean time, possibly throw a statement to end program?
            throw new Exception("Can't get fair price for token because it is directly available from public APIs. Will not be able to process transactions");
        }
    }

    private String getURI(String ticker) {
        return "https://api.pro.coinbase.com/products/" + StaticObjects.CBProTickers.get(ticker) +
                "/candles?start=" + dateISO8601 + "&end=" + dateISO8601 +
                "&granularity=60";
    }

    public String convertEpochToISO8601(){
        //Format for ISO8601 for API GET request
        String format = "yyyy-MM-dd'T'HH:mm'Z'";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date(this.dateEpoch * 1000));
    }
}
