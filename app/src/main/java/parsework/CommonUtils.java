package parsework;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.mapmap.R;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import activities.DashboardActivity;
import activities.MainLocationActivity;
import commonUtils.Utils;
import views.Effectstype;
import views.NiftyDialogBuilder;
import views.Progressdialog;
import views.RotateProgressDialog;

/**
 * Created by osx on 13/10/15.
 */
public class CommonUtils {

    public static final String SHOPKEEPER_TABLE = "Shopkeeper";
    public static final String CUSTOMER_TABLE = "Customers";
    public static final String SHOPKEEPER_PROFILE_DATA = "ShopkeeperProfile";
    public static final String CUSTOMER_PROFILE_DATA = "CustomerProfile";
    public static final String DISCOUNT_TABLE = "Discounts";
    public static final String CATEGORIES = "Categories";
    public static final String SUBCATEGORIES = "Subcategories";
    public static final String LOCATIONS = "Locations";
    public static final String MAIN_LOCATION="MainLocations";
    public static final String FAVOURITES="Favourite";
    static ParseObject profile_object;
    public static ParseQuery<ParseObject> query = null;
    static NiftyDialogBuilder dialogBuilder;
    static ParseObject userdata = null;

    public static void SendEmail(final Context context, Map<String, String> emaildata, final ParseObject object) {
        if (Utils.checkInternetConnection(context)) {
            ParseCloud.callFunctionInBackground("mailSend", emaildata, new FunctionCallback<Object>() {
                @Override
                public void done(Object o, ParseException e) {
                    RotateProgressDialog.DismissDialog();
                    if (e == null) {
                        Utils.ShowSnackBar(context, context.getString(R.string.verification_message), SnackbarType.MULTI_LINE);
                        ConfirmationDialog(context, Effectstype.SlideBottom, object);
                    } else {
                        System.out.println("error code" + e.getCode());
                        System.out.println("the error" + e.getLocalizedMessage());
                        Utils.ShowCustomDialog(context, Effectstype.SlideBottom, e.getMessage());
                    }
                }
            });
        } else {
            Utils.ShowSnackBar(context, context.getString(R.string.network_error), SnackbarType.SINGLE_LINE);
        }
    }

    public static void SignUp(final Context context, final Map<String, Object> values) {

        if (Utils.checkInternetConnection(context)) {

            RotateProgressDialog.ShowDialog(context);
            final String type = values.get("type").toString();
            ParseQuery<ParseObject> query = null;
            switch (type) {
                case "shopkeeper":
                    query = new ParseQuery<ParseObject>(SHOPKEEPER_TABLE);
                    break;
                case "customer":
                    query = new ParseQuery<ParseObject>(CUSTOMER_TABLE);
                    break;
            }
            query.include("profile_data");
            query.whereEqualTo("email", values.get("email").toString());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        if (list.size() == 0) {

                            Set<String> set = values.keySet();
                            Iterator<String> iterator = set.iterator();
                            switch (type) {
                                case "shopkeeper":
                                    userdata = new ParseObject(SHOPKEEPER_TABLE);
                                    profile_object = new ParseObject(SHOPKEEPER_PROFILE_DATA);
                                    break;
                                case "customer":
                                    System.out.println("inside customer");
                                    userdata = new ParseObject(CUSTOMER_TABLE);
                                    profile_object = new ParseObject(CUSTOMER_PROFILE_DATA);
                                    break;
                            }
                            while (iterator.hasNext()) {
                                String key = iterator.next();
                                Object value = values.get(key);
                                if (key.equalsIgnoreCase("type")) {
                                    continue;
                                } else if (key.equalsIgnoreCase("email") || key.equalsIgnoreCase("password")) {
                                    userdata.put(key, value);
                                } else {
                                    try {
                                        profile_object.put(key, value);
                                    } catch (Exception ee) {
                                        ee.printStackTrace();
                                    }

                                }
                            }
                            userdata.put("profile_data", profile_object);
                            userdata.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e1) {
                                    RotateProgressDialog.DismissDialog();
                                    if (e1 != null) {
                                        Utils.ShowCustomDialog(context, Effectstype.SlideBottom, e1.getMessage());
                                    } else {
                                        switch (type) {
                                            case "shopkeeper":
                                                Utils.ShowSnackBar(context, "Data saved successfully", SnackbarType.SINGLE_LINE);
                                                int verification_number = (int) values.get("verification_number");
                                                Map<String, String> params = new HashMap<>();
                                                params.put("text", context.getString(R.string.verification_code) + verification_number);
                                                params.put("subject", verification_number + "");
                                                params.put("from", "SalesByMap@mapmap.com");
                                                params.put("to", values.get("email").toString());
                                                Log.e("verification_number", verification_number + "\n" + values.get("email").toString() + "\n"
                                                );
                                                SendEmail(context, params, userdata);
                                                break;
                                            case "customer":
                                                int user_type = (int) values.get("user_type");
                                                Utils.ShowSnackBar(context, "Account created successfully", SnackbarType.SINGLE_LINE);
                                                SharedPreferences prefs = context.getSharedPreferences("UserData", 0);
                                                String profileobjectid = userdata.getParseObject("profile_data").getObjectId();
                                                prefs.edit().putString("objectid", userdata.getObjectId()).commit();
                                                prefs.edit().putString("password", values.get("password").toString()).commit();
                                                prefs.edit().putString("type", type).commit();
                                                prefs.edit().putString("profileobjectid", profileobjectid).commit();
                                                prefs.edit().putInt("user_type", user_type).commit();
                                                ((Activity) context).finish();
                                                Intent intent = new Intent(context, DashboardActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                context.startActivity(intent);
                                                ((Activity) context).overridePendingTransition(R.anim.enter_in, R.anim.enter_out);
                                                break;
                                        }

                                    }
                                }
                            });
                        } else if (list.size() == 1) {
                            switch (type) {
                                case "shopkeeper":
                                    try {
                                        RotateProgressDialog.DismissDialog();
                                        ParseObject profile_data = list.get(0).getParseObject("profile_data");
                                        System.out.println("profile data" + profile_data);
                                        boolean check = profile_data.getBoolean("isVerified");

                                        if (check) {
                                            SnackbarManager.show(Snackbar.with(context).type(SnackbarType.MULTI_LINE).text(context.getString(R.string.already_exists)).
                                                    color(ContextCompat.getColor(context, R.color.top_bar_color)).
                                                    textColor(ContextCompat.getColor(context, R.color.White)));
                                        } else {
                                            Utils.ShowSnackBar(context, context.getString(R.string.user_exists_error), SnackbarType.MULTI_LINE);
                                            ConfirmationDialog(context, Effectstype.SlideBottom, list.get(0));
                                        }
                                    } catch (Exception ee) {
                                        ee.printStackTrace();
                                    }

                                    break;
                                case "customer":
                                    try {
                                        RotateProgressDialog.DismissDialog();
                                        ParseObject profile_data = list.get(0).getParseObject("profile_data");
                                        int user_type = profile_data.getInt("user_type");
                                        int values_user_type = (int) values.get("user_type");
                                        if (user_type == 1) {
                                            if (values_user_type == 1) {
                                                SnackbarManager.show(Snackbar.with(context).type(SnackbarType.MULTI_LINE).text(context.getString(R.string.user_exists_login)).
                                                        color(ContextCompat.getColor(context, R.color.top_bar_color)).
                                                        textColor(ContextCompat.getColor(context, R.color.White)));
                                            } else if (values_user_type == 2) {
                                                AccessToken.setCurrentAccessToken(null);
                                                SnackbarManager.show(Snackbar.with(context).type(SnackbarType.MULTI_LINE).text(context.getString(R.string.user_exists_login)).
                                                        color(ContextCompat.getColor(context, R.color.top_bar_color)).
                                                        textColor(ContextCompat.getColor(context, R.color.White)));
                                            }
                                        } else if (user_type == 2) {
                                            if (values_user_type == 1) {
                                                Utils.ShowSnackBar(context, context.getString(R.string.user_exists_fbuser), SnackbarType.SINGLE_LINE);
                                            } else if (values_user_type == 2) {
                                                SharedPreferences prefs = context.getSharedPreferences("UserData", 0);
                                                String profileobjectid = list.get(0).getParseObject("profile_data").getObjectId();
                                                prefs.edit().putString("objectid", list.get(0).getObjectId()).commit();
                                                prefs.edit().putString("type", type).commit();
                                                prefs.edit().putString("profileobjectid", profileobjectid).commit();
                                                prefs.edit().putInt("user_type", user_type).commit();
                                                ((Activity) context).finish();
                                                Intent intent = new Intent(context, DashboardActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                context.startActivity(intent);
                                                ((Activity) context).overridePendingTransition(R.anim.enter_in, R.anim.enter_out);
                                            }
                                        }
                                    } catch (Exception ee) {
                                        ee.printStackTrace();
                                    }

                                    break;

                            }
                        }
                    } else {
                        RotateProgressDialog.DismissDialog();
                        System.out.println("inside common utils");
                        Utils.ShowCustomDialog(context, Effectstype.SlideBottom, e.getMessage());
                    }
                }
            });
        } else {
            Utils.ShowSnackBar(context, context.getResources().getString(R.string.network_error), SnackbarType.SINGLE_LINE);
        }

    }

    public static void ConfirmationDialog(final Context context, Effectstype effect, final ParseObject object) {
        if (dialogBuilder == null || !dialogBuilder.isShowing()) {
            dialogBuilder = NiftyDialogBuilder.getInstance(context);
            dialogBuilder.withEffect(effect);
            dialogBuilder.setCancelable(false);
            dialogBuilder.withDialogColor(Color.WHITE);
            dialogBuilder.withDividerColor(Color.TRANSPARENT);
            dialogBuilder.setTopPanelBackground(Color.TRANSPARENT);
            dialogBuilder.setParentPanelBackground(Color.TRANSPARENT);
            dialogBuilder.setCustomView(R.layout.layout_confirmation_dialog, (Activity) context);
            TextView tv_title = (TextView) dialogBuilder.findViewById(R.id.tv_title);
            tv_title.setTypeface(Utils.SetFont(context, "ROBOTO-REGULAR.TTF"));
            tv_title.setText(context.getString(R.string.verification));
            final EditText et_code = (EditText) dialogBuilder.findViewById(R.id.et_code);
            et_code.setTypeface(Utils.SetFont(context, "ROBOTO-REGULAR.TTF"));
            Button btn_submit = (Button) dialogBuilder.findViewById(R.id.btn_submit);
            btn_submit.setTypeface(Utils.SetFont(context, "ROBOTO-REGULAR.TTF"));
            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.HideDialogKeyboard(context, view);
                    if (TextUtils.isEmpty(et_code.getText().toString())) {
                        Utils.ShowSnackBar(context, context.getString(R.string.enter_digit_code), SnackbarType.SINGLE_LINE);
                    } else {
                        ParseObject profile_data = object.getParseObject("profile_data");
                        int verification_number = profile_data.getInt("verification_number");
                        int entered_number = Integer.parseInt(et_code.getText().toString());
                        if (entered_number == verification_number) {
                            if (Utils.checkInternetConnection(context)) {
                                RotateProgressDialog.ShowDialog(context);
                                try {
                                    object.getParseObject("profile_data").put("isVerified", true);
                                    object.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            RotateProgressDialog.DismissDialog();
                                            if (e == null || e != null) {
                                                dialogBuilder.dismiss();
                                                Utils.ShowSnackBar(context, context.getString(R.string.acc_successfull), SnackbarType.SINGLE_LINE);
                                                ParseObject shopparseobject=object.getParseObject("profile_data");
                                                String profileobjectid = shopparseobject.getObjectId();
                                                ((Activity) context).finish();
                                                Intent intent = new Intent(context, MainLocationActivity.class);
                                                intent.putExtra("objectid",object.getObjectId());
                                                intent.putExtra("password", object.getString("password"));
                                                intent.putExtra("profileobjectid",profileobjectid);
                                                intent.putExtra("category_name",shopparseobject.getString("main_category"));
                                                intent.putExtra("category_url",shopparseobject.getString("category_url"));
                                                intent.putExtra("email", object.getString("email"));
                                                intent.putExtra("type","shopkeeper");
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                context.startActivity(intent);
                                                ((Activity) context).overridePendingTransition(R.anim.enter_in, R.anim.enter_out);
                                            } else {
                                                Utils.ShowCustomDialog(context, Effectstype.SlideBottom, e.getMessage());
                                            }
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {
                                Utils.ShowSnackBar(context, context.getResources().getString(R.string.network_error), SnackbarType.SINGLE_LINE);
                            }

                        } else {
                            Utils.ShowSnackBar(context, context.getString(R.string.code_mismatch), SnackbarType.SINGLE_LINE);
                        }
                    }
                }
            });
            TextView tv_resend_email = (TextView) dialogBuilder.findViewById(R.id.tv_resend_email);
            tv_resend_email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utils.checkInternetConnection(context)) {
                        ParseObject profile_data = object.getParseObject("profile_data");
                        int verification_number = profile_data.getInt("verification_number");
                        Map<String, String> params = new HashMap<>();
                        params.put("text", context.getString(R.string.verification_code) + verification_number);
                        params.put("subject", verification_number+"");
                        params.put("from","SalesByMap@mapmap.com");
                        params.put("to", object.get("email").toString());
                        RotateProgressDialog.ShowDialog(context);
                        System.out.println("my params" + params.toString());
                        ParseCloud.callFunctionInBackground("mailSend", params, new FunctionCallback<Object>() {
                            @Override
                            public void done(Object o, ParseException e) {
                                RotateProgressDialog.DismissDialog();
                                if (e == null) {

                                    System.out.println("object" + o.toString());
                                    Utils.ShowSnackBar(context, context.getString(R.string.verification_message), SnackbarType.MULTI_LINE);
                                } else {
                                    e.printStackTrace();
                                    System.out.println("code" + e.getCode());
                                    Utils.ShowSnackBar(context, e.getMessage(), SnackbarType.MULTI_LINE);
                                }
                            }
                        });

                    } else {
                        Utils.ShowSnackBar(context, context.getString(R.string.network_error), SnackbarType.SINGLE_LINE);
                    }
                }
            });
            dialogBuilder.show();
        }
    }

    public static void Login(final Context context, final Map<String, Object> values) {
        if (Utils.checkInternetConnection(context)) {
            RotateProgressDialog.ShowDialog(context);
            final String type = values.get("type").toString();
            ParseQuery<ParseObject> query = null;
            switch (type) {
                case "shopkeeper":
                    query = new ParseQuery<ParseObject>(SHOPKEEPER_TABLE);
                    break;
                case "customer":
                    query = new ParseQuery<ParseObject>(CUSTOMER_TABLE);
                    break;
            }
            query.include("profile_data");
            query.whereEqualTo("email", values.get("email").toString());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    RotateProgressDialog.DismissDialog();
                    if (e == null) {
                        if (list.size() == 0) {
                            Utils.ShowSnackBar(context, context.getString(R.string.no_user), SnackbarType.SINGLE_LINE);
                        } else if (list.size() >= 1) {
                            switch (type) {
                                case "shopkeeper":
                                    try {
                                        String password = list.get(0).get("password").toString();
                                        ParseObject profile_data = list.get(0).getParseObject("profile_data");
                                        boolean isverified = profile_data.getBoolean("isVerified");
                                        if (password.equalsIgnoreCase(values.get("password").toString())) {
                                            if (isverified || !isverified) {

                                                ParseObject mainloc=profile_data.getParseObject("mainloc");
                                                System.out.println("main location"+mainloc);
                                                SharedPreferences prefs = context.getSharedPreferences("UserData", 0);
                                                String profileobjectid = list.get(0).getParseObject("profile_data").getObjectId();
                                                if(mainloc!=null){
                                                    prefs.edit().putString("objectid", list.get(0).getObjectId()).commit();
                                                    prefs.edit().putString("password", list.get(0).getString("password")).commit();
                                                    prefs.edit().putString("email", list.get(0).getString("email")).commit();
                                                    System.out.println("category name"+profile_data.getString("main_category"));
                                                    prefs.edit().putString("category_name", profile_data.getString("main_category")).commit();
                                                    prefs.edit().putString("category_url",profile_data.getString("category_url")).commit();
                                                    prefs.edit().putString("profileobjectid", profileobjectid).commit();
                                                    prefs.edit().putString("type", type).commit();
                                                    ((Activity) context).finish();
                                                    Intent intent = new Intent(context, DashboardActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    context.startActivity(intent);
                                                    ((Activity) context).overridePendingTransition(R.anim.enter_in, R.anim.enter_out);
                                                }
                                                else{
                                                    ((Activity) context).finish();
                                                    Intent intent = new Intent(context, MainLocationActivity.class);
                                                    intent.putExtra("objectid",list.get(0).getObjectId());
                                                    intent.putExtra("password", list.get(0).getString("password"));
                                                    intent.putExtra("profileobjectid",profileobjectid);
                                                    intent.putExtra("category_name",profile_data.getString("main_category"));
                                                    intent.putExtra("category_url",profile_data.getString("category_url"));
                                                    intent.putExtra("email", list.get(0).getString("email"));
                                                    intent.putExtra("type","shopkeeper");
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    context.startActivity(intent);
                                                    ((Activity) context).overridePendingTransition(R.anim.enter_in, R.anim.enter_out);
                                                }

                                            } else {
                                                Utils.ShowSnackBar(context, context.getString(R.string.acc_verify), SnackbarType.MULTI_LINE);
                                                ConfirmationDialog(context, Effectstype.SlideBottom, list.get(0));
                                            }
                                        } else {
                                            Utils.ShowSnackBar(context, context.getString(R.string.invalid_password), SnackbarType.SINGLE_LINE);
                                        }
                                    } catch (Exception ee) {
                                        e.printStackTrace();
                                    }

                                    break;
                                case "customer":
                                    try {
                                        ParseObject profile_data = list.get(0).getParseObject("profile_data");
                                        int user_type = profile_data.getInt("user_type");
                                        switch (user_type) {
                                            case 1:
                                                String password = list.get(0).getString("password");
                                                if (password.equalsIgnoreCase(values.get("password").toString())) {
                                                    SharedPreferences prefs = context.getSharedPreferences("UserData", 0);
                                                    String profileobjectid = list.get(0).getParseObject("profile_data").getObjectId();
                                                    prefs.edit().putString("objectid", list.get(0).getObjectId()).commit();
                                                    prefs.edit().putString("password", values.get("password").toString()).commit();
                                                    prefs.edit().putString("type", type).commit();
                                                    prefs.edit().putString("profileobjectid", profileobjectid).commit();
                                                    prefs.edit().putInt("user_type", 1).commit();
                                                    ((Activity) context).finish();
                                                    Intent intent = new Intent(context, DashboardActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    context.startActivity(intent);
                                                    ((Activity) context).overridePendingTransition(R.anim.enter_in, R.anim.enter_out);
                                                } else {
                                                    Utils.ShowSnackBar(context, context.getString(R.string.invalid_password), SnackbarType.SINGLE_LINE);
                                                }
                                                break;
                                            case 2:
                                                SnackbarManager.show(Snackbar.with(context).type(SnackbarType.MULTI_LINE).text(context.getString(R.string.user_exists_fb_user)).
                                                        dismissOnActionClicked(true).
                                                        color(ContextCompat.getColor(context, R.color.top_bar_color)).
                                                        textColor(ContextCompat.getColor(context, R.color.White)));
                                                break;
                                        }
                                    } catch (Exception ee) {
                                        ee.printStackTrace();
                                    }

                                    break;
                            }
                        }

                    } else {
                        Utils.ShowCustomDialog(context, Effectstype.SlideBottom, e.getMessage());
                    }
                }
            });
        } else {
            Utils.ShowSnackBar(context, context.getString(R.string.network_error), SnackbarType.SINGLE_LINE);
        }
    }


    public static void Loginn(final Context context, final Map<String, Object> values){
        if (Utils.checkInternetConnection(context)) {
            RotateProgressDialog.ShowDialog(context);
            query=new ParseQuery<ParseObject>(SHOPKEEPER_TABLE);
            query.include("profile_data");
            query.whereEqualTo("email", values.get("email").toString());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    RotateProgressDialog.DismissDialog();
                    if(e==null){
                        if(list.size()==0){
                            RotateProgressDialog.ShowDialog(context);
                            query=new ParseQuery<ParseObject>(CUSTOMER_TABLE);
                            query.include("profile_data");
                            query.whereEqualTo("email", values.get("email").toString());
                            try {
                                ParseObject profile_data = list.get(0).getParseObject("profile_data");
                                int user_type = profile_data.getInt("user_type");
                                switch (user_type) {
                                    case 1:
                                        String password = list.get(0).getString("password");
                                        if (password.equalsIgnoreCase(values.get("password").toString())) {
                                            SharedPreferences prefs = context.getSharedPreferences("UserData", 0);
                                            String profileobjectid = list.get(0).getParseObject("profile_data").getObjectId();
                                            prefs.edit().putString("objectid", list.get(0).getObjectId()).commit();
                                            prefs.edit().putString("password", values.get("password").toString()).commit();
                                            prefs.edit().putString("type", "customer").commit();
                                            prefs.edit().putString("profileobjectid", profileobjectid).commit();
                                            prefs.edit().putInt("user_type", 1).commit();
                                            ((Activity) context).finish();
                                            Intent intent = new Intent(context, DashboardActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            context.startActivity(intent);
                                            ((Activity) context).overridePendingTransition(R.anim.enter_in, R.anim.enter_out);
                                        } else {
                                            Utils.ShowSnackBar(context, context.getString(R.string.invalid_password), SnackbarType.SINGLE_LINE);
                                        }
                                        break;
                                    case 2:
                                        SnackbarManager.show(Snackbar.with(context).type(SnackbarType.MULTI_LINE).text(context.getString(R.string.user_exists_fb_user)).
                                                dismissOnActionClicked(true).
                                                color(ContextCompat.getColor(context, R.color.top_bar_color)).
                                                textColor(ContextCompat.getColor(context, R.color.White)));
                                        break;
                                }
                            } catch (Exception ee) {
                                ee.printStackTrace();
                            }
                        }
                        else{
                            try {
                                String password = list.get(0).get("password").toString();
                                ParseObject profile_data = list.get(0).getParseObject("profile_data");
                                boolean isverified = profile_data.getBoolean("isVerified");
                                if (password.equalsIgnoreCase(values.get("password").toString())) {
                                    if (isverified || !isverified) {
                                        ParseObject mainloc=profile_data.getParseObject("mainloc");
                                        System.out.println("main location"+mainloc);
                                        SharedPreferences prefs = context.getSharedPreferences("UserData", 0);
                                        String profileobjectid = list.get(0).getParseObject("profile_data").getObjectId();
                                        if(mainloc!=null){
                                            prefs.edit().putString("objectid", list.get(0).getObjectId()).commit();
                                            prefs.edit().putString("password", list.get(0).getString("password")).commit();
                                            prefs.edit().putString("email", list.get(0).getString("email")).commit();
                                            System.out.println("category name"+profile_data.getString("main_category"));
                                            prefs.edit().putString("category_name", profile_data.getString("main_category")).commit();
                                            prefs.edit().putString("category_url",profile_data.getString("category_url")).commit();
                                            prefs.edit().putString("profileobjectid", profileobjectid).commit();
                                            prefs.edit().putString("type", "shopkeeper").commit();
                                            ((Activity) context).finish();
                                            Intent intent = new Intent(context, DashboardActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            context.startActivity(intent);
                                            ((Activity) context).overridePendingTransition(R.anim.enter_in, R.anim.enter_out);
                                        }
                                        else{
                                            ((Activity) context).finish();
                                            Intent intent = new Intent(context, MainLocationActivity.class);
                                            intent.putExtra("objectid",list.get(0).getObjectId());
                                            intent.putExtra("password", list.get(0).getString("password"));
                                            intent.putExtra("profileobjectid",profileobjectid);
                                            intent.putExtra("category_name",profile_data.getString("main_category"));
                                            intent.putExtra("category_url",profile_data.getString("category_url"));
                                            intent.putExtra("email", list.get(0).getString("email"));
                                            intent.putExtra("type","shopkeeper");
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            context.startActivity(intent);
                                            ((Activity) context).overridePendingTransition(R.anim.enter_in, R.anim.enter_out);
                                        }

                                    } else {
                                        Utils.ShowSnackBar(context, context.getString(R.string.acc_verify), SnackbarType.MULTI_LINE);
                                        ConfirmationDialog(context, Effectstype.SlideBottom, list.get(0));
                                    }
                                } else {
                                    Utils.ShowSnackBar(context, context.getString(R.string.invalid_password), SnackbarType.SINGLE_LINE);
                                }
                            } catch (Exception ee) {
                                e.printStackTrace();
                            }

                        }
                    }
                    else{
                        Utils.ShowCustomDialog(context, Effectstype.SlideBottom, e.getMessage());
                    }
                }
            });
        }
        else{

        }
    }


}
