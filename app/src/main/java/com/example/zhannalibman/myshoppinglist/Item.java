package com.example.zhannalibman.myshoppinglist;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhannalibman on 06/07/16.
 */
public class Item {
    String name;
    List<String> units = new ArrayList<>();
    String unit;
    float quantity;
    float sum;
    float price;
    boolean isBought = false;

    public Item(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
