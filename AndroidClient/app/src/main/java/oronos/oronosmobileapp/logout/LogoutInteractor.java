package oronos.oronosmobileapp.logout;

/**
 * LogoutInteractor.java
 * Interface pour décrire le contenu du modèle (issu de MVP) pour le logout
 */
public interface LogoutInteractor {

    interface OnLogoutFinishedListener {
        void onSuccess();
        void onError();
    }

    void logout(OnLogoutFinishedListener listener);
}
