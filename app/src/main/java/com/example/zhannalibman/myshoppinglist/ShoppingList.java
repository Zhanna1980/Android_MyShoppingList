package com.example.zhannalibman.myshoppinglist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhannalibman on 06/07/16.
 */
public class ShoppingList implements Serializable {

    String name;
    String date;
    List<Item> itemList;

    public ShoppingList(String name, String date) {
        this.name = name;
        this.date = date;
        itemList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
