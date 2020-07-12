package Process;
//This class holds information that will used in Form 8949
public class Form8949Obj {
    public String ticker;
    public String unitsSold;
    public String dateAcquired; //in mm/dd/yy
    public String dateSold;     //in mm/dd/yy
    public String proceeds;
    public String costBasis;
    public String gainOrLossVal;
    public boolean isShortTerm;

    public Form8949Obj (Balance currBalance, Balance sellBalance){
        this.ticker = sellBalance.ticker;
        double tempUnitsSold = sellBalance.numOfUnits/1000000000000000000d;
        this.unitsSold = Double.toString(tempUnitsSold);
        //TODO: make date into mm/dd/yy
        this.dateAcquired = currBalance.dateISO8601;
        this.dateSold = sellBalance.dateISO8601;
        //TODO: Protect against any overflow or any loss of precision and accuracy
        // Can lose precision and can have overflow, beware
        double tempProceeds = tempUnitsSold * sellBalance.price; //numOfUnits in 10^18 units
        this.proceeds = Double.toString(tempProceeds);
        double tempCostBasis = tempUnitsSold * currBalance.price;
        this.costBasis = Double.toString(tempCostBasis);
        this.gainOrLossVal = Double.toString(tempProceeds - tempCostBasis);
        this.isShortTerm = Math.abs(sellBalance.dateEpoch - currBalance.dateEpoch) <= 31536000L; //31536000 seconds in a year
    }
}
