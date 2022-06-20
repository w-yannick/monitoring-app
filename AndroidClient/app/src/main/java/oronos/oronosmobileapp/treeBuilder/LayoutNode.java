package oronos.oronosmobileapp.treeBuilder;

import android.content.Context;
import android.support.v4.app.Fragment;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

import oronos.oronosmobileapp.widgets.canWidgetFactory.CanWidgetParams;
import oronos.oronosmobileapp.widgets.CanWidgetFragment;
import oronos.oronosmobileapp.widgets.DataDisplayerWidgetFragment;
import oronos.oronosmobileapp.widgets.DisplayLogWidgetFragment;
import oronos.oronosmobileapp.widgets.FindMeWidgetFragment;
import oronos.oronosmobileapp.widgets.HorizontalDualWidgetFragment;
import oronos.oronosmobileapp.widgets.MapWidgetFragment;
import oronos.oronosmobileapp.widgets.ModuleStatusWidgetFragment;
import oronos.oronosmobileapp.widgets.PlotWidgetFragment;
import oronos.oronosmobileapp.widgets.VerticalDualWidgetFragment;
import oronos.oronosmobileapp.tabsManager.BaseTabFragment;
import oronos.oronosmobileapp.tabsManager.TabLayoutManagerFragment;
/**
 * LayoutNode.java
 * Noeud de l'arbre des éléments de vues: classe abstraite
 */
public abstract class LayoutNode extends Fragment {

    protected List<LayoutNode> children;
    private boolean mIsLeaf;
    private Node mRefXmlElem;
    public String mNodeName;
    public static Context mContext;
    public boolean mDiscardNode = false;

    public LayoutNode() {
        super();
        children = new ArrayList<>();
    }

    /**
     * Enregistre la référence XML de ce noeud de l'arbre
     * Et créé les enfants de ce noeuds récursivements
     *
     * @param refXmlElem Noeud Xml qui sera parsé pour la suite de la traversée
     */
    public void setNodeXmlRef(Node refXmlElem) {
        mNodeName = refXmlElem.getNodeName();
        mRefXmlElem = refXmlElem;
        if (mRefXmlElem.hasChildNodes()) {
            buildSubtree();
        } else {
            mIsLeaf = true;
        }
        mDiscardNode = (children.size() == 0 && !mIsLeaf);

    }
    /**
     * Méthode abstraite qui permet d'ajouter des fragments au conteneur spécifié au moment de la création de la vue
     *
     * @param container_id id du conteneur qui contiendra les fragments enfants
     */
    protected abstract void addChildrenFrag(int container_id);

    /**
     * Créé la suite de l'arbre à partir de la référence Xml pour ce noeud.
     */
    private void buildSubtree() {
        NodeList childNodes = mRefXmlElem.getChildNodes();
        Node nextNode;
        int i = 0;
        for (; i < childNodes.getLength(); ++i) {
            nextNode = childNodes.item(i);
            if (nextNode.getNodeType() == nextNode.ELEMENT_NODE) {
                String value = nextNode.getNodeName();
                LayoutNode newNode;
                switch (value) {
                    case "TabContainer":
                        newNode = new TabLayoutManagerFragment();
                        break;
                    case "Tab":
                        newNode = new BaseTabFragment();
                        Element nodeElem = (Element) nextNode;
                        ((BaseTabFragment) newNode).setTitle(nodeElem.getAttribute("name"));
                        break;
                    case "DualVWidget":
                        newNode = new HorizontalDualWidgetFragment();
                        break;
                    case "DualHWidget":
                        newNode = new VerticalDualWidgetFragment();
                        break;
                    case "Modulestatus":
                        newNode = new ModuleStatusWidgetFragment();
                        break;
                    case "DisplayLogWidget":
                        newNode = new DisplayLogWidgetFragment();
                        break;
                    case "Plot":
                        newNode = new PlotWidgetFragment();
                        break;
                    case "Map":
                        newNode = new MapWidgetFragment();
                        break;
                    case "FindMe":
                        newNode = new FindMeWidgetFragment();
                        break;
                    case "DataDisplayer":
                        newNode = new DataDisplayerWidgetFragment();
                        break;
                    case "CAN":
                        CanWidgetFragment canWidgetFragment = new CanWidgetFragment();
                        canWidgetFragment.setCanWidgetParams(new CanWidgetParams(nextNode.getAttributes()));
                        newNode = canWidgetFragment;
                        break;
                    default:
                        continue;

                }
                newNode.setNodeXmlRef(nextNode);
                children.add(newNode);
            }
        }
    }

    public Node getNode() {
        return mRefXmlElem;
    }

    public List<LayoutNode> getChildren() {
        return children;
    }

}
