package oronos.oronosmobileapp.tabsManager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import oronos.oronosmobileapp.R;
import oronos.oronosmobileapp.treeBuilder.LayoutNode;
/**
 * BaseTabFragment.java
 * Classe qui contient un élément TAB
 */
public class BaseTabFragment extends LayoutNode {

    String title = "No Title";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_base, container, false);
        addChildrenFrag(R.id.fragment_content);
        return view;
    }

    @Override
    protected void addChildrenFrag(int container_id) {
        for (LayoutNode fragment : children) {
            getChildFragmentManager().beginTransaction().replace(container_id, fragment).commit();
        }
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
