package adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mapmap.R;
import com.nispok.snackbar.enums.SnackbarType;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import commonUtils.DirectionsJSONParser;
import commonUtils.Utils;
import datamodel.DiscountInfo;
import fragments.LocationScreen;
import parsework.CommonUtils;
import views.Effectstype;
import views.NiftyDialogBuilder;
import views.RotateProgressDialog;

/**
 * Created by osx on 03/11/15.
 */
public class MyDiscountsAdapter extends BaseAdapter {

    List<DiscountInfo> objectlist;
    LayoutInflater inflater;
    Context context;
    Typeface typeface;
    RoundedImageView iv_discount_image;
    ImageView iv_flag;
    Button btn_share,btn_directions,btn_fav;
    TextView tv_productdesc, tv_dis, tv_start_date, tv_end_date, tv_category, tv_phone;
    Bitmap bitmap = null;
    SharedPreferences prefs;
    LinearLayout layout_bottom;

    public MyDiscountsAdapter(Context context, List<DiscountInfo> objectlist) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.objectlist = objectlist;
        typeface = Utils.SetFont(context, "ROBOTO-REGULAR.TTF");
        prefs=context.getSharedPreferences("UserData", 0);
    }

    @Override
    public int getCount() {
        return objectlist.size();
    }

    @Override
    public Object getItem(int i) {
        return objectlist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void notifyadapter(List<DiscountInfo> newlist) {
        objectlist = newlist;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.layout_discounts_row_item, null);
        }
        iv_discount_image = (RoundedImageView) view.findViewById(R.id.iv_discount_image);
        tv_phone = (TextView) view.findViewById(R.id.tv_phone);
        iv_flag = (ImageView) view.findViewById(R.id.iv_flag);
        tv_productdesc = (TextView) view.findViewById(R.id.tv_productdesc);
        tv_dis = (TextView) view.findViewById(R.id.tv_dis);
        tv_start_date = (TextView) view.findViewById(R.id.tv_start_date);
        tv_end_date = (TextView) view.findViewById(R.id.tv_end_date);
        tv_category = (TextView) view.findViewById(R.id.tv_category);
        btn_share=(Button)view.findViewById(R.id.btn_share);
        btn_directions=(Button)view.findViewById(R.id.btn_directions);
        btn_fav=(Button)view.findViewById(R.id.btn_fav);
        layout_bottom=(LinearLayout)view.findViewById(R.id.layout_bottom);
        layout_bottom.setVisibility(View.GONE);
        tv_productdesc.setTypeface(typeface);
        tv_dis.setTypeface(typeface);
        tv_start_date.setTypeface(typeface);
        tv_end_date.setTypeface(typeface);
        tv_category.setTypeface(typeface);
        tv_phone.setTypeface(typeface);
        switch (objectlist.get(position).getDiscount_per()) {
            case "5%-10%":
                bitmap = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.ic_five_ten);
                break;
            case "11%-25%":
                bitmap = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.ic_eleven_twentyfive);
                break;
            case "26%-40%":
                bitmap = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.ic_twentysix_forty);
                break;
            case "41%-50%":
                bitmap = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.ic_fortyone_fifty);
                break;
            case "51%-60%":
                bitmap = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.ic_fiftyone_sixty);
                break;
            default:
                break;
        }
        iv_flag.setImageBitmap(bitmap);
        tv_phone.setText(objectlist.get(position).getPhonenumber());
        tv_productdesc.setText(Html.fromHtml("<font color='black'>" + "<b>" + "Product Description  : " + "</b>" + "</font>" + objectlist.get(position).getDiscount_desc()));
        tv_dis.setText(objectlist.get(position).getDiscount_per());
        tv_start_date.setText(objectlist.get(position).getStartdate());
        tv_end_date.setText(objectlist.get(position).getEnddate());
        tv_category.setText(objectlist.get(position).getDiscount_category());
        Picasso.with(context).load(Uri.parse(objectlist.get(position).getDiscount_category_url())).into(iv_discount_image);
        return view;
    }


}
