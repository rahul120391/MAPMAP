package fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mapmap.R;
import com.viewpagerindicator.CirclePageIndicator;

import activities.MainActivity;
import adapters.CustomPagerAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by osx on 16/02/16.
 */
public class Welcome extends Fragment {

    View view=null;

    @Bind(R.id.pager)
    ViewPager pager;

    @Bind(R.id.indicator)
    CirclePageIndicator indicator;

    @Bind(R.id.tv_skip)
    TextView tv_skip;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_intro,null);
        ButterKnife.bind(this, view);
        CustomPagerAdapter adapter=new CustomPagerAdapter(getActivity());
        pager.setAdapter(adapter);
        indicator.setFillColor(ContextCompat.getColor(getActivity(), android.R.color.white));
        indicator.setStrokeColor(ContextCompat.getColor(getActivity(), android.R.color.white));
        indicator.setStrokeWidth(2);
        indicator.setPageColor(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        indicator.setRadius(10);
        indicator.setViewPager(pager);
        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("inside skip");
                ((MainActivity) getActivity()).FragmentTransactions(new Introduction(), "intro");
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
