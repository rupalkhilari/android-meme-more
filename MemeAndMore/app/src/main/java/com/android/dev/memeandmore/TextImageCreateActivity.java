package com.android.dev.memeandmore;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TextImageCreateActivity extends ActionBarActivity {

    private static final String TAG = "TextImageCreateActivity";
    private static final int TEXT_EDIT_RESULT_CODE = 1;
    private String mCurrentPhotoPath;
    int mImageViewWidth, mImageViewHeight;
    static Uri finalUri = null;

    private RelativeLayout rLayout;
    private RelativeLayout.LayoutParams rlParams;
    private ImageView mImageView;
    private Bitmap targetBitmap;
    private Button btnDone;
    private float currentX, currentY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_image_display_view);

        // Retrieve the uri from the intent.
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        Uri imageUri = (Uri) data.get("imageUri");
        mCurrentPhotoPath = imageUri.getPath();
        Log.i(TAG, "The current photo path is " + mCurrentPhotoPath);

        rLayout = new RelativeLayout(this);
        rlParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT
                ,LayoutParams.MATCH_PARENT);
        rlParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        rLayout.setLayoutParams(rlParams);
        rlParams.bottomMargin = 80;
        mImageView = new ImageView(this);
        mImageView.setLayoutParams(rlParams);
        mImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // Ensure you call it only once :
                mImageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                // get the size
                mImageViewWidth = mImageView.getMeasuredWidth();
                mImageViewHeight = mImageView.getMeasuredHeight();
                setPic();
                // Set the width and height of the imageView.
                mImageView.getLayoutParams().height = targetBitmap.getHeight();
                mImageView.getLayoutParams().width = targetBitmap.getWidth();
                mImageView.setAdjustViewBounds(true);

            }
        });

        mImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i(TAG, "There are " + rLayout.getChildCount() + " children.");
                        for (int i = 0 ; i < rLayout.getChildCount(); i++) {
                            try {
                                View childView = rLayout.getChildAt(i);
                                if (isPointInsideView(event.getX(), event.getY(), childView)) {
                                    Log.i(TAG, "The given point is inside the view " + childView.toString());
                                }
                            }
                            catch (Exception e) {
                                Log.e(TAG, "An exception occured" + e.toString());
                            }
                        }

                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        //Add text at that position.
                        Log.i(TAG, "Drawing text at " + event.getX()  + " and " + event.getY());
                        currentX = event.getX();
                        currentY = event.getY();
                        Intent textInputIntent = new Intent(TextImageCreateActivity.this,
                            TextImageEditActivity.class);
                        startActivityForResult(textInputIntent, TEXT_EDIT_RESULT_CODE);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                    default:
                        break;
                }

                    // Open the dialog to create text at the requested spot.


                return true;
            }
        });

        rLayout.addView(mImageView);

        setContentView(rLayout);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_text_image_create, menu);
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
        if (id == R.id.action_image_save) {
            Log.i(TAG, "We are now ready to save the picture");
            // Save the image before closing this activity.
            // Get the dimensions of the imageView
            int height = targetBitmap.getHeight();
            int width = targetBitmap.getWidth();

            Bitmap bitmap1 = targetBitmap;
            Bitmap bitmap2 = null;
            Bitmap cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas comboImage = new Canvas(cs);
            comboImage.drawBitmap(bitmap1, 0.0f, 0.0f, null);
            for (int i = 0 ; i < rLayout.getChildCount(); i++) {
                try {
                    TextView childView = (TextView) rLayout.getChildAt(i);
                    childView.setBackgroundColor(Color.TRANSPARENT);
                    childView.buildDrawingCache();
                    bitmap2 = childView.getDrawingCache();
                    comboImage.drawBitmap(bitmap2, childView.getX(), childView.getY(), null);
                }
                catch (Exception e) {
                    Log.e(TAG, "An exception occured" + e.toString());
                }
            }

            if (bitmap2 != null) {
                writeFinalImage(cs);
                Log.i(TAG, "Wrote out the final image!" + mCurrentPhotoPath);
                Toast.makeText(getApplicationContext(), "Saved the image to : " + mCurrentPhotoPath, Toast.LENGTH_LONG).show();
                finalUri = Uri.parse("file://" + mCurrentPhotoPath);
                setResult(RESULT_OK);
            }
            else {
                Toast.makeText(getApplicationContext(), "Skipped saving - since no changes were made", Toast.LENGTH_LONG).show();
                setResult(RESULT_CANCELED);
            }

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void writeFinalImage(Bitmap comboBitmap) {
        FileOutputStream fos = null;
        // Save Bitmap to File
        try {
            fos = new FileOutputStream(createImageFile());
            comboBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();
            fos.close();
            fos = null;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (fos != null) {
                try {
                    fos.close();
                    fos = null;
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TEXT_EDIT_RESULT_CODE && resultCode == RESULT_OK) {
            TextView tv = drawText(currentX, currentY);
            tv.setTypeface(TextImageEditActivity.textViewContent.getTypeface());
            tv.setTextColor(TextImageEditActivity.textViewContent.getCurrentTextColor());
            tv.setText(TextImageEditActivity.textViewContent.getText());
            tv.setTextSize(TextImageEditActivity.textViewContent.getTextSize());
        }
    }

    public static boolean isPointInsideView(float x, float y, View view){

        Log.i(TAG, "Checking for view " + view.toString());

        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];
        Log.i(TAG, "viewX=" + viewX + " viewY=" + viewY);
        //point is inside view bounds
        if(( x > viewX && x < (viewX + view.getWidth())) &&
                ( y > viewY && y < (viewY + view.getHeight()))){
            return true;
        } else {
            return false;
        }
    }

    private TextView drawText(float touchX, float touchY) {
        RelativeLayout.LayoutParams tParams = new RelativeLayout.LayoutParams
                (LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        //tParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        tParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        TextView newText = new TextView(this);
        newText.setText("Some text");
        tParams.topMargin = (int) touchY;
        tParams.leftMargin = (int) touchX;

        newText.setTextColor(Color.RED);
        newText.setTypeface(Typeface.DEFAULT_BOLD);
        newText.setLayoutParams(tParams);

        rLayout.addView(newText);
        return newText;

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getPath() + "/MemeAndMore");

        storageDir.mkdirs();
        File image = new File(storageDir + "/" + imageFileName + ".jpg");
        image.createNewFile();
        /*File image = File.createNewFile(
                imageFileName,  /* prefix */
        // ".jpg",         /* suffix */
        // storageDir      /* directory */
        //);/

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        Log.i(TAG, "The image file is " + mCurrentPhotoPath);
        return image;
    }

    private void setPic() {
        // Get the dimensions of the View
        // target width of the view should be the same as that of the image.
        int targetW = mImageViewWidth;
        int targetH = mImageViewHeight;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        targetBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(targetBitmap);
    }
}
