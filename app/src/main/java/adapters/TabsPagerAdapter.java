package adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import fragments.CurrentDiscount;
import fragments.FutureDiscount;
import fragments.PastDiscount;

public class TabsPagerAdapter extends FragmentStatePagerAdapter {

    List<Fragment> list;
    public TabsPagerAdapter(FragmentManager fm,List<Fragment> list) {
        super(fm);
        this.list=list;
    }

    @Override
    public Fragment getItem(int index) {

        return list.get(index);
    }

    @Override
    public int getCount() {
        return list.size();
    }

}
