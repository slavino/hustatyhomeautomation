package com.hustaty.homeautomation.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.hustaty.homeautomation.R;
import com.hustaty.homeautomation.model.StoredEventResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 31-Jul-14.
 */
public class HotWaterItemArrayAdapter extends ArrayAdapter<StoredEventResult> {

    private List<StoredEventResult> itemList;
    private Context context;

    public HotWaterItemArrayAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.itemList = new ArrayList<StoredEventResult>();
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    public List<StoredEventResult> getItemList() {
        return itemList;
    }

    public StoredEventResult getItem(int position) {
        if (itemList != null) {
            return itemList.get(position);
        }
        return null;
    }

    public void setItemList(List<StoredEventResult> itemList) {
        this.itemList = itemList;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    @Override
    public long getItemId(int position) {
        if (itemList != null
                && itemList.get(position) != null) {
            return itemList.get(position).hashCode();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.grid_item, null);
        }

        StoredEventResult c = itemList.get(position);
        TextView text = (TextView) v.findViewById(R.id.item_date_from);
        text.setText(c.getValidFrom());

        TextView text1 = (TextView) v.findViewById(R.id.item_date_until);
        text1.setText(c.getValidUntil());

        TextView text2 = (TextView) v.findViewById(R.id.item_status);
        text2.setText(c.getValueToPass());

        return v;
    }
}
