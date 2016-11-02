package com.example.zhannalibman.myshoppinglist;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhannalibman on 02/11/2016.
 */

public class CurrentState {
    //singleton
    private static CurrentState instance;

    List<ShoppingList> listList;
    List<String> usedItemsNames;
    List<String> units;

    private CurrentState(){
        listList = new LinkedList<>();
        usedItemsNames = new ArrayList<>();
        units = new ArrayList<>();
    }

    public static CurrentState getInstance() {
        if (instance == null){
           instance = new CurrentState();
        }
        return instance;
    }
}
