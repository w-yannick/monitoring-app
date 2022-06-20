package oronos.oronosmobileapp.widgets;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import oronos.oronosmobileapp.MainApplication;
import oronos.oronosmobileapp.R;
import oronos.oronosmobileapp.treeBuilder.LayoutNode;
import oronos.oronosmobileapp.dataUpdator.DataEventsManagerService;
import oronos.oronosmobileapp.settings.Configuration;
import oronos.oronosmobileapp.utilities.Message;

/**
 * Created by beast on 3/21/18.
 */
/**
 * DisplayLogWidgetFragment.java
 * Fragment qui permet l'affichage et la gestion du Tab display Log.
 */
public class DisplayLogWidgetFragment extends LayoutNode implements DataEventsManagerService.OnCanDataReceivedListener {
    private ArrayList<Log> logsList = new ArrayList<>();
    private LogsAdapter arrayAdapter = null;

    private final int nbMaxLogs = Configuration.nbLogsDisplayLogWidget;

    /**
     * Logs Class
     * Defines a log
     */
    public class Log{
        private String logInfo;
        private String logValue;

        public Log(String info, String value)
        {
            logInfo = info;
            logValue = value;
        }

        public String getLogInfo(){
            return logInfo;
        }
        public String getLogValue(){
            return logValue;
        }

    }

    /**
     * LogsAdapter Class
     * Defines the logs' esthetic
     */
    class LogsAdapter extends ArrayAdapter<Log>
    {
        public LogsAdapter(Context context, ArrayList<Log> log) {
            super(context, 0, log);
        }

        /**
         * draws the the logs container
         * @param view
         * @param  color
         * @param strokeWidth
         */
        private View drawBorder(View view,int color, float strokeWidth){
            ShapeDrawable s = new ShapeDrawable();
            // Specify the shape of ShapeDrawable
            s.setShape(new RectShape());
            // Specify the border color of shape
            s.getPaint().setColor(color);
            // Set the border width
            s.getPaint().setStrokeWidth(strokeWidth);
            // Specify the style is a Stroke
            s.getPaint().setStyle(Paint.Style.STROKE);
            view.setBackground(s);
            return  view;
        }

        /**
         * Is called as much times as the logs' list contains logs
         * @param position
         * @param convertView View of one item in the list of views (represnts a linear layout)
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Get the data item for this position
            Log log = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                LinearLayout mainLogsLayout = new LinearLayout(getContext());
                mainLogsLayout.setOrientation(LinearLayout.HORIZONTAL);

                String key = log.getLogInfo();
                String value = log.getLogValue();

                ///TextView for Keys
                TextView keyView = new TextView(getContext());
                keyView.setText(key);
                ////Update Style of TextView
                TableRow.LayoutParams paramsKey = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                keyView.setLayoutParams(paramsKey);
                keyView.setPadding(20, 20, 20, 20);
                keyView.setBackgroundColor(0x7030A030);

                ///TextView for Values
                TextView valueView = new TextView(getContext());
                valueView.setText(value);
                ///Update Style of TextView
                TableRow.LayoutParams paramsValue = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                valueView.setLayoutParams(paramsValue);
                valueView.setPadding(20, 20, 20, 20);

                valueView.setBackgroundColor(0x70C0C0C0);

                mainLogsLayout.addView(keyView);
                mainLogsLayout.addView(valueView);

                mainLogsLayout.setBackgroundColor(0xFFA0A0A0);
                drawBorder(mainLogsLayout, 0xFF505050, 6);
                convertView = mainLogsLayout;
            }
            else
            {
                LinearLayout mainLogsLayout = (LinearLayout)convertView;
                if(mainLogsLayout.getChildCount() == 2)
                {
                    ((TextView)mainLogsLayout.getChildAt(0)).setText(log.getLogInfo());
                    ((TextView)mainLogsLayout.getChildAt(1)).setText(log.getLogValue());
                }
            }
            // Lookup view for data population
            // Return the completed view to render on screen
            return convertView;
        }
    }


    /**
     * Is called at the first time to create the view
     * We register to all logs
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewActivity = inflater.inflate(R.layout.fragment_logs, container, false);
        final ListView listView = (ListView) viewActivity.findViewById(R.id.logsview);

        arrayAdapter = new LogsAdapter(getContext(), logsList);

        // DataBind ListView with logs from ArrayAdapter
        listView.setAdapter(arrayAdapter);

        if(children.isEmpty()){
            MainApplication.getInstance().getService().registerToAllData(this);
        }
        else{
            int i = 0;
            String[] cansId = new String[children.size()];
            for(LayoutNode layoutNode: children){
                CanWidgetFragment canFrag = (CanWidgetFragment) layoutNode;
                cansId[i] = canFrag.canWidgetParams.id;
                i++;
            }
            MainApplication.getInstance().getService().addListener(this, cansId);
        }

        return viewActivity;
    }

    /**
     * Is called by onReceivedCanData
     * add the new received log to the list of logs
     * @param log
     */
    private void pushLogs(Log log){
        if(null != arrayAdapter && logsList != null )
        {
            if(logsList.size() >= nbMaxLogs)
            {
                clearLogs(nbMaxLogs / 2);
            }
            // If adding new logs to the bottom
            //logsList.add(log);
            logsList.add(0, log);
            arrayAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Is called by pushLogs
     * Delete half of the logs in the actual log list
     * @param nbLogsToClear
     * Use -1 to clear all logs if needed
     */
    private void clearLogs(int nbLogsToClear){
        if(null != arrayAdapter && logsList != null )
        {
            if(nbLogsToClear < 0)
            {
                logsList.clear();
            }
            else
            {
                for(int i = 0; i < nbLogsToClear && i < logsList.size(); i++)
                {
                    // If Old logs in the top
                    //logsList.remove(0);
                    logsList.remove(logsList.size() - 1);
                }
            }
            arrayAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Is called by DataEventsManagerService
     * Is called as soon as we receive a log
     * Get the log attributes and calls pushLogs
     * @param protoMessage
     * Use -1 to clear all logs if needed
     */
    @Override
    public void onReceivedCanData(Message.CanData protoMessage) {
        String message = protoMessage.getSrcType() + ";" + protoMessage.getSrcSerial();

        message += ";" + protoMessage.getDestType();

        switch(protoMessage.getData1().getData1Case().name())
        {
            case("UNSIGNED_TYPE"):
                message += ";" + protoMessage.getData1().getUnsignedType();
                break;
            case("DOUBLE_TYPE"):
                message += ";" + protoMessage.getData1().getDoubleType();
                break;
            case("INT_TYPE"):
                message += ";" + protoMessage.getData1().getIntType();
                break;
        }

        switch(protoMessage.getData2().getData1Case().name())
        {
            case("UNSIGNED_TYPE"):
                message += ";" + protoMessage.getData2().getUnsignedType();
                break;
            case("DOUBLE_TYPE"):
                message += ";" + protoMessage.getData2().getDoubleType();
                break;
            case("INT_TYPE"):
                message += ";" + protoMessage.getData2().getIntType();
                break;
        }

        pushLogs(new Log(protoMessage.getId(), message));
    }


    @Override
    protected void addChildrenFrag(int container_id) {

    }
}
