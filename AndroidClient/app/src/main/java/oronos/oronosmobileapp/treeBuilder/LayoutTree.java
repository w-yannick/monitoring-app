package oronos.oronosmobileapp.treeBuilder;

import android.content.Context;
import android.view.Menu;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import oronos.oronosmobileapp.MainActivity;
import oronos.oronosmobileapp.utilities.FLog;
import oronos.oronosmobileapp.utilities.Tag;
/**
 * LayoutTree.java
 * Arbre complet pour contenir les éléments de nos vues de manière organisée et hiérarchique
 */
public class LayoutTree {

    public static int MENU_GROUP_ID = 500;

    public TabsContainer mManagedTabs;

    private Document mParsedXML;
    private Context mContext;

    public LayoutTree(Context context, String xmlContent){
        mContext = context;
        LayoutNode.mContext = mContext;
        mManagedTabs = new TabsContainer(context);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        InputSource is = new InputSource(new StringReader(xmlContent));
        try {
            mParsedXML = builder.parse(is);
        } catch (SAXException e) {
            FLog.e(Tag.LAYOUT_TREE, "Error parsing during xml : " + e);
        } catch (IOException e) {
            FLog.e(Tag.LAYOUT_TREE, "Error parsing during xml : " + e);
        }
    }

    /**
     * Méthode pour débuter la construction de l'arbre de manière exhaustive
     *
     */
    public void buildTree(){
        NodeList childNodes = mParsedXML.getChildNodes();
        Node nextNode;
        int i = 0;
        for(; i < childNodes.getLength(); ++i){
            nextNode = childNodes.item(i);
            String value = nextNode.getNodeName();
            if(nextNode.getNodeType() == nextNode.ELEMENT_NODE) {
                switch (value) {
                    case "Rocket":
                        MainActivity activity = (MainActivity) mContext;
                        Element nodeElem = (Element) nextNode;
                        activity.setTitle(nodeElem.getAttribute("name") + " - "+ nodeElem.getAttribute("id"));
                        i = -1;
                        childNodes = nextNode.getChildNodes();
                        break;
                    case "Grid":
                        mManagedTabs.setNodeXmlRef(nextNode);
                        break;
                    default:
                        i = -1;
                        childNodes = nextNode.getChildNodes();
                        continue;

                }
            }
        }

    }

    /**
     * Créé la vue pour les grid containers
     * @param menu menu qui contiendra les Grids
     */
    public void createMenu(Menu menu) {
        menu.clear();
        Integer i = 0;
        for(LayoutNode tabCont : mManagedTabs.children){
            if(tabCont.getChildren().size() > 0){
                menu.add(MENU_GROUP_ID, i, i , "Grid " + i.toString()).setIcon(android.support.design.R.drawable.abc_btn_radio_to_on_mtrl_000);
                i++;
            }
        }
    }

}
