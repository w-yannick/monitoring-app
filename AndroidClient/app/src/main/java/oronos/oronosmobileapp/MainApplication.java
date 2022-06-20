package oronos.oronosmobileapp;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import oronos.oronosmobileapp.dataUpdator.DataEventsManagerService;
import oronos.oronosmobileapp.utilities.FLog;
import oronos.oronosmobileapp.utilities.Tag;

public class MainApplication extends Application {

    private static MainApplication mInstance = null;
    private DataEventsManagerService mService = null;
    private boolean mIsBound;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = ((DataEventsManagerService.LocalBinder) iBinder).getInstance();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
        }
    };

    public static synchronized MainApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = MainApplication.this;
        FLog.i(Tag.APPLICATION, "running");
    }

    public void startService() {
        startService(new Intent(this.getBaseContext(), DataEventsManagerService.class));
        doBindService();
    }

    public void stopService() {
        doUnbindService();
        stopService(new Intent(this.getBaseContext(), DataEventsManagerService.class));
    }

    public DataEventsManagerService getService() {
        return mService;
    }

    private void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(this,
                DataEventsManagerService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    private void doUnbindService() {
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }

}
