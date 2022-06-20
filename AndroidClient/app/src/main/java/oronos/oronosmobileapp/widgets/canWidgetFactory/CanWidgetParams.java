package oronos.oronosmobileapp.widgets.canWidgetFactory;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * CanWidgetParams.java
 * Class qui permet d'extraire (d'un noeud DOM) et conserver les paramètres possibles pour une vue can
 */
public class CanWidgetParams {

    public String name;
    public String id;
    public String display;
    public String customUpdate;
    public String customAcceptable;
    public String specificSource;
    public String minAcceptable;
    public String maxAcceptable;
    public String serialNb;
    public String chiffresSign;
    public String unitSymbol;
    public String customUpdateParam;
    public String updateEach;

    public CanWidgetParams(NamedNodeMap canAttributs) {
        for(int i = 0; i < canAttributs.getLength(); i++) {
            Node item = canAttributs.item(i);
            switch (item.getNodeName()) {
                case "name" :
                    name = item.getNodeValue();
                    break;
                case "id" :
                    id = item.getNodeValue();
                    break;
                case "display" :
                    String strItem = item.getNodeValue();
                    String[] splited = strItem.split("\\s+");
                    display = splited[0];
                    if(splited.length > 1)
                        unitSymbol = splited[1];
                    break;
                case "customUpdate" :
                    customUpdate = item.getNodeValue();
                    break;
                case "customAcceptable" :
                    customAcceptable = item.getNodeValue();
                    break;
                case "specificSource" :
                    specificSource = item.getNodeValue();
                    break;
                case "minAcceptable" :
                    minAcceptable = item.getNodeValue();
                    break;
                case "maxAcceptable" :
                    maxAcceptable = item.getNodeValue();
                    break;
                case "serialNb" :
                    serialNb = item.getNodeValue();
                    break;
                case "chiffresSign" :
                    chiffresSign = item.getNodeValue();
                    break;
                case "updateEach" :
                    updateEach = item.getNodeValue();
                    break;
                case "customUpdateParam" :
                    customUpdateParam = item.getNodeValue();
                    break;
            }
        }
    }

    @Override
    public String toString() {
        return "[ name : " + name + "\n" +
                " id : " + id + "\n" +
                " display : " + display + "\n" +
                " unitSymbol : " + unitSymbol + "\n" +
                " customUpdate : " + customUpdate + "\n" +
                " customAcceptable : " + customAcceptable + "\n" +
                " specificSource : " + specificSource + "\n" +
                " minAcceptable : " + minAcceptable + "\n" +
                " maxAcceptable : " + maxAcceptable + "\n" +
                " serialNb : " + serialNb + "\n" +
                " chiffresSign : " + chiffresSign + " ]";
    }

}
