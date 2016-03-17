package fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mapmap.R;
import com.nispok.snackbar.enums.SnackbarType;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import org.joda.time.LocalDate;
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
import java.util.Map;

import activities.DashboardActivity;
import activities.MainActivity;
import adapters.CategoriesAdapter;
import adapters.CategoryItem;
import adapters.MyDiscountWindowAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import commonUtils.DirectionsJSONParser;
import commonUtils.Utils;
import datamodel.ChildModel;
import datamodel.DiscountInfo;
import datamodel.HeaderModel;
import parsework.CommonUtils;
import views.Effectstype;
import views.MyMapFragment;
import views.Progressdialog;
import views.RotateProgressDialog;
import views.Sliding;

/**
 * Created by osx on 19/10/15.
 */
public class LocationScreen extends android.support.v4.app.Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {

    View view = null;
    SupportMapFragment mapfragnent;
    GoogleMap gmap;


    @Bind(R.id.main_content)
    Sliding main_content;


    @Bind(R.id.rel_bar_click)
    RelativeLayout iv_bar;

    @Bind(R.id.imageView2)
    ImageView view_right;

    @Bind(R.id.imageView)
    ImageView view_left;

    @Bind(R.id.imageView3)
    ImageView view_left2;

    @Bind(R.id.layout_search)
    RelativeLayout layout_search;


    @Bind(R.id.iv_search)
    ImageView iv_search;

    @Bind(R.id.iv_go)
    ImageView iv_go;

    @Bind(R.id.layout_filter)
    LinearLayout layout_filter;

    @Bind(R.id.iv_searchh)
    ImageView iv_searchh;

    @Bind(R.id.iv_filter)
    ImageView iv_filter;

    @Bind(R.id.et_search)
    EditText et_search;
    LocationRequest mLocationRequest;

    public static Location mCurrentLocation;

    GoogleApiClient mGoogleApiClient;
    SharedPreferences prefs;

    private int lastExpandedPosition = -1;
    String type = null;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    ParseQuery<ParseObject> discounts;
    Dialog dialog;


    List<HeaderModel> headerlist = new ArrayList<>();
    List<DiscountInfo> parseObjectList = new ArrayList<>();
    ArrayList<String> positions = new ArrayList<String>();
    Map<HeaderModel, List<ChildModel>> maplist = new HashMap<>();
    CategoriesAdapter adapter;
    Polyline line;
    String categoryname, subcategory;
    boolean flag = false;
    float latitude_dis_add = 0.00001f;
    float longitude_dis_add = 0.00001f;
    Double latt, longit;

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Utils.isGooglePlayServicesAvailable(getActivity())) {
            Utils.ShowSnackBar(getActivity(), getString(R.string.play_services_error), SnackbarType.SINGLE_LINE);
            flag = true;
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        prefs = getActivity().getSharedPreferences("UserData", 0);
        prefs.edit().putBoolean("locationfetch", false).commit();
        type = prefs.getString("type", "");
        try {
            view = inflater.inflate(R.layout.fragment_location, container, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ButterKnife.bind(this, view);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        main_content.setVisibility(View.GONE);
        mapfragnent = new MyMapFragment();
        getFragmentManager().beginTransaction().replace(R.id.map_container, mapfragnent).commit();
        mapfragnent.getMapAsync(this);
        if (flag) {
            main_content.setVisibility(View.GONE);
            iv_search.setVisibility(View.GONE);
            flag = false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect location");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().finish();
                    }
                });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });

                builder.show();
            }
        }
        iv_searchh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("clicking ivsearch");
                Gone(layout_search);
                Visibility(iv_search);
            }
        });
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rel_bar_click:
                BarClick();
                break;
            case R.id.imageView2:
                ProfileClick();
                break;
            case R.id.imageView3:
                HistoryClick();
                break;
            case R.id.imageView:
                FavouriteClick();
                break;
            case R.id.iv_search:
                Search(view);
                break;
            case R.id.iv_go:
                Go();
                break;
            case R.id.layout_filter:
                Filter();
                break;
        }
    }


    @OnClick(R.id.iv_bar)
    public void BarClick() {
        if (main_content.getVisibility() == View.VISIBLE) {
            main_content.setVisibility(View.GONE);
        } else if (main_content.getVisibility() == View.GONE) {
            main_content.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.imageView2)
    public void ProfileClick() {

        System.out.println("profile clicked" + type);
        //main_content.setVisibility(View.GONE);
        if (type == null || type.equalsIgnoreCase("")) {
            Choice ch = new Choice();
            Bundle bundle = new Bundle();
            bundle.putString("from", "login");
            ch.setArguments(bundle);
            ((MainActivity) getActivity()).FragmentTransactions(ch, "choice");
        } else {
            ((DashboardActivity) getActivity()).getSlide_drawer().openDrawer(DashboardActivity.mDrawerPane);//openRightSide();
        }

    }

    @OnClick(R.id.imageView3)
    public void HistoryClick() {
        main_content.setVisibility(View.GONE);
        if (type == null || type.equalsIgnoreCase("")) {
            Choice ch = new Choice();
            Bundle bundle = new Bundle();
            bundle.putString("from", "login");
            ch.setArguments(bundle);
            ((MainActivity) getActivity()).FragmentTransactions(ch, "choice");
        } else {
            ((DashboardActivity) getActivity()).getSlide_drawer().closeDrawer(DashboardActivity.mDrawerPane);
        }
    }

    @OnClick(R.id.imageView)
    public void FavouriteClick() {
        main_content.setVisibility(View.GONE);
        if (type == null || type.equalsIgnoreCase("")) {
            Choice ch = new Choice();
            Bundle bundle = new Bundle();
            bundle.putString("from", "login");
            ch.setArguments(bundle);
            ((MainActivity) getActivity()).FragmentTransactions(ch, "choice");
        } else {
            ((DashboardActivity) getActivity()).getSlide_drawer().closeDrawer(DashboardActivity.mDrawerPane);
            ((DashboardActivity) getActivity()).FragmentTransactions(new Favourites(), "favourite");

        }
    }

    @OnClick(R.id.iv_search)
    public void Search(View v) {
        System.out.println("inside search");
        et_search.setText("");
        Visibility(layout_search);
        Gone(iv_search);

        /*
        iv_search.post(new Runnable() {
            @Override
            public void run() {
                layout_search.setVisibility(View.VISIBLE);
                layout_search.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down));
                iv_search.setVisibility(View.GONE);
            }
        });
        */
    }

    @OnClick(R.id.iv_go)
    public void Go() {
        if (et_search.getText().toString().trim().length() > 0) {
            Utils.HideKeyboard(getActivity());
            SearchDiscount();
        } else {
            Utils.ShowSnackBar(getActivity(), "please enter text to search discount", SnackbarType.MULTI_LINE);
        }
    }

    @OnClick(R.id.iv_filter)
    public void Filter() {
        main_content.setVisibility(View.GONE);
        Intent jump = new Intent(getActivity(), CategoriesPop.class);
        startActivityForResult(jump, 1);
        //ShowCategories();
    }


    //@OnClick(R.id.iv_searchh)
    public void SearchMagnifier(View view) {
        System.out.println("inside search magnifier");

        /*layout_search.post(new Runnable() {
            @Override
            public void run() {
                iv_search.setVisibility(View.VISIBLE);

                layout_search.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up));
                layout_search.setVisibility(View.GONE);
            }
        });
        */
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
            if (mGoogleApiClient.isConnected()) {
                startLocationUpdates();
            }
        }
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);
                System.out.println("disconnected successfully");
                mGoogleApiClient.disconnect();
            }

        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        prefs.edit().putBoolean("locationfetch", true).commit();
        Progressdialog.DismissDialog();
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        System.out.println("location changed");
        System.out.println("Loc: " + location);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        prefs.edit().putBoolean("locationfetch", true).commit();
        Progressdialog.DismissDialog();
    }

    protected void startLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            if (mLocationRequest != null) {
                System.out.println("start location updates");
                PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
            }

        }
    }

    private void updateUI() {
        //Progressdialog.DismissDialog();
        RotateProgressDialog.DismissDialog();
        prefs.edit().putBoolean("locationfetch", true).commit();
        System.out.println("value of location on after loaded" + prefs.getBoolean("locationfetch", false));

        if (null != mCurrentLocation && mGoogleApiClient.isConnected()) {
            LatLng latlng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            if (gmap != null) {
                gmap.clear();
                gmap.addMarker(new MarkerOptions().position(latlng).title(latlng.latitude + "," + latlng.longitude)).hideInfoWindow();
                gmap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                gmap.animateCamera(CameraUpdateFactory.zoomTo(12));
                gmap.getUiSettings().setIndoorLevelPickerEnabled(true);
                gmap.setIndoorEnabled(true);

            }
        } else {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }
            if (mCurrentLocation != null && mGoogleApiClient.isConnected()) {
                LatLng latlng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                if (gmap != null) {
                    gmap.clear();
                    gmap.addMarker(new MarkerOptions().position(latlng).title(latlng.latitude + "," + latlng.longitude)).hideInfoWindow();
                    gmap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                    gmap.animateCamera(CameraUpdateFactory.zoomTo(12));
                    gmap.getUiSettings().setIndoorLevelPickerEnabled(true);
                    gmap.setIndoorEnabled(true);
                }

            } else {
                Utils.ShowSnackBar(getActivity(), "Unable to fetch location", SnackbarType.SINGLE_LINE);
            }
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setOnMapLoadedCallback(this);
        gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gmap.setBuildingsEnabled(true);
        gmap.getUiSettings().setZoomControlsEnabled(false);
        gmap.getUiSettings().setMyLocationButtonEnabled(true);
        gmap.getUiSettings().setMapToolbarEnabled(false);
        gmap.setIndoorEnabled(true);
        gmap.setMyLocationEnabled(true);
        gmap.getUiSettings().setIndoorLevelPickerEnabled(true);
        gmap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.hideInfoWindow();
                LatLng latlng = marker.getPosition();
                System.out.println("latitude" + latlng.latitude);
                System.out.println("longitude" + latlng.longitude);
                final ArrayList<DiscountInfo> list = new ArrayList<DiscountInfo>();
                for (DiscountInfo info : parseObjectList) {
                    Double latitude = info.getLatitude();
                    Double longitude = info.getLongitude();
                    System.out.println("latitudee value" + latitude);
                    System.out.println("longitudee value" + longitude);
                    if (latlng.latitude == latitude && latlng.longitude == longitude) {

                        list.add(info);
                    }
                }
                System.out.println("size is" + list.size());

                if (list.size() > 0) {
                    ShowCategories(list);
                }


                return true;
            }
        });
        gmap.clear();
    }


    @Override
    public void onMapLoaded() {

        if (!prefs.getBoolean("locationfetch", false)) {
            //Progressdialog.ShowDialog(getActivity());
            RotateProgressDialog.ShowDialog(getActivity());
            updateUI();
        }
        FetchDiscounts();

    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
        System.out.println("inside on pause");

    }
/*
    public void ShowCategories() {
        dialog = new Dialog(getActivity(), R.style.DialogTheme);
        if (!dialog.isShowing()) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View dialoglayout = inflater.inflate(R.layout.dialog_categories, null);
            dialog.setContentView(dialoglayout);
            TextView tv_screenname = (TextView) dialoglayout.findViewById(R.id.tv_screenname);
            tv_screenname.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            tv_screenname.setText(getString(R.string.categories));
            CardView view_right = (CardView) dialoglayout.findViewById(R.id.view_right);
            view_right.setVisibility(View.GONE);
            final ExpandableListView elv_categories = (ExpandableListView) dialoglayout.findViewById(R.id.elv_categories);
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
            elv_categories.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                @Override
                public void onGroupExpand(int groupPosition) {
                    if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                        elv_categories.collapseGroup(lastExpandedPosition);
                    }
                    lastExpandedPosition = groupPosition;
                }
            });

            elv_categories.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                    String st = headerlist.get(i).getState();
                    if (st.equalsIgnoreCase("Selected")) {
                        headerlist.get(i).setState("Unselect");
                    } else {
                        headerlist.get(i).setState("Selected");
                    }
                    adapter.notifyDataSetChanged();
                    return false;
                }
            });
            Fetch_Categories(elv_categories, dialoglayout);
        }
    }

    public void Fetch_Categories(final ExpandableListView expandableListView, final View view) {
        if (Utils.checkInternetConnection(getActivity())) {
            Progressdialog.ShowDialog(getActivity());
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(CommonUtils.CATEGORIES);
            query.orderByAscending("name");
            query.include("sub_category");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    Progressdialog.DismissDialog();
                    if (e == null) {
                        if (list.size() == 0) {
                            Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, getString(R.string.no_category));
                        } else if (list.size() > 0) {
                            headerlist.clear();
                            maplist.clear();
                            for (int i = 0; i < list.size(); i++) {
                                ParseObject categories = list.get(i);
                                ParseObject sub_categries = categories.getParseObject("sub_category");
                                JSONArray sub_categories = sub_categries.getJSONArray("sub_categories");
                                final HeaderModel model = new HeaderModel();
                                System.out.println("category_name" + categories.get("name").toString());
                                model.setCategoryname(categories.get("name").toString());
                                ParseFile photo = categories.getParseFile("category_image");
                                String url = photo.getUrl();
                                ParseFile photo2 = categories.getParseFile("category_image2");
                                String url2 = photo2.getUrl();
                                System.out.println("url" + url);
                                model.setUrl(url);
                                model.setUrl2(url2);
                                model.setState("Unselect");
                                headerlist.add(model);
                                List<ChildModel> childlist = new ArrayList<ChildModel>();

                                for (int x = 0; x < sub_categories.length(); x++) {
                                    try {
                                        System.out.println("sub_categories" + sub_categories.getString(x));
                                        ChildModel childmodel = new ChildModel();
                                        childmodel.setState(false);
                                        childmodel.setSub_category_name(sub_categories.getString(x));
                                        childlist.add(childmodel);
                                    } catch (Exception ee) {
                                        ee.printStackTrace();
                                    }

                                }
                                maplist.put(model, childlist);
                            }
                            adapter = new CategoriesAdapter(getActivity(), headerlist, maplist);
                            expandableListView.setAdapter(adapter);
                        }
                    } else {
                        Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, e.getMessage());
                    }
                }
            });
        } else {
            Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, getString(R.string.network_error));
        }


    }
    */

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createLocationRequest();
                    startLocationUpdates();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover location");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getActivity().finish();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return;
            }
        }
    }

    public void FetchDiscounts() {
        if (Utils.checkInternetConnection(getActivity())) {
            RotateProgressDialog.ShowDialog(getActivity());
            discounts = new ParseQuery<ParseObject>(CommonUtils.DISCOUNT_TABLE);
            discounts.orderByAscending("discount_category");
            discounts.findInBackground(new FindCallback<ParseObject>() {
                                           @Override
                                           public void done(List<ParseObject> list, ParseException e) {
                                               RotateProgressDialog.DismissDialog();
                                               if (e == null) {
                                                   if (list.size() == 0) {
                                                       Utils.ShowSnackBar(getActivity(), "No discount yet", SnackbarType.SINGLE_LINE);
                                                   } else {
                                                       String lat = null;
                                                       String lng = null;
                                                       parseObjectList.clear();
                                                       positions.clear();
                                                       for (ParseObject object : list) {
                                                           String start_date = object.get("startdate").toString();
                                                           String end_date = object.get("enddate").toString();
                                                           System.out.println("start date" + start_date);
                                                           System.out.println("end date" + end_date);
                                                           String discount = object.get("discount_per").toString();
                                                           String latitude = String.valueOf(object.getDouble("latitude"));
                                                           String longitude = String.valueOf(object.getDouble("longitude"));
                                                           String currentddate = Utils.getCurrentDate();
                                                           int startday = Integer.parseInt(start_date.split("/")[0]);
                                                           int startmonth = Integer.parseInt(start_date.split("/")[1]);
                                                           int startyear = Integer.parseInt(start_date.split("/")[2]);
                                                           LocalDate start_datee = new LocalDate(startyear, startmonth, startday);
                                                           int endday = Integer.parseInt(end_date.split("/")[0]);
                                                           int endmonth = Integer.parseInt(end_date.split("/")[1]);
                                                           int endyear = Integer.parseInt(end_date.split("/")[2]);
                                                           LocalDate end_datee = new LocalDate(endyear, endmonth, endday);
                                                           int currentday = Integer.parseInt(currentddate.split("/")[0]);
                                                           int currentmonth = Integer.parseInt(currentddate.split("/")[1]);
                                                           int currentyear = Integer.parseInt(currentddate.split("/")[2]);
                                                           LocalDate current_date = new LocalDate(currentyear, currentmonth, currentday);
                                                           BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_discount_icon);
                                                           switch (discount) {
                                                               case "5%-10%":
                                                                   bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_five_ten);
                                                                   break;
                                                               case "11%-25%":
                                                                   bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_eleven_twentyfive);
                                                                   break;
                                                               case "26%-40%":
                                                                   bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_twentysix_forty);
                                                                   break;
                                                               case "41%-50%":
                                                                   bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_fortyone_fifty);
                                                                   break;
                                                               case "51%-60%":
                                                                   bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_fiftyone_sixty);
                                                                   break;
                                                               case "free, BuyOne-GetOne":
                                                                   bitmap = BitmapDescriptorFactory.fromResource(R.drawable.white_flag);
                                                                   break;
                                                               default:
                                                                   break;
                                                           }
                                                           if (current_date.isBefore(start_datee)) {
                                                               lat = latitude;
                                                               lng = longitude;
                                                               System.out.println("current date before start");
                                                               System.out.println("latitude value" + lat);
                                                               System.out.println("longitude" + lng);
                                                               if (positions.contains(lat + "," + lng)) {
                                                                   System.out.println("yes laready contains");
                                                                   latt = Double.valueOf(lat) + latitude_dis_add;
                                                                   longit = Double.valueOf(lng) + longitude_dis_add;
                                                                   if (positions.contains(latt + "," + longit)) {
                                                                       latt = Double.valueOf(lat) + latitude_dis_add * 10;
                                                                       longit = Double.valueOf(lng) + longitude_dis_add * 10;
                                                                   }
                                                                   positions.add(latt + "," + longit);
                                                                   BitmapDescriptor bitmapp = BitmapDescriptorFactory.fromResource(R.drawable.ic_multiple);
                                                                   gmap.addMarker(new MarkerOptions().position(new LatLng(latt, longit)).icon(bitmap));
                                                               } else {
                                                                   latt = Double.valueOf(lat);
                                                                   longit = Double.valueOf(lng);
                                                                   positions.add(lat + "," + lng);
                                                                   gmap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(lat), Double.valueOf(lng))).icon(bitmap));
                                                               }
                                                               DiscountInfo info = new DiscountInfo();
                                                               info.setDiscount_category(object.get("discount_category").toString());
                                                               info.setDiscount_per(object.get("discount_per").toString());
                                                               info.setDiscount_location(object.get("loc").toString());
                                                               info.setLatitude(latt);
                                                               info.setLongitude(longit);
                                                               info.setDiscount_category_url(object.get("discount_category_url").toString());
                                                               info.setDiscount_sub_category(object.get("discount_sub_category").toString());
                                                               info.setDiscount_desc(object.get("discount_desc").toString());
                                                               info.setStartdate(object.get("startdate").toString());
                                                               info.setPhonenumber(object.get("phone").toString());
                                                               info.setEnddate(object.get("enddate").toString());
                                                               parseObjectList.add(info);
                                                               System.out.println("discount desc" + object.get("discount_desc").toString() + "," + latt + "," + longit);
                                                           } else if (current_date.isAfter(start_datee) && (current_date.isBefore(end_datee)
                                                                   || current_date.isEqual(end_datee))) {
                                                               lat = latitude;
                                                               lng = longitude;
                                                               System.out.println("current date after start");
                                                               System.out.println("latitude value" + lat);
                                                               System.out.println("longitude" + lng);
                                                               if (positions.contains(lat + "," + lng)) {
                                                                   latt = Double.valueOf(lat) + latitude_dis_add;
                                                                   longit = Double.valueOf(lng) + longitude_dis_add;
                                                                   if (positions.contains(latt + "," + longit)) {
                                                                       latt = Double.valueOf(lat) + latitude_dis_add * 10;
                                                                       longit = Double.valueOf(lng) + longitude_dis_add * 10;
                                                                   }
                                                                   positions.add(latt + "," + longit);
                                                                   BitmapDescriptor bitmapp = BitmapDescriptorFactory.fromResource(R.drawable.ic_multiple);
                                                                   gmap.addMarker(new MarkerOptions().position(new LatLng(latt, longit)).icon(bitmap));
                                                               } else {
                                                                   latt = Double.valueOf(lat);
                                                                   longit = Double.valueOf(lng);
                                                                   positions.add(lat + "," + lng);
                                                                   gmap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(lat), Double.valueOf(lng))).icon(bitmap));
                                                               }
                                                               DiscountInfo info = new DiscountInfo();
                                                               info.setDiscount_category(object.get("discount_category").toString());
                                                               info.setDiscount_per(object.get("discount_per").toString());
                                                               info.setDiscount_location(object.get("loc").toString());
                                                               info.setLatitude(latt);
                                                               info.setLongitude(longit);
                                                               info.setDiscount_category_url(object.get("discount_category_url").toString());
                                                               info.setDiscount_sub_category(object.get("discount_sub_category").toString());
                                                               info.setDiscount_desc(object.get("discount_desc").toString());
                                                               info.setStartdate(object.get("startdate").toString());
                                                               info.setPhonenumber(object.get("phone").toString());
                                                               info.setEnddate(object.get("enddate").toString());
                                                               parseObjectList.add(info);
                                                               System.out.println("discount desc" + object.get("discount_desc").toString() + "," + latt + "," + longit);
                                                           } else if (current_date.isEqual(start_datee) && (current_date.isEqual(end_datee) || current_date.isBefore(end_datee))) {
                                                               lat = latitude;
                                                               lng = longitude;
                                                               System.out.println("current date equal start");
                                                               System.out.println("latitude value" + lat);
                                                               System.out.println("longitude" + lng);
                                                               if (positions.contains(lat + "," + lng)) {
                                                                   latt = Double.valueOf(lat) + latitude_dis_add;
                                                                   longit = Double.valueOf(lng) + longitude_dis_add;
                                                                   if (positions.contains(latt + "," + longit)) {
                                                                       latt = Double.valueOf(lat) + latitude_dis_add * 10;
                                                                       longit = Double.valueOf(lng) + longitude_dis_add * 10;
                                                                   }
                                                                   positions.add(latt + "," + longit);
                                                                   BitmapDescriptor bitmapp = BitmapDescriptorFactory.fromResource(R.drawable.ic_multiple);
                                                                   gmap.addMarker(new MarkerOptions().position(new LatLng(latt, longit)).icon(bitmap));
                                                               } else {
                                                                   latt = Double.valueOf(lat);
                                                                   longit = Double.valueOf(lng);
                                                                   positions.add(lat + "," + lng);
                                                                   gmap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(lat), Double.valueOf(lng))).icon(bitmap));
                                                               }
                                                               DiscountInfo info = new DiscountInfo();
                                                               info.setDiscount_category(object.get("discount_category").toString());
                                                               info.setDiscount_per(object.get("discount_per").toString());
                                                               info.setDiscount_location(object.get("loc").toString());
                                                               info.setLatitude(latt);
                                                               info.setLongitude(longit);
                                                               info.setDiscount_category_url(object.get("discount_category_url").toString());
                                                               info.setDiscount_sub_category(object.get("discount_sub_category").toString());
                                                               info.setDiscount_desc(object.get("discount_desc").toString());
                                                               info.setStartdate(object.get("startdate").toString());
                                                               info.setPhonenumber(object.get("phone").toString());
                                                               info.setEnddate(object.get("enddate").toString());
                                                               parseObjectList.add(info);
                                                               System.out.println("discount desc" + object.get("discount_desc").toString() + "," + latt + "," + longit);
                                                           }


                                                       }
                                                       if (parseObjectList.size() == 0) {
                                                           Utils.ShowSnackBar(getActivity(), "No Discounts available", SnackbarType.MULTI_LINE);
                                                       } else {
                                                           System.out.println("size of list" + list.size());
                                                           for (DiscountInfo inff : parseObjectList) {
                                                               System.out.println("info value" + inff.getLatitude());
                                                           }
                                                           gmap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.valueOf(lat), Double.valueOf(lng))));
                                                           gmap.animateCamera(CameraUpdateFactory.zoomTo(12));
                                                       }
                                                   }
                                               } else {
                                                   Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, e.getMessage());
                                               }
                                           }
                                       }

            );
        } else {
            Utils.ShowSnackBar(getActivity(), getString(R.string.network_error), SnackbarType.SINGLE_LINE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        System.out.println("inside on activity result");
        if (data != null) {
            if (requestCode == 1) {
                if (resultCode == Activity.RESULT_OK) {
                    categoryname = data.getStringExtra("categoryname");
                    String url = data.getStringExtra("url");
                    System.out.println("category name" + categoryname);
                    System.out.println("category url" + url);
                    Picasso.with(getActivity()).load(url).into(iv_filter);
                    Intent jump = new Intent(getActivity(), CategoryItem.class);
                    startActivityForResult(jump, 2);
                }
            }
            if (requestCode == 2) {
                if (resultCode == Activity.RESULT_OK) {
                    subcategory = data.getStringExtra("subcategory");
                    System.out.println("sub category" + subcategory);
                }
            }
        }
    }


    public void SearchDiscount() {
        if (Utils.checkInternetConnection(getActivity())) {
            if (parseObjectList.size() > 0) {
                for (DiscountInfo info : parseObjectList) {
                    if (info.getDiscount_category().equalsIgnoreCase(categoryname) && info.getDiscount_sub_category().equalsIgnoreCase(subcategory) && info.getDiscount_desc().contains(et_search.getText().toString().trim())) {
                        System.out.println("inside found");
                        gmap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(info.getLatitude(), info.getLongitude())));
                    }
                }
            }
        } else {
            Utils.ShowSnackBar(getActivity(), getString(R.string.network_error), SnackbarType.SINGLE_LINE);
        }
    }

    public void ShowCategories(final ArrayList<DiscountInfo> list) {
        dialog = new Dialog(getActivity(), R.style.DialogTheme);
        if (!dialog.isShowing()) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            final View dialoglayout = inflater.inflate(R.layout.layout_discount_info, null);
            dialog.setContentView(dialoglayout);
            ListView lv_discountinfo = (ListView) dialoglayout.findViewById(R.id.lv_discountinfo);
            TextView tv_location = (TextView) dialoglayout.findViewById(R.id.tv_location);
            ImageView iv_cancel = (ImageView) dialoglayout.findViewById(R.id.iv_cancel);
            dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_SmileWindow;
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.show();
            tv_location.setText(list.get(0).getDiscount_location());
            MyDiscountWindowAdapter adapter = new MyDiscountWindowAdapter(getActivity(), list, dialog, LocationScreen.this);
            lv_discountinfo.setAdapter(adapter);
            iv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }
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
                Utils.ShowSnackBar(getActivity(), "result not found", SnackbarType.SINGLE_LINE);
            }


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            RotateProgressDialog.ShowDialog(getActivity());
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
            if (line != null) {
                line.remove();
            }
            line = gmap.addPolyline(lineOptions);
            RotateProgressDialog.DismissDialog();
        }
    }

    public void AddPolyline(PolylineOptions lineOptions) {
        if (line != null) {
            line.remove();
        }
        line = gmap.addPolyline(lineOptions);

    }
    public void Visibility(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation slide = new TranslateAnimation(0,0,-100,0);
        slide.setDuration(700);
        slide.setFillAfter(true);
        view.startAnimation(slide);
    }

    public void Gone(View view){
        view.setVisibility(View.GONE);
        TranslateAnimation slidee = new TranslateAnimation(0,0,0,-100);
        slidee.setDuration(700);
        slidee.setFillBefore(true);
        view.startAnimation(slidee);

    }

}
