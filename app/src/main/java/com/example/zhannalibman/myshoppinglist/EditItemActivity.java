package com.example.zhannalibman.myshoppinglist;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

public class EditItemActivity extends AppCompatActivity {

    private EditText editItemName;
    private EditText enterQuantity;
    private AutoCompleteTextView enterUnits;
    private EditText enterCategory;
    private EditText enterNotes;
    private ActionBar actionBar;

    private Item editedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        editItemName = (EditText)findViewById(R.id.editItemName);
        enterQuantity = (EditText)findViewById(R.id.enterQuantity);
        enterUnits = (AutoCompleteTextView)findViewById(R.id.enterUnits);
        enterCategory = (EditText)findViewById(R.id.enterCategory);
        enterNotes = (EditText)findViewById(R.id.enterNotes);

        // Get a support ActionBar corresponding to this toolbar
        actionBar = getSupportActionBar();
        // Enable the Up button
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Receiving intent
        int shoppingListIndexInListList = getIntent().getIntExtra("shoppingListIndexInListList", -1);
        boolean isSelectedItemInItemList = getIntent().getBooleanExtra("isSelectedItemInItemList", false);
        int positionInSectionList = getIntent().getIntExtra("positionInSectionList", -1);
        if (shoppingListIndexInListList > -1 && shoppingListIndexInListList < CurrentState.getInstance().listList.size()
                && positionInSectionList > -1) {
            ShoppingList shoppingList = CurrentState.getInstance().listList.get(shoppingListIndexInListList);
            if (isSelectedItemInItemList && positionInSectionList < shoppingList.itemList.size()){
                editedItem = shoppingList.itemList.get(positionInSectionList);
            }
            else if (!isSelectedItemInItemList && positionInSectionList < shoppingList.inCart.size()){
                editedItem = shoppingList.inCart.get(positionInSectionList);
            }
            else{
                handleIntentError();
            }
        }
        else{
            handleIntentError();
        }

        fillData();



    }

    /**
     * handle errors in passed through intent data
     * */
    private void handleIntentError(){
        Toast.makeText(this, this.getString(R.string.error), Toast.LENGTH_SHORT).show();
        finish();
    }

    private void fillData(){
        editItemName.setText(editedItem.getName());
        enterQuantity.setText(editedItem.itemQuantityToString());
        enterUnits.setText(editedItem.getUnit());
        enterCategory.setText(editedItem.getCategory() != null ? editedItem.getCategory() : getString(R.string.none));
        if (editedItem.getNotes() != null) {
            enterNotes.setText(editedItem.getNotes());
        }
    }
}
