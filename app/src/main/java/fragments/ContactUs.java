package fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
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
 * Created by osx on 28/01/16.
 */
public class ContactUs extends Fragment {
    View view = null;

    @Bind(R.id.tv_screenname)
    TextView tv_screenname;

    @Bind(R.id.view_right)
    CardView view_right;

    @Bind(R.id.iv_back)
    ImageView iv_back;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contactus, container,false);
        ButterKnife.bind(this, view);
        view_right.setVisibility(View.GONE);
        tv_screenname.setText(getString(R.string.contact_us));
        tv_screenname.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
        return view;
    }

    @OnClick(R.id.iv_back)
    public void Back() {
        getActivity().onBackPressed();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
