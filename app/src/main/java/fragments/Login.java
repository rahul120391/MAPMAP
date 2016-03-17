package fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;


import com.mapmap.R;
import com.nispok.snackbar.enums.SnackbarType;

import java.util.HashMap;
import java.util.Map;

import activities.MainActivity;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import commonUtils.Utils;
import parsework.CommonUtils;

/**
 * Created by osx on 14/10/15.
 */
public class Login extends Fragment {
    View view = null;
    String from = null;

    @Bind(R.id.et_email)
    EditText et_email;

    @Bind(R.id.et_password)
    EditText et_password;

    @Bind(R.id.btn_login)
    Button btn_login;

    @Bind(R.id.txt_forgot)
    TextView txt_forgot;

    @Bind(R.id.scrollView)
    ScrollView scrollView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.fragment_login, container, false);
            ButterKnife.bind(this, view);
            et_email.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            et_password.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            btn_login.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            txt_forgot.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
           // Bundle bundle = getArguments();
            //from = bundle.getString("from");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }



    @OnClick(R.id.btn_login)
    public void Login() {
        try {
            Utils.HideKeyboard(getActivity());
            if (!Patterns.EMAIL_ADDRESS.matcher(et_email.getText().toString().trim()).matches()) {
                Utils.ShowSnackBar(getActivity(), getString(R.string.error_email), SnackbarType.SINGLE_LINE);
            } else if (et_password.getText().toString().trim().length() < 6) {
                Utils.ShowSnackBar(getActivity(), getString(R.string.error_password), SnackbarType.SINGLE_LINE);
            } else {
                Map<String, Object> values = new HashMap<>();
                values.put("email", et_email.getText().toString().trim());
                values.put("password", et_password.getText().toString().trim());
               /* if (from.equalsIgnoreCase("shoplogin")) {
                    values.put("type", "shopkeeper");
                } else if (from.equalsIgnoreCase("customerlogin")) {
                    values.put("type", "customer");
                }
                */
                CommonUtils.Loginn(getActivity(), values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.txt_forgot)
    public void ForgotPassword() {
        Forgotpassword password = new Forgotpassword();
        Bundle bundle = new Bundle();
        bundle.putString("from", from);
        password.setArguments(bundle);
        ((MainActivity) getActivity()).FragmentTransactions(password,"password");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
