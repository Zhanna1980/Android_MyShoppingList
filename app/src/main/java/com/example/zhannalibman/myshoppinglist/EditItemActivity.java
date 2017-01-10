package com.example.zhannalibman.myshoppinglist;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class EditItemActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 301;
    static final int REQUEST_PICK_IMAGE = 302;
    private final int REQUEST_EXTERNAL_STORAGE = 303;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

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
    private boolean shouldTakePhoto = false;
    private ArrayList<String> units = new ArrayList<>();
    private ArrayList<String> predefinedUnits = new ArrayList<>();
    private ArrayAdapter<String> autoCompleteUnitsAdapter;


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

        //Set adapter to AutoCompleteTextView enterUnits
        predefinedUnits.addAll(Arrays.asList(getResources().getStringArray(R.array.predefinedUnits)));
        units.addAll(CurrentState.getInstance().usersUnits);
        units.addAll(predefinedUnits);
        autoCompleteUnitsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, units);
        enterUnits.setAdapter(autoCompleteUnitsAdapter);
        enterUnits.setThreshold(1);

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
            shouldTakePhoto = savedInstanceState.getBoolean("shouldTakePhoto");
        }

        editedItem = itemPosition.getItem();
        if (editedItem == null){
            handleError();
        }
        else{
            //Fills the data into the activity's views after their demensions are known (after completion the layout)
            itemPhoto.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    fillData();
                }
            });
        }
    }

    /**
     * handles errors in passed data
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
        if (!changedQuantity.isEmpty()) {
            float quantity = 0f;
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
            nf.setMaximumFractionDigits(2);
            try {
                quantity = nf.parse(changedQuantity).floatValue();
            } catch (ParseException e) {
                Toast.makeText(this, getString(R.string.parse_quantity_error), Toast.LENGTH_SHORT).show();
            }
            editedItem.setQuantity(quantity);
        }
        if (!changedUnits.isEmpty()) {
            String newUnit = changedUnits.toLowerCase();
            if (!predefinedUnits.contains(newUnit) && !CurrentState.getInstance().usersUnits.contains(newUnit)){
                CurrentState.getInstance().usersUnits.add(newUnit);
            }
            editedItem.setUnit(newUnit);
        }
        editedItem.setCategory(changedCategory);
        editedItem.setNotes(notes);
        editedItem.setItemImageFilePath(itemImageFilePath);
    }

    /**
     * Verifying Storage permission for devices with API 23+
     * */
    public void onBtnTakePhotoClick(View view) {
        shouldTakePhoto = true;
        verifyStoragePermissions(shouldTakePhoto);

    }

    private void takePhoto(){
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                String itemImageFileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) +".jpg";
                File itemImageFile = new File(Environment.getExternalStorageDirectory(),
                        itemImageFileName);
//                File itemImageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                        itemImageFileName);
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
        else if(requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri URI = data.getData();
            String[] FILE = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(URI,
                    FILE, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(FILE[0]);
            itemImageFilePath = cursor.getString(columnIndex);

            setItemImageToImageView(itemImageFilePath);
            cursor.close();
        }
    }

    private void setItemImageToImageView(String itemImageFilePath){
        if (itemImageFilePath != null && !itemImageFilePath.isEmpty()) {
            // Get the dimensions of the imageView
            int itemImageWidth = itemPhoto.getWidth();
            int itemImageHeight = itemPhoto.getHeight();
            // Get the dimensions of the bitmap
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(itemImageFilePath, options);
                    int photoWidth = options.outWidth;
                    int photoHeight = options.outHeight;
                    // Determine how much to scale down the image
                    int scaleFactor = Math.min(photoWidth/itemImageWidth, photoHeight/itemImageHeight);
                    // Decode the image file into a Bitmap sized to fill the View
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = scaleFactor;
                    Log.d("Zhanna", String.valueOf(scaleFactor));
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
        savedInstanceState.putBoolean("shouldTakePhoto", shouldTakePhoto);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    public void verifyStoragePermissions(boolean shouldTakePhoto) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        else{
            if(shouldTakePhoto) {
                takePhoto();
            }
            else{
                pickPhoto();
            }
        }
    }


    /**
     * If user gives his permission for writing external storage he proceeds to taking/picking photo.
     * Otherwise there will be an alertDialog shown that taking pictures needs permission to write to external storage
     * */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (shouldTakePhoto == true) {
                    takePhoto();
                }
                else{
                    pickPhoto();
                }
            }
            else{
                showAlertDialog();
            }
        }
    }

    public void onBtnPickPhotoClick(View view) {
        shouldTakePhoto = false;
        verifyStoragePermissions(shouldTakePhoto);
    }

    public void pickPhoto(){
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        // Start the Intent
        startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE);
    }

    public void onBtnDeletePhotoClick(View view) {
        itemPhoto.setImageBitmap(null);
        itemImageFilePath = null;
    }

    public void showAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setMessage(getString(R.string.storage_permission_denied));
        alertDialogBuilder.setPositiveButton(getString(R.string.ok), null);
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }
}
