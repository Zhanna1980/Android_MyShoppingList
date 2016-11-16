package com.example.zhannalibman.myshoppinglist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhannalibman on 02/11/2016.
 */

public class CurrentState {
    //singleton
    private static CurrentState instance;

    List<ShoppingList> listList;
    ArrayList<String> usedItemsNames;
    ArrayList<String> units;

    private CurrentState(){
        listList = new LinkedList<>();
        usedItemsNames = new ArrayList<>();
        units = new ArrayList<>(Arrays.asList("bag(s)", "bottle(s)", "box(es)", "bunch(es)", "can(s)", "case(s)", "cm", "dl",
                "dozen(s)", "g", "gallon(s)", "jar(s)", "kg", "l", "large", "lbs", "m", "medium", "ml", "pack(s)",
                "pair(s)", "piece(s)", "roll(s)", "small"));
    }

    public static CurrentState getInstance() {
        if (instance == null){
           instance = new CurrentState();
        }
        return instance;
    }
}
