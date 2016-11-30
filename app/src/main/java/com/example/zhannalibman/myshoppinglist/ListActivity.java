package com.example.zhannalibman.myshoppinglist;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class ListActivity extends AppCompatActivity {

    private final int REQUEST_CODE_SPEECH_INPUT = 100;
    private final int REQUEST_CODE_EDIT_ITEM = 200;

    private ShoppingList shoppingList;
    private int shoppingListIndexInListList;
    private ActionBar actionBar;
    ActionMode.Callback actionModeCallback;
    ActionMode actionMode;

    private AutoCompleteTextView enterItemName;
    private ListView itemsList;

    private ItemsInListAdapter itemsInListAdapter;
    private ArrayList<String> usedItems;
    private ArrayAdapter<String> autoCompleteUnitsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        itemsList = (ListView)findViewById(R.id.activity_list_itemsList);
        enterItemName = (AutoCompleteTextView)findViewById(R.id.activity_list_enterItemName);
        enterItemName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                addItemToListIfDoesNotExist();
                return false;
            }
        });

        // Get a support ActionBar corresponding to this toolbar
        actionBar = getSupportActionBar();
        // Enable the Up button
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Receiving intent with index of the current list
        shoppingListIndexInListList = getIntent().getIntExtra("shoppingListIndexInListList", -1);
        if (shoppingListIndexInListList > -1 && shoppingListIndexInListList < CurrentState.getInstance().listList.size()) {
            shoppingList = CurrentState.getInstance().listList.get(shoppingListIndexInListList);
            actionBar.setTitle(shoppingList.getName());
            //autocomplete
            usedItems = CurrentState.getInstance().usedItemsNames;
            autoCompleteUnitsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, usedItems);
            enterItemName.setAdapter(autoCompleteUnitsAdapter);
            enterItemName.setThreshold(1);
            //setting adapter for the listView
            itemsInListAdapter = new ItemsInListAdapter(this, shoppingList.itemList, shoppingList.inCart);
            itemsList.setAdapter(itemsInListAdapter);
            //creating action mode
            createActionMode();
        }
        else{
            Toast.makeText(this, this.getString(R.string.error), Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    /** Static method for using by other activities for starting ListActivity
     *@param fromActivity Current activity
     *@param index Shopping list index in listList
    * */
    public static void startListActivity(Activity fromActivity, int index){
        Intent intent = new Intent(fromActivity, ListActivity.class);
        intent.putExtra("shoppingListIndexInListList", index);
        fromActivity.startActivity(intent);
    }

    //add button was clicked
    public void onClickAddItem(View view) {
        addItemToListIfDoesNotExist();
    }

    /**
     * Method for handling onClick event for btnVoiceAdding. Checks internet connection.
     * If the device is connected to internet calls for the metod showPromptSpeechInput().
     * Otherwise makes toast that there is no internet connection for voice recognition
     * */
    public void onClickVoiceAddItem(View view) {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            showPromptSpeechInput();
        }
        else{
            Toast.makeText(this, getString(R.string.no_internet_connection_for_speech), Toast.LENGTH_SHORT).show();
        }
    }

    public void addItemToListIfDoesNotExist(){
        String enteredItemName = enterItemName.getText().toString();
        if (!enteredItemName.isEmpty()) {
            if (isAlreadyInTheList(enteredItemName)) {
                Toast.makeText(ListActivity.this, getString(R.string.already_in_list), Toast.LENGTH_SHORT).show();
            } else {
                addItemToList(enteredItemName);
                enterItemName.setText("");
                enterItemName.setHint(getString(R.string.enterItemName_hint));
                enterItemName.requestFocus();
                if (autoCompleteUnitsAdapter.getPosition(enteredItemName) < 0) {
                    autoCompleteUnitsAdapter.add(enteredItemName);
                    usedItems.add(enteredItemName);
                    //Toast.makeText(this, Integer.toString(CurrentState.getInstance().usedItemsNames.size()), Toast.LENGTH_SHORT).show();
               }
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

    /**
     * Creating a new Item with entered by the user name and adding it to the itemList in current shopping list
     * @param enteredName string that represents new item name typed by the user
    * */
    public void addItemToList(String enteredName){
        Item newItem = new Item(enteredName);
        shoppingList.itemList.add(0, newItem);
        itemsInListAdapter.notifyDataSetChanged();

    }

    /**
     * Showing google speech input dialog
     * */
    private void showPromptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", new String[]{"he", "en-US"} );
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Receiving speech input. There is space in switch for turning back from other activities
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    enterItemName.setText(result.get(0));
                }
                break;
            }
            case REQUEST_CODE_EDIT_ITEM:{
                if (resultCode == RESULT_OK){
                    Toast.makeText(this, "Result_ok", Toast.LENGTH_SHORT).show();
                    itemsInListAdapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(this, "Result_canceled", Toast.LENGTH_SHORT).show();
                }
                break;
            }

        }
    }

    public void createActionMode(){
        actionModeCallback = new ActionMode.Callback() {
            //methods of ActionMode.Callback interface
            // Called when the action mode is created; startActionMode() was called
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate a menu resource providing context menu items
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_item, menu);
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
                int positionInList = itemsInListAdapter.getPositionOfSelectedItem();
                boolean isSelectedItemInItemList;
                int positionInSectionList;
                if (positionInList < shoppingList.itemList.size()){
                    isSelectedItemInItemList = true;
                    positionInSectionList = positionInList;
                }
                else{
                    isSelectedItemInItemList = false;
                    positionInSectionList = positionInList - shoppingList.itemList.size() - 2;
                }
                switch (item.getItemId()) {
                    case R.id.edit:
                        editSelectedItem(isSelectedItemInItemList,positionInSectionList);
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    case R.id.delete:
                        deleteSelectedItem(isSelectedItemInItemList,positionInSectionList);
                        mode.finish();
                        return true;
                    case R.id.move:
                        moveSelectedItem(isSelectedItemInItemList,positionInSectionList);
                        mode.finish();
                        return true;
                    case R.id.copy:
                        copySelectedItem(isSelectedItemInItemList,positionInSectionList);
                        mode.finish();
                        return  true;
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

    /**
     * Deletes selected item
     * */
    public void deleteSelectedItem(boolean isSelectedItemInItemList, int positionInSectionList){
        if (isSelectedItemInItemList){
            shoppingList.itemList.remove(positionInSectionList);
        }
        else{
            shoppingList.inCart.remove(positionInSectionList);
        }
        itemsInListAdapter.notifyDataSetChanged();
    }

    public void editSelectedItem(boolean isSelectedItemInItemList, int positionInSectionList){
        Intent intent = new Intent(this, EditItemActivity.class);
        intent.putExtra("shoppingListIndexInListList", shoppingListIndexInListList);
        intent.putExtra("isSelectedItemInItemList", isSelectedItemInItemList);
        intent.putExtra("positionInSectionList", positionInSectionList);
        startActivityForResult(intent, REQUEST_CODE_EDIT_ITEM);
    }

    public void copySelectedItem(boolean isSelectedItemInItemList, int positionInSectionList){

    }

    public void moveSelectedItem(boolean isSelectedItemInItemList, int positionInSectionList){

    }
}
