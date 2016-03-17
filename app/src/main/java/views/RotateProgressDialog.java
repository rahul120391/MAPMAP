package views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.mapmap.R;

/**
 * Created by osx on 01/01/16.
 */
public class RotateProgressDialog {

    public static Dialog dialog;
    public static ImageView iv_loading;
    public static  void ShowDialog(Context context){
        dialog=new Dialog(context);

        dialog.setTitle("");
        DismissDialog();
        if(!dialog.isShowing()){
            if(android.os.Build.VERSION.SDK_INT<21){
                int divierId = dialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                View divider = dialog.findViewById(divierId);
                if(divider!=null){
                    divider.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
                }

            }
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.layout_progress_dialog);
            Animation anim= AnimationUtils.loadAnimation(context,R.anim.progress_rotator);
            iv_loading=(ImageView)dialog.findViewById(R.id.iv_loading);
            iv_loading.setAnimation(anim);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent)));
            dialog.show();
        }

    }

    public static void DismissDialog(){
        if(dialog!=null){
            if(iv_loading!=null){
                iv_loading.clearAnimation();
            }

            dialog.dismiss();
        }
    }
}
