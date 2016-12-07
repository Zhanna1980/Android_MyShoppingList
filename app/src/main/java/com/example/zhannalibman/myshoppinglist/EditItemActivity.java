package com.example.zhannalibman.myshoppinglist;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Float.parseFloat;

public class EditItemActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 301;

    private EditText editItemName;
    private EditText enterQuantity;
    private AutoCompleteTextView enterUnits;
    private EditText enterCategory;
    private EditText enterNotes;
    private ImageView itemPhoto;
    private ActionBar actionBar;

    private Item editedItem;
    private ItemPosition itemPosition;
    private Uri outputFileUri;
    private File itemImageFile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        Log.d("Zhanna", "onCreate");


        editItemName = (EditText)findViewById(R.id.editItemName);
        enterQuantity = (EditText)findViewById(R.id.enterQuantity);
        enterUnits = (AutoCompleteTextView)findViewById(R.id.enterUnits);
        enterCategory = (EditText)findViewById(R.id.enterCategory);
        enterNotes = (EditText)findViewById(R.id.enterNotes);
        itemPhoto = (ImageView)findViewById(R.id.itemPhoto);

        // Get a support ActionBar corresponding to this toolbar
        actionBar = getSupportActionBar();
        // Enable the Up button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");



        //Receiving intent
        int shoppingListIndexInListList = getIntent().getIntExtra("shoppingListIndexInListList", -1);
        boolean isSelectedItemInItemList = getIntent().getBooleanExtra("isSelectedItemInItemList", false);
        int positionInSectionList = getIntent().getIntExtra("positionInSectionList", -1);

        if (savedInstanceState != null){
            Log.d("Zhanna", "restoring state");
            shoppingListIndexInListList = savedInstanceState.getInt("shoppingListIndexInListList");
            isSelectedItemInItemList = savedInstanceState.getBoolean("isSelectedItemInItemList");
            positionInSectionList = savedInstanceState.getInt("positionInSectionList");
//            outputFileUri = Uri.parse(savedInstanceState.getString("outputFileUri"));
            String itemImageFileAsString = savedInstanceState.getString("itemImageFile");
            if (itemImageFileAsString != null) {
                itemImageFile = new File(itemImageFileAsString);
            }
        }

        itemPosition = new ItemPosition(shoppingListIndexInListList,
                isSelectedItemInItemList, positionInSectionList);
        editedItem = itemPosition.getItem();
        if (editedItem != null){
            Log.d("Zhanna", editedItem.getName());
            fillData();
        }
        else{
           handleError();
        }
    }

    /**
     * handle errors in passed data
     * */
    private void handleError(){
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
        if (itemImageFile != null && itemImageFile.exists()){
            Log.d("Zhanna", "Fill data photo");
            File externalStorageDirectory = Environment.getExternalStorageDirectory();
            File output = new File(externalStorageDirectory, itemImageFile.getName());
            Bitmap bitmap = BitmapFactory.decodeFile(output.toString());
            itemPhoto.setImageBitmap(bitmap);
        }
    }

    /**
     *Adding Done button to ActionBar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_item, menu);
        return true;
    }

    /**
     * Responding on ActionBar options click
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Up button is clicked
            case android.R.id.home:
                Intent upIntent = new Intent();
                setResult(RESULT_CANCELED, upIntent);
                finish();
                return true;
            //Button Done is clicked
            case R.id.done:
                saveDataAfterEditing();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Picking all the entered data
     * */
    private void saveDataAfterEditing(){
        String changedName = editItemName.getText().toString();
        String changedQuantity = enterQuantity.getText().toString();
        String changedUnits = enterUnits.getText().toString();
        String changedCategory = enterCategory.getText().toString();
        String notes = enterNotes.getText().toString();
        if (!changedName.isEmpty()){
            editedItem.setName(changedName);
        }
        else{
            Toast.makeText(this, getString(R.string.enterItemName_hint), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!changedQuantity.isEmpty()){
            editedItem.setQuantity(parseFloat(changedQuantity));
        }
        if (!changedUnits.isEmpty()){
            editedItem.setUnit(changedUnits);
        }
        if (!changedCategory.isEmpty()){
            editedItem.setCategory(changedCategory);
        }
        if (!notes.isEmpty()){
            editedItem.setNotes(notes);
        }
    }

    public void onBtnTakePhotoClick(View view) {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                itemImageFile = new File(Environment.getExternalStorageDirectory(),
                        (new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())) +".jpg");
                outputFileUri = Uri.fromFile(itemImageFile);
                Log.d("Zhanna", "file saved in " + outputFileUri.toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
            else {
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
            }

        }
        else{
            Toast.makeText(this, getString(R.string.no_camera), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Log.d("Zhanna", "result ok");
                if (data != null) {
                    Log.d("Zhanna", "receiving data");
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    itemPhoto.setImageBitmap(imageBitmap);
                }
            if(itemImageFile.exists()) {
                Log.d("Zhanna", "file exists");
                File externalStorageDirectory = Environment.getExternalStorageDirectory();
                File output = new File(externalStorageDirectory, itemImageFile.getName());
                Bitmap bitmap = BitmapFactory.decodeFile(output.toString());
                itemPhoto.setImageBitmap(bitmap);
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //savedInstanceState.putString("outputFileUri", outputFileUri.toString());
        if (itemImageFile != null) {
            savedInstanceState.putString("itemImageFile", itemImageFile.toString());
        }
        savedInstanceState.putInt("shoppingListIndexInListList", itemPosition.shoppingListIndexInListList);
        savedInstanceState.putBoolean("isSelectedItemInItemList", itemPosition.isSelectedItemInItemList);
        savedInstanceState.putInt("positionInSectionList", itemPosition.positionInSectionList);
        super.onSaveInstanceState(savedInstanceState);

    }

    /**
     * Class for preserving the location and restoring of edited item
     * */
    private class ItemPosition {
        int shoppingListIndexInListList;
        boolean isSelectedItemInItemList;
        int positionInSectionList;

        ItemPosition(int shoppingListIndexInListList, boolean isSelectedItemInItemList, int positionInSectionList){
            this.shoppingListIndexInListList = shoppingListIndexInListList;
            this.isSelectedItemInItemList = isSelectedItemInItemList;
            this.positionInSectionList = positionInSectionList;
        }

        Item getItem(){
            if (shoppingListIndexInListList > -1 && shoppingListIndexInListList < CurrentState.getInstance().listList.size()
                    && positionInSectionList > -1) {
                Log.d("Zhanna", Integer.valueOf(shoppingListIndexInListList).toString() +
                        Boolean.valueOf(isSelectedItemInItemList).toString() + Integer.valueOf(positionInSectionList).toString());
                ShoppingList shoppingList = CurrentState.getInstance().listList.get(shoppingListIndexInListList);
                if (isSelectedItemInItemList && positionInSectionList < shoppingList.itemList.size()){
                    return shoppingList.itemList.get(positionInSectionList);
                }
                else if (!isSelectedItemInItemList && positionInSectionList < shoppingList.inCart.size()){
                    return shoppingList.inCart.get(positionInSectionList);
                }
                else{
                    return null;
                }
            }
            else{
                return null;
            }
        }
    }
}
