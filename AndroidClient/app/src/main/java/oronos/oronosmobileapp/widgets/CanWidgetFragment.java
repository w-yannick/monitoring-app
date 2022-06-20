package oronos.oronosmobileapp.widgets;

import oronos.oronosmobileapp.treeBuilder.LayoutNode;
import oronos.oronosmobileapp.widgets.canWidgetFactory.CanWidgetParams;

/**
 * CanWidgetFragment.java
 * Fragment qui permet actuellement d'être consistant avec l'architecture mais sert simplement
 * à stocker les paramètres d'un tag Can
 */
public class CanWidgetFragment extends LayoutNode {

    CanWidgetParams canWidgetParams;

    public void setCanWidgetParams(CanWidgetParams canWidgetParams) {
        this.canWidgetParams = canWidgetParams;
    }

    @Override
    protected void addChildrenFrag(int container_id) {

    }

}
