package oronos.oronosmobileapp.widgets.canWidgetFactory;

import android.content.Context;

import oronos.oronosmobileapp.utilities.Message;

/**
 * CanDataOneWireView.java
 * Class qui implemente une vue can qui possède une customUpdate égale à onewire
 */
public class CanDataOneWireView extends CanDataSimpleCustomUpdateDisplayView {

    private String customUpdateParam;

    public CanDataOneWireView(Context context, CanWidgetParams canWidgetParams) {
        super(context, canWidgetParams);
        customUpdateParam = canWidgetParams.customUpdateParam.toUpperCase();
        minAcceptable = 15.0;
        maxAcceptable = 65.0;

        if(lastCanMsg != null)
            displayData(lastCanMsg);
    }

    @Override
    public void onReceivedCanData(Message.CanData protoMessage) {
        displayData(protoMessage);
    }

    // Om reccupère en hexadécimal la valeur de l'adresse du onewire reçu
    private String convertCanToStringHex(Message.Data data) {
        int dataValue = new Double(convertDataToDouble(data)).intValue();
        return "0X" + Integer.toHexString(dataValue).toUpperCase();
    }

    @Override
    public void displayData(Message.CanData protoMessage) {
        Message.Data data = getData(protoMessage);

        if(customUpdateParam == null)
            return;

        // If care about serial and serial is not the same, don't upadate data !
        if (serialNb != -1 && serialNb != protoMessage.getSrcSerial())
            return;

        // If care about specificSource and specificSource is not the same, don't upadate data !
        if (specificSource != null && !specificSource.equals(protoMessage.getSrcType()))
            return;

        // If is not the same address
        if(!customUpdateParam.equals(convertCanToStringHex(protoMessage.getData1())))
            return;

        String dataToDisplay = getDataToDisplay(protoMessage);

        if(unitSymbol == null)
            super.data.setText(dataToDisplay);
        else
            super.data.setText(dataToDisplay + "  " + unitSymbol);

        double dataValue = convertDataToDouble(data);
        setColor(dataValue);
    }

}
