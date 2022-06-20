package oronos.oronosmobileapp.widgets;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import oronos.oronosmobileapp.R;
import oronos.oronosmobileapp.treeBuilder.LayoutNode;

public class VerticalDualWidgetFragment extends LayoutNode {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dual_v, container, false);
        addChildrenFrag(0);
        if(children.size()>1){
            view.findViewById(R.id.linear_v_right).setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    protected void addChildrenFrag(int container_id) {
        if(children.size() > 0)
            getChildFragmentManager().beginTransaction().replace(R.id.linear_v_left, children.get(0)).commit();
        if(children.size() > 1)
            getChildFragmentManager().beginTransaction().replace(R.id.linear_v_right, children.get(1)).commit();
    }
}
