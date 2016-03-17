package adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapmap.R;

import commonUtils.Utils;

/**
 * Created by osx on 03/11/15.
 */
public class DiscountAdapter extends BaseAdapter {
    String[] discounts;
    int[] images;
    Context context;
    ImageView iv_flags;
    TextView tv_per;
    Typeface typeface;
    LayoutInflater inflater;
    public DiscountAdapter(Context context,String[] discounts,int[] images){
        this.context=context;
        this.discounts=discounts;
        this.images=images;
        inflater=LayoutInflater.from(context);
        typeface= Utils.SetFont(context, "ROBOTO-ITALIC.TTF");
    }
    @Override
    public int getCount() {
        return discounts.length;
    }

    @Override
    public Object getItem(int i) {
        return discounts[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(view==null){
            view=inflater.inflate(R.layout.layout_sel_percentage,null);
        }
        tv_per=(TextView)view.findViewById(R.id.tv_per);
        iv_flags=(ImageView)view.findViewById(R.id.iv_flags);
        iv_flags.setImageResource(images[position]);
        tv_per.setText(discounts[position]);
        return view;
    }
}
