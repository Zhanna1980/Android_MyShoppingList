package com.example.zhannalibman.myshoppinglist;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhannalibman on 06/07/16.
 */
public class ShoppingList implements Serializable {

    private String name;
    private Date date;
    List<Item> itemList;

    public ShoppingList(String name) {
        this.date = new Date(System.currentTimeMillis());
        this.name = name;
        itemList = new ArrayList<>();
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
}
