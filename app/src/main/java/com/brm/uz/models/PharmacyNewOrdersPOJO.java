package com.brm.uz.models;

public class PharmacyNewOrdersPOJO {
    String name, id, parent;
    int quantity, stock, full, half, semi;

    public PharmacyNewOrdersPOJO() {
    }

    public PharmacyNewOrdersPOJO(String name, String id, String parent, int quantity, int stock, int full, int half, int semi) {
        this.name = name;
        this.id = id;
        this.parent = parent;
        this.quantity = quantity;
        this.stock = stock;
        this.full = full;
        this.half = half;
        this.semi = semi;
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

    public int getFull() {
        return full;
    }

    public void setFull(int full) {
        this.full = full;
    }

    public int getHalf() {
        return half;
    }

    public void setHalf(int half) {
        this.half = half;
    }

    public int getSemi() {
        return semi;
    }

    public void setSemi(int semi) {
        this.semi = semi;
    }
}
