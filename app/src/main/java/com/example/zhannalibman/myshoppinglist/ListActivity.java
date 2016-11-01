package com.example.zhannalibman.myshoppinglist;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListActivity extends AppCompatActivity {
    ShoppingList shoppingList;

    TextView activity_list_title;
    AutoCompleteTextView activity_list_enterItemName;
    ListView activity_list_itemsList;

    private ItemsInListAdapter itemsInListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        activity_list_title = (TextView)findViewById(R.id.activity_list_title);
        activity_list_itemsList = (ListView)findViewById(R.id.activity_list_itemsList);
        activity_list_enterItemName = (AutoCompleteTextView)findViewById(R.id.activity_list_enterItemName);
        activity_list_enterItemName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                addItemToListIfDoesNotExist();
                return false;
            }
        });


        Bundle bundle = getIntent().getExtras();
        shoppingList = (ShoppingList)bundle.getSerializable("shoppingList");
        activity_list_title.setText(shoppingList.getName());

        itemsInListAdapter = new ItemsInListAdapter(this, shoppingList.itemList);
        activity_list_itemsList.setAdapter(itemsInListAdapter);
    }


    public static void startWithListForResult(Activity fromActivity, ShoppingList shoppingList, int requestCode){
        Intent intent = new Intent(fromActivity, ListActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable("shoppingList", shoppingList);
        intent.putExtras(extras);
        fromActivity.startActivityForResult(intent, requestCode);
    }

    public void onClickAddItem(View view) {
        addItemToListIfDoesNotExist();
    }

    public void onClickVoiceAddItem(View view) {
    }

    public void addItemToListIfDoesNotExist(){
        String enteredItemName = activity_list_enterItemName.getText().toString();
        if (!enteredItemName.isEmpty()) {
            if (isAlreadyInTheList(enteredItemName)) {
                Toast.makeText(ListActivity.this, "it is in the list", Toast.LENGTH_SHORT).show();
            } else {
                addItemToList(enteredItemName);
                activity_list_enterItemName.setText("");
                activity_list_enterItemName.setHint("Please type item name");
                activity_list_enterItemName.requestFocus();
            }
        }
    }

    public boolean isAlreadyInTheList (String enteredItemName){
        for ( int i = 0; i < shoppingList.itemList.size(); i++) {
            if (shoppingList.itemList.get(i).getName().equals(enteredItemName)){
                return true;
            }
        }
        return false;
    }

    public void addItemToList(String enteredName){
        Item newItem = new Item(enteredName);
        shoppingList.itemList.add(0, newItem);
        itemsInListAdapter.notifyDataSetChanged();

    }
}
