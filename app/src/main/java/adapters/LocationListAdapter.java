package adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
import com.mapmap.R;
import com.nispok.snackbar.enums.SnackbarType;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import commonUtils.Utils;
import fragments.ShopProfile;
import views.Effectstype;
import views.Progressdialog;

/**
 * Created by osx on 27/10/15.
 */
public class LocationListAdapter extends BaseAdapter {

    List<ParseObject> list;
    Context context;
    LayoutInflater inflater;
    TextView tv_location, tv_working_hours, tv_phone;
    ImageView iv_edit;
    Typeface typeface;
    TextView tv_weekdays_timetable,tv_saturday_timetable, tv_sunday_timetable;
    TextView tv_weekdays,tv_saturday, tv_sunday,tv_comment;
    ShopProfile shopProfile;
    public LocationListAdapter(ShopProfile shopProfile,Context context, List<ParseObject> list) {
        this.context = context;
        this.list = list;
        this.shopProfile=shopProfile;
        inflater = LayoutInflater.from(context);
        typeface = Utils.SetFont(context, "ROBOTO-REGULAR.TTF");
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
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.location_row_item, viewGroup, false);
        }
        tv_comment=(TextView)view.findViewById(R.id.tv_comment);
        tv_comment.setTypeface(typeface);
        tv_location = (TextView) view.findViewById(R.id.tv_location);
        tv_location.setTypeface(typeface);
        tv_working_hours = (TextView) view.findViewById(R.id.tv_working_hours);
        tv_working_hours.setTypeface(typeface);
        tv_phone = (TextView) view.findViewById(R.id.tv_phone);
        tv_phone.setTypeface(typeface);
        iv_edit = (ImageView) view.findViewById(R.id.iv_edit);
        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  Edit(context,list.get(position),position);

            }
        });
        tv_weekdays=(TextView)view.findViewById(R.id.tv_weekdays);
        tv_saturday=(TextView)view.findViewById(R.id.tv_saturday);
        tv_sunday=(TextView)view.findViewById(R.id.tv_sunday);
        tv_weekdays_timetable=(TextView)view.findViewById(R.id.tv_weekdays_timetable);
        tv_saturday_timetable=(TextView)view.findViewById(R.id.tv_saturday_timetable);
        tv_sunday_timetable=(TextView)view.findViewById(R.id.tv_sunday_timetable);
        tv_weekdays.setTypeface(typeface);
        tv_saturday.setTypeface(typeface);
        tv_sunday.setTypeface(typeface);
        tv_weekdays_timetable.setTypeface(typeface);
        tv_saturday_timetable.setTypeface(typeface);
        tv_sunday_timetable.setTypeface(typeface);
        ParseObject object=list.get(position);
        if(object.get("comment")!=null){

        }
        tv_comment.setText(object.get("comment").toString());
        tv_weekdays_timetable.setText(object.get("weekdays").toString());
        tv_saturday_timetable.setText(object.get("sat").toString());
        tv_sunday_timetable.setText(object.get("sun").toString());
        tv_location.setText(object.get("loc").toString());
        tv_phone.setText(object.get("phone").toString());
        return view;
    }
    public void Edit(final Context context,final ParseObject object,final int pos) {
        BottomSheet.Builder sheet = new BottomSheet.Builder(context, R.style.BottomSheet_StyleDialog);
        sheet.title("Choose type");
        sheet.sheet(R.menu.edit_location_menu);
        sheet.listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case R.id.edit:
                        shopProfile.AddLocations(object,pos);
                        break;
                    case R.id.delete:
                        Progressdialog.ShowDialog(context);
                        object.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                Progressdialog.DismissDialog();
                                if (e == null) {
                                    shopProfile.RemoveMarker((Double)object.get("latitude"),(Double)object.get("longitude"));
                                    list.remove(object);
                                    notifyDataSetChanged();
                                    shopProfile.ShowHide();
                                    Utils.ShowSnackBar(context,context.getString(R.string.loc_del), SnackbarType.SINGLE_LINE);
                                }
                                else{
                                    Utils.ShowCustomDialog(context, Effectstype.SlideBottom,e.getMessage());
                                }
                            }
                        });
                        break;
                }
            }
        });
        sheet.show();
    }

}
