package adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mapmap.R;
import com.parse.ParseObject;

import java.util.List;

import commonUtils.Utils;
import datamodel.LocationInfo;

/**
 * Created by osx on 03/11/15.
 */
public class LocationsAdapter extends BaseAdapter {

    List<LocationInfo> list;
    Context context;
    TextView tv_locations;
    LayoutInflater inf;
    Typeface typeface;
    public LocationsAdapter(Context context,List<LocationInfo> list){
        this.context=context;
        inf=LayoutInflater.from(context);
        this.list=list;
        typeface= Utils.SetFont(context, "ROBOTO-REGULAR.TTF");
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(view==null){
            view=inf.inflate(R.layout.locationsnames_row_item,null);
        }
        tv_locations=(TextView)view.findViewById(R.id.tv_location);
        tv_locations.setTypeface(typeface);
        tv_locations.setText(list.get(position).getLocation());
        return view;
    }
}
