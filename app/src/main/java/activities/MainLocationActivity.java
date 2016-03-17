package activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.mapmap.R;
import com.nispok.snackbar.enums.SnackbarType;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import commonUtils.Utils;
import parsework.CommonUtils;
import views.Effectstype;
import views.Progressdialog;
import views.RadialPickerLayout;
import views.RotateProgressDialog;
import views.TimePickerDialog;

/**
 * Created by osx on 31/12/15.
 */
public class MainLocationActivity extends AppCompatActivity {

    String objectid, password, profileobjectid, email, type,main_category,category_url;
    int PLACE_PICKER_REQUEST = 300;
    Double latitude, longitude;
    int pos;
    @Bind(R.id.et_enter_location)
    EditText et_enter_location;

    @Bind(R.id.et_phone)
    EditText et_phone;

    @Bind(R.id.et_addtext)
    EditText et_addtext;

    @Bind(R.id.btn_submit)
    Button btn_submit;

    @Bind(R.id.tv_weekdays_from)
    TextView tv_weekdays_from;

    @Bind(R.id.tv_weekdays_to)
    TextView tv_weekdays_to;

    @Bind(R.id.ch_weekdays)
    CheckBox ch_weekdays;

    @Bind(R.id.tv_sat_from)
    TextView tv_sat_from;

    @Bind(R.id.tv_sat_to)
    TextView tv_sat_to;

    @Bind(R.id.ch_saturday)
    CheckBox ch_saturday;

    @Bind(R.id.tv_sun_from)
    TextView tv_sun_from;

    @Bind(R.id.tv_sun_to)
    TextView tv_sun_to;

    @Bind(R.id.ch_sunday)
    CheckBox ch_sunday;

    @Bind(R.id.tv_add_location)
    TextView tv_add_location;

    @Bind(R.id.tv_select_working_hours)
    TextView tv_select_working_hours;

    @Bind(R.id.tv_days)
    TextView tv_days;

    @Bind(R.id.tv_working_hours)
    TextView tv_working_hours;

    @Bind(R.id.tv_closed)
    TextView tv_closed;

    @Bind(R.id.tv_weekdays)
    TextView tv_weekdays;

    @Bind(R.id.tv_saturday)
    TextView tv_saturday;

    @Bind(R.id.tv_sunday)
    TextView tv_sunday;

    String weekdays, sat, sun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainlocation);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            objectid = getIntent().getStringExtra("objectid");
            password = getIntent().getStringExtra("password");
            profileobjectid = getIntent().getStringExtra("profileobjectid");
            email = getIntent().getStringExtra("email");
            type = getIntent().getStringExtra("type");
            main_category=getIntent().getStringExtra("category_name");
            category_url=getIntent().getStringExtra("category_url");
        } else {
            objectid = savedInstanceState.getString("objectid");
            password = savedInstanceState.getString("password");
            profileobjectid = savedInstanceState.getString("profileobjectid");
            email = savedInstanceState.getString("email");
            type = savedInstanceState.getString("type");
            main_category=savedInstanceState.getString("category_name");
            category_url=savedInstanceState.getString("category_url");
        }
        tv_add_location.setTypeface(Utils.SetFont(MainLocationActivity.this, "ROBOTO-REGULAR.TTF"));
        et_enter_location.setTypeface(Utils.SetFont(MainLocationActivity.this, "ROBOTO-REGULAR.TTF"));
        tv_select_working_hours.setTypeface(Utils.SetFont(MainLocationActivity.this, "ROBOTO-REGULAR.TTF"));
        tv_days.setTypeface(Utils.SetFont(MainLocationActivity.this, "ROBOTO-REGULAR.TTF"));
        tv_working_hours.setTypeface(Utils.SetFont(MainLocationActivity.this, "ROBOTO-REGULAR.TTF"));
        tv_closed.setTypeface(Utils.SetFont(MainLocationActivity.this, "ROBOTO-REGULAR.TTF"));
        tv_weekdays.setTypeface(Utils.SetFont(MainLocationActivity.this, "ROBOTO-REGULAR.TTF"));
        tv_saturday.setTypeface(Utils.SetFont(MainLocationActivity.this, "ROBOTO-REGULAR.TTF"));
        tv_sunday.setTypeface(Utils.SetFont(MainLocationActivity.this, "ROBOTO-REGULAR.TTF"));
        tv_weekdays_from.setTypeface(Utils.SetFont(MainLocationActivity.this, "ROBOTO-REGULAR.TTF"));
        tv_weekdays_to.setTypeface(Utils.SetFont(MainLocationActivity.this, "ROBOTO-REGULAR.TTF"));
        tv_sat_from.setTypeface(Utils.SetFont(MainLocationActivity.this, "ROBOTO-REGULAR.TTF"));
        tv_sat_to.setTypeface(Utils.SetFont(MainLocationActivity.this, "ROBOTO-REGULAR.TTF"));
        tv_sun_from.setTypeface(Utils.SetFont(MainLocationActivity.this, "ROBOTO-REGULAR.TTF"));
        tv_sun_to.setTypeface(Utils.SetFont(MainLocationActivity.this, "ROBOTO-REGULAR.TTF"));
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
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("objectid", objectid);
        outState.putString("password", password);
        outState.putString("profileobjectid", profileobjectid);
        outState.putString("email", email);
        outState.putString("type", type);
        outState.putString("category_name",main_category);
        outState.putString("category_url",category_url);
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.tv_weekdays_from)
    public void WeekdaysFrom(View view) {
        if (tv_weekdays_to.getText().toString().length() == 0) {
            pos = 1;
            ShowTimePicker("weekdays_open", view);
        } else {
            ShowTimePikcerOn(tv_weekdays_from, tv_weekdays_to);
        }
    }

    @OnClick(R.id.tv_weekdays_to)
    public void WeekdaysTo(View view) {
        if (tv_weekdays_from.getText().toString().trim().length() == 0) {
            Utils.ShowCustomDialog(MainLocationActivity.this, Effectstype.SlideBottom, getString(R.string.select_opening_hour));
        } else {
            pos = 1;
            ShowTimePicker("weekdays_close", view);
        }
    }

    @OnClick(R.id.tv_sat_from)
    public void SatFrom(View view) {
        if (tv_sat_to.getText().toString().length() == 0) {
            pos = 2;
            ShowTimePicker("sat_open", view);
        } else {
            ShowTimePikcerOn(tv_sat_from, tv_sat_to);
        }
    }

    @OnClick(R.id.tv_sat_to)
    public void SatTo(View view) {
        if (tv_sat_from.getText().toString().trim().length() == 0) {
            Utils.ShowCustomDialog(MainLocationActivity.this, Effectstype.SlideBottom, getString(R.string.select_opening_hour));
        } else {
            pos = 2;
            ShowTimePicker("sat_close", view);
        }
    }

    @OnClick(R.id.tv_sun_from)
    public void SundayFrom(View view) {
        if (tv_sun_to.getText().toString().length() == 0) {
            pos = 3;
            ShowTimePicker("sun_open", view);
        } else {
            ShowTimePikcerOn(tv_sun_from, tv_sun_to);
        }
    }

    @OnClick(R.id.tv_sun_to)
    public void SunTo(View view) {
        if (tv_sun_from.getText().toString().trim().length() == 0) {
            Utils.ShowCustomDialog(MainLocationActivity.this, Effectstype.SlideBottom, getString(R.string.select_opening_hour));
        } else {
            pos = 3;
            ShowTimePicker("sun_close", view);
        }
    }

    @OnClick(R.id.btn_submit)
    public void Submit() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_phone.getWindowToken(), 0);
        if (et_enter_location.getText().toString().trim().length() == 0) {
            Utils.ShowCustomDialog(MainLocationActivity.this, Effectstype.SlideBottom, getString(R.string.sel_loc_err));
        }

        else if (et_phone.getText().toString().trim().length() == 0) {
            Utils.ShowCustomDialog(MainLocationActivity.this, Effectstype.SlideBottom, getString(R.string.enter_phone_err));
            ;
        }
        else {
            if (ch_weekdays.isChecked() == true) {
                weekdays = getString(R.string.close);
            } else if (ch_weekdays.isChecked() == false) {
                if (tv_weekdays_from.getText().toString().trim().length() == 0) {
                    Utils.ShowCustomDialog(MainLocationActivity.this, Effectstype.SlideBottom, getString(R.string.sel_mon_open));
                    return;
                } else if (tv_weekdays_to.getText().toString().trim().length() == 0) {
                    Utils.ShowCustomDialog(MainLocationActivity.this, Effectstype.SlideBottom, getString(R.string.sel_mon_close));
                    return;
                } else {
                    weekdays = tv_weekdays_from.getText().toString() + "-" + tv_weekdays_to.getText().toString();

                }
            }
            if (ch_saturday.isChecked() == true) {
                sat = getString(R.string.close);
            } else if (ch_saturday.isChecked() == false) {
                if (tv_sat_from.getText().toString().trim().length() == 0) {
                    Utils.ShowCustomDialog(MainLocationActivity.this, Effectstype.SlideBottom, getString(R.string.sel_sat_open));
                    return;
                } else if (tv_sat_to.getText().toString().trim().length() == 0) {
                    Utils.ShowCustomDialog(MainLocationActivity.this, Effectstype.SlideBottom, getString(R.string.sel_sat_close));
                    return;
                } else {
                    sat = tv_sat_from.getText().toString() + "-" + tv_sat_to.getText().toString();
                }

            }
            if (ch_sunday.isChecked() == true) {
                sun = getString(R.string.close);
            } else if (ch_sunday.isChecked() == false) {
                if (tv_sun_from.getText().toString().trim().length() == 0) {
                    Utils.ShowCustomDialog(MainLocationActivity.this, Effectstype.SlideBottom, getString(R.string.sel_sun_open));
                    return;
                } else if (tv_sun_to.getText().toString().trim().length() == 0) {
                    Utils.ShowCustomDialog(MainLocationActivity.this, Effectstype.SlideBottom, getString(R.string.sel_sun_close));
                    return;
                } else {
                    sun = tv_sun_from.getText().toString() + "-" + tv_sun_to.getText().toString();
                }
            }
            if (ch_weekdays.isChecked() && ch_sunday.isChecked() && ch_saturday.isChecked()) {
                Utils.ShowCustomDialog(MainLocationActivity.this, Effectstype.SlideBottom, getString(R.string.select_one_working_hour));
                ;
                return;
            }
            if(Utils.checkInternetConnection(MainLocationActivity.this)){
                ParseObject profile = ParseObject.createWithoutData(CommonUtils.SHOPKEEPER_PROFILE_DATA, profileobjectid);
                ParseObject mainloc=new ParseObject(CommonUtils.MAIN_LOCATION);
                mainloc.put("loc",et_enter_location.getText().toString());
                mainloc.put("phone",et_phone.getText().toString().trim());
                mainloc.put("weekdays",weekdays);
                mainloc.put("sat", sat);
                mainloc.put("sun", sun);
                if(et_addtext.getText().toString().trim().equalsIgnoreCase("")){
                    mainloc.put("comment","No comment");
                }
                else{
                    mainloc.put("comment",et_addtext.getText().toString().trim());
                }
                mainloc.put("latitude",latitude);
                mainloc.put("longitude",longitude);
                profile.put("mainloc",mainloc);
                RotateProgressDialog.ShowDialog(MainLocationActivity.this);
                profile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        RotateProgressDialog.DismissDialog();
                        if(e==null){
                            Utils.ShowSnackBar(MainLocationActivity.this, getString(R.string.main_loc_save), SnackbarType.MULTI_LINE);
                            finish();
                            Intent intent = new Intent(MainLocationActivity.this, AddDiscountActivity.class);
                            intent.putExtra("objectid",objectid);
                            intent.putExtra("password",password);
                            intent.putExtra("profileobjectid",profileobjectid);
                            intent.putExtra("email", email);
                            intent.putExtra("type","shopkeeper");
                            intent.putExtra("phone",et_phone.getText().toString().trim()) ;
                            intent.putExtra("main_category",main_category);
                            intent.putExtra("category_url",category_url);
                            intent.putExtra("latitude",latitude);
                            intent.putExtra("location",et_enter_location.getText().toString().trim());
                            intent.putExtra("longitude",longitude);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter_in, R.anim.enter_out);
                        }
                        else{
                            Utils.ShowCustomDialog(MainLocationActivity.this, Effectstype.SlideBottom, e.getMessage());
                        }
                    }
                });

            }
            else{
                Utils.ShowSnackBar(MainLocationActivity.this, getString(R.string.network_error), SnackbarType.SINGLE_LINE);
            }
        }
    }

    @OnClick(R.id.et_enter_location)
    public void Location() {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

            Context context = getApplicationContext();

            startActivityForResult(builder.build(context), PLACE_PICKER_REQUEST);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, MainLocationActivity.this);
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;

                if (place.getAddress().toString().trim().equalsIgnoreCase("")) {
                    Utils.ShowCustomDialog(MainLocationActivity.this, Effectstype.SlideBottom, getString(R.string.unable_fetch_loc));
                } else {
                    et_enter_location.setText(place.getAddress().toString().trim());
                }

            } else {
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
                                    Utils.ShowCustomDialog(MainLocationActivity.this, Effectstype.SlideBottom, getString(R.string.close_time_error));
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
                                    Utils.ShowCustomDialog(MainLocationActivity.this, Effectstype.SlideBottom, getString(R.string.close_time_error));
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
                                    Utils.ShowCustomDialog(MainLocationActivity.this, Effectstype.SlideBottom, getString(R.string.close_time_error));
                                } else {
                                    tv_sun_to.setText(strHrsToShow + ":" + minutee + " " + am_pm);
                                }
                                break;
                        }
                        break;
                }

            }
        }, hourOfDay, minute, false);
        if (open_close.endsWith("open")) {
            time.setStartTime(9, 0);
        } else if (open_close.endsWith("close")) {
            time.setStartTime(21, 0);
        }
        time.show(getSupportFragmentManager(), "datepicker");
    }

    public void ShowTimePikcerOn(final TextView tv_from, final TextView tv_to) {
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
                    Utils.ShowCustomDialog(MainLocationActivity.this, Effectstype.SlideBottom, "opening time must be less than closing");
                }
            }
        }, hourOfDay, minute, false);
        time.show(getSupportFragmentManager(), "datepicker");
    }
}
