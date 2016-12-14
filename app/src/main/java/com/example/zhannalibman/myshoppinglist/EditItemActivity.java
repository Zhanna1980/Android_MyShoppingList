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

    private String itemImageFilePath;


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
        itemPosition = (ItemPosition)getIntent().getSerializableExtra("itemPosition");
        int shoppingListIndexInListList = itemPosition.shoppingListIndexInListList;
        boolean isSelectedItemInItemList = itemPosition.isSelectedItemInItemList;
        int positionInSectionList = itemPosition.positionInSectionList;

        if (savedInstanceState != null){
            Log.d("Zhanna", "restoring state");
            shoppingListIndexInListList = savedInstanceState.getInt("shoppingListIndexInListList");
            isSelectedItemInItemList = savedInstanceState.getBoolean("isSelectedItemInItemList");
            positionInSectionList = savedInstanceState.getInt("positionInSectionList");
            itemPosition = new ItemPosition(shoppingListIndexInListList,
                    isSelectedItemInItemList, positionInSectionList);
            itemImageFilePath = savedInstanceState.getString("itemImageFilePath");
        }


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

    /**
     * Fills the data of the edited item into views of the activity
     * */
    private void fillData(){
        editItemName.setText(editedItem.getName());
        enterQuantity.setText(editedItem.itemQuantityToString());
        enterUnits.setText(editedItem.getUnit());
        enterCategory.setText(editedItem.getCategory() != null ? editedItem.getCategory() : getString(R.string.none));
        if (editedItem.getNotes() != null) {
            enterNotes.setText(editedItem.getNotes());
        }
        //setImageFromFile();
        if (itemImageFilePath != null && !itemImageFilePath.isEmpty()){
            setItemImageToImageView(itemImageFilePath);
        }
        else if(editedItem.getItemImageFilePath() != null && !editedItem.getItemImageFilePath().isEmpty()){
            itemImageFilePath = editedItem.getItemImageFilePath();
            setItemImageToImageView(itemImageFilePath);
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
     * Picking all the entered data after button Done has been clicked and saving it into the item's properties
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
        if (itemImageFilePath != null && !itemImageFilePath.isEmpty()){
            editedItem.setItemImageFilePath(itemImageFilePath);
        }
    }

    /**
     * Capturing photo
     * */
    public void onBtnTakePhotoClick(View view) {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                String itemImageFileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) +".jpg";
                File itemImageFile = new File(Environment.getExternalStorageDirectory(),
                        itemImageFileName);
                Uri outputFileUri = Uri.fromFile(itemImageFile);
                itemImageFilePath = outputFileUri.getPath().toString();
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
            setItemImageToImageView(itemImageFilePath);
        }
    }

    private void setItemImageToImageView(String itemImageFilePath){
        if (itemImageFilePath != null && !itemImageFilePath.isEmpty()){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10;
            Bitmap bitmap = BitmapFactory.decodeFile(itemImageFilePath, options);
            itemPhoto.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("itemImageFilePath", itemImageFilePath);
        savedInstanceState.putInt("shoppingListIndexInListList", itemPosition.shoppingListIndexInListList);
        savedInstanceState.putBoolean("isSelectedItemInItemList", itemPosition.isSelectedItemInItemList);
        savedInstanceState.putInt("positionInSectionList", itemPosition.positionInSectionList);
        super.onSaveInstanceState(savedInstanceState);
    }
}
