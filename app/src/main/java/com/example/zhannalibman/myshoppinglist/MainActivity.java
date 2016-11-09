package com.example.zhannalibman.myshoppinglist;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_LIST_ACTIVITY = 14;
    List<ShoppingList> listList = CurrentState.getInstance().listList;

    EditText activity_main_enterListName;
    ListView activity_main_listOfLists;
    ActionBar actionBar;
    ActionMode.Callback actionModeCallback;
    ActionMode actionMode;

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
                return false;
            }
        });

        // Get a support ActionBar corresponding to this toolbar
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);

        listArrayAdapter = new ShoppingListsAdapter(this,listList);
        activity_main_listOfLists.setAdapter(listArrayAdapter);
        createActionMode();


    }

    public void onClickAddList(View view) {
        checkAndAdd();
    }

    public void checkAndAdd(){
        enteredListName = activity_main_enterListName.getText().toString();
        if (enteredListName.isEmpty()) {
            enteredListName = getString(R.string.unnamed_list);
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
            activity_main_enterListName.setHint(getString(R.string.enterListName_hint));
            ListActivity.startWithListForResult(this, listList.get(0), REQUEST_CODE_LIST_ACTIVITY);

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
        ShoppingList newShoppingList = new ShoppingList(enteredListName);
        listList.add(0, newShoppingList);
        listArrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public void showDialog(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setIcon(R.drawable.ic_mic_black_24dp);
        alertDialogBuilder.setMessage(getString(R.string.alert_add_existing_list));
        alertDialogBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addListToListAndGoToListActivity();
            }
        });
        alertDialogBuilder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity_main_enterListName.requestFocus();
            }
        });

        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }

    public void createActionMode(){
        actionModeCallback = new ActionMode.Callback() {
            //methods of ActionMode.Callback interface
            // Called when the action mode is created; startActionMode() was called
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate a menu resource providing context menu items
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_list, menu);
                return true;
            }

            // Called each time the action mode is shown. Always called after onCreateActionMode, but
            // may be called multiple times if the mode is invalidated.
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false; // Return false if nothing is done
            }

            // Called when the user selects a contextual menu item
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        mode.finish();
                        return true;
                    case R.id.delete:
                        mode.finish();
                        return true;
                    case R.id.share:
                        //shareSelectedList();
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    default:
                        return false;
                }
            }

            // Called when the user exits the action mode
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                actionMode = null;
            }
        };
    }


}
