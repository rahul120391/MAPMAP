package fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import com.mapmap.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import adapters.CategoriesAdapter;
import adapters.CategoryItem;
import commonUtils.Utils;
import datamodel.ChildModel;
import datamodel.HeaderModel;
import parsework.CommonUtils;
import views.Effectstype;
import views.Progressdialog;
import views.RotateProgressDialog;

/**
 * Created by Umesh on 12/11/2015.
 */
public class CategoriesPop extends Activity {

    public static List<HeaderModel> headerlist = new ArrayList<>();
    public static ArrayList<String> list2 = new ArrayList<String>();
    public static ArrayList<ArrayList<String>> list3 = new ArrayList<>();
    public static ArrayList<String> mainlist = new ArrayList<String>();
    HashMap<String, String> map;
    public static String title;
    public static Map<HeaderModel, List<ChildModel>> maplist = new HashMap<>();
    CategoriesAdapter adapter;
    public static int post = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getAttributes().windowAnimations = R.style.Animations_SmileWindow;
        setContentView(R.layout.dialog_categories);
        final GridView elv_categories = (GridView) findViewById(R.id.elv_categories);
        elv_categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HeaderModel model=headerlist.get(i);
                CategoriesPop.post = i;
                CategoriesPop.mainlist.clear();
                title = model.getCategoryname();
                for(int j = 0; j < CategoriesPop.list3.get(CategoriesPop.post).size(); j++)
                {
                    System.out.println("Items : "+CategoriesPop.list3.get(CategoriesPop.post).get(j));
                    CategoriesPop.mainlist.add(CategoriesPop.list3.get(CategoriesPop.post).get(j));
                }
                String url=model.getUrl();
                Intent intent = new Intent();
                intent.putExtra("categoryname", title);
                intent.putExtra("url",url);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        /*elv_categories.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                    elv_categories.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });

        elv_categories.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                String st = headerlist.get(i).getState();
                if (st.equalsIgnoreCase("Selected")) {
                    headerlist.get(i).setState("Unselect");
                } else {
                    headerlist.get(i).setState("Selected");
                }
                adapter.notifyDataSetChanged();
                return false;
            }
        });*/
        Fetch_Categories(elv_categories);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void Fetch_Categories(final GridView expandableListView) {
        if (Utils.checkInternetConnection(CategoriesPop.this)) {
            RotateProgressDialog.ShowDialog(CategoriesPop.this);
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(CommonUtils.CATEGORIES);
            query.orderByAscending("name");
            query.include("sub_category");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    RotateProgressDialog.DismissDialog();
                    headerlist.clear();
                    maplist.clear();
                    list2.clear();
                    if (e == null) {
                        if (list.size() == 0) {
                            Utils.ShowCustomDialog(CategoriesPop.this, Effectstype.SlideBottom, getString(R.string.no_category));
                        } else if (list.size() > 0) {
                            headerlist.clear();
                            maplist.clear();
                            for (int i = 0; i < list.size(); i++) {
                                ParseObject categories = list.get(i);
                                ParseObject sub_categries = categories.getParseObject("sub_category");
                                JSONArray sub_categories = sub_categries.getJSONArray("sub_categories");
                                final HeaderModel model = new HeaderModel();
                                System.out.println("category_name" + categories.get("name").toString());
                                model.setCategoryname(categories.get("name").toString());
                                ParseFile photo = categories.getParseFile("category_image");
                                String url = photo.getUrl();
                                ParseFile photo2 = categories.getParseFile("category_image2");
                                String url2 = photo2.getUrl();
                                System.out.println("url" + url);
                                model.setUrl(url);
                                model.setUrl2(url2);
                                model.setState("Unselect");
                                headerlist.add(model);
                                List<ChildModel> childlist = new ArrayList<ChildModel>();
                                list2 = new ArrayList<String>();

                                for (int x = 0; x < sub_categories.length(); x++) {
                                    try {
                                        map = new HashMap<String, String>();
                                        System.out.println("sub_categories" + sub_categories.getString(x));
                                        ChildModel childmodel = new ChildModel();
                                        childmodel.setState(false);
                                        childmodel.setSub_category_name(sub_categories.getString(x));
                                        map.put("child", sub_categories.getString(x));
                                        list2.add(sub_categories.getString(x));
                                        childlist.add(childmodel);
                                    } catch (Exception ee) {
                                        ee.printStackTrace();
                                    }

                                }
                                maplist.put(model, childlist);
                                list3.add(i, list2);
                            }
                            adapter = new CategoriesAdapter(CategoriesPop.this, headerlist, maplist);
                            expandableListView.setAdapter(adapter);
                        }
                    } else {
                        Utils.ShowCustomDialog(CategoriesPop.this, Effectstype.SlideBottom, e.getMessage());
                    }
                }
            });
        } else {
            Utils.ShowCustomDialog(CategoriesPop.this, Effectstype.SlideBottom, getString(R.string.network_error));
        }


    }
}
