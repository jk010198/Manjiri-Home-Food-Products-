package com.ithub.groceryshop.Models;

public class OrdersModel {

    String id;
    String order_id;
    String name;
    String customer_id;
    String whatsapp_number;
    String alternet_number;
    String order_weight;
    String order;
    String product_order_qty;
    String order_date_time;
    String order_grandtotal;
    String address;
    String landmark;
    String area;
    String agent;
    String pending_orders;

    public OrdersModel(String id, String order_id, String name, String customer_id, String whatsapp_number, String alternet_number,
                       String order_weight, String order, String product_order_qty, String order_date_time, String order_grandtotal,
                       String address, String landmark, String area, String agent, String pending_orders) {
        this.id = id;
        this.order_id = order_id;
        this.name = name;
        this.customer_id = customer_id;
        this.whatsapp_number = whatsapp_number;
        this.alternet_number = alternet_number;
        this.order_weight = order_weight;
        this.order = order;
        this.product_order_qty = product_order_qty;
        this.order_date_time = order_date_time;
        this.order_grandtotal = order_grandtotal;
        this.address = address;
        this.landmark = landmark;
        this.area = area;
        this.agent = agent;
        this.pending_orders = pending_orders;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getWhatsapp_number() {
        return whatsapp_number;
    }

    public void setWhatsapp_number(String whatsapp_number) {
        this.whatsapp_number = whatsapp_number;
    }

    public String getAlternet_number() {
        return alternet_number;
    }

    public void setAlternet_number(String alternet_number) {
        this.alternet_number = alternet_number;
    }

    public String getOrder_weight() {
        return order_weight;
    }

    public void setOrder_weight(String order_weight) {
        this.order_weight = order_weight;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getProduct_order_qty() {
        return product_order_qty;
    }

    public void setProduct_order_qty(String product_order_qty) {
        this.product_order_qty = product_order_qty;
    }

    public String getOrder_date_time() {
        return order_date_time;
    }

    public void setOrder_date_time(String order_date_time) {
        this.order_date_time = order_date_time;
    }

    public String getOrder_grandtotal() {
        return order_grandtotal;
    }

    public void setOrder_grandtotal(String order_grandtotal) {
        this.order_grandtotal = order_grandtotal;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getPending_orders() {
        return pending_orders;
    }

    public void setPending_orders(String pending_orders) {
        this.pending_orders = pending_orders;
    }
}