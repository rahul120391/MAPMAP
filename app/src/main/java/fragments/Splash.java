package fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapmap.R;

import activities.MainActivity;

/**
 * Created by osx on 14/10/15.
 */
public class Splash extends Fragment {
    View view = null;
    Handler handler;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.fragment_splash, container, false);
            handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity)getActivity()).FragmentTransactions(new Welcome(),"welcome");
                }
            },5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

}
