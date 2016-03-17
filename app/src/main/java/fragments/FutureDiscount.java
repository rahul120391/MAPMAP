package fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
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
public class FutureDiscount extends Activity {
    View view = null;

    @Bind(R.id.iv_back)
    ImageView iv_back;

    @Bind(R.id.view_right)
    CardView view_right;

    @Bind(R.id.tv_screenname)
    TextView tv_screenname;

    SharedPreferences prefs;

    ParseQuery<ParseObject> discounts;

    List<DiscountInfo> future_discounts = new ArrayList<>();
    MyDiscountsAdapter adapter;


    @Bind(R.id.lv_future_discount)
    ListView lv_future_discount;

    @Bind(R.id.swiperefresh)
    SwipeRefreshLayout swiperefresh;
    private int skip = 0;

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_future_discount);
        ButterKnife.bind(this);
        prefs = getSharedPreferences("UserData", 0);
        view_right.setVisibility(View.GONE);
        tv_screenname.setText("Future Discount");
        System.out.println("inside future discount");
        FutureDiscounts();
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FutureDiscounts();
            }
        });
        adapter = new MyDiscountsAdapter(FutureDiscount.this, future_discounts);
        lv_future_discount.setAdapter(adapter);
    }

    @OnClick(R.id.iv_back)
    public void Back() {
        onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (discounts != null) {
            discounts.cancel();
        }
    }

    public void FutureDiscounts() {
        if (Utils.checkInternetConnection(FutureDiscount.this)) {
            System.out.println("inside future");
            RotateProgressDialog.ShowDialog(FutureDiscount.this);
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
                            Utils.ShowSnackBar(FutureDiscount.this, "No discounts", SnackbarType.SINGLE_LINE);
                        } else {
                            for (ParseObject dis : list) {
                                    String loc=dis.getString("loc");
                                    String enddate = dis.get("enddate").toString();
                                    String startdate = dis.get("startdate").toString();
                                    int startday = Integer.parseInt(startdate.split("/")[0]);
                                    int startmonth = Integer.parseInt(startdate.split("/")[1]);
                                    int startyear = Integer.parseInt(startdate.split("/")[2]);
                                    LocalDate start_date = new LocalDate(startyear, startmonth, startday);
                                    String currentddate = Utils.getCurrentDate();
                                    int currentday = Integer.parseInt(currentddate.split("/")[0]);
                                    int currentmonth = Integer.parseInt(currentddate.split("/")[1]);
                                    int currentyear = Integer.parseInt(currentddate.split("/")[2]);
                                    LocalDate current_date = new LocalDate(currentyear, currentmonth, currentday);
                                    if (current_date.isBefore(start_date)) {
                                        DiscountInfo info=new DiscountInfo();
                                        info.setStartdate(startdate);
                                        info.setEnddate(enddate);
                                        info.setDiscount_category(dis.getString("discount_category"));
                                        info.setDiscount_per(dis.getString("discount_per"));
                                        info.setDiscount_location(loc);
                                        info.setPhonenumber(dis.getString("phone"));
                                        info.setDiscount_desc(dis.getString("discount_desc"));
                                        info.setDiscount_category_url(dis.getString("discount_category_url"));
                                        future_discounts.add(info);
                                    }

                            }
                            if (future_discounts.size() > 0) {
                                skip = skip + list.size();
                                Collections.sort(future_discounts, new Comparator<DiscountInfo>() {
                                    @Override
                                    public int compare(DiscountInfo d1, DiscountInfo d2) {
                                        return d1.getDiscount_category().compareTo(d2.getDiscount_category());
                                    }
                                });
                                adapter.notifyadapter(future_discounts);
                            } else {
                                Utils.ShowSnackBar(FutureDiscount.this, "No future discounts yet", SnackbarType.SINGLE_LINE);
                            }

                        }
                        System.out.println("list" + list.size());

                    } else {

                        Utils.ShowCustomDialog(FutureDiscount.this, Effectstype.SlideBottom, e.getMessage());
                    }
                }
            });
        } else {
            Utils.ShowSnackBar(FutureDiscount.this, getString(R.string.network_error), SnackbarType.SINGLE_LINE);

        }


    }
}
