package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mapmap.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import commonUtils.Utils;
import datamodel.ChildModel;
import datamodel.HeaderModel;
import fragments.CategoriesPop;
/**
 * Created by Umesh on 12/14/2015.
 */
public class CategoryItem extends AppCompatActivity implements SearchView.OnQueryTextListener{

    List<HeaderModel> headerlist = new ArrayList<>();
    Map<HeaderModel, List<ChildModel>> maplist = new HashMap<>();
    CategoriesAdapter2 adapter;
    public static int post = -1;
    Typeface typeface;

    @Bind(R.id.elv_categories)
    ListView elv_categories;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.tv_screenname)
    TextView tv_screenname;

    @Bind(R.id.iv_back)
    ImageView iv_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cat_pop_up);
        typeface = Utils.SetFont(this, "ROBOTO-ITALIC.TTF");
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        tv_screenname.setText(CategoriesPop.title);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Fetch_Categories(elv_categories);

        elv_categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               // onBackPressed();
                Intent intent = new Intent();
                String sub_category=(String)adapterView.getItemAtPosition(i);
                intent.putExtra("subcategory",sub_category);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if(newText.trim().length()>=0){
            System.out.println("text is"+newText);
            adapter.filter(newText);
        }
        return false;
    }

    public class CategoriesAdapter2 extends BaseAdapter {

        Context context;
        List<HeaderModel> headerlist;
        HashMap<String, String> map;
        LayoutInflater inflater;
        Map<HeaderModel, List<ChildModel>> childlist;
        ArrayList<String> list = new ArrayList<String>();
        private ArrayList<String> newlist;
        ImageView img;
        TextView tv_category_name;
        Typeface typeface;

        public CategoriesAdapter2(Context context, ArrayList<String> list) {
            this.context = context;
            this.list = list;
            inflater = LayoutInflater.from(context);
            typeface = Utils.SetFont(context, "ROBOTO-ITALIC.TTF");
            newlist=new ArrayList<>();
            newlist.addAll(list);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View convertView, ViewGroup viewGroup) {
            if(convertView==null){
                convertView = inflater.inflate(R.layout.cat_item_view,viewGroup,false);
            }

            tv_category_name=(TextView)convertView.findViewById(R.id.tv_category_name);
            tv_category_name.setText(list.get(i));
            tv_category_name.setTypeface(typeface);
            return convertView;
        }

        public void filter(String charText) {
            list.clear();
            if (charText.length() == 0) {
                list.addAll(newlist);
            }
            else
            {
                System.out.println("inside else"+newlist.size());
                for (String mytext : newlist)
                {
                    System.out.println("texts"+mytext);
                    if (mytext.contains(charText))
                    {
                        System.out.println("contains");
                        list.add(mytext);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }
    public void Fetch_Categories(final ListView expandableListView) {
        Collections.sort(CategoriesPop.mainlist);
        adapter = new CategoriesAdapter2(CategoryItem.this, CategoriesPop.mainlist);
        expandableListView.setAdapter(adapter);
        expandableListView.setTextFilterEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return true;
    }

    @OnClick(R.id.iv_back)
    public void back(){
        onBackPressed();
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem searchViewMenuItem = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) searchViewMenuItem.getActionView();
        mSearchView.setOnQueryTextListener(this);
        int searchImgId =android.support.v7.appcompat.R.id.search_button;
        ImageView v = (ImageView) mSearchView.findViewById(searchImgId);
        TextView searchText = (TextView) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchText.setTextColor(ContextCompat.getColor(CategoryItem.this, R.color.White));
        searchText.setHintTextColor(ContextCompat.getColor(CategoryItem.this,R.color.White));
        searchText.setHint("");
        v.setImageResource(R.drawable.ic_search);
        return super.onPrepareOptionsMenu(menu);
    }
}
