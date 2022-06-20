package oronos.oronosmobileapp.logout;

import android.os.AsyncTask;

import oronos.oronosmobileapp.settings.Configuration;
import oronos.oronosmobileapp.httpClientUtilities.PostRequest;

/**
 * LogoutInteractorImpl.java
 * Implementation du modèle pour le logout
 */
public class LogoutInteractorImpl implements LogoutInteractor {
    OnLogoutFinishedListener mListener;
    @Override
    public void logout(final OnLogoutFinishedListener listener) {
        mListener = listener;
        LogoutRequest tryDisconnectPost = new LogoutRequest();
        tryDisconnectPost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,Configuration.getIpAddr() + Configuration.urlLogout, "username", Configuration.username);
    }
    private class LogoutRequest extends PostRequest {

        @Override
        protected void onPostExecute(String result) {
            // Si la requête HTTP est valide
            if(result.equals("200")){
                mListener.onSuccess();
            }
            else{
                mListener.onError();
            }
        }
    }
}


