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

    ShoppingList shoppingList;
    ActionBar actionBar;
    ActionMode.Callback actionModeCallback;
    ActionMode actionMode;

    AutoCompleteTextView enterItemName;
    ListView itemsList;

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


        Bundle bundle = getIntent().getExtras();
        shoppingList = (ShoppingList)bundle.getSerializable("shoppingList");

        //autocomplete
        usedItems = CurrentState.getInstance().usedItemsNames;
        autoCompleteUnitsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, usedItems);
        enterItemName.setAdapter(autoCompleteUnitsAdapter);
        enterItemName.setThreshold(1);

        // Get a support ActionBar corresponding to this toolbar
        actionBar = getSupportActionBar();
        // Enable the Up button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(shoppingList.getName());

        itemsInListAdapter = new ItemsInListAdapter(this, shoppingList.itemList);
        itemsList.setAdapter(itemsInListAdapter);
        createActionMode();
    }


    public static void startWithListForResult(Activity fromActivity, ShoppingList shoppingList, int requestCode){
        Intent intent = new Intent(fromActivity, ListActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable("shoppingList", shoppingList);
        intent.putExtras(extras);
        fromActivity.startActivityForResult(intent, requestCode);
    }
    //add button was clicked
    public void onClickAddItem(View view) {
        addItemToListIfDoesNotExist();
    }

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
                switch (item.getItemId()) {
                    case R.id.edit:
                        mode.finish();
                        return true;
                    case R.id.delete:
                        mode.finish();
                        return true;
                    case R.id.move:
                        //shareSelectedList();
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    case R.id.copy:
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
}
