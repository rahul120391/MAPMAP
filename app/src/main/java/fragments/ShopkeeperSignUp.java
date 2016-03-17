package fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mapmap.R;
import com.nispok.snackbar.enums.SnackbarType;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapters.CategoryAdapter;
import adapters.CategoryItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import commonUtils.Utils;
import datamodel.HeaderModel;
import parsework.CommonUtils;
import views.Effectstype;
import views.RotateProgressDialog;

/**
 * Created by osx on 14/10/15.
 */
public class ShopkeeperSignUp extends Fragment {
    View view = null;

    @Bind(R.id.et_shop_name)
    EditText et_shop_name;

    @Bind(R.id.et_email)
    EditText et_email;

    @Bind(R.id.et_password)
    EditText et_password;

    @Bind(R.id.btn_signup)
    Button btn_signup;

    @Bind(R.id.scrollView)
    ScrollView scrollView;

    @Bind(R.id.et_category)
    EditText et_category;

    Dialog dialog;
    ArrayList<HeaderModel> categorieslist = new ArrayList<>();
    @Bind(R.id.layout_sel_category)
    RelativeLayout layout_sel_category;
    CategoryAdapter adapter;
    String name, url;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.fragment_shopkeeper_signup, container, false);
            ButterKnife.bind(this, view);
            et_shop_name.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            et_email.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            et_password.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            btn_signup.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
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
        if (TextUtils.isEmpty(et_shop_name.getText().toString().trim())) {
            Utils.ShowSnackBar(getActivity(), getResources().getString(R.string.error_shopname), SnackbarType.SINGLE_LINE);
        } else if (et_category.getText().toString().trim().length()<=0) {
            Utils.ShowSnackBar(getActivity(), getResources().getString(R.string.sel_category), SnackbarType.SINGLE_LINE);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(et_email.getText().toString().trim()).matches()) {
            Utils.ShowSnackBar(getActivity(), getResources().getString(R.string.error_email), SnackbarType.SINGLE_LINE);
        } else if (et_password.getText().toString().trim().length() < 6) {
            Utils.ShowSnackBar(getActivity(), getResources().getString(R.string.error_password), SnackbarType.SINGLE_LINE);
        } else {
            Map<String, Object> values = new HashMap<>();
            values.put("email", et_email.getText().toString().trim());
            values.put("shop_name", et_shop_name.getText().toString().trim());
            values.put("main_category", name);  //grid
            values.put("category_url", url);
            values.put("password", et_password.getText().toString().trim());
            values.put("type", "shopkeeper");
            values.put("isVerified", false);
            values.put("verification_number", Utils.GenrateRandomNumber());
            CommonUtils.SignUp(getActivity(), values);
        }
    }

    @OnClick(R.id.et_category)
    public void SelectCategory() {
        /*
        if (categorieslist.size() == 0) {
            getCategories();
        } else {
            ShowCategories();
        }
        */
        Intent jump = new Intent(getActivity(), CategoriesPop.class);
        startActivityForResult(jump, 1);
    }

    public void getCategories() {
        if (Utils.checkInternetConnection(getActivity())) {
            RotateProgressDialog.ShowDialog(getActivity());
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(CommonUtils.CATEGORIES);
            query.orderByAscending("name");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        if (list.size() > 0) {
                            categorieslist.clear();
                            for (int i = 0; i < list.size(); i++) {
                                ParseObject categories = list.get(i);
                                String name = categories.get("name").toString();
                                ParseFile photo = categories.getParseFile("category_image");
                                String url = photo.getUrl();
                                HeaderModel model = new HeaderModel();
                                model.setUrl(url);
                                model.setCategoryname(name);
                                categorieslist.add(model);
                            }
                            RotateProgressDialog.DismissDialog();
                            ShowCategories();

                        } else {
                            Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, getString(R.string.no_category));
                        }
                    } else {
                        RotateProgressDialog.DismissDialog();
                        Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, e.getMessage());
                    }

                }
            });
        } else {
            Utils.ShowSnackBar(getActivity(), getString(R.string.network_error), SnackbarType.SINGLE_LINE);
        }

    }

    public void ShowCategories() {
        dialog = new Dialog(getActivity(), R.style.DialogTheme);
        if (!dialog.isShowing()) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            final View dialoglayout = inflater.inflate(R.layout.dialog_categories_list, null);
            dialog.setContentView(dialoglayout);
            final ListView lv_categories = (ListView) dialoglayout.findViewById(R.id.lv_categories);
            TextView tv_screenname = (TextView) dialoglayout.findViewById(R.id.tv_screenname);
            tv_screenname.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            tv_screenname.setText(getString(R.string.categories));
            ImageView iv_back = (ImageView) dialoglayout.findViewById(R.id.iv_back);
            iv_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_SmileWindow;
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.show();
            adapter = new CategoryAdapter(getActivity(), categorieslist);
            lv_categories.setAdapter(adapter);
            lv_categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    dialog.dismiss();
                    HeaderModel model = categorieslist.get(position);
                    name = model.getCategoryname();
                    url = model.getUrl();
                    et_category.setText(name);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 1) {
                if (resultCode == Activity.RESULT_OK) {
                    name = data.getStringExtra("categoryname");
                    url = data.getStringExtra("url");
                    et_category.setText(name);
                }
            }
        }
    }

}
