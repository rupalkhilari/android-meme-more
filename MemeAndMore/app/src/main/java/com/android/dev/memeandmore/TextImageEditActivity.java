package com.android.dev.memeandmore;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.android.dev.memeandmore.colorpicker.ColorPickerDialog;

import java.util.ArrayList;


public class TextImageEditActivity extends ActionBarActivity implements ColorPickerDialog.OnColorChangedListener {

    public static final String TAG = "TextImageEditActivity";

    ImageView mImageView;
    String mCurrentPhotoPath;
    int mImageViewWidth = 0;
    int mImageViewHeight = 0;
    public static TextView textViewContent;
    Button btnChangeColor;
    Button btnEditDone;
    NumberPicker numberPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_text_image_edit);

        // For the text content
        textViewContent = (TextView) findViewById(R.id.txt_content);

        // for the change button
        btnChangeColor = (Button) findViewById(R.id.btn_color_picker);

        final ColorPickerDialog color = new ColorPickerDialog(this, this, "picker", Color.BLACK,Color.WHITE);
        btnChangeColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color.show();
            }
        });

        // for the font type spinner.
        Spinner typeSpinner = (Spinner) findViewById(R.id.spinner_font_type);
        ArrayList<String> typeList = new ArrayList<>();
        typeList.add("Normal");
        typeList.add("Bold");
        typeList.add("Italic");
        typeList.add("Bold-Italic");
        ArrayAdapter<String> arrayAdapterType = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, typeList);
        arrayAdapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(arrayAdapterType);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "Selected the view at the position " + position + " for the id " + id);
                switch ((int) id) {
                    case 0: // for normal
                        textViewContent.setTypeface(textViewContent.getTypeface(), Typeface.NORMAL);
                        break;
                    case 1: // for bold
                        textViewContent.setTypeface(textViewContent.getTypeface(), Typeface.BOLD);
                        break;
                    case 2: // for italic
                        textViewContent.setTypeface(textViewContent.getTypeface(), Typeface.ITALIC);
                        break;
                    case 3: // for bold-italic
                        textViewContent.setTypeface(textViewContent.getTypeface(), Typeface.BOLD_ITALIC);
                        break;
                    default:
                        Log.i(TAG, "Invalid font type");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(TAG, "Nothing selected now");
            }
        });

        // Adding the spinner for font face.
        Spinner faceSpinner = (Spinner) findViewById(R.id.spinner_font_face);
        ArrayList<String> faceList = new ArrayList<>();

        faceList.add("Default");
        faceList.add("Monospace");
        faceList.add("San-Serif");
        faceList.add("Serif");
        ArrayAdapter<String> arrayAdapterFace = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, faceList);
        arrayAdapterFace.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        faceSpinner.setAdapter(arrayAdapterFace);

        faceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch((int)id) {
                    case 0:
                        textViewContent.setTypeface(Typeface.DEFAULT);
                        break;
                    case 1:
                        textViewContent.setTypeface(Typeface.MONOSPACE);
                        break;
                    case 2:
                        textViewContent.setTypeface(Typeface.SANS_SERIF);
                        break;
                    case 3:
                        textViewContent.setTypeface(Typeface.SERIF);
                        break;
                    default:
                        Log.e(TAG, "Invalid font face.");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // The numberPicker.
        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);

        textViewContent.setTextSize(20.0f);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(30);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setValue(20);
        numberPicker.setScrollBarSize(10);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                textViewContent.setTextSize((float) newVal);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_text_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_create_done) {
            setResult(RESULT_OK);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void colorChanged(String key, int color) {
        // TODO Auto-generated method stub
        Log.i(TAG, "The color is set to " + color);
        btnChangeColor.setBackgroundColor(color);
        textViewContent.setTextColor(color);
    }
}
