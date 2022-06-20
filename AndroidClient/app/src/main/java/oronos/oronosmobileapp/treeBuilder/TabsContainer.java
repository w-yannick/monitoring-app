package oronos.oronosmobileapp.treeBuilder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import oronos.oronosmobileapp.MainActivity;
import oronos.oronosmobileapp.R;
import oronos.oronosmobileapp.tabsManager.TabLayoutManagerFragment;
/**
 * TabsContainer.java
 * Conteneur de tabs à haut niveau. Permet de gérer les évènements de transition entre les Grids
 */
public class TabsContainer extends LayoutNode implements BottomNavigationView.OnNavigationItemSelectedListener {

    public Context mContext;
    public TabsContainer(){super();}

    @Override
    protected void addChildrenFrag(int container_id) {
    }

    @SuppressLint("ValidFragment")
    public TabsContainer(Context context){
        mContext = context;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem  item) {
        if(item.getGroupId() == LayoutTree.MENU_GROUP_ID){
            TabLayoutManagerFragment selectedOption = (TabLayoutManagerFragment) children.get(item.getItemId());
            final MainActivity activity = (MainActivity) mContext;
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_content, selectedOption)
                    .commit();
            return true;
        }
        else{
            return false;
        }
    }

}
