package com.example.zhannalibman.myshoppinglist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zhannalibman on 08/07/16.
 */
public class ShoppingListsAdapter extends ArrayAdapter<ShoppingList>{
    private MainActivity activity;
    private List<ShoppingList> listList;
    private int positionOfSelectedList;

    ShoppingListsAdapter(MainActivity activity, List<ShoppingList> listList) {
        super(activity, R.layout.list_in_lists, listList);
        this.activity = activity;
        this.listList = listList;
    }

    public int getPositionOfSelectedList() {
        return positionOfSelectedList;
    }

    private static class ViewContainer{
        LinearLayout listLayout;
        LinearLayout clickableListLayout;
        TextView listNameInList;
        TextView listDate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewContainer viewContainer;
        View singleList = convertView;
        if (singleList == null){
            LayoutInflater inflater = activity.getLayoutInflater();
            singleList = inflater.inflate(R.layout.list_in_lists, parent, false);
            viewContainer = new ViewContainer();
            viewContainer.listLayout = (LinearLayout)singleList.findViewById(R.id.listLayout);
            viewContainer.clickableListLayout = (LinearLayout)singleList.findViewById(R.id.clickableListLayout);
            viewContainer.listNameInList = (TextView) singleList.findViewById(R.id.listNameInList);
            viewContainer.listDate = (TextView) singleList.findViewById(R.id.listDate);
            singleList.setTag(viewContainer);
        }
        else {
            viewContainer = (ViewContainer)singleList.getTag();
        }
        viewContainer.listNameInList.setText(listList.get(position).getName());
        viewContainer.listDate.setText(listList.get(position).getDate());
        viewContainer.clickableListLayout.setTag(position);
        viewContainer.clickableListLayout.setOnClickListener(onClickListener);
        viewContainer.clickableListLayout.setOnLongClickListener(onLongClickListener);

        return singleList;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            int location = (int)v.getTag();
            ListActivity.startListActivity(activity, location);
        }
    };

    /**
     * Defining onLongClickListener for starting actionMode for selected list.
     * */
    private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener(){

        @Override
        public boolean onLongClick(View v) {
            positionOfSelectedList = (int)v.getTag();
            if (activity.actionMode != null) {
                return false;
            }
            // Start actionMode
            activity.actionMode = activity.startActionMode(activity.actionModeCallback);
            ((View)v.getParent()).setSelected(true);
            return true;
        }
    };
}
