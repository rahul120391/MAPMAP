package fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mapmap.R;
import com.nispok.snackbar.enums.SnackbarType;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.joda.time.LocalDate;

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
import views.Progressdialog;
import views.RotateProgressDialog;

/**
 * Created by osx on 21/10/15.
 */
public class PastDiscount extends Activity {

    @Bind(R.id.iv_back)
    ImageView iv_back;

    @Bind(R.id.view_right)
    CardView view_right;

    @Bind(R.id.tv_screenname)
    TextView tv_screenname;

    @Bind(R.id.swiperefresh)
    SwipeRefreshLayout swiperefresh;
    View view=null;

    SharedPreferences prefs;

    ParseQuery<ParseObject> discounts;

    List<DiscountInfo> past_discounts=new ArrayList<>();
    MyDiscountsAdapter adapter;

    @Bind(R.id.lv_past_discount)
    ListView lv_past_discount;
    private int skip = 0;
    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_past_discount);
        ButterKnife.bind(this);
        view_right.setVisibility(View.GONE);
        tv_screenname.setText("Past Discount");
        prefs = getSharedPreferences("UserData", 0);
        System.out.println("inside past discount");
        PastDiscounts();
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                PastDiscounts();
            }
        });
        adapter=new MyDiscountsAdapter(PastDiscount.this,past_discounts);
        lv_past_discount.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(discounts!=null){
            discounts.cancel();
        }
    }

    @OnClick(R.id.iv_back)
    public void Back(){
        onBackPressed();
    }

    public void PastDiscounts(){
        if(Utils.checkInternetConnection(PastDiscount.this)){
            RotateProgressDialog.ShowDialog(PastDiscount.this);
            int limit = 10;
            String profileobjectid = prefs.getString("profileobjectid", "");
            ParseObject profile = ParseObject.createWithoutData(CommonUtils.SHOPKEEPER_PROFILE_DATA, profileobjectid);
            discounts = new ParseQuery<ParseObject>(CommonUtils.DISCOUNT_TABLE);
            discounts.whereEqualTo(CommonUtils.DISCOUNT_TABLE, profile);
            discounts.setSkip(skip);
            discounts.setLimit(limit);
            discounts.orderByAscending("discount_category");
            discounts.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    RotateProgressDialog.DismissDialog();
                    swiperefresh.setRefreshing(false);
                    if (e == null) {
                        if (list.size() == 0) {
                            Utils.ShowSnackBar(PastDiscount.this, "No discounts", SnackbarType.SINGLE_LINE);
                        } else {
                            for (ParseObject dis : list) {
                                    String loc=dis.getString("loc");
                                    String startdate = dis.get("startdate").toString();
                                    String enddate = dis.get("enddate").toString();
                                    int endday = Integer.parseInt(enddate.split("/")[0]);
                                    int endmonth = Integer.parseInt(enddate.split("/")[1]);
                                    int endyear = Integer.parseInt(enddate.split("/")[2]);
                                    LocalDate end_date = new LocalDate(endyear, endmonth, endday);
                                    String currentddate = Utils.getCurrentDate();
                                    int currentday = Integer.parseInt(currentddate.split("/")[0]);
                                    int currentmonth = Integer.parseInt(currentddate.split("/")[1]);
                                    int currentyear = Integer.parseInt(currentddate.split("/")[2]);
                                    LocalDate current_date = new LocalDate(currentyear, currentmonth, currentday);
                                    if (current_date.isAfter(end_date)) {
                                        DiscountInfo info=new DiscountInfo();
                                        info.setStartdate(startdate);
                                        info.setEnddate(enddate);
                                        info.setDiscount_category(dis.getString("discount_category"));
                                        info.setDiscount_per(dis.getString("discount_per"));
                                        info.setDiscount_location(loc);
                                        info.setPhonenumber(dis.getString("phone"));
                                        info.setDiscount_desc(dis.getString("discount_desc"));
                                        info.setDiscount_category_url(dis.getString("discount_category_url"));
                                        past_discounts.add(info);
                                    }

                                }
                                if (past_discounts.size() > 0) {
                                    skip = skip + list.size();
                                    Collections.sort(past_discounts, new Comparator<DiscountInfo>() {
                                        @Override
                                        public int compare(DiscountInfo d1, DiscountInfo d2) {
                                            return d1.getDiscount_category().compareTo(d2.getDiscount_category());
                                        }
                                    });
                                    adapter.notifyadapter(past_discounts);
                                } else {
                                    Utils.ShowSnackBar(PastDiscount.this, "No past discounts yet", SnackbarType.SINGLE_LINE);
                                }

                            }
                            System.out.println("list" + list.size());

                        }
                        else{
                            Utils.ShowCustomDialog(PastDiscount.this, Effectstype.SlideBottom, e.getMessage());
                        }
                    }
                }

                );
            }
            else{
            swiperefresh.setRefreshing(false);
            Utils.ShowSnackBar(PastDiscount.this, getString(R.string.network_error), SnackbarType.SINGLE_LINE);

        }


    }
}
