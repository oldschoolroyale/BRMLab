package com.brm.uz.models;

public class PharmacyNewOrdersPOJO {
    String name, id, parent;
    int quantity, stock;
    double full, half, semi, price, fullPrice;

    public PharmacyNewOrdersPOJO() {
    }

    public PharmacyNewOrdersPOJO(String name, String id, String parent, int quantity, int stock, double full, double half, double semi, double price, double fullPrice) {
        this.name = name;
        this.id = id;
        this.parent = parent;
        this.quantity = quantity;
        this.stock = stock;
        this.full = full;
        this.half = half;
        this.semi = semi;
        this.price = price;
        this.fullPrice = fullPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getFull() {
        return full;
    }

    public void setFull(double full) {
        this.full = full;
    }

    public double getHalf() {
        return half;
    }

    public void setHalf(double half) {
        this.half = half;
    }

    public double getSemi() {
        return semi;
    }

    public void setSemi(double semi) {
        this.semi = semi;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getFullPrice() {
        return fullPrice;
    }

    public void setFullPrice(double fullPrice) {
        this.fullPrice = fullPrice;
    }
}
