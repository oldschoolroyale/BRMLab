package com.brm.uz.models;

public class AccountPOJO {
    public String name, image, region;

    public AccountPOJO() {
    }

    public AccountPOJO(String name, String image, String region) {
        this.name = name;
        this.image = image;
        this.region = region;
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
