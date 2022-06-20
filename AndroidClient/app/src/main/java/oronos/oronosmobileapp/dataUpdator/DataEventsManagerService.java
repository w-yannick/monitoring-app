package oronos.oronosmobileapp.dataUpdator;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import oronos.oronosmobileapp.settings.Configuration;
import oronos.oronosmobileapp.socketStreamHandler.SocketStream;
import oronos.oronosmobileapp.socketStreamHandler.SocketStreamImpl;
import oronos.oronosmobileapp.utilities.FLog;
import oronos.oronosmobileapp.utilities.Message;
import oronos.oronosmobileapp.utilities.Tag;

/**
 * DataEventsManagerService.java
 * Service qui gère la distribution des message can
 */
public class DataEventsManagerService extends Service implements SocketStream.OnReceiveDataListener {

    // HasMap de tous les listeners abonnés à un message can par son id (clé de la map)
    private Map<String, Set<OnCanDataReceivedListener>> listeners = new HashMap();
    private SocketStreamImpl socketTask = null;

    // HasMap qui contient les Configuration.nbLastSavingData derniers messages arrivés
    private Map<String, Queue<Message.CanData>> lastSavingData =  new HashMap();

    // Set des listenners interresser par tous les messages
    private Set<OnCanDataReceivedListener> listenersRegisterAll = new HashSet();

    private final IBinder mIBinder = new LocalBinder();


    /**
     * S'enregistrer pour tout les cans qui correspondent aux ids en paramètre
     *
     * Retourne la dernière données pour les ids donnés
     * Si il n'y a pas de données sauvegardées la donnée
     * associée à l'id sera null.
     * @param listener auditeur qui surcharge onReceivedCanData
     * @param canIDs tout les ids auquels s'eregistrer, il peut être unique ou multiple
     */
    public Map<String, List<Message.CanData>> addListener(OnCanDataReceivedListener listener, String... canIDs) {
        for(String canID : canIDs)
            addListener(canID, listener);

        return getMapForSpecifDataSaving(canIDs);
    }


    /**
     * S'eregistrer à tout les messaga can provenant du serveur (utiliser uniquement quand cela est nécéssaire)
     * @param listener
     */
    public void registerToAllData(OnCanDataReceivedListener listener) {
        listenersRegisterAll.add(listener);
    }

    public List<Message.CanData> getLastSavingData(String canID) {
        if(lastSavingData.get(canID) != null)
            return new ArrayList(lastSavingData.get(canID));
        return null;
    }

    private  Map<String, List<Message.CanData>> getMapForSpecifDataSaving(String[] canIDs) {
        Map<String, List<Message.CanData>> savingDataMap = new HashMap();

        for (String canId : canIDs) {
            Queue<Message.CanData> savingDataSpacific = lastSavingData.get(canId);
            if(savingDataSpacific != null) {
                savingDataMap.put(canId, new ArrayList(savingDataSpacific));
            }
        }

        return savingDataMap;
    }

    private void addListener(String canID, OnCanDataReceivedListener listener) {
        Set<OnCanDataReceivedListener> listenersSet = listeners.get(canID);
        if (listenersSet == null) {
            listenersSet = new HashSet(Arrays.asList(listener));
            listeners.put(canID, listenersSet);
        } else
            listenersSet.add(listener);
    }

    public void removeListener(OnCanDataReceivedListener listener) {
    }

    @Override
    public void onDataReceive(Message.CanData protoMessage) {

        String key = protoMessage.getId();
        Set<OnCanDataReceivedListener> listenersSet = listeners.get(key);

        // Notify specific listeners
        if (listenersSet != null) {
            for (OnCanDataReceivedListener listener : listenersSet)
                listener.onReceivedCanData(protoMessage);
        }

        // Notify listeners register to all
        for(OnCanDataReceivedListener listener : listenersRegisterAll)
            listener.onReceivedCanData(protoMessage);

        // Save data
        Queue<Message.CanData> savingDataQueue = lastSavingData.get(key);
        if (savingDataQueue == null){
            savingDataQueue = new CircularFifoQueue(Configuration.nbLastSavingData);
            lastSavingData.put(key, savingDataQueue);
        }
        savingDataQueue.add(protoMessage);
    }

    /**
     * Interface qui permet d'être notifié lors de l'arrivé d'un message
     */
    public interface OnCanDataReceivedListener {

        void onReceivedCanData(Message.CanData protoMessage);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        FLog.i(Tag.UPDATOR, "upadator service started");
        socketTask = new SocketStreamImpl(this);
        socketTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        FLog.i(Tag.UPDATOR, "upadator service stoped");
        if (socketTask != null) {
            socketTask.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    public class LocalBinder extends Binder {
        public DataEventsManagerService getInstance() {
            return DataEventsManagerService.this;
        }
    }

}
