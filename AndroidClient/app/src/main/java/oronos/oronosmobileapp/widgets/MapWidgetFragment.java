package oronos.oronosmobileapp.widgets;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;

import oronos.oronosmobileapp.MainActivity;
import oronos.oronosmobileapp.MainApplication;
import oronos.oronosmobileapp.R;
import oronos.oronosmobileapp.treeBuilder.LayoutNode;
import oronos.oronosmobileapp.dataUpdator.DataEventsManagerService;
import oronos.oronosmobileapp.settings.Configuration;
import oronos.oronosmobileapp.utilities.Message;

/**
 * MapWidgetFragment.java
 * Fragment qui permet l'affichage et la gestion des Maps.
 */
public class MapWidgetFragment extends LayoutNode
        implements OnMapReadyCallback, DataEventsManagerService.OnCanDataReceivedListener {

    MapView mMapView;
    GoogleMap mMap;
    public Marker mRocketPosition_Marker;
    private LatLng mRocketPosition;
    private Marker mServerPosition_Marker;

    public MapWidgetFragment() {
        MainApplication.getInstance().getService().addListener(this,
                "GPS1_LATITUDE",
                "GPS1_LONGITUDE",
                "GPS1_ALT_MSL",
                "GPS2_LATITUDE",
                "GPS2_LONGITUDE",
                "GPS2_ALT_MSL");
        //Configuration.rocketsConfig.map.ServerPosition = new LatLng(Configuration.rocketsConfig.map.LAT, Configuration.rocketsConfig.map.LONG);
        mRocketPosition = Configuration.rocketsConfig.map.ServerPosition;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = (MapView) v.findViewById(R.id.mapview);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        //setUpMap();
        return v;
    }

    @Override
    protected void addChildrenFrag(int container_id) {
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NONE);

        GroundOverlayOptions offlineMap = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromBitmap(getBitmapFromAsset(getContext(), "Maps/" + Configuration.rocketsConfig.map.MAP_NAME + ".png")))
                .position(Configuration.rocketsConfig.map.ServerPosition, 5000f, 5000f);

        mMap.addGroundOverlay(offlineMap);

        mRocketPosition_Marker = mMap.addMarker(new MarkerOptions()
                .position(mRocketPosition)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_rocket_maker)));
        mRocketPosition_Marker.setTag(0);
        mServerPosition_Marker = mMap.addMarker(new MarkerOptions()
                .position(Configuration.rocketsConfig.map.ServerPosition)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_server_marker)));
        mServerPosition_Marker.setTag(1);
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        try {
            updateCamera();
        } catch (UnknownError e) {
            e.printStackTrace();
        }
    }

    private void updateCamera() {
        MainActivity activity = ((MainActivity) getContext());
        if (activity != null) {
            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(mRocketPosition)
                    .include(Configuration.rocketsConfig.map.ServerPosition).build();

            Point displaySize = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
            if(mMap != null)
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, displaySize.x, 250, 30));
        }
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private void updateLatitude(double doubleType) {
        mRocketPosition = new LatLng(doubleType, mRocketPosition.longitude);
        if (mRocketPosition_Marker != null) {
            mRocketPosition_Marker.setPosition(mRocketPosition);
        }

    }

    private void updateLongitude(double doubleType) {
        mRocketPosition = new LatLng(mRocketPosition.latitude, -doubleType);
        if (mRocketPosition_Marker != null) {
            mRocketPosition_Marker.setPosition(mRocketPosition);
        }
    }

    @Override
    public void onReceivedCanData(Message.CanData protoMessage) {
        switch (protoMessage.getId()) {
            case "GPS1_LATITUDE":
                updateLatitude(protoMessage.getData1().getDoubleType());
                break;
            case "GPS1_LONGITUDE":
                updateLongitude(protoMessage.getData1().getDoubleType());
                break;
            case "GPS1_ALT_MSL":
                break;
            case "GPS2_LATITUDE":
                updateLatitude(protoMessage.getData1().getDoubleType());
                break;
            case "GPS2_LONGITUDE":
                updateLongitude(protoMessage.getData1().getDoubleType());
                break;
            case "GPS2_ALT_MSL":
                break;
        }
        updateCamera();
    }

}
