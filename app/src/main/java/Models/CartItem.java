package Models;

import java.util.Date;

public class CartItem {
    private String productId;
    private int purchasedAmount;
    private Date timestamp;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getPurchasedAmount() {
        return purchasedAmount;
    }

    public void setPurchasedAmount(int purchasedAmount) {
        this.purchasedAmount = purchasedAmount;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public CartItem(String productId, int purchasedAmount, Date timestamp) {
        this.productId = productId;
        this.purchasedAmount = purchasedAmount;
        this.timestamp = timestamp;
    }
    public CartItem(){}
}
