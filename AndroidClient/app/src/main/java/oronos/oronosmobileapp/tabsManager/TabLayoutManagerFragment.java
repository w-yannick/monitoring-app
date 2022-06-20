package oronos.oronosmobileapp.tabsManager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import oronos.oronosmobileapp.R;
import oronos.oronosmobileapp.treeBuilder.LayoutNode;
/**
 * TabLayoutManager.java
 * Gestionnaire d'une vue pour la tabulation
 */
public class TabLayoutManagerFragment extends LayoutNode
        implements ViewPager.OnPageChangeListener,
        TabLayout.OnTabSelectedListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabsLayoutPresenter tabsLayoutPresenter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        tabsLayoutPresenter = new TabsLayoutPresenter(getChildFragmentManager());

        View view = LayoutInflater.from(getContext()).inflate(R.layout.activity_tabs, container, false);

        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(this);

        viewPager = (ViewPager) view.findViewById(R.id.tabs_viewpager);
        viewPager.setAdapter(tabsLayoutPresenter.getPagerAdapter());
        viewPager.addOnPageChangeListener(this);

        addChildrenFrag(0); // dummy variable

        return view;
    }

    @Override
    protected void addChildrenFrag(int container_id) {
        for (LayoutNode tab : children) {
            if (!tab.mDiscardNode) {
                BaseTabFragment fragment = (BaseTabFragment) tab;
                tabLayout.addTab(tabLayout.newTab().setText(fragment.getTitle()));
                tabsLayoutPresenter.addFragment(fragment);
            }
        }
    }

    @Override
    public void onDestroyView() {
        viewPager.removeAllViews();
        viewPager.removeOnPageChangeListener(this);
        tabLayout.removeOnTabSelectedListener(this);
        tabLayout.removeAllTabs();
        super.onDestroyView();
    }

    // ViewPager.OnPageChangeListener
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        tabLayout.setScrollPosition(position, 0, true);
        tabLayout.setSelected(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    // TabLayout.OnTabSelectedListener
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

}
