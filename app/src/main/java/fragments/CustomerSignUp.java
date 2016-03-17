package fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.mapmap.R;
import com.nispok.snackbar.enums.SnackbarType;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import commonUtils.Utils;
import parsework.CommonUtils;
import views.Progressdialog;

/**
 * Created by osx on 14/10/15.
 */
public class CustomerSignUp extends Fragment {
    View view = null;

    @Bind(R.id.et_username)
    EditText et_username;

    @Bind(R.id.et_email)
    EditText et_email;

    @Bind(R.id.et_password)
    EditText et_password;

    @Bind(R.id.btn_signup)
    Button btn_signup;

    @Bind(R.id.txt_signinwith)
    TextView txt_signinwith;

    @Bind(R.id.iv_fb)
    ImageView iv_fb;

    @Bind(R.id.scrollView)
    ScrollView scrollView;

    public static CallbackManager callbackManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.fragment_customer_signup, container, false);
            ButterKnife.bind(this, view);
            et_username.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            et_email.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            et_password.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            et_email.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            btn_signup.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            txt_signinwith.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    fetchUserData(loginResult.getAccessToken());

                }

                @Override
                public void onCancel() {
                    Utils.ShowSnackBar(getActivity(),getString(R.string.req_can), SnackbarType.SINGLE_LINE);
                }

                @Override
                public void onError(FacebookException e) {
                    Utils.ShowSnackBar(getActivity(), e.getMessage(), SnackbarType.SINGLE_LINE);
                }
            });

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

    @OnClick(R.id.btn_signup)
    public void SignUp() {
        Utils.HideKeyboard(getActivity());
        if (TextUtils.isEmpty(et_username.getText().toString().trim())) {
            Utils.ShowSnackBar(getActivity(), getString(R.string.enter_username), SnackbarType.SINGLE_LINE);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(et_email.getText().toString().trim()).matches()) {
            Utils.ShowSnackBar(getActivity(), getString(R.string.error_email), SnackbarType.SINGLE_LINE);
        } else if (et_password.getText().toString().length() < 6) {
            Utils.ShowSnackBar(getActivity(), getString(R.string.error_password), SnackbarType.SINGLE_LINE);
        } else {
            if (Utils.checkInternetConnection(getActivity())) {
                Map<String, Object> values = new HashMap<>();
                values.put("username", et_username.getText().toString().trim());
                values.put("email", et_email.getText().toString().trim());
                values.put("password", et_password.getText().toString().trim());
                values.put("user_type", 1);
                values.put("type", "customer");
                CommonUtils.SignUp(getActivity(), values);
            } else {
                Utils.ShowSnackBar(getActivity(), getString(R.string.network_error), SnackbarType.SINGLE_LINE);
            }

        }
    }

    @OnClick(R.id.iv_fb)
    public void FacebookLogin() {
        try{
            if(Utils.checkInternetConnection(getActivity())){
                AccessToken token=AccessToken.getCurrentAccessToken();
                if(token!=null && !token.getToken().equalsIgnoreCase("")){
                    fetchUserData(AccessToken.getCurrentAccessToken());
                }
                else{
                    LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile", "email"));
                }
            }
            else{
                Utils.ShowSnackBar(getActivity(),getString(R.string.network_error),SnackbarType.SINGLE_LINE);
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void fetchUserData(AccessToken token){
        if(Utils.checkInternetConnection(getActivity())){
            Progressdialog.ShowDialog(getActivity());

            GraphRequest request = GraphRequest.newMeRequest(
                    token,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            Progressdialog.DismissDialog();
                            if(object!=null){
                                System.out.println("response"+response);
                                if(object.optBoolean("verified")){
                                    String email=object.optString("email");
                                    String first_name=object.optString("first_name");
                                    String last_name=object.optString("last_name");
                                    String id=object.optString("id");
                                    Map<String,Object> values=new HashMap<String, Object>();
                                    values.put("email",email);
                                    values.put("first_name",first_name);
                                    values.put("last_name",last_name);
                                    values.put("password",id);
                                    values.put("user_type", 2);
                                    values.put("type","customer");
                                    CommonUtils.SignUp(getActivity(),values);
                                }
                                else{
                                    Utils.ShowSnackBar(getActivity(),getString(R.string.verify_email),SnackbarType.SINGLE_LINE);
                                }
                            }


                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "email,first_name,last_name,id,verified");
            request.setParameters(parameters);
            request.executeAsync();
        }
        else{
            Utils.ShowSnackBar(getActivity(), "No internet connection", SnackbarType.SINGLE_LINE);
        }

    }


}
