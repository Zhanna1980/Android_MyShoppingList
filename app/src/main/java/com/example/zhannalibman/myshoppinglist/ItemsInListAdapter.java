package com.example.zhannalibman.myshoppinglist;

import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zhannalibman on 31/07/16.
 */
public class ItemsInListAdapter extends BaseAdapter{
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_FOOTER = 2;

    private ListActivity activity;
    private List<Item> itemList;
    private List<Item> inCart;
    private int positionInListView;
    private LayoutInflater inflater;

    ItemsInListAdapter(ListActivity activity, List<Item> itemList, List<Item> inCart) {
        super();
        this.activity = activity;
        this.itemList = itemList;
        this.inCart = inCart;
        this.inflater = activity.getLayoutInflater();
    }

    /**
     * Class for holding pointers for view of item type in ListView
     * */
    private static class ItemContainer {
        LinearLayout itemLayout;
        LinearLayout clickableLayoutinItem;
        CheckBox checkboxInItem;
        TextView itemName;
        TextView itemQuantity;
    }

    /**
     * Class for holding pointers for view of header type
     * */
    private static class HeaderContainer {
        TextView headerTitle;
    }

    /**
     * Class for holding pointers for view of footer type
     * */
    private static class FooterContainer {
        TextView itemsAmount;
        Button btnCheckUnCheckAll;
        Button btnDeleteAll;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }


    @Override
    public int getCount() {
        return itemList.size() + inCart.size() + 3;
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        if (position < itemList.size()){
            return itemList.get(position);
        }
        else if (position == itemList.size() || position == itemList.size() + inCart.size() + 2){
            return "footer";
        }
        else if (position == itemList.size() + 1){
            return "header";
        }
        else{
            return inCart.get(position - itemList.size() - 2);
        }
    }
    @Override
    public int getItemViewType(int position) {
        if (position == itemList.size() || position == (itemList.size() + inCart.size() + 2)){
            return TYPE_FOOTER;
        }
        else if (position == itemList.size() + 1){
            return TYPE_HEADER;
        }
        else{
            return TYPE_ITEM;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int rowType = getItemViewType(position);
        View view;
        switch (rowType) {
            case TYPE_ITEM:
                view = getViewTypeItem(position, convertView, parent);
                break;
            case TYPE_HEADER:
                view = getViewTypeHeader(convertView, parent);
                break;
            case TYPE_FOOTER:
                view = getViewTypeFooter(position, convertView, parent);
                break;
            default:
                view = inflater.inflate(R.layout.footer_in_list, parent, false);
                break;
        }
        convertView = view;
        return convertView;
    }

    private View getViewTypeItem(int position, View convertView, ViewGroup parent){
        ItemContainer itemContainer;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_in_list, parent, false);
            itemContainer = new ItemContainer();
            itemContainer.itemLayout = (LinearLayout)convertView.findViewById(R.id.itemLayout);
            itemContainer.clickableLayoutinItem = (LinearLayout)convertView.findViewById(R.id.clickableLayoutinItem);
            itemContainer.checkboxInItem = (CheckBox) convertView.findViewById(R.id.checkboxInItem);
            itemContainer.itemName = (TextView) convertView.findViewById(R.id.itemName);
            itemContainer.itemQuantity = (TextView) convertView.findViewById(R.id.itemQuantity);
            convertView.setTag(itemContainer);
            itemContainer.clickableLayoutinItem.setOnLongClickListener(onLongClickListener);
        }
        else{
            itemContainer = (ItemContainer) convertView.getTag();
        }
        if (position < itemList.size()) {
            itemContainer.itemName.setText(itemList.get(position).getName());
            itemContainer.itemName.setPaintFlags(0);
            itemContainer.itemQuantity.setText(itemList.get(position).itemQuantityAndUnitsToString());
            itemContainer.checkboxInItem.setOnCheckedChangeListener(null);
            itemContainer.checkboxInItem.setChecked(false);
            itemContainer.checkboxInItem.setOnCheckedChangeListener(onCheckedChangeListener);
        }
        else {
            itemContainer.itemName.setText(inCart.get(position - itemList.size() - 2).getName());
            itemContainer.itemQuantity.setText(inCart.get(position - itemList.size() - 2).itemQuantityAndUnitsToString());
            itemContainer.itemName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            itemContainer.checkboxInItem.setOnCheckedChangeListener(null);
            itemContainer.checkboxInItem.setChecked(true);
            itemContainer.checkboxInItem.setOnCheckedChangeListener(onCheckedChangeListener);
        }
        itemContainer.clickableLayoutinItem.setTag(position);
        itemContainer.checkboxInItem.setTag(position);
        return convertView;
    }

    /**
     * Returning view of header type.
     * */
    private View getViewTypeHeader(View convertView, ViewGroup parent){
        HeaderContainer headerContainer;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.header_in_list, parent, false);
            headerContainer = new HeaderContainer();
            headerContainer.headerTitle = (TextView)convertView.findViewById(R.id.headerTitle);
            convertView.setTag(headerContainer);
        }
        else{
            headerContainer = (HeaderContainer)convertView.getTag();
        }
        headerContainer.headerTitle.setText(R.string.items_in_cart_header_title);
        return convertView;
    }

    /**
     * Returning view of footer type.
     * */
    private View getViewTypeFooter(int position, View convertView, ViewGroup parent){
        FooterContainer footerContainer;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.footer_in_list, parent, false);
            footerContainer = new FooterContainer();
            footerContainer.itemsAmount = (TextView)convertView.findViewById(R.id.itemsAmount);
            footerContainer.btnCheckUnCheckAll = (Button)convertView.findViewById(R.id.btnCheckUnCheckAll);
            footerContainer.btnDeleteAll = (Button)convertView.findViewById(R.id.btnDeleteAll);
            convertView.setTag(footerContainer);
        }
        else{
            footerContainer = (FooterContainer)convertView.getTag();
        }
        if (position == itemList.size()) {
            footerContainer.itemsAmount.setText(Integer.valueOf(itemList.size()).toString() + " " + activity.getString(R.string.items_footer));
            footerContainer.btnCheckUnCheckAll.setText(R.string.check_all);
        }
        else {
            footerContainer.itemsAmount.setText(Integer.valueOf(inCart.size()).toString() + " " + activity.getString(R.string.items_footer));
            footerContainer.btnCheckUnCheckAll.setText(R.string.uncheck_all);
        }
        footerContainer.btnCheckUnCheckAll.setOnClickListener(onBtnCheckUncheckAllClickListener);
        footerContainer.btnDeleteAll.setTag(position);
        footerContainer.btnDeleteAll.setOnClickListener(onBtnDeleteAllClickListener);
        return convertView;
    }

    private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener(){

        @Override
        public boolean onLongClick(View v) {
            positionInListView = (int)v.getTag();
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
            positionInListView = (int)buttonView.getTag();
            if(positionInListView < itemList.size()){
                Item item = (Item)getItem(positionInListView);
                item.setPreviousPositionInItemList(positionInListView);
                itemList.remove(positionInListView);
                inCart.add(0,item);
            }
            else{
                Item item = (Item)getItem(positionInListView);
                inCart.remove(positionInListView - itemList.size() - 2);
                if(item.getPreviousPositionInItemList() < itemList.size()) {
                    itemList.add(item.getPreviousPositionInItemList(), item);
                }
                else{
                    itemList.add(item);
                }
            }
            notifyDataSetChanged();
        }
    };

    /**
     * handling click event for btnCheckUncheckAll.
     * If "Check all" is clicked all items are moved from itemList to inCart list.
     * If "Uncheck all" is clicked all items are moved from inCart list to itemList.
     * */
    private View.OnClickListener onBtnCheckUncheckAllClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button clickedButton = (Button)v;
            String buttonText = clickedButton.getText().toString();
            if (buttonText.equals(activity.getString(R.string.check_all))){
                inCart.addAll(itemList);
                itemList.clear();
            }
            else{
                itemList.addAll(inCart);
                inCart.clear();
            }
            notifyDataSetChanged();
        }
    };

    /**
     * Handling click event for btnDeleteAll
     * If "Delete All" button was clicked at the footer of itemList section all items from the section would be removed.
     * Otherwise if "Delete All" button was clicked at the footer of inCart section all items from the inCart section would be removed.
     * */
    private View.OnClickListener onBtnDeleteAllClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if((int)v.getTag() == itemList.size()){
                itemList.clear();
            }
            else{
                inCart.clear();
            }
            notifyDataSetChanged();
        }
    };
}
