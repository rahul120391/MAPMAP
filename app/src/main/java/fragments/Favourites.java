package fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mapmap.R;
import com.nispok.snackbar.enums.SnackbarType;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import adapters.MyDiscountsAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import commonUtils.Utils;
import datamodel.DiscountInfo;
import parsework.CommonUtils;
import views.Effectstype;
import views.RotateProgressDialog;

/**
 * Created by osx on 05/02/16.
 */
public class Favourites extends Fragment {
    View view = null;

    @Bind(R.id.lv_fav)
    ListView lv_fav;
    private int skip = 0;
    MyDiscountsAdapter adapter;
    ArrayList<DiscountInfo> myfavourites = new ArrayList<>();
    @Bind(R.id.iv_back)
    ImageView iv_back;

    @Bind(R.id.view_right)
    CardView view_right;

    @Bind(R.id.tv_screenname)
    TextView tv_screenname;

    @Bind(R.id.swiperefresh)
    SwipeRefreshLayout swiperefresh;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favourite, container, false);
        ButterKnife.bind(this, view);
        view_right.setVisibility(View.GONE);
        tv_screenname.setText("Favourites");
        adapter = new MyDiscountsAdapter(getActivity(), myfavourites);
        lv_fav.setAdapter(adapter);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetFavourites();
            }
        });
        GetFavourites();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void GetFavourites() {
        if (Utils.checkInternetConnection(getActivity())) {
            int limit = 10;
            SharedPreferences prefs = getActivity().getSharedPreferences("UserData", 0);
            String objectid = prefs.getString("objectid", "");
            ParseQuery<ParseObject> fav = new ParseQuery<ParseObject>(CommonUtils.FAVOURITES);
            fav.whereEqualTo("personid", objectid);
            fav.setSkip(skip);
            fav.setLimit(limit);
            RotateProgressDialog.ShowDialog(getActivity());
            fav.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    RotateProgressDialog.DismissDialog();
                    swiperefresh.setRefreshing(false);
                    if (e == null) {
                        if (list.size() == 0) {
                            Utils.ShowSnackBar(getActivity(), "No favourites discounts", SnackbarType.SINGLE_LINE);
                        } else {
                            for (int i = 0; i < list.size(); i++) {
                                JSONArray favdiscounts = list.get(i).getJSONArray("favdiscounts");
                                for (int x = 0; x < favdiscounts.length(); x++) {
                                    try {
                                        DiscountInfo info = new DiscountInfo();
                                        JSONObject obj = favdiscounts.getJSONObject(x);
                                        info.setDiscount_location(obj.optString("location"));
                                        info.setStartdate(obj.optString("startdate"));
                                        info.setEnddate(obj.optString("enddate"));
                                        info.setDiscount_desc(obj.optString("description"));
                                        info.setDiscount_per(obj.optString("discountper"));
                                        info.setDiscount_category(obj.optString("category"));
                                        info.setDiscount_category_url(obj.optString("categoryurl"));
                                        info.setPhonenumber(obj.optString("phone"));
                                        myfavourites.add(info);
                                    } catch (Exception ee) {
                                        ee.printStackTrace();
                                    }
                                }
                            }
                            if (myfavourites.size() > 0) {
                                skip = skip + list.size();
                                Collections.sort(myfavourites, new Comparator<DiscountInfo>() {
                                    @Override
                                    public int compare(DiscountInfo d1, DiscountInfo d2) {
                                        return d1.getDiscount_category().compareTo(d2.getDiscount_category());
                                    }
                                });
                                adapter.notifyadapter(myfavourites);
                            } else {
                                Utils.ShowSnackBar(getActivity(), "No favourites discounts", SnackbarType.SINGLE_LINE);
                            }

                        }
                    } else {
                        Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, e.getMessage());
                    }
                }
            });

        } else {
            swiperefresh.setRefreshing(false);
            Utils.ShowSnackBar(getActivity(), getString(R.string.network_error), SnackbarType.SINGLE_LINE);
        }
    }

    @OnClick(R.id.iv_back)
    public void Back(){
        getActivity().onBackPressed();
    }

}
