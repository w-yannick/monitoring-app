package oronos.oronosmobileapp.widgets.canWidgetFactory;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import oronos.oronosmobileapp.MainApplication;
import oronos.oronosmobileapp.R;
import oronos.oronosmobileapp.dataUpdator.DataEventsManagerService.OnCanDataReceivedListener;
import oronos.oronosmobileapp.utilities.Message;

/**
 * CanWidgetView.java
 * Class qui implémente la vue d'une donnée can
 */
public abstract class CanWidgetView extends ConstraintLayout implements OnCanDataReceivedListener {

    protected TextView title;
    protected TextView data;
    protected Message.CanData lastCanMsg;

    public CanWidgetView(Context context, String name, String canId) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View inflatedLayout = inflater.inflate(R.layout.can_view, null, false);
        this.addView(inflatedLayout);

        // On s'enregistre auprès du service pour recevoir tout les massages du même ID.
        List<Message.CanData> lastCansData = MainApplication.getInstance().getService().addListener(this, canId).get(canId);
        if (lastCansData != null && !lastCansData.isEmpty()) {
            lastCanMsg = lastCansData.get(lastCansData.size() - 1);
        }

        title = (TextView) inflatedLayout.findViewById(R.id.text_view_name);
        data = (TextView) inflatedLayout.findViewById(R.id.text_view_data);

        title.setText(name);
    }

    public void displayData(Message.CanData protoMessage) {
    }

    protected void changeBackgroundColor(int color) {
        Drawable mDrawable = data.getBackground();
        mDrawable.clearColorFilter();
        // On met à jour la couleur du drawable du champ data
        mDrawable.setColorFilter(new
                PorterDuffColorFilter(ContextCompat.getColor(getContext(),color), PorterDuff.Mode.MULTIPLY));
    }

}
