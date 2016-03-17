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
public class CurrentDiscount extends Activity {
    View view = null;

    @Bind(R.id.lv_current_discount)
    ListView lv_current_discount;

    @Bind(R.id.view_right)
    CardView view_right;
    SharedPreferences prefs;
    @Bind(R.id.iv_back)
    ImageView iv_back;
    @Bind(R.id.tv_screenname)
    TextView tv_screenname;

    @Bind(R.id.swiperefresh)
    SwipeRefreshLayout swiperefresh;

    ParseQuery<ParseObject> discounts;
    List<DiscountInfo> current_discounts = new ArrayList<>();
    MyDiscountsAdapter adapter;
    private int skip = 0;
    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_current_discount);

        ButterKnife.bind(this);
        view_right.setVisibility(View.GONE);
        prefs = getSharedPreferences("UserData", 0);
        System.out.println("inside current discount");
        tv_screenname.setText("Current Discount");
        adapter = new MyDiscountsAdapter(CurrentDiscount.this, current_discounts);
        lv_current_discount.setAdapter(adapter);
        CurrentDiscounts();
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CurrentDiscounts();
            }
        });

        System.out.println("value of skip"+skip);

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

    public void CurrentDiscounts() {
        if (Utils.checkInternetConnection(CurrentDiscount.this)) {
            RotateProgressDialog.ShowDialog(CurrentDiscount.this);
            int limit = 10;
            String profileobjectid = prefs.getString("profileobjectid", "");
            ParseObject profile = ParseObject.createWithoutData(CommonUtils.SHOPKEEPER_PROFILE_DATA, profileobjectid);
            discounts = new ParseQuery<ParseObject>(CommonUtils.DISCOUNT_TABLE);
            discounts.whereEqualTo(CommonUtils.DISCOUNT_TABLE, profile);
            discounts.setSkip(skip);
            System.out.println("skip value"+skip);
            discounts.setLimit(limit);
            discounts.orderByAscending("discount_category");
            discounts.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    RotateProgressDialog.DismissDialog();
                    swiperefresh.setRefreshing(false);
                    if (e == null) {
                        if (list.size() == 0) {
                            Utils.ShowSnackBar(CurrentDiscount.this, "No discounts", SnackbarType.SINGLE_LINE);
                        } else {
                            for (ParseObject dis : list) {
                                    String loc=dis.getString("loc");
                                    System.out.println("location"+loc);
                                    String startdate = dis.get("startdate").toString();
                                    int startday = Integer.parseInt(startdate.split("/")[0]);
                                    int startmonth = Integer.parseInt(startdate.split("/")[1]);
                                    int startyear = Integer.parseInt(startdate.split("/")[2]);
                                    LocalDate start_date = new LocalDate(startyear, startmonth, startday);
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
                                    if ((current_date.isAfter(start_date) || current_date.isEqual(start_date)) && (current_date.isBefore(end_date) || current_date.isEqual(end_date))) {
                                        DiscountInfo info=new DiscountInfo();
                                        info.setStartdate(startdate);
                                        info.setEnddate(enddate);
                                        info.setDiscount_category(dis.getString("discount_category"));
                                        info.setDiscount_per(dis.getString("discount_per"));
                                        info.setDiscount_location(loc);
                                        info.setPhonenumber(dis.getString("phone"));
                                        info.setDiscount_desc(dis.getString("discount_desc"));
                                        info.setDiscount_category_url(dis.getString("discount_category_url"));
                                        current_discounts.add(info);
                                    }

                            }
                            if (current_discounts.size() > 0) {
                                skip = skip + list.size();
                                Collections.sort(current_discounts, new Comparator<DiscountInfo>() {
                                    @Override
                                    public int compare(DiscountInfo d1, DiscountInfo d2) {
                                        return d1.getDiscount_category().compareTo(d2.getDiscount_category());
                                    }
                                });
                                adapter.notifyadapter(current_discounts);
                            } else {
                                Utils.ShowSnackBar(CurrentDiscount.this, "No current discounts yet", SnackbarType.SINGLE_LINE);
                            }

                        }
                        System.out.println("list" + list.size());

                    } else {

                        Utils.ShowCustomDialog(CurrentDiscount.this, Effectstype.SlideBottom, e.getMessage());
                    }
                }
            });
        } else {
            Utils.ShowSnackBar(CurrentDiscount.this, getString(R.string.network_error), SnackbarType.SINGLE_LINE);

        }


    }
}
