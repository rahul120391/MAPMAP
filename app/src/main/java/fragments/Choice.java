package fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapmap.R;

import activities.MainActivity;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import commonUtils.Utils;

/**
 * Created by osx on 14/10/15.
 */
public class Choice extends Fragment {
    View view = null;

    @Bind(R.id.iv_shop_signup)
    ImageView iv_shop_signup;

    @Bind(R.id.iv_customer_signup)
    ImageView iv_customer_signup;

    @Bind(R.id.tv_store)
    TextView tv_store;

    @Bind(R.id.tv_customer)
    TextView tv_customer;

    String value;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.fragment_choice, container, false);
            ButterKnife.bind(this, view);
            tv_store.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            tv_customer.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            Bundle bundle = getArguments();
            if (bundle != null && bundle.getString("from").equalsIgnoreCase("login")) {
                value = bundle.getString("from");
                Spanned span = (Html.fromHtml("<b>" + "Shop " + "</b> "));
                String shopLogin = "<b>" + span + "</b> " + "Sign in";
                tv_store.setText(Html.fromHtml(shopLogin));
                span = (Html.fromHtml("<b>" + "Customer " + "</b> "));
                String customerLogin = "<b>" + span + "</b> " + "Sign in";
                tv_customer.setText(Html.fromHtml(customerLogin));
            } else {
                Spanned span = (Html.fromHtml("<b>" + "Shop " + "</b> "));
                String shopLogin = "<b>" + span + "</b> " + "Sign up";
                tv_store.setText(Html.fromHtml(shopLogin));
                span = (Html.fromHtml("<b>" + "Customer " + "</b> "));
                String customerLogin = "<b>" + span + "</b> " + "Sign up";
                tv_customer.setText(Html.fromHtml(customerLogin));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.iv_shop_signup)
    public void ShopSignUp() {
        try {
            if (value != null && value.equalsIgnoreCase("login")) {
                Login login=new Login();
                Bundle bundle=new Bundle();
                bundle.putString("from","shoplogin");
                login.setArguments(bundle);
                ((MainActivity) getActivity()).FragmentTransactions(login,"login");
            } else {
                ((MainActivity) getActivity()).FragmentTransactions(new ShopkeeperSignUp(),"shopsignup");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.iv_customer_signup)
    public void CustomerSignUp() {
        try {
            if (value != null && value.equalsIgnoreCase("login")) {
                Login login=new Login();
                Bundle bundle=new Bundle();
                bundle.putString("from","customerlogin");
                login.setArguments(bundle);
                ((MainActivity) getActivity()).FragmentTransactions(login,"login");
            } else {
               ((MainActivity) getActivity()).FragmentTransactions(new CustomerSignUp(),"customersignup");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
