package fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapmap.R;
import com.nispok.snackbar.enums.SnackbarType;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.SaveCallback;

import org.joda.time.LocalDate;
import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import activities.DashboardActivity;
import adapters.DiscountAdapter;
import adapters.LocationsAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import commonUtils.Utils;
import datamodel.LocationInfo;
import parsework.CommonUtils;
import views.DatePickerDialog;
import views.Effectstype;
import views.NiftyDialogBuilder;
import views.Progressdialog;
import views.RotateProgressDialog;

/**
 * Created by osx on 23/10/15.
 */
public class AddDiscount extends Fragment implements DatePickerDialog.OnDateSetListener {

    View view = null;
    @Bind(R.id.tv_discount_desc)
    TextView tv_discount_desc;

    @Bind(R.id.tv_disc_dur)
    TextView tv_disc_dur;

    @Bind(R.id.tv_disc_per)
    TextView tv_disc_per;

    @Bind(R.id.tv_sub_category_head)
    TextView tv_sub_category_head;


    @Bind(R.id.tv_start_date)
    TextView tv_start_date;

    @Bind(R.id.tv_end_date)
    TextView tv_end_date;

    @Bind(R.id.tv_location_head)
    TextView tv_location_head;

    @Bind(R.id.tv_location)
    TextView tv_location;

    @Bind(R.id.et_discount_desc)
    EditText et_discount_desc;

    @Bind(R.id.tv_discount)
    TextView tv_discount;

    @Bind(R.id.tv_screenname)
    TextView tv_screenname;

    @Bind(R.id.btn_cancel)
    Button btn_cancel;

    @Bind(R.id.btn_add)
    Button btn_add;

    @Bind(R.id.tv_sub_category)
    TextView tv_sub_category;

    @Bind(R.id.iv_back)
    ImageView iv_back;

    @Bind(R.id.iv_flag)
    ImageView iv_flag;

    @Bind(R.id.view_right)
    CardView view_right;

    @Bind(R.id.layout_sel_per)
    RelativeLayout layout_sel_per;

    @Bind(R.id.layout_sel_subcat)
    RelativeLayout layout_sel_subcat;

    @Bind(R.id.layout_sel_cat)
    RelativeLayout layout_sel_cat;

    @Bind(R.id.layout_sel_startdate)
    RelativeLayout layout_sel_startdate;

    @Bind(R.id.layout_sel_enddate)
    RelativeLayout layout_end_date;

    @Bind(R.id.tv_category)
    TextView tv_category;

    String phone;

    //ParseObject object;
    SharedPreferences prefs;
    String category_name;
    String locname;
    int pos;

    Dialog dialog;
    NiftyDialogBuilder dialogBuilder;

    List<String> subcategories = new ArrayList<>();
    List<LocationInfo> names = new ArrayList<>();
    DatePickerDialog datePickerDialog;
    Double latitude, longitude;
    ParseQuery<ParseObject> query;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_discount, container, false);
        ButterKnife.bind(this, view);
        tv_screenname.setText(getString(R.string.add_discountt));
        view_right.setVisibility(View.GONE);
        tv_discount_desc.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-ITALIC.TTF"));
        tv_disc_dur.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-ITALIC.TTF"));
        tv_disc_per.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-ITALIC.TTF"));
        tv_end_date.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-ITALIC.TTF"));
        tv_start_date.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-ITALIC.TTF"));
        tv_sub_category_head.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-ITALIC.TTF"));
        tv_location_head.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-ITALIC.TTF"));
        tv_screenname.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
        et_discount_desc.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
        tv_discount.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
        tv_sub_category.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
        tv_location.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
        prefs = getActivity().getSharedPreferences("UserData", 0);
        System.out.println("category name"+prefs.getString("category_name", ""));
        if (prefs.getString("category_name", "").equalsIgnoreCase("")) {
            Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, getString(R.string.sel_cat));
        } else {
            category_name = prefs.getString("category_name", "");
            System.out.println("category name" + category_name);
            FetchLocations();
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btn_cancel)
    public void Cancel() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.btn_add)
    public void add() {
        try {
            Utils.HideKeyboard(getActivity());
            if (category_name.equalsIgnoreCase("")) {
                Utils.ShowSnackBar(getActivity(), getString(R.string.sel_cat), SnackbarType.SINGLE_LINE);
            } else if (TextUtils.isEmpty(et_discount_desc.getText().toString().trim())) {
                Utils.ShowSnackBar(getActivity(), getString(R.string.dis_dec_error), SnackbarType.MULTI_LINE);
            } else if (TextUtils.isEmpty(tv_disc_per.getText().toString().trim())) {
                Utils.ShowSnackBar(getActivity(), getString(R.string.sel_dis_per), SnackbarType.SINGLE_LINE);
            } else if (TextUtils.isEmpty(tv_sub_category.getText().toString().trim())) {
                Utils.ShowSnackBar(getActivity(), getString(R.string.sel_sub_cat), SnackbarType.SINGLE_LINE);
            } else if (TextUtils.isEmpty(tv_start_date.getText().toString().trim())) {
                Utils.ShowSnackBar(getActivity(), getString(R.string.sel_start_date), SnackbarType.SINGLE_LINE);
            } else if (TextUtils.isEmpty(tv_end_date.getText().toString().trim())) {
                Utils.ShowSnackBar(getActivity(), getString(R.string.sel_end_date), SnackbarType.SINGLE_LINE);
            } else if (TextUtils.isEmpty(tv_location.getText().toString().trim())) {
                Utils.ShowSnackBar(getActivity(), getString(R.string.sel_loc_err), SnackbarType.SINGLE_LINE);
            } else {
                if (Utils.checkInternetConnection(getActivity())) {
                    RotateProgressDialog.ShowDialog(getActivity());
                    String profileobjectid = prefs.getString("profileobjectid", "");
                    final ParseObject profile = ParseObject.createWithoutData(CommonUtils.SHOPKEEPER_PROFILE_DATA, profileobjectid);
                    query = new ParseQuery<ParseObject>(CommonUtils.DISCOUNT_TABLE);
                    query.whereEqualTo(CommonUtils.DISCOUNT_TABLE, profile);
                    query.countInBackground(new CountCallback() {
                        @Override
                        public void done(int i, ParseException ee) {
                            if(ee==null){
                                if(i<2){
                                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                                    ParseObject discount = new ParseObject(CommonUtils.DISCOUNT_TABLE);
                                    discount.put("discount_desc", et_discount_desc.getText().toString());
                                    discount.put("discount_per", tv_discount.getText().toString());
                                    discount.put("discount_category", tv_category.getText().toString().trim());
                                    discount.put("discount_sub_category", tv_sub_category.getText().toString());
                                    discount.put("discount_category_url", prefs.getString("category_url", ""));
                                    discount.put("loc", locname);
                                    discount.put("phone",phone);
                                    discount.put("latitude",latitude);
                                    discount.put("longitude",longitude);
                                    discount.put("startdate", tv_start_date.getText().toString());
                                    discount.put("enddate", tv_end_date.getText().toString());
                                    ParseRelation<ParseObject> relation = discount.getRelation(CommonUtils.DISCOUNT_TABLE);
                                    relation.add(profile);

                                    discount.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            RotateProgressDialog.DismissDialog();
                                            if (e == null) {
                                                Utils.ShowSnackBar(getActivity(), getString(R.string.dis_add_succ), SnackbarType.SINGLE_LINE);
                                                StringBuilder builder = new StringBuilder();
                                                builder.append(getString(R.string.category) + ":  " + category_name + "\n");
                                                builder.append(getString(R.string.sub_category) + ":  " + tv_sub_category.getText().toString() + "\n");
                                                builder.append(getString(R.string.discount_per) + ":  " + tv_discount.getText().toString() + "\n");
                                                builder.append(getString(R.string.discount_desc) + ":  " + et_discount_desc.getText().toString() + "\n");
                                                builder.append(getString(R.string.start_date) + ": " + tv_start_date.getText().toString() + "\n");
                                                builder.append(getString(R.string.end_date) + ": " + tv_end_date.getText().toString() + "\n");
                                                builder.append(getString(R.string.dis_loc) + ": " + tv_location.getText().toString());
                                                Share(getActivity(), Effectstype.SlideBottom, builder.toString());
                                            } else {
                                                Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, e.getMessage());
                                            }
                                        }
                                    });
                                }
                                else{
                                    RotateProgressDialog.DismissDialog();
                                    Utils.ShowSnackBar(getActivity(),"Can only add 2 discounts",SnackbarType.SINGLE_LINE);
                                }
                            }
                            else{
                                RotateProgressDialog.DismissDialog();
                                Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, ee.getMessage());
                            }
                        }
                    });

                } else {
                    Utils.ShowSnackBar(getActivity(), getString(R.string.network_error), SnackbarType.SINGLE_LINE);
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.iv_back)
    public void Back() {
        getActivity().onBackPressed();
    }

    public void Share(Context context, Effectstype effect, final String text) {
        if (dialogBuilder == null || !dialogBuilder.isShowing()) {
            dialogBuilder = NiftyDialogBuilder.getInstance(context);
            dialogBuilder.withEffect(effect);
            dialogBuilder.withDialogColor(ContextCompat.getColor(getActivity(), R.color.White));
            dialogBuilder.withDividerColor(ContextCompat.getColor(getActivity(), android.R.color.transparent));
            dialogBuilder.setTopPanelBackground(ContextCompat.getColor(getActivity(), android.R.color.transparent));
            dialogBuilder.setParentPanelBackground(ContextCompat.getColor(getActivity(), android.R.color.transparent));
            dialogBuilder.setCustomView(R.layout.confirmation_dialog_view, (Activity) context);
            TextView tv_title = (TextView) dialogBuilder.findViewById(R.id.tv_title);
            tv_title.setText(getString(R.string.share));
            tv_title.setTypeface(Utils.SetFont(context, "ROBOTO-REGULAR.TTF"));
            TextView tv_message = (TextView) dialogBuilder.findViewById(R.id.tv_message);
            tv_message.setTypeface(Utils.SetFont(context, "ROBOTO-REGULAR.TTF"));
            tv_message.setText(getString(R.string.share_confirm));
            Button btn_yes = (Button) dialogBuilder.findViewById(R.id.btn_yes);
            btn_yes.setTypeface(Utils.SetFont(context, "ROBOTO-REGULAR.TTF"));
            btn_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogBuilder.dismiss();
                    FragmentManager fg = ((DashboardActivity) getActivity()).getFragmentManager();
                    for (int i = 0; i < fg.getBackStackEntryCount(); i++) {
                        fg.popBackStackImmediate();
                    }
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, text);
                    sendIntent.setType("text/plain");
                    startActivityForResult(sendIntent, 1);
                }
            });
            Button btn_no = (Button) dialogBuilder.findViewById(R.id.btn_no);
            btn_no.setTypeface(Utils.SetFont(context, "ROBOTO-REGULAR.TTF"));
            btn_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogBuilder.dismiss();
                    getActivity().onBackPressed();
                }
            });
            dialogBuilder.show();
        }


    }

    public void getSubCategories() {
        if (Utils.checkInternetConnection(getActivity())) {
            RotateProgressDialog.ShowDialog(getActivity());
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(CommonUtils.CATEGORIES);
            query.whereEqualTo("name", tv_category.getText().toString().trim());
            query.include("sub_category");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    RotateProgressDialog.DismissDialog();
                    if (e == null) {
                        if (list.size() > 0) {
                            subcategories.clear();
                            try {
                                ParseObject sub_category = list.get(0).getParseObject("sub_category");
                                JSONArray sub_categories = sub_category.getJSONArray("sub_categories");
                                for (int i = 0; i < sub_categories.length(); i++) {
                                    String sub_cat = sub_categories.getString(i);
                                    subcategories.add(sub_cat);
                                }
                                Collections.sort(subcategories);
                                System.out.println("sub categories" + subcategories);
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                            ShowCategories();
                        } else {
                            Utils.ShowSnackBar(getActivity(), getString(R.string.subcat_fetch_error), SnackbarType.SINGLE_LINE);
                        }

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

    public void FetchLocations() {
        if (Utils.checkInternetConnection(getActivity())) {
            String profileobjectid = prefs.getString("profileobjectid", "");
            ParseQuery<ParseObject> object=new ParseQuery<ParseObject>(CommonUtils.SHOPKEEPER_PROFILE_DATA);
            object.include("mainloc");
            RotateProgressDialog.ShowDialog(getActivity());
            object.getInBackground(profileobjectid, new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if(e==null){
                        ParseObject mainloc=parseObject.getParseObject("mainloc");
                        System.out.println("mainloc" + mainloc.getString("loc"));
                        LocationInfo info=new LocationInfo();
                        info.setLatitude(mainloc.getDouble("latitude"));
                        info.setLongitude(mainloc.getDouble("longitude"));
                        info.setLocation(mainloc.getString("loc"));
                        info.setPhone(mainloc.getString("phone"));
                        info.setCategory(category_name);
                        names.add(0,info);
                        ParseQuery<ParseObject> locations = new ParseQuery<ParseObject>(CommonUtils.LOCATIONS);
                        locations.whereEqualTo("Locations", parseObject);
                        locations.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> list, ParseException e) {
                                RotateProgressDialog.DismissDialog();
                                if (e == null) {
                                    if (list.size() > 0) {
                                       for(ParseObject obj:list){
                                           LocationInfo infoo=new LocationInfo();
                                           infoo.setCategory(obj.getString("category"));
                                           infoo.setLatitude(obj.getDouble("latitude"));
                                           infoo.setLongitude(obj.getDouble("longitude"));
                                           infoo.setLocation(obj.getString("loc"));
                                           infoo.setPhone(obj.getString("phone"));
                                           names.add(infoo);
                                       }
                                        System.out.println("size list"+names.size());
                                    } else {
                                        if(names.size()==0){
                                            Utils.ShowSnackBar(getActivity(), "Please add locations in your profile", SnackbarType.MULTI_LINE);
                                        }

                                    }
                                } else {
                                    Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, e.getMessage());
                                }
                            }
                        });
                    }
                    else{
                        RotateProgressDialog.DismissDialog();
                        Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, e.getMessage());
                    }

                }
            });


        } else {
            Utils.ShowSnackBar(getActivity(), getString(R.string.network_error), SnackbarType.SINGLE_LINE);
        }
    }

    @OnClick(R.id.layout_sel_per)
    public void SelectPercentage() {
        ShowPercentage();
    }

    @OnClick(R.id.layout_sel_subcat)
    public void SelectSubCategory() {
        if(tv_category.getText().toString().trim().length()==0){
            Utils.ShowSnackBar(getActivity(), "Please select category", SnackbarType.SINGLE_LINE);
        }
        else{
            getSubCategories();
        }

    }

    @OnClick(R.id.layout_sel_cat)
    public void SelectCategory(){
        if(tv_location.getText().toString().length()==0){
            Utils.ShowSnackBar(getActivity(), "Please select location", SnackbarType.SINGLE_LINE);
        }
    }

    @OnClick(R.id.layout_sel_startdate)
    public void SelectStartDate() {
        pos = 1;
        Calendar now = Calendar.getInstance();
        if (datePickerDialog == null || !datePickerDialog.isVisible()) {
            datePickerDialog = DatePickerDialog.newInstance(this, now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.setYearRange(now.get(Calendar.YEAR), now.get(Calendar.YEAR) + 15);
            datePickerDialog.show(getFragmentManager(), "startdate");
        }

    }

    @OnClick(R.id.layout_sel_enddate)
    public void SelectEndDate() {
        if (tv_start_date.getText().toString().length() == 0) {
            Utils.ShowSnackBar(getActivity(), "Please select the start date", SnackbarType.SINGLE_LINE);
        } else {
            pos = 2;
            Calendar now = Calendar.getInstance();
            if (datePickerDialog == null || !datePickerDialog.isVisible()) {
                datePickerDialog = DatePickerDialog.newInstance(this, now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setYearRange(now.get(Calendar.YEAR), now.get(Calendar.YEAR) + 15);
                datePickerDialog.show(getFragmentManager(), "enddate");
            }
        }

    }

    @OnClick(R.id.layout_select_location)
    public void SelectLocation() {
        ShowLocations();
    }


    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        String currentDate = Utils.getCurrentDate();
        System.out.println("currentdate" + currentDate);
        String curr_date[] = currentDate.split("/");
        int curr_day = Integer.parseInt(curr_date[0]);
        int curr_month = Integer.parseInt(curr_date[1]);
        int curr_year = Integer.parseInt(curr_date[2]);
        month = month + 1;
        String month_of_year = "";
        String day_of_month = "";
        if (month < 10) {
            month_of_year = "0" + month;
        } else {
            month_of_year = month + "";
        }
        if (day < 10) {
            day_of_month = "0" + day;
        } else {
            day_of_month = day + "";
        }
        String date_Selected = day_of_month + "/" + month_of_year + "/" + year;
        String date_sel[] = date_Selected.split("/");
        int sel_day = Integer.parseInt(date_sel[0]);
        int sel_month = Integer.parseInt(date_sel[1]);
        int sel_year = Integer.parseInt(date_sel[2]);
        LocalDate cr_date = new LocalDate(curr_year, curr_month, curr_day);
        LocalDate sl_date = new LocalDate(sel_year, sel_month, sel_day);
        boolean check = sl_date.isBefore(cr_date);
        if (check) {
            Utils.ShowSnackBar(getActivity(), "selected date must be greater than or equal to current date", SnackbarType.MULTI_LINE);
        } else {
            switch (pos) {
                case 1:
                    if (tv_end_date.getText().toString().length() == 0) {
                        tv_start_date.setText(date_Selected);
                    } else {
                        String date[] = tv_end_date.getText().toString().split("/");
                        int end_day = Integer.parseInt(date[0]);
                        int end_month = Integer.parseInt(date[1]);
                        int end_year = Integer.parseInt(date[2]);
                        LocalDate end_date = new LocalDate(end_year, end_month, end_day);
                        LocalDate sel_date = new LocalDate(sel_year, sel_month, sel_day);
                        boolean date_check = sel_date.equals(end_date);
                        System.out.println("sel date" + sel_year + "/" + sel_month + "/" + sel_day);
                        System.out.println("end date" + tv_end_date.getText().toString());
                        System.out.println("date check" + date_check);
                        if (date_check) {
                            tv_start_date.setText(date_Selected);
                        } else {
                            boolean ch = sel_date.isBefore(end_date);
                            if (ch) {
                                tv_start_date.setText(date_Selected);
                            } else {
                                Utils.ShowSnackBar(getActivity(), "Starting date must be less than or equal to ending", SnackbarType.MULTI_LINE);
                            }
                        }
                    }

                    break;
                case 2:
                    String date[] = tv_start_date.getText().toString().split("/");
                    int start_day = Integer.parseInt(date[0]);
                    int start_month = Integer.parseInt(date[1]);
                    int start_year = Integer.parseInt(date[2]);
                    LocalDate start_date = new LocalDate(start_year, start_month, start_day);
                    Log.e("Month", "" + month);
                    LocalDate end_date = new LocalDate(year, month, day);
                    boolean checkk = start_date.isEqual(end_date);

                    if (checkk) {
                        tv_end_date.setText(date_Selected);
                    } else {
                        boolean enddate = start_date.isAfter(end_date);
                        if (enddate) {
                            Utils.ShowSnackBar(getActivity(), "Ending date must be greater than or equal to starting", SnackbarType.MULTI_LINE);
                        } else {
                            tv_end_date.setText(date_Selected);
                        }
                    }
                    break;
            }
        }
    }

    public void ShowCategories() {
        dialog = new Dialog(getActivity(), R.style.categorydialogtheme);
        if (!dialog.isShowing()) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            final View dialoglayout = inflater.inflate(R.layout.dialog_list_view, null);
            dialog.setContentView(dialoglayout);
            ListView lv_list = (ListView) dialoglayout.findViewById(R.id.lv_list);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, subcategories) {
                public View getView(int position, View convertView, android.view.ViewGroup parent) {
                    TextView v = (TextView) super.getView(position, convertView, parent);
                    v.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
                    v.setTextColor(ContextCompat.getColor(getActivity(), R.color.Black));
                    v.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.drawer_bg));
                    return v;
                }
            };
            lv_list.setAdapter(adapter);
            lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    dialog.dismiss();
                    String name = subcategories.get(i);
                    tv_sub_category.setText(name);
                }
            });
            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.getAttributes().windowAnimations = R.style.Animations_SmileWindow;
            window.setAttributes(wlp);
            dialog.show();
        }
    }

    public void ShowLocations() {
        dialog = new Dialog(getActivity(), R.style.categorydialogtheme);
        if (!dialog.isShowing()) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            final View dialoglayout = inflater.inflate(R.layout.dialog_list_view, null);
            dialog.setContentView(dialoglayout);
            ListView lv_list = (ListView) dialoglayout.findViewById(R.id.lv_list);
            LocationsAdapter adapter = new LocationsAdapter(getActivity(), names);
            lv_list.setAdapter(adapter);
            lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    dialog.dismiss();
                    phone=names.get(i).getPhone();
                    locname = names.get(i).getLocation();
                    latitude = names.get(i).getLatitude();
                    longitude = names.get(i).getLongitude();
                    tv_location.setText(locname);
                    tv_category.setText(names.get(i).getCategory());
                }
            });
            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.getAttributes().windowAnimations = R.style.Animations_SmileWindow;
            window.setAttributes(wlp);
            dialog.show();
        }
    }

    public void ShowPercentage() {
        final int images[] = {R.drawable.ic_five_ten,R.drawable.ic_eleven_twentyfive,R.drawable.ic_twentysix_forty,R.drawable.ic_fortyone_fifty,R.drawable.ic_fiftyone_sixty,R.drawable.white_flag};
        String discounts[] = {"5%-10%","11%-25%","26%-40%","41%-50%","51%-60%","free, BuyOne-GetOne"};
        dialog = new Dialog(getActivity(), R.style.categorydialogtheme);
        if (!dialog.isShowing()) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            final View dialoglayout = inflater.inflate(R.layout.dialog_list_view, null);
            dialog.setContentView(dialoglayout);
            ListView lv_list = (ListView) dialoglayout.findViewById(R.id.lv_list);
            DiscountAdapter adapter = new DiscountAdapter(getActivity(), discounts, images);
            lv_list.setAdapter(adapter);
            lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    dialog.dismiss();
                    String percentage = (String) adapterView.getItemAtPosition(i);
                    tv_discount.setText(percentage);
                    iv_flag.setImageResource(images[i]);
                    System.out.println("name" + percentage);
                }
            });
            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.getAttributes().windowAnimations = R.style.Animations_SmileWindow;
            window.setAttributes(wlp);
            dialog.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            ((DashboardActivity)getActivity()).FragmentTransactions(new LocationScreen(),"location");
        }
    }
}
