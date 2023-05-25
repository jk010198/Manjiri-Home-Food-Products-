package com.ithub.groceryshop.Models;

public class FeedbackModel {

    String id;
    String name;
    String mobilenumber;
    String msg;

    public FeedbackModel() {
    }

    public FeedbackModel(String id, String name, String mobilenumber, String msg) {
        this.id = id;
        this.name = name;
        this.mobilenumber = mobilenumber;
        this.msg = msg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobilenumber() {
        return mobilenumber;
    }

    public void setMobilenumber(String mobilenumber) {
        this.mobilenumber = mobilenumber;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
