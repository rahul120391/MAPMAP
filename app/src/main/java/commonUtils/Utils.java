package commonUtils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.mapmap.R;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import views.Effectstype;
import views.NiftyDialogBuilder;

/**
 * Created by osx on 29/07/15.
 */
public class Utils {

    static NiftyDialogBuilder dialogBuilder;
    static Typeface font;

    /**
     * this method is used to hidekeyboard
     *
     * @param activity -pass activity context as argument to hide keyboard on that activity
     */
    public static void HideKeyboard(Activity activity) {
        @SuppressWarnings("static-access")
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(activity.getWindow().getDecorView().getApplicationWindowToken(), 0);
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }

    }
/***********************************************************************************************************************/
    /**
     * this method is used to show snackbar given by materialDesign
     *
     * @param context -pass activity context as argument to show snackbar on that activity
     * @param message -message to show inside snackbar
     */
    public static void ShowSnackBar(Context context, String message, SnackbarType typee) {
        SnackbarManager.show(Snackbar.with(context).type(typee).text(message).
                dismissOnActionClicked(true).
                color(ContextCompat.getColor(context, R.color.top_bar_color)).
                textColor(ContextCompat.getColor(context, R.color.White)));

    }

/***********************************************************************************************************************/
    /**
     * this method is used to close keyboard in dialog
     *
     * @param context-pass fragment context as argument to close keyboard
     * @param v-view       to get focus
     */
    public static void HideDialogKeyboard(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
    }

    /**
     * *******************************************************************************************************************
     */

    public static void ShowCustomDialog(Context context, Effectstype effect,String message) {
        if (dialogBuilder == null || !dialogBuilder.isShowing()) {
            dialogBuilder = NiftyDialogBuilder.getInstance(context);
            dialogBuilder.withEffect(effect);
            dialogBuilder.withDialogColor(ContextCompat.getColor(context, R.color.White));
            dialogBuilder.withDividerColor(ContextCompat.getColor(context, android.R.color.transparent));
            dialogBuilder.setTopPanelBackground(ContextCompat.getColor(context, android.R.color.transparent));
            dialogBuilder.setParentPanelBackground(ContextCompat.getColor(context, android.R.color.transparent));
            dialogBuilder.setCustomView(R.layout.messagedialog_customview, (Activity) context);
            Button btn_ok = (Button) dialogBuilder.findViewById(R.id.btn_ok);
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogBuilder.dismiss();
                }
            });
            TextView tv_title=(TextView)dialogBuilder.findViewById(R.id.tv_title);
            tv_title.setText(context.getString(R.string.error));
            TextView tv_message=(TextView)dialogBuilder.findViewById(R.id.tv_message);
            tv_message.setText(message);
            btn_ok.setTypeface(SetFont(context,"ROBOTO-REGULAR.TTF"));
            tv_title.setTypeface(SetFont(context,"ROBOTO-REGULAR.TTF"));
            tv_message.setTypeface(SetFont(context,"ROBOTO-REGULAR.TTF"));
            dialogBuilder.show();
        }
    }

    public static Typeface SetFont(Context context, String fontname) {
        font = Typeface.createFromAsset(context.getAssets(), fontname);
        return font;
    }

    public static boolean checkInternetConnection(Context cnt) {
        ConnectivityManager cm = (ConnectivityManager) cnt
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }

        return false;
    }

    public static int GenrateRandomNumber() {
        String result = "";
        int random;
        while (true) {
            random = (int) ((Math.random() * (10)));
            if (result.length() == 0 && random == 0) {//when parsed this insures that the number doesn't start with 0
                random += 1;
                result += random;
            } else if (!result.contains(Integer.toString(random))) {//if my result doesn't contain the new generated digit then I add it to the result
                result += Integer.toString(random);
            }
            if (result.length() == 4) {//when i reach the number of digits desired i break out of the loop and return the final result
                break;
            }
        }
        int x = Integer.parseInt(result);
        return x;
    }


    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                inImage, System.currentTimeMillis() + "", null);
        return Uri.parse(path);
    }

    public static String getRealPathFromURI(Uri uri, Context context) {
        Cursor cursor = context.getContentResolver().query(uri, null,
                null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        if (idx > -1) {
            return cursor.getString(idx);
        } else {
            return null;
        }

    }

    public static String getCurrentDate() {
        String formattedDate = "";
        try {

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            formattedDate = df.format(c.getTime());
            System.out.println("date is" + formattedDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    public static String getCurrentDateTime() {
        String formattedDate = "";
        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
            formattedDate = df.format(c.getTime());
            System.out.println("date is" + formattedDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    public static String getCurrentDay(){
        String weekDay;
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
        Calendar calendar = Calendar.getInstance();
        weekDay = dayFormat.format(calendar.getTime());
        return weekDay;
    }

    public static boolean isGooglePlayServicesAvailable(Context context) {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, (Activity) context, 0).show();
            return false;
        }
    }

}
