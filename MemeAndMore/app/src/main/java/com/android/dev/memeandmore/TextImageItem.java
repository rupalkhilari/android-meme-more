package android.coursera.dailyselfie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by rupal on 24/4/15.
 */

public class TextImageItem {
    public final static String DATE = "date";
    public final static String THUMBNAIL = "thumbnail";
    public final static String PATH = "path";
    public final static String TAG = "SelfieListItem";
    public static final String ITEM_SEP = System.getProperty("line.separator");

    public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    public final static SimpleDateFormat LISTVIEW_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);

    private Date mDate = null;

    // Make sure this is initialized.
    private Bitmap mThumbnail = null;

    // Holds the path of the image.
    private Uri mImageUri = null;

    SelfieListItem(Date date, Bitmap thumbnail, Uri uri) {
        this.mThumbnail = thumbnail;
        this.mDate = date;
        this.mImageUri = uri;
    }

    // Create a new SelfieItem from the packaged intent.
    SelfieListItem(Intent data) {
        mDate = new Date();
        Bundle extras = data.getExtras();
        mThumbnail = (Bitmap) extras.get("data");
    }

    // Getter and setter methods
    public Date getDate() {
        return this.mDate;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    public Bitmap getThumbnail() {
        return this.mThumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.mThumbnail = thumbnail;
    }

    public Uri getImageUri() {
        return this.mImageUri;
    }

    public void setmImageUri(Uri uri) {
        this.mImageUri = uri;
    }

    public String toString() {
        return mImageUri.getPath() + ITEM_SEP + FORMAT.format(mDate);
    }

    public String toLog() {
        return "Date:" + FORMAT.format(mDate) + ITEM_SEP + "Path:" + mImageUri.getPath() + "\n";

    }


}
