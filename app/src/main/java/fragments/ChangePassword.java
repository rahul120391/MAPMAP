package fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapmap.R;
import com.nispok.snackbar.enums.SnackbarType;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import commonUtils.Utils;
import parsework.CommonUtils;
import views.Effectstype;
import views.RotateProgressDialog;

/**
 * Created by osx on 22/10/15.
 */
public class ChangePassword extends Fragment {

    View view = null;

    @Bind(R.id.tv_screenname)
    TextView tv_screenname;

    @Bind(R.id.et_new_password)
    EditText et_new_password;

    @Bind(R.id.et_current_password)
    EditText et_current_password;

    @Bind(R.id.et_confirm_password)
    EditText et_confirm_password;

    @Bind(R.id.btn_submit)
    Button btn_submit;

    @Bind(R.id.iv_back)
    ImageView iv_back;

    @Bind(R.id.view_right)
    CardView view_right;

    SharedPreferences prefs;
    String current_password;
    ParseObject object;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_changepassword, container, false);
        ButterKnife.bind(this, view);
        view_right.setVisibility(View.GONE);
        tv_screenname.setText(getString(R.string.change_pass));
        tv_screenname.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
        et_new_password.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
        et_current_password.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
        et_confirm_password.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
        prefs = getActivity().getSharedPreferences("UserData", 0);
        current_password = prefs.getString("password", "");

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btn_submit)
    public void submit() {
        Utils.HideKeyboard(getActivity());
        System.out.println("current password" + current_password);
        if (!et_current_password.getText().toString().trim().equalsIgnoreCase(current_password)) {
            Utils.ShowSnackBar(getActivity(), getString(R.string.current_pass_error), SnackbarType.MULTI_LINE);
        } else if (et_new_password.getText().toString().trim().length() < 6) {
            Utils.ShowSnackBar(getActivity(), getString(R.string.enter_new_password), SnackbarType.SINGLE_LINE);
        } else if (et_new_password.getText().toString().trim().equalsIgnoreCase(et_current_password.getText().toString().trim())) {
            Utils.ShowSnackBar(getActivity(), getString(R.string.new_current_pass_error), SnackbarType.MULTI_LINE);
        } else if (!et_confirm_password.getText().toString().trim().equalsIgnoreCase(et_new_password.getText().toString().trim())) {
            Utils.ShowSnackBar(getActivity(), getString(R.string.confirm_pass_error), SnackbarType.MULTI_LINE);
        } else {
            if (Utils.checkInternetConnection(getActivity())) {
                ParseObject object = null;
                switch (prefs.getString("type", "")) {
                    case "shopkeeper":
                        object = ParseObject.createWithoutData(CommonUtils.SHOPKEEPER_TABLE, prefs.getString("objectid", ""));
                        break;
                    case "customer":
                        object = ParseObject.createWithoutData(CommonUtils.CUSTOMER_PROFILE_DATA, prefs.getString("objectid", ""));
                        break;
                }
                RotateProgressDialog.ShowDialog(getActivity());
                object.put("password", et_new_password.getText().toString().trim());
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        RotateProgressDialog.DismissDialog();
                        if (e == null) {
                            Utils.ShowSnackBar(getActivity(), getString(R.string.pass_Save), SnackbarType.SINGLE_LINE);
                            prefs.edit().putString("password", et_new_password.getText().toString().trim()).commit();
                            et_confirm_password.setText("");
                            et_new_password.setText("");
                            et_current_password.setText("");
                            getActivity().onBackPressed();
                        } else {
                            Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, e.getMessage());
                        }
                    }
                });
            } else {
                Utils.ShowSnackBar(getActivity(), getString(R.string.network_error), SnackbarType.SINGLE_LINE);
            }


        }
    }

    @OnClick(R.id.iv_back)
    public void Back() {
        getActivity().onBackPressed();
    }
}
