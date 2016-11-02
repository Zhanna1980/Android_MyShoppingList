package com.example.zhannalibman.myshoppinglist;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhannalibman on 06/07/16.
 */
public class Item {
    String name; 
    String unit;
    float quantity;
    boolean isBought = false;

    public Item(String name) {
        this.name = name;
        this.quantity = 1;
        this.unit = "";
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
