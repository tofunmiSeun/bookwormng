package com.bookwormng.android.Operations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bookwormng.android.R;

/**
 * Created by Tofunmi Seun on 15/09/2015.
 */
public class HomeArrayAdapter extends BaseAdapter {
    Context myContext;
    String [] myItems;
    String [] mylabels;
    static LayoutInflater myInflater;
    public HomeArrayAdapter(Context context, String [] items, String [] labels)
    {
     myContext = context;
        mylabels = labels;
        myItems = items;
     myInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public int getCount() {
        return myItems.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null)
        {
            view = myInflater.inflate(R.layout.home_list_item,null);
        }

        TextView t1 = (TextView) view.findViewById(R.id.textView4);
        TextView t2 = (TextView)view.findViewById(R.id.textView5);

        t1.setText(mylabels[position]);
        t2.setText(myItems[position]);
        return view;
    }
}
