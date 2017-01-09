package com.example.zhannalibman.myshoppinglist;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Represents the list of all shopping lists.
 * */
public class MainActivity extends AppCompatActivity {
    private static final int DIALOG_LIST_EXISTS = 111;
    private static final int DIALOG_EDIT_LIST_NAME = 112;

    List<ShoppingList> listList = CurrentState.getInstance().listList;

    EditText enterListName;
    ListView listOfLists;
    ActionBar actionBar;
    ActionMode.Callback actionModeCallback;
    ActionMode actionMode;

    private ShoppingListsAdapter listArrayAdapter;

    private String enteredListName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enterListName = (EditText) findViewById(R.id.enterListName);
        listOfLists = (ListView)findViewById(R.id.listOfLists);
        enterListName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        listOfLists.setAdapter(listArrayAdapter);
        createActionMode();


    }

    /**
     * Button btnAddList was clicked
     * */
    public void onClickAddList(View view) {
        checkAndAdd();
    }

    public void checkAndAdd(){
        enteredListName = enterListName.getText().toString();
        if (enteredListName.isEmpty()) {
            enteredListName = getString(R.string.unnamed_list);
        }
        if (isAlreadyInTheList(enteredListName)) {
            showDialogs(DIALOG_LIST_EXISTS, -1);
        }
        else{
            addListToListAndGoToListActivity();
        }
    }


    public void addListToListAndGoToListActivity (){
            createNewList(enteredListName);
            //InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            enterListName.setText("");
            enterListName.setHint(getString(R.string.enterListName_hint));
            ListActivity.startListActivity(this, 0);

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

    /**
     * Shows dialog
     * @param dialogType is a constant which determines which dialog to show
     * @param positionInListList is the index of the selected list in listList.
     *                           If there is no specified list the positionInListList is -1;
     * */
    public void showDialogs(int dialogType, final int positionInListList){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setCancelable(true);
        if (dialogType == DIALOG_LIST_EXISTS){
            alertDialogBuilder.setTitle(getString(R.string.alert_add_existing_list));
            alertDialogBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addListToListAndGoToListActivity();
                }
            });
        }
        else if (dialogType == DIALOG_EDIT_LIST_NAME && positionInListList > -1){
            String listName = listList.get(positionInListList).getName();
            alertDialogBuilder.setTitle(getString(R.string.edit));
            final EditText editListName = new EditText(this);
            alertDialogBuilder.setView(editListName);
            editListName.setText(listName);
            editListName.setSelection(editListName.length());
            alertDialogBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String editedListName = editListName.getText().toString();
                    if (editedListName.isEmpty()){
                        editedListName = getString(R.string.unnamed_list);
                    }
                    listList.get(positionInListList).setName(editedListName);
                    listArrayAdapter.notifyDataSetChanged();
                }
            });
        }
        else{
            return;
        }

        alertDialogBuilder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterListName.requestFocus();
            }
        });

        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
        doKeepDialog(dialog);
    }


    /**
     * Prevents dialog dismiss when orientation changes
     * */
    private static void doKeepDialog(Dialog dialog){
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
    }

    /**
     * Creates action mode and determines its options
     * */
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
                        showDialogs (DIALOG_EDIT_LIST_NAME, listArrayAdapter.getPositionOfSelectedList());
                        mode.finish();
                        return true;
                    case R.id.delete:
                        deleteSelectedList (listArrayAdapter.getPositionOfSelectedList());
                        mode.finish();
                        return true;
                    case R.id.share:
                        shareSelectedList (listArrayAdapter.getPositionOfSelectedList());
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

    /**
     * Deletes selected list.
     * @param positionInListList index of the selected item in listList.
     * */
    public void deleteSelectedList (int positionInListList){
        listList.remove(positionInListList);
        listArrayAdapter.notifyDataSetChanged();
    }

    /**
     * Shares selected list.
     * @param positionInListList index of the selected item in listList.
     * */
    public void shareSelectedList (int positionInListList){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, listList.get(positionInListList).getName());
        intent.putExtra(Intent.EXTRA_TEXT, listList.get(positionInListList).getListFullDescription());
        //Verifies there is an app to receive the intent
        PackageManager packageManager = getPackageManager();
        List activities = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;
        if (isIntentSafe) {
            startActivity(Intent.createChooser(intent, getString(R.string.sharing_chooser_title) + " "
                    + listList.get(positionInListList).getName()));
        }
    }


}
