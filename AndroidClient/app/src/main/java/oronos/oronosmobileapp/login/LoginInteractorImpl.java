package oronos.oronosmobileapp.login;

import android.text.TextUtils;

import oronos.oronosmobileapp.settings.Configuration;
import oronos.oronosmobileapp.httpClientUtilities.PostRequest;

class LoginInteractorImpl implements LoginInteractor {
    OnLoginFinishedListener mListener;
    @Override
    public void login(final String username, final String password, final OnLoginFinishedListener listener) {
        mListener = listener;
        if (TextUtils.isEmpty(username)) {
            mListener.onUsernameError();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            mListener.onPasswordError();
            return;
        }
        LoginRequest tryConnectPost = new LoginRequest();
        tryConnectPost.execute(Configuration.getIpAddr() + Configuration.urlLogin, "username", username,"password",  password);
        Configuration.username = username;
    }
    private class LoginRequest extends PostRequest{

        @Override
        protected void onPostExecute(String result) {

            if(result.equals("200")){
                mListener.onSuccess();
            }
            else{
                mListener.onBadCredentials();
            }
        }
    }

}
