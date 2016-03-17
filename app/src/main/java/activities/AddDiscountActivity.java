package activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.SaveCallback;

import org.joda.time.LocalDate;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import adapters.CategoryAdapter;
import adapters.DiscountAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import commonUtils.Utils;
import datamodel.HeaderModel;
import fragments.AddDiscount;
import parsework.CommonUtils;
import views.DatePickerDialog;
import views.Effectstype;
import views.RotateProgressDialog;

/**
 * Created by osx on 02/02/16.
 */
public class AddDiscountActivity extends FragmentActivity implements DatePickerDialog.OnDateSetListener {


    @Bind(R.id.layout_sel_category)
    RelativeLayout layout_sel_category;

    @Bind(R.id.layout_sel_enddate)
    RelativeLayout layout_sel_enddate;

    @Bind(R.id.layout_select_location)
    RelativeLayout layout_select_location;


    @Bind(R.id.tv_sel_category)
    TextView tv_sel_category;


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

    @Bind(R.id.btn_add)
    Button btn_add;

    @Bind(R.id.tv_sub_category)
    TextView tv_sub_category;

    @Bind(R.id.iv_back)
    ImageView iv_back;


    @Bind(R.id.view_right)
    CardView view_right;

    @Bind(R.id.layout_sel_per)
    RelativeLayout layout_sel_per;

    @Bind(R.id.layout_sel_subcat)
    RelativeLayout layout_sel_subcat;

    @Bind(R.id.layout_sel_startdate)
    RelativeLayout layout_sel_startdate;

    @Bind(R.id.btn_skip)
    Button btn_skip;

    @Bind(R.id.iv_flag)
    ImageView iv_flag;
    Dialog dialog;
    String objectid, password, profileobjectid, email, type, category_url, category_name, category_urll,phone;
    Double latitude, longitude;
    String location;
    ArrayList<HeaderModel> categorieslist = new ArrayList<>();
    List<String> subcategories = new ArrayList<>();
    DatePickerDialog datePickerDialog;
    CategoryAdapter adapter;
    String name;
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adddiscount);
        ButterKnife.bind(this);
        tv_screenname.setText(getString(R.string.add_discountt));
        view_right.setVisibility(View.GONE);
        if (savedInstanceState == null) {
            objectid = getIntent().getStringExtra("objectid");
            password = getIntent().getStringExtra("password");
            profileobjectid = getIntent().getStringExtra("profileobjectid");
            email = getIntent().getStringExtra("email");
            type = getIntent().getStringExtra("type");
            latitude = getIntent().getDoubleExtra("latitude", 0.0);
            longitude = getIntent().getDoubleExtra("longitude", 0.0);
            category_url = getIntent().getStringExtra("category_url");
            category_name = getIntent().getStringExtra("main_category");
            location=getIntent().getStringExtra("location");
            phone=getIntent().getStringExtra("phone");
        } else {
            objectid = savedInstanceState.getString("objectid");
            password = savedInstanceState.getString("password");
            profileobjectid = savedInstanceState.getString("profileobjectid");
            email = savedInstanceState.getString("email");
            type = savedInstanceState.getString("type");
            latitude = savedInstanceState.getDouble("latitude", 0.0);
            longitude = savedInstanceState.getDouble("longitude", 0.0);
            category_url = savedInstanceState.getString("category_url");
            category_name = savedInstanceState.getString("main_category");
            location=savedInstanceState.getString("location");
            phone=savedInstanceState.getString("phone");
        }
        tv_location.setText(location);
        System.out.println("category name" + category_name);
       // tv_sel_category.setText(category_name);
       /* if (category_name != null) {
            getSubCategories();
        }
        */

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("objectid", objectid);
        outState.putString("password", password);
        outState.putString("profileobjectid", profileobjectid);
        outState.putString("email", email);
        outState.putString("main_category", category_name);
        outState.putString("category_url", category_url);
        outState.putString("type", type);
        outState.putString("phone", phone);
        outState.putString("location", location);
        outState.putDouble("latitude", latitude);
        outState.putDouble("longitude", longitude);
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.layout_sel_per)
    public void SelectPercentage() {
        ShowPercentage();
    }

    public void ShowPercentage() {
        final int images[] = {R.drawable.ic_five_ten, R.drawable.ic_eleven_twentyfive, R.drawable.ic_twentysix_forty, R.drawable.ic_fortyone_fifty, R.drawable.ic_fiftyone_sixty,R.drawable.white_flag};
        String discounts[] = {"5%-10%", "11%-25%", "26%-40%", "41%-50%", "51%-60%","free, BuyOne-GetOne"};
        dialog = new Dialog(AddDiscountActivity.this, R.style.categorydialogtheme);
        if (!dialog.isShowing()) {
            LayoutInflater inflater = LayoutInflater.from(AddDiscountActivity.this);
            final View dialoglayout = inflater.inflate(R.layout.dialog_list_view, null);
            dialog.setContentView(dialoglayout);
            ListView lv_list = (ListView) dialoglayout.findViewById(R.id.lv_list);
            DiscountAdapter adapter = new DiscountAdapter(AddDiscountActivity.this, discounts, images);
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

    @OnClick(R.id.layout_sel_category)
    public void SelectCategory() {
        System.out.println("category size"+categorieslist.size());
        if (categorieslist.size()>0) {
            ShowCategories();
        } else {
            getCategories();
        }
    }

    public void getCategories() {
        if (Utils.checkInternetConnection(AddDiscountActivity.this)) {
            RotateProgressDialog.ShowDialog(AddDiscountActivity.this);
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(CommonUtils.CATEGORIES);
            query.orderByAscending("name");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    RotateProgressDialog.DismissDialog();
                    if (e == null) {
                        if (list.size() > 0) {
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
                            ShowCategories();


                        } else {
                            Utils.ShowCustomDialog(AddDiscountActivity.this, Effectstype.SlideBottom, getString(R.string.no_category));
                        }
                    } else {
                        RotateProgressDialog.DismissDialog();
                        Utils.ShowCustomDialog(AddDiscountActivity.this, Effectstype.SlideBottom, e.getMessage());
                    }

                }
            });
        } else {
            Utils.ShowSnackBar(AddDiscountActivity.this, getString(R.string.network_error), SnackbarType.SINGLE_LINE);
        }

    }

    public void ShowCategories() {
        dialog = new Dialog(AddDiscountActivity.this, R.style.DialogTheme);
        if (!dialog.isShowing()) {
            LayoutInflater inflater = LayoutInflater.from(AddDiscountActivity.this);
            final View dialoglayout = inflater.inflate(R.layout.dialog_categories_list, null);
            dialog.setContentView(dialoglayout);
            final ListView lv_categories = (ListView) dialoglayout.findViewById(R.id.lv_categories);
            TextView tv_screenname = (TextView) dialoglayout.findViewById(R.id.tv_screenname);
            tv_screenname.setTypeface(Utils.SetFont(AddDiscountActivity.this, "ROBOTO-REGULAR.TTF"));
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
            adapter = new CategoryAdapter(AddDiscountActivity.this, categorieslist);
            lv_categories.setAdapter(adapter);
            lv_categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    dialog.dismiss();
                    HeaderModel model = categorieslist.get(position);
                    category_name = model.getCategoryname();
                    category_urll = model.getUrl();
                    tv_sel_category.setText(category_name);
                    getSubCategories();
                }
            });
        }
    }

    @OnClick(R.id.layout_sel_subcat)
    public void SelectSubCategory() {
        if (category_name != null) {
            getSubCategories();
        } else {
            Utils.ShowSnackBar(AddDiscountActivity.this, "Select category first", SnackbarType.SINGLE_LINE);
        }

    }

    public void getSubCategories() {
        if (Utils.checkInternetConnection(AddDiscountActivity.this)) {
            tv_sub_category.setText("");
            RotateProgressDialog.ShowDialog(AddDiscountActivity.this);
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(CommonUtils.CATEGORIES);
            query.whereEqualTo("name", category_name);
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
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                            ShowSubCategories();

                        } else {
                            Utils.ShowSnackBar(AddDiscountActivity.this, getString(R.string.subcat_fetch_error), SnackbarType.SINGLE_LINE);
                        }
                    } else {
                        RotateProgressDialog.DismissDialog();
                        Utils.ShowCustomDialog(AddDiscountActivity.this, Effectstype.SlideBottom, e.getMessage());
                    }
                }
            });
        } else {
            Utils.ShowSnackBar(AddDiscountActivity.this, getString(R.string.network_error), SnackbarType.SINGLE_LINE);
        }
    }

    public void ShowSubCategories() {
        dialog = new Dialog(AddDiscountActivity.this, R.style.categorydialogtheme);
        if (!dialog.isShowing()) {
            LayoutInflater inflater = LayoutInflater.from(AddDiscountActivity.this);
            final View dialoglayout = inflater.inflate(R.layout.dialog_list_view, null);
            dialog.setContentView(dialoglayout);
            ListView lv_list = (ListView) dialoglayout.findViewById(R.id.lv_list);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddDiscountActivity.this, android.R.layout.simple_list_item_1, subcategories) {
                public View getView(int position, View convertView, android.view.ViewGroup parent) {
                    TextView v = (TextView) super.getView(position, convertView, parent);
                    v.setTypeface(Utils.SetFont(AddDiscountActivity.this, "ROBOTO-REGULAR.TTF"));
                    v.setTextColor(ContextCompat.getColor(AddDiscountActivity.this, R.color.Black));
                    v.setBackgroundColor(ContextCompat.getColor(AddDiscountActivity.this,R.color.drawer_bg));
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


    @OnClick(R.id.layout_sel_startdate)
    public void SelectStartDate() {
        pos = 1;
        Calendar now = Calendar.getInstance();
        if (datePickerDialog == null || !datePickerDialog.isVisible()) {
            datePickerDialog = DatePickerDialog.newInstance(AddDiscountActivity.this, now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.setYearRange(now.get(Calendar.YEAR), now.get(Calendar.YEAR) + 15);
            datePickerDialog.show(getSupportFragmentManager(), "startdate");
        }

    }

    @OnClick(R.id.layout_sel_enddate)
    public void SelectEndDate() {
        if (tv_start_date.getText().toString().length() == 0) {
            Utils.ShowSnackBar(AddDiscountActivity.this, "Please select the start date", SnackbarType.SINGLE_LINE);
        } else {
            pos = 2;
            Calendar now = Calendar.getInstance();
            if (datePickerDialog == null || !datePickerDialog.isVisible()) {
                datePickerDialog = DatePickerDialog.newInstance(AddDiscountActivity.this, now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setYearRange(now.get(Calendar.YEAR), now.get(Calendar.YEAR) + 15);
                datePickerDialog.show(getSupportFragmentManager(), "enddate");
            }
        }

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
            Utils.ShowSnackBar(AddDiscountActivity.this, "selected date must be greater than or equal to current date", SnackbarType.MULTI_LINE);
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
                                Utils.ShowSnackBar(AddDiscountActivity.this, "Starting date must be less than or equal to ending", SnackbarType.MULTI_LINE);
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
                            Utils.ShowSnackBar(AddDiscountActivity.this, "Ending date must be greater than or equal to starting", SnackbarType.MULTI_LINE);
                        } else {
                            tv_end_date.setText(date_Selected);
                        }
                    }
                    break;
            }
        }
    }

    @OnClick(R.id.btn_add)
    public void add() {
        try {
            Utils.HideKeyboard(AddDiscountActivity.this);
            if (tv_sel_category.getText().toString().trim().length()==0) {
                Utils.ShowSnackBar(AddDiscountActivity.this, getString(R.string.sel_cat), SnackbarType.SINGLE_LINE);
            }
            else if(tv_sub_category.getText().toString().trim().length()==0){
                Utils.ShowSnackBar(AddDiscountActivity.this, "Please select subcategory", SnackbarType.SINGLE_LINE);
            }
            else if (TextUtils.isEmpty(et_discount_desc.getText().toString().trim())) {
                Utils.ShowSnackBar(AddDiscountActivity.this, getString(R.string.dis_dec_error), SnackbarType.MULTI_LINE);
            } else if (TextUtils.isEmpty(tv_disc_per.getText().toString().trim())) {
                Utils.ShowSnackBar(AddDiscountActivity.this, getString(R.string.sel_dis_per), SnackbarType.SINGLE_LINE);
            } else if (TextUtils.isEmpty(tv_start_date.getText().toString().trim())) {
                Utils.ShowSnackBar(AddDiscountActivity.this, getString(R.string.sel_start_date), SnackbarType.SINGLE_LINE);
            } else if (TextUtils.isEmpty(tv_end_date.getText().toString().trim())) {
                Utils.ShowSnackBar(AddDiscountActivity.this, getString(R.string.sel_end_date), SnackbarType.SINGLE_LINE);
            } else if (TextUtils.isEmpty(tv_location.getText().toString().trim())) {
                Utils.ShowSnackBar(AddDiscountActivity.this, getString(R.string.sel_loc_err), SnackbarType.SINGLE_LINE);
            } else {
                if (Utils.checkInternetConnection(AddDiscountActivity.this)) {
                    ParseObject discount = new ParseObject(CommonUtils.DISCOUNT_TABLE);
                    discount.put("discount_desc", et_discount_desc.getText().toString());
                    discount.put("discount_per", tv_discount.getText().toString());
                    discount.put("discount_category", tv_sel_category.getText().toString().trim());
                    discount.put("discount_sub_category", tv_sub_category.getText().toString());
                    discount.put("discount_category_url", category_urll);
                    discount.put("loc", tv_location.getText().toString());
                    discount.put("latitude", latitude);
                    discount.put("phone",phone);
                    discount.put("longitude", longitude);
                    discount.put("startdate", tv_start_date.getText().toString());
                    discount.put("enddate", tv_end_date.getText().toString());
                    ParseObject profile = ParseObject.createWithoutData(CommonUtils.SHOPKEEPER_PROFILE_DATA, profileobjectid);
                    ParseRelation<ParseObject> relation = discount.getRelation(CommonUtils.DISCOUNT_TABLE);
                    relation.add(profile);
                    RotateProgressDialog.ShowDialog(AddDiscountActivity.this);
                    discount.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            RotateProgressDialog.DismissDialog();
                            if (e == null) {
                                Utils.ShowSnackBar(AddDiscountActivity.this, getString(R.string.dis_add_succ), SnackbarType.SINGLE_LINE);
                                finish();
                                SharedPreferences prefs = getSharedPreferences("UserData", 0);
                                prefs.edit().putString("objectid", objectid).commit();
                                prefs.edit().putString("password", password).commit();
                                prefs.edit().putString("category_name", category_name).commit();
                                prefs.edit().putString("category_url", category_url).commit();
                                prefs.edit().putString("profileobjectid", profileobjectid).commit();
                                prefs.edit().putString("email", email).commit();
                                prefs.edit().putString("type", type).commit();
                                Intent intent = new Intent(AddDiscountActivity.this, DashboardActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else {
                                Utils.ShowCustomDialog(AddDiscountActivity.this, Effectstype.SlideBottom, e.getMessage());
                            }
                        }
                    });
                } else {
                    Utils.ShowSnackBar(AddDiscountActivity.this, getString(R.string.network_error), SnackbarType.SINGLE_LINE);
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.btn_skip)
    public void Skip(){
        finish();
        SharedPreferences prefs = getSharedPreferences("UserData", 0);
        prefs.edit().putString("objectid", objectid).commit();
        prefs.edit().putString("password", password).commit();
        prefs.edit().putString("category_name", category_name).commit();
        prefs.edit().putString("category_url", category_url).commit();
        prefs.edit().putString("profileobjectid", profileobjectid).commit();
        prefs.edit().putString("email", email).commit();
        prefs.edit().putString("type", type).commit();
        Intent intent = new Intent(AddDiscountActivity.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
