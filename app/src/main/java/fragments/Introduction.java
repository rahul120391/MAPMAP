package fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mapmap.R;
import com.nispok.snackbar.enums.SnackbarType;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import activities.MainActivity;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import commonUtils.Utils;
import parsework.CommonUtils;
import views.Effectstype;
import views.Progressdialog;

/**
 * Created by osx on 14/10/15.
 */
public class Introduction extends Fragment {
    View view = null;

    @Bind(R.id.btn_signup)
    Button btn_signup;

    @Bind(R.id.btn_login)
    Button btn_login;

    @Bind(R.id.tv_guest)
    TextView tv_guest;
    int x=0;

    int category_icons[] = {R.drawable.ic_electronics, R.drawable.ic_office,R.drawable.ic_motor,R.drawable.ic_jewellery,R.drawable.ic_kids,R.drawable.ic_wedding,R.drawable.ic_event,R.drawable.ic_food,R.drawable.ic_fashion,R.drawable.ic_home_garden,R.drawable.ic_wine,R.drawable.ic_home,R.drawable.ic_pets,R.drawable.ic_sports,R.drawable.ic_beauty};
    int category_icons_selected[] = {R.drawable.electronics, R.drawable. office,R.drawable.motors,R.drawable.jewelry,R.drawable. kids,R.drawable. wedding,R.drawable. event,R.drawable. food,R.drawable. fashion,R.drawable.home,R.drawable. wine,R.drawable. home,R.drawable. pets,R.drawable. sports,R.drawable.  beauty};
    String category_name[] = {"Electronics", "Office","Motors","Jewellary & watch","Kids & Baby", "Wedding & Party", "Events", "Food & Drinks","Fashion","Garden","Wines & Spirits","Home","Pets","Sports","Beauty"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.fragment_introduction, container, false);
            ButterKnife.bind(this, view);

            btn_login.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            btn_signup.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-REGULAR.TTF"));
            tv_guest.setTypeface(Utils.SetFont(getActivity(), "ROBOTO-ITALIC.TTF"));
            //StoreCategories();
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
        try {
            ((MainActivity) getActivity()).FragmentTransactions(new Choice(),"choice");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_login)
    public void Login() {
        try {
            Choice ch = new Choice();
            Bundle bundle = new Bundle();
            bundle.putString("from", "login");
            ch.setArguments(bundle);
            ((MainActivity) getActivity()).FragmentTransactions(new Login(),"login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.tv_guest)
    public void Guest() {
        try {
            ((MainActivity) getActivity()).FragmentTransactions(new LocationScreen(),"location");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void StoreCategories() {
        System.out.print("Inside Store");
        int start=0;
        int end=3;
        for(x=0;x<15;x++){
            ParseObject object = new ParseObject(CommonUtils.CATEGORIES);
            object.put("name",category_name[x]);
            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    category_icons[x]);

            Bitmap icon2 = BitmapFactory.decodeResource(getResources(),
                    category_icons_selected[x]);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            icon.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            ParseFile file = new ParseFile("picture.png", byteArray);
            object.put("category_image", file);

            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
            icon2.compress(Bitmap.CompressFormat.PNG, 100, stream2);
            byte[] byteArray2 = stream2.toByteArray();

            ParseFile file2 = new ParseFile("picture2.png", byteArray2);
            object.put("category_image2", file2);

            ParseObject parse = new ParseObject(CommonUtils.SUBCATEGORIES);
            ArrayList<String> subcategories_name=new ArrayList<>();
            switch (x) {
                case  0:
                    end=6;
                    subcategories_name.add("Phone");
                    subcategories_name.add("Home Sysytem");
                    subcategories_name.add("Computer");
                    subcategories_name.add("Appliances");
                    subcategories_name.add("Kids baby");
                    subcategories_name.add("Event");
                    break;
                case  1:
                    end=3;
                    subcategories_name.add("Furniture");
                    subcategories_name.add("Appliances");
                    subcategories_name.add("Supplies");
                    break;
                case  2:
                    end=5;
                    subcategories_name.add("Car Spa");
                    subcategories_name.add("Accesssories");
                    subcategories_name.add("Electronics");
                    subcategories_name.add("Car Shop");
                    subcategories_name.add("Event");
                    break;
                case  3:
                    end=5;
                    subcategories_name.add("Watch");
                    subcategories_name.add("Fine Jewelry");
                    subcategories_name.add("Watch");
                    subcategories_name.add("Accessories");
                    subcategories_name.add("Event");
                    break;
                case  4:
                    end=9;
                    subcategories_name.add("Hygiene");
                    subcategories_name.add("Accessories");
                    subcategories_name.add("Baby Trolley");
                    subcategories_name.add("Food");
                    subcategories_name.add("Electronics");
                    subcategories_name.add("Clothing");
                    subcategories_name.add("Entertainment");
                    subcategories_name.add("Furniture");
                    subcategories_name.add("Event");
                    break;
                case  5:
                    end=11;
                    subcategories_name.add("Bakery");
                    subcategories_name.add("Spa");
                    subcategories_name.add("Hair Salon");
                    subcategories_name.add("Nail Salon");
                    subcategories_name.add("Beauty Clinique");
                    subcategories_name.add("Makeup");
                    subcategories_name.add("Jewelry");
                    subcategories_name.add("Wines & Spirits");
                    subcategories_name.add("Clothing");
                    subcategories_name.add("Shoes");
                    subcategories_name.add("Event");
                    break;
                case  6:
                    end=13;
                    subcategories_name.add("DS & Baby");
                    subcategories_name.add("Food & Drinks");
                    subcategories_name.add("Beauty");
                    subcategories_name.add("Pets");
                    subcategories_name.add("Motors");
                    subcategories_name.add("Wine E Spirits");
                    subcategories_name.add("Electronics");
                    subcategories_name.add("Wedding Party");
                    subcategories_name.add("Jewelry & Watch");
                    subcategories_name.add("Fashion");
                    subcategories_name.add("Home");
                    subcategories_name.add("Garden");
                    subcategories_name.add("Sport");
                    break;
                case  7:
                    end=10;
                    subcategories_name.add("Meat");
                    subcategories_name.add("Coffee Shop");
                    subcategories_name.add("Bakery");
                    subcategories_name.add("Vegetarian");
                    subcategories_name.add("Vegan");
                    subcategories_name.add("Seafood");
                    subcategories_name.add("Event");
                    subcategories_name.add("Baby");
                    subcategories_name.add("Bars");
                    subcategories_name.add("Munches");
                    break;
                case  8:
                    end=6;
                    subcategories_name.add("Shoes");
                    subcategories_name.add("Accessories");
                    subcategories_name.add("Underwear");
                    subcategories_name.add("Event");
                    subcategories_name.add("Bags");
                    subcategories_name.add("Clothes");
                    break;
                case  9:
                    end=7;
                    subcategories_name.add("Furniture");
                    subcategories_name.add("Lighting");
                    subcategories_name.add("Storage");
                    subcategories_name.add("Artificial");
                    subcategories_name.add("Decorative");
                    subcategories_name.add("Garden suppliers");
                    subcategories_name.add("Event");
                    break;
                case  10:
                    end=4;
                    subcategories_name.add("Bar");
                    subcategories_name.add("Club");
                    subcategories_name.add("Restorent");
                    subcategories_name.add("Event");
                    break;
                case  11:
                    end=9;
                    subcategories_name.add("Living Room");
                    subcategories_name.add("Bedroom");
                    subcategories_name.add("Bathroom");
                    subcategories_name.add("Kitchen");
                    subcategories_name.add("Kids & Baby");
                    subcategories_name.add("Storage");
                    subcategories_name.add("Textil & Rugs");
                    subcategories_name.add("Lighting");
                    subcategories_name.add("Event");
                    break;
                case  12:
                    end=4;
                    subcategories_name.add("Food");
                    subcategories_name.add("Accessories");
                    subcategories_name.add("Pets Salon");
                    subcategories_name.add("Entertainment");
                    break;
                case  13:
                    end=5;
                    subcategories_name.add("Sport gear");
                    subcategories_name.add("GYM");
                    subcategories_name.add("Sport Clothing");
                    subcategories_name.add("Sport Shoes");
                    subcategories_name.add("Event");
                    break;
                case  14:
                    end=7;
                    subcategories_name.add("Hair");
                    subcategories_name.add("Cliniques");
                    subcategories_name.add("Massage & Spa");
                    subcategories_name.add("Cosmetic & Makeup");
                    subcategories_name.add("Nail Salon");
                    subcategories_name.add("Man Spa");
                    subcategories_name.add("Event");
                    break;
            }
            /*for (int i = start; i < end; i++) {
                subcategories_name.add("subcategories"+i);
            }*/
            start=end;
            end=end+3;
            parse.put("sub_categories",subcategories_name);
            object.put("sub_category", parse);
            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    System.out.println("values of x"+x);
                    if (e == null) {
                        Utils.ShowSnackBar(getActivity(), "saved successfully", SnackbarType.SINGLE_LINE);
                    } else if(e!=null){
                        Utils.ShowCustomDialog(getActivity(), Effectstype.SlideBottom, e.getMessage());
                    }

                }
            });
        }


    }
}
