package oronos.oronosmobileapp.login;

import oronos.oronosmobileapp.utilities.FLog;
import oronos.oronosmobileapp.utilities.Tag;

class LoginPresenterImpl implements LoginPresenter, LoginInteractor.OnLoginFinishedListener {

        private LoginView loginView;
        private final LoginInteractor loginInteractor;

        public LoginPresenterImpl(LoginView loginView, LoginInteractor loginInteractor) {
            this.loginView = loginView;
            this.loginInteractor = loginInteractor;
        }

        @Override
        public void validateCredentials(String username, String password) {
            if (loginView != null) {
                loginView.showProgress();
            }

            loginInteractor.login(username, password, this);
        }

        @Override
        public void onDestroy() {
            loginView = null;
        }

        @Override
        public void onUsernameError() {
            if (loginView != null) {
                loginView.setUsernameError();
                loginView.hideProgress();
            }
        }

        @Override
        public void onPasswordError() {
            if (loginView != null) {
                loginView.setPasswordError();
                loginView.hideProgress();
            }
        }

    @Override
    public void onBadCredentials() {
        if (loginView != null) {
            loginView.setBadCredentialsError();
        }
    }

    @Override
        public void onSuccess() {
            if (loginView != null) {
                FLog.i(Tag.LOGIN, "Success to login");
                loginView.startHttp();
            }
        }

}
