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

    /**
     * Returns the date of list creation as String
     * */
    public String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date);
    }


    @Override
    public String toString() {
        return name + " (" + getDate() +")";
    }

    /**
     * Represents the list with its items in itemList as one String.
     * */
    public String getListFullDescription(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(name + System.lineSeparator());
        for (Item item :itemList){
            stringBuilder.append(item.toString() + System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    /**
     * Checks if the list contains the item with specified name
     **/
    public boolean containsItem (String itemName){
        for ( int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getName().equals(itemName)){
                return true;
            }
        }
        for (int i = 0; i < inCart.size(); i++){
            if (inCart.get(i).getName().equals(itemName)){
                return true;
            }
        }
        return false;
    }
}
