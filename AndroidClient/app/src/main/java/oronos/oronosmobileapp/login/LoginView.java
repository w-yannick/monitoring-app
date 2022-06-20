package oronos.oronosmobileapp.login;

public interface LoginView {

    void showProgress();

    void hideProgress();

    void setUsernameError();

    void setPasswordError();

    void setBadCredentialsError();

    void navigateToHome();

    void startHttp();
}
