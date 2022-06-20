package oronos.oronosmobileapp.socketStreamHandler;

import android.os.AsyncTask;

import com.google.protobuf.InvalidProtocolBufferException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import oronos.oronosmobileapp.settings.Configuration;
import oronos.oronosmobileapp.utilities.FLog;
import oronos.oronosmobileapp.utilities.Message;
import oronos.oronosmobileapp.utilities.Tag;

/**
 * SocketStreamImpl.java
 * Class qui créé le socket UDP pour recevoir des données du serveur
 */

public class SocketStreamImpl extends AsyncTask<Void, byte[], Void> implements SocketStream {

    private OnReceiveDataListener listener;

    public SocketStreamImpl(OnReceiveDataListener listener) {
        super();
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        try {
            FLog.i(Tag.SOCKET, "Socket listen on port : " + Configuration.rocketsConfig.socketStreamPort);
            DatagramSocket socketUDP = new DatagramSocket(Integer.parseInt(Configuration.rocketsConfig.socketStreamPort));
            DatagramPacket recvPacket = new DatagramPacket(new byte[Configuration.udpPacketLength], Configuration.udpPacketLength);
            //Ajout d'un timout pour que le reciev ne soit pas bloquant indéfiniment
            socketUDP.setSoTimeout(Configuration.SOCKET_TIMEOUT);

            while (true) {
                //Permet de stoper une tâche asynchrone qui boucle indéfiniment
                if (isCancelled()) {
                    socketUDP.close();
                    FLog.i(Tag.SOCKET, "Socket closed");
                    break;
                }
                    try {
                        socketUDP.receive(recvPacket);
                    } catch (SocketTimeoutException e) {
                        FLog.i(Tag.SOCKET, "socket receive timeout : ");
                        continue;
                    }
                    byte[] rcvData = Arrays.copyOf(recvPacket.getData(), recvPacket.getLength());
                    //Le message est publier pour être utilisable sur le UI thread
                    publishProgress(rcvData);
            }
        } catch (InvalidProtocolBufferException e) {
            FLog.e(Tag.SOCKET, "socket error : " + e);
        } catch (IOException e) {
            FLog.e(Tag.SOCKET, "socket error : " + e);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(byte[]... progress) {
        List<Message.CanData> recvCanData = null;
        Collection<Message.CanData> values = null;
        try {
            //Parsing du message binaire
            recvCanData = Message.CanDataStream.parseFrom(progress[0]).getMultipleCanValueList();
        } catch (InvalidProtocolBufferException e) {
            FLog.e(Tag.SOCKET, "Error parsing byte message in can proto");
        }
        //Le listener est prévenu can par can
        for(Message.CanData canData: recvCanData) {
            listener.onDataReceive(canData);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        FLog.d(Tag.SOCKET, "socket async task cancelled");
    }

}
