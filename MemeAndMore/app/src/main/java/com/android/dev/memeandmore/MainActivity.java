package com.android.dev.memeandmore;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends ActionBarActivity {

    private final static String TAG = "MainActivity";
    private final static String FILE_NAME = "TextImageInfo.txt";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_PHOTO_SELECT = 2;
    static final int REQUEST_TEXT_IMAGE_SAVE = 3;

    TextImageListAdapter mAdapter;
    String mCurrentPhotoPath;
    Uri mFileUri = null;
    Bitmap cameraBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // initialize the mAdapter
        mAdapter = new TextImageListAdapter(getApplicationContext());


        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(mAdapter);

        // Set the listeners for the listView items.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextImageItem item = (TextImageItem) mAdapter.getItem(position);
                Log.i(TAG, "You clicked on the item:" + item.getDate());
                // Getting the bitmap image path.
                Uri imageUri = item.getImageUri();

                // Open the image in android's default image viewer instead.
                Intent displayIntent = new Intent();
                displayIntent.setAction(Intent.ACTION_VIEW);
                displayIntent.setDataAndType(imageUri, "image/*");
                startActivity(displayIntent);

                /*
                Intent displayIntent = new Intent(MainActivity.this, TextImageEditActivity.class);
                displayIntent.putExtra("imageUri", imageUri);
                startActivity(displayIntent); */

            }
        });

        TextView tvMessage = (TextView) findViewById(R.id.textView);
        try {
            long test= listView.getItemIdAtPosition(0);
            Log.i(TAG, "Testing " + test);
            tvMessage.setVisibility(View.INVISIBLE);
        }
        catch (Exception e) {
            tvMessage.setVisibility(View.VISIBLE);
        }

        /*AlarmService alarmService = new AlarmService(getApplicationContext());
        alarmService.setRepeatingAlarm();*/

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "Calling onResume()");
        if (mAdapter.getCount() == 0)
            loadItems();

    }

    @Override
    protected void onPause() {

        super.onPause();
        Log.i(TAG, "Calling onPause()");
        // Save the items.
        saveItems();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}
        //else
        /*if (id == R.id.action_camera) {
            Log.i(TAG, "We now launch the camera app to take a new photo");
            // Create an intent to take a picture.
            //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            dispatchTakePictureIntent();
        }
        else */
        if (id == R.id.action_browse) {
            Log.i(TAG, "We now browse for images to edit.");
            dispatchImageBrowseIntent();
        }

        return super.onOptionsItemSelected(item);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir + "/" + imageFileName + ".jpg");
        image.createNewFile();
        /*File image = File.createNewFile(
                imageFileName,  /* prefix */
        // ".jpg",         /* suffix */
        // storageDir      /* directory */
        //);/

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private File createTemporaryFile(String part, String ext) throws Exception
    {
        File tempDir= Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath()+"/.temp/");
        if(!tempDir.exists())
        {
            tempDir.mkdir();
        }
        return File.createTempFile(part, ext, tempDir);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        //if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
        Log.i(TAG, "Able to resolve the activity");
        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = this.createTemporaryFile("picture", "jpeg");
            Log.i(TAG, "Created the image file");
        } catch (Exception ex) {
            // Error occurred while creating the File
            Log.i(TAG, "An exception occured while trying to create a file " + ex.toString());

        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Log.i(TAG, "Printing out the photoFile " + photoFile);
            mFileUri = Uri.fromFile(photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    mFileUri);

            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
        //}
    }

    public void grabAndSaveImage(ImageView imageView)
    {
        this.getContentResolver().notifyChange(mFileUri, null);
        ContentResolver cr = this.getContentResolver();
        Bitmap bitmap;
        try
        {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mFileUri);
            imageView.setImageBitmap(bitmap);
            // create a image.

        }
        catch (Exception e)
        {
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Failed to load", e);
        }
    }

    private void dispatchImageBrowseIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_PHOTO_SELECT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            File f = new File(mFileUri.getPath());
            if (f.exists()) {
                Log.i(TAG, "The given photo " + mFileUri.getPath() + " exists.");
            }
            else {
                Log.i(TAG, "The given photo does not exist.");
            }

            Uri imageUri = mFileUri;
            // Open the other text image create activity
            openImageForEditing(mFileUri);

        }

        else if (requestCode == REQUEST_PHOTO_SELECT && resultCode == RESULT_OK) {
            // Existing image selected from the gallery.
            try {
                Uri selectedImageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                Uri openImageUri = getImageUri(getApplicationContext(), selectedImage);
                String openImagePath = getRealPathFromURI(openImageUri);

                Log.i(TAG, "Opening the following image for editing: " + openImagePath);
                // Open the text image create activity.
                openImageForEditing(Uri.parse("file://" + openImagePath));

            }
            catch (IOException e) {
                Log.e(TAG, "An exception occured while trying to select an image: " + e.toString());

            }
        }
        else if (requestCode ==  REQUEST_TEXT_IMAGE_SAVE && resultCode == RESULT_OK) {
            // Add the image to the listView. TODO
            Uri imageUri = TextImageCreateActivity.finalUri;
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_camera);
                Date date = new Date();
                TextImageItem textImageItem = new TextImageItem(date, bitmap, imageUri);
                mAdapter.add(textImageItem);
            }
            catch (Exception e) {
                Log.e(TAG, "Could not get the bitmap thumbnail from the imageUri" + e.toString());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void openImageForEditing(Uri imageUri) {

        Intent editIntent = new Intent(MainActivity.this, TextImageCreateActivity.class);
        editIntent.putExtra("imageUri", imageUri);
        startActivityForResult(editIntent, REQUEST_TEXT_IMAGE_SAVE);

    }

    // Util functions to get the real image path.
    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", "desc");
        return Uri.parse(path);
    }

    // Util function to get the real path from the uri.
    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    // Load the stored TextImageListItems
    private void loadItems() {
        BufferedReader reader = null;

        try {
            FileInputStream fis = openFileInput(FILE_NAME);
            reader = new BufferedReader(new InputStreamReader(fis));

            String imageUriPath = null;
            Date date = null;

            while ((imageUriPath = reader.readLine()) != null) {
                date = TextImageItem.FORMAT.parse(reader.readLine());
                Uri uri = Uri.parse(new File("file://" + imageUriPath).toString());
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                TextImageItem textImageItem = new TextImageItem(date, bitmap, uri);
                mAdapter.add(textImageItem);
            }

        }
        catch (FileNotFoundException e) {
            Log.e(TAG, "Could not find the required file " + e.toString());
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.e(TAG, "An IOException occured." + e.toString());
            e.printStackTrace();
        }
        catch (ParseException e) {
            Log.e(TAG, "A parse exception occured. " + e.toString());
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // Save the TextImageListItems to a file.
    private void saveItems() {
        PrintWriter writer = null;
        try {
            FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(fos)));

            for (int i = 0; i < mAdapter.getCount(); i++) {
                writer.println(mAdapter.getItem(i));
                Log.i(TAG, "Printing out the item : " + mAdapter.getItem(i).toString());
            }
        }
        catch( IOException e) {
            Log.i(TAG, "An IOException occured while trying to saveItems");
            e.printStackTrace();
        }
        finally {
            if (writer != null)
                writer.close();

        }
    }
}
