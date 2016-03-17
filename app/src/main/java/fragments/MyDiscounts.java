package fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.mapmap.R;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

import activities.MainActivity;
import adapters.TabsPagerAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import commonUtils.Utils;

/**
 * Created by osx on 21/10/15.
 */
public class MyDiscounts extends android.support.v4.app.Fragment implements ViewPager.OnPageChangeListener {
    View view=null;

    /*@Bind(android.R.id.tabhost)
    FragmentTabHost tabhost;*/
    @Bind(R.id.iv_back)
    ImageView iv_back;

    @Bind(R.id.tv_screenname)
    TextView tv_screenname;

    //@Bind(R.id.viewpager)
    //ViewPager viewpager;

    @Bind(R.id.view_right)
    CardView view_right;

    @Bind(R.id.current_discount)
    TextView current_discount;

    @Bind(R.id.future_discount)
    TextView future_discount;

    @Bind(R.id.past_discount)
    TextView past_discount;

    // TabsPagerAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_mydiscounts,container,false);
        ButterKnife.bind(this, view);
        view_right.setVisibility(View.GONE);
        tv_screenname.setText(getString(R.string.my_discount));
        tv_screenname.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
        current_discount.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
        past_discount.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
        future_discount.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));

        Spanned span = (Html.fromHtml("<b>" + "Current " + "</b> "));
        String shopLogin = "<b>" + span + "</b> " + "Discount";
        current_discount.setText(Html.fromHtml(shopLogin));

        span = (Html.fromHtml("<b>" + "Past " + "</b> "));
        shopLogin = "<b>" + span + "</b> " + "Discount";
        past_discount.setText(Html.fromHtml(shopLogin));

        span = (Html.fromHtml("<b>" + "Future " + "</b> "));
        shopLogin = "<b>" + span + "</b> " + "Discount";
        future_discount.setText(Html.fromHtml(shopLogin));

        /*tabhost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);
        tabhost.addTab(tabhost.newTabSpec(getString(R.string.current_discount)).setIndicator(Indicator(getString(R.string.current_discount))), CurrentDiscount.class, null);
        tabhost.addTab(tabhost.newTabSpec(getString(R.string.future_discount)).setIndicator(Indicator(getString(R.string.future_discount))), FutureDiscount.class, null);
        tabhost.addTab(tabhost.newTabSpec(getString(R.string.past_discount)).setIndicator(Indicator(getString(R.string.past_discount))), PastDiscount.class, null);
        List<android.support.v4.app.Fragment> list=new ArrayList<>();
        list.add(new CurrentDiscount());
        list.add(new FutureDiscount());
        list.add(new PastDiscount());
        adapter=new TabsPagerAdapter(getFragmentManager(),list);
        //viewpager.setAdapter(adapter);
        //viewpager.addOnPageChangeListener(this);
        //viewpager.setOffscreenPageLimit(0);
        tabhost.setOnTabChangedListener(this);*/

        return view;
    }

    @OnClick(R.id.current_discount)
    public void CurrentDiscount() {

        Intent current = new Intent(getActivity(), CurrentDiscount.class);
        getActivity().startActivity(current);

    }

    @OnClick(R.id.future_discount)
    public void FutureDiscount() {
        Intent current = new Intent(getActivity(), FutureDiscount.class);
        getActivity().startActivity(current);
    }

    @OnClick(R.id.past_discount)
    public void PastDiscount() {
        Intent current = new Intent(getActivity(), PastDiscount.class);
        getActivity().startActivity(current);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public View Indicator(String text){
        View tabview=getActivity().getLayoutInflater().inflate(R.layout.tab_indicator,null);
        TextView tv_tab_header=(TextView)tabview.findViewById(R.id.tv_tab_header);
        tv_tab_header.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-BOLD.TTF"));
        tv_tab_header.setText(text);
        RelativeLayout layout_bg=(RelativeLayout)tabview.findViewById(R.id.layout_bg);
        if(text.equalsIgnoreCase(getString(R.string.past_discount))){
            layout_bg.setBackgroundResource(R.drawable.tab_select_unselect);
        }
        else{
            layout_bg.setBackgroundResource(R.drawable.tab_select_unselectt);
        }

        return tabview;
    }

    @OnClick(R.id.iv_back)
    public void Back(){
        getActivity().onBackPressed();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //int pos = this.viewpager.getCurrentItem();
        //this.tabhost.setCurrentTab(pos);
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

   /* @Override
    public void onTabChanged(String s) {
        int pos = this.tabhost.getCurrentTab();
        //this.viewpager.setCurrentItem(pos);
    }*/
}

