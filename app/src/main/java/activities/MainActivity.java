package activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.mapmap.R;
import com.navdrawer.SimpleSideDrawer;

import commonUtils.UnCaughtException;
import fragments.CustomerSignUp;
import fragments.Splash;


public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentmanager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(MainActivity.this));
        SharedPreferences prefs=getSharedPreferences("UserData",0);
        if(prefs.getString("objectid","")!=null && !prefs.getString("objectid","").equalsIgnoreCase(""))
        {
            finish();
            Intent intent=new Intent(MainActivity.this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.enter_in, R.anim.enter_out);
        }
        else{
            fragmentmanager=getSupportFragmentManager();
            FragmentTransactions(new Splash(),"splash");
        }
    }

    /**
     * This method is used for replacing one fragment with another
     *@param tag-tag to identify fragment
     * @param fragment -fragment to replace
     */
    public void FragmentTransactions( Fragment fragment,String tag) {
        Fragment frag=fragmentmanager.findFragmentByTag(tag);
        if(frag==null){
            FragmentTransaction fragmentTransaction = fragmentmanager.beginTransaction();
            //fragmentTransaction.setCustomAnimations(R.anim.enter_in, R.anim.enter_out, R.anim.exit_in, R.anim.exit_out);
            fragmentTransaction.replace(R.id.fragment_container, fragment,tag);
            fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.commitAllowingStateLoss();
        }
        else{
            fragmentmanager.popBackStack(tag,0);
        }
    }

    @Override
    public void onBackPressed() {
        if (fragmentmanager.getBackStackEntryCount() > 2) {
            fragmentmanager.popBackStack();
        } else {
            finish();
            overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            if(CustomerSignUp.callbackManager!=null){
                CustomerSignUp.callbackManager.onActivityResult(requestCode, resultCode, data);
            }
        }

    }
}

//database tables
//https://www.parse.com/apps/mapmap--4/collections#class/Discounts