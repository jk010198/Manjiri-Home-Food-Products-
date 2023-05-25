package com.ithub.groceryshop.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Product_model implements Serializable , Comparable{
    private String product_id;
    private String product_name;
    private String product_description;
    private String product_image;
    private String category_id;
    private String in_stock;
    private String price;
    private String unit_value;
    private String unit;
    private String increament;
    private String discount;
    private String stock;
    private String title;
    @SerializedName("prices")
    private ArrayList<PricesModel> pricesModelArrayList;

    public Product_model() {
    }

    public Product_model(String product_id, String product_name, String product_description, String product_image, String category_id,
                         String in_stock, String price, String unit_value, String unit, String increament, String discount, String stock,
                         String title, ArrayList<PricesModel> pricesModelArrayList) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.product_description = product_description;
        this.product_image = product_image;
        this.category_id = category_id;
        this.in_stock = in_stock;
        this.price = price;
        this.unit_value = unit_value;
        this.unit = unit;
        this.increament = increament;
        this.discount = discount;
        this.stock = stock;
        this.title = title;
        this.pricesModelArrayList = pricesModelArrayList;
    }

    public String getProduct_id() {
        return this.product_id;
    }

    public String getProduct_name() {
        return this.product_name;
    }

    public String getProduct_description() {
        return this.product_description;
    }

    public String getProduct_image() {
        return product_image;
    }

    public String getCategory_id() {
        return this.category_id;
    }

    public String getIn_stock() {
        return this.in_stock;
    }

    public String getPrice() {
        return this.price;
    }

    public String getUnit_value() {
        return this.unit_value;
    }

    public String getUnit() {
        return this.unit;
    }

    public String getIncreament() {
        return this.increament;
    }

    public String getDiscount() {
        return this.discount;
    }

    public String getStock() {
        return this.stock;
    }

    public String getTitle() {
        return this.title;
    }

    public ArrayList<PricesModel> getPricesModelArrayList() {
        return this.pricesModelArrayList;
    }

    @Override
    public int compareTo( Object o) {
        return   product_name.compareTo(((Product_model)o).product_name);
    }
}
