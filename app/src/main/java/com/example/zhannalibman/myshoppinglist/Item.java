package com.example.zhannalibman.myshoppinglist;

import java.text.DecimalFormat;

/**
 * Created by zhannalibman on 06/07/16.
 */
public class Item {
    private String name;
    private String unit;
    private float quantity;
    private int previousPositionInItemList = 0;

    public Item(String name) {
        this.name = name;
        this.quantity = 1;
        this.unit = "";
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        if (unit != null){
            String newUnit = unit.toLowerCase();
            if (!newUnit.isEmpty() && !CurrentState.getInstance().units.contains(newUnit)){
                CurrentState.getInstance().units.add(newUnit);
            }
            this.unit = newUnit;
        }
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        if (quantity >= 0) {
            this.quantity = quantity;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name != null && !name.isEmpty()) {
            this.name = name;
        }
    }

    public String itemQuantityAndUnitsToString(){
        return (new DecimalFormat("#.##").format(quantity)) + unit;
    }

    @Override
    public String toString() {
        return name + " " + itemQuantityAndUnitsToString();
    }

    public int getPreviousPositionInItemList() {
        return previousPositionInItemList;
    }

    public void setPreviousPositionInItemList(int previousPositionInItemList) {
        if(previousPositionInItemList >= 0) {
            this.previousPositionInItemList = previousPositionInItemList;
        }
    }
}
