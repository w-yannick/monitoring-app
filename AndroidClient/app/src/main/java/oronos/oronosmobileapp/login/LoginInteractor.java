package oronos.oronosmobileapp.login;

public interface LoginInteractor {

    interface OnLoginFinishedListener {
        void onUsernameError();

        void onPasswordError();

        void onBadCredentials();

        void onSuccess();
    }

    void login(String username, String password, OnLoginFinishedListener listener);

}
