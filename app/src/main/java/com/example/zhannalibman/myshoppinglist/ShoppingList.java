package com.example.zhannalibman.myshoppinglist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhannalibman on 06/07/16.
 */
public class ShoppingList {

    private String name;
    private Date date;
    List<Item> itemList;
    List<Item> inCart;

    public ShoppingList(String name) {
        this.date = new Date(System.currentTimeMillis());
        this.name = name;
        itemList = new ArrayList<>();
        inCart = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date);
    }

    public void setNewDate() {
        this.date = new Date(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(name + System.lineSeparator());
        for (Item item :itemList){
            stringBuilder.append(item.toString() + System.lineSeparator());
        }
        String listAsString = stringBuilder.toString();
        return listAsString;
    }
}
