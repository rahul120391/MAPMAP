package fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mapmap.R;
import com.nispok.snackbar.enums.SnackbarType;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import commonUtils.Utils;
import parsework.CommonUtils;
import views.Progressdialog;
import views.RotateProgressDialog;

/**
 * Created by osx on 15/10/15.
 */
public class Forgotpassword extends android.support.v4.app.Fragment {
    View view = null;
    String from = null;

    @Bind(R.id.et_email)
    EditText et_email;

    @Bind(R.id.btn_submit)
    Button btn_submit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.fragment_forgotpassword, container, false);
            ButterKnife.bind(this, view);
            et_email.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            btn_submit.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));

            Bundle bundle = getArguments();
            from = bundle.getString("from");
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

    @OnClick(R.id.btn_submit)
    public void Submit() {
        try {
            Utils.HideKeyboard(getActivity());
            if (!Patterns.EMAIL_ADDRESS.matcher(et_email.getText().toString().trim()).matches()) {
                Utils.ShowSnackBar(getActivity(), getString(R.string.error_email), SnackbarType.SINGLE_LINE);
            } else {
                ParseQuery<ParseObject> query=null;
                System.out.println("===> from = "+from);
              //  from="shoplogin";
                switch (from){
                    case "shoplogin":
                        query = new ParseQuery<ParseObject>(CommonUtils.SHOPKEEPER_TABLE);
                        break;
                    case "customerlogin":
                        query = new ParseQuery<ParseObject>(CommonUtils.CUSTOMER_TABLE);
                        break;
                }
                query.include("profile_data");
                query.whereEqualTo("email", et_email.getText().toString().trim());
                if (Utils.checkInternetConnection(getActivity())) {
                    RotateProgressDialog.ShowDialog(getActivity());
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {
                            RotateProgressDialog.DismissDialog();
                            if (e == null) {
                                if (list.size() == 0) {
                                    Utils.ShowSnackBar(getActivity(), getString(R.string.no_user), SnackbarType.SINGLE_LINE);
                                } else {
                                    switch (from) {
                                        case "customerlogin":
                                            ParseObject profile_data = list.get(0).getParseObject("profile_data");
                                            int user_type = profile_data.getInt("user_type");
                                            switch (user_type) {
                                                case 1:
                                                    SendEmail(list.get(0).get("password").toString());
                                                    break;
                                                case 2:
                                                    Utils.ShowSnackBar(getActivity(), getString(R.string.fb_register), SnackbarType.SINGLE_LINE);
                                                    break;
                                            }
                                            break;
                                        case "shoplogin":
                                            SendEmail(list.get(0).get("password").toString());
                                            break;
                                    }

                                }
                            } else {
                                Utils.ShowSnackBar(getActivity(), e.getMessage(), SnackbarType.SINGLE_LINE);
                            }
                        }
                    });
                } else {
                    Utils.ShowSnackBar(getActivity(), getString(R.string.network_error), SnackbarType.SINGLE_LINE);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SendEmail(String password) {
        RotateProgressDialog.ShowDialog(getActivity());
        Map<String, String> params = new HashMap<>();
        params.put("text", getString(R.string.password_is) + password);
        params.put("subject", getString(R.string.password_reset));
        params.put("from", "SalesByMap@mapmap.com");
        params.put("to", et_email.getText().toString().trim());
        ParseCloud.callFunctionInBackground("mailSend", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object o, ParseException e) {
                RotateProgressDialog.DismissDialog();
                if (e == null) {
                    Utils.ShowSnackBar(getActivity(), getString(R.string.check_email), SnackbarType.MULTI_LINE);
                } else {
                    Utils.ShowSnackBar(getActivity(), e.getMessage(), SnackbarType.SINGLE_LINE);
                }
            }
        });
    }
}
