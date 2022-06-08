package com.flysolo.mistervapeshop.models;

public class Product {
    private String product_id;
    private String product_name;
    private double price;
    private String gen_name;
    private int qty;
    private  String supplier;

    public Product() {
    }

    public Product(String product_id, String product_name, double price, String gen_name, int qty, String supplier){
        this.product_id = product_id;
        this.product_name = product_name;
        this.gen_name = gen_name;
        this.price = price;
        this.qty = qty;
        this.supplier = supplier;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getTitle() {
        return product_name;
    }

    public void setTitle(String title) {
        this.product_name = product_name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getString() {
        return gen_name;
    }

    public void setString(String gen_name) {
        this.gen_name = gen_name;
    }

    public int getQty() {return qty;}

    public void setQty (int qty) {
        this.qty = qty;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getGen_name() {
        return gen_name;
    }

    public void setGen_name(String gen_name) {
        this.gen_name = gen_name;
    }
}
