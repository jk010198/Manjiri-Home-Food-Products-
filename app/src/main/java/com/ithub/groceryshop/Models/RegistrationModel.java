package com.ithub.groceryshop.Models;

public class RegistrationModel {

    public String id;
    public String c_id;
    public String name;
    public String email;
    public String occuption;
    public String whatsappnumber;
    public String mobileno;
    public String address;
    public String landmark;
    public String area;
    public String password;

    public RegistrationModel() {
    }

    public RegistrationModel(String id, String c_id, String name, String email, String occuption, String whatsappnumber, String mobileno,
                             String address, String landmark, String area, String password) {
        this.id = id;
        this.c_id = c_id;
        this.name = name;
        this.email = email;
        this.occuption = occuption;
        this.whatsappnumber = whatsappnumber;
        this.mobileno = mobileno;
        this.address = address;
        this.landmark = landmark;
        this.area = area;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getC_id() {
        return c_id;
    }

    public void setC_id(String c_id) {
        this.c_id = c_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOccuption() {
        return occuption;
    }

    public void setOccuption(String occuption) {
        this.occuption = occuption;
    }

    public String getWhatsappnumber() {
        return whatsappnumber;
    }

    public void setWhatsappnumber(String whatsappnumber) {
        this.whatsappnumber = whatsappnumber;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
