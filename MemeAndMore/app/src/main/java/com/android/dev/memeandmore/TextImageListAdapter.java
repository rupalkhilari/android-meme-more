package android.coursera.dailyselfie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rupal on 24/4/15.
 */
public class TextImageListAdapter extends BaseAdapter {
    private final List<SelfieListItem> mItems = new ArrayList<SelfieListItem>();

    private final Context mContext;
    private static final String TAG = "TextImageListAdapter";

    public TextImageListAdapter(Context context) {
        mContext = context;
    }

    // Add a TextImageItem to the adapter.
    // Notify observers that the data set has changed.

    public void add(SelfieListItem item) {
        mItems.add(item);
        notifyDataSetChanged();
    }

    // Clear the list adapter of all the items.
    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }
    // Return the number of SelfieListItems

    @Override
    public int getCount() {
        return mItems.size();
    }

    // Retrieve the number of SelfieItems
    @Override
    public Object getItem(int pos) {
        return mItems.get(pos);
    }

    // Get the ID for the SelfieListItem
    // TODO : check if we need this
    @Override
    public long getItemId(int pos) {
        return pos;
    }

    // Create a View for the SelfieItem at the specified position.

    public View getView(int position, View convertView, ViewGroup parent) {


        // Get the current SelfieListItem

        final SelfieListItem selfieListItem = (SelfieListItem) getItem(position);

        // Inflate the View for the SelfieListItem from the xml file.
        if ( convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.selfie_list_item, parent, false);

        }
        else {
            convertView.invalidate();
        }

        // Fill in the specific item.
        TextView tvDate = (TextView) convertView.findViewById(R.id.textView_date);
        tvDate.setText("  " + SelfieListItem.LISTVIEW_DATE_FORMAT.format(selfieListItem.getDate()));

        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView_thumbnail);
        imageView.setImageBitmap(selfieListItem.getThumbnail());

        return convertView;

    }
}
