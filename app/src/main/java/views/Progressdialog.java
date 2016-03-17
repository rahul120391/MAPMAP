package views;

import android.app.AlertDialog;
import android.content.Context;


import com.mapmap.R;

import dmax.dialog.SpotsDialog;

/**
 * Created by osx on 29/07/15.
 */
public class Progressdialog {
    static AlertDialog dialog;

    public static void ShowDialog(Context context) {
        dialog = new SpotsDialog(context, R.style.Custom);
        DismissDialog();
        if (!dialog.isShowing()) {
            dialog.setTitle(context.getString(R.string.dialog_message));
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    public static void DismissDialog() {
        if(dialog!=null){
            dialog.dismiss();
        }
    }

}
