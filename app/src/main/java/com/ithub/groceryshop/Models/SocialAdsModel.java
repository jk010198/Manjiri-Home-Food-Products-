package com.ithub.groceryshop.Models;

public class SocialAdsModel implements Comparable {

    String id;
    String image;
    String business_name;
    String business_contact;
    String alternet_contact;
    String business_basic_info;
    String business_address;

    public SocialAdsModel() {
    }

    public SocialAdsModel(String id, String image, String business_name, String business_contact, String alternet_contact,
                          String business_basic_info, String business_address) {
        this.id = id;
        this.image = image;
        this.business_name = business_name;
        this.business_contact = business_contact;
        this.alternet_contact = alternet_contact;
        this.business_basic_info = business_basic_info;
        this.business_address = business_address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }

    public String getBusiness_contact() {
        return business_contact;
    }

    public void setBusiness_contact(String business_contact) {
        this.business_contact = business_contact;
    }

    public String getAlternet_contact() {
        return alternet_contact;
    }

    public void setAlternet_contact(String alternet_contact) {
        this.alternet_contact = alternet_contact;
    }

    public String getBusiness_basic_info() {
        return business_basic_info;
    }

    public void setBusiness_basic_info(String business_basic_info) {
        this.business_basic_info = business_basic_info;
    }

    public String getBusiness_address() {
        return business_address;
    }

    public void setBusiness_address(String business_address) {
        this.business_address = business_address;
    }

    @Override
    public int compareTo( Object o) {
        return   business_name.compareTo(((SocialAdsModel)o).business_name);
    }
}
