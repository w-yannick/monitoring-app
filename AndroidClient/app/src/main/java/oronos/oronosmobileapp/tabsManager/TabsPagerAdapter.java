package oronos.oronosmobileapp.tabsManager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;
/**
 * TabsPagerAdapter.java
 */

public class TabsPagerAdapter extends FragmentPagerAdapter {

    private List<BaseTabFragment> fragments = new ArrayList<>();

    public TabsPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getTitle();
    }

    public void addFragment(BaseTabFragment fragment) {
        fragments.add(fragment);
        notifyDataSetChanged();
    }


}
