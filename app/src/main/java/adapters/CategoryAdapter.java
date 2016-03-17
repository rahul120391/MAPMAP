package adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapmap.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import commonUtils.Utils;
import datamodel.HeaderModel;

/**
 * Created by osx on 26/10/15.
 */
public class CategoryAdapter extends BaseAdapter {

    Context context;
    ArrayList<HeaderModel> list;
    LayoutInflater inflater;
    ImageView iv_category_image;
    TextView tv_category;
    Typeface typeface;
    public CategoryAdapter(Context context,ArrayList<HeaderModel> list){
        this.context=context;
        this.list=list;
        inflater=LayoutInflater.from(context);
        typeface= Utils.SetFont(context, "ROBOTO-ITALIC.TTF");
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
            view=inflater.inflate(R.layout.categories_layout_row_item,viewGroup,false);
        }
        HeaderModel model=list.get(position);
        tv_category=(TextView)view.findViewById(R.id.tv_category);
        tv_category.setTypeface(typeface);
        tv_category.setText(model.getCategoryname());
        iv_category_image=(ImageView)view.findViewById(R.id.iv_category_image);
        Picasso.with(context).load(Uri.parse(model.getUrl())).into(iv_category_image);
        return view;
    }
}
