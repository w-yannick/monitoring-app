package oronos.oronosmobileapp.socketStreamHandler;

import oronos.oronosmobileapp.utilities.Message;

/**
 * SocketStream.java
 * Interface qui permet de recevoir les données reçues par le socket.
 */
public interface SocketStream {

    interface OnReceiveDataListener {
        void onDataReceive(Message.CanData protoMessage);
    }

}
