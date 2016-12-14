package com.example.zhannalibman.myshoppinglist;

import java.text.DecimalFormat;

/**
 * Created by zhannalibman on 06/07/16.
 */
public class Item {
    private String name;
    private String unit;
    private float quantity;
    private String category;
    private String notes;
    private String itemImageFilePath;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if ( category != null && !category.isEmpty()) {
            this.category = category;
        }
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        if (notes != null && !notes.isEmpty()) {
            this.notes = notes;
        }
    }
    public String getItemImageFilePath() {
        return itemImageFilePath;
    }

    public void setItemImageFilePath(String itemImageFileName) {
        this.itemImageFilePath = itemImageFileName;
    }

    public String itemQuantityToString(){
        return new DecimalFormat("#.##").format(quantity);
    }

    public String itemQuantityAndUnitsToString(){
        return itemQuantityToString() + unit;
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
