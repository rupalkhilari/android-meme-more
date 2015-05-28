package com.android.dev.memeandmore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TextImageListAdapter extends BaseAdapter {
    private final List<TextImageItem> mItems = new ArrayList<TextImageItem>();

    private final Context mContext;
    private static final String TAG = "TextImageListAdapter";

    public TextImageListAdapter(Context context) {
        mContext = context;
    }

    // Add a TextImageItem to the adapter.
    // Notify observers that the data set has changed.

    public void add(TextImageItem item) {
        mItems.add(item);
        notifyDataSetChanged();
    }

    // Clear the list adapter of all the items.
    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }
    // Return the number of TextImageListItems

    @Override
    public int getCount() {
        return mItems.size();
    }

    // Retrieve the number of TextImageItems
    @Override
    public Object getItem(int pos) {
        return mItems.get(pos);
    }

    // Get the ID for the TextImageListItem
    // TODO : check if we need this
    @Override
    public long getItemId(int pos) {
        return pos;
    }

    // Create a View for the TextImageItem at the specified position.

    public View getView(int position, View convertView, ViewGroup parent) {


        // Get the current TextImageListItem

        final TextImageItem textImageItem = (TextImageItem) getItem(position);

        // Inflate the View for the TextImageListItem from the xml file.
        if ( convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.text_image_list_item, parent, false);

        }
        else {
            convertView.invalidate();
        }

        // Fill in the specific item.
        TextView tvDate = (TextView) convertView.findViewById(R.id.textView_date);
        tvDate.setText(TextImageItem.LISTVIEW_DATE_VIEW_FORMAT.format(textImageItem.getDate()));

        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView_thumbnail);
        imageView.setImageBitmap(textImageItem.getThumbnail());

        return convertView;

    }
}
