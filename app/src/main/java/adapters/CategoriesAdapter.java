package adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapmap.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

import commonUtils.Utils;
import datamodel.ChildModel;
import datamodel.HeaderModel;
import fragments.CategoriesPop;

/**
 * Created by osx on 21/10/15.
 */
public class CategoriesAdapter extends BaseAdapter {

    Context context;
    List<HeaderModel> headerlist;
    LayoutInflater inflater;
    Map<HeaderModel,List<ChildModel>> childlist;
    ImageView img;
    TextView tv_category_name;
    TextView tv_sub_category_name;
    CheckBox ch_select_sub_category;
    Typeface typeface;
    HeaderModel model;

    public CategoriesAdapter(Context context, List<HeaderModel> headerlist, Map<HeaderModel, List<ChildModel>> childlist) {
        this.context=context;
        this.headerlist=headerlist;
        this.childlist=childlist;
        inflater=LayoutInflater.from(context);
        typeface= Utils.SetFont(context,"ROBOTO-ITALIC.TTF");
    }
/*
    @Override
    public int getGroupCount() {
        return headerlist.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childlist.get(headerlist.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return headerlist.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childlist.get(headerlist.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }*/

    @Override
    public int getCount() {
        return headerlist.size();
    }

    @Override
    public Object getItem(int i) {
        return headerlist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        model=headerlist.get(i);
        if(view==null){
            view=inflater.inflate(R.layout.layout_category_header_view,viewGroup,false);
        }

//        tv_category_name.setText(model.getCategoryname());
        img = (ImageView) view.findViewById(R.id.iv_category_image);
        //Log.e("Url",model.getUrl());
        if(model.getState().equals("Selected")) {
            model.setState("Selected");
            Picasso.with(context).load(Uri.parse(model.getUrl2())).into(img);
        } else {
            model=headerlist.get(i);
            model.setState("Unselect");
            Picasso.with(context).load(Uri.parse(model.getUrl())).into(img);
        }

  /*      img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model=headerlist.get(i);
                model.setState("Selected");
                Log.e("Item postition",""+i);
                CategoriesPop.post = i;
                Picasso.with(context).load(Uri.parse(model.getUrl2())).into(img);
            }
        });
*/
        return view;
    }

    /*@Override
    public View getGroupView(int groupPosition, boolean b, View view, ViewGroup viewGroup) {
        HeaderModel model=(HeaderModel)getGroup(groupPosition);
        if(view==null){
            view=inflater.inflate(R.layout.layout_category_header_view,viewGroup,false);
        }
        tv_category_name=(TextView)view.findViewById(R.id.tv_category_name);
        tv_category_name.setTypeface(typeface);
        iv_category_image=(ImageView)view.findViewById(R.id.iv_category_image);
        tv_category_name.setText(model.getCategoryname());
        if(model.getState().equals("Selected")) {
            model=(HeaderModel)getGroup(groupPosition);
            model.setState("Selected");
            Picasso.with(context).load(Uri.parse(model.getUrl2())).into(iv_category_image);
        } else {
            model=(HeaderModel)getGroup(groupPosition);
            model.setState("Unselect");
            Picasso.with(context).load(Uri.parse(model.getUrl())).into(iv_category_image);
        }
        return view;
    }

    @Override
    public View getChildView(final int groupPosition,int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildModel child=(ChildModel)getChild(groupPosition,childPosition);
        if(convertView==null){
            convertView=inflater.inflate(R.layout.layout_category_child_view,null);
        }
        tv_sub_category_name=(TextView)convertView.findViewById(R.id.tv_sub_category_name);
        tv_sub_category_name.setText(child.getSub_category_name());
        tv_sub_category_name.setTypeface(typeface);
        ch_select_sub_category=(CheckBox)convertView.findViewById(R.id.ch_select_sub_category);
        ch_select_sub_category.setTag(childPosition);
        if(child.isState()){
            ch_select_sub_category.setChecked(true);
        }
        else{
            ch_select_sub_category.setChecked(false);
        }
        ch_select_sub_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position=(int)view.getTag();
                HeaderModel headerModel=(HeaderModel)getGroup(groupPosition);
                if(((CheckBox)view).isChecked()){
                   childlist.get(headerModel).get(position).setState(true);
                }
                else{
                    childlist.get(headerModel).get(position).setState(false);
                }
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }*/
}
