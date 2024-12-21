package Models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Product {
    private String name;
    private String image;
    private Date endingTime;
    private HashMap<String, Double> prices;
    private int boughtAmount;
    private Double currentPrice;
    private String productId;


    public Product(String name, String image, Date endingTime, HashMap<String, Double> prices, int boughtAmount, Double currentPrice) {
        this.name = name;
        this.image = image;
        this.endingTime = endingTime;
        this.prices = prices;
        this.boughtAmount = boughtAmount;
        this.currentPrice = currentPrice;
    }

    public Product(){
        this.boughtAmount = 0;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(Date endingTime) {
        this.endingTime = endingTime;
    }

    public Map<String, Double> getPrices() {
        return prices;
    }

    public void setPrices(HashMap<String, Double> prices) {
        this.prices = prices;
    }

    public int getBoughtAmount() {
        return boughtAmount;
    }

    public void setBoughtAmount(int boughtAmount) {
        this.boughtAmount = boughtAmount;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
