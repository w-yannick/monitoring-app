package oronos.oronosmobileapp.widgets.canWidgetFactory;

import android.content.Context;

import oronos.oronosmobileapp.utilities.Message;

/**
 * CanDataBWOhmAcceptable.java
 * Class qui implemente une vue can qui possède un customAcceptable égale à BWOhm
 */
public class CanDataBWOhmAcceptable extends CanDataSimpleCustomUpdateDisplayView {

    public CanDataBWOhmAcceptable(Context context, CanWidgetParams canWidgetParams) {
        super(context, canWidgetParams);
        minAcceptable = 4.0;
        maxAcceptable = 6.5;

        if(lastCanMsg != null)
            displayData(lastCanMsg);
    }

    @Override
    public void onReceivedCanData(Message.CanData protoMessage) {
        displayData(protoMessage);
    }

    @Override
    public void displayData(Message.CanData protoMessage) {
        Message.Data data = getData(protoMessage);

        // If care about serial and serial is not the same, don't upadate data !
        if (serialNb != -1 && serialNb != protoMessage.getSrcSerial())
            return;

        // If care about specificSource and specificSource is not the same, don't upadate data !
        if (specificSource != null && !specificSource.equals(protoMessage.getSrcType()))
            return;

        String dataToDisplay = getDataToDisplay(protoMessage);

        if(unitSymbol == null)
            super.data.setText(dataToDisplay);
        else
            super.data.setText(dataToDisplay + "  " + unitSymbol);

        double dataValue = convertDataToDouble(data);
        setColor((dataValue * 3.5) / 1000);
    }

}
