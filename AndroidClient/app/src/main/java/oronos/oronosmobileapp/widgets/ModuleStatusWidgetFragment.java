package oronos.oronosmobileapp.widgets;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

import oronos.oronosmobileapp.MainApplication;
import oronos.oronosmobileapp.R;
import oronos.oronosmobileapp.treeBuilder.LayoutNode;
import oronos.oronosmobileapp.dataUpdator.DataEventsManagerService;
import oronos.oronosmobileapp.utilities.Message;

/**
 * ModuleStatusWidgetFragment.java
 * Fragment qui permet l'affichage et la gestion des ModuleStatus.
 */

public class ModuleStatusWidgetFragment extends LayoutNode implements DataEventsManagerService.OnCanDataReceivedListener{
    private GridLayout gridLayout = null;
    private int nGrid = 0;
    private int nCol = 0;
    private List<String> listOfPCB = new ArrayList();
    private List<Handler> listOfTimer = new ArrayList<>();
    private List<CardView> listOfCardview = new ArrayList<>();
    private enum textView{PCBName, status}

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_modulestatus, container, false);

        if(nGrid == 0 && nCol == 0)
            setGridAttributs(getNode().getAttributes());

        gridLayout = (GridLayout) v.findViewById(R.id.gridLayout);

        this.gridLayout.setColumnCount(this.nCol);

        if(listOfCardview.isEmpty()){
            this.inflateCardView(this.nGrid,inflater,container);
        }
        this.addCvToGridLayout();

        MainApplication.getInstance().getService().registerToAllData(ModuleStatusWidgetFragment.this);
        return v;
    }

    public void updateStatus(int id, String color){
        try {
            if (color == "orange"){
                this.listOfCardview.get(id).setBackgroundColor(Color.parseColor("#FFAA00"));
                this.setTextOnCv(id,"DELAY",textView.status.ordinal());
            }
            else if (color == "red"){
                this.listOfCardview.get(id).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red_can));
                this.setTextOnCv(id,"OFFLINE",textView.status.ordinal());
            }
            else if (color == "green"){
                this.listOfCardview.get(id).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green_can));
                this.setTextOnCv(id,"ONLINE",textView.status.ordinal());
            }
        }
        catch (Exception e){
        }
    }

    public void setTextOnCv(int id, String name,int textview){
        try{
            TextView pcbName = (TextView) this.listOfCardview.get(id).getChildAt(textview);
            pcbName.setText(name);
        }
        catch (Exception e){
        }
    }

    @Override
    public void onReceivedCanData(Message.CanData protoMessage) {
        String PCB = protoMessage.getSrcType() + " (" + Integer.toString(protoMessage.getSrcSerial()) + (')') ;

        if( !this.listOfPCB.contains(PCB)){

            setTextOnCv(this.listOfPCB.size(), PCB, textView.PCBName.ordinal());
            updateStatus(this.listOfPCB.size() ,"green");

            final Handler timer = new Handler();
            startHandler(timer,this.listOfPCB.size());

            this.listOfTimer.add(timer);
            this.listOfPCB.add(PCB);
        }
        else{
            listOfTimer.get(this.listOfPCB.indexOf(PCB)).removeCallbacksAndMessages(null); //cancel previous timer
            updateStatus(this.listOfPCB.indexOf(PCB),"green");
            startHandler(listOfTimer.get(this.listOfPCB.indexOf(PCB)),this.listOfPCB.indexOf(PCB));//start timer again
        }
    }

    public void startHandler(final Handler timer, final int id) {
        timer.postDelayed(new Runnable() {
            public void run() {
                updateStatus(id, "orange");
                timer.postDelayed(new Runnable() {
                    public void run() {
                        updateStatus(id, "red");
                    }
                }, 2000);
            }
        }, 2000);
    }

    public void addCvToGridLayout(){
        for (int i = 0; i < this.listOfCardview.size(); i++){
            this.gridLayout.addView(this.listOfCardview.get(i));
        }

    }

    public void inflateCardView(int nGrid, LayoutInflater li, ViewGroup vg){
        for(int i = 0; i < nGrid; i++){
            CardView cardview = (CardView) li.inflate(R.layout.cardview_model_layout,vg,false);
            cardview.setId(i);
            this.listOfCardview.add(cardview);
        }

    }


    @Override
    protected void addChildrenFrag(int container_id) {

    }

    private void setGridAttributs(NamedNodeMap gridAttribs) {
        for(int i = 0; i < gridAttribs.getLength(); i++) {
            Node item = gridAttribs.item(i);
            switch (item.getNodeName()) {
                case "nGrid" :
                    nGrid = Integer.parseInt(item.getNodeValue());
                    break;
                case "nColumns" :
                    nCol =  Integer.parseInt(item.getNodeValue());
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.gridLayout.removeAllViews();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.gridLayout.removeAllViews();
    }
}
