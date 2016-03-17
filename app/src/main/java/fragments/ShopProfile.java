package fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mapmap.R;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import activities.DashboardActivity;
import adapters.CategoryAdapter;
import adapters.LocationListAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import commonUtils.Utils;
import datamodel.HeaderModel;
import parsework.CommonUtils;
import views.CustomMapFragment;
import views.Effectstype;
import views.Progressdialog;
import views.RadialPickerLayout;
import views.RotateProgressDialog;
import views.TimePickerDialog;

/**
 * Created by osx on 23/10/15.
 */
public class ShopProfile extends Fragment implements OnMapReadyCallback {

    View view = null;
    CustomMapFragment mapfragment;

    @Bind(R.id.iv_back)
    ImageView iv_back;
    ArrayList<HeaderModel> categorieslist = new ArrayList<>();
    Dialog dialog;
    String category_name;
    CategoryAdapter adapter;

    @Bind(R.id.iv_category_image)
    ImageView iv_category_image;

    @Bind(R.id.view_right)
    CardView view_right;


    @Bind(R.id.et_buisnessname)
    EditText et_buisnessname;

    @Bind(R.id.et_email)
    EditText et_email;

    @Bind(R.id.et_store_url)
    EditText et_store_url;

    @Bind(R.id.et_fb)
    EditText et_fb;

    @Bind(R.id.et_twitter)
    EditText et_twitter;

    @Bind(R.id.et_desc)
    EditText et_desc;


    @Bind(R.id.iv_profile_image)
    ImageView iv_profile_image;

    @Bind(R.id.layout_add_location)
    LinearLayout layout_add_location;

    //@Bind(R.id.map_container)
    //FrameLayout map_container;

    @Bind(R.id.scrollview)
    ScrollView scrollview;

    @Bind(R.id.tv_screenname)
    TextView tv_screenname;

    @Bind(R.id.tv_location)
    TextView tv_location;
    @Bind(R.id.tv_weekdays_timetable)
    TextView tv_weekdays_timetable;


    @Bind(R.id.tv_saturday_timetable)
    TextView tv_saturday_timetable;

    @Bind(R.id.tv_sunday_timetable)
    TextView tv_sunday_timetable;

    @Bind(R.id.tv_comment)
    TextView tv_comment;

    @Bind(R.id.tv_phone)
    TextView tv_phone;
    View dialoglayout;
    String path;
    Bitmap bitmap;
    private static final int GALLERY_CODE = 100;
    private static final int CAMERA_CODE = 200;
    int PLACE_PICKER_REQUEST = 300;
    //GoogleMap googlemap;
    Double latitude, longitude;
    EditText et_enter_location;
    String weekdays,sat, sun;
    LocationListAdapter locationadapter;
    List<ParseObject> locationdata = new ArrayList<ParseObject>();
    @Bind(R.id.lv_list)
    ListView lv_list;

    @Bind(R.id.tv_additional_location)
    TextView tv_additional_location;
    List<Marker> markerlist = new CopyOnWriteArrayList<>();
    int pos;
    String category_url;
    String location;
    String workinghours;
    Double previouslatitude = 0.0;
    public  static PopupWindow popupWindow;
    Double previouslongitude = 0.0;
    TextView tv_weekdays_from, tv_sat_from, tv_sun_from;
    TextView tv_weekdays_to, tv_sat_to, tv_sun_to;
    SharedPreferences prefs;
    ParseFile photo;
    EditText et_category;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shop_profile, container, false);
        ButterKnife.bind(this, view);
        //mapfragment = new CustomMapFragment();
        //getChildFragmentManager().beginTransaction().replace(R.id.map_container, mapfragment).commit();
        //mapfragment.getMapAsync(this);
        /*mapfragment.setListener(new CustomMapFragment.OnTouchListener() {

            @Override
            public void onTouch() {
                scrollview.requestDisallowInterceptTouchEvent(true);
            }
        });
        */
        tv_screenname.setText(getString(R.string.shop_profile));
        et_buisnessname.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
        et_email.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
        et_store_url.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
        et_fb.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
        et_twitter.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
        et_desc.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
        tv_screenname.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
        locationadapter = new LocationListAdapter(ShopProfile.this, getActivity(), locationdata);
        lv_list.setAdapter(locationadapter);
        prefs = getActivity().getSharedPreferences("UserData", 0);
        String email = prefs.getString("email", "");
        et_email.setText(email);
        getCategories();
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        /*
        googlemap = googleMap;
        googlemap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googlemap.setBuildingsEnabled(true);
        googlemap.getUiSettings().setZoomControlsEnabled(false);
        googlemap.getUiSettings().setMyLocationButtonEnabled(true);
        googlemap.setIndoorEnabled(true);
        googlemap.setMyLocationEnabled(true);
        googlemap.getUiSettings().setMapToolbarEnabled(false);
        googlemap.getUiSettings().setIndoorLevelPickerEnabled(true);
        googlemap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng latlng = marker.getPosition();
                for (ParseObject object : locationdata) {
                    latitude = (Double) object.get("latitude");
                    longitude = (Double) object.get("longitude");
                    LatLng latlngg = new LatLng(latitude, longitude);
                    if (latlng.equals(latlngg)) {
                        location = object.get("loc").toString();
                        switch (Utils.getCurrentDay()) {
                            case "Monday":
                                workinghours = object.get("weekdays").toString();
                                break;
                            case "Tuesday":
                                workinghours = object.get("weekdays").toString();
                                break;
                            case "Wednesday":
                                workinghours = object.get("weekdays").toString();
                                break;
                            case "Thursday":
                                workinghours = object.get("weekdays").toString();
                                break;
                            case "Friday":
                                workinghours = object.get("weekdays").toString();
                                break;
                            case "Saturday":
                                workinghours = object.get("sat").toString();
                                break;
                            case "Sunday":
                                workinghours = object.get("sun").toString();
                                break;
                        }
                    }
                }
                googlemap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        View v = getActivity().getLayoutInflater().inflate(R.layout.layout_custom_infowindow, null);
                        TextView tv_location = (TextView) v.findViewById(R.id.tv_location);
                        TextView tv_workinghours = (TextView) v.findViewById(R.id.tv_workinghours);
                        tv_location.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
                        tv_workinghours.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
                        tv_location.setText(location);
                        tv_workinghours.setText(workinghours);
                        return v;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {

                        return null;
                    }
                });
                return false;
            }
        });
        */
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (photo!=null){
            photo.cancel();
        }
    }

    @OnClick(R.id.iv_back)
    public void Back() {
        getActivity().onBackPressed();
    }

    public void ShowCategories(String from) {
        if(category_name!=null && from.equalsIgnoreCase("profile")){
            Utils.ShowSnackBar(getActivity(),"main category cant be editable",SnackbarType.SINGLE_LINE);
        }
        else{
            dialog = new Dialog(getActivity(), R.style.DialogTheme);
            if (!dialog.isShowing()) {
                System.out.println("show dialog");
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                final View dialoglayout = inflater.inflate(R.layout.dialog_categories_list, null);
                dialog.setContentView(dialoglayout);
                final ListView lv_categories = (ListView) dialoglayout.findViewById(R.id.lv_categories);
                TextView tv_screenname = (TextView) dialoglayout.findViewById(R.id.tv_screenname);
                tv_screenname.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
                tv_screenname.setText(getString(R.string.categories));
                ImageView iv_back = (ImageView) dialoglayout.findViewById(R.id.iv_back);
                iv_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_SmileWindow;
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dialog.show();
                adapter = new CategoryAdapter(getActivity(), categorieslist);
                lv_categories.setAdapter(adapter);
                lv_categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        dialog.dismiss();
                        HeaderModel model = categorieslist.get(position);
                        category_name = model.getCategoryname();
                        category_url= model.getUrl();
                        if(et_category!=null){
                            et_category.setText(category_name);
                        }
                        Picasso.with(getActivity()).load(Uri.parse(category_url)).into(iv_category_image);
                    }
                });
            }
        }

    }

    public void getCategories() {
        if (Utils.checkInternetConnection(getActivity())) {
            RotateProgressDialog.ShowDialog(getActivity());
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(CommonUtils.CATEGORIES);
            query.orderByAscending("name");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        if (list.size() > 0) {
                            categorieslist.clear();
                            for (int i = 0; i < list.size(); i++) {
                                ParseObject categories = list.get(i);
                                String name = categories.get("name").toString();
                                ParseFile photo = categories.getParseFile("category_image");
                                String url = photo.getUrl();
                                HeaderModel model = new HeaderModel();
                                model.setUrl(url);
                                model.setCategoryname(name);
                                categorieslist.add(model);
                            }


                        } else {
                            Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, getString(R.string.no_category));
                        }
                        getShopProfileData();
                    } else {
                        RotateProgressDialog.DismissDialog();
                        Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, e.getMessage());
                    }

                }
            });
        } else {
            Utils.ShowSnackBar(getActivity(), getString(R.string.network_error), SnackbarType.SINGLE_LINE);
        }

    }

    @OnClick(R.id.view_right)
    public void SelectCategory() {
        ShowCategories("profile");
    }


    @OnClick(R.id.layout_add_location)
    public void AddLocation() {
        AddLocations(null, -1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                if (data != null && data.getData() != null) {
                    InputStream is = null;
                    try {

                        is = getActivity().getContentResolver().openInputStream(data.getData());
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeStream(is, null, options);
                        Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math.abs(options.outWidth - 100);
                        if (options.outHeight * options.outWidth * 2 >= 200 * 200 * 2) {
                            double sampleSize = scaleByHeight
                                    ? options.outHeight / 100
                                    : options.outWidth / 100;
                            options.inSampleSize =
                                    (int) Math.pow(2d, Math.floor(
                                            Math.log(sampleSize) / Math.log(2d)));
                        }
                        options.inJustDecodeBounds = false;
                        is.close();
                        is = getActivity().getContentResolver().openInputStream(data.getData());
                        bitmap = BitmapFactory.decodeStream(is, null, options);
                        is.close();
                        if (bitmap != null) {
                            System.out.println("scaled bitmap" + bitmap);
                            iv_profile_image.setImageBitmap(bitmap);
                        } else {
                            System.out.println("bitmap is null");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Utils.ShowSnackBar(getActivity(), "Unable to fetch image", SnackbarType.SINGLE_LINE);
                }
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                Utils.ShowSnackBar(getActivity(), getString(R.string.req_can), SnackbarType.SINGLE_LINE);
            }


        } else if (requestCode == CAMERA_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                if (data != null && data.getExtras() != null) {
                    bitmap = (Bitmap) data.getExtras().get("data");
                    iv_profile_image.setImageBitmap(bitmap);
                } else {
                    Utils.ShowSnackBar(getActivity(), "Unable to fetch image", SnackbarType.SINGLE_LINE);
                }
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                Utils.ShowSnackBar(getActivity(), getString(R.string.req_can), SnackbarType.SINGLE_LINE);
            }


        } else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == getActivity().RESULT_OK) {
              Place place = PlacePicker.getPlace(data, getActivity());
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;

                if (place.getAddress().toString().trim().equalsIgnoreCase("")) {
                    Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, getString(R.string.unable_fetch_loc));
                } else {
                    et_enter_location.setText(place.getAddress().toString().trim());
                }

            } else {
                //Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, getString(R.string.req_can));
            }
        }

    }

    public void ShowTimePicker(final String open_close, final View view) {
        Calendar cal = Calendar.getInstance();
        int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        TimePickerDialog time = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout vieww, int hourOfDay, int minute) {
                String minutee = null;
                String am_pm = null;
                if (minute < 10) {
                    minutee = "0" + minute;
                } else {
                    minutee = minute + "";
                }
                Calendar datetime = Calendar.getInstance();
                datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                datetime.set(Calendar.MINUTE, minute);
                switch (datetime.get(Calendar.AM_PM)) {
                    case Calendar.AM:
                        am_pm = "AM";
                        break;
                    case Calendar.PM:
                        am_pm = "PM";
                        break;
                }
                String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ? "12" : datetime.get(Calendar.HOUR) + "";
                switch (pos) {
                    case 1:
                        switch (open_close) {
                            case "weekdays_open":
                                tv_weekdays_from.setText(strHrsToShow + ":" + minutee + " " + am_pm);
                                break;
                            case "weekdays_close":
                                System.out.println("inside monday close" + strHrsToShow + ":" + minutee + " " + am_pm);
                                DateTimeFormatter format = DateTimeFormat.forPattern("hh:mm a");
                                LocalTime open = LocalTime.parse(tv_weekdays_from.getText().toString(), format);
                                LocalTime close = LocalTime.parse(strHrsToShow + ":" + minutee + " " + am_pm, format);
                                boolean check = open.isAfter(close);
                                if (check) {
                                    Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, getString(R.string.close_time_error));
                                } else {
                                    tv_weekdays_to.setText(strHrsToShow + ":" + minutee + " " + am_pm);
                                }
                                break;
                        }
                        break;
                    case 2:
                        switch (open_close) {
                            case "sat_open":
                                tv_sat_from.setText(strHrsToShow + ":" + minutee + " " + am_pm);
                                break;
                            case "sat_close":
                                DateTimeFormatter format = DateTimeFormat.forPattern("hh:mm a");
                                LocalTime open = LocalTime.parse(tv_sat_from.getText().toString().trim(), format);
                                LocalTime close = LocalTime.parse(strHrsToShow + ":" + minutee + " " + am_pm, format);
                                boolean check = open.isAfter(close);
                                if (check) {
                                    Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, getString(R.string.close_time_error));
                                } else {
                                    tv_sat_to.setText(strHrsToShow + ":" + minutee + " " + am_pm);
                                }
                                break;
                        }
                        break;
                    case 3:
                        switch (open_close) {
                            case "sun_open":
                                tv_sun_from.setText(strHrsToShow + ":" + minutee + " " + am_pm);
                                break;
                            case "sun_close":
                                DateTimeFormatter format = DateTimeFormat.forPattern("hh:mm a");
                                LocalTime open = LocalTime.parse(tv_sun_from.getText().toString().trim(), format);
                                LocalTime close = LocalTime.parse(strHrsToShow + ":" + minutee + " " + am_pm, format);
                                boolean check = open.isAfter(close);
                                if (check) {
                                    Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom,getString(R.string.close_time_error));
                                } else {
                                    tv_sun_to.setText(strHrsToShow + ":" + minutee + " " + am_pm);
                                }
                                break;
                        }
                        break;
                }

            }
        }, hourOfDay, minute, false);
        if(open_close.endsWith("open")){
            time.setStartTime(9, 0);
        }
        else if(open_close.endsWith("close")){
            time.setStartTime(21,0);
        }
        time.show(getChildFragmentManager(), "datepicker");
    }

    @OnClick(R.id.btn_submit)
    public void Submit() {
        Utils.HideKeyboard(getActivity());
        if (TextUtils.isEmpty(et_buisnessname.getText().toString())) {
            Utils.ShowSnackBar(getActivity(), getString(R.string.error_buisnessname), SnackbarType.SINGLE_LINE);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(et_email.getText().toString().trim()).matches()) {
            Utils.ShowSnackBar(getActivity(), getString(R.string.error_email), SnackbarType.SINGLE_LINE);
        } else if (!Patterns.WEB_URL.matcher(et_store_url.getText().toString().trim()).matches()) {
            Utils.ShowSnackBar(getActivity(), getString(R.string.error_weblink), SnackbarType.SINGLE_LINE);
        } else {

            Drawable d = iv_profile_image.getDrawable();
            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
            byte[] bitmapdata = null;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            bitmapdata = stream.toByteArray();
            final ParseFile profilepic = new ParseFile("picture.png", bitmapdata);
            String profileobjectid = prefs.getString("profileobjectid", "");
            final ParseObject object = ParseObject.createWithoutData(CommonUtils.SHOPKEEPER_PROFILE_DATA, profileobjectid);
            object.put("shop_name", et_buisnessname.getText().toString().trim());
            object.put("store_url", et_store_url.getText().toString().trim());
            object.put("fb_link", et_fb.getText().toString().trim());
            object.put("twitter_link", et_twitter.getText().toString().trim());
            object.put("description", et_desc.getText().toString().trim());
            object.put("main_category", category_name);
            object.put("category_url",category_url);
            if (Utils.checkInternetConnection(getActivity())) {
                RotateProgressDialog.ShowDialog(getActivity());
                profilepic.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            object.put("photo", profilepic);
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    RotateProgressDialog.DismissDialog();
                                    if (e == null) {
                                        Utils.ShowSnackBar(getActivity(), getString(R.string.profile_save), SnackbarType.SINGLE_LINE);
                                        prefs.edit().putString("category_name", category_name).commit();
                                        prefs.edit().putString("category_url", category_url).commit();
                                       // ((DashboardActivity)getActivity()).FragmentTransactions(new AddDiscount(), "adddiscount");
                                    } else {
                                        Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, e.getMessage());
                                    }
                                }
                            });
                        } else {
                            RotateProgressDialog.DismissDialog();
                            Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, e.getMessage());
                        }
                    }
                });


            } else {
                Utils.ShowSnackBar(getActivity(), getString(R.string.network_error), SnackbarType.SINGLE_LINE);
            }
        }

    }


    @OnClick(R.id.iv_profile_image)
    public void SelectImage() {
        BottomSheet.Builder sheet = new BottomSheet.Builder(getActivity(), R.style.BottomSheet_StyleDialog);
        sheet.title("Choose Source");
        sheet.sheet(R.menu.bottom_sheet_menu);
        sheet.listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case R.id.camera:
                        try {
                            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(i, CAMERA_CODE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.gallery:
                        try {
                            Intent gallery = null;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                gallery = new Intent(
                                        Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            } else {
                                gallery = new Intent();
                                gallery.setType("image/*");
                                gallery.setAction(Intent.ACTION_GET_CONTENT);
                            }
                            startActivityForResult(gallery, GALLERY_CODE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
        sheet.show();
    }

    public void getShopProfileData() {
        if (Utils.checkInternetConnection(getActivity())) {
            final String objectid = prefs.getString("profileobjectid", "");
            System.out.println("profileobjectid" + objectid);
            ParseQuery<ParseObject> object = new ParseQuery<ParseObject>(CommonUtils.SHOPKEEPER_PROFILE_DATA);
            object.include("mainloc");
            object.getInBackground(objectid, new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null) {
                        getdata();
                        if (parseObject.get("main_category") != null) {
                            category_name = parseObject.get("main_category").toString();
                            for (HeaderModel model : categorieslist) {
                                if (model.getCategoryname().equalsIgnoreCase(category_name)) {
                                    category_url= model.getUrl();
                                    Picasso.with(getActivity()).load(Uri.parse(category_url)).into(iv_category_image);
                                }
                            }
                            prefs.edit().putString("category_name", category_name).commit();
                            prefs.edit().putString("category_url", category_url).commit();
                        }
                        else{
                            if(categorieslist.size()>0){
                                category_name = categorieslist.get(0).getCategoryname();
                                category_url= categorieslist.get(0).getUrl();
                                Picasso.with(getActivity()).load(Uri.parse(category_url)).into(iv_category_image);
                            }

                        }

                        if (parseObject.get("shop_name") != null) {
                            et_buisnessname.setText(parseObject.get("shop_name").toString());
                        }

                        if (parseObject.get("store_url") != null) {
                            et_store_url.setText(parseObject.get("store_url").toString());
                        }
                        if (parseObject.get("description") != null) {
                            et_desc.setText(parseObject.get("description").toString());
                        }
                        if (parseObject.get("fb_link") != null) {
                            et_fb.setText(parseObject.get("fb_link").toString());
                        }
                        if (parseObject.get("twitter_link") != null) {
                            et_twitter.setText(parseObject.get("twitter_link").toString());
                        }
                        photo = (ParseFile) parseObject.get("photo");
                        if (photo != null) {
                            photo.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] bytes, ParseException e) {
                                    if (e == null) {
                                        BitmapFactory.Options options = new BitmapFactory.Options();
                                        options.inJustDecodeBounds=true;
                                        options.outHeight=200;
                                        options.outWidth=200;
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        if(bitmap!=null){
                                            iv_profile_image.setImageBitmap(bitmap);
                                        }
                                        else{
                                            System.out.println("bitmap is coming null");
                                        }

                                    } else {
                                        Utils.ShowSnackBar(getActivity(), e.getMessage(), SnackbarType.SINGLE_LINE);
                                    }
                                }
                            });

                        }
                        ParseObject obj=parseObject.getParseObject("mainloc");
                        System.out.println("obj"+obj);
                        if(obj.getString("loc")!=null){
                            tv_location.setText(obj.getString("loc"));
                        }
                        if(obj.getString("comment")!=null){
                            tv_comment.setText(obj.getString("comment"));
                        }
                        if(obj.getString("phone")!=null){
                            tv_phone.setText(obj.getString("phone"));
                        }

                        if(obj.getString("weekdays")!=null){
                            tv_weekdays_timetable.setText(obj.getString("weekdays"));
                        }

                        if(obj.getString("sat")!=null){
                            tv_saturday_timetable.setText(obj.getString("sat"));
                        }

                        if(obj.getString("sun")!=null){
                            tv_sunday_timetable.setText(obj.getString("sun"));
                        }
                    } else {
                        RotateProgressDialog.DismissDialog();
                        Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, e.getMessage());
                    }
                }
            });
        } else {
            RotateProgressDialog.DismissDialog();
            Utils.ShowSnackBar(getActivity(), getString(R.string.network_error), SnackbarType.SINGLE_LINE);
        }
    }

    public void getdata() {
        final String objectid = prefs.getString("profileobjectid", "");
        ParseObject obj = ParseObject.createWithoutData(CommonUtils.SHOPKEEPER_PROFILE_DATA, objectid);
        System.out.println("object id" + objectid);
        ParseQuery<ParseObject> query = ParseQuery.getQuery(CommonUtils.LOCATIONS);
        query.whereEqualTo("Locations", obj);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                RotateProgressDialog.DismissDialog();
                locationdata.clear();
                if (e == null) {
                    if (list.size() > 0) {
                        tv_additional_location.setVisibility(View.VISIBLE);
                        locationdata = list;
                        System.out.println("location data" + locationdata);
                        locationadapter = new LocationListAdapter(ShopProfile.this, getActivity(), locationdata);
                        lv_list.setAdapter(locationadapter);
                        /*
                        if (googlemap != null) {
                            for (ParseObject object : locationdata) {
                                latitude = (Double) object.get("latitude");
                                longitude = (Double) object.get("longitude");
                                Marker marker = googlemap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
                                markerlist.add(marker);
                            }
                            googlemap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
                            googlemap.animateCamera(CameraUpdateFactory.zoomTo(10));

                        }
                        */
                    }

                } else {
                    Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, e.getMessage());
                }
            }
        });
    }


    public void AddLocations(final ParseObject object, final int loc) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        dialoglayout = inflater.inflate(R.layout.layout_location_add, (ViewGroup) getActivity().findViewById(R.id.layout_main));
        popupWindow = new PopupWindow(
                dialoglayout,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.getContentView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    popupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });
        if (!popupWindow.isShowing()) {
            TextView tv_add_location = (TextView) dialoglayout.findViewById(R.id.tv_add_location);
            tv_add_location.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            if(object==null){
                tv_add_location.setText(getString(R.string.add_location));
            }
            else{
                tv_add_location.setText(getString(R.string.edit_location));
            }
            et_category=(EditText)dialoglayout.findViewById(R.id.et_category);
            if(object!=null){
                et_category.setText(object.getString("category"));
            }
            et_category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(object==null){
                        System.out.println("show categories");
                        ShowCategories("addloc");
                    }
                    else{
                        Utils.ShowCustomDialog(getActivity(),Effectstype.SlideBottom,"Category cant be editable");
                    }

                }
            });
            et_enter_location = (EditText) dialoglayout.findViewById(R.id.et_enter_location);
            final EditText et_addtext=(EditText)dialoglayout.findViewById(R.id.et_addtext);
            et_addtext.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            et_enter_location.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            et_enter_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        Context context = getActivity().getApplicationContext();
                        startActivityForResult(builder.build(context), PLACE_PICKER_REQUEST);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            TextView tv_select_working_hours = (TextView) dialoglayout.findViewById(R.id.tv_select_working_hours);
            tv_select_working_hours.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            TextView tv_days = (TextView) dialoglayout.findViewById(R.id.tv_days);
            tv_days.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            TextView tv_working_hours = (TextView) dialoglayout.findViewById(R.id.tv_working_hours);
            tv_working_hours.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            TextView tv_closed = (TextView) dialoglayout.findViewById(R.id.tv_closed);
            tv_closed.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            TextView tv_weekdays = (TextView) dialoglayout.findViewById(R.id.tv_weekdays);
            TextView tv_saturday = (TextView) dialoglayout.findViewById(R.id.tv_saturday);
            TextView tv_sunday = (TextView) dialoglayout.findViewById(R.id.tv_sunday);
            tv_weekdays.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            tv_saturday.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            tv_sunday.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            final EditText et_phone = (EditText) dialoglayout.findViewById(R.id.et_phone);
            et_phone.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            final CheckBox ch_weekdays = (CheckBox) dialoglayout.findViewById(R.id.ch_weekdays);
            final CheckBox ch_saturday = (CheckBox) dialoglayout.findViewById(R.id.ch_saturday);
            final CheckBox ch_sunday = (CheckBox) dialoglayout.findViewById(R.id.ch_sunday);
            tv_weekdays_from = (TextView) dialoglayout.findViewById(R.id.tv_weekdays_from);
            tv_weekdays_from.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            tv_sat_from = (TextView) dialoglayout.findViewById(R.id.tv_sat_from);
            tv_sat_from.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            tv_sun_from = (TextView) dialoglayout.findViewById(R.id.tv_sun_from);
            tv_sun_from.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            tv_weekdays_to = (TextView) dialoglayout.findViewById(R.id.tv_weekdays_to);
            tv_weekdays_to.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            tv_sat_to = (TextView) dialoglayout.findViewById(R.id.tv_sat_to);
            tv_sat_to.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            tv_sun_to = (TextView) dialoglayout.findViewById(R.id.tv_sun_to);
            tv_sun_to.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            ch_weekdays.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        tv_weekdays_from.setText("");
                        tv_weekdays_to.setText("");
                        tv_weekdays_from.setEnabled(false);
                        tv_weekdays_to.setEnabled(false);
                    } else {
                        tv_weekdays_from.setEnabled(true);
                        tv_weekdays_to.setEnabled(true);
                    }
                }
            });
            ch_saturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        tv_sat_from.setText("");
                        tv_sat_to.setText("");
                        tv_sat_from.setEnabled(false);
                        tv_sat_to.setEnabled(false);
                    } else {
                        tv_sat_from.setEnabled(true);
                        tv_sat_to.setEnabled(true);
                    }
                }
            });
            ch_sunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        tv_sun_from.setText("");
                        tv_sun_to.setText("");
                        tv_sun_from.setEnabled(false);
                        tv_sun_to.setEnabled(false);
                    } else {
                        tv_sun_from.setEnabled(true);
                        tv_sun_to.setEnabled(true);
                    }
                }
            });
            tv_weekdays_from.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(tv_weekdays_to.getText().toString().length()==0){
                        pos = 1;
                        ShowTimePicker("weekdays_open", view);
                    }
                    else{
                        ShowTimePikcerOn(tv_weekdays_from,tv_weekdays_to);
                    }

                }
            });

            tv_weekdays_to.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tv_weekdays_from.getText().toString().trim().length() == 0) {
                        Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, getString(R.string.select_opening_hour));
                    } else {
                        pos = 1;
                        ShowTimePicker("weekdays_close", view);
                    }
                }
            });
            tv_sat_from.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(tv_sat_to.getText().toString().length()==0){
                        pos = 2;
                        ShowTimePicker("sat_open", view);
                    }
                    else{
                        ShowTimePikcerOn(tv_sat_from,tv_sat_to);
                    }

                }
            });
            tv_sat_to.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tv_sat_from.getText().toString().trim().length() == 0) {
                        Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, getString(R.string.select_opening_hour));
                    } else {
                        pos = 2;
                        ShowTimePicker("sat_close", view);
                    }
                }
            });
            tv_sun_from.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(tv_sun_to.getText().toString().length()==0){
                        pos = 3;
                        ShowTimePicker("sun_open", view);
                    }
                    else{
                        ShowTimePikcerOn(tv_sun_from,tv_sun_to);
                    }

                }
            });
            tv_sun_to.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tv_sun_from.getText().toString().trim().length() == 0) {
                        Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, getString(R.string.select_opening_hour));
                    } else {
                        pos = 3;
                        ShowTimePicker("sun_close", view);
                    }
                }
            });
            if (object != null) {
                et_enter_location.setText(object.get("loc").toString());
                et_phone.setText(object.get("phone").toString());
                et_addtext.setText(object.get("comment").toString());
                weekdays = object.get("weekdays").toString();
                sat = object.get("sat").toString();
                sun = object.get("sun").toString();
                latitude = (Double) object.get("latitude");
                longitude = (Double) object.get("longitude");
                previouslatitude = latitude;
                previouslongitude = longitude;
                if (object.get("weekdays").toString().equalsIgnoreCase(getString(R.string.close))) {
                    ch_weekdays.setChecked(true);
                    tv_weekdays_from.setEnabled(false);
                    tv_weekdays_to.setEnabled(false);
                } else {
                    String from_to[] = object.get("weekdays").toString().split("-");
                    tv_weekdays_from.setText(from_to[0]);
                    tv_weekdays_to.setText(from_to[1]);
                }
                if (object.get("sat").toString().equalsIgnoreCase(getString(R.string.close))) {
                    ch_saturday.setChecked(true);
                    tv_sat_from.setEnabled(false);
                    tv_sat_to.setEnabled(false);
                } else {
                    String from_to[] = object.get("sat").toString().split("-");
                    tv_sat_from.setText(from_to[0]);
                    tv_sat_to.setText(from_to[1]);
                }
                if (object.get("sun").toString().equalsIgnoreCase(getString(R.string.close))) {
                    ch_sunday.setChecked(true);
                    tv_sun_from.setEnabled(false);
                    tv_sun_to.setEnabled(false);
                } else {
                    String from_to[] = object.get("sun").toString().split("-");
                    tv_sun_from.setText(from_to[0]);
                    tv_sun_to.setText(from_to[1]);
                }
            }

            Button btn_submit = (Button) dialoglayout.findViewById(R.id.btn_submit);

            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_phone.getWindowToken(), 0);
                    if (et_enter_location.getText().toString().trim().length() == 0) {
                        Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, getString(R.string.sel_loc_err));

                    }
                    else if (et_phone.getText().toString().trim().length() == 0) {
                        Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, getString(R.string.enter_phone_err));;
                    }
                    else if(et_category.getText().toString().trim().length()==0){
                        Utils.ShowCustomDialog(getActivity(),Effectstype.SlideBottom,"Please select category");
                    }
                    else {
                        if (ch_weekdays.isChecked() == true) {
                            weekdays = getString(R.string.close);
                        } else if (ch_weekdays.isChecked() == false) {
                            if (tv_weekdays_from.getText().toString().trim().length() == 0) {
                                Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, getString(R.string.sel_mon_open));
                                return;
                            } else if (tv_weekdays_to.getText().toString().trim().length() == 0) {
                                Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, getString(R.string.sel_mon_close));
                                return;
                            } else {
                                weekdays = tv_weekdays_from.getText().toString() + "-" + tv_weekdays_to.getText().toString();

                            }
                        }
                        if (ch_saturday.isChecked() == true) {
                            sat = getString(R.string.close);
                        } else if (ch_saturday.isChecked() == false) {
                            if (tv_sat_from.getText().toString().trim().length() == 0) {
                                Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, getString(R.string.sel_sat_open));
                                return;
                            } else if (tv_sat_to.getText().toString().trim().length() == 0) {
                                Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, getString(R.string.sel_sat_close));
                                return;
                            } else {
                                sat = tv_sat_from.getText().toString() + "-" + tv_sat_to.getText().toString();
                            }

                        }
                        if (ch_sunday.isChecked() == true) {
                            sun = getString(R.string.close);
                        } else if (ch_sunday.isChecked() == false) {
                            if (tv_sun_from.getText().toString().trim().length() == 0) {
                                Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, getString(R.string.sel_sun_open));
                                return;
                            } else if (tv_sun_to.getText().toString().trim().length() == 0) {
                                Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, getString(R.string.sel_sun_close));
                                return;
                            } else {
                                sun = tv_sun_from.getText().toString() + "-" + tv_sun_to.getText().toString();
                            }
                        }

                        if(ch_weekdays.isChecked() && ch_sunday.isChecked() && ch_saturday.isChecked()){
                            Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, getString(R.string.select_one_working_hour));;
                            return;
                        }

                        if(Utils.checkInternetConnection(getActivity())){
                            RotateProgressDialog.ShowDialog(getActivity());
                            if (object != null) {
                                object.put("loc", et_enter_location.getText().toString().trim());
                                object.put("phone", et_phone.getText().toString().trim());
                                object.put("weekdays",weekdays);
                                object.put("category",et_category.getText().toString().trim());
                                object.put("sat", sat);
                                object.put("sun", sun);
                                if(et_addtext.getText().toString().trim().equalsIgnoreCase("")){
                                    object.put("comment","No comment");
                                }
                                else{
                                    object.put("comment",et_addtext.getText().toString().trim());
                                }

                                object.put("latitude", latitude);
                                object.put("longitude", longitude);
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        RotateProgressDialog.DismissDialog();
                                        if (e == null) {
                                            popupWindow.dismiss();
                                            locationdata.set(loc, object);
                                            locationadapter.notifyDataSetChanged();
                                            Utils.ShowSnackBar(getActivity(),getString(R.string.loc_update),SnackbarType.SINGLE_LINE);
                                            RemoveMarker(previouslatitude, previouslongitude);
                                            /*
                                            if (googlemap != null) {
                                                Marker marker = googlemap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
                                                markerlist.add(marker);
                                            }
                                            System.out.println("new latitude" + latitude);
                                            System.out.println("new longitude" + longitude);
                                            googlemap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
                                            googlemap.animateCamera(CameraUpdateFactory.zoomTo(10));
                                            */
                                        } else {
                                            Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, e.getMessage());
                                        }
                                    }
                                });
                            } else {

                                final ParseObject location_object = new ParseObject(CommonUtils.LOCATIONS);
                                location_object.put("loc", et_enter_location.getText().toString().trim());
                                location_object.put("phone", et_phone.getText().toString().trim());
                                location_object.put("weekdays",weekdays);
                                location_object.put("category",et_category.getText().toString().trim());
                                location_object.put("sat", sat);
                                location_object.put("sun", sun);
                                if(et_addtext.getText().toString().trim().equalsIgnoreCase("")){
                                    location_object.put("comment","No comment");
                                }
                                else{
                                    location_object.put("comment",et_addtext.getText().toString().trim());
                                }

                                location_object.put("latitude", latitude);
                                location_object.put("longitude", longitude);
                                SharedPreferences prefs = getActivity().getSharedPreferences("UserData", 0);
                                String profileobjectid = prefs.getString("profileobjectid", "");
                                ParseObject profile = ParseObject.createWithoutData(CommonUtils.SHOPKEEPER_PROFILE_DATA, profileobjectid);
                                ParseRelation<ParseObject> relation = location_object.getRelation("Locations");
                                relation.add(profile);
                                location_object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        RotateProgressDialog.DismissDialog();
                                        if (e == null) {
                                            popupWindow.dismiss();
                                            locationdata.add(location_object);
                                            locationadapter.notifyDataSetChanged();
                                            Utils.ShowSnackBar(getActivity(), getString(R.string.loc_save), SnackbarType.SINGLE_LINE);
                                            /*
                                            if (googlemap != null) {
                                                Marker marker = googlemap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
                                                markerlist.add(marker);
                                            }
                                            tv_additional_location.setVisibility(View.VISIBLE);
                                            googlemap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
                                            googlemap.animateCamera(CameraUpdateFactory.zoomTo(10));
                                            */

                                        } else {
                                            Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, e.getMessage());
                                        }
                                    }
                                });


                            }
                        }
                        else{
                            Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, getString(R.string.network_error));
                        }
                    }
                }
            });
            popupWindow.showAtLocation(dialoglayout, Gravity.CENTER, 0, 0);
        }
    }

    public void RemoveMarker(Double latitude, Double longitude) {
        LatLng position = new LatLng(latitude, longitude);
        Iterator<Marker> marker=markerlist.iterator();
        while (marker.hasNext()){
            Marker mm=marker.next();
            if(mm.getPosition().equals(position)){
                mm.remove();
                markerlist.remove(mm);
            }
        }
    }

    public void ShowHide(){
        if(lv_list.getCount()==0){
            tv_additional_location.setVisibility(View.GONE);
        }
    }

    public void OpenPopUp() {
        View popupView = getActivity().getLayoutInflater().inflate(R.layout.layout_location_add
                , null);
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.update();
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    public void ShowTimePikcerOn(final TextView tv_from,final TextView tv_to){
        Calendar cal = Calendar.getInstance();
        int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        TimePickerDialog time = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                String minutee = null;
                String am_pm = null;
                if (minute < 10) {
                    minutee = "0" + minute;
                } else {
                    minutee = minute + "";
                }
                Calendar datetime = Calendar.getInstance();
                datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                datetime.set(Calendar.MINUTE, minute);
                switch (datetime.get(Calendar.AM_PM)) {
                    case Calendar.AM:
                        am_pm = "AM";
                        break;
                    case Calendar.PM:
                        am_pm = "PM";
                        break;
                }
                String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ? "12" : datetime.get(Calendar.HOUR) + "";
                DateTimeFormatter format = DateTimeFormat.forPattern("hh:mm a");
                LocalTime close = LocalTime.parse(tv_to.getText().toString(), format);
                LocalTime open = LocalTime.parse(strHrsToShow + ":" + minutee + " " + am_pm, format);
                boolean check = open.isBefore(close);
                if (check) {
                    tv_from.setText(strHrsToShow + ":" + minutee + " " + am_pm);
                } else {
                  Utils.ShowCustomDialog(getActivity(),Effectstype.SlideBottom,"opening time must be less than closing");
                }
            }
        },hourOfDay, minute, false);
        time.show(getChildFragmentManager(), "datepicker");
    }


}
