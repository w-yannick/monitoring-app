package oronos.oronosmobileapp;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.view.Menu;

import oronos.oronosmobileapp.settings.Configuration;
import oronos.oronosmobileapp.treeBuilder.LayoutTree;
import oronos.oronosmobileapp.tabsManager.TabLayoutManagerFragment;
import oronos.oronosmobileapp.utilities.BottomNavigationViewHelper;


public class MainActivity extends BaseActivity {

    private LayoutTree mLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayout = new LayoutTree(this, Configuration.rocketsConfig.xmlContentLayout);
        mLayout.buildTree();
        super.setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);
        if (bottomNavigationView != null) {
            if (Configuration.darkTheme) {

                bottomNavigationView.setItemTextColor(ContextCompat.getColorStateList(context, R.color.drawer_dark));
                bottomNavigationView.setItemIconTintList(ContextCompat.getColorStateList(context, R.color.drawer_dark));
            } else {
                    bottomNavigationView.setItemTextColor(ContextCompat.getColorStateList(context, R.color.drawer));
                    bottomNavigationView.setItemIconTintList(ContextCompat.getColorStateList(context, R.color.drawer));
            }
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigationView);
        mLayout.createMenu(navigation.getMenu());
        navigation.setOnNavigationItemSelectedListener(mLayout.mManagedTabs);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        TabLayoutManagerFragment selectedOption = (TabLayoutManagerFragment) mLayout.mManagedTabs.getChildren().get(0);
        super.getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_content, selectedOption)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
