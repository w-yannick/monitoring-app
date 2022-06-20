package oronos.oronosmobileapp.widgets;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;

import oronos.oronosmobileapp.R;
import oronos.oronosmobileapp.treeBuilder.LayoutNode;
import oronos.oronosmobileapp.widgets.canWidgetFactory.CanDataViewFactory;
import oronos.oronosmobileapp.widgets.canWidgetFactory.CanWidgetView;

/**
 * DataDisplayerWidgetFragment.java
 * Fragment qui permet d'afficher un ensemble de can (tag dataDisplayer)
 */
public class DataDisplayerWidgetFragment extends LayoutNode {

    // Nb de colonne pour afficher les can views
    private final int COLUMN_COUNT = 3;
    private GridLayout gridLayout = null;
    private List<CanWidgetView> canWidgetViews = new ArrayList();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_data_displayer, container, false);
        gridLayout = (GridLayout) v.findViewById(R.id.cans_grid);
        gridLayout.setColumnCount(COLUMN_COUNT);
        if(canWidgetViews.isEmpty())
            addChildrenFrag(0);
        addCanView(canWidgetViews);
        return v;
    }

    private void addCanView(List<CanWidgetView> canWidgetViews) {
        for(CanWidgetView canView : canWidgetViews)
            gridLayout.addView(canView);
    }

    @Override
    protected void addChildrenFrag(int container_id) {
        CanDataViewFactory canDataViewFactory = new CanDataViewFactory();
        for(LayoutNode layoutNode : children) {
            CanWidgetFragment canWidgetFragment = (CanWidgetFragment) layoutNode;
            CanWidgetView canWidgetView = canDataViewFactory.createCanView(getContext(), canWidgetFragment.canWidgetParams);
            if (canWidgetView != null)
                canWidgetViews.add(canWidgetView);
        }
    }

    @Override
    public void onDestroy() {
        gridLayout.removeAllViews();
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        gridLayout.removeAllViews();
        super.onDestroyView();
    }

}
