package oronos.oronosmobileapp.tabsManager;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
/**
 * TabsLayoutPresenter.java
 */
public class TabsLayoutPresenter {

    private TabsPagerAdapter mPagerAdapter;

    public TabsLayoutPresenter(FragmentManager fragmentManager) {
        mPagerAdapter =  new TabsPagerAdapter(fragmentManager);
    }

    public PagerAdapter getPagerAdapter() {
        return mPagerAdapter;
    }

    public void addFragment(BaseTabFragment fragment) {
        mPagerAdapter.addFragment(fragment);
    }
}
