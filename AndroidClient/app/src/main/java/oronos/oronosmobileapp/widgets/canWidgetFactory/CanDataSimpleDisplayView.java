package oronos.oronosmobileapp.widgets.canWidgetFactory;

import android.content.Context;

import java.math.BigDecimal;
import java.math.MathContext;

import oronos.oronosmobileapp.R;
import oronos.oronosmobileapp.utilities.Message;

/**
 * CanDataSimpleDisplayView.java
 * Class qui implemente une vue can sans customUpdate
 */
public class CanDataSimpleDisplayView extends CanWidgetView {

    private String dataToDisplay = "__DATA1__";
    protected double minAcceptable = -Double.MAX_VALUE;
    protected double maxAcceptable = Double.MAX_VALUE;
    protected int chiffresSign = Integer.MAX_VALUE;
    protected int serialNb = -1;
    protected String specificSource;
    protected String unitSymbol;

    public CanDataSimpleDisplayView(Context context, CanWidgetParams canWidgetParams) {
        super(context, canWidgetParams.name, canWidgetParams.id);

        if (canWidgetParams.display != null)
            dataToDisplay = canWidgetParams.display;
        if (canWidgetParams.minAcceptable != null)
            minAcceptable = Double.parseDouble(canWidgetParams.minAcceptable);
        if (canWidgetParams.maxAcceptable != null)
            maxAcceptable = Double.parseDouble(canWidgetParams.maxAcceptable);
        if (canWidgetParams.chiffresSign != null)
            chiffresSign = Integer.parseInt(canWidgetParams.chiffresSign);
        if (canWidgetParams.serialNb != null)
            serialNb = Integer.parseInt(canWidgetParams.serialNb);
        if (canWidgetParams.unitSymbol != null)
            unitSymbol = canWidgetParams.unitSymbol;
        if (canWidgetParams.specificSource != null)
            specificSource = canWidgetParams.specificSource;

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

        double dataValue = convertDataToDouble(data);
        dataValue = roundDoubleToSignificantFigures(dataValue);

        if(unitSymbol == null)
            super.data.setText(Double.toString(dataValue));
        else
            super.data.setText(Double.toString(dataValue) + "  " + unitSymbol);

        setColor(dataValue);
    }

    // Récuppérer la bonnne donée (data1 ou data2)
    protected Message.Data getData(Message.CanData protoMessage) {
        if (dataToDisplay.equals("__DATA1__"))
            return protoMessage.getData1();
        else if (dataToDisplay.equals("__DATA2__"))
            return protoMessage.getData2();
        return null;
    }

    protected double convertDataToDouble(Message.Data data) {
        //TODO : juste pour le débuging
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

    // Ajustement du fond en fonction des seuils donnés (rouge ou vert)
    protected void setColor(double dataValue) {
        changeBackgroundColor(R.color.red_can);
        if (dataValue >= minAcceptable && dataValue <= maxAcceptable)
            changeBackgroundColor(R.color.green_can);
    }

    private double roundDoubleToSignificantFigures(double value) {
        return new BigDecimal(value).round(new MathContext(chiffresSign)).doubleValue();
    }

}
