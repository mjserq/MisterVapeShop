package com.flysolo.mistervapeshop.models;



public class CartItem {
    private String cartItemID;
    private String userID;
    private String productID;
    private int cartItemQuantity;
    private long timestamp;
    private boolean isChecked;
    public CartItem() {
    }

    public CartItem(String cartItemID, String userID, String productID, int cartItemQuantity, long timestamp) {
        this.cartItemID = cartItemID;
        this.userID = userID;
        this.productID = productID;
        this.cartItemQuantity = cartItemQuantity;
        this.timestamp = timestamp;
        isChecked = false;
    }

    public String getCartItemID() {
        return cartItemID;
    }

    public void setCartItemID(String cartItemID) {
        this.cartItemID = cartItemID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public int getCartItemQuantity() {
        return cartItemQuantity;
    }

    public void setCartItemQuantity(int cartItemQuantity) {
        this.cartItemQuantity = cartItemQuantity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
