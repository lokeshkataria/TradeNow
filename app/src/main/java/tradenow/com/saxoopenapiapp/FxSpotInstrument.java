package tradenow.com.saxoopenapiapp;

/**
 * Created by Lokesh on 01-05-2017.
 */

public class FxSpotInstrument {
    private String description;
    private String identifier;
    private String symbol;
    private String price;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public FxSpotInstrument(){
        description = "";
        identifier = "";
        symbol = "";
        price="";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
