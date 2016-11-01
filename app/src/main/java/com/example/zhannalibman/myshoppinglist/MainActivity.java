package com.example.zhannalibman.myshoppinglist;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 14;
    List<ShoppingList> listList;
    SimpleDateFormat dateFormat;

    EditText activity_main_enterListName;
    ListView activity_main_listOfLists;

    private ShoppingListsAdapter listArrayAdapter;

    private String enteredListName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity_main_enterListName = (EditText) findViewById(R.id.activity_main_enterListName);
        activity_main_listOfLists = (ListView)findViewById(R.id.activity_main_listOfLists);
        activity_main_enterListName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                checkAndAdd();
                //ListActivity.startWithListForResult(MainActivity.this, listList.get(0), REQUEST_CODE);
                return false;
            }
        });

        listList = new LinkedList<>();
        listArrayAdapter = new ShoppingListsAdapter(this,listList);
        activity_main_listOfLists.setAdapter(listArrayAdapter);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");


    }

    public void onClickAddList(View view) {
        checkAndAdd();
    }

    public void checkAndAdd(){
        enteredListName = activity_main_enterListName.getText().toString();
        if (enteredListName.isEmpty()) {
            enteredListName = "Unnamed list";
        }
        if (isAlreadyInTheList(enteredListName)) {
            showDialog();
        }
        else{
            addListToListAndGoToListActivity();
        }
    }


    public void addListToListAndGoToListActivity (){


            createNewList(enteredListName);
            //InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            activity_main_enterListName.setText("");
            activity_main_enterListName.setHint("Please enter your list name");
            ListActivity.startWithListForResult(this, listList.get(0), REQUEST_CODE);
            //Toast.makeText(MainActivity.this, "Such list already exists.", Toast.LENGTH_SHORT).show();

    }

    public boolean isAlreadyInTheList (String listName){
        for (ShoppingList list: listList) {
            if (list.getName().equals(listName)){
                return true;
            }
        }
        return false;
    }

    public void createNewList(String enteredListName){
        Date d = new Date(System.currentTimeMillis());
        String date = dateFormat.format(d);
        ShoppingList newShoppingList = new ShoppingList(enteredListName, date);
        listList.add(0, newShoppingList);
        listArrayAdapter.notifyDataSetChanged();
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public void showDialog(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setIcon(R.drawable.ic_mic_black_24dp);
        alertDialogBuilder.setMessage("Such list already exists. Add anyway?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addListToListAndGoToListActivity();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity_main_enterListName.requestFocus();
            }
        });

        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }
}
