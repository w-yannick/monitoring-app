package oronos.oronosmobileapp.widgets.canWidgetFactory;

import android.content.Context;

/**
 * CanDataViewFactory.java
 * Class qui implémente une simple factory pour créer le bon type de can view
 */
public class CanDataViewFactory {

    public CanWidgetView createCanView(Context context, CanWidgetParams params) {
        // Si il y a pas de nom ou d'id la can n'est pas valide
        if (params.name == null || params.id == null)
            return null;
        // Si il y pas de customUpadate alors c'est une simple display can view
        else if (params.customUpdate == null)
            return new CanDataSimpleDisplayView(context, params);
        // Si il la customUpadate est onewire
        else if (params.customUpdate.equals("oneWire"))
            return new CanDataOneWireView(context, params);
        // Si il la customAcceptable est isBWOhmAcceptable
        else if (params.customAcceptable != null && params.customAcceptable.equals("isBWOhmAcceptable"))
            return new CanDataBWOhmAcceptable(context, params);
        // Si il y un customUpadate alors c'est une simple custom display can view
        else if (params.customUpdate != null)
            return new CanDataSimpleCustomUpdateDisplayView(context, params);

        return null;
    }

}
