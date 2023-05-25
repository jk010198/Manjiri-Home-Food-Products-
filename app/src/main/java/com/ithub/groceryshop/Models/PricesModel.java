package com.ithub.groceryshop.Models;

import java.io.Serializable;

public class PricesModel implements Serializable {
    private String price_id;
    private String product_id;
    private String price;
    private String qty;
    private String unit;

    public PricesModel() {
    }

    public PricesModel(String price_id, String product_id, String price, String qty, String unit) {
        this.price_id = price_id;
        this.product_id = product_id;
        this.price = price;
        this.qty = qty;
        this.unit = unit;
    }

    public String getPrice_id() {
        return this.price_id;
    }

    public String getProduct_id() {
        return this.product_id;
    }

    public String getPrice() {
        return this.price;
    }

    public String getQty() {
        return this.qty;
    }

    public String getUnit() {
        return this.unit;
    }
}
