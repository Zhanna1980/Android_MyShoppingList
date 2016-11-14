package com.example.zhannalibman.myshoppinglist;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zhannalibman on 31/07/16.
 */
public class ItemsInListAdapter extends ArrayAdapter<Item>{
    private ListActivity activity;
    private List<Item> itemList;
    private int location;
    private int counterOfChecked = 0;

    ItemsInListAdapter(ListActivity activity, List<Item> itemList) {
        super(activity, R.layout.item_in_list, itemList);
        this.activity = activity;
        this.itemList = itemList;
    }

    private static class ViewContainer{
        LinearLayout itemLayout;
        LinearLayout clickableLayoutinItem;
        CheckBox checkboxInItem;
        TextView itemName;
        TextView itemQuantity;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewContainer viewContainer;
        View singleItem = convertView;
        if (singleItem == null){
            LayoutInflater inflater = activity.getLayoutInflater();
            singleItem = inflater.inflate(R.layout.item_in_list, parent, false);
            viewContainer = new ViewContainer();
            viewContainer.itemLayout = (LinearLayout)singleItem.findViewById(R.id.itemLayout);
            viewContainer.clickableLayoutinItem = (LinearLayout)singleItem.findViewById(R.id.clickableLayoutinItem);
            viewContainer.checkboxInItem = (CheckBox) singleItem.findViewById(R.id.checkboxInItem);
            viewContainer.itemName = (TextView) singleItem.findViewById(R.id.itemName);
            viewContainer.itemQuantity = (TextView) singleItem.findViewById(R.id.itemQuantity);
            singleItem.setTag(viewContainer);

            viewContainer.clickableLayoutinItem.setOnLongClickListener(onLongClickListener);
        }
        else {
            viewContainer = (ViewContainer)singleItem.getTag();
        }
        viewContainer.itemName.setText(itemList.get(position).getName());
        viewContainer.itemName.setPaintFlags(itemList.get(position).isBought ? Paint.STRIKE_THRU_TEXT_FLAG : 0);
        //viewContainer.itemQuantity.setText(String.valueOf(itemList.get(position).getQuantity()) + itemList.get(position).getUnit());
        viewContainer.clickableLayoutinItem.setTag(position);
        viewContainer.checkboxInItem.setTag(position);
        viewContainer.checkboxInItem.setOnCheckedChangeListener(null);
        viewContainer.checkboxInItem.setChecked(itemList.get(position).isBought);
        viewContainer.checkboxInItem.setOnCheckedChangeListener(onCheckedChangeListener);
        return singleItem;
    }


    private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener(){

        @Override
        public boolean onLongClick(View v) {
            location = (int)v.getTag();
            if (activity.actionMode != null) {
                return false;
            }
            //Start actionMode
            activity.actionMode = activity.startActionMode(activity.actionModeCallback);
            ((View)v.getParent()).setSelected(true);
            return true;
        }
    };

    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            location = (int)buttonView.getTag();
            Item item = getItem(location);
            remove(item);
            if (item != null) {
                item.isBought = isChecked;
            }
            if (isChecked){
                insert(item, itemList.size() - counterOfChecked);
                counterOfChecked ++;
                //add(item);
            }
            else{
                insert(item, 0);
                counterOfChecked --;
            }
        }
    };
}
