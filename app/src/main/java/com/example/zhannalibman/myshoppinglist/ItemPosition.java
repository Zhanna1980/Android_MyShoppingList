package com.example.zhannalibman.myshoppinglist;

import java.io.Serializable;

/**
 * Created by zhannalibman on 08/12/2016.
 *
 * Class for preserving the item location and further search for the item
 *
 */

public class ItemPosition implements Serializable{
    int shoppingListIndexInListList;
    boolean isSelectedItemInItemList;
    int positionInSectionList;

    ItemPosition(int shoppingListIndexInListList, boolean isSelectedItemInItemList, int positionInSectionList){
        this.shoppingListIndexInListList = shoppingListIndexInListList;
        this.isSelectedItemInItemList = isSelectedItemInItemList;
        this.positionInSectionList = positionInSectionList;
    }

    Item getItem(){
        if (shoppingListIndexInListList > -1 && shoppingListIndexInListList < CurrentState.getInstance().listList.size()
                && positionInSectionList > -1) {
            ShoppingList shoppingList = CurrentState.getInstance().listList.get(shoppingListIndexInListList);
            if (isSelectedItemInItemList && positionInSectionList < shoppingList.itemList.size()){
                return shoppingList.itemList.get(positionInSectionList);
            }
            else if (!isSelectedItemInItemList && positionInSectionList < shoppingList.inCart.size()){
                return shoppingList.inCart.get(positionInSectionList);
            }
            else{
                return null;
            }
        }
        else{
            return null;
        }
    }
}
