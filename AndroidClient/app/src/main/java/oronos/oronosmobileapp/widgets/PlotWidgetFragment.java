package oronos.oronosmobileapp.widgets;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import oronos.oronosmobileapp.MainApplication;
import oronos.oronosmobileapp.R;
import oronos.oronosmobileapp.treeBuilder.LayoutNode;
import oronos.oronosmobileapp.dataUpdator.DataEventsManagerService;
import oronos.oronosmobileapp.utilities.Message;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by beast on 3/21/18.
 *
 * PlotWidgetFragment.java
 * Fragment qui permet l'affichage et la gestion des graphes.
 */

public class PlotWidgetFragment extends LayoutNode implements DataEventsManagerService.OnCanDataReceivedListener {

    private LineChart mChart;
    ArrayList<ArrayList<Entry>> axes = new ArrayList<>();
    private boolean[] isAdded;

    private String dataToDisplay = "__DATA1__";

    private Date time;
    private long timeStart;

    private boolean firstOnCreate;
    private int[] colors;

    private int dataSetIndex;
    private String dataSetId;

    public String name;
    public String axis;
    public String unit;

    private String[] ids;

    public PlotWidgetFragment() {
        // Initialise le temp repère à la création du graphe.
        // Ce temp (timeStart) sera utilie lors de l'ajout de nouvelles données dans le graphe.
        time = new Date();
        timeStart = time.getTime();

        // Booléen qui permet de savoir si c'est la première fois ou non que la méthode onCreate() à été appelée.
        // La méthode onCreate() peut être appelé plusieurs.
        // (par exemple : lors que l'on quitte la vue des graphes (g=on change de grid)).
        firstOnCreate = true;

        // Étant donnnées que le maximum de données pouvant être présentes sur un graphe est de 6 (d'après les requis).
        // On initialise 6 couleurs par défaut pour les possibles 6 données du graphe.
        colors = new int[6];
        int color1 = Color.BLUE;
        colors[0] = color1;
        int color2 = Color.RED;
        colors[1] = color2;
        int color3 = Color.CYAN;
        colors[2] = color3;
        int color4 = Color.GREEN;
        colors[3] = color4;
        int color5 = Color.MAGENTA;
        colors[4] = color5;
        int color6 = Color.YELLOW;
        colors[5] = color6;
    }

    /**
     * Méthode de création de vue
     *
     * Retourne une vue.
     * Cette méthode est appelée à chaque création de graphe
     * et permet d'initialiser le graphe et les oourbes des graphes.
     *
     *  @param inflater
     *  @param container
     *  @param savedInstanceState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // À la première que creation des graphes on récupère les dix dernières données CAN reçues.
        if (firstOnCreate) {
            ArrayList<String> array = new ArrayList<>();
            for (LayoutNode child : children) {
                CanWidgetFragment canChild = (CanWidgetFragment) child;
                array.add(canChild.canWidgetParams.id);
            }
            ids = new String[array.size()];
            ids = array.toArray(ids);
            isAdded = new boolean[ids.length];
            for (int i = 0; i < ids.length; i++) {
                isAdded[i] = false;
            }
        }

        // On associe la vue au layout plot_fragment.
        View v = inflater.inflate(R.layout.plot_fagment, container, false);
        mChart = (LineChart) v.findViewById(R.id.plot);

        // À la première création des graphes on créé les listes qui contiendront les points des courbes dans lesquels
        // on y insert directement la dernière donnée CAN reçue depuis sa création.
        // Ces liste représentent les courbes du graphe.
        if (firstOnCreate){
            Map<String, List<Message.CanData>> lastTenChildren = MainApplication.getInstance ().getService().addListener(this, ids);

            for (int i = 0; i < ids.length; i++) {
                List<Message.CanData> data = lastTenChildren.get(ids[i]);
                ArrayList<Entry> entries = new ArrayList<>();
                if (data != null) {
                    entries.add(new Entry(0,(float) convertDataToDouble(getData(data.get(data.size() - 1)))));
                    isAdded[i] = true;
                }else {
                    entries.add(new Entry(0,0));
                }
                axes.add(entries);
            }
            firstOnCreate = false;
        }

        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

        int i = 0;
        // Cette boucle permet de donner l'esthétique à chaque courbe du graphe.
        // C'est ici que l'on donne l'épaisseur, la couleur, le nom, si elle possèdera des points, ... à une courbe.
        for (ArrayList<Entry> yAXE : axes) {


            LineDataSet lineDataSet1 = new LineDataSet(yAXE,ids[i]);
            lineDataSet1.setDrawCircles(true);
            lineDataSet1.setCircleRadius(1f);
            lineDataSet1.setDrawValues(true);
            lineDataSet1.setDrawValues(true);
            lineDataSet1.setCircleColor(colors[i]);
            lineDataSet1.setColor(colors[i]);
            lineDataSet1.setLineWidth(1f);
            i++;

            lineDataSets.add(lineDataSet1);
        }

        mChart.setData(new LineData(lineDataSets));

        // On met l'arrière plan du graphe en gris clair
        mChart.setBackgroundColor(Color.LTGRAY);
        mChart.setTouchEnabled(true);

        getwidgetParams(getNode().getAttributes());

        // On donne le nom au graphe
        mChart.getDescription().setEnabled(true);
        mChart.getDescription().setText(axis + " (" + unit + ")");
        mChart.getDescription().setTextColor(Color.BLACK);
        mChart.getDescription().setTextSize(12f);

        // On personnalise les valeur de l'axe des abscisses (X) en y ajoutant un "s" pour seconde
        mChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                //value = round(value, 3);
                return value + "s";
            }
        });

        // On personnalise les valeur de l'axe des abscisses (X) en y ajoutant l'unité précisé dans le xml
        mChart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.format("%.02f", value) + unit;
            }
        });

        mChart.getAxisRight().setEnabled(false);

        mChart.getAxisLeft().setGranularityEnabled(true);
        mChart.getAxisLeft().setGranularity(0.00001f);

        // On autorise l'ajustement de la fenêtre de graphe uniquement en horizontale (en X) (coulissement et agrandissement)
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);
        mChart.setScaleXEnabled(true);
        mChart.setDrawGridBackground(false);

        mChart.setPinchZoom(true);

        // On limite le nombre de données visible dans la fenêtre de graphe à 300
        mChart.setAutoScaleMinMaxEnabled(false);
        mChart.setVisibleXRangeMaximum(300f);

        return v;
    }

     /**
     * Méthode qui permet d'obtenir la data qu'il faut
     * à partir d'un Message CanData et de l'attribut dataToDisplay
     *
     * Retourne la donnée correspondant à "__DATA1__" ou "__DATA2__"
     * en fonction de l'attribut dataToDisplay.
     *
     *  @param protoMessage la donnée CAN reçue
     */
    protected Message.Data getData(Message.CanData protoMessage) {
        if (dataToDisplay.equals("__DATA1__"))
            return protoMessage.getData1();
        else if (dataToDisplay.equals("__DATA2__"))
            return protoMessage.getData2();
        return null;
    }

    /**
     * Méthode qui permet d'ajouter des entrées dans le graphe
     *
     * Lorsqu'une nouvelle donnée est reçue et que l'on désire ajouter
     * une nouvelle données dans le graphe, cette méthode est appelée
     * pour l'ajouter et mettre à jour (rafraîchir) le graphe.
     *
     *  @param value la valeur que l'on désire ajouter dans une courbe du graphe
     */
    private void addEntry(double value) {
        LineData data =  mChart.getData();
        long currentDate = new Date().getTime();

        if (data != null && Math.abs((currentDate) - timeStart) > 2) {
            ILineDataSet set = data.getDataSetByIndex(dataSetIndex);
            if (isAdded[dataSetIndex] == false) {
                set.removeFirst();
                isAdded[dataSetIndex] = true;
            }
            if (set.getEntryCount() > 350) {// TODO : Verify with PCB (bigger normally)
                set.removeFirst();
            }

            // On ajoute la donnée avec comme X la différence entre le temps courrant
            // et le démarrage des graphes
            data.addEntry(
                    new Entry(((currentDate) - timeStart)/1000, (float) value), dataSetIndex);

            data.notifyDataChanged();
            // On prévient la vue que le graphe a changé
            mChart.notifyDataSetChanged();

            // On limite le nombre de données visible dans la fenêtre de graphe à 300
            mChart.setAutoScaleMinMaxEnabled(false);
            mChart.setVisibleXRangeMaximum(300f);

            // On fait en sorte que la vue suive la dernière valeur arrivée
            mChart.moveViewToX(data.getEntryCount()-1);
        }
    }

    /**
     * Méthode permettant d'obtenir les paramètre du widget depuis le xml.
     *
     * Permet d'initialiser les attribut name, axis (nom du graphe)
     * et unit (l'unité de mesure) depuis le xml.
     *
     *  @param canAttributs le noeud xml (plot) correspondant au fragment
     */
    public void getwidgetParams(NamedNodeMap canAttributs) {
        for(int i = 0; i < canAttributs.getLength(); i++) {
            Node item = canAttributs.item(i);
            switch (item.getNodeName()) {
                case "name" :
                    name = item.getNodeValue();
                    break;
                case "axis" :
                    axis = item.getNodeValue();
                    break;
                case "unit" :
                    unit = item.getNodeValue();
                    break;
            }
        }
    }

    @Override
    protected void addChildrenFrag(int container_id) {
    }

    /**
     * Méthode de reception de donnée CAN
     *
     * C'est cette méthode qui reçoit les données CAN lors quelles arrivent
     * et appelle les méthodes nécessaires pour la mise à jour du graphe
     *
     *  @param protoMessage listener who override onReceivedCanData
     */
    @Override
    public void onReceivedCanData(Message.CanData protoMessage) {
        dataSetId = protoMessage.getId();

        for (int i = 0; i < ids.length; i++) {
            if (dataSetId.equals(ids[i])) {
                dataSetIndex = i;
                break;
            }
        }
        double value = convertDataToDouble(getData(protoMessage));

        if (Math.abs(value) < Math.pow(10.0, (-20.0))){
            value = 0.0;
        }
        addEntry(value);
    }

    /**
     * Méthode permettant de convertir un donnée CAN en double.
     *
     *  @param data la donnée CAN que l'on désire convertir en double
     */
    protected double convertDataToDouble(Message.Data data) {
        double result = -87777;

        switch (data.getData1Case().getNumber()) {
            case Message.Data.INT_TYPE_FIELD_NUMBER:
                result = (double) data.getIntType();
                break;
            case Message.Data.DOUBLE_TYPE_FIELD_NUMBER:
                result = data.getDoubleType();
                break;
            case Message.Data.UNSIGNED_TYPE_FIELD_NUMBER:
                result = (double) data.getUnsignedType();
                break;
            default :
        }
        return result;
    }

}
