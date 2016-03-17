package activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapmap.R;
import com.navdrawer.SimpleSideDrawer;
import commonUtils.Utils;
import fragments.AboutMapMap;
import fragments.AddDiscount;
import fragments.ChangePassword;
import fragments.ContactUs;
import fragments.LocationScreen;
import fragments.MoreInformation;
import fragments.MyDiscounts;
import fragments.RateApplication;
import fragments.ShopProfile;
import views.Effectstype;
import views.NiftyDialogBuilder;
import views.RotateProgressDialog;


public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {


    FragmentManager fragmentmanager;
    String data, address;

    SharedPreferences prefs;

    String type = null;

    public DrawerLayout getSlide_drawer() {
        return mDrawerLayout;
    }

    SimpleSideDrawer slide_drawer;
    LinearLayout layout_logout, layout_change_pass, layout_add_discount, layout_my_discounts;
    LinearLayout layout_about, layout_store_profile,layout_contactus, layout_rateapp,layout_moreinfo;
    NiftyDialogBuilder dialogBuilder;
    DrawerLayout mDrawerLayout;
    public static RelativeLayout mDrawerPane;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(DashboardActivity.this));
        setContentView(R.layout.activity_dashboard);
        prefs = getSharedPreferences("UserData", 0);
        type = prefs.getString("type", "");
        System.out.println("type" + type);
        slide_drawer = new SimpleSideDrawer(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.mainContent);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerPane = (RelativeLayout) findViewById(R.id.scroll);

        ActionBarDrawerToggle  mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.drawable.ic_ok, R.string.desc)
        {
            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
                System.out.println("inside open");
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d("onDrawerClosed", "onDrawerClosed: " + getTitle());
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        TextView tv_store_profile = (TextView)  findViewById(R.id.tv_store_profile);
        TextView tv_change_password = (TextView)  findViewById(R.id.tv_change_password);
        TextView tv_add_discount = (TextView)  findViewById(R.id.tv_add_discount);
        TextView tv_my_discount = (TextView)  findViewById(R.id.tv_my_discount);
        TextView tv_salesbymap = (TextView)  findViewById(R.id.tv_salesbymap);
        TextView tv_rate_app = (TextView)  findViewById(R.id.tv_rate_app);
        TextView tv_contact_us = (TextView)  findViewById(R.id.tv_contact_us);
        TextView tv_more_info = (TextView)  findViewById(R.id.tv_more_info);
        TextView tv_logout = (TextView)  findViewById(R.id.tv_logout);
        layout_logout = (LinearLayout)  findViewById(R.id.layout_logout);
        layout_logout.setOnClickListener(this);
        layout_change_pass = (LinearLayout)  findViewById(R.id.layout_change_pass);
        layout_change_pass.setOnClickListener(this);
        layout_add_discount = (LinearLayout)  findViewById(R.id.layout_add_discount);
        layout_add_discount.setOnClickListener(this);
        layout_my_discounts = (LinearLayout)  findViewById(R.id.layout_my_discounts);
        layout_my_discounts.setOnClickListener(this);
        layout_about = (LinearLayout)  findViewById(R.id.layout_about);
        layout_about.setOnClickListener(this);
        layout_store_profile = (LinearLayout)  findViewById(R.id.layout_store_profile);
        layout_store_profile.setOnClickListener(this);
        layout_contactus=(LinearLayout) findViewById(R.id.layout_contactus);
        layout_contactus.setOnClickListener(this);
        layout_rateapp=(LinearLayout) findViewById(R.id.layout_rateapp);
        layout_rateapp.setOnClickListener(this);
        layout_moreinfo=(LinearLayout) findViewById(R.id.layout_moreinfo);
        layout_moreinfo.setOnClickListener(this);
        tv_store_profile.setTypeface(Utils.SetFont(this, "ROBOTO-REGULAR.TTF"));
        tv_change_password.setTypeface(Utils.SetFont(this, "ROBOTO-REGULAR.TTF"));
        tv_add_discount.setTypeface(Utils.SetFont(this, "ROBOTO-REGULAR.TTF"));
        tv_salesbymap.setTypeface(Utils.SetFont(this, "ROBOTO-REGULAR.TTF"));
        tv_my_discount.setTypeface(Utils.SetFont(this, "ROBOTO-REGULAR.TTF"));
        tv_rate_app.setTypeface(Utils.SetFont(this, "ROBOTO-REGULAR.TTF"));
        tv_contact_us.setTypeface(Utils.SetFont(this, "ROBOTO-REGULAR.TTF"));
        tv_more_info.setTypeface(Utils.SetFont(this, "ROBOTO-REGULAR.TTF"));
        tv_logout.setTypeface(Utils.SetFont(this, "ROBOTO-REGULAR.TTF"));

        if (type.equalsIgnoreCase("customer")) {
            int user_type = prefs.getInt("user_type", 0);
            switch (user_type) {
                case 1:
                    tv_store_profile.setText(getString(R.string.my_profile));
                    layout_add_discount.setVisibility(View.GONE);
                    layout_my_discounts.setVisibility(View.GONE);
                    break;
                case 2:
                    tv_store_profile.setText(getString(R.string.my_profile));
                    layout_add_discount.setVisibility(View.GONE);
                    layout_my_discounts.setVisibility(View.GONE);
                    layout_change_pass.setVisibility(View.GONE);
                    break;
            }
        }

        fragmentmanager = getSupportFragmentManager();
        FragmentTransactions(new LocationScreen(),"location");

    }


    @Override
    public void onClick(View view) {
        //slide_drawer.closeRightSide();

        mDrawerLayout.closeDrawer(mDrawerPane);
        switch (view.getId()) {
            case R.id.layout_logout:
                try {
                    ShowConfirmationDialog(DashboardActivity.this, Effectstype.SlideBottom);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.layout_my_discounts:
                try {
                    FragmentTransactions(new MyDiscounts(), "mydiscounts");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.layout_about:
                try {
                    FragmentTransactions(new AboutMapMap(),"aboutmapmap");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.layout_change_pass:
                try {
                    FragmentTransactions(new ChangePassword(), "changepassword");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.layout_store_profile:
                try {
                    System.out.println("store profile clicked");
                    if(type.equalsIgnoreCase("shopkeeper")){
                        FragmentTransactions(new ShopProfile(),"shopprofile");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.layout_add_discount:
                try {
                    FragmentTransactions(new AddDiscount(), "adddiscount");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.layout_contactus:
                try{
                    FragmentTransactions(new ContactUs(),"contactus");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.layout_rateapp:
                try{
                    FragmentTransactions(new RateApplication(),"rateapp");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.layout_moreinfo:
                try{
                    FragmentTransactions(new MoreInformation(),"moreinfo");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    public void ShowConfirmationDialog(Context context, Effectstype effect) {
        if (dialogBuilder == null || !dialogBuilder.isShowing()) {
            dialogBuilder = NiftyDialogBuilder.getInstance(context);
            dialogBuilder.withEffect(effect);
            dialogBuilder.withDialogColor(ContextCompat.getColor(context, R.color.White));
            dialogBuilder.withDividerColor(ContextCompat.getColor(context, R.color.transparent));
            dialogBuilder.setTopPanelBackground(ContextCompat.getColor(context, R.color.transparent));
            dialogBuilder.setParentPanelBackground(ContextCompat.getColor(context, R.color.transparent));
            dialogBuilder.setCustomView(R.layout.layout_logout_confirmation, (Activity) context);
            TextView tv_title = (TextView) dialogBuilder.findViewById(R.id.tv_title);
            tv_title.setTypeface(Utils.SetFont(context, "ROBOTO-REGULAR.TTF"));
            tv_title.setText(getString(R.string.confirmation));
            TextView tv_message = (TextView) dialogBuilder.findViewById(R.id.tv_message);
            tv_message.setTypeface(Utils.SetFont(context, "ROBOTO-REGULAR.TTF"));
            tv_message.setText(getString(R.string.logout_confirm));
            Button btn_yes = (Button) dialogBuilder.findViewById(R.id.btn_yes);
            btn_yes.setTypeface(Utils.SetFont(context, "ROBOTO-REGULAR.TTF"));
            btn_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogBuilder.dismiss();
                    try {
                        SharedPreferences prefs = getSharedPreferences("UserData", 0);
                        prefs.edit().clear().commit();
                        Intent logout = new Intent(DashboardActivity.this
                                , MainActivity.class);
                        logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(logout);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            Button btn_no = (Button) dialogBuilder.findViewById(R.id.btn_no);
            btn_no.setTypeface(Utils.SetFont(context, "ROBOTO-REGULAR.TTF"));
            btn_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogBuilder.dismiss();
                }
            });
            dialogBuilder.show();
        }


    }

    /**
     * This method is used for replacing one fragment with another
     *@param tag-tag to identify fragment
     * @param fragment -fragment to replace
     */
    public void FragmentTransactions(Fragment fragment,String tag) {
        Fragment frag=fragmentmanager.findFragmentByTag(tag);
        if(frag==null){
            FragmentTransaction fragmentTransaction = fragmentmanager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.enter_in, R.anim.enter_out, R.anim.exit_in, R.anim.exit_out);
            fragmentTransaction.replace(R.id.fragment_dashboard_container, fragment,tag);
            fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.commitAllowingStateLoss();
        }
        else{
            fragmentmanager.popBackStack(tag,0);
        }
        System.out.println("fragment count"+fragmentmanager.getBackStackEntryCount());
    }

    @Override
    public void onBackPressed() {
        System.out.println("fragment count"+fragmentmanager.getBackStackEntryCount());
        RotateProgressDialog.DismissDialog();
        if (fragmentmanager.getBackStackEntryCount() > 1) {
            fragmentmanager.popBackStack();
        } else {
            if (!slide_drawer.isClosed()) {
                slide_drawer.closeRightSide();
            } else {
                Exit(this, Effectstype.SlideBottom);
            }
        }
    }

    public void Exit(Context context, Effectstype effect) {
        if (dialogBuilder == null || !dialogBuilder.isShowing()) {
            dialogBuilder = NiftyDialogBuilder.getInstance(context);
            dialogBuilder.withEffect(effect);
            dialogBuilder.withDialogColor(ContextCompat.getColor(DashboardActivity.this,R.color.White));
            dialogBuilder.withDividerColor(ContextCompat.getColor(DashboardActivity.this,android.R.color.transparent));
            dialogBuilder.setTopPanelBackground(ContextCompat.getColor(DashboardActivity.this,android.R.color.transparent));
            dialogBuilder.setParentPanelBackground(ContextCompat.getColor(DashboardActivity.this,android.R.color.transparent));
            dialogBuilder.setCustomView(R.layout.confirmation_dialog_view, (Activity) context);
            TextView tv_title = (TextView) dialogBuilder.findViewById(R.id.tv_title);
            tv_title.setTypeface(Utils.SetFont(context, "ROBOTO-REGULAR.TTF"));
            tv_title.setText(getString(R.string.confirmation));
            TextView tv_message = (TextView) dialogBuilder.findViewById(R.id.tv_message);
            tv_message.setTypeface(Utils.SetFont(context, "ROBOTO-REGULAR.TTF"));
            tv_message.setText(getString(R.string.exit_app));
            Button btn_yes = (Button) dialogBuilder.findViewById(R.id.btn_yes);
            btn_yes.setTypeface(Utils.SetFont(context, "ROBOTO-REGULAR.TTF"));
            btn_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogBuilder.dismiss();
                    finish();
                    overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
                }
            });
            Button btn_no = (Button) dialogBuilder.findViewById(R.id.btn_no);
            btn_no.setTypeface(Utils.SetFont(context, "ROBOTO-REGULAR.TTF"));
            btn_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogBuilder.dismiss();
                }
            });
            dialogBuilder.show();
        }


    }


}
