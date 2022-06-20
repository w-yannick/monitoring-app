package oronos.oronosmobileapp.widgets;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import oronos.oronosmobileapp.R;
import oronos.oronosmobileapp.treeBuilder.LayoutNode;
/**
 * HorizontalDualWidgetFragment.java
 * Fragment qui permet l'affichage et la gestion des HorizontalDualWidget.
 */
public class HorizontalDualWidgetFragment extends LayoutNode {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dual_h, container, false);
        addChildrenFrag(0);
        if(children.size()>1){
            view.findViewById(R.id.linear_h_down).setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    protected void addChildrenFrag(int container_id) {
        if(children.size() > 0){
            getChildFragmentManager().beginTransaction().replace(R.id.linear_h_up, children.get(0)).commit();
        }
        if(children.size() > 1){
            getChildFragmentManager().beginTransaction().replace(R.id.linear_h_down, children.get(1)).commit();
        }
    }

}
