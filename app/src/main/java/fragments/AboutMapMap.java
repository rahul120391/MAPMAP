package fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapmap.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import commonUtils.Utils;

/**
 * Created by osx on 22/10/15.
 */
public class AboutMapMap extends Fragment {

    View view=null;

    @Bind(R.id.tv_name)
    TextView tv_name;

    @Bind(R.id.iv_back)
    ImageView iv_back;

    @Bind(R.id.tv_screenname)
    TextView tv_screenname;

    @Bind(R.id.view_right)
    CardView view_right;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_about,container,false);
        ButterKnife.bind(this, view);
        view_right.setVisibility(View.GONE);
        tv_screenname.setText(getString(R.string.heading_about));
        tv_screenname.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
        tv_name.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-BOLD.TTF"));
        tv_name.setText(Html.fromHtml("<font color='#000'>"+getString(R.string.heading_about)+"</font>"+"  "+"<font color='#63b9d4'>"+getString(R.string.mapmap)+"</font>"));
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.iv_back)
    public void Back(){
        getActivity().onBackPressed();
    }
}
