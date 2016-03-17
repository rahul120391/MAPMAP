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
 * Created by osx on 10/02/16.
 */
public class MyDiscountWindowAdapter extends BaseAdapter {
    List<DiscountInfo> objectlist;
    LayoutInflater inflater;
    Context context;
    Typeface typeface;
    RoundedImageView iv_discount_image;
    ImageView iv_flag;
    Button btn_share,btn_directions,btn_fav,btn_call;
    TextView tv_productdesc, tv_dis, tv_start_date, tv_end_date, tv_category, tv_phone;
    Bitmap bitmap = null;
    Polyline line;
    SharedPreferences prefs;
    Dialog dialog;
    LocationScreen screen;
    NiftyDialogBuilder dialogBuilder;

    public MyDiscountWindowAdapter(Context context, List<DiscountInfo> objectlist,Dialog dialog,LocationScreen screen) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.objectlist = objectlist;
        typeface = Utils.SetFont(context, "ROBOTO-REGULAR.TTF");
        prefs=context.getSharedPreferences("UserData", 0);
        this.dialog=dialog;
        this.screen=screen;
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
        btn_call=(Button)view.findViewById(R.id.btn_call);
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
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                StringBuilder builder = new StringBuilder();
                builder.append(context.getString(R.string.category) + ":  " + objectlist.get(position).getDiscount_category() + "\n");
                builder.append(context.getString(R.string.sub_category) + ":  " + objectlist.get(position).getDiscount_sub_category() + "\n");
                builder.append(context.getString(R.string.discount_per) + ":  " + objectlist.get(position).getDiscount_per() + "\n");
                builder.append(context.getString(R.string.discount_desc) + ":  " + objectlist.get(position).getDiscount_desc() + "\n");
                builder.append(context.getString(R.string.start_date) + ": " + objectlist.get(position).getStartdate() + "\n");
                builder.append(context.getString(R.string.end_date) + ": " + objectlist.get(position).getEnddate() + "\n");
                builder.append(context.getString(R.string.dis_loc) + ": " + objectlist.get(position).getDiscount_location());
                Share(context, Effectstype.SlideBottom, builder.toString());
            }
        });
        btn_directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LocationScreen.mCurrentLocation!=null) {
                    dialog.dismiss();
                    if (Utils.checkInternetConnection(context)) {
                        Double longitude = LocationScreen.mCurrentLocation.getLongitude();
                        Double latitude = LocationScreen.mCurrentLocation.getLatitude();
                        LatLng origin = new LatLng(latitude, longitude);
                        Double destlatitude = objectlist.get(position).getLatitude();
                        Double destlongitude = objectlist.get(position).getLongitude();
                        LatLng dest = new LatLng(destlatitude, destlongitude);
                        String url = getDirectionsUrl(origin, dest);
                        System.out.println("url is" + url);
                        DownloadTask downloadTask = new DownloadTask();
                        downloadTask.execute(url);
                    } else {
                        Utils.ShowSnackBar(context, context.getString(R.string.network_error), SnackbarType.SINGLE_LINE);
                    }
                }
            }
        });

        btn_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!prefs.getString("objectid", "").equalsIgnoreCase("")){
                    if(Utils.checkInternetConnection(context)){
                        ParseObject favobject = new ParseObject(CommonUtils.FAVOURITES);
                        favobject.put("personid", prefs.getString("objectid", ""));
                        favobject.put("location", objectlist.get(position).getDiscount_location());
                        favobject.put("category", objectlist.get(position).getDiscount_category());
                        favobject.put("categoryurl", objectlist.get(position).getDiscount_category_url());
                        favobject.put("discountper", objectlist.get(position).getDiscount_per());
                        favobject.put("startdate", objectlist.get(position).getStartdate());
                        favobject.put("enddate", objectlist.get(position).getEnddate());
                        favobject.put("description", objectlist.get(position).getDiscount_desc());
                        favobject.put("phone", objectlist.get(position).getPhonenumber());
                        RotateProgressDialog.ShowDialog(context);
                        favobject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                RotateProgressDialog.DismissDialog();
                                dialog.dismiss();
                                if (e == null) {
                                    Utils.ShowSnackBar(context, "added to favourite", SnackbarType.SINGLE_LINE);
                                } else {
                                    Utils.ShowSnackBar(context, e.getMessage(), SnackbarType.SINGLE_LINE);
                                }
                            }
                        });
                    }
                    else{
                        Utils.ShowSnackBar(context,context.getString(R.string.network_error), SnackbarType.SINGLE_LINE);
                    }
                }
                else{
                    dialog.dismiss();
                    Utils.ShowSnackBar(context, "please login to add favourite", SnackbarType.SINGLE_LINE);
                }
            }
        });
        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tv_phone.getText().toString()));
                context.startActivity(intent);
            }
        });
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

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {

            System.out.println("download url" + strUrl);
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            //make some HTTP header nicety
            urlConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();

            br.close();

        } catch (Exception e) {
            System.out.println("exception is" + e.getMessage());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                ParserTask parserTask = new ParserTask();
                System.out.println("result" + result);
                // Invokes the thread for parsing the JSON data
                parserTask.execute(result);
            } else {
                RotateProgressDialog.DismissDialog();
                Utils.ShowSnackBar(context, "result not found", SnackbarType.SINGLE_LINE);
            }


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            RotateProgressDialog.ShowDialog(context);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.RED);

            }

            // Drawing polyline in the Google Map for the i-th route
            RotateProgressDialog.DismissDialog();
            screen.AddPolyline(lineOptions);
        }
    }

    public void Share(final Context context, Effectstype effect, final String text) {
        if (dialogBuilder == null || !dialogBuilder.isShowing()) {
            dialogBuilder = NiftyDialogBuilder.getInstance(context);
            dialogBuilder.withEffect(effect);
            dialogBuilder.withDialogColor(ContextCompat.getColor(context, R.color.White));
            dialogBuilder.withDividerColor(ContextCompat.getColor(context, android.R.color.transparent));
            dialogBuilder.setTopPanelBackground(ContextCompat.getColor(context, android.R.color.transparent));
            dialogBuilder.setParentPanelBackground(ContextCompat.getColor(context, android.R.color.transparent));
            dialogBuilder.setCustomView(R.layout.confirmation_dialog_view, (Activity) context);
            TextView tv_title = (TextView) dialogBuilder.findViewById(R.id.tv_title);
            tv_title.setText(context.getString(R.string.share));
            tv_title.setTypeface(Utils.SetFont(context, "ROBOTO-REGULAR.TTF"));
            TextView tv_message = (TextView) dialogBuilder.findViewById(R.id.tv_message);
            tv_message.setTypeface(Utils.SetFont(context, "ROBOTO-REGULAR.TTF"));
            tv_message.setText(context.getString(R.string.share_confirm));
            Button btn_yes = (Button) dialogBuilder.findViewById(R.id.btn_yes);
            btn_yes.setTypeface(Utils.SetFont(context, "ROBOTO-REGULAR.TTF"));
            btn_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogBuilder.dismiss();
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, text);
                    sendIntent.setType("text/plain");
                    ((Activity)context).startActivity(sendIntent);
                }
            });
            Button btn_no = (Button) dialogBuilder.findViewById(R.id.btn_no);
            btn_no.setTypeface(Utils.SetFont(context, "ROBOTO-REGULAR.TTF"));
            btn_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogBuilder.dismiss();
                    ((Activity)context).onBackPressed();
                }
            });
            dialogBuilder.show();
        }


    }
}
